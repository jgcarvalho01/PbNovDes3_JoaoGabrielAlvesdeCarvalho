package br.com.compass.ms_ticket_management.service;

import br.com.compass.ms_ticket_management.domain.Ticket;
import br.com.compass.ms_ticket_management.exception.TicketNotFoundException;
import br.com.compass.ms_ticket_management.repository.TicketRepository;
import br.com.compass.ms_ticket_management.web.dto.EventResponse;
import br.com.compass.ms_ticket_management.web.dto.TicketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventClient eventClient;
    private final RabbitTemplate rabbitTemplate;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final String queueName = "ticket-queue";

    public TicketResponse createTicket(Ticket ticket) {
        String ticketId = String.valueOf(sequenceGeneratorService.generateSequence("ticket_sequence"));
        ticket.setTicketId(ticketId);

        EventResponse event = eventClient.getEventById(ticket.getEventId());

        ticket.setEventName(event.getEventName());
        ticket.setStatus("Concluído");

        Ticket savedTicket = ticketRepository.save(ticket);

        String brlAmountFormatted = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
                .format(ticket.getBrlAmount());
        String usdAmountFormatted = NumberFormat.getCurrencyInstance(Locale.US)
                .format(ticket.getUsdAmount());

        String message = buildPersonalizedMessage(ticket, event, brlAmountFormatted, usdAmountFormatted);
        rabbitTemplate.convertAndSend(queueName, message);

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
        return ticketRepository.findById(id)
                .orElseThrow(() -> {
                    return new TicketNotFoundException("Ticket não encontrado com ID: " + id);
                });
    }

    public Ticket updateTicket(String id, Ticket updatedTicket) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> {
                    return new TicketNotFoundException("Ticket não encontrado com ID: " + id);
                });

        ticket.setCustomerName(updatedTicket.getCustomerName());
        ticket.setCpf(updatedTicket.getCpf());
        ticket.setCustomerMail(updatedTicket.getCustomerMail());
        ticket.setBrlAmount(updatedTicket.getBrlAmount());
        ticket.setUsdAmount(updatedTicket.getUsdAmount());

        return ticketRepository.save(ticket);
    }

    public void cancelTicket(String id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> {
                    return new TicketNotFoundException("Ticket não encontrado com ID: " + id);
                });
        ticket.setStatus("Cancelado");
        ticketRepository.save(ticket);
    }

}
