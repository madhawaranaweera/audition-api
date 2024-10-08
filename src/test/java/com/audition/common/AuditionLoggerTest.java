package com.audition.common;

import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.logging.AuditionLogger;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;

class AuditionLoggerTest {

    private AuditionLogger auditionLogger;
    private Logger logger;

    @BeforeEach
    void setUp() {
        auditionLogger = new AuditionLogger();
        logger = mock(Logger.class);
    }

    @Test
    void testInfoWithMessage() {
        when(logger.isInfoEnabled()).thenReturn(true);
        auditionLogger.info(logger, "Test message");
        verify(logger, times(1)).info("Test message");
    }

    @Test
    void testInfoWithMessageAndObject() {
        when(logger.isInfoEnabled()).thenReturn(true);
        auditionLogger.info(logger, "Test message {}", "TestObject");
        verify(logger, times(1)).info("Test message {}", "TestObject");
    }

    @Test
    void testDebugWithMessageAndObject() {
        when(logger.isDebugEnabled()).thenReturn(true);
        auditionLogger.debug(logger, "Debug message {}", "DebugObject");
        verify(logger, times(1)).debug("Debug message {}", "DebugObject");
    }

    @Test
    void testWarnWithMessageAndObject() {
        when(logger.isWarnEnabled()).thenReturn(true);
        auditionLogger.warn(logger, "Warn message {}", "WarnObject");
        verify(logger, times(1)).warn("Warn message {}", "WarnObject");
    }

    @Test
    void testErrorWithMessage() {
        when(logger.isErrorEnabled()).thenReturn(true);
        auditionLogger.error(logger, "Error message");
        verify(logger, times(1)).error("Error message");
    }

    @Test
    void testLogErrorWithException() {
        final Exception e = new Exception("Test exception");
        when(logger.isErrorEnabled()).thenReturn(true);
        auditionLogger.logErrorWithException(logger, "Error with exception", e);
        verify(logger, times(1)).error("Error with exception", e);
    }

    @Test
    void testLogHttpStatusCodeError() {
        when(logger.isErrorEnabled()).thenReturn(true);
        auditionLogger.logHttpStatusCodeError(logger, "Not Found", 404);
        verify(logger, times(1)).error(contains("Error Code: 404"));
    }

    @Test
    void testLogStandardProblemDetail() {
        final ProblemDetail problemDetail = mock(ProblemDetail.class);
        when(problemDetail.getType()).thenReturn(URI.create("https://example.com/problem"));
        when(problemDetail.getTitle()).thenReturn("Test Title");
        when(problemDetail.getStatus()).thenReturn(404);
        when(problemDetail.getDetail()).thenReturn("Test Detail");
        when(problemDetail.getInstance()).thenReturn(URI.create("https://example.com/instance"));

        final Map<String, Object> properties = new ConcurrentHashMap<>();
        properties.put("additionalInfo", "Some info");
        when(problemDetail.getProperties()).thenReturn(properties);

        final Exception exception = new RuntimeException("Test Exception");

        when(logger.isErrorEnabled()).thenReturn(true);

        auditionLogger.logStandardProblemDetail(logger, problemDetail, exception);

        final ArgumentCaptor<String> logMessageCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(logger).error(logMessageCaptor.capture(), exceptionCaptor.capture());

        final String expectedMessage = "Type: https://example.com/problem, Title: Test Title, Status: 404, Detail: Test Detail, Instance: https://example.com/instance, Properties: {additionalInfo=Some info}";
        final String actualMessage = logMessageCaptor.getValue();
        assert actualMessage.equals(expectedMessage);

        final Exception loggedException = exceptionCaptor.getValue();
        assert loggedException.equals(exception);
    }
}
