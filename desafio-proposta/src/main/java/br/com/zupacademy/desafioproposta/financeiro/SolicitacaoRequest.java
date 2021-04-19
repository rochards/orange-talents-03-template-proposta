package br.com.zupacademy.desafioproposta.financeiro;

import java.util.Objects;

public class SolicitacaoRequest {

    private String documento;
    private String nome;
    private String idProposta;

    public SolicitacaoRequest(String documento, String nome, String idProposta) {
        this.documento = documento;
        this.nome = nome;
        this.idProposta = idProposta;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNome() {
        return nome;
    }

    public String getIdProposta() {
        return idProposta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolicitacaoRequest)) return false;
        SolicitacaoRequest that = (SolicitacaoRequest) o;
        return Objects.equals(documento, that.documento) && Objects.equals(nome, that.nome) && Objects.equals(idProposta, that.idProposta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento, nome, idProposta);
    }
}
