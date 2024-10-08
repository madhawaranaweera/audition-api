package com.audition.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.audition.common.validator.IdValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class IdValidatorTest {

    private IdValidator idValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        idValidator = new IdValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    void testValidSinglePostId() {
        assertTrue(idValidator.isValid("1", context));
        assertTrue(idValidator.isValid("123456", context));
    }

    @Test
    void testInvalidSinglePostId() {
        assertFalse(idValidator.isValid("0", context));
        assertFalse(idValidator.isValid("-1", context));
        assertFalse(idValidator.isValid("abc", context));
        assertFalse(idValidator.isValid("", context));
    }

    @Test
    void testValidPostIdList() {
        final List<String> validPostIds = Arrays.asList("1", "2", "3");
        assertTrue(idValidator.isValid(validPostIds, context));

        final List<String> emptyPostIds = Collections.emptyList();
        assertTrue(idValidator.isValid(emptyPostIds, context));
    }

    @Test
    void testInvalidPostIdList() {
        final List<String> mixedPostIds = Arrays.asList("1", "2", "invalid", "4");
        assertFalse(idValidator.isValid(mixedPostIds, context));

        final List<String> allInvalidPostIds = Arrays.asList("-1", "0", "abc");
        assertFalse(idValidator.isValid(allInvalidPostIds, context));
    }
}
