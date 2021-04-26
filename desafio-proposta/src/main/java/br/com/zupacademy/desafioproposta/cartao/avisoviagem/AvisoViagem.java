package br.com.zupacademy.desafioproposta.cartao.avisoviagem;

import br.com.zupacademy.desafioproposta.cartao.Cartao;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class AvisoViagem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String destino;

    @Column(nullable = false)
    private LocalDate emViagemAte;

    private String ipCliente;
    private String userAgent;

    @ManyToOne(optional = false)
    private Cartao cartao;

    @Column(nullable = false)
    private LocalDateTime registradoEm;

    /**
     * @Deprecated hibernate only
     * */
    @Deprecated
    public AvisoViagem() {
    }

    public AvisoViagem(String destino, LocalDate emViagemAte, String ipCliente, String userAgent, Cartao cartao) {
        this.destino = destino;
        this.emViagemAte = emViagemAte;
        this.ipCliente = ipCliente;
        this.userAgent = userAgent;
        this.cartao = cartao;
        this.registradoEm = LocalDateTime.now();
    }
}
