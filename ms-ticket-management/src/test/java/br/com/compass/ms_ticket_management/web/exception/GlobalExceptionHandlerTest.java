package br.com.compass.ms_ticket_management.web.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleValidationException() {
        ConstraintViolationException ex = new ConstraintViolationException("Campo obrigatório ausente", null);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro de validação no payload: Campo obrigatório ausente", response.getBody().get("error"));
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Erro inesperado!");

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro inesperado!", response.getBody().get("error"));
    }
}
