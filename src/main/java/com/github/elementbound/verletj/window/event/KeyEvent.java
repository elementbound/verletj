package com.github.elementbound.verletj.window.event;

public record KeyEvent(
        int key,
        int scancode,
        int action,
        int mods
) {
}
