package br.com.zupacademy.desafioproposta.cartao.biometria;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class NovaBiometriaRequest {

    @NotBlank
    @Pattern(regexp = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$", message = "deveria estar em " +
            "base64")
    private String impressaoDigital;

    public NovaBiometriaRequest(@JsonProperty("impressaoDigital") String impressaoDigital) {
        this.impressaoDigital = impressaoDigital;
    }

    public Biometria toModel(Cartao cartao) {
        return new Biometria(impressaoDigital, cartao);
    }

    public String getImpressaoDigital() {
        return impressaoDigital;
    }
}
