package br.com.compass.ms_ticket_management.web.controller;

import br.com.compass.ms_ticket_management.domain.Ticket;
import br.com.compass.ms_ticket_management.exception.TicketNotFoundException;
import br.com.compass.ms_ticket_management.repository.TicketRepository;
import br.com.compass.ms_ticket_management.service.TicketService;
import br.com.compass.ms_ticket_management.web.dto.TicketResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
public class TicketControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private TicketRepository ticketRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTicket_Success() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setEventId("123");
        ticket.setCustomerName("João");
        ticket.setCpf("12345678900");
        ticket.setCustomerMail("joao@email.com");
        ticket.setBrlAmount(100.0);
        ticket.setUsdAmount(20.0);

        TicketResponse response = TicketResponse.builder()
                .ticketId("1")
                .customerName("João")
                .cpf("12345678900")
                .status("Concluído")
                .build();

        when(ticketService.createTicket(any(Ticket.class))).thenReturn(response);

        mockMvc.perform(post("/br/com/compass/ticketmanagement/v1/create-ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value("1"))
                .andExpect(jsonPath("$.customerName").value("João"))
                .andExpect(jsonPath("$.status").value("Concluído"));

        verify(ticketService, times(1)).createTicket(any(Ticket.class));
    }


    @Test
    void testGetTicketById_Success() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setTicketId("1");
        ticket.setCustomerName("João");
        ticket.setCpf("12345678900");

        when(ticketService.getTicketById("1")).thenReturn(ticket);

        mockMvc.perform(get("/br/com/compass/ticketmanagement/v1/get-ticket/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value("1"))
                .andExpect(jsonPath("$.customerName").value("João"));
    }

    @Test
    void testGetTicketById_NotFound() throws Exception {
        when(ticketService.getTicketById("2")).thenThrow(new TicketNotFoundException("Ticket não encontrado com ID: 2"));

        mockMvc.perform(get("/br/com/compass/ticketmanagement/v1/get-ticket/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket não encontrado com ID: 2"));
    }

    @Test
    void testUpdateTicket_Success() throws Exception {
        Ticket updatedTicket = new Ticket();
        updatedTicket.setCustomerName("Maria");
        updatedTicket.setCpf("98765432100");

        Ticket savedTicket = new Ticket();
        savedTicket.setTicketId("3");
        savedTicket.setCustomerName("Maria Atualizado");
        savedTicket.setCpf("98765432100");

        when(ticketService.updateTicket(eq("3"), any(Ticket.class))).thenReturn(savedTicket);

        mockMvc.perform(put("/br/com/compass/ticketmanagement/v1/update-ticket/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTicket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value("3"))
                .andExpect(jsonPath("$.customerName").value("Maria Atualizado"));
    }

    @Test
    void testUpdateTicket_NotFound() throws Exception {
        when(ticketService.updateTicket(eq("5"), any(Ticket.class)))
                .thenThrow(new TicketNotFoundException("Ticket não encontrado com ID: 5"));

        mockMvc.perform(put("/br/com/compass/ticketmanagement/v1/update-ticket/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Ticket())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket não encontrado com ID: 5"));
    }

    @Test
    void testCancelTicket_Success() throws Exception {
        doNothing().when(ticketService).cancelTicket("6");

        mockMvc.perform(delete("/br/com/compass/ticketmanagement/v1/cancel-ticket/6"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCancelTicket_NotFound() throws Exception {
        doThrow(new TicketNotFoundException("Ticket não encontrado com ID: 7"))
                .when(ticketService).cancelTicket("7");

        mockMvc.perform(delete("/br/com/compass/ticketmanagement/v1/cancel-ticket/7"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket não encontrado com ID: 7"));
    }

    @Test
    void testCheckTicketsByEvent_Success() throws Exception {
        String eventId = "666";
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("tickets", 9);

        when(ticketService.checkTicketsByEvent(eventId)).thenReturn(mockResponse);

        mockMvc.perform(get("/br/com/compass/ticketmanagement/v1/check-tickets-by-event/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tickets").value(9));
    }
}
