package br.com.compass.ms_event_management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@EnableFeignClients(basePackages = "br.com.compass.ms_event_management.service")
@SpringBootApplication
public class MsEventManagementApplication {

	public static void main(String[] args) {
		log.info("Iniciando o microsserviço Event Management...");
		SpringApplication.run(MsEventManagementApplication.class, args);
		log.info("Microsserviço Event Management iniciado com sucesso!");
	}

}
