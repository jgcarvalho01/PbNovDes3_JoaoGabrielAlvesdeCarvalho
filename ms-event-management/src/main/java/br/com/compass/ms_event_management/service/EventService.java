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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;
    private final ViaCepClient viaCepClient;
    private final TicketClient ticketClient;

    @Transactional
    public EventResponseDto createEvent(EventCreateDto dto) {
        log.info("Iniciando a criação de um novo evento: {}", dto.getEventName());
        ViaCepResponse address = viaCepClient.getAddressByCep(dto.getCep());

        Event event = EventMapper.toEntity(dto);
        event.setLogradouro(address.getLogradouro());
        event.setBairro(address.getBairro());
        event.setCidade(address.getLocalidade());
        event.setUf(address.getUf());

        Event savedEvent = eventRepository.save(event);
        log.info("Evento criado com sucesso: {}", savedEvent.getId());
        return EventMapper.toDto(savedEvent);
    }

    @Transactional(readOnly = true)
    public EventResponseDto getEventById(String id) {
        log.info("Buscando evento com ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Evento não encontrado com ID: {}", id);
                    return new EventNotFoundException("Evento não encontrado com ID: " + id);
                });
        return EventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        log.info("Buscando todos os eventos");
        return eventRepository.findAll()
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEventsSorted() {
        log.info("Buscando todos os eventos ordenados por nome");
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, "eventName"))
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponseDto updateEvent(String id, EventCreateDto dto) {
        log.info("Atualizando evento com ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Evento não encontrado para atualização. ID: {}", id);
                    return new EventNotFoundException("Evento não encontrado com ID: " + id);
                });
        ViaCepResponse address = viaCepClient.getAddressByCep(dto.getCep());

        event.setEventName(dto.getEventName());
        event.setDateTime(dto.getDateTime());
        event.setCep(dto.getCep());
        event.setLogradouro(address.getLogradouro());
        event.setBairro(address.getBairro());
        event.setCidade(address.getLocalidade());
        event.setUf(address.getUf());

        Map<String, Object> ticketResponse = ticketClient.checkTicketsByEvent(id);
        boolean hasTickets = (boolean) ticketResponse.get("hasTickets");

        if (hasTickets) {
            log.error("Evento não pode ser atualizado porque possui ingressos vendidos.");
            throw new EventCannotBeUpdateException("O evento não pode ser atualizado porque possui ingressos vendidos.");
        }

        Event updatedEvent = eventRepository.save(event);
        log.info("Evento atualizado com sucesso. ID: {}", updatedEvent.getId());
        return EventMapper.toDto(updatedEvent);
    }

    @Transactional
    public void deleteEvent(String id) {
        log.info("Deletando evento com ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Evento não encontrado para deleção. ID: {}", id);
                    return new EventNotFoundException("Evento não encontrado com ID: " + id);
                });

        Map<String, Object> ticketResponse = ticketClient.checkTicketsByEvent(id);
        boolean hasTickets = (boolean) ticketResponse.get("hasTickets");

        if (hasTickets) {
            log.error("Evento não pode ser deletado porque possui ingressos vendidos.");
            throw new EventCannotBeDeletedException("O evento não pode ser deletado porque possui ingressos vendidos.");
        }

        eventRepository.deleteById(id);
        log.info("Evento com ID {} deletado com sucesso", id);
    }
}
