package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.cartao.carteira.CarteiraDigital;
import br.com.zupacademy.desafioproposta.cartao.carteira.StatusCarteira;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import br.com.zupacademy.desafioproposta.contas.CarteiraResponse;
import br.com.zupacademy.desafioproposta.contas.NovaCarteiraRequest;
import br.com.zupacademy.desafioproposta.contas.ResultadoCarteira;
import br.com.zupacademy.desafioproposta.contas.ServicoDeContas;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CriacaoBuscaCarteira {

    private final Transacao transacao;
    private final ServicoDeContas servicoDeContas;
    private final Logger logger = LoggerFactory.getLogger(CriacaoBuscaCarteira.class);

    public CriacaoBuscaCarteira(Transacao transacao, ServicoDeContas servicoDeContas) {
        this.transacao = transacao;
        this.servicoDeContas = servicoDeContas;
    }

    public void associaCarteira(String idCartao, NovaCarteiraRequest novaCarteiraRequest, CarteiraDigital carteira) {
        try {
            var carteiraResponse = servicoDeContas.associaCartaoACarteira(idCartao, novaCarteiraRequest);
            atualizaCarteira(carteiraResponse, carteira);
        } catch (FeignException ex) {
            logger.error(ex.getMessage());
        }
    }

    public void associaCarteiras() {
        var carteirasNaoAssociadas = transacao.busca(CarteiraDigital.class, "status", StatusCarteira.NAO_ASSOCIADA,
                100);
        for (var carteira : carteirasNaoAssociadas) {
            var novaCarteiraRequest = new NovaCarteiraRequest(carteira.getEmail(), carteira.getNome().name());
            try {
                var carteiraResponse = servicoDeContas.associaCartaoACarteira(carteira.getContasIdCartao(),
                        novaCarteiraRequest);
                atualizaCarteira(carteiraResponse, carteira);
            } catch (FeignException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    private void atualizaCarteira(CarteiraResponse carteiraResponse, CarteiraDigital carteira) {
        if (carteiraResponse.getResultado() == ResultadoCarteira.ASSOCIADA) {
            carteira.setStatus(StatusCarteira.ASSOCIADA);
            carteira.setContasIdCarteira(carteiraResponse.getId());

            transacao.atualizaEComita(carteira);
        }
    }
}
