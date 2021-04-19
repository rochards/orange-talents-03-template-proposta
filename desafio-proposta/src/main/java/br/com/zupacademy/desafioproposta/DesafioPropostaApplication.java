package br.com.zupacademy.desafioproposta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableFeignClients
@SpringBootApplication
@EnableJpaRepositories(enableDefaultTransactions = false)
public class DesafioPropostaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafioPropostaApplication.class, args);
	}

}

/*
* Motivo para eu ter added @EnableJpaRepositories: todos os métodos declarados nos repositories são transacionais por
* default. Assim, quando vc chama, por exemplo em um controller, dois métodos save, cada um será executado em uma transação
* diferente. Tal comportamento não é adequado, pois você deveria ser o responsável por cuidar das transações. Daí
* surge o motivo da configuração acima. Agora você serei obrigado a anotar um transação com @Transactional.
* */
