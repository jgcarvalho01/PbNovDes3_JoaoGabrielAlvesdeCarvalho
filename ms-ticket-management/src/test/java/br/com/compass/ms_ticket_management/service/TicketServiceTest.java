package br.com.compass.ms_ticket_management.service;

import br.com.compass.ms_ticket_management.domain.Ticket;
import br.com.compass.ms_ticket_management.exception.TicketNotFoundException;
import br.com.compass.ms_ticket_management.repository.TicketRepository;
import br.com.compass.ms_ticket_management.web.dto.EventResponse;
import br.com.compass.ms_ticket_management.web.dto.TicketResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TicketServiceTest {
    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventClient eventClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTicket_Success() {
        Ticket ticket = new Ticket();
        ticket.setEventId("666");
        ticket.setCustomerName("João");
        ticket.setCpf("12345678900");
        ticket.setBrlAmount(100.0);
        ticket.setUsdAmount(100.0);

        EventResponse eventResponse = EventResponse.builder()
                .id("666")
                .eventName("Show da Banda Quarto 666")
                .dateTime("2024-12-30T21:00:00")
                .logradouro("Rua Fulano")
                .bairro("Bairro Tal")
                .cidade("Cidade Catumbi")
                .uf("CI")
                .build();

        Ticket savedTicket = new Ticket();
        savedTicket.setTicketId("789");
        savedTicket.setEventId(ticket.getEventId());
        savedTicket.setCustomerName(ticket.getCustomerName());
        savedTicket.setCpf(ticket.getCpf());
        savedTicket.setBrlAmount(ticket.getBrlAmount());
        savedTicket.setUsdAmount(ticket.getUsdAmount());
        savedTicket.setStatus("Concluído");

        when(sequenceGeneratorService.generateSequence("tickets_sequence")).thenReturn(789L);
        when(eventClient.getEventById("666")).thenReturn(eventResponse);
        when(ticketRepository.save(ticket)).thenReturn(savedTicket);

        TicketResponse response = ticketService.createTicket(ticket);

        assertNotNull(response);
        assertEquals("789", response.getTicketId());
        assertEquals("João", response.getCustomerName());
        assertEquals("12345678900", response.getCpf());
        assertEquals("R$ 100,00", response.getBrlTotalAmount());
        assertEquals("$100.00", response.getUsdTotalAmount());

        String expectedMessage = String.format(
                "🎉 Ei %s, seu ingresso está confirmado! 🎟️\n" +
                        "Detalhes:\n" +
                        "🎤 Evento: %s\n" +
                        "📅 Data: %s\n" +
                        "📍 Local: %s, %s - %s/%s\n" +
                        "💰 Valor: %s (ou %s)\n\n" +
                        "Aproveite o show e não esqueça de contar pros amigos! 🤩",
                ticket.getCustomerName(),
                eventResponse.getEventName(),
                eventResponse.getDateTime(),
                eventResponse.getLogradouro(),
                eventResponse.getBairro(),
                eventResponse.getCidade(),
                eventResponse.getUf(),
                "R$ 100,00",
                "$100.00"
        );
        verify(rabbitTemplate, times(1)).convertAndSend("ticket-queue", expectedMessage);

        verify(eventClient, times(1)).getEventById("666");
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testGetTicketById_Success() {
        Ticket mockTicket = new Ticket();
        mockTicket.setTicketId("7");
        mockTicket.setCustomerName("João");
        when(ticketRepository.findById("7")).thenReturn(Optional.of(mockTicket));

        Ticket ticket = ticketService.getTicketById("7");

        assertNotNull(ticket);
        assertEquals("7", ticket.getTicketId());
        assertEquals("João", ticket.getCustomerName());
    }

    @Test
    void testGetTicketById_NotFound() {
        when(ticketRepository.findById("2")).thenReturn(Optional.empty());

        Exception exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.getTicketById("2");
        });

        assertEquals("Ticket não encontrado com ID: 2", exception.getMessage());
    }

    @Test
    void testUpdateTicket_Success() {
        String ticketId = "123";
        Ticket existingTicket = new Ticket();
        existingTicket.setTicketId(ticketId);
        existingTicket.setCustomerName("João");
        existingTicket.setCpf("12345678900");
        existingTicket.setCustomerMail("joao@email.com");
        existingTicket.setBrlAmount(100.0);
        existingTicket.setUsdAmount(20.0);

        Ticket updatedTicket = new Ticket();
        updatedTicket.setCustomerName("Maria");
        updatedTicket.setCpf("98765432100");
        updatedTicket.setCustomerMail("maria@email.com");
        updatedTicket.setBrlAmount(150.0);
        updatedTicket.setUsdAmount(30.0);

        Ticket savedTicket = new Ticket();
        savedTicket.setTicketId(ticketId);
        savedTicket.setCustomerName("Maria");
        savedTicket.setCpf("98765432100");
        savedTicket.setCustomerMail("maria@email.com");
        savedTicket.setBrlAmount(150.0);
        savedTicket.setUsdAmount(30.0);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(existingTicket));
        when(ticketRepository.save(existingTicket)).thenReturn(savedTicket);

        Ticket result = ticketService.updateTicket(ticketId, updatedTicket);

        assertNotNull(result);
        assertEquals("Maria", result.getCustomerName());
        assertEquals("98765432100", result.getCpf());
        assertEquals("maria@email.com", result.getCustomerMail());
        assertEquals(150.0, result.getBrlAmount());
        assertEquals(30.0, result.getUsdAmount());

        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, times(1)).save(existingTicket);
    }

    @Test
    void testUpdateTicket_NotFound() {
        String ticketId = "3";
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TicketNotFoundException.class, () -> {
            Ticket updatedTicket = new Ticket();
            ticketService.updateTicket(ticketId, updatedTicket);
        });

        assertEquals("Ticket não encontrado com ID: " + ticketId, exception.getMessage());

        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testCancelTicket() {
        Ticket mockTicket = new Ticket();
        mockTicket.setTicketId("4");
        mockTicket.setStatus("ATIVO");

        when(ticketRepository.findById("4")).thenReturn(Optional.of(mockTicket));

        ticketService.cancelTicket("4");

        assertEquals("Cancelado", mockTicket.getStatus());
        verify(ticketRepository, times(1)).save(mockTicket);
    }

    @Test
    void testCancelTicket_NotFound() {
        String ticketId = "5";
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.cancelTicket(ticketId);
        });

        assertEquals("Ticket não encontrado com ID: " + ticketId, exception.getMessage());

        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testCheckTicketsByEvent_WithTickets() {
        String eventId = "10";
        when(ticketRepository.existsByEventId(eventId)).thenReturn(true);

        Map<String, Object> response = ticketService.checkTicketsByEvent(eventId);

        assertEquals(2, response.size());
        assertEquals(eventId, response.get("eventId"));
        assertEquals(true, response.get("hasTickets"));

        verify(ticketRepository).existsByEventId(eventId);
    }
}
