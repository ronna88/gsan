package gcom.gui.cobranca.spcserasa;

import gcom.cadastro.localidade.FiltroGerenciaRegional;
import gcom.cadastro.localidade.FiltroLocalidade;
import gcom.cadastro.localidade.FiltroSetorComercial;
import gcom.cadastro.localidade.FiltroUnidadeNegocio;
import gcom.cadastro.localidade.GerenciaRegional;
import gcom.cadastro.localidade.Localidade;
import gcom.cadastro.localidade.SetorComercial;
import gcom.cadastro.localidade.UnidadeNegocio;
import gcom.cobranca.CobrancaGrupo;
import gcom.cobranca.FiltroCobrancaGrupo;
import gcom.fachada.Fachada;
import gcom.gui.ActionServletException;
import gcom.gui.GcomAction;
import gcom.util.ConstantesSistema;
import gcom.util.Util;
import gcom.util.filtro.ParametroSimples;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Esta classe tem por finalidade exibir para o usu�rio a tela que receber� os
 * par�metros para realiza��o da inser��o de um Comando de Negativa��o (Aba n�
 * 04 - Localiza��o)
 * 
 * @author Ana Maria
 * @date 06/11/2007
 */
public class ExibirInserirComandoNegativacaoLocalizacaoAction extends
		GcomAction {

	public ActionForward execute(ActionMapping actionMapping,
			ActionForm actionForm, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		ActionForward retorno = actionMapping
				.findForward("inserirComandoNegativacaoLocalizacao");

		HttpSession sessao = httpServletRequest.getSession(false);

		Fachada fachada = Fachada.getInstancia();

		fachada.validarInclusaoProcessosNegativacao(null);
		
		InserirComandoNegativacaoActionForm inserirComandoNegativacaoActionForm = (InserirComandoNegativacaoActionForm) actionForm;

		// Caso informe o cliente, os campos da Aba 5 - Dados da Localiza��o
		// devem ser desabilitados
		// Caso informe o id da simula��o, os campos da Aba 5 - Dados da
		// Localiza��o devem ser desabilitados
		if ((inserirComandoNegativacaoActionForm.getIdCliente() != null && !inserirComandoNegativacaoActionForm
				.getIdCliente().equals(""))
				|| (inserirComandoNegativacaoActionForm.getIdComandoSimulado() != null && !inserirComandoNegativacaoActionForm
						.getIdComandoSimulado().equals(""))) {
			inserirComandoNegativacaoActionForm.setCobrancaGrupo(null);
			inserirComandoNegativacaoActionForm.setGerenciaRegional(null);
			inserirComandoNegativacaoActionForm.setUnidadeNegocio(null);
			inserirComandoNegativacaoActionForm.setEloPolo(null);
			inserirComandoNegativacaoActionForm.setIdLocalidadeInicial(null);
			inserirComandoNegativacaoActionForm
					.setLocalidadeDescricaoInicial(null);
			inserirComandoNegativacaoActionForm
					.setCodigoSetorComercialInicial(null);
			inserirComandoNegativacaoActionForm
					.setSetorComercialDescricaoInicial(null);
			inserirComandoNegativacaoActionForm.setIdLocalidadeFinal(null);
			inserirComandoNegativacaoActionForm
					.setLocalidadeDescricaoFinal(null);
			inserirComandoNegativacaoActionForm
					.setCodigoSetorComercialFinal(null);
			inserirComandoNegativacaoActionForm
					.setSetorComercialDescricaoFinal(null);
			httpServletRequest.setAttribute("desabilitar", "ok");
		}

		// Pesquisando grupo de cobran�a
		if (sessao.getAttribute("colcaoCobrancaGrupo") == null) {
			FiltroCobrancaGrupo filtroCobrancaGrupo = new FiltroCobrancaGrupo();
			filtroCobrancaGrupo.setCampoOrderBy(FiltroCobrancaGrupo.DESCRICAO);
			Collection colcaoCobrancaGrupo = fachada.pesquisar(
					filtroCobrancaGrupo, CobrancaGrupo.class.getName());
			if (colcaoCobrancaGrupo != null && !colcaoCobrancaGrupo.isEmpty()) {
				sessao.setAttribute("colcaoCobrancaGrupo", colcaoCobrancaGrupo);
			} else {
				throw new ActionServletException("atencao.naocadastrado", null,
						"Grupo Cobran�a");
			}
		}

		// Pesquisando ger�ncia regional
		if (sessao.getAttribute("colecaoGerenciaRegional") == null) {
			FiltroGerenciaRegional filtroGerenciaRegional = new FiltroGerenciaRegional();
			filtroGerenciaRegional.setCampoOrderBy(FiltroGerenciaRegional.NOME);
			Collection colecaoGerenciaRegional = fachada.pesquisar(
					filtroGerenciaRegional, GerenciaRegional.class.getName());
			if (colecaoGerenciaRegional != null
					&& !colecaoGerenciaRegional.isEmpty()) {
				sessao.setAttribute("colecaoGerenciaRegional",
						colecaoGerenciaRegional);
			} else {
				throw new ActionServletException("atencao.naocadastrado", null,
						"Ger�ncia Regional");
			}
		}

		// Pesquisando unidade de neg�cio
		if (sessao.getAttribute("colecaoUnidadeNegocio") == null) {
			FiltroUnidadeNegocio filtroUnidadeNegocio = new FiltroUnidadeNegocio();
			filtroUnidadeNegocio.setCampoOrderBy(FiltroUnidadeNegocio.NOME);
			Collection colecaoUnidadeNegocio = fachada.pesquisar(
					filtroUnidadeNegocio, UnidadeNegocio.class.getName());
			if (colecaoUnidadeNegocio != null
					&& !colecaoUnidadeNegocio.isEmpty()) {
				sessao.setAttribute("colecaoUnidadeNegocio",
						colecaoUnidadeNegocio);
			} else {
				throw new ActionServletException("atencao.naocadastrado", null,
						"Unidade Neg�cio");
			}
		}

		// Pesquisando Elo P�lo
		if (sessao.getAttribute("colecaoEloPolo") == null) {

			// Collection colecaoEloPolo = fachada.pesquisarEloPolo();

			FiltroLocalidade filtro = new FiltroLocalidade();
			filtro.adicionarParametro(new ParametroSimples(
					FiltroLocalidade.INDICADORUSO,
					ConstantesSistema.INDICADOR_USO_ATIVO));
			filtro.setCampoOrderBy(FiltroLocalidade.DESCRICAO);

			Collection colecaoEloPolo = fachada.pesquisar(filtro,
					Localidade.class.getName());

			if (colecaoEloPolo != null && !colecaoEloPolo.isEmpty()) {
				sessao.setAttribute("colecaoEloPolo", colecaoEloPolo);
			} else {
				throw new ActionServletException("atencao.naocadastrado", null,
						"Elo P�lo");
			}
		}

		// Pesquisa Localidade Incial
		String idLocalidadeInicial = inserirComandoNegativacaoActionForm
				.getIdLocalidadeInicial();
		if (idLocalidadeInicial != null && !idLocalidadeInicial.equals("")) {

			FiltroLocalidade filtroLocalidade = new FiltroLocalidade();

			filtroLocalidade.adicionarParametro(new ParametroSimples(
					FiltroLocalidade.ID, idLocalidadeInicial));

			Collection colecaoLocalidade = fachada.pesquisar(filtroLocalidade,
					Localidade.class.getName());

			if (colecaoLocalidade != null && !colecaoLocalidade.isEmpty()) {
				httpServletRequest.setAttribute("idLocalidadeNaoEncontrada",
						"valor");
				httpServletRequest.setAttribute(
						"idLocalidadeFinalNaoEncontrada", "valor");

				inserirComandoNegativacaoActionForm.setIdLocalidadeInicial(""
						+ ((Localidade) ((List) colecaoLocalidade).get(0))
								.getId());
				inserirComandoNegativacaoActionForm
						.setLocalidadeDescricaoInicial(""
								+ ((Localidade) ((List) colecaoLocalidade)
										.get(0)).getDescricao());
				inserirComandoNegativacaoActionForm
						.setLocalidadeDescricaoFinal(""
								+ ((Localidade) ((List) colecaoLocalidade)
										.get(0)).getDescricao());
			} else {
				httpServletRequest.setAttribute("idLocalidadeNaoEncontrada",
						"exception");
				inserirComandoNegativacaoActionForm
						.setIdLocalidadeInicial(null);
				inserirComandoNegativacaoActionForm.setIdLocalidadeFinal(null);
				httpServletRequest.setAttribute(
						"idLocalidadeFinalNaoEncontrada", "exception");
				inserirComandoNegativacaoActionForm
						.setLocalidadeDescricaoInicial("Localidade Inexistente");
				inserirComandoNegativacaoActionForm
						.setLocalidadeDescricaoFinal("Localidade Inexistente");
			}
		}

		// Pesquisa Localidade Final
		String idLocalidadeFinal = inserirComandoNegativacaoActionForm
				.getIdLocalidadeFinal();
		if (idLocalidadeFinal != null && !idLocalidadeFinal.equals("")) {

			FiltroLocalidade filtroLocalidade = new FiltroLocalidade();

			filtroLocalidade.adicionarParametro(new ParametroSimples(
					FiltroLocalidade.ID, idLocalidadeFinal));

			Collection colecaoLocalidade = fachada.pesquisar(filtroLocalidade,
					Localidade.class.getName());

			if (colecaoLocalidade != null && !colecaoLocalidade.isEmpty()) {
				httpServletRequest.setAttribute(
						"idLocalidadeFinalNaoEncontrada", "valor");

				inserirComandoNegativacaoActionForm.setIdLocalidadeFinal(""
						+ ((Localidade) ((List) colecaoLocalidade).get(0))
								.getId());
				inserirComandoNegativacaoActionForm
						.setLocalidadeDescricaoFinal(""
								+ ((Localidade) ((List) colecaoLocalidade)
										.get(0)).getDescricao());
			} else {
				httpServletRequest.setAttribute(
						"idLocalidadeFinalNaoEncontrada", "exception");
				inserirComandoNegativacaoActionForm.setIdLocalidadeFinal(null);
				inserirComandoNegativacaoActionForm
						.setLocalidadeDescricaoFinal("Localidade Inexistente");
			}
		}

		// Pesquisar Setor Comercial Inicial
		String codigoSetorComercialInicial = inserirComandoNegativacaoActionForm
				.getCodigoSetorComercialInicial();
		verificaExistenciaCodSetorComercialInicial(idLocalidadeInicial,
				codigoSetorComercialInicial,
				inserirComandoNegativacaoActionForm, httpServletRequest,
				fachada, sessao);

		// Pesquisar Setor Comercial Final
		String codigoSetorComercialFinal = inserirComandoNegativacaoActionForm
				.getCodigoSetorComercialFinal();
		verificaExistenciaCodSetorComercialFinal(idLocalidadeFinal,
				codigoSetorComercialFinal, inserirComandoNegativacaoActionForm,
				httpServletRequest, fachada, sessao);

		return retorno;
	}

	private void verificaExistenciaCodSetorComercialInicial(
			String idLocalidadeInicial, String codigoSetorComercialInicial,
			InserirComandoNegativacaoActionForm form,
			HttpServletRequest httpServletRequest, Fachada fachada,
			HttpSession sessao) {

		// Verifica se o c�digo do Setor Comercial foi digitado
		if ((codigoSetorComercialInicial != null && !codigoSetorComercialInicial
				.toString().trim().equalsIgnoreCase(""))
				&& (idLocalidadeInicial != null && !idLocalidadeInicial
						.toString().trim().equalsIgnoreCase(""))) {

			FiltroSetorComercial filtroSetorComercial = new FiltroSetorComercial();
			if (idLocalidadeInicial != null
					&& !idLocalidadeInicial.toString().trim()
							.equalsIgnoreCase("")) {

				filtroSetorComercial.adicionarParametro(new ParametroSimples(
						FiltroSetorComercial.ID_LOCALIDADE, new Integer(
								idLocalidadeInicial)));
			}

			filtroSetorComercial.adicionarParametro(new ParametroSimples(
					FiltroSetorComercial.CODIGO_SETOR_COMERCIAL, new Integer(
							codigoSetorComercialInicial)));

			Collection<SetorComercial> setorComerciais = fachada.pesquisar(
					filtroSetorComercial, SetorComercial.class.getName());

			if (setorComerciais != null && !setorComerciais.isEmpty()) {
				// o setor comercial foi encontrado
				SetorComercial setorComercialEncontrado = (SetorComercial) Util
						.retonarObjetoDeColecao(setorComerciais);
				form.setCodigoSetorComercialInicial(""
						+ (setorComercialEncontrado.getCodigo()));
				form.setSetorComercialDescricaoInicial(setorComercialEncontrado
						.getDescricao());
				httpServletRequest.setAttribute(
						"idSetorComercialNaoEncontrada", "true");

			} else {
				// o setor comercial n�o foi encontrado
				form.setCodigoSetorComercialInicial("");
				httpServletRequest.setAttribute(
						"idSetorComercialNaoEncontrada", "exception");
				form.setSetorComercialDescricaoInicial("SETOR COMERCIAL INEXISTENTE");
			}
		}
	}

	private void verificaExistenciaCodSetorComercialFinal(
			String idLocalidadeFinal, String codigoSetorComercialFinal,
			InserirComandoNegativacaoActionForm form,
			HttpServletRequest httpServletRequest, Fachada fachada,
			HttpSession sessao) {

		// Verifica se o c�digo do Setor Comercial foi digitado
		if ((codigoSetorComercialFinal != null && !codigoSetorComercialFinal
				.toString().trim().equalsIgnoreCase(""))
				&& (idLocalidadeFinal != null && !idLocalidadeFinal.toString()
						.trim().equalsIgnoreCase(""))) {

			FiltroSetorComercial filtroSetorComercial = new FiltroSetorComercial();
			if (idLocalidadeFinal != null
					&& !idLocalidadeFinal.toString().trim()
							.equalsIgnoreCase("")) {

				filtroSetorComercial.adicionarParametro(new ParametroSimples(
						FiltroSetorComercial.ID_LOCALIDADE, new Integer(
								idLocalidadeFinal)));
			}

			filtroSetorComercial.adicionarParametro(new ParametroSimples(
					FiltroSetorComercial.CODIGO_SETOR_COMERCIAL, new Integer(
							codigoSetorComercialFinal)));

			Collection<SetorComercial> setorComerciais = fachada.pesquisar(
					filtroSetorComercial, SetorComercial.class.getName());

			if (setorComerciais != null && !setorComerciais.isEmpty()) {
				// o setor comercial foi encontrado
				SetorComercial setorComercialEncontrado = (SetorComercial) Util
						.retonarObjetoDeColecao(setorComerciais);
				form.setCodigoSetorComercialFinal(""
						+ (setorComercialEncontrado.getCodigo()));
				form.setSetorComercialDescricaoFinal(setorComercialEncontrado
						.getDescricao());
				httpServletRequest.setAttribute(
						"idSetorComercialNaoEncontrada", "true");

			} else {
				// o setor comercial n�o foi encontrado
				form.setCodigoSetorComercialFinal("");
				httpServletRequest.setAttribute(
						"idSetorComercialNaoEncontrada", "exception");
				form.setSetorComercialDescricaoFinal("SETOR COMERCIAL INEXISTENTE");
			}
		}
	}

}
