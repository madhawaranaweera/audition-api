package com.audition.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.audition.common.exception.SystemException;
import org.junit.jupiter.api.Test;

class SystemExceptionTest {

    @Test
    void testDefaultConstructor() {
        final SystemException exception = new SystemException();
        assertNotNull(exception);
    }

    @Test
    void testConstructorWithMessage() {
        final String message = "An error occurred";
        final SystemException exception = new SystemException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructorWithMessageAndErrorCode() {
        final String message = "An error occurred";
        final Integer errorCode = 400;
        final SystemException exception = new SystemException(message, errorCode);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructorWithMessageAndThrowable() {
        final String message = "An error occurred";
        final Throwable cause = new Exception("Cause of the exception");
        final SystemException exception = new SystemException(message, cause);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructorWithDetailTitleAndErrorCode() {
        final String detail = "Detailed exception message";
        final String title = "Custom Title";
        final Integer errorCode = 404;
        final SystemException exception = new SystemException(detail, title, errorCode);
        assertEquals(detail, exception.getMessage());
    }

    @Test
    void testConstructorWithDetailTitleAndThrowable() {
        final String detail = "Detailed error message";
        final String title = "Custom Title";
        final Throwable cause = new Exception("Cause of the error");
        final SystemException exception = new SystemException(detail, title, cause);
        assertEquals(detail, exception.getMessage());
    }

    @Test
    void testConstructorWithDetailErrorCodeAndThrowable() {
        final String detail = "Detailed error message";
        final Integer errorCode = 403;
        final Throwable cause = new Exception("Cause of the error");
        final SystemException exception = new SystemException(detail, errorCode, cause);
        assertEquals(detail, exception.getMessage());
    }

    @Test
    void testConstructorWithDetailTitleErrorCodeAndThrowable() {
        final String detail = "Detailed error message";
        final String title = "Custom Title";
        final Integer errorCode = 500;
        final Throwable cause = new Exception("Cause of the error");
        final SystemException exception = new SystemException(detail, title, errorCode, cause);
        assertEquals(detail, exception.getMessage());
    }
}
