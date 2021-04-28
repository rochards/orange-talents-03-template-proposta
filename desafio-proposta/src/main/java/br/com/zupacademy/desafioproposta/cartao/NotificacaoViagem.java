package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.cartao.avisoviagem.AvisoViagem;
import br.com.zupacademy.desafioproposta.cartao.avisoviagem.AvisoViagemRequest;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import br.com.zupacademy.desafioproposta.contas.ServicoDeContas;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static br.com.zupacademy.desafioproposta.cartao.avisoviagem.StatusAvisoViagem.CRIADO;
import static br.com.zupacademy.desafioproposta.cartao.avisoviagem.StatusAvisoViagem.NAO_CRIADO;

@Component
public class NotificacaoViagem {

    private final Transacao transacao;
    private final ServicoDeContas servicoDeContas;
    private final Logger logger = LoggerFactory.getLogger(NotificacaoViagem.class);

    public NotificacaoViagem(Transacao transacao, ServicoDeContas servicoDeContas) {
        this.transacao = transacao;
        this.servicoDeContas = servicoDeContas;
    }

    /**
     * Notifica ao sistema legado sobre viagens
     * */
    public void notifica() {
        var avisosNaoAtualizados = transacao.busca(AvisoViagem.class, "statusAvisoNoLegado", NAO_CRIADO, 100);
        for (var avisoViagem : avisosNaoAtualizados) {
            try {
                var avisoRequest = new AvisoViagemRequest(avisoViagem.getDestino(), avisoViagem.getEmViagemAte());
                servicoDeContas.notificaViagem(avisoViagem.getContasIdCartao(), avisoRequest);

                avisoViagem.setStatusAvisoNoLegado(CRIADO);
                transacao.atualizaEComita(avisoViagem);
            } catch (FeignException ex) {
                logger.error(ex.getMessage());
            }
        }
    }
}
