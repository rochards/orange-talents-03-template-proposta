package br.com.zupacademy.desafioproposta.cartao.carteira;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class NovaCarteiraRequest {

    @NotBlank @Email
    private String email;

    public NovaCarteiraRequest(@JsonProperty("email") String email) {
        this.email = email;
    }

    public CarteiraDigital toModel(NomeCarteira nomeCarteira, Cartao cartao) {
        return new CarteiraDigital(email, nomeCarteira, cartao);
    }

    public String getEmail() {
        return email;
    }
}
