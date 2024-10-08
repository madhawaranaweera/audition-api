package com.audition.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class UserIdValidator implements ConstraintValidator<ValidUserId, Object> {

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (value instanceof String) {
            return isValidUserId((String) value);
        } else if (value instanceof List) {
            final List<String> userIds = (List<String>) value;

            if (userIds.isEmpty()) {
                return true;
            }

            return userIds.stream().allMatch(this::isValidUserId);
        }
        return true;
    }

    private boolean isValidUserId(final String userIds) {
        try {
            final long id = Long.parseLong(userIds);
            return id > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
