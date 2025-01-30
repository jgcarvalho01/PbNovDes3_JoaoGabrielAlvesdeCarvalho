package br.com.compass.ms_ticket_management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@EnableFeignClients(basePackages = "br.com.compass.ms_ticket_management.service")
public class MsTicketManagementApplication {

	public static void main(String[] args) {
		log.info("Iniciando o microsserviço Ticket Management...");
		SpringApplication.run(MsTicketManagementApplication.class, args);
		log.info("Microsserviço Ticket Management iniciado com sucesso!");
	}

}
