package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.cartao.EventosCartao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NovaPropostaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private EventosCartao eventosCartao;
    @MockBean
    private AnalisaNovaProposta analisaNovaProposta;

    private URI endpoint;

    @BeforeEach
    void setUp() throws URISyntaxException {
        this.endpoint = new URI("/propostas");
    }

    @Test
    @DisplayName("deveria criar uma nova proposta com sucesso")
    void teste01() throws Exception {
        var propostaRequest = new NovaPropostaRequest("93577269006", "teste@email.com.br", "Nome de teste", "Endereco " +
                "de teste", BigDecimal.valueOf(1950));
        String jsonRequest = toJson(propostaRequest);

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o documento estiver com formato inválido")
    void teste02() throws Exception {
        var propostaRequest = new NovaPropostaRequest("00000000000", "teste@email.com.br", "Nome de teste", "Endereco " +
                "de teste", BigDecimal.valueOf(1950));
        String jsonRequest = toJson(propostaRequest);

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("documento")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o e-mail estiver com formato inválido")
    void teste03() throws Exception {
        var propostaRequest = new NovaPropostaRequest("93577269006", "invalido.com.br", "Nome de teste", "Endereco " +
                "de teste", BigDecimal.valueOf(1950));
        String jsonRequest = toJson(propostaRequest);

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("email")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o nome estiver em branco")
    void teste04() throws Exception {
        var propostaRequest = new NovaPropostaRequest("93577269006", "teste@email.com.br", "   ", "Endereco " +
                "de teste", BigDecimal.valueOf(1950));
        String jsonRequest = toJson(propostaRequest);

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("nome")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o endereço estiver em branco")
    void teste05() throws Exception {
        var propostaRequest = new NovaPropostaRequest("93577269006", "teste@email.com.br", "Nome de teste", " ",
                BigDecimal.valueOf(1950));
        String jsonRequest = toJson(propostaRequest);

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("endereco")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o salário for menor ou igual a zero")
    void teste06() throws Exception {
        var propostaRequest = new NovaPropostaRequest("93577269006", "teste@email.com.br", "Nome de teste", "Endereco " +
                "de teste", BigDecimal.ZERO);
        String jsonRequest = toJson(propostaRequest);

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("salario")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o documento estiver com pontuação")
    void teste07() throws Exception {
        var propostaRequest = new NovaPropostaRequest("935.772.690-06", "teste@email.com.br", "Nome de teste",
                "Endereco de teste", BigDecimal.valueOf(1950));
        String jsonRequest = toJson(propostaRequest);

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("documento")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta com documento já existente no sistema")
    void teste08() throws Exception {
        var propostaRequest = new NovaPropostaRequest("93293100000134", "teste@email.com.br", "Nome de teste",
                "Endereco de teste", BigDecimal.valueOf(1950));
        String jsonRequest1 = toJson(propostaRequest);
        String jsonRequest2 = toJson(propostaRequest);

        mockMvc.perform(post(endpoint).content(jsonRequest1).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        mockMvc.perform(post(endpoint).content(jsonRequest2).contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field", is("documento")));
    }

    @Test
    @DisplayName("deveria enviar solicitação de novo cartão, caso a nova proposta seja elegível para tal")
    void teste09() throws Exception {
        var propostaRequest = new NovaPropostaRequest("79074714005", "teste@email.com.br", "Nome de teste",
                "Endereco de teste", BigDecimal.valueOf(1950));
        String jsonRequest = toJson(propostaRequest);

        when(analisaNovaProposta.semRestricao(eq(propostaRequest.getDocumento()), eq(propostaRequest.getNome()), anyString()))
                .thenReturn(true);

        var location = mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        var idProposta = recuperaIdDoLocation(location);
        verify(analisaNovaProposta).semRestricao(propostaRequest.getDocumento(), propostaRequest.getNome(), String.valueOf(idProposta));
        verify(eventosCartao).solicitaNovo(propostaRequest.getDocumento(), propostaRequest.getNome(), idProposta);
    }

    @Test
    @DisplayName("não deveria enviar solicitação de novo cartão, quando a nova proposta não seja elegível para tal")
    void teste10() throws Exception {
        var propostaRequest = new NovaPropostaRequest("91326072021", "teste@email.com.br", "Nome de teste",
                "Endereco de teste", BigDecimal.valueOf(1950));
        String jsonRequest = toJson(propostaRequest);

        when(analisaNovaProposta.semRestricao(eq(propostaRequest.getDocumento()), eq(propostaRequest.getNome()), anyString()))
                .thenReturn(false);

        var location = mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        var idProposta = recuperaIdDoLocation(location);
        verify(analisaNovaProposta).semRestricao(propostaRequest.getDocumento(), propostaRequest.getNome(), String.valueOf(idProposta));
        verify(eventosCartao, never()).solicitaNovo(propostaRequest.getDocumento(), propostaRequest.getNome(), idProposta);
    }

    private String toJson(NovaPropostaRequest propostaRequest) throws JsonProcessingException {
        return mapper.writeValueAsString(propostaRequest);
    }

    private int recuperaIdDoLocation(String location) {
        var recurso = "/propostas/";
        var begin = location.indexOf(recurso);

        return Integer.parseInt(location.substring(begin + recurso.length()));
    }
}
