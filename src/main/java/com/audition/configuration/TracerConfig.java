package com.audition.configuration;

import brave.propagation.B3Propagation;
import brave.propagation.B3Propagation.Format;
import io.micrometer.tracing.brave.propagation.PropagationFactorySupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracerConfig {

    @Bean
    PropagationFactorySupplier propagationFactorySupplier() {
        return () -> B3Propagation.newFactoryBuilder().injectFormat(Format.MULTI).build();
    }
}
