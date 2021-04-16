package br.com.zupacademy.desafioproposta.cartao;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class Cartao {

    private final Logger logger = LoggerFactory.getLogger(Cartao.class);
    private final ServicoDeContas servicoDeContas;

    public Cartao(ServicoDeContas servicoDeContas) {
        this.servicoDeContas = servicoDeContas;
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
}
