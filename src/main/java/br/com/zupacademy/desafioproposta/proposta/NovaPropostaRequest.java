package br.com.zupacademy.desafioproposta.proposta;

import java.math.BigDecimal;

public class NovaPropostaRequest {

    private String documento;
    private String email;
    private String nome;
    private String endereco;
    private BigDecimal salario;

    public NovaPropostaRequest(String documento, String email, String nome, String endereco, BigDecimal salario) {
        this.documento = documento;
        this.email = email;
        this.nome = nome;
        this.endereco = endereco;
        this.salario = salario;
    }

    public Proposta toModel() {
        return new Proposta(documento, email, nome, endereco, salario);
    }
}
