package com.github.elementbound.verletj;

import com.github.elementbound.verletj.window.Window;
import com.github.elementbound.verletj.window.WindowHint;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadMatrixf;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glVertex2f;
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

        while (!window.shouldClose()) {
            glClearColor(0.f, .5f, 1.f, 1.f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            var aspect = window.getWidth() / (float) window.getHeight();
            var matProjection = new Matrix4f().ortho2D(-2.f * aspect, 2.f * aspect, -2.f, 2.f);
            var matModelView = new Matrix4f();

            glViewport(0, 0, window.getWidth(), window.getHeight());

            try (var stack = stackPush()) {
                var matData = stack.mallocFloat(16);
                glMatrixMode(GL_PROJECTION);
                glLoadMatrixf(matProjection.get(matData));
            }

            glBegin(GL_TRIANGLE_FAN);
            int res = 64;
            for (int i = 0; i < res; ++i) {
                float f = i / (float) res;
                glVertex2f((float) Math.cos(f * 2 * Math.PI), (float) Math.sin(f * 2 * Math.PI));
                // glColor3f(f, 1.f - f, 0.f);
                glColor4f(1.f, 1.f, 1.f, 1.f);
            }
            glEnd();

            window.swapBuffers();
            glfwPollEvents();
        }
    }

    private void deinit() {
        window.destroy();
        glfwTerminate();
    }
}
