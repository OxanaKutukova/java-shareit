package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {
    private final ErrorResponse errorResponse = new ErrorResponse("error");

    @Test
    public void getError() {
        assertEquals("error", errorResponse.getError());
    }

}