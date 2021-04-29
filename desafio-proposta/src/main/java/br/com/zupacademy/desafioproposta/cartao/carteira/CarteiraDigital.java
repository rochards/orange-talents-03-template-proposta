package br.com.zupacademy.desafioproposta.cartao.carteira;

import br.com.zupacademy.desafioproposta.cartao.Cartao;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class CarteiraDigital {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusCarteira status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NomeCarteira nome;

    @ManyToOne(optional = false)
    private Cartao cartao;

    private String contasIdCarteira;

    /**
     * @Deprecated hibernate only
     * */
    @Deprecated
    public CarteiraDigital() {
    }

    public CarteiraDigital(String email, NomeCarteira nome, Cartao cartao) {
        this.email = email;
        this.status = StatusCarteira.NAO_ASSOCIADA;
        this.nome = nome;
        this.cartao = cartao;
    }

    public void setStatus(StatusCarteira status) {
        this.status = status;
    }

    public void setContasIdCarteira(String contasIdCarteira) {
        this.contasIdCarteira = contasIdCarteira;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public StatusCarteira getStatus() {
        return status;
    }

    public NomeCarteira getNome() {
        return nome;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public String getContasIdCartao() {
        return cartao.getContasIdCartao();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarteiraDigital)) return false;
        CarteiraDigital that = (CarteiraDigital) o;
        return email.equals(that.email) && nome == that.nome;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, nome);
    }
}
