package gcom.batch.cobranca.cobrancaporresultado;

import gcom.cobranca.ComandoEmpresaCobrancaConta;
import gcom.seguranca.acesso.usuario.Usuario;
import gcom.tarefa.TarefaBatch;
import gcom.tarefa.TarefaException;
import gcom.util.ConstantesJNDI;
import gcom.util.agendadortarefas.AgendadorTarefas;

import java.util.Collection;
import java.util.List;

/**
 * [UC1168] Encerrar Comandos de Cobran�a por Empresa
 * Tarefa que manda para batch Encerrar Comandos de Cobran�a por Empresa
 * 
 * @author Mariana Victor
 * @created 09/05/2011
 */
public class TarefaBatchEncerrarComandosDeCobrancaPorEmpresa extends TarefaBatch {
	
	private static final long serialVersionUID = 1L;

	public TarefaBatchEncerrarComandosDeCobrancaPorEmpresa(Usuario usuario,
			int idFuncionalidadeIniciada) {

		super(usuario, idFuncionalidadeIniciada);
	}

	@Deprecated
	public TarefaBatchEncerrarComandosDeCobrancaPorEmpresa() {
		super(null, 0);
	}

	@SuppressWarnings("unchecked")
	public Object executar() throws TarefaException {

		List<ComandoEmpresaCobrancaConta> comandos = (List<ComandoEmpresaCobrancaConta>) getParametro("comandos");
		Usuario usuario = (Usuario) getParametro("usuario");
		Integer idCobrancaSituacao = (Integer) getParametro("idCobrancaSituacao");
		
		for (ComandoEmpresaCobrancaConta comando : comandos) {
			
			enviarMensagemControladorBatch(ConstantesJNDI.BATCH_ENCERRAR_COMANDO_DE_COBRANCA_POR_EMPRESA,
					new Object[] {
					this.getIdFuncionalidadeIniciada(),
					usuario,
					comando.getId(),
					idCobrancaSituacao});
		}

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection pesquisarTodasUnidadeProcessamentoBatch() {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection pesquisarTodasUnidadeProcessamentoReinicioBatch() {

		return null;
	}

	@Override
	public void agendarTarefaBatch() {
		AgendadorTarefas.agendarTarefa("EncerrarComandosDeCobrancaPorEmpresaBatch", this);
	}

}
