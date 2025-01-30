package br.com.compass.ms_ticket_management.config.database;

import br.com.compass.ms_ticket_management.service.SequenceGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class SequenceGeneratorServiceTest {
    @Mock
    private MongoOperations mongoOperations;

    @InjectMocks
    private SequenceGeneratorService sequenceGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateSequence_ReturnsIncrementedValue() {
        DatabaseSequence mockSequence = new DatabaseSequence();
        mockSequence.setId("ticket_sequence");
        mockSequence.setSequenceValue(10);

        when(mongoOperations.findAndModify(
                any(Query.class),
                any(Update.class),
                any(FindAndModifyOptions.class),
                eq(DatabaseSequence.class)
        )).thenReturn(mockSequence);

        long sequence = sequenceGeneratorService.generateSequence("ticket_sequence");

        assertEquals(10, sequence);
        verify(mongoOperations, times(1)).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(DatabaseSequence.class));
    }

    @Test
    void testGenerateSequence_WhenNull_ReturnsOne() {
        when(mongoOperations.findAndModify(
                any(Query.class),
                any(Update.class),
                any(FindAndModifyOptions.class),
                eq(DatabaseSequence.class)
        )).thenReturn(null);

        long sequence = sequenceGeneratorService.generateSequence("ticket_sequence");

        assertEquals(1, sequence);
        verify(mongoOperations, times(1)).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(DatabaseSequence.class));
    }
}
