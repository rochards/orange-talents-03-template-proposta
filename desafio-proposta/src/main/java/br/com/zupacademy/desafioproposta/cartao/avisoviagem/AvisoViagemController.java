package br.com.zupacademy.desafioproposta.cartao.avisoviagem;

import br.com.zupacademy.desafioproposta.cartao.Cartao;
import br.com.zupacademy.desafioproposta.compartilhado.handlers.APIErrorHandler;
import br.com.zupacademy.desafioproposta.compartilhado.transacao.Transacao;
import io.opentracing.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/cartoes")
public class AvisoViagemController {

    private final Tracer tracer;
    private final Transacao transacao;

    public AvisoViagemController(Tracer tracer, Transacao transacao) {
        this.tracer = tracer;
        this.transacao = transacao;
    }

    @PostMapping("{idCartao}/avisos")
    public ResponseEntity<?> registra(@PathVariable String idCartao, @RequestBody @Valid AvisoViagemRequest avisoRequest,
                                      BindingResult result, HttpServletRequest httpRequest) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new APIErrorHandler(result.getFieldErrors()));
        }
        var cartao = transacao.busca(Cartao.class, "contasIdCartao", idCartao);
        if (cartao == null) {
            return ResponseEntity.notFound().build();
        }

        var activeSpan = tracer.activeSpan();
        activeSpan.setTag("id.cartao", idCartao);
        activeSpan.setBaggageItem("user.agent", httpRequest.getHeader("User-Agent"));
        activeSpan.log("aviso sobre viagem...");

        var novoAviso = avisoRequest.toModel(httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent"), cartao);
        transacao.salvaEComita(novoAviso);

        return ResponseEntity.ok().build();
    }
}
