package br.com.zupacademy.desafioproposta.proposta;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/propostas")
public class NovaPropostaController {

    private final EntityManager em;

    public NovaPropostaController(EntityManager em) {
        this.em = em;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> cria(@RequestBody NovaPropostaRequest propostaRequest, UriComponentsBuilder uriBuilder) {

        var proposta = propostaRequest.toModel();
        em.persist(proposta);

        URI location = uriBuilder.path("/propostas/{id}").build(proposta.getId());
        var propostaResponse = new NovaPropostaResponse(proposta);

        return ResponseEntity.created(location).body(propostaResponse);
    }
}
