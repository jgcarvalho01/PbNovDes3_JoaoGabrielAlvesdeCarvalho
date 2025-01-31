package br.com.compass.ms_ticket_management.service;

import br.com.compass.ms_ticket_management.web.dto.EventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-event-management", url = "${event-management.url}")
public interface EventClient {
    @GetMapping("/get-event/{id}")
    EventResponse getEventById(@PathVariable("id") String id);
}
