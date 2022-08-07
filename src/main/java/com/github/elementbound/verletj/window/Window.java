package com.github.elementbound.verletj.window;

import com.github.elementbound.verletj.EventSource;
import com.github.elementbound.verletj.window.event.KeyEvent;
import com.github.elementbound.verletj.window.event.ResizeEvent;

import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Window {
    private final long id;

    private int width;
    private int height;

    private final EventSource<KeyEvent> keyEventSource = new EventSource<>();
    private final EventSource<ResizeEvent> resizeEventSource = new EventSource<>();

    public Window(int width, int height, String title, WindowHint... hints) {
        glfwDefaultWindowHints();

        for (var hint : hints) {
            glfwWindowHint(hint.hint(), hint.value());
        }

        id = glfwCreateWindow(width, height, title, 0, 0);

        this.width = width;
        this.height = height;

        if (id == 0L) {
            throw new RuntimeException("Failed to create window");
        }

        glfwSetKeyCallback(id, (window, key, scancode, action, mods) ->
            keyEventSource.emit(new KeyEvent(key, scancode, action, mods))
        );

        glfwSetFramebufferSizeCallback(id, (window, newWidth, newHeight) -> {
            this.width = newWidth;
            this.height = newHeight;
            resizeEventSource.emit(new ResizeEvent(this.width, this.height));
        });
    }

    public void makeActive() {
        glfwMakeContextCurrent(id);
    }

    public void swapBuffers() {
        glfwSwapBuffers(id);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(id);
    }

    public void close() {
        glfwSetWindowShouldClose(id, true);
    }

    public void destroy() {
        glfwDestroyWindow(id);
    }

    public long getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public EventSource<KeyEvent> onKey() {
        return keyEventSource;
    }

    public EventSource<ResizeEvent> onResize() {
        return resizeEventSource;
    }
}
