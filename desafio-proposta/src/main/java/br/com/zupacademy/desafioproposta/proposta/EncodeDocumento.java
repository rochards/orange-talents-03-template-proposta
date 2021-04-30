package br.com.zupacademy.desafioproposta.proposta;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Component;

@Component
public class EncodeDocumento {

    @Value("${documento.salt}")
    private String salt;
    @Value("${documento.password}")
    private String password;

    private String encodedDocumento;

    /**
     * @param rawDocumento documento a ser criptografado
     * @return documento criptografado
     * */
    public String encode(String rawDocumento) {
        var textEncryptor = Encryptors.queryableText(password, salt);
        return textEncryptor.encrypt(rawDocumento);
    }
}
