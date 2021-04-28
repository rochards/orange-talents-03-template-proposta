package br.com.zupacademy.desafioproposta.contas;

public class NovaCarteiraRequest {

    private String email;
    private String carteira;

    public NovaCarteiraRequest(String email, String carteira) {
        this.email = email;
        this.carteira = carteira;
    }

    public String getEmail() {
        return email;
    }

    public String getCarteira() {
        return carteira;
    }
}
