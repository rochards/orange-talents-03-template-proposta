package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.compartilhado.handlers.APIErrorHandler;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
@RequestMapping("/propostas")
public class NovaPropostaController {

    private final PropostaRepository propostaRepository;
    private final AnalisaNovaProposta analisaNovaProposta;
    private final Transacao transacao;

    public NovaPropostaController(PropostaRepository propostaRepository, AnalisaNovaProposta analisaNovaProposta,
                                  Transacao transacao) {
        this.propostaRepository = propostaRepository;
        this.analisaNovaProposta = analisaNovaProposta;
        this.transacao = transacao;
    }

    @PostMapping
    public ResponseEntity<?> cria(@RequestBody @Valid NovaPropostaRequest propostaRequest, BindingResult result,
                                  UriComponentsBuilder uriBuilder) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new APIErrorHandler(result.getFieldErrors()));
        }
        if (propostaRepository.existsByDocumento(propostaRequest.getDocumento())) {
            var errors = new APIErrorHandler(List.of(new FieldError("Proposta", "documento",
                    "já existe uma proposta cadastrada para esse documento")), UNPROCESSABLE_ENTITY);
            return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(errors);
        }

        var proposta = propostaRequest.toModel();
        proposta = transacao.salvaEComita(proposta);

        boolean elegivel = analisaNovaProposta.semRestricao(proposta.getDocumento(), proposta.getNome(),
                proposta.getId().toString());
        proposta.atualizaStatus(elegivel);
        transacao.atualizaEComita(proposta);

        URI location = uriBuilder.path("/propostas/{id}").build(proposta.getId());
        return ResponseEntity.created(location).build();
    }
}
