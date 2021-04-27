package br.com.zupacademy.desafioproposta.cartao.avisoviagem;

import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AvisoViagemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private Transacao transacao;

    private final String endpoint = "/cartoes/{idCartao}/avisos";
    private static final LocalDate maxDate = LocalDate.of(9999, 12, 31);

    @Test
    @DisplayName("deveria retornar 404 caso o id do cartão fornecido não exista")
    void registraTeste01() throws Exception {
        var avisoRequest = new AvisoViagemRequest("Disney", maxDate);

        mockMvc
                .perform(post(endpoint, "0000-0000-0000-0000")
                        .header("User-Agent", "MockMvc")
                        .contentType(APPLICATION_JSON)
                        .content(toJson(avisoRequest)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("sourceRegistraTeste02")
    @DisplayName("deveria retornar 400 caso o json da request viole alguma restrição de validação")
    void registraTeste02(String destino, LocalDate emViagemAte, String fieldError) throws Exception {
        var avisoRequest = new AvisoViagemRequest(destino, emViagemAte);

        mockMvc
                .perform(post(endpoint, "2545-1400-4183-6148")
                        .header("User-Agent", "MockMvc")
                        .contentType(APPLICATION_JSON)
                        .content(toJson(avisoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is(fieldError)));
    }

    static Stream<Arguments> sourceRegistraTeste02() {
        return Stream.of(
                Arguments.of("  ", maxDate, "destino"),
                Arguments.of("Disney", LocalDate.now(), "emViagemAte"),
                Arguments.of(null, maxDate, "destino"),
                Arguments.of("Disney", null, "emViagemAte")
        );
    }

    @Test
    @DisplayName("deveria retornar 200 caso uma request válida com id de cartão existente fosse fornecida, e " +
            "inserir os dados do aviso no banco de dados")
    void registraTeste03() throws Exception {
        var idCartao = "2545-1400-4183-6148";
        var avisoRequest = new AvisoViagemRequest("Disney", maxDate);

        mockMvc
                .perform(post(endpoint, idCartao)
                        .header("User-Agent", "MockMvc")
                        .contentType(APPLICATION_JSON)
                        .content(toJson(avisoRequest)))
                .andExpect(status().isOk());

        var avisoViagemEsperado = transacao.busca(AvisoViagem.class, "id", 1);

        assertNotNull(avisoViagemEsperado);
        assertEquals("Disney", avisoViagemEsperado.getDestino());
        assertEquals(maxDate, avisoViagemEsperado.getEmViagemAte());
        assertEquals("MockMvc", avisoViagemEsperado.getUserAgent());
        assertNotNull(avisoViagemEsperado.getEmViagemAte());
        assertEquals(StatusAvisoViagem.NAO_CRIADO, avisoViagemEsperado.getStatusAvisoNoLegado());
        assertEquals(idCartao, avisoViagemEsperado.getContasIdCartao());

    }

    private String toJson(AvisoViagemRequest propostaRequest) throws JsonProcessingException {
        return mapper.writeValueAsString(propostaRequest);
    }
}