package com.audition.common.logging;

import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
public class AuditionLogger {

    public void info(final Logger logger, final String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    public void info(final Logger logger, final String message, final Object object) {
        if (logger.isInfoEnabled()) {
            logger.info(message, object);
        }
    }

    public void debug(final Logger logger, final String message, final Object object) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, object);
        }
    }

    public void warn(final Logger logger, final String message, final Object object) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, object);
        }
    }

    public void error(final Logger logger, final String message, final Object object) {
        if (logger.isErrorEnabled()) {
            logger.error(message, object);
        }
    }

    public void logErrorWithException(final Logger logger, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    public void logStandardProblemDetail(final Logger logger, final ProblemDetail problemDetail, final Exception e) {
        if (logger.isErrorEnabled()) {
            final var message = createStandardProblemDetailMessage(problemDetail);
            logger.error(message, e);
        }
    }

    public void logHttpStatusCodeError(final Logger logger, final String message, final Integer errorCode) {
        if (logger.isErrorEnabled()) {
            logger.error(createBasicErrorResponseMessage(errorCode, message) + "\n");
        }
    }

    private String createStandardProblemDetailMessage(final ProblemDetail problemDetail) {
        final StringBuilder messageBuilder = new StringBuilder(256);

        messageBuilder.append("Type: ").append(problemDetail.getType())
            .append(", Title: ").append(problemDetail.getTitle())
            .append(", Status: ").append(problemDetail.getStatus())
            .append(", Detail: ").append(problemDetail.getDetail() != null ? problemDetail.getDetail() : "N/A")
            .append(", Instance: ").append(problemDetail.getInstance() != null ? problemDetail.getInstance() : "N/A");

        if (problemDetail.getProperties() != null && !problemDetail.getProperties().isEmpty()) {
            messageBuilder.append(", Properties: ").append(problemDetail.getProperties());
        }

        return messageBuilder.toString();
    }


    private String createBasicErrorResponseMessage(final Integer errorCode, final String message) {
        if (errorCode == null) {
            return "Error: No error code provided.";
        }

        final StringBuilder responseMessage = new StringBuilder(256);
        responseMessage.append("Error Code: ").append(errorCode).append(", ");

        if (message != null && !message.isEmpty()) {
            responseMessage.append("Message: ").append(message);
        } else {
            responseMessage.append("Message: No additional message provided.");
        }

        return responseMessage.toString();
    }

}
