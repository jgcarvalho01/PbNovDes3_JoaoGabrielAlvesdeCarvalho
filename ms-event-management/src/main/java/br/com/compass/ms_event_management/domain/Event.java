package br.com.compass.ms_event_management.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "events")
public class Event {
    @Id
    private String id;
    @NotNull(message = "O nome do evento é obrigatório")
    @Size(min = 3, max = 100, message = "O nome do evento deve ter entre 3 e 100 caracteres")
    private String eventName;
    @NotNull(message = "A data e hora do evento são obrigatórias")
    private LocalDateTime dateTime;
    @NotNull(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato 00000-000")
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
}
