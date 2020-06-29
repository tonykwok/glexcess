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

import java.io.IOException;
import java.util.Random;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import demos.common.ResourceRetriever;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
final class Scene10 implements Scene {
    private final Random random = new Random();
    private Texture[] i_Text;
    private static final int numtexs = 21;
    private static boolean init = true;
    private float i_time = 0;

    private long i_gettime;

    private float i_timer = 0.0f;
    private final float[] i_radius = new float[7];
    private final float i_zeta = 0.4f;
    private float i_scale;
    private float i_shade;
    private float i_incr = 180.0f;

    private int i_x;

    private final int i_num=10;
    private final int i_numray=100;

    private static final class i_part {
        long start;
        float size;
        float phase;
        float xspd,yspd;
        float i_x,y;
        float r,g,b,a;
        float i_shade;
        boolean up;
    }

    private final i_part[] rays = new i_part[i_numray];
    private final int[] i_alpha = new int[i_num];
    private final GLUT glut = new GLUT();

    private void init(GLAutoDrawable drawable, GLU glu) {
        i_gettime = 0;

        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();
        i_Text = new Texture[numtexs];
        for (int i = 0; i < i_Text.length; i++) {
            i_Text[i] = new Texture();
        }
        try {
            i_Text[1].load(gl,glu,ResourceRetriever.getResourceAsStream("data/you.raw"));
            i_Text[2].load(gl,glu,ResourceRetriever.getResourceAsStream("data/youglow.raw"));
            i_Text[3].load(gl,glu,ResourceRetriever.getResourceAsStream("data/gotta.raw"));
            i_Text[4].load(gl,glu,ResourceRetriever.getResourceAsStream("data/gottaglow.raw"));
            i_Text[5].load(gl,glu,ResourceRetriever.getResourceAsStream("data/say.raw"));
            i_Text[6].load(gl,glu,ResourceRetriever.getResourceAsStream("data/sayglow.raw"));
            i_Text[7].load(gl,glu,ResourceRetriever.getResourceAsStream("data/yes.raw"));
            i_Text[8].load(gl,glu,ResourceRetriever.getResourceAsStream("data/yesglow.raw"));
            i_Text[9].load(gl,glu,ResourceRetriever.getResourceAsStream("data/cl.raw"));
            i_Text[10].load(gl,glu,ResourceRetriever.getResourceAsStream("data/text1.raw"));
            i_Text[11].load(gl,glu,ResourceRetriever.getResourceAsStream("data/xp10.raw"));
            i_Text[12].load(gl,glu,ResourceRetriever.getResourceAsStream("data/basic2.raw"));
            i_Text[13].load(gl,glu,ResourceRetriever.getResourceAsStream("data/cl2.raw"));
            i_Text[14].load(gl,glu,ResourceRetriever.getResourceAsStream("data/excess.raw"));
            i_Text[15].load(gl,glu,ResourceRetriever.getResourceAsStream("data/excessglow.raw"));
            i_Text[16].load(gl,glu,ResourceRetriever.getResourceAsStream("data/another.raw"));
            i_Text[17].load(gl,glu,ResourceRetriever.getResourceAsStream("data/anotherglow.raw"));
            i_Text[18].load(gl,glu,ResourceRetriever.getResourceAsStream("data/to.raw"));
            i_Text[19].load(gl,glu,ResourceRetriever.getResourceAsStream("data/toglow.raw"));
            i_Text[20].load(gl,glu,ResourceRetriever.getResourceAsStream("data/esaflr.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glMatrixMode(GL2.GL_PROJECTION);						// Select The Projection Matrix
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 4.6f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glDisable(GL2.GL_DEPTH_TEST);

        for (int i = 0; i < 7; i++) i_radius[i] = -1.5f;

        for (int i = 0; i < i_numray; i++) {
            rays[i] = new i_part();
            i_rst(i);
        }

        for (int i = 0; i < i_num; i++) i_alpha[i] = (int)(128 - 12.8 * (float) i / i_num);

        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_COLOR);
        gl.glEnable(GL2.GL_BLEND);
    }

    private static void i_drawquad(GL2 gl, float size) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-0.5f * size, -0.5f * size, 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(0.5f * size, -0.5f * size, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(0.5f * size, 0.5f * size, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-0.5f * size, 0.5f * size, 0.0f);
        gl.glEnd();
    }

    private static void i_drawtqd(GL2 gl, float size, float off, int a, int r, int g, int b) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4ub((byte)r, (byte)g, (byte)b, (byte)a);
        gl.glTexCoord2f(0.0f + off, 0.0f);
        gl.glVertex3f(-0.5f * size, -0.5f * size, 0.0f);

        gl.glColor4i(0, 0, 0, 0);
        gl.glTexCoord2f(0.5f + off, 0.0f);
        gl.glVertex3f(0.5f * size, -0.5f * size, 0.0f);

        gl.glTexCoord2f(0.5f + off, 1.0f);
        gl.glVertex3f(0.5f * size, 0.5f * size, 0.0f);

        gl.glColor4ub((byte)a, (byte)a, (byte)a, (byte)a);
        gl.glTexCoord2f(0.0f + off, 1.0f);
        gl.glVertex3f(-0.5f * size, 0.5f * size, 0.0f);
        gl.glEnd();
    }

    private static void i_drawtqd1(GL2 gl, float size, float off, int a, int r, int g, int b) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4i(0, 0, 0, 0);
        gl.glTexCoord2f(0.0f + off, 0.0f);
        gl.glVertex3f(-0.5f * size, -0.5f * size, 0.0f);

        gl.glColor4ub((byte)r, (byte)g, (byte)b, (byte)a);
        gl.glTexCoord2f(0.5f + off, 0.0f);
        gl.glVertex3f(0.5f * size, -0.5f * size, 0.0f);

        gl.glTexCoord2f(0.5f + off, 1.0f);
        gl.glVertex3f(0.5f * size, 0.5f * size, 0.0f);

        gl.glColor4i(0, 0, 0, 0);
        gl.glTexCoord2f(0.0f + off, 1.0f);
        gl.glVertex3f(-0.5f * size, 0.5f * size, 0.0f);
        gl.glEnd();
    }

