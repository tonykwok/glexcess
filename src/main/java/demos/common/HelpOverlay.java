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
package demos.common;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
public class HelpOverlay implements GLEventListener {
    private List keyboardEntries = new ArrayList();
    private List mouseEntries = new ArrayList();
    private boolean visible = false;
    private GLUT glut = new GLUT();
    GLU glu = new GLU();
    private static final int CHAR_HEIGHT = 12;
    private static final int OFFSET = 15;
    private static final int INDENT = 3;
    private static final String KEYBOARD_CONTROLS = "Keyboard controls";
    private static final String MOUSE_CONTROLS = "Mouse controls";

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void display(GLAutoDrawable GLAutoDrawable) {
        GL2 gl = GLAutoDrawable.getGL().getGL2();
        // GLU glu = GLAutoDrawable.getGLU();
        // Store old matrices
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        int width = GLAutoDrawable.getSurfaceWidth();
        int height = GLAutoDrawable.getSurfaceHeight();
        gl.glViewport(0, 0, width, height);

        // Store enabled state and disable lighting, texture mapping and the depth buffer
        gl.glPushAttrib(GL2.GL_ENABLE_BIT);
        gl.glDisable(GL2.GL_BLEND);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_DEPTH_TEST);

        // Retrieve the current viewport and switch to orthographic mode
        IntBuffer viewPort = GLBuffers.newDirectIntBuffer(4);;
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort);
        glu.gluOrtho2D(0, viewPort.get(2), viewPort.get(3), 0);

        // Render the text
        gl.glColor3f(1, 1, 1);

        int x = OFFSET;
        int maxx = 0;
        int y = OFFSET + CHAR_HEIGHT;

        if (keyboardEntries.size() > 0) {
            gl.glRasterPos2i(x, y);
            glut.glutBitmapString(/*gl,*/ glut.BITMAP_HELVETICA_12, KEYBOARD_CONTROLS);
            maxx = Math.max(maxx, OFFSET + glut.glutBitmapLength(glut.BITMAP_HELVETICA_12, KEYBOARD_CONTROLS));

            y += OFFSET;
            x += INDENT;
            for (int i = 0; i < keyboardEntries.size(); i++) {
                gl.glRasterPos2f(x, y);
                String text = (String) keyboardEntries.get(i);
                glut.glutBitmapString(/*gl,*/ glut.BITMAP_HELVETICA_12, text);
                maxx = Math.max(maxx, OFFSET + glut.glutBitmapLength(glut.BITMAP_HELVETICA_12, text));
                y += OFFSET;
            }
        }

        if (mouseEntries.size() > 0) {
            x = maxx + OFFSET;
            y = OFFSET + CHAR_HEIGHT;
            gl.glRasterPos2i(x, y);
            glut.glutBitmapString(/*gl, */glut.BITMAP_HELVETICA_12, MOUSE_CONTROLS);

            y += OFFSET;
            x += INDENT;
            for (int i = 0; i < mouseEntries.size(); i++) {
                gl.glRasterPos2f(x, y);
                glut.glutBitmapString(/*gl,*/ glut.BITMAP_HELVETICA_12, (String) mouseEntries.get(i));
                y += OFFSET;
            }
        }

        // Restore enabled state
        gl.glPopAttrib();

        // Restore old matrices
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();
    }

    public void dispose(GLAutoDrawable GLAutoDrawable) {
    }

    public void init(GLAutoDrawable GLAutoDrawable) {
    }

    public void reshape(GLAutoDrawable GLAutoDrawable, int i, int i1, int i2, int i3) {
    }

    public void registerKeyStroke(KeyStroke keyStroke, String description) {
        String modifiersText = KeyEvent.getKeyModifiersText(keyStroke.getModifiers());
        String keyText = KeyEvent.getKeyText(keyStroke.getKeyCode());
        keyboardEntries.add(
                (modifiersText.length() != 0 ? modifiersText + " " : "") +
                keyText + ": " +
                description
        );
    }

    public void registerMouseEvent(int id, int modifiers, String description) {
        String mouseText = null;
        switch (id) {
            case MouseEvent.MOUSE_CLICKED:
                mouseText = "Clicked " + MouseEvent.getModifiersExText(modifiers);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                mouseText = "Dragged " + MouseEvent.getModifiersExText(modifiers);
                break;
            case MouseEvent.MOUSE_ENTERED:
                mouseText = "Mouse enters";
                break;
            case MouseEvent.MOUSE_EXITED:
                mouseText = "Mouse exits";
                break;
            case MouseEvent.MOUSE_MOVED:
                mouseText = "Mouse moves";
                break;
            case MouseEvent.MOUSE_PRESSED:
                mouseText = "Pressed " + MouseEvent.getModifiersExText(modifiers);
                break;
            case MouseEvent.MOUSE_RELEASED:
                mouseText = "Released " + MouseEvent.getModifiersExText(modifiers);
                break;
        }
        mouseEntries.add(
                mouseText + ": " + description
        );

    }
}
