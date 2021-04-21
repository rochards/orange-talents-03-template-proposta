package br.com.zupacademy.desafioproposta.cartao.biometria;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import br.com.zupacademy.desafioproposta.proposta.Proposta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class NovaBiometriaControllerTest {

    @InjectMocks
    private ObjectMapper mapper;
    @MockBean
    private Transacao transacao;

    private URI endpoint;
    private MockMvc mockMvc;
    private Proposta proposta;
    private Cartao cartao;

    @BeforeEach
    void setUp() throws URISyntaxException {
        this.endpoint = new URI("/cartoes");
        this.proposta = new Proposta("91326072021", "parker.aranha@gmail.com", "Peter Parker", "Queens", BigDecimal.TEN);
        this.cartao = new Cartao("1234-5678-9010", LocalDateTime.now(), proposta);

        this.mockMvc = MockMvcBuilders // tive que fazer manual pq o @Autowired em MockMvc não estava funcionando
                .standaloneSetup(new NovaBiometriaController(transacao))
                .build();
    }

    @Test
    @DisplayName("deveria retornar 201 caso a impressão digital esteja em base64 e exista cartão para o id fornecido," +
            " e salvar a biometria")
    void cadastraTeste01() throws Exception {
        var biometriaRequest = new NovaBiometriaRequest("ZGVkb2luZGljYWRvcg==");
        var novaBiometria = biometriaRequest.toModel(cartao);
        ReflectionTestUtils.setField(novaBiometria, "id", 1);

        when(transacao.busca(Cartao.class, "contasIdCartao", "1234-5678-9010"))
                .thenReturn(cartao);
        when(transacao.salvaEComita(biometriaRequest.toModel(cartao)))
                .thenReturn(novaBiometria);

        var location = mockMvc
                .perform(post(endpoint + "/1234-5678-9010").content(toJson(biometriaRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");


        var idBiometria = recuperaIdDoLocation(location);
        verify(transacao).salvaEComita(biometriaRequest.toModel(cartao));
        assertEquals(1, idBiometria);
    }

    @Test
    @DisplayName("deveria retornar 400 caso a impressão digital fornecida não esteja em base64, e não deveria salvar " +
            "a biometria")
    void cadastraTeste02() throws Exception {
        var biometriaRequest = new NovaBiometriaRequest("-ssdfohu989-");

        mockMvc
                .perform(post(endpoint + "/1234-5678-9010").content(toJson(biometriaRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("impressaoDigital")));

    }

    @Test
    @DisplayName("deveria retornar 400 caso a impressão digital seja fornecida vazia, e não deveria salvar a biometria")
    void cadastraTeste03() throws Exception {
        var biometriaRequest = new NovaBiometriaRequest("   ");

        mockMvc
                .perform(post(endpoint + "/1234-5678-9010").content(toJson(biometriaRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("impressaoDigital")));

    }

    @Test
    @DisplayName("deveria retornar 404 se o cartão não for encontrado para o id fornecido, e não deveria salvar a " +
            "biometria")
    void cadastraTeste04() throws Exception {
        var biometriaRequest = new NovaBiometriaRequest("ZGVkb2luZGljYWRvcg==");

        when(transacao.busca(Cartao.class, "contasIdCartao", "0000-0000-0000"))
                .thenReturn(null);

        mockMvc
                .perform(post(endpoint + "/0000-0000-0000").content(toJson(biometriaRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(transacao, never()).salvaEComita(any(Biometria.class));
    }

    private String toJson(NovaBiometriaRequest biometriaRequest) throws JsonProcessingException {
        return mapper.writeValueAsString(biometriaRequest);
    }

    private int recuperaIdDoLocation(String location) {
        var recurso = "/biometrias/";
        var begin = location.indexOf(recurso);

        return Integer.parseInt(location.substring(begin + recurso.length()));
    }
}