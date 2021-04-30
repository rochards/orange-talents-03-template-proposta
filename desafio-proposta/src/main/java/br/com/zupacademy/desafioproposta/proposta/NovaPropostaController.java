package br.com.zupacademy.desafioproposta.proposta;

import br.com.zupacademy.desafioproposta.cartao.EventosCartao;
import br.com.zupacademy.desafioproposta.compartilhado.handlers.APIErrorHandler;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import io.opentracing.Tracer;
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

    private final Tracer tracer;
    private final Transacao transacao;
    private final EventosCartao eventosCartao;
    private final EncodeDocumento encodeDocumento;
    private final PropostaRepository propostaRepository;
    private final AnalisaNovaProposta analisaNovaProposta;

    public NovaPropostaController(Tracer tracer, Transacao transacao, EventosCartao eventosCartao,
                                  EncodeDocumento encodeDocumento, PropostaRepository propostaRepository, AnalisaNovaProposta analisaNovaProposta) {
        this.tracer = tracer;
        this.transacao = transacao;
        this.eventosCartao = eventosCartao;
        this.encodeDocumento = encodeDocumento;
        this.propostaRepository = propostaRepository;
        this.analisaNovaProposta = analisaNovaProposta;
    }

    @PostMapping
    public ResponseEntity<?> cria(@RequestBody @Valid NovaPropostaRequest propostaRequest, BindingResult result,
                                  UriComponentsBuilder uriBuilder) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new APIErrorHandler(result.getFieldErrors()));
        }
        if (propostaRepository.existsByDocumento(encodeDocumento.encode(propostaRequest.getDocumento()))) {
            var errors = new APIErrorHandler(List.of(new FieldError("Proposta", "documento",
                    "já existe uma proposta cadastrada para esse documento")), UNPROCESSABLE_ENTITY);
            return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(errors);
        }

        var activeSpan = tracer.activeSpan();
        activeSpan.setTag("solicitante.email", propostaRequest.getEmail());
        activeSpan.setBaggageItem("solicitante.email", propostaRequest.getEmail());
        activeSpan.log("criando nova proposta...");

        var proposta = propostaRequest.toModel(encodeDocumento);
        proposta = transacao.salvaEComita(proposta);

        boolean elegivel = analisaNovaProposta.semRestricao(propostaRequest.getDocumento(), proposta.getNome(),
                proposta.getId().toString());
        if (elegivel) {
            eventosCartao.solicitaNovo(propostaRequest.getDocumento(), proposta.getNome(), proposta.getId());
        }

        proposta.atualizaStatus(elegivel);
        transacao.atualizaEComita(proposta);

        URI location = uriBuilder.path("/propostas/{id}").build(proposta.getId());
        return ResponseEntity.created(location).build();
    }
}
