package com.audition.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class PostIdValidator implements ConstraintValidator<ValidPostId, Object> {

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (value instanceof String) {
            return isValidPostId((String) value);
        } else if (value instanceof List) {
            final List<String> postIds = (List<String>) value;

            if (postIds.isEmpty()) {
                return true;
            }

            return postIds.stream().allMatch(this::isValidPostId);
        }
        return true;
    }

    private boolean isValidPostId(final String postId) {
        try {
            final long id = Long.parseLong(postId);
            return id > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
