package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.cartao.Cartao;

import java.math.BigDecimal;
import java.util.List;

public class ConsultaPropostaResponse {

    private String documento;
    private String email;
    private String nome;
    private String endereco;
    private BigDecimal salario;
    private StatusProposta status;
    private List<Cartao> cartao;

    public ConsultaPropostaResponse(Proposta proposta) {
        this.documento = proposta.getDocumento();
        this.email = proposta.getEmail();
        this.nome = proposta.getNome();
        this.endereco = proposta.getEndereco();
        this.salario = proposta.getSalario();
        this.status = proposta.getStatus();
        this.cartao = proposta.getCartao();
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

    public StatusProposta getStatus() {
        return status;
    }

    public List<Cartao> getCartao() {
        return cartao;
    }
}
