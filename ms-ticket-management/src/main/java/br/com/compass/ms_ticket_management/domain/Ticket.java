package br.com.compass.ms_ticket_management.domain;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String ticketId;
    @NotNull(message = "O nome do cliente é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String customerName;
    @NotNull(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos")
    private String cpf;
    @NotNull(message = "O e-mail do cliente é obrigatório")
    @Email(message = "E-mail inválido")
    private String customerMail;
    @NotNull(message = "O ID do evento é obrigatório")
    private String eventId;
    private String eventName;
    @NotNull(message = "O valor em BRL é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O valor deve ser maior que 0")
    private Double brlAmount;
    @NotNull(message = "O valor em USD é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O valor deve ser maior que 0")
    private Double usdAmount;
    private String status;

}
