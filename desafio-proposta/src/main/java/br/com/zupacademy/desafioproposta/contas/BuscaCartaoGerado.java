package br.com.zupacademy.desafioproposta.contas;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import br.com.zupacademy.desafioproposta.proposta.Proposta;
import br.com.zupacademy.desafioproposta.proposta.PropostaRepository;
import br.com.zupacademy.desafioproposta.proposta.StatusProposta;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BuscaCartaoGerado {

    private final Transacao transacao;
    private final ServicoDeContas servicoDeContas;
    private final PropostaRepository propostaRepository;
    private final Logger logger = LoggerFactory.getLogger(BuscaCartaoGerado.class);

    public BuscaCartaoGerado(Transacao transacao, ServicoDeContas servicoDeContas, PropostaRepository propostaRepository) {
        this.transacao = transacao;
        this.servicoDeContas = servicoDeContas;
        this.propostaRepository = propostaRepository;
    }

    public void busca() {
        var propostasSemCartoes = propostaRepository.findFirst100ByStatusAndCartoesEmpty(StatusProposta.ELEGIVEL);
        for (var proposta : propostasSemCartoes) {
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
