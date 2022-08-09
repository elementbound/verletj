package com.github.elementbound.verletj.simulation;

import org.joml.Vector2d;

public interface Entity {
    default void onSpawn() {
    }

    default void onDespawn() {
    }

    default void simulate(double t, double dt) {
    }

    default void draw() {
    }

    Vector2d getPosition();

    void setPosition(Vector2d position);
}
