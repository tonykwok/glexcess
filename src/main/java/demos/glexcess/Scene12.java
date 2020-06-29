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

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import demos.common.ResourceRetriever;

import java.io.IOException;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
final class Scene12 implements Scene {
    private Texture[] k_Text;
    private static final int numtexs = 7;
    private static boolean init = true;
    private float k_time = 0;

    private float k_timer;

    private void init(GLAutoDrawable drawable, GLU glu) {
        k_Text = new Texture[numtexs];
        for (int i = 0; i < k_Text.length; i++) {
            k_Text[i] = new Texture();
        }

        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = drawable.getGLU();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 100.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        try {
            k_Text[1].load(gl,glu,ResourceRetriever.getResourceAsStream("data/glxcess.raw"));
            k_Text[2].load(gl,glu,ResourceRetriever.getResourceAsStream("data/cl.raw"));
            k_Text[3].load(gl,glu,ResourceRetriever.getResourceAsStream("data/glxcesss.raw"));
            k_Text[4].load(gl,glu,ResourceRetriever.getResourceAsStream("data/crite.raw"));
            k_Text[5].load(gl,glu,ResourceRetriever.getResourceAsStream("data/lightmask.raw"));
            k_Text[6].load(gl,glu,ResourceRetriever.getResourceAsStream("data/cl2.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glClearColor(.5f, 0.3f, 0.2f, 0.0f);
        gl.glClearColor(.0f, .0f, .0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glFrontFace(GL2.GL_CCW);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
    }

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        k_Text[1].kill(gl);
        k_Text[2].kill(gl);
        k_Text[3].kill(gl);
        k_Text[4].kill(gl);
        k_Text[5].kill(gl);
        k_Text[6].kill(gl);
        init = true;
    }

    private static void k_drawrect(GL2 gl, float b, float h) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-b / 2, -h / 2, 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(b / 2, -h / 2, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(b / 2, h / 2, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-b / 2, h / 2, 0.0f);
        gl.glEnd();
    }

    private static void k_drawrect1(GL2 gl, float b, float h, float shifta, float shiftb) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f + shifta, 0.0f + shiftb);
        gl.glVertex3f(-b / 2, -h / 2, 0.0f);
        gl.glTexCoord2f(1.5f + shifta, 0.0f + shiftb);
        gl.glVertex3f(b / 2, -h / 2, 0.0f);
        gl.glTexCoord2f(1.5f + shifta, 1.5f + shiftb);
        gl.glVertex3f(b / 2, h / 2, 0.0f);
        gl.glTexCoord2f(0.0f + shifta, 1.5f + shiftb);
        gl.glVertex3f(-b / 2, h / 2, 0.0f);
        gl.glEnd();
    }

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float time) {
        if (init) {
            init(drawable, glu);
            init = false;
        }
        k_time = 10 * time;

        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        k_timer = -1.0f + (k_time) / 5000.0f;

        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        k_Text[2].use(gl);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -5.0f);
        k_timer += 3;
        gl.glPushMatrix();
        //glColor4f(1,1,1,.15+.05*(float)Math.sin(k_timer/13.0f));
        gl.glColor4f(1, 1, 1, .25f + .25f * (float)Math.sin(k_timer / 13.0f));
        gl.glRotatef(20 * (float)Math.sin(k_timer * 2.0), 1, 0, 0);
        gl.glRotatef(90 * (float)Math.sin(k_timer), 0, 0, 1);
        k_drawrect1(gl, 8.5f, 8.5f, k_timer / 10.0f, .25f - k_timer / 5.0f);
        gl.glPopMatrix();

        //k_Text[2].use(gl);
        gl.glPushMatrix();
        gl.glRotatef(k_timer * 10.0f, 0, 0, 1);
        gl.glTranslatef(0, 0, 2.0f * (float)Math.sin(k_timer / 1.0f));
        //glColor4f(1,1,1,.1+.1*(float)Math.cos(k_timer/9.0f));
        gl.glColor4f(1, 1, 1, .3f + .3f * (float)Math.cos(k_timer / 9.0f));
        k_drawrect1(gl, 10, 10, .5f - k_timer / 7.5f, .75f + k_timer / 2.5f);
        gl.glPopMatrix();

