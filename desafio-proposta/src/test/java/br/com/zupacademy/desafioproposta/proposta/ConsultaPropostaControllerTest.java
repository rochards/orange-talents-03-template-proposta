package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultaPropostaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private PropostaRepository propostaRepository;

    @Test
    @DisplayName("deveria retornar status 200 para uma proposta registrada no banco de dados")
    void consultaTeste01() throws Exception {
        var propostaRegistrada = new Proposta("93577269006", "parker.aranha@gmail.com", "Peter Parker", "Queens",
                BigDecimal.TEN);
        propostaRegistrada.atualizaStatus(true);

        var cartaoRegistrado = new Cartao("1234-4147-1574", LocalDateTime.now(), propostaRegistrada);
        ReflectionTestUtils.setField(propostaRegistrada, "cartoes", List.of(cartaoRegistrado));

        var propostaResponseEsperada = new ConsultaPropostaResponse(propostaRegistrada);


        when(propostaRepository.findById(1))
                .thenReturn(Optional.of(propostaRegistrada));

        MvcResult resultRequest = mockMvc.perform(get("/propostas/1"))
                .andExpect(status().isOk())
                .andReturn();

        String propostaResponse = resultRequest.getResponse().getContentAsString();
        assertEquals(propostaResponse, objectToJsonString(propostaResponseEsperada));
    }

    @Test
    @DisplayName("deveria retornar status 404 para uma proposta não registrada no banco de dados")
    void consultaTeste02() throws Exception {
        when(propostaRepository.findById(1))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/propostas/1"))
                .andExpect(status().isNotFound());
    }

    private String objectToJsonString(ConsultaPropostaResponse consultaResponse) throws JsonProcessingException {
        return mapper.writeValueAsString(consultaResponse);
    }

}