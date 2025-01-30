package br.com.compass.ms_event_management.web.controller;

import br.com.compass.ms_event_management.domain.Event;
import br.com.compass.ms_event_management.exception.EventCannotBeDeletedException;
import br.com.compass.ms_event_management.exception.EventCannotBeUpdateException;
import br.com.compass.ms_event_management.exception.EventNotFoundException;
import br.com.compass.ms_event_management.service.EventService;
import br.com.compass.ms_event_management.web.dto.EventCreateDto;
import br.com.compass.ms_event_management.web.dto.EventResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import br.com.compass.ms_event_management.web.controller.EventController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateEvent_Success() throws Exception {
        EventCreateDto eventDto = new EventCreateDto();
        eventDto.setEventName("Show da Xuxa");
        eventDto.setDateTime(LocalDateTime.parse("2024-12-30T21:00:00"));
        eventDto.setCep("01001-000");

        EventResponseDto createdEvent = new EventResponseDto(
                "1",
                "Show da Xuxa",
                LocalDateTime.parse("2024-12-30T21:00:00"),
                "01020-000",
                "Rua da Fe",
                "Pelotas",
                "Sao Paulo",
                "SP"
        );

        when(eventService.createEvent(any(EventCreateDto.class))).thenReturn(createdEvent);

        mockMvc.perform(post("/br/com/compass/eventmanagement/v1/create-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Show da Xuxa"));

        verify(eventService, times(1)).createEvent(any(EventCreateDto.class));
    }

    @Test
    void testGetEventById_Success() throws Exception {
        EventResponseDto eventResponse = new EventResponseDto(
                "1",
                "Show da Xuxa",
                LocalDateTime.parse("2024-12-30T21:00:00"),
                "01020-000",
                "Rua da Fe",
                "Pelotas",
                "Sao Paulo",
                "SP"
        );

        when(eventService.getEventById("1")).thenReturn(eventResponse);

        mockMvc.perform(get("/br/com/compass/eventmanagement/v1/get-event/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.eventName").value("Show da Xuxa"));

        verify(eventService, times(1)).getEventById("1");
    }

    @Test
    void testGetEventById_NotFound() throws Exception {
        when(eventService.getEventById("4")).thenThrow(new EventNotFoundException("Evento não encontrado com ID: 4"));

        mockMvc.perform(get("/br/com/compass/eventmanagement/v1/get-event/4"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Evento não encontrado com ID: 4"));

        verify(eventService, times(1)).getEventById("4");
    }

    @Test
    void testGetAllEvents_Success() throws Exception {
        EventResponseDto event1 = new EventResponseDto(
                "1",
                "Show da Xuxa",
                LocalDateTime.parse("2024-12-30T21:00:00"),
                "01020-000",
                "Rua da Fe",
                "Pelotas",
                "Sao Paulo",
                "SP"
        );
        EventResponseDto event2 = new EventResponseDto(
                "2",
                "Show do Roberto Carlos",
                LocalDateTime.parse("2024-12-30T21:00:00"),
                "01020-000",
                "Rua da Fe",
                "Pelotas",
                "Sao Paulo",
                "SP"
        );

        List<EventResponseDto> events = List.of(event1, event2);

        when(eventService.getAllEvents()).thenReturn(events);

        mockMvc.perform(get("/br/com/compass/eventmanagement/v1/get-all-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].eventName").value("Show da Xuxa"))
                .andExpect(jsonPath("$[1].eventName").value("Show do Roberto Carlos"));

        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    void testGetAllEventsSorted_Success() throws Exception {
        EventResponseDto event1 = new EventResponseDto(
                "1",
                "Show do Roberto Carlos",
                LocalDateTime.parse("2024-12-30T21:00:00"),
                "01020-000",
                "Rua da Fe",
                "Pelotas",
                "Sao Paulo",
                "SP"
        );
        EventResponseDto event2 = new EventResponseDto(
                "2",
                "Show da Xuxa",
                LocalDateTime.parse("2024-12-30T21:00:00"),
                "01020-000",
                "Rua da Fe",
                "Pelotas",
                "Sao Paulo",
                "SP"
        );

        List<EventResponseDto> events = List.of(event1, event2);

        when(eventService.getAllEventsSorted()).thenReturn(events);

        mockMvc.perform(get("/br/com/compass/eventmanagement/v1/get-all-events/sorted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].eventName").value("Show do Roberto Carlos"))
                .andExpect(jsonPath("$[1].eventName").value("Show da Xuxa"));

        verify(eventService, times(1)).getAllEventsSorted();
    }

    @Test
    void testUpdateEvent_Success() throws Exception {
        EventCreateDto updatedEventDto = new EventCreateDto();
        updatedEventDto.setEventName("Show da Xuxa - Edição Especial");
        updatedEventDto.setDateTime(LocalDateTime.parse("2024-12-30T21:00:00"));
        updatedEventDto.setCep("01001-000");

        EventResponseDto updatedEvent = new EventResponseDto(
                "2",
                "Show da Xuxa - Edição Especial",
                LocalDateTime.parse("2024-12-30T21:00:00"),
                "01001-000",
                "Rua da Fe",
                "Pelotas",
                "Sao Paulo",
                "SP"
        );

        when(eventService.updateEvent(eq("2"), any(EventCreateDto.class))).thenReturn(updatedEvent);

        // Executando o teste
        mockMvc.perform(MockMvcRequestBuilders.put("/br/com/compass/eventmanagement/v1/update-event/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.eventName").value("Show da Xuxa - Edição Especial"));

        verify(eventService, times(1)).updateEvent(eq("2"), any(EventCreateDto.class));
    }
    @Test
    void testUpdateEvent_Conflict() throws Exception {
        EventCreateDto updatedEventDto = new EventCreateDto();
        updatedEventDto.setEventName("Show da Xuxa - Edição Especial");
        updatedEventDto.setDateTime(LocalDateTime.parse("2024-12-30T21:00:00"));
        updatedEventDto.setCep("01001-000");

        doThrow(new EventCannotBeUpdateException("O evento não pode ser atualizado porque possui ingressos vendidos."))
                .when(eventService).updateEvent(eq("11"), any(EventCreateDto.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/br/com/compass/eventmanagement/v1/update-event/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEventDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("O evento não pode ser atualizado porque possui ingressos vendidos."));

        verify(eventService, times(1)).updateEvent(eq("11"), any(EventCreateDto.class));
    }

    @Test
    void testDeleteEvent_Success() throws Exception {
        doNothing().when(eventService).deleteEvent("4");

        mockMvc.perform(delete("/br/com/compass/eventmanagement/v1/delete-event/4"))
                .andExpect(status().isNoContent());

        verify(eventService, times(1)).deleteEvent("4");
    }

    @Test
    void testDeleteEvent_Conflict() throws Exception {
        doThrow(new EventCannotBeDeletedException("O evento não pode ser deletado porque possui ingressos vendidos."))
                .when(eventService).deleteEvent("7");

        mockMvc.perform(delete("/br/com/compass/eventmanagement/v1/delete-event/7"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("O evento não pode ser deletado porque possui ingressos vendidos."));

        verify(eventService, times(1)).deleteEvent("7");
    }

    @Test
    void testDeleteEvent_NotFound() throws Exception {
        doThrow(new EventNotFoundException("Evento não encontrado com ID: 9"))
                .when(eventService).deleteEvent("9");

        mockMvc.perform(delete("/br/com/compass/eventmanagement/v1/delete-event/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Evento não encontrado com ID: 9"));

        verify(eventService, times(1)).deleteEvent("9");
    }
}

