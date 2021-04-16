package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
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

import static br.com.zupacademy.desafioproposta.proposta.StatusProposta.ELEGIVEL;

@Component
@EnableAsync
@EnableScheduling
public class Cartao {

    private final Logger logger = LoggerFactory.getLogger(Cartao.class);
    private final Transacao transacao;
    private final ServicoDeContas servicoDeContas;
    private final PropostaRepository propostaRepository;

    public Cartao(Transacao transacao, ServicoDeContas servicoDeContas, PropostaRepository propostaRepository) {
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
        var propostasSemCartao = propostaRepository.findFirst100ByStatusAndIdCartaoNull(ELEGIVEL);

        for (var proposta : propostasSemCartao) {
            try {
                Map<String, Object> cartaoGerado = servicoDeContas.consultaCartaoGerado(proposta.getId());

                if (ehTitularDoCartao(proposta, (String) cartaoGerado.get("titular"))) {
                    proposta.setIdCartao((String) cartaoGerado.get("id"));
                    transacao.atualizaEComita(proposta);
                }
            } catch (FeignException.InternalServerError ex) {
                logger.warn(ex.getMessage());
                System.out.println(ex.getMessage());
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
            logger.error("Sistema de cartões gerou cartão para titular errado!");
        }

        return ehTitular;
    }
}
