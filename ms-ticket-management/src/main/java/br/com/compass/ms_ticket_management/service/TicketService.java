package br.com.compass.ms_ticket_management.service;

import br.com.compass.ms_ticket_management.domain.Ticket;
import br.com.compass.ms_ticket_management.exception.TicketNotFoundException;
import br.com.compass.ms_ticket_management.repository.TicketRepository;
import br.com.compass.ms_ticket_management.web.dto.EventResponse;
import br.com.compass.ms_ticket_management.web.dto.TicketResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventClient eventClient;
    private final RabbitTemplate rabbitTemplate;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final String queueName = "ticket-queue";

    public TicketResponse createTicket(Ticket ticket) {
        log.info("Iniciando a criação de um novo ticket para o evento {}", ticket.getEventId());
        String ticketId = String.valueOf(sequenceGeneratorService.generateSequence("ticket_sequence"));
        ticket.setTicketId(ticketId);

        EventResponse event = eventClient.getEventById(ticket.getEventId());
        log.info("Evento encontrado para criação do ticket: {}", event.getEventName());

        ticket.setEventName(event.getEventName());
        ticket.setStatus("Concluído");

        Ticket savedTicket = ticketRepository.save(ticket);

        String brlAmountFormatted = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
                .format(ticket.getBrlAmount());
        String usdAmountFormatted = NumberFormat.getCurrencyInstance(Locale.US)
                .format(ticket.getUsdAmount());

        String message = buildPersonalizedMessage(ticket, event, brlAmountFormatted, usdAmountFormatted);
        log.info("Enviando mensagem para a fila {}: {}", queueName, message);
        rabbitTemplate.convertAndSend(queueName, message);
        log.info("Mensagem enviada com sucesso para a fila {}", queueName);

        log.info("Ticket criado com sucesso: {}", savedTicket.getTicketId());
        return TicketResponse.builder()
                .ticketId(savedTicket.getTicketId())
                .cpf(savedTicket.getCpf())
                .customerName(savedTicket.getCustomerName())
                .customerMail(savedTicket.getCustomerMail())
                .event(TicketResponse.Event.builder()
                        .eventId(event.getId())
                        .eventName(event.getEventName())
                        .eventDateTime(event.getDateTime())
                        .logradouro(event.getLogradouro())
                        .bairro(event.getBairro())
                        .cidade(event.getCidade())
                        .uf(event.getUf())
                        .build())
                .brlTotalAmount(brlAmountFormatted)
                .usdTotalAmount(usdAmountFormatted)
                .status(savedTicket.getStatus())
                .build();
    }

    private String buildPersonalizedMessage(Ticket ticket, EventResponse event, String brlAmount, String usdAmount) {
        return String.format(
                "🎉 Ei %s, seu ingresso está confirmado! 🎟️\n" +
                        "Detalhes:\n" +
                        "🎤 Evento: %s\n" +
                        "📅 Data: %s\n" +
                        "📍 Local: %s, %s - %s/%s\n" +
                        "💰 Valor: %s (ou %s)\n\n" +
                        "Aproveite o show e não esqueça de contar pros amigos! 🤩",
                ticket.getCustomerName(),
                event.getEventName(),
                event.getDateTime(),
                event.getLogradouro(),
                event.getBairro(),
                event.getCidade(),
                event.getUf(),
                brlAmount,
                usdAmount
        );
    }

    public Ticket getTicketById(String id) {
        log.info("Buscando ticket com ID: {}", id);
        return ticketRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ticket não encontrado com ID: {}", id);
                    return new TicketNotFoundException("Ticket não encontrado com ID: " + id);
                });
    }

    public Ticket updateTicket(String id, Ticket updatedTicket) {
        log.info("Atualizando ticket com ID: {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ticket não encontrado para atualização. ID: {}", id);
                    return new TicketNotFoundException("Ticket não encontrado com ID: " + id);
                });

        ticket.setCustomerName(updatedTicket.getCustomerName());
        ticket.setCpf(updatedTicket.getCpf());
        ticket.setCustomerMail(updatedTicket.getCustomerMail());
        ticket.setBrlAmount(updatedTicket.getBrlAmount());
        ticket.setUsdAmount(updatedTicket.getUsdAmount());

        log.info("Ticket com ID {} atualizado com sucesso", id);
        return ticketRepository.save(ticket);
    }

    public void cancelTicket(String id) {
        log.info("Iniciando o cancelamento do ticket com ID {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ticket não encontrado para cancelamento. ID: {}", id);
                    return new TicketNotFoundException("Ticket não encontrado com ID: " + id);
                });
        ticket.setStatus("Cancelado");
        ticketRepository.save(ticket);
        log.info("Ticket com ID {} foi cancelado com sucesso.", id);
    }

    public Map<String, Object> checkTicketsByEvent(String eventId) {
        log.info("Verificando tickets vinculados ao evento com ID: {}", eventId);
        boolean hasTickets = ticketRepository.existsByEventId(eventId);
        log.info("Verificação concluída para o evento {}. Tickets encontrados: {}", eventId, hasTickets);
        Map<String, Object> response = new HashMap<>();
        response.put("eventId", eventId);
        response.put("hasTickets", hasTickets);
        return response;
    }

}
