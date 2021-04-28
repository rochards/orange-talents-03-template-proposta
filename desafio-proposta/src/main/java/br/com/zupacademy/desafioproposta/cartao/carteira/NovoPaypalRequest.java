package br.com.zupacademy.desafioproposta.cartao.carteira;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class NovoPaypalRequest {

    @NotBlank @Email
    private String email;

    public NovoPaypalRequest(@JsonProperty("email") String email) {
        this.email = email;
    }

    public CarteiraDigital toModel(Cartao cartao) {
        return new CarteiraDigital(email, NomeCarteira.PAYPAL, cartao);
    }
}
