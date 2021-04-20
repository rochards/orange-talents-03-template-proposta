package br.com.zupacademy.desafioproposta.proposta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropostaRepository extends JpaRepository<Proposta, Integer> {

    boolean existsByDocumento(String documento);

    List<Proposta> findFirst100ByStatusAndCartoesEmpty(StatusProposta status);
}
