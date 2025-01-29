package br.com.compass.ms_event_management.web.dto.mapper;

import br.com.compass.ms_event_management.domain.Event;
import br.com.compass.ms_event_management.web.dto.EventCreateDto;
import br.com.compass.ms_event_management.web.dto.EventResponseDto;

public class EventMapper {

    public static Event toEntity(EventCreateDto dto) {
        Event event = new Event();
        event.setEventName(dto.getEventName());
        event.setDateTime(dto.getDateTime());
        event.setCep(dto.getCep());
        return event;
    }

    public static EventResponseDto toDto(Event event) {
        return new EventResponseDto(
                event.getId(),
                event.getEventName(),
                event.getDateTime(),
                event.getCep(),
                event.getLogradouro(),
                event.getBairro(),
                event.getCidade(),
                event.getUf()
        );
    }
}
