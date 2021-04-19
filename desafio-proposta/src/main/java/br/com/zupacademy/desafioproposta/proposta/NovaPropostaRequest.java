package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.compartilhado.validators.CpfOrCnpj;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class NovaPropostaRequest {

    @NotBlank @Pattern(regexp = "([0-9]{11})|([0-9]{14})", message = "CPF/CNPJ não deve conter pontuação")
    @CpfOrCnpj
    private String documento;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String nome;

    @NotBlank
    private String endereco;

    @NotNull @Positive
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

    public String getDocumento() {
        return documento;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public BigDecimal getSalario() {
        return salario;
    }
}
