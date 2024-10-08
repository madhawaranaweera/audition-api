package com.audition.configuration;

import brave.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ResponseHeaderInjector implements HandlerInterceptor {

    private final Tracer tracer;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
        throws Exception {
        final String traceId = tracer.currentSpan().context().traceIdString();
        final String spanId = tracer.currentSpan().context().spanIdString();

        response.setHeader("trace_id", traceId);
        response.setHeader("span_id", spanId);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
