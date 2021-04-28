package br.com.zupacademy.desafioproposta.cartao.bloqueio;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.cartao.StatusCartao;
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusCartao statusCartaoNoLegado;

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
        this.statusCartaoNoLegado = StatusCartao.ATIVO;
    }

    public void setStatusCartaoNoLegado(StatusCartao statusCartaoNoLegado) {
        this.statusCartaoNoLegado = statusCartaoNoLegado;
    }

    public String getUserAgentCliente() {
        return userAgentCliente;
    }

    public String getContasIdCartao() {
        return cartao.getContasIdCartao();
    }

    public StatusCartao getStatusCartaoNoLegado() {
        return statusCartaoNoLegado;
    }
}
