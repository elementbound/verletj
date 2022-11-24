package com.github.elementbound.verletj;

import com.github.elementbound.verletj.scene.EffectorScene;
import com.github.elementbound.verletj.simulation.Simulator;
import com.github.elementbound.verletj.window.Window;
import com.github.elementbound.verletj.window.WindowHint;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadMatrixf;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

public class VerletJApp {
    private Window window;

    public static void main(String[] args) {
        new VerletJApp().run();
    }

    private void run() {
        init();
        loop();
        deinit();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.out);

        if (!glfwInit()) {
            throw new IllegalStateException("Failed to init GLFW");
        }

        window = new Window(640, 480, "VerletJ", new WindowHint(GLFW_RESIZABLE, GLFW_TRUE));

        window.onKey().subscribe(event -> {
            if (event.key() == GLFW_KEY_ESCAPE) {
                window.close();
            }
        });
    }

    private void loop() {
        window.makeActive();
        GL.createCapabilities();

        var simulator = new Simulator();

        var scene = new EffectorScene();
        scene.run(simulator);

        var lastSimulated = System.currentTimeMillis() / 1000.0;
        var simulationTime = 0.0;
        final var simulationInterval = 1.0 / 60.0;
        final var timeScale = 1.0;
        final var maxCatchupIterations = 1;

        final boolean[] isPaused = {false};

        window.onKey().subscribe(event -> {
            if (event.key() == GLFW_KEY_SPACE && event.action() == GLFW_PRESS) {
                isPaused[0] = !isPaused[0];
            }
        });

        while (!window.shouldClose()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            var aspect = window.getWidth() / (float) window.getHeight();
            var matProjection = new Matrix4f().ortho2D(-8.f * aspect, 8.f * aspect, -8.f, 8.f);

            glViewport(0, 0, window.getWidth(), window.getHeight());

            try (var stack = stackPush()) {
                var matData = stack.mallocFloat(16);
                glMatrixMode(GL_PROJECTION);
                glLoadMatrixf(matProjection.get(matData));
            }

            var catchupTime = System.currentTimeMillis() / 1000.0;
            for (int i = 0; i < maxCatchupIterations && catchupTime - lastSimulated > simulationInterval; ++i) {
                lastSimulated += simulationInterval;
                var dt = simulationInterval * timeScale;
                simulationTime += dt;

                if (!isPaused[0]) {
                    simulator.simulate(simulationTime, dt);
                }
            }

            simulator.draw();

            // Yield remaining time slice
            Thread.yield();

            window.swapBuffers();
            glfwPollEvents();
        }

        var metrics = simulator.getMetrics();

        System.out.print("Metrics:\n");
        System.out.printf("\tFrames simulated: %d\n", metrics.getSimulatedFrames());
        System.out.printf("\tTime simulated: %fs\n", metrics.getSimulatedTime());
        System.out.printf("\tReal time: %fs\n", metrics.getRealTime());
        System.out.printf("\tEntities simulated: %d\n", metrics.getEntitiesSimulated());
        System.out.printf("\tCollisions resolved: %d\n", metrics.getCollisionsResolved());
        System.out.print("\t---\n");
        System.out.printf("\tAverage time per frame: %fms\n", metrics.getRealTime() / metrics.getSimulatedFrames() * 1000.0);
        System.out.printf("\tAverage FPS: %f\n", 1.0 / (metrics.getRealTime() / metrics.getSimulatedFrames()));
        System.out.printf("\tCollisions resolved per sec: %f\n", metrics.getCollisionsResolved() / (double) metrics.getSimulatedFrames());
    }

    private void deinit() {
        window.destroy();
        glfwTerminate();
    }
}
