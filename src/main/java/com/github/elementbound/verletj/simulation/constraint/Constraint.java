package com.github.elementbound.verletj.simulation.constraint;

public interface Constraint {
    void apply();

    default void draw() {
    }
}
