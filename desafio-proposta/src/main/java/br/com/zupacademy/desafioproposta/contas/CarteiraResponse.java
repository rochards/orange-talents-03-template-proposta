package br.com.zupacademy.desafioproposta.contas;

public class CarteiraResponse {

    private ResultadoCarteira resultado;
    private String id;

    public CarteiraResponse(ResultadoCarteira resultado, String id) {
        this.resultado = resultado;
        this.id = id;
    }

    public ResultadoCarteira getResultado() {
        return resultado;
    }

    public String getId() {
        return id;
    }
}
