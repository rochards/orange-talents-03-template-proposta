package br.com.zupacademy.desafioproposta.cartao;

import br.com.zupacademy.desafioproposta.cartao.carteira.CarteiraDigital;
import br.com.zupacademy.desafioproposta.cartao.carteira.NomeCarteira;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartaoTest {


    private Cartao cartao;

    @BeforeEach
    void setUp() {
        this.cartao = new Cartao("1234-1234-1234-1234", LocalDateTime.now(), null);
    }

    @ParameterizedTest
    @CsvSource({"true, PAYPAL", "true, SAMSUNG_PAY"})
    @DisplayName("verificar se as carteiras PAYPAL e SAMSUNG_PAY já estão associadas a esse cartão")
    void associadoACarteiraTeste01(boolean resultadoEsperado, NomeCarteira nomeCarteira) {
        cartao.getCarteirasDigitais().addAll(List.of(
                new CarteiraDigital("mail1@gmail.com", NomeCarteira.PAYPAL, cartao),
                new CarteiraDigital("mail2@gmail.com", NomeCarteira.SAMSUNG_PAY, cartao)
        ));

        assertEquals(resultadoEsperado, cartao.associadoACarteira(nomeCarteira));
    }

    @ParameterizedTest
    @CsvSource({"true, PAYPAL", "false, SAMSUNG_PAY"})
    @DisplayName("verificar se a carteira PAYPAL já está associada a esse cartão")
    void associadoACarteiraTeste02(boolean resultadoEsperado, NomeCarteira nomeCarteira) {
        cartao.getCarteirasDigitais().addAll(List.of(
                new CarteiraDigital("mail1@gmail.com", NomeCarteira.PAYPAL, cartao)
        ));

        assertEquals(resultadoEsperado, cartao.associadoACarteira(nomeCarteira));
    }

    @ParameterizedTest
    @CsvSource({"false, PAYPAL", "true, SAMSUNG_PAY"})
    @DisplayName("verificar se a carteira SAMSUNG_PAY já está associada a esse cartão")
    void associadoACarteiraTeste03(boolean resultadoEsperado, NomeCarteira nomeCarteira) {
        cartao.getCarteirasDigitais().addAll(List.of(
                new CarteiraDigital("mail1@gmail.com", NomeCarteira.SAMSUNG_PAY, cartao)
        ));

        assertEquals(resultadoEsperado, cartao.associadoACarteira(nomeCarteira));
    }

    @ParameterizedTest
    @CsvSource({"false, PAYPAL", "false, SAMSUNG_PAY"})
    @DisplayName("verificar se nenhuma carteira está associada a esse cartão")
    void associadoACarteiraTeste04(boolean resultadoEsperado, NomeCarteira nomeCarteira) {
        cartao.getCarteirasDigitais().addAll(List.of());

        assertEquals(resultadoEsperado, cartao.associadoACarteira(nomeCarteira));
    }
}