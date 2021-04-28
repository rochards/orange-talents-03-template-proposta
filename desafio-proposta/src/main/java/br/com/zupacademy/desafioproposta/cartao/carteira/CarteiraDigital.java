package br.com.zupacademy.desafioproposta.cartao.carteira;

import br.com.zupacademy.desafioproposta.cartao.Cartao;

import javax.persistence.*;

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

    public Integer getId() {
        return id;
    }

    public NomeCarteira getNome() {
        return nome;
    }
}
