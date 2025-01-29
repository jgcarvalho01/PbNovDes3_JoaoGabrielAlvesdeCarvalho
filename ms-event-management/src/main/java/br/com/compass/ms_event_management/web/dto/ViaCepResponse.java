package br.com.compass.ms_event_management.web.dto;

import lombok.Data;

@Data
public class ViaCepResponse {
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
}
