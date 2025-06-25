package br.com.teste;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Api {

	public static void main(String[] args) {
		SpringApplication.run(Api.class, args);
	}


}
