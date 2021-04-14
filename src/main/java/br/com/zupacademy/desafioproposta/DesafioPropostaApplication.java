package br.com.zupacademy.desafioproposta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class DesafioPropostaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafioPropostaApplication.class, args);
	}

}
