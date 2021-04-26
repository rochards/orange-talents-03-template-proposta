package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.cartao.bloqueio.Bloqueio;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import br.com.zupacademy.desafioproposta.contas.NovoCartaoRequest;
import br.com.zupacademy.desafioproposta.contas.ServicoDeContas;
import br.com.zupacademy.desafioproposta.proposta.Proposta;
import br.com.zupacademy.desafioproposta.proposta.PropostaRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

import static br.com.zupacademy.desafioproposta.cartao.StatusCartao.ATIVO;
import static br.com.zupacademy.desafioproposta.cartao.StatusCartao.BLOQUEADO;
import static br.com.zupacademy.desafioproposta.proposta.StatusProposta.ELEGIVEL;

@Component
@EnableAsync
@EnableScheduling
public class EventosCartao {

    private final Logger logger = LoggerFactory.getLogger(EventosCartao.class);
    private final Transacao transacao;
    private final ServicoDeContas servicoDeContas;
    private final PropostaRepository propostaRepository;

    public EventosCartao(Transacao transacao, ServicoDeContas servicoDeContas, PropostaRepository propostaRepository) {
        this.transacao = transacao;
        this.servicoDeContas = servicoDeContas;
        this.propostaRepository = propostaRepository;
    }

    @Async
    public void solicitaNovo(String documentoSoliciante, String nomeSoliciante, Integer idProposta) {
        var novoCartaoRequest = new NovoCartaoRequest(documentoSoliciante, nomeSoliciante, idProposta);
        try {
            servicoDeContas.solicitaNovoCartao(novoCartaoRequest);
        } catch (FeignException ex) {
            logger.error(ex.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${cartao.fixed-delay}")
    public void buscaGerados() {
        var propostasSemCartao = propostaRepository.findFirst100ByStatusAndCartoesEmpty(ELEGIVEL);
        for (var proposta: propostasSemCartao) {
            try {
                var cartaoGerado = servicoDeContas.consultaCartaoGerado(proposta.getId());

                if (ehTitularDoCartao(proposta, cartaoGerado.getTitular())) {
                    var novoCartao = new Cartao(cartaoGerado.getId(), cartaoGerado.getEmitidoEm(), proposta);
                    transacao.salvaEComita(novoCartao);
                }
            } catch (FeignException.InternalServerError ex) {
                logger.warn(ex.getLocalizedMessage() + " ainda não foi encontrado cartão para essa proposta");
            } catch (FeignException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    /**
     * Bloqueia cartões no sistema legado Serviço de Contas
     * */
    @Scheduled(fixedDelayString = "${cartao.fixed-delay}")
    public void bloqueiaCartoes() {
        var bloqueiosNaoAtualizadosNoLegado = transacao.busca(Bloqueio.class, "statusCartaoNoLegado", ATIVO,
                100);
        for (var bloqueio : bloqueiosNaoAtualizadosNoLegado) {
            try {
                servicoDeContas.bloqueiaCartao(bloqueio.getCartao().getContasIdCartao(), Map.of("sistemaResponsavel",
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

    private boolean ehTitularDoCartao(Proposta proposta, String titular) {
        var ehTitular = false;

        if (proposta.getNome().equals(titular)) {
            ehTitular = true;
        } else {
            logger.error("sistema de cartões gerou cartão para o titular errado!");
        }

        return ehTitular;
    }
}
