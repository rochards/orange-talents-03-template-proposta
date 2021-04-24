package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.cartao.StatusCartao;

import java.time.LocalDateTime;

public class CartaoResponse {

    private String idCartao;
    private LocalDateTime emitidoEm;
    private StatusCartao status;

    public CartaoResponse(Cartao cartao) {
        this.idCartao = cartao.getContasIdCartao();
        this.emitidoEm = cartao.getEmitidoEm();
        this.status = cartao.getStatus();
    }

    public String getIdCartao() {
        return idCartao;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }

    public StatusCartao getStatus() {
        return status;
    }
}
