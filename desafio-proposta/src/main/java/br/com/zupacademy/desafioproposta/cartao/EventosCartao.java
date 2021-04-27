package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.cartao.avisoviagem.AvisoViagem;
import br.com.zupacademy.desafioproposta.cartao.avisoviagem.AvisoViagemRequest;
import br.com.zupacademy.desafioproposta.cartao.bloqueio.Bloqueio;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import br.com.zupacademy.desafioproposta.contas.NovoCartaoRequest;
import br.com.zupacademy.desafioproposta.contas.ServicoDeContas;
import br.com.zupacademy.desafioproposta.contas.BuscaCartoesGerados;
import br.com.zupacademy.desafioproposta.contas.SolicitacaoNovoCartao;
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
import static br.com.zupacademy.desafioproposta.cartao.avisoviagem.StatusAvisoViagem.CRIADO;
import static br.com.zupacademy.desafioproposta.cartao.avisoviagem.StatusAvisoViagem.NAO_CRIADO;

@Component
@EnableAsync
@EnableScheduling
public class EventosCartao {

    private final Logger logger = LoggerFactory.getLogger(EventosCartao.class);
    private final Transacao transacao;
    private final ServicoDeContas servicoDeContas;
    private final PropostaRepository propostaRepository;
    private final BuscaCartoesGerados buscaCartoesGerados;
    private final SolicitacaoNovoCartao solicitacaoNovoCartao;

    public EventosCartao(Transacao transacao, ServicoDeContas servicoDeContas, PropostaRepository propostaRepository, BuscaCartoesGerados buscaCartoesGerados, SolicitacaoNovoCartao solicitacaoNovoCartao) {
        this.transacao = transacao;
        this.servicoDeContas = servicoDeContas;
        this.propostaRepository = propostaRepository;
        this.buscaCartoesGerados = buscaCartoesGerados;
        this.solicitacaoNovoCartao = solicitacaoNovoCartao;
    }

    @Async
    public void solicitaNovo(String documentoSoliciante, String nomeSoliciante, Integer idProposta) {
        solicitacaoNovoCartao.solicita(new NovoCartaoRequest(documentoSoliciante, nomeSoliciante, idProposta));
    }

    @Scheduled(fixedDelayString = "${cartao.fixed-delay}")
    public void buscaGerados() {
        buscaCartoesGerados.busca();
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

    /**
     * Notifica ao sistema legado sobre viagens
     * */
    @Scheduled(fixedDelayString = "${cartao.fixed-delay}")
    public void notificaViagens() {
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
