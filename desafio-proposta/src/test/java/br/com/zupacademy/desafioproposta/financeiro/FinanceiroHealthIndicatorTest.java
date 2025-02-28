package br.com.zupacademy.desafioproposta.financeiro;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FinanceiroHealthIndicatorTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ServicoFinanceiro servicoFinanceiro;

    private URI healthEndpoint;

    @BeforeEach
    void setUp() throws URISyntaxException {
        this.healthEndpoint = new URI("/actuator/health/financeiro");
    }


    @Test
    @DisplayName("deveria retornar UP quando o serviço de Análise Financeira estiver respondendo")
    void healthTeste01() throws Exception {
        doNothing()
                .when(servicoFinanceiro).isUp();


        mockMvc.perform(get(healthEndpoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    @DisplayName("deveria retornar DOWN quando o serviço de Análise Financeira não estiver respondendo")
    void healthTest02() throws Exception {
        doThrow(FeignException.class)
                .when(servicoFinanceiro).isUp();

        mockMvc.perform(get(healthEndpoint))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status", is("DOWN")));
    }
}
