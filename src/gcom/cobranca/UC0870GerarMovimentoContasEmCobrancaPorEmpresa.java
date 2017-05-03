package gcom.cobranca;

import gcom.atendimentopublico.ligacaoagua.LigacaoAguaSituacao;
import gcom.atendimentopublico.ordemservico.FiltroServicoTipo;
import gcom.atendimentopublico.ordemservico.OrdemServico;
import gcom.atendimentopublico.ordemservico.ServicoTipo;
import gcom.batch.ControladorBatchLocal;
import gcom.batch.ControladorBatchLocalHome;
import gcom.cadastro.cliente.Cliente;
import gcom.cadastro.empresa.EmpresaCobranca;
import gcom.cadastro.empresa.EmpresaCobrancaFaixa;
import gcom.cadastro.empresa.FiltroEmpresaCobranca;
import gcom.cadastro.empresa.FiltroEmpresaCobrancaFaixa;
import gcom.cadastro.imovel.ControladorImovelLocal;
import gcom.cadastro.imovel.ControladorImovelLocalHome;
import gcom.cadastro.imovel.Imovel;
import gcom.cadastro.imovel.ImovelCobrancaSituacao;
import gcom.cadastro.imovel.ImovelPerfil;
import gcom.cadastro.localidade.GerenciaRegional;
import gcom.cadastro.localidade.UnidadeNegocio;
import gcom.cadastro.sistemaparametro.SistemaParametro;
import gcom.cadastro.unidade.UnidadeOrganizacional;
import gcom.fachada.Fachada;
import gcom.faturamento.ControladorFaturamentoLocal;
import gcom.faturamento.ControladorFaturamentoLocalHome;
import gcom.faturamento.conta.ContaGeral;
import gcom.seguranca.acesso.Funcionalidade;
import gcom.seguranca.acesso.usuario.Usuario;
import gcom.util.ConstantesJNDI;
import gcom.util.ConstantesSistema;
import gcom.util.ControladorException;
import gcom.util.ControladorUtilLocal;
import gcom.util.ControladorUtilLocalHome;
import gcom.util.ErroRepositorioException;
import gcom.util.ServiceLocator;
import gcom.util.ServiceLocatorException;
import gcom.util.SistemaException;
import gcom.util.Util;
import gcom.util.filtro.ParametroNulo;
import gcom.util.filtro.ParametroSimples;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;

public class UC0870GerarMovimentoContasEmCobrancaPorEmpresa {

	private static UC0870GerarMovimentoContasEmCobrancaPorEmpresa instancia;

	private IRepositorioCobranca repositorioCobranca;

	private UC0870GerarMovimentoContasEmCobrancaPorEmpresa(IRepositorioCobranca repositorioCobranca) {
		this.repositorioCobranca = repositorioCobranca;
	}

	public static UC0870GerarMovimentoContasEmCobrancaPorEmpresa getInstancia(IRepositorioCobranca repositorioCobranca) {
		if (instancia == null) {
			instancia = new UC0870GerarMovimentoContasEmCobrancaPorEmpresa(repositorioCobranca);
		}
		return instancia;
	}

	private ControladorUtilLocal getControladorUtil() {
		try {
			ServiceLocator locator = ServiceLocator.getInstancia();
			ControladorUtilLocalHome localHome = (ControladorUtilLocalHome) locator.getLocalHome(ConstantesJNDI.CONTROLADOR_UTIL_SEJB);

			return localHome.create();
		} catch (CreateException e) {
			throw new SistemaException(e);
		} catch (ServiceLocatorException e) {
			throw new SistemaException(e);
		}
	}

	protected ControladorImovelLocal getControladorImovel() {

		try {
			ServiceLocator locator = ServiceLocator.getInstancia();
			ControladorImovelLocalHome localHome = (ControladorImovelLocalHome) locator.getLocalHome(ConstantesJNDI.CONTROLADOR_IMOVEL_SEJB);

			return localHome.create();
		} catch (CreateException e) {
			throw new SistemaException(e);
		} catch (ServiceLocatorException e) {
			throw new SistemaException(e);
		}
	}

	private ControladorFaturamentoLocal getControladorFaturamento() {
		try {
			ServiceLocator locator = ServiceLocator.getInstancia();
			ControladorFaturamentoLocalHome localHome = (ControladorFaturamentoLocalHome) locator.getLocalHomePorEmpresa(ConstantesJNDI.CONTROLADOR_FATURAMENTO_SEJB);

			return localHome.create();
		} catch (CreateException e) {
			throw new SistemaException(e);
		} catch (ServiceLocatorException e) {
			throw new SistemaException(e);
		}
	}

