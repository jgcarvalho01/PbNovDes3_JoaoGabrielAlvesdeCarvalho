package br.com.compass.ms_event_management.service;

import br.com.compass.ms_event_management.domain.Event;
import br.com.compass.ms_event_management.exception.EventNotFoundException;
import br.com.compass.ms_event_management.repository.EventRepository;
import br.com.compass.ms_event_management.web.dto.EventCreateDto;
import br.com.compass.ms_event_management.web.dto.EventResponseDto;
import br.com.compass.ms_event_management.web.dto.ViaCepResponse;
import br.com.compass.ms_event_management.web.dto.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;
    private final ViaCepClient viaCepClient;

    @Transactional
    public EventResponseDto createEvent(EventCreateDto dto) {
        ViaCepResponse address = viaCepClient.getAddressByCep(dto.getCep());

        Event event = EventMapper.toEntity(dto);
        event.setLogradouro(address.getLogradouro());
        event.setBairro(address.getBairro());
        event.setCidade(address.getLocalidade());
        event.setUf(address.getUf());

        Event savedEvent = eventRepository.save(event);
        return EventMapper.toDto(savedEvent);
    }

    @Transactional(readOnly = true)
    public EventResponseDto getEventById(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    return new EventNotFoundException("Evento não encontrado com ID: " + id);
                });
        return EventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEventsSorted() {
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, "eventName"))
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponseDto updateEvent(String id, EventCreateDto dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
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

        Event updatedEvent = eventRepository.save(event);
        return EventMapper.toDto(updatedEvent);
    }
}
