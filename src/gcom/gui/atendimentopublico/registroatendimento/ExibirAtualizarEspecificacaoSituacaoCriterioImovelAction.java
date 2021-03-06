package gcom.gui.atendimentopublico.registroatendimento;

import gcom.atendimentopublico.ligacaoagua.FiltroDiametroLigacao;
import gcom.atendimentopublico.ligacaoagua.FiltroLigacaoAguaSituacao;
import gcom.atendimentopublico.ligacaoagua.FiltroMaterialLigacao;
import gcom.atendimentopublico.ligacaoagua.LigacaoAguaSituacao;
import gcom.atendimentopublico.ligacaoesgoto.FiltroLigacaoEsgotoSituacao;
import gcom.atendimentopublico.ligacaoesgoto.LigacaoEsgotoSituacao;
import gcom.atendimentopublico.registroatendimento.EspecificacaoImovSitCriterio;

import gcom.gui.ActionServletException;
import gcom.gui.GcomAction;
import gcom.util.ConstantesSistema;
import gcom.util.filtro.ParametroSimples;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * [UC0404] - Manter Especifica��o da Situa��o do Im�vel
 * 
 * [SB0001] - Atualizar Especifica��o da situa��o Criterio Imovel
 *
 * @author Rafael Pinto
 * 
 * @date 09/11/2006
 */

public class ExibirAtualizarEspecificacaoSituacaoCriterioImovelAction extends GcomAction {
	
