package com.audition.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.audition.common.validator.UserIdValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class UserIdValidatorTest {

    private UserIdValidator userIdValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        userIdValidator = new UserIdValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    void testValidSingleUserId() {
        assertTrue(userIdValidator.isValid("1", context));
        assertTrue(userIdValidator.isValid("123456", context));
    }

    @Test
    void testInvalidSingleUserId() {
        assertFalse(userIdValidator.isValid("0", context));
        assertFalse(userIdValidator.isValid("-1", context));
        assertFalse(userIdValidator.isValid("abc", context));
        assertFalse(userIdValidator.isValid("", context));
    }

    @Test
    void testValidUserIdList() {
        final List<String> validUserIds = Arrays.asList("1", "2", "3");
        assertTrue(userIdValidator.isValid(validUserIds, context));

        final List<String> emptyUserIds = Collections.emptyList();
        assertTrue(userIdValidator.isValid(emptyUserIds, context));
    }

    @Test
    void testInvalidUserIdList() {
        final List<String> mixedUserIds = Arrays.asList("1", "2", "invalid", "4");
        assertFalse(userIdValidator.isValid(mixedUserIds, context));

        final List<String> allInvalidUserIds = Arrays.asList("-1", "0", "abc");
        assertFalse(userIdValidator.isValid(allInvalidUserIds, context));
    }
}
