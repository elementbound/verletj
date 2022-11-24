package com.github.elementbound.verletj.simulation;

import java.util.StringJoiner;

public class SimulationMetrics {
    private double simulatedTime = 0.0;
    private double realTime = 0.0;
    private long simulatedFrames = 0;
    private long collisionsResolved = 0;
    private long entitiesSimulated = 0;

    private double frameStart;

    public void beginFrame() {
        frameStart = System.currentTimeMillis() / 1000.0;
    }

    public void endFrame(double dt) {
        realTime += (System.currentTimeMillis() / 1000.0) - frameStart;
        simulatedTime += dt;
        simulatedFrames++;
    }

    public void collision(long count) {
        collisionsResolved += count;
    }

    public void entities(long count) {
        entitiesSimulated += count;
    }

    public double getSimulatedTime() {
        return simulatedTime;
    }

    public double getRealTime() {
        return realTime;
    }

    public long getSimulatedFrames() {
        return simulatedFrames;
    }

    public long getCollisionsResolved() {
        return collisionsResolved;
    }

    public long getEntitiesSimulated() {
        return entitiesSimulated;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimulationMetrics.class.getSimpleName() + "[", "]")
                .add("simulatedTime=" + simulatedTime)
                .add("realTime=" + realTime)
                .add("simulatedFrames=" + simulatedFrames)
                .add("collisionsResolved=" + collisionsResolved)
                .add("entitiesSimulated=" + entitiesSimulated)
                .add("frameStart=" + frameStart)
                .toString();
    }
}
