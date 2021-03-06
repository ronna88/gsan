package gcom.cobranca.repositorios;

import gcom.cadastro.imovel.Imovel;
import gcom.cobranca.bean.CancelarParcelamentoHelper;
import gcom.cobranca.parcelamento.Parcelamento;
import gcom.cobranca.parcelamento.ParcelamentoSituacao;
import gcom.faturamento.debito.DebitoACobrar;
import gcom.faturamento.debito.DebitoCreditoSituacao;
import gcom.faturamento.debito.DebitoTipo;
import gcom.util.ConstantesSistema;
import gcom.util.ErroRepositorioException;
import gcom.util.HibernateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class RepositorioParcelamentoHBM implements IRepositorioParcelamentoHBM {

	private static RepositorioParcelamentoHBM instancia;

	protected RepositorioParcelamentoHBM() {
	}

	public static RepositorioParcelamentoHBM getInstancia() {
		if (instancia == null) {
			instancia = new RepositorioParcelamentoHBM();
		}
		return instancia;
	}
	
	public CancelarParcelamentoHelper pesquisarParcelamentoParaCancelar(Integer idParcelamento) throws ErroRepositorioException {
		Session session = HibernateUtil.getSession();
		CancelarParcelamentoHelper retorno = null;
		
		try {
			String where = "WHERE p.parc_id = :idParcelamento AND dac.dbtp_id NOT IN (:jurosParcelamento, :multa, :jurosMora, :atualizacaoMonetaria) ";
			StringBuilder consulta = new StringBuilder();
			consulta.append(montarRaizConsulta(where, true));
			
			Query query = criarQuery(session, consulta.toString());
			Object[] dados = (Object[]) query.setInteger("idParcelamento", idParcelamento)
											 .setInteger("jurosParcelamento", DebitoTipo.JUROS_SOBRE_PARCELAMENTO)
											 .setInteger("multa", DebitoTipo.MULTA_IMPONTUALIDADE)
											 .setInteger("jurosMora", DebitoTipo.JUROS_MORA)
											 .setInteger("atualizacaoMonetaria", DebitoTipo.ATUALIZACAO_MONETARIA)
											 .uniqueResult();
			
			if (dados != null && dados.length > 0){
				Parcelamento parcelamento = pesquisarPorId((Integer) dados[0]);
				Imovel imovel = pesquisarImovelPorId((Integer) dados[1]);
				retorno = new CancelarParcelamentoHelper(parcelamento, imovel, dados);
			}
		} catch (ErroRepositorioException e) {
			throw new ErroRepositorioException(e, "Erro no Hibernate");
		} finally {
			HibernateUtil.closeSession(session);
		}
		
		return retorno;
	}
		
	@SuppressWarnings("unchecked")
	public List<CancelarParcelamentoHelper> pesquisarParcelamentosParaCancelar() throws ErroRepositorioException {
		Session session = HibernateUtil.getSession();
		List<CancelarParcelamentoHelper> helper = new ArrayList<CancelarParcelamentoHelper>();

		try {
			StringBuilder where = new StringBuilder();
			where.append("INNER JOIN cadastro.imovel i on i.imov_id = p.imov_id ")
				 .append("WHERE c.cnta_dtvencimentoconta < :dataAtual ")
				 .append("      AND c.dcst_idatual IN (:normal, :retificada, :incluida) ")
				 .append("      AND p.pcst_id = :parcelamentoSituacao ")
				 .append("      AND NOT EXISTS (SELECT cnta_id from arrecadacao.pagamento pg WHERE pg.cnta_id = c.cnta_id) ")
				 .append("      AND (i.imov_nnreparcelamento IS NULL OR i.imov_nnreparcelamento <= 0) ");
			
//			String groupBy = "GROUP BY p.parc_id, p.imov_id HAVING count(distinct c.cnta_id) >= :qtdContas ";
			
			String consulta = montarRaizConsulta(where.toString(), false);
			
			Query query = criarQuery(session, consulta.toString());
			List<Object[]> lista = query.setDate("dataAtual", new Date())
										.setInteger("normal", DebitoCreditoSituacao.NORMAL)
										.setInteger("retificada", DebitoCreditoSituacao.RETIFICADA)
										.setInteger("incluida", DebitoCreditoSituacao.INCLUIDA)
										.setInteger("parcelamentoSituacao", ParcelamentoSituacao.NORMAL)
										.setInteger("qtdContas", ConstantesSistema.QTD_CONTAS_CANCELAMENTO_PARCELAMENTO)
										.list();

			for (Object[] dados : lista) {
				Parcelamento parcelamento = pesquisarPorId((Integer) dados[0]);
				Imovel imovel = pesquisarImovelPorId((Integer) dados[1]);
				helper.add(new CancelarParcelamentoHelper(parcelamento, imovel, dados));
			}
		} catch (HibernateException e) {
			throw new ErroRepositorioException(e, "Erro no Hibernate");
		} finally {
			HibernateUtil.closeSession(session);
		}

		return helper;
	}
	
	private String montarRaizConsulta(String where, boolean botaoCancelar) {
		StringBuilder select = new StringBuilder();
		select.append("SELECT distinct p.parc_id as idParcelamento, ")
			  .append("p.imov_id as idImovel, ")
			  .append("p.parc_vlconta as valorContas, ")
			  .append("p.parc_vlentrada as valorEntrada, ")
			  .append("p.parc_vljurosmora + p.parc_vlmulta + p.parc_vlatualizacaomonetaria as valorAcrescimos, ")
			  .append("p.parc_vldescontoacrescimos as valorDescontoAcrescimos, ")
			  .append("p.parc_vldescontofaixa as valorDescontoFaixa, ")
			  .append("p.parc_nnprestacoes as numeroPrestacoes, ");
		
		StringBuilder join = new StringBuilder();
		
		if (botaoCancelar) {
			join.append("LEFT JOIN faturamento.debito_cobrado dc on dc.dbac_id = dac.dbac_id  ")
				.append("LEFT JOIN faturamento.conta c on c.cnta_id = dc.cnta_id ");
		} else {
			join.append("INNER JOIN faturamento.debito_cobrado dc on dc.dbac_id = dac.dbac_id  ")
				.append("INNER JOIN faturamento.conta c on c.cnta_id = dc.cnta_id ");
		}
		
		StringBuilder consulta = new StringBuilder();
		consulta.append(select)
				.append("       dac.dbac_nnprestacaocobradas as numeroPrestacoesCobradas, ")
				.append("       coalesce(p.parc_vlguiapapagamento,0) as valorGuias ")
				.append("FROM cobranca.parcelamento p ")
				.append("INNER JOIN faturamento.debito_a_cobrar dac on dac.parc_id = p.parc_id  ")
		        .append(join)
		        .append(where)
		        .append(montarComplementoGuia())
		        .append(" UNION ALL ")
		        .append(select)
		        .append("       dac.dahi_nnprestacaocobradas as numeroPrestacoesCobradas, ")
		        .append("       coalesce(p.parc_vlguiapapagamento,0) as valorGuias ")
			  .append("FROM cobranca.parcelamento p ")
		        .append("INNER JOIN faturamento.deb_a_cobrar_hist dac on dac.parc_id = p.parc_id  ")
		        .append(join)
		        .append(where)
		        .append(montarComplementoGuia());
		
		return consulta.toString();
	}
	
	private Parcelamento pesquisarPorId(Integer id) throws ErroRepositorioException {
		Session session = HibernateUtil.getSession();
		Parcelamento parcelamento = null;

		try {
			parcelamento = (Parcelamento) session.get(Parcelamento.class, id);
		} catch (HibernateException e) {
			throw new ErroRepositorioException(e, "Erro no Hibernate");
		} finally {
			HibernateUtil.closeSession(session);
		}

		return parcelamento;
	}
	
	private Imovel pesquisarImovelPorId(Integer id) throws ErroRepositorioException {
		Session session = HibernateUtil.getSession();
		Imovel imovel = null;

		try {
			StringBuilder consulta = new StringBuilder();
			consulta.append("SELECT i FROM Imovel i ")
					.append("INNER JOIN FETCH i.quadra ")
					.append("INNER JOIN FETCH i.setorComercial ")
					.append("LEFT JOIN FETCH i.ligacaoEsgoto ")
					.append("WHERE i.id = :id");
			
			imovel = (Imovel) session.createQuery(consulta.toString()).setInteger("id", id).uniqueResult();
		} catch (HibernateException e) {
			throw new ErroRepositorioException(e, "Erro no Hibernate");
		} finally {
			HibernateUtil.closeSession(session);
		}

		return imovel;
	}
	
	private Query criarQuery(Session session, String consulta) {
		return session.createSQLQuery(consulta.toString())
					  .addScalar("idParcelamento", Hibernate.INTEGER)
					  .addScalar("idImovel", Hibernate.INTEGER)
					  .addScalar("valorContas", Hibernate.BIG_DECIMAL)
					  .addScalar("valorEntrada", Hibernate.BIG_DECIMAL)
					  .addScalar("valorAcrescimos", Hibernate.BIG_DECIMAL)
					  .addScalar("valorDescontoAcrescimos", Hibernate.BIG_DECIMAL)
					  .addScalar("valorDescontoFaixa", Hibernate.BIG_DECIMAL)
					  .addScalar("numeroPrestacoes", Hibernate.SHORT)
					  .addScalar("numeroPrestacoesCobradas", Hibernate.SHORT)
					  .addScalar("valorGuias", Hibernate.BIG_DECIMAL);
	}
	
	private String montarComplementoGuia() {
		StringBuilder complemento = new StringBuilder();
		complemento.append("AND (p.parc_id IN (SELECT parc_id FROM arrecadacao.pagamento pg ")
				   .append("                   INNER JOIN faturamento.guia_pagamento gp on gp.gpag_id = pg.gpag_id ")
				   .append("                   WHERE parc_id IN (p.parc_id)) ")
				   .append(" OR (p.parc_id IN (SELECT parc_id FROM faturamento.guia_pagamento_historico ")
				   .append("                   WHERE parc_id IN (p.parc_id) and dcst_idatual = " + DebitoCreditoSituacao.NORMAL + "))) ");
		
		return complemento.toString();
	}
	
	public DebitoACobrar pesquisarDebitoACobrar(Integer idParcelamento, Integer idDebitoTipo) throws ErroRepositorioException {
		Session session = HibernateUtil.getSession();
		DebitoACobrar debitoACobrar = null;
		
		try {
			StringBuilder consulta = new StringBuilder();
			consulta.append("SELECT dac FROM DebitoACobrar dac ")
					.append("INNER JOIN FETCH dac.imovel i ")
					.append("INNER JOIN FETCH i.quadra ")
					.append("INNER JOIN FETCH i.setorComercial ")
					.append("WHERE dac.parcelamento.id = :idParcelamento AND dac.debitoTipo.id = :idDebitoTipo");
			
			debitoACobrar = (DebitoACobrar) session.createQuery(consulta.toString())
					.setInteger("idParcelamento", idParcelamento)
					.setInteger("idDebitoTipo", idDebitoTipo)
					.uniqueResult();
			
		} catch (HibernateException e) {
			throw new ErroRepositorioException(e, "Erro no Hibernate");
		} finally {
			HibernateUtil.closeSession(session);
		}
		
		return debitoACobrar;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarDebitoACobrarCurtoELongoPrazo(Integer idParcelamento) throws ErroRepositorioException {
		Session session = HibernateUtil.getSession();
		List<Object[]> retorno = null;
		
		try {
			StringBuilder consulta = new StringBuilder();
			consulta.append("SELECT dac.dbtp_id debitoTipo, (dac.dbac_vldebito/dbac_nnprestacaodebito)*(dbac_nnprestacaodebito-dbac_nnprestacaocobradas) valorRestante FROM faturamento.debito_a_cobrar dac ")
					.append("INNER JOIN faturamento.debito_tipo dt ON dt.dbtp_id = dac.dbtp_id ")
					.append("WHERE parc_id = :idParcelamento ")
					.append("AND dac.dbtp_id IN (:debitoTipoCurtoPrazo,:debitoTipoLongoPrazo) ")
					.append("UNION ALL ")
					.append("SELECT dch.dbtp_id, (dch.dahi_vldebito/dahi_nnprestacaodebito)*(dahi_nnprestacaodebito-dahi_nnprestacaocobradas) valorRestante FROM faturamento.deb_a_cobrar_hist dch ")
					.append("INNER JOIN faturamento.debito_tipo dt ON dt.dbtp_id = dch.dbtp_id ")
					.append("WHERE parc_id = :idParcelamento ")
					.append("AND dch.dbtp_id IN (:debitoTipoCurtoPrazo,:debitoTipoLongoPrazo) ");
			
			retorno = session.createSQLQuery(consulta.toString())
					.addScalar("debitoTipo", Hibernate.INTEGER)
					.addScalar("valorRestante", Hibernate.BIG_DECIMAL)
					.setInteger("idParcelamento", idParcelamento)
					.setInteger("debitoTipoCurtoPrazo", DebitoTipo.REPARCELAMENTOS_CURTO_PRAZO)
					.setInteger("debitoTipoLongoPrazo", DebitoTipo.REPARCELAMENTOS_LONGO_PRAZO)
					.list();
			
		} catch (HibernateException e) {
			throw new ErroRepositorioException(e, "Erro no Hibernate");
		} finally {
			HibernateUtil.closeSession(session);
		}
		
		return retorno;
	}
	
	public boolean isParcelamentoEmDebito(Integer idParcelamento) throws ErroRepositorioException {
		Session session = HibernateUtil.getSession();
		boolean retorno = false;
		
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count(p) AS qtd ")
			   .append("FROM cobranca.parcelamento p ")
			   .append("INNER JOIN faturamento.deb_a_cobrar_hist dac on dac.parc_id = p.parc_id ")
			   .append("INNER JOIN faturamento.debito_cobrado dc on dc.dbac_id = dac.dbac_id ")
			   .append("INNER JOIN faturamento.conta c on c.cnta_id = dc.cnta_id ")
			   .append("WHERE p.parc_id = :idParcelamento ")
			   .append("AND dac.dbtp_id = :debitoTipo ")
			   .append("AND not exists (SELECT * FROM arrecadacao.pagamento pag WHERE pag.cnta_id = c.cnta_id) ")
			   .append("AND not exists (SELECT * FROM arrecadacao.pagamento_historico pag WHERE pag.cnta_id = c.cnta_id)");
			
			int qtd = (Integer) session.createSQLQuery(sql.toString())
					.addScalar("qtd", Hibernate.INTEGER)
					.setInteger("idParcelamento", idParcelamento)
					.setInteger("debitoTipo", DebitoTipo.PARCELAMENTO_CONTAS)
					.uniqueResult();
			
			if (qtd > 0)
				retorno = true;
			
		} catch (HibernateException e) {
			throw new ErroRepositorioException(e, "Erro no Hibernate");
		} finally {
			HibernateUtil.closeSession(session);
		}
		
		return retorno;
	}
}
