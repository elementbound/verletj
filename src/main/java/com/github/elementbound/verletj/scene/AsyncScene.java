package com.github.elementbound.verletj.scene;

import com.github.elementbound.verletj.simulation.Simulator;

public abstract class AsyncScene implements Scene {
    @Override
    public void run(Simulator simulator) {
        var thread = new Thread(() -> asyncRun(simulator));
        thread.start();
    }

    protected abstract void asyncRun(Simulator simulator);

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
