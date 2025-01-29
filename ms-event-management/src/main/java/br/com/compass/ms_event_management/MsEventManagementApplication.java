package br.com.compass.ms_event_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "br.com.compass.ms_event_management.service")
@SpringBootApplication
public class MsEventManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsEventManagementApplication.class, args);
	}

}
