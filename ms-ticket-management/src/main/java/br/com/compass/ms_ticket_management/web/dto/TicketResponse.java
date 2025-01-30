package br.com.compass.ms_ticket_management.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketResponse {
    private String ticketId;
    private String cpf;
    private String customerName;
    private String customerMail;
    private Event event;
    private String brlTotalAmount;
    private String usdTotalAmount;
    private String status;

    @Data
    @Builder
    public static class Event {
        private String eventId;
        private String eventName;
        private String eventDateTime;
        private String logradouro;
        private String bairro;
        private String cidade;
        private String uf;
    }
}
