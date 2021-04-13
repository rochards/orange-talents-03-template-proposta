package br.com.zupacademy.desafioproposta.proposta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.core.Is.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NovaPropostaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private URI endpoint;


    @BeforeEach
    void setUp() throws URISyntaxException {
        this.endpoint = new URI("/propostas");
    }

    @Test
    @DisplayName("deveria criar uma nova proposta com sucesso")
    void teste01() throws Exception {
        String jsonRequest = "{\"documento\":\"93577269006\", \"email\":\"teste@email.com.br\", " +
                "\"nome\":\"Nome de teste\", \"endereco\":\"Endereco de teste\", \"salario\":\"1950.00\"}";

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o documento estiver com formato inválido")
    void teste02() throws Exception {
        String jsonRequest = "{\"documento\":\"00000000000\", \"email\":\"teste@email.com.br\", " +
                "\"nome\":\"Nome de teste\", \"endereco\":\"Endereco de teste\", \"salario\":\"1950.00\"}";

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("documento")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o e-mail estiver com formato inválido")
    void teste03() throws Exception {
        String jsonRequest = "{\"documento\":\"93577269006\", \"email\":\"invalido.com.br\", " +
                "\"nome\":\"Nome de teste\", \"endereco\":\"Endereco de teste\", \"salario\":\"1950.00\"}";

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("email")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o nome estiver em branco")
    void teste04() throws Exception {
        String jsonRequest = "{\"documento\":\"93577269006\", \"email\":\"teste@email.com.br\", " +
                "\"nome\":\"  \", \"endereco\":\"Endereco de teste\", \"salario\":\"1950.00\"}";

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("nome")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o endereço estiver em branco")
    void teste05() throws Exception {
        String jsonRequest = "{\"documento\":\"93577269006\", \"email\":\"teste@email.com.br\", " +
                "\"nome\":\"Nome de teste\", \"endereco\":\"  \", \"salario\":\"1950.00\"}";

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("endereco")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o salário for menor ou igual a zero")
    void teste06() throws Exception {
        String jsonRequest = "{\"documento\":\"93577269006\", \"email\":\"teste@email.com.br\", " +
                "\"nome\":\"Nome de teste\", \"endereco\":\"Endereco de teste\", \"salario\":\"0.00\"}";

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("salario")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta se o documento estiver com pontuação")
    void teste07() throws Exception {
        String jsonRequest = "{\"documento\":\"935.772.690-06\", \"email\":\"teste@email.com.br\", " +
                "\"nome\":\"Nome de teste\", \"endereco\":\"Endereco de teste\", \"salario\":\"1950.00\"}";

        mockMvc.perform(post(endpoint).content(jsonRequest).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("documento")));
    }

    @Test
    @DisplayName("não deveria criar uma nova proposta com documento já existente no sistema")
    void teste08() throws Exception {
        String jsonRequest1 = "{\"documento\":\"93293100000134\", \"email\":\"teste1@email.com.br\", " +
                "\"nome\":\"Nome de teste\", \"endereco\":\"Endereco de teste\", \"salario\":\"1950.00\"}";
        String jsonRequest2 = "{\"documento\":\"93293100000134\", \"email\":\"teste2@email.com.br\", " +
                "\"nome\":\"Nome de teste\", \"endereco\":\"Endereco de teste\", \"salario\":\"1950.00\"}";

        mockMvc.perform(post(endpoint).content(jsonRequest1).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        mockMvc.perform(post(endpoint).content(jsonRequest2).contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field", is("documento")));
    }
}