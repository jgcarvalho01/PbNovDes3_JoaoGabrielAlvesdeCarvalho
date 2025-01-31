package br.com.compass.ms_event_management.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "ms-ticket-management", url = "${ticket-management.url}")
public interface TicketClient {
    @GetMapping("/check-tickets-by-event/{eventId}")
    Map<String, Object> checkTicketsByEvent(@PathVariable("eventId") String eventId);
}
