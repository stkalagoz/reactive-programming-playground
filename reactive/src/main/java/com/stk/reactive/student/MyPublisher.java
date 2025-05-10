package com.stk.reactive.student;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class MyPublisher {
    private final Sinks.Many<Student> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void emit(Student entity) {
        sink.tryEmitNext(entity);
    }

    public Flux<Student> getStream() {
        return sink.asFlux();
    }
}
