package com.github.elementbound.verletj;

import com.github.elementbound.verletj.simulation.Simulator;
import com.github.elementbound.verletj.simulation.SphereEntity;
import com.github.elementbound.verletj.window.Window;
import com.github.elementbound.verletj.window.WindowHint;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
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

        window = new Window(640, 480, "VerletJ",
                new WindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        );

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
        var random = new Random();
        random.setSeed(0xC0FFEE);

        for (int i = 0; i < 256; ++i) {
            var sphere = new SphereEntity();
            sphere.setPosition(new Vector2d(
                    random.nextGaussian() * 2.0,
                    random.nextGaussian() * 2.0
            ));
            sphere.setR(0.25);

            simulator.spawn(sphere);
        }

        var lastSimulated = System.currentTimeMillis() / 1000.0;
        var simulationTime = 0.0;
        final var simulationInterval = 1.0 / 60.0;
        final var timeScale = 1.0;

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

            while (System.currentTimeMillis() / 1000.0 - lastSimulated > simulationInterval) {
                lastSimulated += simulationInterval;
                var dt = simulationInterval * timeScale;
                simulationTime += dt;

                simulator.simulate(simulationTime, dt);
            }

            simulator.draw();

            // Yield remaining time slice
            sleep(1);

            window.swapBuffers();
            glfwPollEvents();
        }
    }

    private void deinit() {
        window.destroy();
        glfwTerminate();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}