	public ActionForward execute(ActionMapping actionMapping,
			ActionForm actionForm, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		ActionForward retorno = actionMapping.findForward("atualizarEspecificacaoSituacaoCriterioImovel");

		AtualizarEspecificacaoSituacaoImovelActionForm form = 
			(AtualizarEspecificacaoSituacaoImovelActionForm) actionForm;
		
		HttpSession sessao = this.getSessao(httpServletRequest); 
		
		String idAtualizar = null;
		String tipoOperacao = httpServletRequest.getParameter("tipoOperacao");
		
		if(tipoOperacao != null && !tipoOperacao.equals("")){
			
			idAtualizar = form.getIdAtualizar();

			int index = 0;
			
			for (Iterator iter = form.getEspecificacaoImovelSituacaoCriterio().iterator(); iter.hasNext();) {
				
				index++;
				
				if (idAtualizar.equals(""+index)) {
					
					EspecificacaoImovSitCriterio novaEspecificacaoImovSitCriterio = new EspecificacaoImovSitCriterio();
					
					if(form.getIndicadorHidrometroLigacaoAgua() != null && 
						!form.getIndicadorHidrometroLigacaoAgua().equals(""+ConstantesSistema.NUMERO_NAO_INFORMADO)){
						
						novaEspecificacaoImovSitCriterio.setIndicadorHidrometroLigacaoAgua(
							new Short(form.getIndicadorHidrometroLigacaoAgua()));	
					}
					
					if(form.getIndicadorHidrometroPoco() != null &&
						!form.getIndicadorHidrometroPoco().equals(""+ConstantesSistema.NUMERO_NAO_INFORMADO)){
					
						novaEspecificacaoImovSitCriterio.setIndicadorHidrometroPoco(
							new Short(form.getIndicadorHidrometroPoco()));
					}		
					
					

					if(form.getSituacaoLigacaoAgua() != null &&
						!form.getSituacaoLigacaoAgua().equals(""+ConstantesSistema.NUMERO_NAO_INFORMADO)){
						
						Integer idSituacaoLigacaoAgua = new Integer(form.getSituacaoLigacaoAgua());
						
						LigacaoAguaSituacao ligacaoAguaSituacao = this.retornaLigacaoAguaSituacao(sessao,idSituacaoLigacaoAgua);
						
						novaEspecificacaoImovSitCriterio.setLigacaoAguaSituacao(ligacaoAguaSituacao);
					}		
					
					if(form.getSituacaoLigacaoEsgoto() != null && 
						!form.getSituacaoLigacaoEsgoto().equals(""+ConstantesSistema.NUMERO_NAO_INFORMADO)){

						Integer idSituacaoLigacaoEsgoto = new Integer(form.getSituacaoLigacaoEsgoto());
						
						LigacaoEsgotoSituacao ligacaoEsgotoSituacao = 
							this.retornaLigacaoEsgotoSituacao(sessao,idSituacaoLigacaoEsgoto);

						novaEspecificacaoImovSitCriterio.setLigacaoEsgotoSituacao(ligacaoEsgotoSituacao);
					}		

					// Faz as valida��es de inser��o de especificao situacao imovel
					this.getFachada().validarExibirInsercaoEspecificacaoImovSitCriterio(
						form.getEspecificacaoImovelSituacaoCriterio(),novaEspecificacaoImovSitCriterio);

					EspecificacaoImovSitCriterio especificacaoImovSitCriterio = (EspecificacaoImovSitCriterio) iter.next();
					
					especificacaoImovSitCriterio.setIndicadorHidrometroLigacaoAgua(
						novaEspecificacaoImovSitCriterio.getIndicadorHidrometroLigacaoAgua());
					
					especificacaoImovSitCriterio.setIndicadorHidrometroPoco(
						novaEspecificacaoImovSitCriterio.getIndicadorHidrometroPoco());
					
					especificacaoImovSitCriterio.setLigacaoAguaSituacao(
						novaEspecificacaoImovSitCriterio.getLigacaoAguaSituacao());							
					
					especificacaoImovSitCriterio.setLigacaoEsgotoSituacao(
						novaEspecificacaoImovSitCriterio.getLigacaoEsgotoSituacao());
					
					especificacaoImovSitCriterio.setUltimaAlteracao(new Date());					
					
					httpServletRequest.setAttribute("fechaPopup", "true");
				}//fim do if index
			}
			
		}else{
		
			idAtualizar = httpServletRequest.getParameter("idAtualizar");
			
			EspecificacaoImovSitCriterio especificacaoImovSitCriterio = null;
			
			int index = 0;
			
			for (Iterator iter = form.getEspecificacaoImovelSituacaoCriterio().iterator(); iter.hasNext();) {
				
				index++;
				
				EspecificacaoImovSitCriterio element = (EspecificacaoImovSitCriterio) iter.next();
				
				if (idAtualizar.equals(""+index)) {
					especificacaoImovSitCriterio = element;
					break;
				}
			}
			
			form.setIdAtualizar(idAtualizar);
			form.setIdEspecificacaoCriterio(""+especificacaoImovSitCriterio.getId());
			
			if(especificacaoImovSitCriterio.getIndicadorHidrometroLigacaoAgua() != null){
				form.setIndicadorHidrometroLigacaoAgua(
					""+especificacaoImovSitCriterio.getIndicadorHidrometroLigacaoAgua());	
			}
			
			if(especificacaoImovSitCriterio.getIndicadorHidrometroPoco() != null){
				form.setIndicadorHidrometroPoco(
					""+especificacaoImovSitCriterio.getIndicadorHidrometroPoco());		
			}		
			
			if(especificacaoImovSitCriterio.getLigacaoAguaSituacao() != null){
				form.setSituacaoLigacaoAgua(""+especificacaoImovSitCriterio.getLigacaoAguaSituacao().getId());			
			}		
			
			if(especificacaoImovSitCriterio.getLigacaoEsgotoSituacao() != null){
				form.setSituacaoLigacaoEsgoto(""+especificacaoImovSitCriterio.getLigacaoEsgotoSituacao().getId());
			}		
			
			
			this.consultaSelectObrigatorio(sessao);
		}		
		return retorno;
	}

	
	private void consultaSelectObrigatorio(HttpSession sessao){
		
		// Filtro para o campo Situa��o Liga��o �gua
		Collection colecaoSituacaoLigacaoAgua = (Collection) 
			sessao.getAttribute("colecaoSituacaoLigacaoAgua");

		if(colecaoSituacaoLigacaoAgua == null){

			FiltroLigacaoAguaSituacao filtroLigacaoAguaSituacao = new FiltroLigacaoAguaSituacao();

			filtroLigacaoAguaSituacao.adicionarParametro(new ParametroSimples(
					FiltroLigacaoAguaSituacao.INDICADOR_USO,ConstantesSistema.INDICADOR_USO_ATIVO));

			filtroLigacaoAguaSituacao.setCampoOrderBy(FiltroDiametroLigacao.DESCRICAO);

			colecaoSituacaoLigacaoAgua = 
				this.getFachada().pesquisar(filtroLigacaoAguaSituacao, LigacaoAguaSituacao.class.getName());

			if (colecaoSituacaoLigacaoAgua != null && !colecaoSituacaoLigacaoAgua.isEmpty()) {
				sessao.setAttribute("colecaoSituacaoLigacaoAgua",colecaoSituacaoLigacaoAgua);
			} else {
				throw new ActionServletException("atencao.naocadastrado",null, "Situa��o Liga��o de Agua");
			}
		}	

		// Filtro para o campo Situacao da Liga��o Esgoto
		Collection colecaoSituacaoLigacaoEsgoto = (Collection) 
			sessao.getAttribute("colecaoSituacaoLigacaoEsgoto");

		if(colecaoSituacaoLigacaoEsgoto == null){

			FiltroLigacaoEsgotoSituacao filtroLigacaoEsgotoSituacao = new FiltroLigacaoEsgotoSituacao();
			
			filtroLigacaoEsgotoSituacao.adicionarParametro(new ParametroSimples(
					FiltroLigacaoEsgotoSituacao.INDICADOR_USO,ConstantesSistema.INDICADOR_USO_ATIVO));

			filtroLigacaoEsgotoSituacao.setCampoOrderBy(FiltroMaterialLigacao.DESCRICAO);

			colecaoSituacaoLigacaoEsgoto = 
				this.getFachada().pesquisar(filtroLigacaoEsgotoSituacao, LigacaoEsgotoSituacao.class.getName());

			if (colecaoSituacaoLigacaoEsgoto != null && !colecaoSituacaoLigacaoEsgoto.isEmpty()) {
				sessao.setAttribute("colecaoSituacaoLigacaoEsgoto",colecaoSituacaoLigacaoEsgoto);
			} else {
				throw new ActionServletException("atencao.naocadastrado",null, "Situa��o Liga��o de Esgoto");
			}
		}
	}	

