/*
 * GLExcess v1.0 Demo
 * Copyright (C) 2001-2003 Paolo Martella
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package demos.glexcess;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.gl2.GLUgl2;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
final class Renderer implements GLEventListener {
    private static final boolean loop = true;
    private float timing = 0;
    private float step = 1;
    private final Scene[] scenes = new Scene[]{
        new Scene1(),
        new Scene2(),
        new Scene3(),
        new Scene4(),
        new Scene5(),
        new Scene6(),
        new Scene7(),
        new Scene8(),
        new Scene9(),
        new Scene10(),
        new Scene11(),
        new Scene12()
    };

    private int currentScene = 0;
    private boolean switchScene;

    private GLU glu;

    private void drawscene(GLAutoDrawable drawable) {
        if (switchScene)
            nextScene(drawable);

        boolean rendered = scenes[currentScene].drawScene(drawable, glu, timing);
        if (!rendered) {
            nextScene(drawable);
        }
    }

    public final void nextScene() {
        switchScene = true;
    }

    private void nextScene(GLAutoDrawable drawable) {
        scenes[currentScene].clean(drawable);
        timing = 0;
        currentScene++;
        if (currentScene >= scenes.length) {
            if (loop) {
                currentScene = 0;
            } else {
                System.exit(0);
            }
        }
        switchScene = false;
    }

    public final void increaseStep() {
        step += 1;
    }

    public final void decreaseStep() {
        step = Math.max(-1, step - 1);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLUgl2();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        timing += step;
        drawscene(drawable);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int xstart,
                        int ystart,
                        int width,
                        int height) {
        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = glAutoDrawable.getGLU();

        height = (height == 0) ? 1 : height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45, (float) width / height, 1, 1000);
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
}