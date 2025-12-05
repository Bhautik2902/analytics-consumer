package com.livevote.analyticsconsumer.analyticsconsumer.service;

public interface EventConsumer<T> {
    void consume(T event);
}