    private static void i_drawtri(GL2 gl, float r, float g, float b, float a, float size) {
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glColor4f(r / 2, g / 2, b / 2, a);
        gl.glVertex3f(0, 0, 0);
        gl.glColor4f(0, 0, 0, 0);
        gl.glVertex3f(0, .5f * size, 7);
        gl.glColor4f(r, g, b, a);
        gl.glVertex3f(0, 0, 7);
        gl.glColor4f(0, 0, 0, 0);
        gl.glVertex3f(0, -.5f * size, 7);
        gl.glEnd();
    }

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        i_Text[1].kill(gl);
        i_Text[2].kill(gl);
        i_Text[3].kill(gl);
        i_Text[4].kill(gl);
        i_Text[5].kill(gl);
        i_Text[6].kill(gl);
        i_Text[7].kill(gl);
        i_Text[8].kill(gl);
        i_Text[9].kill(gl);
        i_Text[10].kill(gl);
        i_Text[11].kill(gl);
        i_Text[12].kill(gl);
        i_Text[13].kill(gl);
        i_Text[14].kill(gl);
        i_Text[15].kill(gl);
        i_Text[16].kill(gl);
        i_Text[17].kill(gl);
        i_Text[18].kill(gl);
        i_Text[19].kill(gl);
        i_Text[20].kill(gl);
        init = true;
    }

    private void i_rst(int i_x) {
        rays[i_x].size = .25f + .0015f * ((float) (Math.abs(random.nextInt()) % 1000));
        rays[i_x].phase = .180f * (float) (Math.abs(random.nextInt()) % 1000);
        rays[i_x].xspd = .1f + .001f * ((float) (Math.abs(random.nextInt()) % 1000));
        rays[i_x].yspd = .1f + .001f * ((float) (Math.abs(random.nextInt()) % 1000));
        rays[i_x].i_x = 0.0f;
        rays[i_x].y = 0.0f;
        rays[i_x].r = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
        rays[i_x].g = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
        rays[i_x].b = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
        rays[i_x].a = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
        rays[i_x].i_shade = 0.0f;
        rays[i_x].up = true;
        rays[i_x].start = i_gettime;
    }

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float globtime) {
        if (init) {
            init(drawable, glu);
            init = false;
        }
        i_time = 10 * globtime;

        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);

        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -5 + i_zeta);

        gl.glEnable(GL2.GL_TEXTURE_GEN_S);						///////////////////// STROBE
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
        //glBindTexture(GL2.GL_TEXTURE_2D, texture[9]);
        i_Text[10].use(gl);
        gl.glPushMatrix();
        gl.glRotatef(45 * (i_radius[0] + i_radius[1]), 1, 0, 0);
        gl.glRotatef(45 * (i_radius[1] + i_radius[2]) + 10 * i_timer, 0, 1, 0);
        gl.glRotatef(45 * (i_radius[2] + i_radius[3]), 0, 0, 1);
        gl.glRotatef(45 * (i_radius[3] + i_radius[4]), 1, 0, 1);
        gl.glRotatef(45 * (i_radius[4] + i_radius[5]), 1, 1, 0);
        gl.glRotatef(45 * (i_radius[5] + i_radius[6]), 0, 1, 1);
        gl.glScalef(.05f, .05f, .05f);
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)(192 + Math.abs(random.nextInt()) % 63));
        glut.glutSolidDodecahedron(/*gl*/);
        gl.glPopMatrix();

        gl.glDisable(GL2.GL_TEXTURE_GEN_S);
        gl.glDisable(GL2.GL_TEXTURE_GEN_T);
        //glBindTexture(GL2.GL_TEXTURE_2D, texture[10]);
        i_Text[11].use(gl);
        gl.glColor4f((float)Math.cos(i_radius[0]) * (float)Math.cos(i_radius[0]) + (float)Math.cos(i_radius[3]) * (float)Math.cos(i_radius[3]) + (float)Math.cos(i_radius[4]) * (float)Math.cos(i_radius[4]) + (float)Math.cos(i_radius[5]) * (float)Math.cos(i_radius[5]),
                (float)Math.cos(i_radius[1]) * (float)Math.cos(i_radius[1]) + (float)Math.cos(i_radius[4]) * (float)Math.cos(i_radius[4]),
                (float)Math.cos(i_radius[2]) * (float)Math.cos(i_radius[2]) + (float)Math.cos(i_radius[3]) * (float)Math.cos(i_radius[3]) + (float)Math.cos(i_radius[6]) * (float)Math.cos(i_radius[6]) + (float)Math.cos(i_radius[6]) * (float)Math.cos(i_radius[6]),
                .75f);
        gl.glPushMatrix();
        //glBindTexture(GL2.GL_TEXTURE_2D, texture[11]);
        i_Text[12].use(gl);
        i_drawquad(gl, .75f + .5f * (float)Math.cos(2 * (i_radius[0] + i_radius[1] + i_radius[2] + i_radius[3] + i_radius[4] + i_radius[5] + i_radius[6])));
        gl.glRotatef(10 * (i_radius[0] + i_radius[1] + i_radius[2] + i_radius[3] + i_radius[4] + i_radius[5] + i_radius[6]), 0, 0, 1);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        //glBindTexture(GL2.GL_TEXTURE_2D, texture[10]);
        i_Text[11].use(gl);
        i_drawquad(gl, .5f + .25f * (float)Math.sin(i_radius[0] + i_radius[1] + i_radius[2] + i_radius[3] + i_radius[4] + i_radius[5] + i_radius[6]));
        gl.glPopMatrix();
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);

        if ((i_radius[0] > -1.495) && (i_radius[0] < 1.495)) {
            gl.glPushMatrix();													// YOU RAY
            if (i_radius[0] < -1.0f)
                i_shade = 1.0f + 2 * (i_radius[0] + 1.0f);
            else if (i_radius[0] > 1.0f)
                i_shade = 1.0f - 2 * (i_radius[0] - 1.0f);
            else
                i_shade = 1.0f;
            gl.glRotatef(60 * i_radius[0], 0, 1, 0);
            gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
            gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[8]);
            i_Text[9].use(gl);
            gl.glBegin(GL2.GL_TRIANGLE_FAN);
            gl.glColor4f(0.5f, 0.162f, 0.067f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, 0, 0);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, 2.25f + .00025f * (float) (Math.abs(random.nextInt()) % 100), 3);
            gl.glColor4f(1.0f, 0.375f, 0.125f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, 1.8f, 3);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, .9f + .00025f * (float) (Math.abs(random.nextInt()) % 100), 3);
            gl.glEnd();
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glPopMatrix();
        }
        ///////////////////////////////////////////////////// YOU

        gl.glTranslatef(0, 1.25f, 0);
        if ((i_radius[0] > -1.495) && (i_radius[0] < 1.495)) {
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glPushMatrix();
            gl.glTranslatef(i_radius[0] * .5f, 0, 0);
            gl.glScalef(4, 1.5f, 1);
            gl.glColor4ub((byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte)255);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[0]);
            i_Text[1].use(gl);
            i_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[1]);
            i_Text[2].use(gl);
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(128 - 64 * (1 + i_radius[0]));
                gl.glPushMatrix();
                gl.glTranslatef(1 + i_radius[0] * 2.5f, 0, .5f * (float) i_x / i_num);
                gl.glScalef(2, 1.5f, 1);
                i_scale = .5f + i_radius[0] / 2;
                i_drawtqd(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 255, 160, 64);
                gl.glPopMatrix();
            }
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(64 * (1 + i_radius[0]));
                gl.glPushMatrix();
                gl.glTranslatef(-1 + i_radius[0] * 2.5f, 0, .5f * (float) i_x / i_num);
                gl.glScalef(2, 1.5f, 1);
                i_scale = i_radius[0] / 2;
                i_drawtqd1(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 255, 160, 64);
                gl.glPopMatrix();
            }
        }

        if ((i_radius[1] > -1.495) && (i_radius[1] < 1.495)) {
            gl.glPushMatrix();								// GOTTA RAY
            if (i_radius[1] < -1.0f)
                i_shade = 1.0f + 2 * (i_radius[1] + 1.0f);
            else if (i_radius[1] > 1.0f)
                i_shade = 1.0f - 2 * (i_radius[1] - 1.0f);
            else
                i_shade = 1.0f;
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[8]);
            i_Text[9].use(gl);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -5 + i_zeta);
            gl.glRotatef(60 * i_radius[1], 0, 1, 0);
            gl.glBegin(GL2.GL_TRIANGLE_FAN);
            gl.glColor4f(0.067f, 0.5f, 0.162f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, 0, 0);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, 1 + .00025f * (float) (Math.abs(random.nextInt()) % 100), 5);
            gl.glColor4f(0.125f, 1.0f, 0.375f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, .5f, 5);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, -.05f + .00025f * (float) (Math.abs(random.nextInt()) % 100), 5);
            gl.glEnd();
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glPopMatrix();
        }

        ///////////////////////////////////////////////////// GOTTA

        gl.glTranslatef(0, -1, 0);
        if ((i_radius[1] > -1.495) && (i_radius[1] < 1.495)) {
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glPushMatrix();
            gl.glTranslatef(i_radius[1] * .5f, 0, 0);
            gl.glScalef(4, 1, 1);
            gl.glColor4ub((byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte)255);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[2]);
            i_Text[3].use(gl);
            i_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[3]);
            i_Text[4].use(gl);
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(128 - 64 * (1 + i_radius[1]));
                gl.glPushMatrix();
                gl.glTranslatef(1 + i_radius[1] * 2.5f, 0, .5f * (float) i_x / i_num);
                gl.glScalef(2, 1, 1);
                i_scale = .5f + i_radius[1] / 2;
                i_drawtqd(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 64, 255, 160);
                gl.glPopMatrix();
            }
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(64 * (1 + i_radius[1]));
                gl.glPushMatrix();
                gl.glTranslatef(-1 + i_radius[1] * 2.5f, 0, .5f * (float) i_x / i_num);
                gl.glScalef(2, 1, 1);
                i_scale = i_radius[1] / 2;
                i_drawtqd1(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 64, 255, 160);
                gl.glPopMatrix();
            }
        }

        if ((i_radius[3] > -1.495) && (i_radius[3] < 1.495)) {
            gl.glPushMatrix();								// YES RAY
            if (i_radius[3] < -1.0f)
                i_shade = 1.0f + 2 * (i_radius[3] + 1.0f);
            else if (i_radius[3] > 1.0f)
                i_shade = 1.0f - 2 * (i_radius[3] - 1.0f);
            else
                i_shade = 1.0f;
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[8]);
            i_Text[9].use(gl);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -5 + i_zeta);
            gl.glRotatef(60 * i_radius[3], 0, 1, 0);
            gl.glBegin(GL2.GL_TRIANGLE_FAN);
            gl.glColor4f(0.5f, 0.162f, 0.5f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, 0, 0);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, -1 + .00025f * (float) (Math.abs(random.nextInt()) % 100), 3);
            gl.glColor4f(1.0f, 0.375f, 1.0f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, -1.5f, 3);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, -2 + .00025f * (float) (Math.abs(random.nextInt()) % 100), 3);
            gl.glEnd();
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glPopMatrix();
        }

        ///////////////////////////////////////////////////// YES

        gl.glTranslatef(0, -1.6f, 0);
        if ((i_radius[3] > -1.495) && (i_radius[3] < 1.495)) {
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glPushMatrix();
            gl.glTranslatef(i_radius[3] * .5f, 0, 0);
            gl.glScalef(4, 1, 1);
            gl.glColor4ub((byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte)255);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[6]);
            i_Text[7].use(gl);
            i_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[7]);
            i_Text[8].use(gl);
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(128 - 64 * (1 + i_radius[3]));
                gl.glPushMatrix();
                gl.glTranslatef(1 + i_radius[3] * 2.5f, 0, .5f * (float) i_x / i_num);
                gl.glScalef(2, 1, 1);
                i_scale = .5f + i_radius[3] / 2;
                i_drawtqd(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 255, 128, 255);
                gl.glPopMatrix();
            }
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(64 * (1 + i_radius[3]));
                gl.glPushMatrix();
                gl.glTranslatef(-1 + i_radius[3] * 2.5f, 0, .5f * (float) i_x / i_num);
                gl.glScalef(2, 1, 1);
                i_scale = i_radius[3] / 2;
                i_drawtqd1(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 255, 128, 255);
                gl.glPopMatrix();
            }
        }

        if ((i_radius[2] > -1.495) && (i_radius[2] < 1.495)) {
            gl.glPushMatrix();								// SAY RAY
            if (i_radius[2] < -1.0f)
                i_shade = 1.0f + 2 * (i_radius[2] + 1.0f);
            else if (i_radius[2] > 1.0f)
                i_shade = 1.0f - 2 * (i_radius[2] - 1.0f);
            else
                i_shade = 1.0f;
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[8]);
            i_Text[9].use(gl);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -5 + i_zeta);
            gl.glRotatef(60 * i_radius[2], 0, 1, 0);
            gl.glBegin(GL2.GL_TRIANGLE_FAN);
            gl.glColor4f(0.067f, 0.162f, 0.5f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, 0, 0);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, -.25f + .00025f * (float) (Math.abs(random.nextInt()) % 100), 4);
            gl.glColor4f(0.125f, 0.375f, 1.0f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, -.7f, 4);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, -1.3f + .00025f * (float) (Math.abs(random.nextInt()) % 100), 4);
            gl.glEnd();
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glPopMatrix();
        }

        ///////////////////////////////////////////////////// SAY

        gl.glTranslatef(0, .85f, 0);
        if ((i_radius[2] > -1.495) && (i_radius[2] < 1.495)) {
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glPushMatrix();
            gl.glTranslatef(i_radius[2] * .5f, 0, 0);
            gl.glScalef(4, 1, 1);
            gl.glColor4ub((byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte)255);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[4]);
            i_Text[5].use(gl);
            i_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[5]);
            i_Text[6].use(gl);
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(128 - 64 * (1 + i_radius[2]));
                gl.glPushMatrix();
                gl.glTranslatef(1 + i_radius[2] * 2.5f, 0, .5f * (float) i_x / i_num);
                gl.glScalef(2, 1, 1);
                i_scale = .5f + i_radius[2] / 2;
                i_drawtqd(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 64, 160, 255);
                gl.glPopMatrix();
            }
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(64 * (1 + i_radius[2]));
                gl.glPushMatrix();
                gl.glTranslatef(-1 + i_radius[2] * 2.5f, 0, .5f * (float) i_x / i_num);
                gl.glScalef(2, 1, 1);
                i_scale = i_radius[2] / 2;
                i_drawtqd1(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 64, 160, 255);
                gl.glPopMatrix();
            }
        }

        if ((i_radius[4] > -1.495) && (i_radius[4] < 1.495)) {
            gl.glPushMatrix();								// TO RAY
            if (i_radius[4] < -1.0f)
                i_shade = 1.0f + 2 * (i_radius[4] + 1.0f);
            else if (i_radius[4] > 1.0f)
                i_shade = 1.0f - 2 * (i_radius[4] - 1.0f);
            else
                i_shade = 1.0f;
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[8]);
            i_Text[9].use(gl);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -5 + i_zeta);
            gl.glRotatef(60 * i_radius[4], 0, 1, 0);
            gl.glBegin(GL2.GL_TRIANGLE_FAN);
            gl.glColor4f(0.5f, 0.162f, 0.067f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, 0, 0);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, 2.5f + .00025f * (float) (Math.abs(random.nextInt()) % 100), 4);
            gl.glColor4f(0.5f, 0.162f, 0.067f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, 1.75f, 4);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, 1 + .00025f * (float) (Math.abs(random.nextInt()) % 100), 4);
            gl.glEnd();
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glPopMatrix();
        }

        ///////////////////////////////////////////////////// TO

        gl.glTranslatef(0, 1.5f, 0);
        if ((i_radius[4] > -1.495) && (i_radius[4] < 1.495)) {
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glPushMatrix();
            gl.glTranslatef(i_radius[4] * .5f, 0, 0);
            gl.glScalef(4, 1.5f, 1);
            gl.glColor4ub((byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte)255);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[17]);
            i_Text[18].use(gl);
            i_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[18]);
            i_Text[19].use(gl);
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(128 - 64 * (1 + i_radius[4]));
                gl.glPushMatrix();
                gl.glTranslatef(1 + i_radius[4] * 2.5f, 0, .5f * (float) i_x / i_num);
                gl.glScalef(2, 1.5f, 1);
                i_scale = .5f + i_radius[4] / 2;
                i_drawtqd(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 255, 160, 64);
                gl.glPopMatrix();
            }
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(64 * (1 + i_radius[4]));
                gl.glPushMatrix();
                gl.glTranslatef(-1 + i_radius[4] * 2.5f, 0, .5f * (float) i_x / i_num);
                gl.glScalef(2, 1.5f, 1);
                i_scale = i_radius[4] / 2;
                i_drawtqd1(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 255, 160, 64);
                gl.glPopMatrix();
            }
        }

        if ((i_radius[5] > -1.495) && (i_radius[5] < 1.495)) {
            gl.glPushMatrix();								// ANOTHER RAY
            if (i_radius[5] < -1.0f)
                i_shade = 1.0f + 2 * (i_radius[5] + 1.0f);
            else if (i_radius[5] > 1.0f)
                i_shade = 1.0f - 2 * (i_radius[5] - 1.0f);
            else
                i_shade = 1.0f;
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[8]);
            i_Text[9].use(gl);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -5 + i_zeta);
            gl.glRotatef(30 * i_radius[5], 0, 1, 0);
            gl.glBegin(GL2.GL_TRIANGLE_FAN);
            gl.glColor4f(0.5f, 0.162f, 0.067f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, 0, 0);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, -.25f + .00025f * (float) (Math.abs(random.nextInt()) % 100), 4);
            gl.glColor4f(0.5f, 0.162f, 0.067f, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, -.7f, 4);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, -1.3f + .00025f * (float) (Math.abs(random.nextInt()) % 100), 4);
            gl.glEnd();
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glPopMatrix();
        }

        ///////////////////////////////////////////////////// ANOTHER

        gl.glTranslatef(0, -2, 0);
        if ((i_radius[5] > -1.495) && (i_radius[5] < 1.495)) {
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glPushMatrix();
            gl.glTranslatef(i_radius[5] * .5f, 0, 0);
            gl.glScalef(4, .5f, 1);
            gl.glColor4ub((byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte)255);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[15]);
            i_Text[16].use(gl);
            i_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[16]);
            i_Text[17].use(gl);
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(128 - 64 * (1 + i_radius[5]));
                gl.glPushMatrix();
                gl.glTranslatef(1 + i_radius[5] * 2.5f, 0, .25f * (float) i_x / i_num);
                gl.glScalef(2, .5f, 1);
                i_scale = .5f + i_radius[5] / 2;
                i_drawtqd(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 255, 96, 96);
                gl.glPopMatrix();
            }
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(64 * (1 + i_radius[5]));
                gl.glPushMatrix();
                gl.glTranslatef(-1 + i_radius[5] * 2.5f, 0, .25f * (float) i_x / i_num);
                gl.glScalef(2, .5f, 1);
                i_scale = i_radius[5] / 2;
                i_drawtqd1(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 255, 96, 96);
                gl.glPopMatrix();
            }
        }

        if ((i_radius[6] > -1.495) && (i_radius[6] < 1.495)) {
            gl.glPushMatrix();								// EXCESS RAY
            if (i_radius[6] < -1.0f)
                i_shade = 1.0f + 2 * (i_radius[6] + 1.0f);
            else if (i_radius[6] > 1.0f)
                i_shade = 1.0f - 2 * (i_radius[6] - 1.0f);
            else
                i_shade = 1.0f;
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[8]);
            i_Text[9].use(gl);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -5 + i_zeta);
            gl.glRotatef(30 * i_radius[6], 0, 1, 0);
            gl.glBegin(GL2.GL_TRIANGLE_FAN);
            gl.glColor4f(0.067f * 2, 0.162f * 2, 0.5f * 2, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, 0, 0);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, .35f + .00025f * (float) (Math.abs(random.nextInt()) % 100), 3);
            gl.glColor4f(0.125f * 2, 0.375f * 2, 1.0f * 2, i_shade * (0.625f + 0.0025f * ((float) (Math.abs(random.nextInt()) % 100))));
            gl.glVertex3f(0, 0, 3);
            gl.glColor4i(0, 0, 0, 0);
            gl.glVertex3f(0, -.35f + .00025f * (float) (Math.abs(random.nextInt()) % 100), 3);
            gl.glEnd();
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glPopMatrix();
        }

        ///////////////////////////////////////////////////// EXCESS

        gl.glTranslatef(0, 1, 0);
        if ((i_radius[6] > -1.495) && (i_radius[6] < 1.495)) {

            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glPushMatrix();
            gl.glTranslatef(-i_radius[6] * 1.5f, 0, .5f);
            gl.glScalef(8, 2, 1);
            gl.glColor4ub((byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte) ((float) 255 * i_shade), (byte)255);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[13]);
            i_Text[14].use(gl);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
            i_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            //glBindTexture(GL2.GL_TEXTURE_2D, texture[14]);
            i_Text[15].use(gl);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(128 - 64 * (1 + i_radius[6]));
                gl.glPushMatrix();
                gl.glTranslatef(2 + i_radius[6] * 2.5f, 0, .5f + .75f * (float) i_x / i_num);
                gl.glScalef(4, 2, 1);
                i_scale = .5f + i_radius[6] / 2;
                i_drawtqd(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 96, 128, 255);
                gl.glPopMatrix();
            }
            for (i_x = 0; i_x < i_num; i_x++) {
                i_alpha[i_x] = (int)(64 * (1 + i_radius[6]));
                gl.glPushMatrix();
                gl.glTranslatef(-2 + i_radius[6] * 2.5f, 0, .5f + .75f * (float) i_x / i_num);
                gl.glScalef(4, 2, 1);
                i_scale = i_radius[6] / 2;
                i_drawtqd1(gl, 1, i_scale, (int) ((float) i_alpha[i_x] * i_shade), 96, 128, 255);
                gl.glPopMatrix();
            }

        }
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        gl.glTranslatef(0, 0, 0);				////////////////////////////////////////	RAYS
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
        //glBindTexture(GL2.GL_TEXTURE_2D, texture[8]);
        i_Text[9].use(gl);

        for (int r = 0; r < i_numray; r++) {
            float time = (float) (i_gettime - rays[r].start) / 25.0f;
            gl.glPushMatrix();
            int sign;
            if ((r % 2) == 0) sign = 1; else sign = -1;
            gl.glRotatef(sign * rays[r].phase + sign * rays[r].i_x, 1, 0, 0);
            gl.glRotatef(sign * rays[r].phase + sign * rays[r].y, 0, 1, 0);
            //if (((rays[r].phase+rays[r].i_x>-90)&&(rays[r].phase+rays[r].i_x<90))&&((rays[r].phase+rays[r].y>-90)&&(rays[r].phase+rays[r].y<90)))
            i_drawtri(gl, rays[r].r, rays[r].g, rays[r].b, rays[r].i_shade + .00005f * ((float) (Math.abs(random.nextInt()) % 1000)), rays[r].size);
            rays[r].i_x = rays[r].xspd * time;
            rays[r].y = rays[r].yspd * time;
            if (time * (rays[r].phase + 10.0f) / 3000.0f < 2.0f * 3.1415f)
                rays[r].i_shade = rays[r].a * (1.0f - (float)Math.cos(time * (rays[r].phase + 10.0f) / 3000.0f)) / 2.0f;
            else
                i_rst(r);
            gl.glPopMatrix();
        }

        gl.glDisable(GL2.GL_TEXTURE_GEN_S);
        gl.glDisable(GL2.GL_TEXTURE_GEN_T);

        gl.glPushMatrix();
        //glBindTexture(GL2.GL_TEXTURE_2D, texture[12]);
        i_Text[13].use(gl);
        gl.glColor4f(1, 1, 1, .15f);
        gl.glPushMatrix();
        gl.glScalef(3.0f + 3.0f * (-(float)Math.cos(i_timer / 2.5f) + 1.0f), 3.0f + 3.0f * (-(float)Math.cos(i_timer / 2.5f) + 1.0f), 1);
        gl.glRotatef(i_incr, 0, 0, 1);
        i_drawquad(gl, 1);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glScalef(3.0f + 3.0f * ((float)Math.cos(i_timer / 2.5f) + 1.0f), 3.0f + 3.0f * ((float)Math.cos(i_timer / 2.5f) + 1.0f), 1);
        gl.glRotatef(-i_incr, 0, 0, 1);
        i_drawquad(gl, 1);
        gl.glPopMatrix();
        //i_incr+=1.0f;
        gl.glPopMatrix();
        i_timer = ((float) (i_gettime)) / 500.0f;
        i_incr = i_timer * 10.0f;

        if (i_timer < 4.0f) {
            float shader = 1.0f - i_timer / 4.0f;
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            //glDisable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            gl.glColor4f(shader, shader, shader, .5f);
            i_drawquad(gl, 1.2f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        }
        if (i_timer > 102.0f) {
            float shader = (i_timer - 102.0f) / 6.0f;
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            //glDisable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            gl.glColor4f(shader, shader, shader, .5f);
            i_drawquad(gl, 1.2f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        }


        if ((i_timer > 3.5f) && (i_timer < 6.5f)) {
            gl.glLoadIdentity();
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glTranslatef(0, 0, -1.0f);
            gl.glColor4f(1, 1, 1, .5f - .5f * (float)Math.cos((i_timer - 3.5f) * 3.1415f / 1.5f));
            i_drawquad(gl, 1.2f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }
        if ((i_timer > 91.5f) && (i_timer < 97.5f)) {
            gl.glLoadIdentity();
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glTranslatef(0, 0, -1.0f);
            if (i_timer < 93.5f)
                gl.glColor4f(1, 1, 1, .5f - .5f * (float)Math.cos((i_timer - 91.5f) * 3.1415f / 2.0f));
            else
                gl.glColor4f(1, 1, 1, .5f + .5f * (float)Math.cos((i_timer - 93.5f) * 3.1415f / 4.0f));
            i_drawquad(gl, 1.2f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }


        if (i_timer > 95.5f) {
            float i_offset = 96.5f;
            if ((i_timer > i_offset) && (i_timer < i_offset + 1.0f)) {
                float alphaval = 1.0f - (float)Math.sin((i_timer - i_offset) * 3.1415f / 2.0f);
                i_Text[1].use(gl);
                for (int rep = 1; rep < 5; rep++) {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, .5f, -1.0f + (float) (rep) / 5.0f + 3.0f * (-i_timer + i_offset));
                    gl.glColor4f(1, 1, 1, alphaval / rep);
                    gl.glScalef(2.0f, .75f, 1);
                    i_drawquad(gl, 1);
                }
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -1.0f);
                gl.glColor4f(1, 1, 1, alphaval / 2.0f);
                gl.glDisable(GL2.GL_TEXTURE_2D);
                i_drawquad(gl, 1.2f);
                gl.glEnable(GL2.GL_TEXTURE_2D);
            }
            i_offset = 97.0f;
            if ((i_timer > i_offset) && (i_timer < i_offset + 1.0f)) {
                float alphaval = 1.0f - (float)Math.sin((i_timer - i_offset) * 3.1415f / 2.0f);
                i_Text[3].use(gl);
                for (int rep = 1; rep < 5; rep++) {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, .166f, -1.0f + (float) (rep) / 5.0f + 3.0f * (-i_timer + i_offset));
                    gl.glColor4f(1, 1, 1, alphaval / rep);
                    gl.glScalef(2.0f, .5f, 1);
                    i_drawquad(gl, 1);
                }
                /*glLoadIdentity();
                gl.glTranslatef(0,.166,-1.0f+3.0f*(-i_timer+i_offset));
                gl.glColor4f(1,1,1,alphaval);
                gl.glScalef(2.0f,.5f,1);
                i_drawquad(1);*/
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -1.0f);
                gl.glColor4f(1, 1, 1, alphaval / 2.0f);
                gl.glDisable(GL2.GL_TEXTURE_2D);
                i_drawquad(gl, 1.2f);
                gl.glEnable(GL2.GL_TEXTURE_2D);
            }
            i_offset = 97.7f;
            if ((i_timer > i_offset) && (i_timer < i_offset + 1.0f)) {
                float alphaval = 1.0f - (float)Math.sin((i_timer - i_offset) * 3.1415f / 2.0f);
                i_Text[5].use(gl);
                for (int rep = 1; rep < 5; rep++) {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, -.166f, -1.0f + (float) (rep) / 5.0f + 3.0f * (-i_timer + i_offset));
                    gl.glColor4f(1, 1, 1, alphaval / rep);
                    gl.glScalef(2.0f, .55f, 1);
                    i_drawquad(gl, 1);
                }
                /*glLoadIdentity();
                gl.glTranslatef(0,-.166,-1.0f+3.0f*(-i_timer+i_offset));

                gl.glColor4f(1,1,1,alphaval);
                gl.glScalef(2.0f,.55f,1);
                i_drawquad(1);
                */
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -1.0f);
                gl.glColor4f(1, 1, 1, alphaval / 2.0f);
                gl.glDisable(GL2.GL_TEXTURE_2D);
                i_drawquad(gl, 1.2f);
                gl.glEnable(GL2.GL_TEXTURE_2D);
            }
            i_offset = 98.5f;
            if ((i_timer > i_offset) && (i_timer < i_offset + 2.0f)) {
                float alphaval = 1.0f - (float)Math.sin((i_timer - i_offset) * 3.1415f / 4.0f);
                i_Text[7].use(gl);
                for (int rep = 1; rep < 5; rep++) {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, -.5f, -1.5f + (float) (rep) / 5.0f + 1.5f * (-i_timer + i_offset));
                    gl.glColor4f(1, 1, 1, alphaval / rep);
                    gl.glScalef(2.0f, .55f, 1);
                    i_drawquad(gl, 1);
                }
                /*
                gl.glLoadIdentity();
                gl.glTranslatef(0,-.5,-1.0f+3.0f*(-i_timer+i_offset));

                gl.glColor4f(1,1,1,alphaval);
                gl.glScalef(2.0f,.55f,1);
                i_drawquad(1);
                */
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -1.0f);
                gl.glColor4f(1, 1, 1, alphaval / 2.0f);
                gl.glDisable(GL2.GL_TEXTURE_2D);
                i_drawquad(gl, 1.2f);
                gl.glEnable(GL2.GL_TEXTURE_2D);
            }
            i_offset = 101.5f;
            if ((i_timer > i_offset) && (i_timer < i_offset + 1.0f)) {
                float alphaval = 1.0f - (float)Math.sin((i_timer - i_offset) * 3.1415f / 2.0f);
                i_Text[18].use(gl);
                for (int rep = 1; rep < 5; rep++) {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, .25f, -1.0f + (float) (rep) / 5.0f + 3.0f * (-i_timer + i_offset));
                    gl.glColor4f(1, 1, 1, alphaval / rep);
                    gl.glScalef(1.5f, .55f, 1);
                    i_drawquad(gl, 1);
                }
                /*
                gl.glLoadIdentity();
                gl.glTranslatef(0,.25,-1.0f+3.0f*(-i_timer+i_offset));
                gl.glColor4f(1,1,1,alphaval);
                gl.glScalef(1.5f,.55f,1);
                i_drawquad(1);
                */
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -1.0f);
                gl.glColor4f(1, 1, 1, alphaval / 2.0f);
                gl.glDisable(GL2.GL_TEXTURE_2D);
                i_drawquad(gl, 1.2f);
                gl.glEnable(GL2.GL_TEXTURE_2D);
            }
            i_offset = 102.2f;
            if ((i_timer > i_offset) && (i_timer < i_offset + 1.0f)) {
                float alphaval = 1.0f - (float)Math.sin((i_timer - i_offset) * 3.1415f / 2.0f);
                i_Text[16].use(gl);
                for (int rep = 1; rep < 5; rep++) {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, -.25f, -1.0f + (float) (rep) / 5.0f + 3.0f * (-i_timer + i_offset));
                    gl.glColor4f(1, 1, 1, alphaval / rep);
                    gl.glScalef(2.0f, .25f, 1);
                    i_drawquad(gl, 1);
                }
                /*
                gl.glLoadIdentity();
                gl.glTranslatef(0,-.25,-1.0f+3.0f*(-i_timer+i_offset));
                gl.glColor4f(1,1,1,alphaval);
                gl.glScalef(2.0f,.25f,1);
                i_drawquad(1);
                */
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -1.0f);
                gl.glColor4f(1, 1, 1, alphaval / 2.0f);
                gl.glDisable(GL2.GL_TEXTURE_2D);
                i_drawquad(gl, 1.2f);
                gl.glEnable(GL2.GL_TEXTURE_2D);
            }
            i_offset = 103.5f;
            if ((i_timer > i_offset) && (i_timer < i_offset + 4.0f)) {
                float alphaval = 1.0f - (float)Math.sin((i_timer - i_offset) * 3.1415f / 8.0f);
                i_Text[14].use(gl);
                for (int rep = 1; rep < 5; rep++) {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, 0, -1.0f + (float) (rep) / 5.0f - 1.5f * (float)Math.sqrt(i_timer - i_offset));
                    gl.glColor4f(1, 1, 1, alphaval / rep);
                    gl.glScalef(2.0f, .65f, 1);
                    i_drawquad(gl, 1);
                }
                /*
                gl.glLoadIdentity();
                gl.glTranslatef(0,0,-2.0f);//+1.2f*(-i_timer+i_offset));
                gl.glColor4f(1,1,1,alphaval);
                gl.glScalef(2.0f,.6f,1);
                i_drawquad(1);
                */
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -1.0f);
                gl.glColor4f(1, 1, 1, alphaval / 2.0f);
                gl.glDisable(GL2.GL_TEXTURE_2D);
                i_drawquad(gl, 1.2f);
                gl.glEnable(GL2.GL_TEXTURE_2D);
            }
        }

        if (i_radius[0] < 1.495f) i_radius[0] = -1.5f + ((float) (i_gettime)) / 4000.0f;
        for (int i = 1; i < 7; i++) {
            //if (benchmode) {if ((i_radius[i-1]>0)&&(i_radius[i]<.745f)) i_radius[i]=-1.5f+((float)(i_gettime-limit-6000*i))/2000.0f;}
            //	else
            {
                if ((i_radius[i - 1] > 0) && (i_radius[i] < 1.495f)) i_radius[i] = -1.5f + ((float) (i_gettime - 6000 * i)) / 4000.0f;
            }
        }

        if (i_timer > 108.0f) {
            //********************* FINISH
            //i_Clean();
            return false;
        }

        i_gettime = (long)i_time;
        return true;
    }
}
