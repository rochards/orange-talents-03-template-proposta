package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.contas.NovoCartaoRequest;
import br.com.zupacademy.desafioproposta.contas.ServicoDeContas;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SolicitacaoNovoCartao {

    private final ServicoDeContas servicoDeContas;
    private final Logger logger = LoggerFactory.getLogger(SolicitacaoNovoCartao.class);

    public SolicitacaoNovoCartao(ServicoDeContas servicoDeContas) {
        this.servicoDeContas = servicoDeContas;
    }

    public void solicita(NovoCartaoRequest novoCartaoRequest) {
        try {
            servicoDeContas.solicitaNovoCartao(novoCartaoRequest);
        } catch (FeignException ex) {
            logger.error(ex.getMessage());
        }
    }
}
