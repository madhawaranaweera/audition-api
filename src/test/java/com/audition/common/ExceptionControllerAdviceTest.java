package com.audition.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.web.advice.ExceptionControllerAdvice;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.HttpClientErrorException;

@SuppressWarnings("PMD.UnusedPrivateField")
class ExceptionControllerAdviceTest {

    @InjectMocks
    private ExceptionControllerAdvice exceptionControllerAdvice;

    @Mock
    private AuditionLogger logger;

    @Mock
    private Logger slf4jLogger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleHttpClientException() {
        final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleHttpClientException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
    }

    @Test
    void testHandleMainException() {
        final Exception exception = new Exception("General error occurred");
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleMainException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
    }

    @Test
    void testHandleConstraintViolationException() {
        final ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", null);
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleConstraintViolationException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
    }

    @Test
    void testHandleHttpRequestMethodNotSupportedException() {
        final HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("GET");
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleMainException(exception);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), problemDetail.getStatus());
    }

    @Test
    void testHandleSystemException() {
        final SystemException systemException = new SystemException("System error occurred", "System Error", 500);
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleSystemException(systemException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
    }
}
