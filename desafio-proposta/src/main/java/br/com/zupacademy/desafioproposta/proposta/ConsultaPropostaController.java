package br.com.zupacademy.desafioproposta.proposta;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/propostas")
public class ConsultaPropostaController {

    private final PropostaRepository propostaRepository;

    public ConsultaPropostaController(PropostaRepository propostaRepository) {
        this.propostaRepository = propostaRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> consulta(@PathVariable Integer id) {
        var optProposta = propostaRepository.findById(id);
        return optProposta.map(proposta -> ResponseEntity.ok(new ConsultaPropostaResponse(proposta)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
