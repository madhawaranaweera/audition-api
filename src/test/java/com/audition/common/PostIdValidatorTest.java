package com.audition.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.audition.common.validator.PostIdValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PostIdValidatorTest {

    private PostIdValidator postIdValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        postIdValidator = new PostIdValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    void testValidSinglePostId() {
        assertTrue(postIdValidator.isValid("1", context));
        assertTrue(postIdValidator.isValid("123456", context));
    }

    @Test
    void testInvalidSinglePostId() {
        assertFalse(postIdValidator.isValid("0", context));
        assertFalse(postIdValidator.isValid("-1", context));
        assertFalse(postIdValidator.isValid("abc", context));
        assertFalse(postIdValidator.isValid("", context));
    }

    @Test
    void testValidPostIdList() {
        final List<String> validPostIds = Arrays.asList("1", "2", "3");
        assertTrue(postIdValidator.isValid(validPostIds, context));

        final List<String> emptyPostIds = Collections.emptyList();
        assertTrue(postIdValidator.isValid(emptyPostIds, context));
    }

    @Test
    void testInvalidPostIdList() {
        final List<String> mixedPostIds = Arrays.asList("1", "2", "invalid", "4");
        assertFalse(postIdValidator.isValid(mixedPostIds, context));

        final List<String> allInvalidPostIds = Arrays.asList("-1", "0", "abc");
        assertFalse(postIdValidator.isValid(allInvalidPostIds, context));
    }
}
