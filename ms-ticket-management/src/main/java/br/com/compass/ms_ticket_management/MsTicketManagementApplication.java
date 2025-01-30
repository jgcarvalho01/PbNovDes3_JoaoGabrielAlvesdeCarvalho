package br.com.compass.ms_ticket_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "br.com.compass.ms_ticket_management.service")
public class MsTicketManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTicketManagementApplication.class, args);
	}

}
