package com.audition.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class IdValidator implements ConstraintValidator<ValidId, Object> {

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (value instanceof String) {
            return isValidId((String) value);
        } else if (value instanceof List) {
            final List<String> postIds = (List<String>) value;

            if (postIds.isEmpty()) {
                return true;
            }

            return postIds.stream().allMatch(this::isValidId);
        }
        return true;
    }

    private boolean isValidId(final String postId) {
        try {
            final long id = Long.parseLong(postId);
            return id > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
