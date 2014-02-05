package gcom.cadastro.atualizacaocadastral.command;

import gcom.cadastro.ArquivoTextoAtualizacaoCadastral;

import java.util.ArrayList;
import java.util.List;

public class AtualizacaoCadastral {

	private ArquivoTextoAtualizacaoCadastral arquivoTexto = null;

	private boolean validacaoLiberada = false;
	
	private List<AtualizacaoCadastralImovel> imoveisComErro = new ArrayList<AtualizacaoCadastralImovel>();
	
	private AtualizacaoCadastralImovel imovelAtual = new AtualizacaoCadastralImovel();
	
	private int totalImoveis = 0;
	
	public ArquivoTextoAtualizacaoCadastral getArquivoTexto() {
		return arquivoTexto;
	}

	public void setArquivoTexto(ArquivoTextoAtualizacaoCadastral arquivoTexto) {
		this.arquivoTexto = arquivoTexto;
	}

	public void liberarValidacao() {
		validacaoLiberada = true;
	}
	
	public boolean validacaoLiberada(){
		return validacaoLiberada;
	}
	
	public AtualizacaoCadastralImovel novaAtualizacaoImovel(){
		validacaoLiberada = false;
		AtualizacaoCadastralImovel imovel = new AtualizacaoCadastralImovel(this);
		imovelAtual = imovel;
		imoveisComErro.add(imovel);
		totalImoveis++;
		return imovel;
	}
	
	public AtualizacaoCadastralImovel getImovelAtual(){
		return imovelAtual;
	}
	
	public List<AtualizacaoCadastralImovel> getAtualizacoesImovel(){
		return imoveisComErro;
	}

	public void excluirImovelSemErros() {
		imoveisComErro.remove(imovelAtual);
	}
	
	public boolean existeErroNoArquivo() {
		return imoveisComErro.size() > 0;
	}

	public int getTotalImoveisComErro() {
		return imoveisComErro.size();
	}

	public int getTotalImoveis() {
		return totalImoveis;
	}

	public void setTotalImoveis(int totalImoveis) {
		this.totalImoveis = totalImoveis;
	}
}