package br.com.zupacademy.desafioproposta.financeiro;

public class SolicitacaoResponse {

    private String documento;
    private String nome;
    private StatusSolicitacao resultadoSolicitacao;
    private String idProposta;

    public SolicitacaoResponse() {
    }

    public SolicitacaoResponse(String documento, String nome, StatusSolicitacao resultadoSolicitacao, String idProposta) {
        this.documento = documento;
        this.nome = nome;
        this.resultadoSolicitacao = resultadoSolicitacao;
        this.idProposta = idProposta;
    }

    public void setResultadoSolicitacao(StatusSolicitacao resultadoSolicitacao) {
        this.resultadoSolicitacao = resultadoSolicitacao;
    }

    public StatusSolicitacao getResultadoSolicitacao() {
        return resultadoSolicitacao;
    }
}
