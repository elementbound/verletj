package com.github.elementbound.verletj;

import com.github.elementbound.verletj.simulation.Simulator;
import com.github.elementbound.verletj.simulation.CircleEntity;
import com.github.elementbound.verletj.simulation.constraint.GlobalDistanceConstraint;
import com.github.elementbound.verletj.simulation.effector.ForceEffector;
import com.github.elementbound.verletj.simulation.effector.GravityEffector;
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

        var distanceConstraint = new GlobalDistanceConstraint(simulator.getCircles());
        distanceConstraint.setMaxDistance(7.0);
        simulator.addConstraint(distanceConstraint);

        var gravityEffector = new GravityEffector(new Vector2d(0.0, -0.5), simulator.getCircles());
        simulator.addEffector(gravityEffector);

        var forceEffector = new ForceEffector(simulator.getCircles());
        forceEffector.setStrength(-1.0);
        forceEffector.setRange(4.0);
        forceEffector.setFalloff(2.0);
        forceEffector.getPosition().set(0., -distanceConstraint.getMaxDistance() + forceEffector.getRange());
        simulator.addEffector(forceEffector);

        int count = 256;
        for (int i = 0; i < count; ++i) {
            var circle = new CircleEntity();
            circle.getPosition().x = MathUtil.birandom(random, 3.0);
            circle.getPosition().y = MathUtil.birandom(random, 3.0);

            circle.setR(0.25);

            simulator.spawn(circle);
        }

        var lastSimulated = System.currentTimeMillis() / 1000.0;
        var simulationTime = 0.0;
        final var simulationInterval = 1.0 / 60.0;
        final var timeScale = 1.0 / 1.0;

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
