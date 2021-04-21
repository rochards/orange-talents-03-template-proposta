package br.com.zupacademy.desafioproposta.cartao.biometria;

import br.com.zupacademy.desafioproposta.cartao.Cartao;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Biometria {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String impressaoDigital;

    @Column(nullable = false)
    private LocalDateTime cadastradaEm;

    @ManyToOne(optional = false)
    private Cartao cartao;

    public Biometria(String impressaoDigital, Cartao cartao) {
        this.impressaoDigital = impressaoDigital;
        this.cadastradaEm = LocalDateTime.now();
        this.cartao = cartao;
    }

    public Integer getId() {
        return id;
    }
}
