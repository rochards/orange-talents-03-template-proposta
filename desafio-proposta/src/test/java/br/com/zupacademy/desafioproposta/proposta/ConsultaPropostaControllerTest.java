package br.com.zupacademy.desafioproposta.proposta;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConsultaPropostaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private PropostaRepository propostaRepository;

    private final String endpoint = "/propostas/{id}";

    @Test
    @Transactional // apesar de não estar salvando nada no banco, estou utilizando essa anotação para evitar o
    // error org.hibernate.lazyinitializationexception could not initialize proxy - no session
    @DisplayName("deveria retornar status 200 para uma proposta registrada no banco de dados")
    void consultaTeste01() throws Exception {
        var idProposta = 1;
        var propostaEsperada = propostaRepository.findById(idProposta).get();
        var propostaResponseEsperada = new ConsultaPropostaResponse(propostaEsperada);

        MvcResult resultRequest = mockMvc
                .perform(get(endpoint, idProposta))
                .andExpect(status().isOk())
                .andReturn();

        var propostaResponse = resultRequest.getResponse().getContentAsString();
        assertEquals(propostaResponse, objectToJsonString(propostaResponseEsperada));
    }

    @Test
    @DisplayName("deveria retornar status 404 para uma proposta não registrada no banco de dados")
    void consultaTeste02() throws Exception {
        mockMvc.perform(get(endpoint, "100"))
                .andExpect(status().isNotFound());
    }

    private String objectToJsonString(ConsultaPropostaResponse consultaResponse) throws JsonProcessingException {
        return mapper.writeValueAsString(consultaResponse);
    }
}