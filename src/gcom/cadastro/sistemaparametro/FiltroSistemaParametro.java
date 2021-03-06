package gcom.cadastro.sistemaparametro;

import gcom.util.filtro.Filtro;

/**
 * < <Descri��o da Classe>>
 * 
 * @author Administrador
 */
public class FiltroSistemaParametro extends Filtro {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Construtor da classe FiltroCategoria
	 */
	public FiltroSistemaParametro() {
	}

	/**
	 * Description of the Field
	 */
	public final static String Parm_Id = "parmId";

	public final static String ANO_MES_REFERECIA_ARRECADACAO = "anoMesFaturamento";

	/**
	 * Construtor da classe FiltroCategoria
	 * 
	 * @param campoOrderBy
	 *            Descri��o do par�metro
	 */
	public FiltroSistemaParametro(String campoOrderBy) {
		this.campoOrderBy = campoOrderBy;
	}

}