	protected ControladorCobrancaLocal getControladorCobranca() {
		try {
			ServiceLocator locator = ServiceLocator.getInstancia();
			ControladorCobrancaLocalHome localHome = (ControladorCobrancaLocalHome) locator.getLocalHomePorEmpresa(ConstantesJNDI.CONTROLADOR_COBRANCA_SEJB);

			return localHome.create();
		} catch (CreateException e) {
			throw new SistemaException(e);
		} catch (ServiceLocatorException e) {
			throw new SistemaException(e);
		}
	}

	protected ControladorBatchLocal getControladorBatch() {
		try {
			ServiceLocator locator = ServiceLocator.getInstancia();
			ControladorBatchLocalHome localHome = (ControladorBatchLocalHome) locator.getLocalHome(ConstantesJNDI.CONTROLADOR_BATCH_SEJB);
			return localHome.create();
		} catch (CreateException e) {
			throw new SistemaException(e);
		} catch (ServiceLocatorException e) {
			throw new SistemaException(e);
		}

	}

	public void gerarMovimentoContasEmCobranca(ComandoEmpresaCobrancaConta comando) throws ControladorException {
		try {
			SistemaParametro sistemaParametros = getControladorUtil().pesquisarParametrosDoSistema();
			
			Integer anoMesArrecadacaoInicio = sistemaParametros.getAnoMesArrecadacao();
			Integer anoMesArrecadacaoFim = Util.somaMesAnoMesReferencia(sistemaParametros.getAnoMesArrecadacao(), 36);

			EmpresaCobranca empresaCobranca = filtrarEmpresaCobranca(comando);

			Collection<Integer> idsImoveis = null;

			Collection<Integer> idsImoveisAtualizar = new ArrayList<Integer>();
			Collection<CobrancaSituacaoHistorico> colecaoCobrancaSituacaoHistorico = new ArrayList<CobrancaSituacaoHistorico>();
			Collection<ImovelCobrancaSituacao> colecaoImovelCobrancaSituacao = new ArrayList<ImovelCobrancaSituacao>();
			
			
			ComandoEmpresaCobrancaContaHelper helper = new ComandoEmpresaCobrancaContaHelper();
			helper.setComandoEmpresaCobrancaConta(comando);
			helper.setColecaoGerenciaRegional(filtrarContaGerencia(comando, helper));
			helper.setColecaoUnidadeNegocio(filtrarUnidadeNegocio(comando, helper));
			helper.setColecaoImovelPerfil(filtrarImovelPerfil(comando, helper));
			helper.setColecaoLigacaoAguaSituacao(filtrarLigacaoAguaSituacao(comando, helper));

			ServicoTipo servicoTipo = filtrarServicoTipo(comando);

			idsImoveis = getControladorFaturamento().pesquisarImoveisInformarContasEmCobranca(helper, empresaCobranca.isPercentualInformado());

			if (idsImoveis != null && !idsImoveis.isEmpty()) {
				System.out.println("Cobran�a por Resultado - Quantidade de Im�veis do Comando " + comando.getId() + ": " + idsImoveis.size());

				Collection<EmpresaCobrancaConta> colecaoEmpresaCobrancaConta = new ArrayList<EmpresaCobrancaConta>();

				Collection<Object[]> colecaoDados = repositorioCobranca.pesquisarContasInformarContasEmCobranca(comando, idsImoveis, sistemaParametros);

				if (colecaoDados != null && !colecaoDados.isEmpty()) {

					Map<Integer, Integer> imovelOS = new HashMap<Integer, Integer>();

					for (Object[] dados : colecaoDados) {

						if (!empresaCobranca.isPercentualInformado() && dados != null && dados[2] != null && ((Short) dados[2]).equals(ConstantesSistema.NAO)) {
							continue;
						}

						EmpresaCobrancaConta empresaCobrancaConta = criarEmpresaCobrancaConta(dados, comando, empresaCobranca);

						if (dados != null) {
							if (dados[3] != null) {
								Integer idImovel = (Integer) dados[3];
								if (!idsImoveisAtualizar.contains(idImovel)) {
									CobrancaSituacaoHistorico cobrancaSituacaoHistorico = criarCobrancaSituacaoHistorico(idImovel, anoMesArrecadacaoInicio, anoMesArrecadacaoFim);
									colecaoCobrancaSituacaoHistorico.add(cobrancaSituacaoHistorico);

									ImovelCobrancaSituacao imovelCobrancaSituacao = criarImovelCobrancaSituacao(idImovel, comando);
									colecaoImovelCobrancaSituacao.add(imovelCobrancaSituacao);

									idsImoveisAtualizar.add(empresaCobrancaConta.getImovel().getId());

									if (servicoTipo != null && servicoTipo.getId() != null) {
										Imovel imovel = new Imovel();
										imovel.setId(new Integer(idImovel));

										if (servicoTipo != null && servicoTipo.getId() != null) {
											UnidadeOrganizacional unidadeOrganizacional = Fachada.getInstancia().pesquisarUnidadeOrganizacionalPorImovel(imovel.getId());

											OrdemServico ordemServico = new OrdemServico();
											ordemServico.setServicoTipo(servicoTipo);
											ordemServico.setImovel(imovel);
											ordemServico.setUnidadeAtual(unidadeOrganizacional);

											Integer idOS = Fachada.getInstancia().gerarOrdemServico(ordemServico, Usuario.USUARIO_BATCH, Funcionalidade.GERAR_MOVIMENTO_CONTAS_COBRANCA_POR_EMPRESA);

											imovelOS.put(idImovel, idOS);
										}
									}
								}
							}
						}

						if (imovelOS != null && !imovelOS.isEmpty() && imovelOS.get(empresaCobrancaConta.getImovel().getId()) != null) {
							OrdemServico ordemServico = new OrdemServico();
							ordemServico.setId(imovelOS.get(empresaCobrancaConta.getImovel().getId()));

							empresaCobrancaConta.setOrdemServico(ordemServico);
						}

						colecaoEmpresaCobrancaConta.add(empresaCobrancaConta);
					}

					getControladorBatch().inserirColecaoObjetoParaBatch(colecaoEmpresaCobrancaConta);

					idsImoveis.clear();
					idsImoveis = null;
					colecaoEmpresaCobrancaConta.clear();
					colecaoEmpresaCobrancaConta = null;
				}
			}

			if (empresaCobranca.isPercentualInformado()) {
				getControladorBatch().inserirColecaoObjetoParaBatch(colecaoCobrancaSituacaoHistorico);
			}

			getControladorBatch().inserirColecaoObjetoParaBatch(colecaoImovelCobrancaSituacao);

			Integer idCobrancaSituacao = repositorioCobranca.pesquisarCobrancaSituacao(CobrancaSituacao.COBRANCA_EMPRESA_TERCEIRIZADA);

			if (idCobrancaSituacao != null && idsImoveisAtualizar != null && !idsImoveisAtualizar.isEmpty()) {
				getControladorImovel().atualizarSituacaoCobrancaETipoIdsImoveis(idCobrancaSituacao, CobrancaSituacaoTipo.COBRANCA_EMPRESA_TERCEIRIZADA, idsImoveisAtualizar);
			}
		} catch (ErroRepositorioException e) {
			throw new ControladorException("erro.sistema", e);
		}
	}

