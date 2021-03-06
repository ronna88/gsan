package gcom.gui.arrecadacao;

import gcom.gui.GcomAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Description of the Class
 * 
 * @author Fernanda Paiva
 */
public class CancelarConsultarDadosDiariosArrecadacaoAction extends GcomAction {

    /**
     * Description of the Method
     * 
     * @param actionMapping
     *            Description of the Parameter
     * @param actionForm
     *            Description of the Parameter
     * @param httpServletRequest
     *            Description of the Parameter
     * @param httpServletResponse
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionForward retorno = actionMapping.findForward("telaPrincipal");

        //obt�m a inst�ncia da sess�o
        HttpSession sessao = httpServletRequest.getSession(false);

        //limpa a sess�o
        sessao.removeAttribute("colecaoArrecadacaoDadosDiarios");
        sessao.removeAttribute("FiltrarDadosDiariosArrecadacaoActionForm");
        sessao.removeAttribute("tipoPesquisaRetorno");
        sessao.removeAttribute("statusWizard");
        sessao.removeAttribute("colecaoDadosDiarios");
		sessao.removeAttribute("referencia");
		sessao.removeAttribute("valorTotal");
		sessao.removeAttribute("valor");
		sessao.removeAttribute("map");
		sessao.removeAttribute("colecaoDocumentoTipo");
		sessao.removeAttribute("colecaoImoveisPerfil");
		sessao.removeAttribute("colecaoLigacaoAgua");
		sessao.removeAttribute("colecaoLigacaoEsgoto");
		sessao.removeAttribute("colecaoCategoria");
		sessao.removeAttribute("colecaoEsferaPoder");
			
        return retorno;
    }
}
