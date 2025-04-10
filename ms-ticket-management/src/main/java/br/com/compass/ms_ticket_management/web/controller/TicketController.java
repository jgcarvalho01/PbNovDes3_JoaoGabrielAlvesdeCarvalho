package br.com.compass.ms_ticket_management.web.controller;

import br.com.compass.ms_ticket_management.domain.Ticket;
import br.com.compass.ms_ticket_management.service.TicketService;
import br.com.compass.ms_ticket_management.web.dto.TicketResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Tag(name = "Ticket Management", description = "APIs para gerenciar ingressos")
@RestController
@RequestMapping("/br/com/compass/ticketmanagement/v1")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Criar um novo ingresso", description = "Cria um ingresso com base no evento fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingresso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação no payload"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @PostMapping("/create-ticket")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody Ticket ticket) {
        log.info("Recebendo requisição para criar um novo ticket para o evento: {}", ticket.getEventId());
        TicketResponse createdTicket = ticketService.createTicket(ticket);
        log.info("Ticket criado com sucesso. ID do ticket: {}", createdTicket.getTicketId());
        return ResponseEntity.ok(createdTicket);
    }

    @Operation(summary = "Buscar um ingresso pelo ID", description = "Retorna os detalhes de um ingresso pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingresso encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ingresso não encontrado")
    })
    @GetMapping("/get-ticket/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable String id) {
        log.info("Recebendo requisição para buscar o ticket com ID: {}", id);
        Ticket ticket = ticketService.getTicketById(id);
        log.info("Ticket encontrado: {}", ticket.getTicketId());
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "Atualizar um ingresso", description = "Atualiza as informações de um ingresso pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingresso atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ingresso não encontrado")
    })
    @PutMapping("/update-ticket/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable String id, @RequestBody Ticket updatedTicket) {
        log.info("Recebendo requisição para atualizar o ticket com ID: {}", id);
        Ticket ticket = ticketService.updateTicket(id, updatedTicket);
        log.info("Ticket atualizado com sucesso. ID do ticket: {}", ticket.getTicketId());
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "Cancelar um ingresso", description = "Realiza o soft-delete de um ingresso, alterando o status para INATIVO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ingresso cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ingresso não encontrado")
    })
    @DeleteMapping("/cancel-ticket/{id}")
    public ResponseEntity<Void> cancelTicket(@PathVariable String id) {
        log.info("Recebendo requisição para cancelar o ticket com ID: {}", id);
        ticketService.cancelTicket(id);
        log.info("Ticket com ID {} cancelado com sucesso.", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verificar ingressos vinculados a um evento", description = "Verifica se existem ingressos associados a um evento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    })
    @GetMapping("/check-tickets-by-event/{eventId}")
    public ResponseEntity<Map<String, Object>> checkTicketsByEvent(@PathVariable String eventId) {
        log.info("Recebendo requisição para verificar tickets vinculados ao evento com ID: {}", eventId);
        Map<String, Object> response = ticketService.checkTicketsByEvent(eventId);
        log.info("Verificação concluída para o evento: {}.", eventId);
        return ResponseEntity.ok(response);
    }

}
