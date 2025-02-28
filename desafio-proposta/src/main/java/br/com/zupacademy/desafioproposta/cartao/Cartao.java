package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.cartao.bloqueio.Bloqueio;
import br.com.zupacademy.desafioproposta.cartao.carteira.CarteiraDigital;
import br.com.zupacademy.desafioproposta.cartao.carteira.NomeCarteira;
import br.com.zupacademy.desafioproposta.proposta.Proposta;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Cartao {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String contasIdCartao;

    @Column(nullable = false)
    private LocalDateTime emitidoEm;

    @ManyToOne(optional = false)
    private Proposta proposta;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusCartao status;

    @OneToOne(cascade = CascadeType.MERGE, mappedBy = "cartao")
    private Bloqueio bloqueio;

    @OneToMany(mappedBy = "cartao")
    private List<CarteiraDigital> carteirasDigitais;

    /**
     * @Deprecated hibernate only
     * */
    @Deprecated
    public Cartao() {
    }

    public Cartao(String contasIdCartao, LocalDateTime emitidoEm, Proposta proposta) {
        this.contasIdCartao = contasIdCartao;
        this.emitidoEm = emitidoEm;
        this.proposta = proposta;
        this.status = StatusCartao.ATIVO;
        this.carteirasDigitais = new ArrayList<>();
    }

    public void setBloqueio(Bloqueio bloqueio) {
        this.bloqueio = bloqueio;
    }

    public void setStatus(StatusCartao status) {
        this.status = status;
    }

    public String getContasIdCartao() {
        return contasIdCartao;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }

    public Bloqueio getBloqueio() {
        return bloqueio;
    }

    public StatusCartao getStatus() {
        return status;
    }

    public List<CarteiraDigital> getCarteirasDigitais() {
        return carteirasDigitais;
    }

    public boolean associadoACarteira(NomeCarteira nomeCarteira) {
        return carteirasDigitais.stream()
                .anyMatch(carteiraDigital -> carteiraDigital.getNome() == nomeCarteira);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cartao)) return false;
        Cartao cartao = (Cartao) o;
        return contasIdCartao.equals(cartao.contasIdCartao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contasIdCartao);
    }
}
