package gcom.faturamento.debito;import gcom.cobranca.FiltroResolucaoDiretoria;import gcom.financeiro.FinanciamentoTipo;import gcom.financeiro.lancamento.LancamentoItemContabil;import gcom.interceptor.ControleAlteracao;import gcom.interceptor.ObjetoTransacao;import gcom.util.filtro.Filtro;import gcom.util.filtro.ParametroSimples;import java.math.BigDecimal;import java.util.Date;import org.apache.commons.lang.builder.ToStringBuilder;@ControleAlteracao()public class DebitoTipo extends ObjetoTransacao {	private static final long serialVersionUID = 1L;	public final static Integer TAXA_COBRANCA = new Integer(17);	public final static Integer ENTRADA_PARCELAMENTO = new Integer(33);	public final static Integer DESPESA_POSTAL = new Integer(97);	public final static Integer ATUALIZACAO_MONETARIA = new Integer(94);	public final static Integer JUROS_MORA = new Integer(91);	public final static Integer MULTA_IMPONTUALIDADE = new Integer(80);	public final static Integer PARCELAMENTO_CONTAS = new Integer(40);	public final static Integer PARCELAMENTO_GUIAS_PAGAMENTO = new Integer(41);	public final static Integer PARCELAMENTO_ACRESCIMOS_IMPONTUALIDADE = new Integer(43);	public final static Integer PARCELAMENTO_DEBITO_A_COBRAR_CURTO_PRAZO = new Integer(45);	public final static Integer PARCELAMENTO_DEBITO_A_COBRAR_LONGO_PRAZO = new Integer(47);	public final static Integer REPARCELAMENTOS_CURTO_PRAZO = new Integer(42);	public final static Integer REPARCELAMENTOS_LONGO_PRAZO = new Integer(50);	public final static Integer JUROS_SOBRE_PARCELAMENTO = new Integer(44);	public final static Integer OUTROS = new Integer(99);	public final static Integer ACRESCIMOS_POR_IMPONTUALIDADE = new Integer(100);	public final static Integer TAXA_2_VIA_CONTA = new Integer(101);	public final static Integer SANCOES_REGULAMENTARES = new Integer(12);	public final static Integer MULTA_POR_INFRACAO = new Integer(46);	public final static Integer CONSUMO_NAO_CONTABILIZADO = new Integer(48);	public final static Integer INSTAL_SUBST_HIDROMETRO = new Integer(49);	public final static Integer SERVICOS_ESPECIAIS = new Integer(15);	public final static Integer DOACAO_AO_PRO_CRIANCA = new Integer(104);	public final static Integer PARCELAMENTO_CONTAS_DIVIDA_ATIVA = new Integer(140);	public final static Integer PARCELAMENTO_ACRESCIMOS_IMPONTUALIDADE_DIVIDA_ATIVA = new Integer(143);	public final static Integer REPARCELAMENTOS_CURTO_PRAZO_DIVIDA_ATIVA = new Integer(142);	public final static Integer REPARCELAMENTOS_LONGO_PRAZO_DIVIDA_ATIVA = new Integer(150);	public final static Integer JUROS_SOBRE_PARCELAMENTO_DIVIDA_ATIVA = new Integer(144);	public final static Integer PAGAMENTO_PARCIAL_CONTA = new Integer(409);	public final static Integer DOACAO_AO_HOSPITAL_INFANTIL_VARELA_SANTIAGO = new Integer(202);	public final static Integer GUIA_DE_PAGAMENTO_NAO_ENCONTRADA = new Integer(8888);	public final static Integer DOACAO = 1;	public final static Integer MOVIMENTO_CARTAO_CREDITO = new Integer(2408);	public final static Integer CONSTANTE_TAXA_2_VIA_CONTA = new Integer(22);	public final static Integer TARIFA_CORTADO = new Integer(2500);	public final static Integer JUROS_SOBRE_CONTR_PARCELAMENTO = new Integer(115);	public final static Integer DOCUMENTO_NAO_ACEITO = new Integer(116);	public final static Integer VALOR_NAO_CONFERE = new Integer(2415);	public final static Integer CANCELAMENTO_PARCELAMENTO_DESCONTO_ACRESCIMOS = new Integer(2441);	private Integer id;	private LancamentoItemContabil lancamentoItemContabil;	private String descricao;	private String descricaoAbreviada;	private Short indicadorUso;	private Date ultimaAlteracao;	private BigDecimal valorLimite;	private Short indicadorGeracaoAutomatica;	private Short indicadorGeracaoConta;	private BigDecimal valorSugerido;	private BigDecimal valorLimiteInferior;	private Integer codigoConstante;	private FinanciamentoTipo financiamentoTipo;	private Short indicadorDebitoCartaoCredito;	private Short indicadorJurosParCliente;	public String[] retornaCamposChavePrimaria() {		String[] retorno = new String[1];		retorno[0] = "id";		return retorno;	}	public Filtro retornaFiltro() {		FiltroDebitoTipo filtro = new FiltroDebitoTipo();		filtro.adicionarCaminhoParaCarregamentoEntidade("lancamentoItemContabil");		filtro.adicionarCaminhoParaCarregamentoEntidade("financiamentoTipo");		filtro.adicionarParametro(new ParametroSimples(FiltroResolucaoDiretoria.CODIGO, this.getId()));		return filtro;	}	public DebitoTipo(LancamentoItemContabil lancamentoItemContabil, String descricao, String descricaoAbreviada, Short indicadorUso, Date ultimaAlteracao, BigDecimal valorLimite,			Short indicadorGeracaoAutomatica, FinanciamentoTipo financiamentoTipo) {		this.lancamentoItemContabil = lancamentoItemContabil;		this.descricao = descricao;		this.descricaoAbreviada = descricaoAbreviada;		this.indicadorUso = indicadorUso;		this.ultimaAlteracao = ultimaAlteracao;		this.valorLimite = valorLimite;		this.indicadorGeracaoAutomatica = indicadorGeracaoAutomatica;		this.financiamentoTipo = financiamentoTipo;	}	public DebitoTipo() {	}	public DebitoTipo(Integer id) {		this.id = id;	}	public DebitoTipo(LancamentoItemContabil lancamentoItemContabil, FinanciamentoTipo financiamentoTipo) {		this.lancamentoItemContabil = lancamentoItemContabil;		this.financiamentoTipo = financiamentoTipo;	}	public DebitoTipo(Integer id, String descricao) {		this.id = id;		this.descricao = descricao;	}	public Integer getId() {		return this.id;	}	public void setId(Integer id) {		this.id = id;	}	public LancamentoItemContabil getLancamentoItemContabil() {		return this.lancamentoItemContabil;	}	public void setLancamentoItemContabil(LancamentoItemContabil lancamentoItemContabil) {		this.lancamentoItemContabil = lancamentoItemContabil;	}	public String getDescricao() {		return this.descricao;	}	public void setDescricao(String descricao) {		this.descricao = descricao;	}	public String getDescricaoAbreviada() {		return this.descricaoAbreviada;	}	public void setDescricaoAbreviada(String descricaoAbreviada) {		this.descricaoAbreviada = descricaoAbreviada;	}	public Short getIndicadorUso() {		return this.indicadorUso;	}	public void setIndicadorUso(Short indicadorUso) {		this.indicadorUso = indicadorUso;	}	public Date getUltimaAlteracao() {		return this.ultimaAlteracao;	}	public void setUltimaAlteracao(Date ultimaAlteracao) {		this.ultimaAlteracao = ultimaAlteracao;	}	public BigDecimal getValorLimite() {		return this.valorLimite;	}	public void setValorLimite(BigDecimal valorLimite) {		this.valorLimite = valorLimite;	}	public Short getIndicadorGeracaoAutomatica() {		return this.indicadorGeracaoAutomatica;	}	public void setIndicadorGeracaoAutomatica(Short indicadorGeracaoAutomatica) {		this.indicadorGeracaoAutomatica = indicadorGeracaoAutomatica;	}	public FinanciamentoTipo getFinanciamentoTipo() {		return this.financiamentoTipo;	}	public void setFinanciamentoTipo(FinanciamentoTipo financiamentoTipo) {		this.financiamentoTipo = financiamentoTipo;	}	public String toString() {		return new ToStringBuilder(this).append("id", getId()).toString();	}	public Short getIndicadorGeracaoConta() {		return indicadorGeracaoConta;	}	public void setIndicadorGeracaoConta(Short indicadorGeracaoConta) {		this.indicadorGeracaoConta = indicadorGeracaoConta;	}	@Override	public boolean equals(Object other) {		boolean retorno = false;		if (other instanceof DebitoTipo) {			DebitoTipo castOther = (DebitoTipo) other;			retorno = this.getId().compareTo(castOther.getId()) == 0;		}		return retorno;	}	@Override	public void initializeLazy() {		this.retornaCamposChavePrimaria();		getDescricao();	}	@Override	public String getDescricaoParaRegistroTransacao() {		return getDescricao();	}	public BigDecimal getValorSugerido() {		return valorSugerido;	}	public void setValorSugerido(BigDecimal valorSugerido) {		this.valorSugerido = valorSugerido;	}	public BigDecimal getValorLimiteInferior() {		return valorLimiteInferior;	}	public void setValorLimiteInferior(BigDecimal valorLimiteInferior) {		this.valorLimiteInferior = valorLimiteInferior;	}	public Integer getCodigoConstante() {		return codigoConstante;	}	public void setCodigoConstante(Integer codigoConstante) {		this.codigoConstante = codigoConstante;	}	public Short getIndicadorDebitoCartaoCredito() {		return indicadorDebitoCartaoCredito;	}	public void setIndicadorDebitoCartaoCredito(Short indicadorDebitoCartaoCredito) {		this.indicadorDebitoCartaoCredito = indicadorDebitoCartaoCredito;	}	public Short getIndicadorJurosParCliente() {		return indicadorJurosParCliente;	}	public void setIndicadorJurosParCliente(Short indicadorJurosParCliente) {		this.indicadorJurosParCliente = indicadorJurosParCliente;	}	@Override	public int hashCode() {		return this.getId();	}}