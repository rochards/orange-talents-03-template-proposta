package br.com.zupacademy.desafioproposta.cartao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.net.URL;

@Component
public class ContasHealthIndicator implements HealthIndicator {

    private final Logger logger = LoggerFactory.getLogger(ContasHealthIndicator.class);

    @Value("${contas.url}")
    private String contasURL;

    @Override
    public Health health() {
        /* Essa forma tbm é válida. Verifique como foi feito na classe FinanceiroHealthIndicator*/
        try (Socket socket = new Socket(new URL(contasURL).getHost(), 8888)) {
        } catch (Exception ex) {
            logger.error(ex.getMessage() + " to " + contasURL);
            return Health.down().build();
        }
        return Health.up().build();
    }
}
