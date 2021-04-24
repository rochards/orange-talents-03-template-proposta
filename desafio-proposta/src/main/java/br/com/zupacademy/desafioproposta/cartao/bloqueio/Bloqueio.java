package br.com.zupacademy.desafioproposta.cartao.bloqueio;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Bloqueio {

    @Id
    private Integer id;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime bloqueadoEm;

    @Column(nullable = false)
    private String ipCliente;

    @Column(nullable = false)
    private String userAgentCliente;

    @MapsId
    @OneToOne(optional = false)
    private Cartao cartao;

    /**
     * @Deprecated hibernate only
     * */
    @Deprecated
    public Bloqueio() {
    }

    public Bloqueio(String ipCliente, String userAgentCliente, Cartao cartao) {
        this.ipCliente = ipCliente;
        this.userAgentCliente = userAgentCliente;
        this.cartao = cartao;
    }

    public String getUserAgentCliente() {
        return userAgentCliente;
    }
}
