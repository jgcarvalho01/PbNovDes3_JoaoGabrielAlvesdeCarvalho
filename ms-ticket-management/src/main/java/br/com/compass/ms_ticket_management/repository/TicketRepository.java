package br.com.compass.ms_ticket_management.repository;

import br.com.compass.ms_ticket_management.domain.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {
}
