package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.cartao.Cartao;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import static br.com.zupacademy.desafioproposta.proposta.StatusProposta.ELEGIVEL;
import static br.com.zupacademy.desafioproposta.proposta.StatusProposta.NAO_ELEGIVEL;

@Entity
public class Proposta {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String documento;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2) UNSIGNED")
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    private StatusProposta status;

    @OneToMany(mappedBy = "proposta")
    private List<Cartao> cartoes;

    /**
     * @Deprecated hibernate only
     * */
    @Deprecated
    public Proposta() {
    }

    /**
     * Cria um nova Proposta. Uma nova Proposta é criada por default com status NAO_ELEGIVE
     * @param documento documento do solicitante. Deve vir criptografado pela classe EncodeDocumento
     * @param email email do solicitante
     * @param nome nome do solicitante
     * @param endereco do solicitante
     * @param salario salário do solicitante
     * */
    public Proposta(String documento, String email, String nome, String endereco, BigDecimal salario) {
        this.documento = documento;
        this.email = email;
        this.nome = nome;
        this.endereco = endereco;
        this.salario = salario;
        this.status = NAO_ELEGIVEL;
    }

    public void atualizaStatus(boolean elegivel) {
        this.status = elegivel ? ELEGIVEL : NAO_ELEGIVEL;
    }

    public Integer getId() {
        return id;
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

    public List<Cartao> getCartoes() {
        return cartoes;
    }
}
