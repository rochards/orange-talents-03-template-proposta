package br.com.zupacademy.desafioproposta.contas;

import br.com.zupacademy.desafioproposta.cartao.bloqueio.Bloqueio;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

import static br.com.zupacademy.desafioproposta.cartao.StatusCartao.ATIVO;
import static br.com.zupacademy.desafioproposta.cartao.StatusCartao.BLOQUEADO;

@Component
public class BloqueioCartao {

    private final Transacao transacao;
    private final ServicoDeContas servicoDeContas;
    private final Logger logger = LoggerFactory.getLogger(BloqueioCartao.class);

    public BloqueioCartao(Transacao transacao, ServicoDeContas servicoDeContas) {
        this.transacao = transacao;
        this.servicoDeContas = servicoDeContas;
    }

    /**
     * Bloqueia cartões no sistema legado Serviço de Contas
     * */
    public void bloqueia() {
        var bloqueiosNaoAtualizadosNoLegado = transacao.busca(Bloqueio.class, "statusCartaoNoLegado", ATIVO,100);
        for (var bloqueio : bloqueiosNaoAtualizadosNoLegado) {
            try {
                servicoDeContas.bloqueiaCartao(bloqueio.getContasIdCartao(), Map.of("sistemaResponsavel",
                        "API propostas"));

                bloqueio.setStatusCartaoNoLegado(BLOQUEADO);
                transacao.atualizaEComita(bloqueio);
            } catch (FeignException.InternalServerError ex) {
                logger.warn(ex.getLocalizedMessage());
            } catch (FeignException ex) {
                logger.error(ex.getMessage());
            }
        }
    }
}
