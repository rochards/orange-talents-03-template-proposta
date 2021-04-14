package br.com.zupacademy.desafioproposta.proposta.financeiro;

public class SolicitacaoResponse {

    private String documento;
    private String nome;
    private StatusSolicitacao resultadoSolicitacao;
    private String idProposta;

    public SolicitacaoResponse(String documento, String nome, StatusSolicitacao resultadoSolicitacao, String idProposta) {
        this.documento = documento;
        this.nome = nome;
        this.resultadoSolicitacao = resultadoSolicitacao;
        this.idProposta = idProposta;
    }

    public StatusSolicitacao getResultadoSolicitacao() {
        return resultadoSolicitacao;
    }
}
