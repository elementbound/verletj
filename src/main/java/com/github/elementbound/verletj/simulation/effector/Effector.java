package com.github.elementbound.verletj.simulation.effector;

public interface Effector {
    void apply();

    default void postResolve() {
    }

    default void draw() {
    }
}
