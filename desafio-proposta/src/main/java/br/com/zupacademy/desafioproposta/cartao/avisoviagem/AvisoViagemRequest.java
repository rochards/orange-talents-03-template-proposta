package br.com.zupacademy.desafioproposta.cartao.avisoviagem;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class AvisoViagemRequest {

    @NotBlank
    private String destino;

    @NotNull @Future
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate emViagemAte;

    public AvisoViagemRequest(String destino, @JsonProperty("emViagemAte") LocalDate emViagemAte) {
        this.destino = destino;
        this.emViagemAte = emViagemAte;
    }

    public AvisoViagem toModel(String ipCliente, String userAgente, Cartao cartao) {
        return new AvisoViagem(destino, emViagemAte, ipCliente, userAgente, cartao);
    }

    public String getDestino() {
        return destino;
    }

    public LocalDate getEmViagemAte() {
        return emViagemAte;
    }
}