	@SuppressWarnings("unchecked")
	private EmpresaCobranca filtrarEmpresaCobranca(ComandoEmpresaCobrancaConta comando) throws ControladorException {
		FiltroEmpresaCobranca filtro = new FiltroEmpresaCobranca();
		filtro.adicionarParametro(new ParametroSimples(FiltroEmpresaCobranca.EMPRESA_ID, comando.getEmpresa().getId()));
		filtro.adicionarParametro(new ParametroNulo(FiltroEmpresaCobranca.DATA_FIM_CONTRATO));

		Collection<EmpresaCobranca> colecaoEmpresaCobranca = getControladorUtil().pesquisar(filtro, EmpresaCobranca.class.getName());

		if (colecaoEmpresaCobranca != null && !colecaoEmpresaCobranca.isEmpty()) {
			return (EmpresaCobranca) Util.retonarObjetoDeColecao(colecaoEmpresaCobranca);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private ServicoTipo filtrarServicoTipo(ComandoEmpresaCobrancaConta comando) {
		ServicoTipo servicoTipo = null;
		if (comando.getServicoTipo() != null && comando.getServicoTipo().getId() != null) {
			FiltroServicoTipo filtroServicoTipo = new FiltroServicoTipo();
			filtroServicoTipo.adicionarParametro(new ParametroSimples(FiltroServicoTipo.ID, comando.getServicoTipo().getId()));
			filtroServicoTipo.adicionarCaminhoParaCarregamentoEntidade(FiltroServicoTipo.SERVICO_TIPO_REFERENCIA);
			Collection<ServicoTipo> colecao = Fachada.getInstancia().pesquisar(filtroServicoTipo, ServicoTipo.class.getName());

			if (colecao != null && !colecao.isEmpty()) {
				servicoTipo = (ServicoTipo) Util.retonarObjetoDeColecao(colecao);
			}
		}
		
		return servicoTipo;
	}

	@SuppressWarnings("rawtypes")
	private Collection<LigacaoAguaSituacao> filtrarLigacaoAguaSituacao(ComandoEmpresaCobrancaConta comando, ComandoEmpresaCobrancaContaHelper helper) {
		Collection<LigacaoAguaSituacao> colecaoLigacaoAguaSituacao = new ArrayList<LigacaoAguaSituacao>();
		
		FiltroCmdEmpresaCobrancaContaLigacaoAguaSituacao filtro = new FiltroCmdEmpresaCobrancaContaLigacaoAguaSituacao();
		filtro.adicionarParametro(new ParametroSimples(FiltroCmdEmpresaCobrancaContaLigacaoAguaSituacao.ID_COMANDO_EMPRESA_COBRANCA_CONTA, comando.getId()));
		filtro.adicionarCaminhoParaCarregamentoEntidade(FiltroCmdEmpresaCobrancaContaLigacaoAguaSituacao.LIGACAO_AGUA_SITUACAO);
		
		Collection colecao = Fachada.getInstancia().pesquisar(filtro, CmdEmpresaCobrancaContaLigacaoAguaSituacao.class.getName());

		if (colecao != null && !colecao.isEmpty()) {
			Iterator iterator = colecao.iterator();
			while (iterator.hasNext()) {
				CmdEmpresaCobrancaContaLigacaoAguaSituacao comandoLigacaoAguaSituacao = (CmdEmpresaCobrancaContaLigacaoAguaSituacao) iterator.next();
				LigacaoAguaSituacao ligacaoAguaSituacao = comandoLigacaoAguaSituacao.getLigacaoAguaSituacao();
				colecaoLigacaoAguaSituacao.add(ligacaoAguaSituacao);
			}
			return colecaoLigacaoAguaSituacao;
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private Collection<ImovelPerfil> filtrarImovelPerfil(ComandoEmpresaCobrancaConta comando, ComandoEmpresaCobrancaContaHelper helper) {
		Collection<ImovelPerfil> colecaoImovelPerfil = new ArrayList<ImovelPerfil>();
		
		FiltroComandoEmpresaCobrancaContaImovelPerfil filtro = new FiltroComandoEmpresaCobrancaContaImovelPerfil();
		filtro.adicionarParametro(new ParametroSimples(FiltroComandoEmpresaCobrancaContaImovelPerfil.ID_COMANDO_EMPRESA_COBRANCA_CONTA, comando.getId()));
		filtro.adicionarCaminhoParaCarregamentoEntidade("imovelPerfil");
		
		Collection colecao = Fachada.getInstancia().pesquisar(filtro, ComandoEmpresaCobrancaContaImovelPerfil.class.getName());

		if (colecao != null && !colecao.isEmpty()) {
			Iterator iterator = colecao.iterator();
			while (iterator.hasNext()) {
				ComandoEmpresaCobrancaContaImovelPerfil comandoImovelPerfil = (ComandoEmpresaCobrancaContaImovelPerfil) iterator.next();
				colecaoImovelPerfil.add(comandoImovelPerfil.getImovelPerfil());
			}
			return colecaoImovelPerfil;
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private Collection<GerenciaRegional> filtrarContaGerencia(ComandoEmpresaCobrancaConta comando, ComandoEmpresaCobrancaContaHelper helper) {
		Collection<GerenciaRegional> colecaoGerenciaRegional = new ArrayList<GerenciaRegional>();
		
		FiltroComandoEmpresaCobrancaContaGerencia filtro = new FiltroComandoEmpresaCobrancaContaGerencia();
		filtro.adicionarParametro(new ParametroSimples(FiltroComandoEmpresaCobrancaContaGerencia.ID_COMANDO_EMPRESA_COBRANCA_CONTA, comando.getId()));
		Collection colecao = Fachada.getInstancia().pesquisar(filtro, ComandoEmpresaCobrancaContaGerencia.class.getName());

		if (colecao != null && !colecao.isEmpty()) {
			Iterator iterator = colecao.iterator();
			while (iterator.hasNext()) {
				colecaoGerenciaRegional.add((GerenciaRegional) iterator.next());
			}
			return colecaoGerenciaRegional;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Collection<UnidadeNegocio> filtrarUnidadeNegocio(ComandoEmpresaCobrancaConta comando, ComandoEmpresaCobrancaContaHelper helper) {
		Collection<UnidadeNegocio> colecaoUnidadeNegocio = new ArrayList<UnidadeNegocio>();
		
		FiltroComandoEmpresaCobrancaContaUnidadeNegocio filtro = new FiltroComandoEmpresaCobrancaContaUnidadeNegocio();
		filtro.adicionarParametro(new ParametroSimples(FiltroComandoEmpresaCobrancaContaUnidadeNegocio.ID_COMANDO_EMPRESA_COBRANCA_CONTA, comando.getId()));
		Collection colecao = Fachada.getInstancia().pesquisar(filtro, ComandoEmpresaCobrancaContaUnidadeNegocio.class.getName());

		if (colecao != null && !colecao.isEmpty()) {
			Iterator iterator = colecao.iterator();
			while (iterator.hasNext()) {
				colecaoUnidadeNegocio.add((UnidadeNegocio) iterator.next());
			}
			return colecaoUnidadeNegocio;
		} else {
			return null;
		}
	}
	
	private EmpresaCobrancaConta criarEmpresaCobrancaConta(Object[] dadosEmpresaCobrancaConta, ComandoEmpresaCobrancaConta comandoEmpresaCobrancaConta, EmpresaCobranca empresaCobranca)
			throws ControladorException, ErroRepositorioException {

		EmpresaCobrancaConta retorno = new EmpresaCobrancaConta();
		Integer idImovel = null;

		if (dadosEmpresaCobrancaConta != null) {

			// Id da Conta
			if (dadosEmpresaCobrancaConta[0] != null) {
				ContaGeral contaGeral = new ContaGeral();
				contaGeral.setId((Integer) dadosEmpresaCobrancaConta[0]);
				retorno.setContaGeral(contaGeral);
			}

			// Valor Original da Conta
			if (dadosEmpresaCobrancaConta[1] != null) {
				retorno.setValorOriginalConta((BigDecimal) dadosEmpresaCobrancaConta[1]);
			}

			// Indicador Pagamento V�lido
			if (dadosEmpresaCobrancaConta[2] != null) {
				retorno.setIndicadorPagamentoValido((Short) dadosEmpresaCobrancaConta[2]);
			}
			// imovel
			if (dadosEmpresaCobrancaConta[3] != null) {
				Imovel imovel = new Imovel();
				imovel.setId((Integer) dadosEmpresaCobrancaConta[3]);
				retorno.setImovel(imovel);

				idImovel = imovel.getId();
			}

		}

		if (empresaCobranca != null) {

			if (empresaCobranca.getPercentualContratoCobranca() != null) {

				retorno.setPercentualEmpresaConta(empresaCobranca.getPercentualContratoCobranca());

			} else {
				SistemaParametro sistemaParametros = getControladorUtil().pesquisarParametrosDoSistema();
				Integer quantidadeContas = repositorioCobranca.pesquisarQuantidadeContasEmCobrancaPorImovel(comandoEmpresaCobrancaConta, idImovel, sistemaParametros);

				FiltroEmpresaCobrancaFaixa filtroEmpresaCobrancaFaixa = new FiltroEmpresaCobrancaFaixa();
				filtroEmpresaCobrancaFaixa.adicionarParametro(new ParametroSimples(FiltroEmpresaCobrancaFaixa.EMPRESA_CONTRATO_COBRANCA_ID, empresaCobranca.getId()));

				List<EmpresaCobrancaFaixa> colecaoEmpresaCobrancaFaixa = (List<EmpresaCobrancaFaixa>) Fachada.getInstancia()
						.pesquisar(filtroEmpresaCobrancaFaixa, EmpresaCobrancaFaixa.class.getName());

				if (colecaoEmpresaCobrancaFaixa != null && !colecaoEmpresaCobrancaFaixa.isEmpty()) {

					for (int i = 0; i < colecaoEmpresaCobrancaFaixa.size(); i++) {

						EmpresaCobrancaFaixa empresaCobrancaFaixa = (EmpresaCobrancaFaixa) colecaoEmpresaCobrancaFaixa.get(i);

						Integer numeroMinimoContas = empresaCobrancaFaixa.getNumeroMinimoContasFaixa();

						Integer numeroMaximoContas = null;

						if (i < (colecaoEmpresaCobrancaFaixa.size() - 1)) {
							numeroMaximoContas = ((EmpresaCobrancaFaixa) colecaoEmpresaCobrancaFaixa.get(i + 1)).getNumeroMinimoContasFaixa() - 1;
						}

						if (quantidadeContas >= numeroMinimoContas && (numeroMaximoContas == null || quantidadeContas <= numeroMaximoContas)) {

							retorno.setPercentualEmpresaConta(empresaCobrancaFaixa.getPercentualFaixa());
							break;
						}

					}
				}
			}

		}

		retorno.setEmpresa(comandoEmpresaCobrancaConta.getEmpresa());
		retorno.setComandoEmpresaCobrancaConta(comandoEmpresaCobrancaConta);
		retorno.setUltimaAlteracao(new Date());

		return retorno;
	}

	private CobrancaSituacaoHistorico criarCobrancaSituacaoHistorico(Integer idImovel, Integer anoMesArrecadacaoInicio, Integer anoMesArrecadacaoFim) {

		CobrancaSituacaoHistorico cobrancaSituacaoHistorico = new CobrancaSituacaoHistorico();

		Imovel imovel = new Imovel();
		imovel.setId(idImovel);
		cobrancaSituacaoHistorico.setImovel(imovel);

		CobrancaSituacaoMotivo cobrancaSituacaoMotivo = new CobrancaSituacaoMotivo();
		cobrancaSituacaoMotivo.setId(CobrancaSituacaoMotivo.IMOVEIS_ENVIADOS_EMPRESA_TERCEIRIZADA);
		cobrancaSituacaoHistorico.setCobrancaSituacaoMotivo(cobrancaSituacaoMotivo);

		CobrancaSituacaoTipo cobrancaSituacaoTipo = new CobrancaSituacaoTipo();
		cobrancaSituacaoTipo.setId(CobrancaSituacaoTipo.COBRANCA_EMPRESA_TERCEIRIZADA);
		cobrancaSituacaoHistorico.setCobrancaSituacaoTipo(cobrancaSituacaoTipo);

		cobrancaSituacaoHistorico.setUltimaAlteracao(new Date());

		cobrancaSituacaoHistorico.setAnoMesCobrancaSituacaoInicio(anoMesArrecadacaoInicio);
		cobrancaSituacaoHistorico.setAnoMesCobrancaSituacaoFim(anoMesArrecadacaoFim);

		cobrancaSituacaoHistorico.setUsuario(Usuario.USUARIO_BATCH);
		cobrancaSituacaoHistorico.setUsuarioInforma(Usuario.USUARIO_BATCH);

		return cobrancaSituacaoHistorico;
	}

	private ImovelCobrancaSituacao criarImovelCobrancaSituacao(Integer idImovel, ComandoEmpresaCobrancaConta comandoEmpresaCobrancaConta) throws ControladorException, ErroRepositorioException {

		ImovelCobrancaSituacao imovelCobrancaSituacao = new ImovelCobrancaSituacao();

		Imovel imovel = new Imovel();
		imovel.setId(idImovel);
		imovelCobrancaSituacao.setImovel(imovel);

		imovelCobrancaSituacao.setDataImplantacaoCobranca(new Date());
		imovelCobrancaSituacao.setUltimaAlteracao(new Date());

		Integer idCobSituacao = repositorioCobranca.pesquisarCobrancaSituacao(CobrancaSituacao.COBRANCA_EMPRESA_TERCEIRIZADA);

		CobrancaSituacao cobrancaSituacao = new CobrancaSituacao();
		cobrancaSituacao.setId(idCobSituacao);
		imovelCobrancaSituacao.setCobrancaSituacao(cobrancaSituacao);

		Cliente cliente = getControladorImovel().pesquisarClienteUsuarioImovel(imovelCobrancaSituacao.getImovel().getId());

		imovelCobrancaSituacao.setCliente(cliente);

		return imovelCobrancaSituacao;
	}

}
