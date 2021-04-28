package br.com.zupacademy.desafioproposta.cartao.carteira;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class NovoSamsungpayRequest {

    @NotBlank @Email
    public String email;

    public NovoSamsungpayRequest(@JsonProperty("email") String email) {
        this.email = email;
    }

    public CarteiraDigital toModel(Cartao cartao) {
        return new CarteiraDigital(email, NomeCarteira.SAMSUNG_PAY, cartao);
    }
}
