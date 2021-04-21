package br.com.zupacademy.desafioproposta.cartao.biometria;

import br.com.zupacademy.desafioproposta.cartao.Cartao;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

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

    // equals e hashCode são necessários para os testes, pois o Mockito utiliza-os para comparar os objetos mockados
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Biometria)) return false;
        Biometria biometria = (Biometria) o;
        return impressaoDigital.equals(biometria.impressaoDigital);
    }

    @Override
    public int hashCode() {
        return Objects.hash(impressaoDigital);
    }
}
