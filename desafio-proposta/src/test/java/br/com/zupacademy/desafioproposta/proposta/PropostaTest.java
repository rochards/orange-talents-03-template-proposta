package br.com.zupacademy.desafioproposta.proposta;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PropostaTest {

    @ParameterizedTest
    @CsvSource({"true, ELEGIVEL", "false, NAO_ELEGIVEL"})
    void atualizaStatusTeste(boolean elegivel, StatusProposta statusEsperado) {
        var proposta = new Proposta("22339358027", "parker.aranha@gmail.com.br", "Peter Parker", "Queens",
                new BigDecimal("1950.00"));

        proposta.atualizaStatus(elegivel);

        assertEquals(statusEsperado, proposta.getStatus());
    }
}