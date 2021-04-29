package br.com.zupacademy.desafioproposta.cartao.carteira;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.cartao.EventosCartao;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.zupacademy.desafioproposta.cartao.carteira.NomeCarteira.SAMSUNG_PAY;
import static br.com.zupacademy.desafioproposta.cartao.carteira.StatusCarteira.NAO_ASSOCIADA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NovoSamsungpayControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private Transacao transacao;
    @MockBean
    private EventosCartao eventosCartao;

    private final String endpoint = "/cartoes/{idCartao}/carteiras/samsungpay";

    @Test
    @DisplayName("deveria retornar 201 caso fosse feita um request válida com id de cartão existente")
    void associaTeste01() throws Exception {
        var novaCarteiraRequest = new NovaCarteiraRequest("parker.aranha@gmail.com");

        var idCartao = "2545-1400-4183-6148";
        var cartao = transacao.busca(Cartao.class, "contasIdCartao", idCartao);
        var carteiraCadastradaEsperada = new CarteiraDigital("parker.aranha@gmail.com", SAMSUNG_PAY, cartao);

        var location =  mockMvc
                .perform(post(endpoint, idCartao)
                    .contentType(APPLICATION_JSON)
                    .content(toJson(novaCarteiraRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        var idCarteiraCadastrada = recuperaIdDoLocation(location);
        var carteiraCadastrada = transacao.busca(CarteiraDigital.class, "id", idCarteiraCadastrada);

        assertNotNull(carteiraCadastrada);
        assertEquals(carteiraCadastradaEsperada, carteiraCadastrada);
        assertEquals(carteiraCadastrada.getCartao(), cartao);
        assertEquals(NAO_ASSOCIADA, carteiraCadastrada.getStatus());
        assertEquals(SAMSUNG_PAY, carteiraCadastrada.getNome());
        verify(eventosCartao).associaCarteira(idCartao, carteiraCadastradaEsperada);

    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "invalido.mail.com.br"})
    @DisplayName("deveria retornar 400 caso fosse feita uma request com email branco ou em formato inválido")
    void associaTeste02(String email) throws Exception {
        var novaCarteiraRequest = new NovaCarteiraRequest(email);
        var idCartao = "2545-1400-4183-6148";

        mockMvc.perform(post(endpoint, idCartao)
                    .contentType(APPLICATION_JSON)
                    .content(toJson(novaCarteiraRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deveria retornar 422 ao tentar associar um cartão a uma carteira que já está associada a esse cartão")
    void associaTeste03() throws Exception {
        var novaCarteiraRequest = new NovaCarteiraRequest("parker.aranha@gmail.com");
        var idCartao = "2545-3400-4183-7000";

        mockMvc.perform(post(endpoint, idCartao)
                    .contentType(APPLICATION_JSON)
                    .content(toJson(novaCarteiraRequest)))
                .andExpect(status().isUnprocessableEntity());

        verify(eventosCartao, never()).associaCarteira(eq(idCartao), any(CarteiraDigital.class));
    }

    private String toJson(NovaCarteiraRequest carteiraRequest) throws JsonProcessingException {
        return mapper.writeValueAsString(carteiraRequest);
    }

    private int recuperaIdDoLocation(String location) {
        var recurso = "/samsungpay/";
        var begin = location.indexOf(recurso);

        return Integer.parseInt(location.substring(begin + recurso.length()));
    }
}