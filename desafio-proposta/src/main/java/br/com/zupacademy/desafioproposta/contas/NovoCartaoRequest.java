package br.com.zupacademy.desafioproposta.contas;

import java.util.Objects;

public class NovoCartaoRequest {

    private String documento;
    private String nome;
    private Integer idProposta;

    public NovoCartaoRequest(String documento, String nome, Integer idProposta) {
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

    public Integer getIdProposta() {
        return idProposta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NovoCartaoRequest)) return false;
        NovoCartaoRequest that = (NovoCartaoRequest) o;
        return Objects.equals(documento, that.documento) && Objects.equals(nome, that.nome) && Objects.equals(idProposta, that.idProposta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento, nome, idProposta);
    }
}
