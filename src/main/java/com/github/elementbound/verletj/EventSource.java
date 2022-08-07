package com.github.elementbound.verletj;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class EventSource<T> {
    private final List<Consumer<T>> listeners = new LinkedList<>();

    public void subscribe(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void unsubscribe(Consumer<T> listener) {
        listeners.remove(listener);
    }

    public void emit(T event) {
        listeners.forEach(listener -> listener.accept(event));
    }
}
