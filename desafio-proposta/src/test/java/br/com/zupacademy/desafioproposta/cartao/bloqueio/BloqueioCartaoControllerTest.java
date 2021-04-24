package br.com.zupacademy.desafioproposta.cartao.bloqueio;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.cartao.StatusCartao;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BloqueioCartaoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Transacao transacao;

    private final String endpoint = "/cartoes/{idCartao}/bloqueios";

    @Test
    @DisplayName("deveria retornar 404 caso o id do cartão fornecido não exista")
    void bloqueiaTeste01() throws Exception {
        mockMvc
                .perform(put(endpoint, "0000-0000-0000-0000").header("User-Agent", "MockMvc"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deveria retornar 422 caso o cartão já esteja bloqueado no sistema")
    void bloqueiaTeste02() throws Exception {
        mockMvc
                .perform(put(endpoint, "8866-2159-7683-4969").header("User-Agent", "MockMvc"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("deveria retornar 400 caso o cliente não forneça seu User-Agent")
    void bloqueiaTeste03() throws Exception {
        mockMvc
                .perform(put(endpoint, "2545-1400-4183-6148"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("User-Agent")));
    }

    @Test
    @DisplayName("deveria retornar 200 caso uma request válida com id de cartão válido fosse fornecida, e atualizar " +
            "o status do cartão para BLOQUEADO no banco de dados e preencher a tabela bloqueio")
    void bloqueiaTeste04() throws Exception {
        var idCartao = "2545-1400-4183-6148";
        mockMvc
                .perform(put(endpoint, idCartao).header("User-Agent", "MockMvc"))
                .andExpect(status().isOk());

        var cartao = transacao.busca(Cartao.class, "contasIdCartao", idCartao);
        var bloqueio = cartao.getBloqueio();

        assertEquals(StatusCartao.BLOQUEADO, cartao.getStatus());
        assertNotNull(bloqueio);
        assertEquals("MockMvc", bloqueio.getUserAgentCliente());
    }
}