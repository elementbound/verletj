package com.github.elementbound.verletj;

import org.joml.Vector2d;
import org.joml.Vector3d;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;

public class GLUtils {
    private static final int DEFAULT_RES = 16;

    public static final Vector3d WHITE = new Vector3d(1.0);
    public static final Vector3d RED = new Vector3d(1., 0., 0.);
    public static final Vector3d GREEN = new Vector3d(0., 1., 0.);
    public static final Vector3d BLUE = new Vector3d(0., 0., 1.);

    public static void drawCircle(Vector2d position, double r, Vector3d color, boolean fill, int res) {
        glBegin(fill ? GL_TRIANGLE_FAN : GL_LINE_LOOP);

        for (int i = 0; i < res; ++i) {
            double f = i / (double) res;
            double a = f * 2.0 * Math.PI;

            glColor3d(color.x(), color.y(), color.z());
            glVertex2d(position.x() + Math.cos(a) * r, position.y() + Math.sin(a) * r);
        }

        glEnd();
    }

    public static void drawCircle(Vector2d position, double r, boolean fill) {
        drawCircle(position, r, WHITE, fill, DEFAULT_RES);
    }

    public static void drawCircle(Vector2d position, double r, Vector3d color, boolean fill) {
        drawCircle(position, r, color, fill, DEFAULT_RES);
    }

    public static void drawLine(double fromX, double fromY, double toX, double toY, double r, double g, double b) {
        glBegin(GL_LINES);
        glColor3d(r, g, b);
        glVertex2d(fromX, fromY);
        glVertex2d(toX, toY);
        glEnd();
    }

    public static void drawLine(double fromX, double fromY, double toX, double toY) {
        drawLine(fromX, fromY, toX, toY, 1., 1., 1.);
    }

    public static void drawLine(Vector2d from, Vector2d to, Vector3d color) {
        drawLine(from.x, from.y, to.x, to.y, color.x, color.y, color.z);
    }

    public static void drawLine(Vector2d from, Vector2d to) {
        drawLine(from, to, WHITE);
    }
}
