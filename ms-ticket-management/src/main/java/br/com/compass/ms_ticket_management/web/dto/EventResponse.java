package br.com.compass.ms_ticket_management.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventResponse {
    private String id;
    private String eventName;
    private String dateTime;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
}