        gl.glPushMatrix();
        //k_Text[6].use(gl);
        gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
        //glColor4f(.75,.75,.75,.5);
        gl.glColor4f(1, 1, 1, 1);
        gl.glTranslatef(0, 0, 2.0f * (float)Math.sin(k_timer / 1.0f));
        gl.glRotatef(k_timer * 20.0f, 0, 0, 1);
        gl.glTranslatef(0, 0, 1 + 2.0f * (float)Math.sin(k_timer / 2.0f) * (float)Math.sin(k_timer / 1.0f));
        k_drawrect1(gl, 10, 10, .35f - k_timer / 10.0f, .1f + k_timer / 25.0f);
        gl.glPopMatrix();
        k_timer -= 3;
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        k_Text[1].use(gl);
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
        gl.glLoadIdentity();
        if (k_timer < 2.501f)
            gl.glTranslatef(0, 0, -7.0f + 3.0f * (float)Math.sin(k_timer * 3.1415f * .5f / 2.5f));
        else
            gl.glTranslatef(0, 0, -4.0f);

        if (k_timer < 2.5f) {
            gl.glPushMatrix();
            if (k_timer <= 1.0f) {
                k_Text[3].use(gl);
                gl.glColor4f(k_timer, k_timer, k_timer, 1);
                gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
                k_drawrect(gl, 4.2f, 1.7f);
                k_Text[1].use(gl);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glColor4f(1, 1, 1, k_timer);
                k_drawrect(gl, 3.5f, 1);
            } else if (k_timer < 1.1f) {
                k_Text[3].use(gl);
                gl.glColor4f(1.0f - 10.0f * (k_timer - 1.0f), 1.0f - 10.0f * (k_timer - 1.0f), 1.0f - 10.0f * (k_timer - 1.0f), 1);
                gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
                k_drawrect(gl, 4.2f, 1.7f);
                k_Text[1].use(gl);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glColor4f(1, 1, 1, 20.0f * (k_timer - 1.0f));
                gl.glTranslatef(0, 0, 15000.0f * (k_timer - 1.0f) * (k_timer - 1.0f) * (k_timer - 1.0f) * (k_timer - 1.0f));
                k_drawrect(gl, 3.5f, 1);
            } else {
                float j_tras = (1.0f + (float)Math.sin((-k_timer + 1.1f) * .5 * 3.1415f / 1.4f));
                gl.glTranslatef(0, 0, 1.5f * j_tras);
                gl.glColor4f(1, 1, 1, j_tras);
                k_drawrect(gl, 3.5f, 1);
            }
            gl.glPopMatrix();
        }

        if (k_timer >= 1.0f) {
            if (k_timer < 1.1f) {
                gl.glColor4f(1, 1, 1, 1.0f - 10.0f * (k_timer - 1.0f));
                k_drawrect(gl, 3.5f, 1);
            } else {
                gl.glPushMatrix();
                if (k_timer < 2.5f) {
                    float j_tras = -(float)Math.sin((-k_timer + 1.1f) * .5 * 3.1415f / 1.4f);
                    k_Text[1].use(gl);
                    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                    gl.glColor4f(1, 1, 1, j_tras);
                    k_drawrect(gl, 3.5f, 1);
                } else {
                    k_Text[3].use(gl);
                    gl.glColor4f(2.0f * (k_timer - 2.5f), 2.0f * (k_timer - 2.5f), 2.0f * (k_timer - 2.5f), 1);
                    gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
                    k_drawrect(gl, 4.2f, 1.7f);
                    k_Text[1].use(gl);
                    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                    gl.glColor4f(1, 1, 1, 1);
                    k_drawrect(gl, 3.5f, 1);
                }
                gl.glPopMatrix();
            }
        }

        if (k_timer < 0.0f) {
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_ALPHA);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glColor4f(1, 1, 1, -k_timer);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            k_drawrect(gl, 1.2f, 1.2f);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }


        k_Text[5].use(gl);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -1.0f);
//	if (k_timer<2.501f) gl.glTranslatef(0,0,-7.0f+3.0f*(float)Math.sin(k_timer*3.1415f*.5f/2.5f));
//	else gl.glTranslatef(0,0,-4.0f);

        gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
        gl.glColor4f(1, 1, 1, 1);
        k_drawrect(gl, 1.85f, 1.25f);
        //k_drawrect(4.8,2.1);

        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        if (k_timer > 2.5f) {

            gl.glLoadIdentity();
            gl.glTranslatef(.03f, -.25f, -1.0f);
            k_Text[4].use(gl);
            if (k_timer - 2.5f < .75f)
                gl.glColor4f(1, 1, 1, k_timer - 2.5f);
            else
                gl.glColor4f(1, 1, 1, .75f);
            gl.glScalef(1, -1, 1);
            k_drawrect(gl, 1, .03125f);
        }

        if (k_timer > 4.0f) {
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_ALPHA);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glColor4f(1, 1, 1, (k_timer - 4.0f) / 3.25f);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            k_drawrect(gl, 1.2f, 1.2f);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }
        if (k_timer > 7.25f) {
            //*************** FINISH
            //k_Clean();
            return false;
        }
        return true;
    }
}
