package br.com.compass.ms_ticket_management.web.controller;

import br.com.compass.ms_ticket_management.domain.Ticket;
import br.com.compass.ms_ticket_management.repository.TicketRepository;
import br.com.compass.ms_ticket_management.service.TicketService;
import br.com.compass.ms_ticket_management.web.dto.TicketResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/br/com/compass/ticketmanagement/v1")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    @PostMapping("/create-ticket")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody Ticket ticket) {
        TicketResponse createdTicket = ticketService.createTicket(ticket);
        return ResponseEntity.ok(createdTicket);
    }

    @GetMapping("/get-ticket/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable String id) {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/update-ticket/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable String id, @RequestBody Ticket updatedTicket) {
        Ticket ticket = ticketService.updateTicket(id, updatedTicket);
        return ResponseEntity.ok(ticket);
    }
}
