package br.com.compass.ms_event_management.service;

import br.com.compass.ms_event_management.domain.Event;
import br.com.compass.ms_event_management.exception.EventCannotBeDeletedException;
import br.com.compass.ms_event_management.exception.EventCannotBeUpdateException;
import br.com.compass.ms_event_management.exception.EventNotFoundException;
import br.com.compass.ms_event_management.repository.EventRepository;
import br.com.compass.ms_event_management.web.dto.EventCreateDto;
import br.com.compass.ms_event_management.web.dto.EventResponseDto;
import br.com.compass.ms_event_management.web.dto.ViaCepResponse;
import br.com.compass.ms_event_management.web.dto.mapper.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServiceTest {
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ViaCepClient viaCepClient;

    @Mock
    private TicketClient ticketClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEvent_Success() {
        EventCreateDto eventCreateDto = new EventCreateDto();
        eventCreateDto.setEventName("Show da Xuxa");
        eventCreateDto.setDateTime(LocalDateTime.now());
        eventCreateDto.setCep("01020-000");

        ViaCepResponse viaCepResponse = new ViaCepResponse();
        viaCepResponse.setLogradouro("Rua Fulano");
        viaCepResponse.setBairro("Bairro Tal");
        viaCepResponse.setLocalidade("Cidade FloriTest");
        viaCepResponse.setUf("FT");

        when(viaCepClient.getAddressByCep(eventCreateDto.getCep())).thenReturn(viaCepResponse);

        Event event = EventMapper.toEntity(eventCreateDto);
        event.setLogradouro("Rua Fulano");
        event.setBairro("Bairro Tal");
        event.setCidade("Cidade FloriTest");
        event.setUf("FT");
        event.setId("generated-id");

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventResponseDto createdEvent = eventService.createEvent(eventCreateDto);

        assertNotNull(createdEvent);
        assertEquals("Show da Xuxa", createdEvent.getEventName());
        assertEquals("Rua Fulano", createdEvent.getLogradouro());
        assertEquals("Cidade FloriTest", createdEvent.getCidade());
        assertEquals("FT", createdEvent.getUf());

        verify(viaCepClient, times(1)).getAddressByCep(eventCreateDto.getCep());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testGetEventById_Success() {
        Event mockEvent = new Event();
        mockEvent.setId("111");
        mockEvent.setEventName("Show de System of a Down");

        when(eventRepository.findById("111")).thenReturn(Optional.of(mockEvent));

        EventResponseDto event = eventService.getEventById("111");

        assertNotNull(event);
        assertEquals("111", event.getId());
        assertEquals("Show de System of a Down", event.getEventName());

        verify(eventRepository, times(1)).findById("111");
    }

    @Test
    void testGetEventById_NotFound() {
        when(eventRepository.findById("6")).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventById("6");
        });

        assertEquals("Evento não encontrado com ID: 6", exception.getMessage());

        verify(eventRepository, times(1)).findById("6");
    }

    @Test
    void testGetAllEvents_Success() {
        Event event1 = new Event();
        event1.setId("1");
        event1.setEventName("Show Ivete");

        Event event2 = new Event();
        event2.setId("2");
        event2.setEventName("Show Claudia Leite");

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<EventResponseDto> events = eventService.getAllEvents();

        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals("Show Ivete", events.get(0).getEventName());
        assertEquals("Show Claudia Leite", events.get(1).getEventName());

        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEvents_Empty() {
        when(eventRepository.findAll()).thenReturn(List.of());

        List<EventResponseDto> events = eventService.getAllEvents();

        assertNotNull(events);
        assertTrue(events.isEmpty());

        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEventsSorted() {
        eventService.getAllEventsSorted();

        verify(eventRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "eventName"));
    }

    @Test
    void testUpdateEvent_Success() {
        Event existingEvent = new Event();
        existingEvent.setId("1");
        existingEvent.setEventName("Evento Original");
        existingEvent.setDateTime(LocalDateTime.parse("2025-01-17T19:30:00"));
        existingEvent.setCep("01020-000");

        EventCreateDto updatedEventDto = new EventCreateDto();
        updatedEventDto.setEventName("Evento Atualizado");
        updatedEventDto.setDateTime(LocalDateTime.parse("2025-01-18T22:00:00"));
        updatedEventDto.setCep("01020-000");

        ViaCepResponse viaCepResponse = new ViaCepResponse();
        viaCepResponse.setLogradouro("Rua Atualizada");
        viaCepResponse.setBairro("Bairro Atualizado");
        viaCepResponse.setLocalidade("Cidade Atualizada");
        viaCepResponse.setUf("SP");

        when(eventRepository.findById("1")).thenReturn(Optional.of(existingEvent));
        when(viaCepClient.getAddressByCep("01020-000")).thenReturn(viaCepResponse);
        when(ticketClient.checkTicketsByEvent("1")).thenReturn(Map.of("hasTickets", false));
        when(eventRepository.save(any(Event.class))).thenReturn(existingEvent);

        EventResponseDto result = eventService.updateEvent("1", updatedEventDto);

        assertNotNull(result);
        assertEquals("Evento Atualizado", result.getEventName());
        assertEquals(LocalDateTime.parse("2025-01-18T22:00:00"), result.getDateTime());
        assertEquals("Rua Atualizada", result.getLogradouro());

        verify(eventRepository, times(1)).findById("1");
        verify(viaCepClient, times(1)).getAddressByCep("01020-000");
        verify(ticketClient, times(1)).checkTicketsByEvent("1");
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdateEvent_EventNotFound() {
        when(eventRepository.findById("2")).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.updateEvent("2", new EventCreateDto());
        });

        assertEquals("Evento não encontrado com ID: 2", exception.getMessage());

        verify(eventRepository, times(1)).findById("2");
    }

    @Test
    void testUpdateEvent_TicketsExist() {
        Event existingEvent = new Event();
        existingEvent.setId("666");
        existingEvent.setEventName("Show do Slipknot");
        existingEvent.setCep("01020-000");

        EventCreateDto updatedEvent = new EventCreateDto();
        updatedEvent.setEventName("Show do Slipknot - 25 anos");
        updatedEvent.setCep("01002-000");

        ViaCepResponse viaCepResponse = new ViaCepResponse();
        viaCepResponse.setLogradouro("Rua Dark");
        viaCepResponse.setBairro("Bairro Sad");
        viaCepResponse.setLocalidade("Cidade Des Moines");
        viaCepResponse.setUf("IA");

        when(eventRepository.findById("666")).thenReturn(Optional.of(existingEvent));
        when(viaCepClient.getAddressByCep("01002-000")).thenReturn(viaCepResponse);
        when(ticketClient.checkTicketsByEvent("666")).thenReturn(Map.of("hasTickets", true));

        Exception exception = assertThrows(EventCannotBeUpdateException.class, () -> {
            eventService.updateEvent("666", updatedEvent);
        });

        assertEquals("O evento não pode ser atualizado porque possui ingressos vendidos.", exception.getMessage());

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testDeleteEvent_TicketsExist() {
        Event event = new Event();
        event.setId("22");
        event.setEventName("Show CPM 22");

        when(eventRepository.findById("22")).thenReturn(Optional.of(event));
        when(ticketClient.checkTicketsByEvent("22")).thenReturn(Map.of("hasTickets", true));

        Exception exception = assertThrows(EventCannotBeDeletedException.class, () -> {
            eventService.deleteEvent("22");
        });

        assertEquals("O evento não pode ser deletado porque possui ingressos vendidos.", exception.getMessage());

        verify(eventRepository, times(1)).findById("22");
        verify(ticketClient, times(1)).checkTicketsByEvent("22");
        verify(eventRepository, never()).deleteById("22");
    }

    @Test
    void testDeleteEvent_Success() {
        Event event = new Event();
        event.setId("1");
        event.setEventName("Evento para Deletar");

        when(eventRepository.findById("1")).thenReturn(Optional.of(event));
        when(ticketClient.checkTicketsByEvent("1")).thenReturn(Map.of("hasTickets", false));

        eventService.deleteEvent("1");

        verify(eventRepository, times(1)).findById("1");
        verify(ticketClient, times(1)).checkTicketsByEvent("1");
        verify(eventRepository, times(1)).deleteById("1");
    }

    @Test
    void testDeleteEvent_EventNotFound() {
        when(eventRepository.findById("2")).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.deleteEvent("2");
        });
        assertEquals("Evento não encontrado com ID: 2", exception.getMessage());

        verify(eventRepository, times(1)).findById("2");
        verify(ticketClient, never()).checkTicketsByEvent(any());
        verify(eventRepository, never()).deleteById(any());
    }

}
