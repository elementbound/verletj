package com.github.elementbound.verletj;

import org.joml.Vector2d;
import org.joml.Vector3d;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;

public class GLUtils {
    private static final Vector3d WHITE = new Vector3d(1.0);
    private static final int DEFAULT_RES = 16;

    public static void drawCircle(Vector2d position, double r, Vector3d color, int res) {
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i < res; ++i) {
            double f = i / (double) res;
            double a = f * 2.0 * Math.PI;

            glColor3d(color.x(), color.y(), color.z());
            glVertex2d(position.x() + Math.cos(a) * r, position.y() + Math.sin(a) * r);
        }

        glEnd();
    }

    public static void drawCircle(Vector2d position, double r) {
        drawCircle(position, r, WHITE, DEFAULT_RES);
    }

    public static void drawCircle(Vector2d position, double r, Vector3d color) {
        drawCircle(position, r, color, DEFAULT_RES);
    }
}