	//retorna LigacaoAguaSituacao pelo id na colecao de ligacaoAguaSituacao	
	private LigacaoAguaSituacao retornaLigacaoAguaSituacao(HttpSession sessao,Integer id){

		LigacaoAguaSituacao retorno = null;

		Collection colecao = (Collection) sessao.getAttribute("colecaoSituacaoLigacaoAgua");
		
		if(colecao != null && !colecao.isEmpty()){
			Iterator itera = colecao.iterator();
			
			while (itera.hasNext()) {
				LigacaoAguaSituacao ligacaoAguaSituacao = (LigacaoAguaSituacao) itera.next();
				
				if(ligacaoAguaSituacao.getId().intValue() == id.intValue()){
					retorno = ligacaoAguaSituacao;
					break;
				}
			}
		}

		return retorno;
	}

	//retorna LigacaoEsgotoSituacao pelo id na colecao de ligacaoEsgotoSituacao	
	private LigacaoEsgotoSituacao retornaLigacaoEsgotoSituacao(HttpSession sessao,Integer id){

		LigacaoEsgotoSituacao retorno = null;

		Collection colecao = (Collection) sessao.getAttribute("colecaoSituacaoLigacaoEsgoto");
		
		if(colecao != null && !colecao.isEmpty()){
			Iterator itera = colecao.iterator();
			
			while (itera.hasNext()) {
				LigacaoEsgotoSituacao ligacaoEsgotoSituacao = (LigacaoEsgotoSituacao) itera.next();
				
				if(ligacaoEsgotoSituacao.getId().intValue() == id.intValue()){
					retorno = ligacaoEsgotoSituacao;
					break;
				}
			}
		}

		return retorno;
	}	
}
