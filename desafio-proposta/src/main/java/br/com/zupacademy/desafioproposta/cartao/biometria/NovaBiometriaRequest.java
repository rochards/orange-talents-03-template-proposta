package br.com.zupacademy.desafioproposta.cartao.biometria;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NovaBiometriaRequest {

    private String impressaoDigital;

    public NovaBiometriaRequest(@JsonProperty("impressaoDigital") String impressaoDigital) {
        this.impressaoDigital = impressaoDigital;
    }

    public Biometria toModel(Cartao cartao) {
        return new Biometria(impressaoDigital, cartao);
    }
}
