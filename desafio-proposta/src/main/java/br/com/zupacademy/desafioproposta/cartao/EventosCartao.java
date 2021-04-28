package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.cartao.carteira.CarteiraDigital;
import br.com.zupacademy.desafioproposta.contas.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
@EnableScheduling
public class EventosCartao {

    private final BloqueioCartao bloqueioCartao;
    private final NotificacaoViagem notificacaoViagem;
    private final BuscaCartaoGerado buscaCartaoGerado;
    private final CriacaoBuscaCarteira criacaoBuscaCarteira;
    private final SolicitacaoNovoCartao solicitacaoNovoCartao;

    public EventosCartao(BloqueioCartao bloqueioCartao, NotificacaoViagem notificacaoViagem, BuscaCartaoGerado buscaCartaoGerado,
                         CriacaoBuscaCarteira criacaoBuscaCarteira, SolicitacaoNovoCartao solicitacaoNovoCartao) {
        this.bloqueioCartao = bloqueioCartao;
        this.notificacaoViagem = notificacaoViagem;
        this.buscaCartaoGerado = buscaCartaoGerado;
        this.criacaoBuscaCarteira = criacaoBuscaCarteira;
        this.solicitacaoNovoCartao = solicitacaoNovoCartao;
    }

    @Async
    public void solicitaNovo(String documentoSoliciante, String nomeSoliciante, Integer idProposta) {
        solicitacaoNovoCartao.solicita(new NovoCartaoRequest(documentoSoliciante, nomeSoliciante, idProposta));
    }

    @Async
    public void associaCarteira(String idCartao, CarteiraDigital carteira) {
        criacaoBuscaCarteira.associaCarteira(idCartao, carteira);
    }

    @Scheduled(fixedDelayString = "${cartao.fixed-delay}")
    public void buscaGerados() {
        buscaCartaoGerado.busca();
    }

    @Scheduled(fixedDelayString = "${cartao.fixed-delay}")
    public void bloqueiaCartoes() {
        bloqueioCartao.bloqueia();
    }

    @Scheduled(fixedDelayString = "${cartao.fixed-delay}")
    public void notificaViagens() {
        notificacaoViagem.notifica();
    }

    @Scheduled(fixedDelayString = "${cartao.fixed-delay}")
    public void associaCarteiras() {
        criacaoBuscaCarteira.associaCarteiras();
    }
}
