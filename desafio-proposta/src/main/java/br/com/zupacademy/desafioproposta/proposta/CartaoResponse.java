package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.cartao.Cartao;

import java.time.LocalDateTime;

public class CartaoResponse {

    private String idCartao;
    private LocalDateTime emitidoEm;

    public CartaoResponse(Cartao cartao) {
        this.idCartao = cartao.getContasIdCartao();
        this.emitidoEm = cartao.getEmitidoEm();
    }

    public String getIdCartao() {
        return idCartao;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }
}
