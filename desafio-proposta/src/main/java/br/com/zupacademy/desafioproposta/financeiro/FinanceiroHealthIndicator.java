package br.com.zupacademy.desafioproposta.financeiro;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class FinanceiroHealthIndicator implements HealthIndicator {

    private final Logger logger = LoggerFactory.getLogger(FinanceiroHealthIndicator.class);
    private final ConsultaFinanceiro consultaFinanceiro;

    public FinanceiroHealthIndicator(ConsultaFinanceiro consultaFinanceiro) {
        this.consultaFinanceiro = consultaFinanceiro;
    }

    @Override
    public Health health() {
        try {
            consultaFinanceiro.isUp();
            return Health.up().build();
        } catch (FeignException ex) {
            logger.error(ex.getMessage());
            return Health.down().build();
        }
    }
}
