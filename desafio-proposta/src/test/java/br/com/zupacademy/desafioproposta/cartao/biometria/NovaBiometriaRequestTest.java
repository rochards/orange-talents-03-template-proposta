package br.com.zupacademy.desafioproposta.cartao.biometria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NovaBiometriaRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest
    @MethodSource("geradorTeste01")
    @DisplayName("deveria validar se a impressão digital está em base64")
    void teste01(String impressaoDigital, boolean esperado) {
        var novaBiometriaRequest = new NovaBiometriaRequest(impressaoDigital);

        Set<ConstraintViolation<NovaBiometriaRequest>> violations = validator.validate(novaBiometriaRequest);

        assertEquals(esperado, violations.isEmpty());
    }

    static Stream<Arguments> geradorTeste01() {
        return Stream.of(
                Arguments.of("-ssdfohu989-", false),
                Arguments.of("3qferararaafs", false),
                Arguments.of("j37942034dfaer", false),
                Arguments.of("aW1wcmVzc2FvRGlnaXRhbA==", true),
                Arguments.of("ZGVkb2luZGljYWRvcg==", true),
                Arguments.of("ZGVkb3BvbGVnYXI=", true)
        );
    }
}