package com.audition.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("PMD.GuardLogStatement")
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
        final ClientHttpRequestExecution execution) throws IOException {
        LOGGER.debug("Request URI={}", request.getURI());
        LOGGER.debug("Request Method={}", request.getMethod());
        LOGGER.debug("Request Body={}", new String(body, StandardCharsets.UTF_8));

        final ClientHttpResponse response = execution.execute(request, body);

        LOGGER.debug("Response Status Code={}", response.getStatusCode());
        LOGGER.debug("Response Body={}", new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));

        return response;
    }
}
