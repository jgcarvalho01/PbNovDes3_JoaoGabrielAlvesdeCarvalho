package br.com.compass.ms_event_management.web.controller;

import br.com.compass.ms_event_management.service.EventService;
import br.com.compass.ms_event_management.web.dto.EventCreateDto;
import br.com.compass.ms_event_management.web.dto.EventResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Event Management", description = "APIs para gerenciar eventos")
@RestController
@RequestMapping("/br/com/compass/eventmanagement/v1")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Criar um novo evento", description = "Cria um evento com base no payload fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação no payload")
    })
    @PostMapping("/create-event")
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventCreateDto dto) {
        EventResponseDto createdEvent = eventService.createEvent(dto);
        return ResponseEntity.ok(createdEvent);
    }

    @Operation(summary = "Buscar evento por ID", description = "Retorna os detalhes de um evento pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @GetMapping("/get-event/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable String id) {
        EventResponseDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Listar todos os eventos", description = "Retorna uma lista com todos os eventos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso")
    })
    @GetMapping("/get-all-events")
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        List<EventResponseDto> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Listar eventos ordenados", description = "Retorna uma lista de eventos ordenada alfabeticamente pelo nome.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso")
    })
    @GetMapping("/get-all-events/sorted")
    public ResponseEntity<List<EventResponseDto>> getAllEventsSorted() {
        List<EventResponseDto> events = eventService.getAllEventsSorted();
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Atualizar um evento", description = "Atualiza as informações de um evento pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @PutMapping("/update-event/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable String id, @Valid @RequestBody EventCreateDto dto) {
        EventResponseDto updatedEvent = eventService.updateEvent(id, dto);
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(summary = "Deletar um evento", description = "Deleta um evento pelo ID. A operação será bloqueada se houver ingressos vinculados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @DeleteMapping("/delete-event/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
