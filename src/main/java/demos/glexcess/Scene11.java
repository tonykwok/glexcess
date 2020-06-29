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
import java.util.Random;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
final class Scene11 implements Scene {
    private final Random random = new Random();
    private Texture[] j_Text;
    private static final int numtexs = 19;
    private static boolean init = true;
    private float j_time = 0;

    private final float[] j_FogColor = {1.0f, 1.0f, 1.0f, 1.0f};
    private float j_max = 0.0f;

    private final float j_zeta = -6.7f;
    private float j_radius = 0.0f;

    private final boolean[] th = new boolean[2];
    private final int j_num = 150;

    private static final class j_part {
        float j_x,j_y,xp;
        float rad;
        float phase;
        float spd;
        int r,g,b,a;
        long init;
    }

    private final j_part[] parts = new j_part[j_num];

    private void init(GLAutoDrawable drawable, GLU glu) {
        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();
        j_Text = new Texture[numtexs];
        for (int i = 0; i < j_Text.length; i++) {
            j_Text[i] = new Texture();
        }
        j_max = 0.0f;
        try {
            j_Text[1].load(gl, glu, ResourceRetriever.getResourceAsStream("data/provaz.raw"));
            j_Text[2].load(gl, glu, ResourceRetriever.getResourceAsStream("data/skygs.raw"));
            j_Text[3].load(gl, glu, ResourceRetriever.getResourceAsStream("data/white.raw"));
            j_Text[4].load(gl, glu, ResourceRetriever.getResourceAsStream("data/moon.raw"));
            j_Text[5].load(gl, glu, ResourceRetriever.getResourceAsStream("data/stars1.raw"));
            j_Text[6].load(gl, glu, ResourceRetriever.getResourceAsStream("data/moonmask.raw"));
            j_Text[7].load(gl, glu, ResourceRetriever.getResourceAsStream("data/spread.raw"));
            j_Text[8].load(gl, glu, ResourceRetriever.getResourceAsStream("data/circle.raw"));
            j_Text[9].load(gl, glu, ResourceRetriever.getResourceAsStream("data/circleempty.raw"));
            j_Text[10].load(gl, glu, ResourceRetriever.getResourceAsStream("data/circlefill.raw"));
            j_Text[11].load(gl, glu, ResourceRetriever.getResourceAsStream("data/noise.raw"));
            j_Text[12].load(gl, glu, ResourceRetriever.getResourceAsStream("data/tail.raw"));
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
            j_Text[13].load(gl, glu, ResourceRetriever.getResourceAsStream("data/profile.raw"));
            j_Text[14].load(gl, glu, ResourceRetriever.getResourceAsStream("data/star.raw"));
            j_Text[15].load(gl, glu, ResourceRetriever.getResourceAsStream("data/land.raw"));
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
            j_Text[16].load(gl, glu, ResourceRetriever.getResourceAsStream("data/esaflr.raw"));
            j_Text[17].load(gl, glu, ResourceRetriever.getResourceAsStream("data/credits.raw"));
            j_Text[18].load(gl, glu, ResourceRetriever.getResourceAsStream("data/creditsneg.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glMatrixMode(GL2.GL_PROJECTION);						// Select The Projection Matrix
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 150.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);							// Select The Modelview Matrix

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0, 0, 0, 0);//.07f, 0.1f, 0.25f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        j_FogColor[0] = 1.0f;
        j_FogColor[1] = .8f;
        j_FogColor[2] = .5f;

        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glEnable(GL2.GL_BLEND);

        for (int i = 0; i < j_num; i++) {
            parts[i] = new j_part();
            parts[i].j_x = .001f * ((float) (Math.abs(random.nextInt()) % 12000));
            if (parts[i].j_x > 6.0f) parts[i].j_x -= 12.0f;
            parts[i].j_y = .001f * ((float) (Math.abs(random.nextInt()) % 5000));
            parts[i].rad = .5f + .005f * ((float) (Math.abs(random.nextInt()) % 1000));//parts[i].j_y*.001*((float)(Math.abs(random.nextInt())%1000));
            parts[i].phase = .002f * ((float) (Math.abs(random.nextInt()) % 1000)) * 3.1415f;
            parts[i].spd = .25f + .0025f * ((float) (Math.abs(random.nextInt()) % 1000));

            parts[i].xp = .00044f * ((float) (Math.abs(random.nextInt()) % 1000));
            if (parts[i].xp > 0.22f) parts[i].xp -= .44f;
            parts[i].r = 128 + Math.abs(random.nextInt()) % 128;
            parts[i].b = parts[i].r;
            parts[i].g = parts[i].r;
            parts[i].a = (int) (parts[i].j_y * 51.0f);
        }
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        th[0] = true;
        th[1] = true;
    }

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        j_Text[1].kill(gl);
        j_Text[2].kill(gl);
        j_Text[3].kill(gl);
        j_Text[4].kill(gl);
        j_Text[5].kill(gl);
        j_Text[6].kill(gl);
        j_Text[7].kill(gl);
        j_Text[8].kill(gl);
        j_Text[9].kill(gl);
        j_Text[10].kill(gl);
        j_Text[11].kill(gl);
        j_Text[12].kill(gl);
        j_Text[13].kill(gl);
        j_Text[14].kill(gl);
        j_Text[15].kill(gl);
        j_Text[16].kill(gl);
        j_Text[17].kill(gl);
        j_Text[18].kill(gl);
        init = true;
    }

    private static void j_drawquad(GL2 gl, float size) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-.5f * size, -.5f * size, 0);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(-.5f * size, .5f * size, 0);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(.5f * size, .5f * size, 0);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(.5f * size, -.5f * size, 0);
        gl.glEnd();
    }

    private static void j_drawcred(GL2 gl, float sizew, float sizeh, float pos, float facts) {
//	if (benchmode) return;
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glBegin(GL2.GL_QUAD_STRIP);

        gl.glColor4f(0, 0, 0, 0);
        gl.glTexCoord2f(0.0f, 0.0f + pos);
        gl.glVertex3f(-1 * sizew, -1 * sizeh, 0);
        gl.glTexCoord2f(1.0f, 0.0f + pos);
        gl.glVertex3f(1 * sizew, -1 * sizeh, 0);

        gl.glColor4f(1, 1, 1, 1);
        gl.glTexCoord2f(0.0f, facts * .025f + pos);
        gl.glVertex3f(-1 * sizew, -.9f * sizeh, 0);
        gl.glTexCoord2f(1.0f, facts * .025f + pos);
        gl.glVertex3f(1 * sizew, -.9f * sizeh, 0);

        gl.glTexCoord2f(0.0f, facts * .475f + pos);
        gl.glVertex3f(-1 * sizew, .9f * sizeh, 0);
        gl.glTexCoord2f(1.0f, facts * .475f + pos);
        gl.glVertex3f(1 * sizew, .9f * sizeh, 0);

        gl.glColor4f(0, 0, 0, 0);
        gl.glTexCoord2f(0.0f, facts * .5f + pos);
        gl.glVertex3f(-1 * sizew, 1 * sizeh, 0);
        gl.glTexCoord2f(1.0f, facts * .5f + pos);
        gl.glVertex3f(1 * sizew, 1 * sizeh, 0);

        gl.glEnd();
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
    }

    private void j_drawquad1(GL2 gl, int col, float sizex, float sizey) {
        gl.glBegin(GL2.GL_QUADS);
        if (col > 32) gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) col); else gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 32);
        gl.glTexCoord2f(0.0f + j_radius / 3, 0.0f + j_radius);
        gl.glVertex3f(-.5f * sizex, -.5f * sizey, 0);
        gl.glTexCoord2f(1.0f * 2 + j_radius / 3, 0.0f + j_radius);
        gl.glVertex3f(-.5f * sizex, .5f * sizey, 0);
        gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (col / 2));
        gl.glTexCoord2f(1.0f * 2 + j_radius / 3, 1.0f * 2 + j_radius);
        gl.glVertex3f(.5f * sizex, .5f * sizey, 0);
        gl.glTexCoord2f(0.0f + j_radius / 3, 1.0f * 2 + j_radius);
        gl.glVertex3f(.5f * sizex, -.5f * sizey, 0);
        gl.glEnd();
    }

    private void j_drawquad10(GL2 gl, int col, float sizex, float sizey) {
        gl.glBegin(GL2.GL_QUADS);
        if (col > 32) gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) col); else gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 32);
        gl.glColor4f(0, 0, 0, 0);
        gl.glVertex3f(-.5f * sizex, -.5f * sizey, 0);
        gl.glVertex3f(-.5f * sizex, .5f * sizey, 0);

        gl.glColor4f(j_FogColor[0], j_FogColor[1], j_FogColor[2], 1.75f * (float) col / 255.0f);
        gl.glVertex3f(.5f * sizex, .5f * sizey, 0);
        gl.glVertex3f(.5f * sizex, -.5f * sizey, 0);

        gl.glEnd();
    }

    private void j_drawquad2(GL2 gl, int col, int shd, float sizex, float sizey) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4ub((byte) col, (byte) col, (byte) col, (byte) shd);
        gl.glTexCoord2f(1 + 0.0f + j_radius / 2, 0.0f + j_radius / 2);
        gl.glVertex3f(-.5f * sizex, -.5f * sizey, 0);
        gl.glTexCoord2f(1 + 1.0f * 2 + j_radius / 2, 0.0f + j_radius / 2);
        gl.glVertex3f(-.5f * sizex, .5f * sizey, 0);
        gl.glColor4ub((byte) col, (byte) col, (byte) col, (byte) (shd / 2));
        gl.glTexCoord2f(1 + 1.0f * 2 + j_radius / 2, 1.0f * 2 + j_radius / 2);
        gl.glVertex3f(.5f * sizex, .5f * sizey, 0);
        gl.glTexCoord2f(1 + 0.0f + j_radius / 2, 1.0f * 2 + j_radius / 2);
        gl.glVertex3f(.5f * sizex, -.5f * sizey, 0);
        gl.glEnd();
    }

    private void j_drawquad3(GL2 gl, int col, float sizex, float sizey) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) col);
        gl.glTexCoord2f(0.0f - j_radius / 10.0f, 0.0f);
        gl.glVertex3f(-.5f * sizex, -.5f * sizey, 0);
        gl.glTexCoord2f(1.0f - j_radius / 10.0f, 0.0f);
        gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) col);
        gl.glVertex3f(-.5f * sizex, .5f * sizey, 0);
        gl.glTexCoord2f(1.0f - j_radius / 10.0f, 2.0f);
        gl.glVertex3f(.5f * sizex, .5f * sizey, 0);
        gl.glTexCoord2f(0.0f - j_radius / 10.0f, 2.0f);
        gl.glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) col);
        gl.glVertex3f(.5f * sizex, -.5f * sizey, 0);
        gl.glEnd();
    }

    private void j_drawquad6(GL2 gl, int col, float sizex, float sizey) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) col);
        gl.glTexCoord2f(0.0f - j_radius * 2.0f, 0.0f + j_radius / 2.0f);
        gl.glVertex3f(-.5f * sizex, -.5f * sizey, 0);
        gl.glTexCoord2f(1.0f - j_radius * 2.0f, 0.0f + j_radius / 2.0f);
        gl.glColor4ub((byte) col, (byte) col, (byte) col, (byte) 255);
        gl.glVertex3f(-.5f * sizex, .5f * sizey, 0);
        gl.glTexCoord2f(1.0f - j_radius * 2.0f, 2.0f + j_radius / 2.0f);
        gl.glVertex3f(.5f * sizex, .5f * sizey, 0);
        gl.glTexCoord2f(0.0f - j_radius * 2.0f, 2.0f + j_radius / 2.0f);
        gl.glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) col);
        gl.glVertex3f(.5f * sizex, -.5f * sizey, 0);
        gl.glEnd();
    }

    private void j_drawtrail(GL2 gl, int thickness, int alpha, float length, int r, int g, int b, int a) {
        gl.glPushMatrix();
        for (int p = 0; p < thickness; p++) {
            gl.glTranslatef(0, -1 / length, 0);
            gl.glPushMatrix();
            gl.glRotatef(-alpha, 1, 0, 0);
            gl.glRotatef(p * j_radius * 10, 0, 0, 1);
            gl.glColor4ub((byte) r, (byte) g, (byte) b, (byte) a);
            j_drawquad(gl, j_max * (p * .2f * j_radius / 2) + .00002f * ((float) (Math.abs(random.nextInt()) % 1000)));
            gl.glPopMatrix();
        }
        gl.glPopMatrix();
    }

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float globtime) {
        if (init) {
            init(drawable, glu);
            init = false;
        }
        j_time = 10 * globtime;

        j_radius = -.075f + (j_time) / 45000.0f;

        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        if (j_radius > .0f) {
            if (j_radius < 1.5f) {
                gl.glLoadIdentity();
                gl.glTranslatef(0.0f, 0.75f, j_zeta - 2.0f);
                gl.glRotatef(80, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(0, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(-90, 0.0f, 0.0f, 1.0f);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[4]);
                j_Text[5].use(gl);
                gl.glPushMatrix();
                gl.glLoadIdentity();
                gl.glTranslatef(0, 1.2f, -5);
                j_drawquad3(gl, (int) (32 * j_radius), 6.0f, 2.5f);		// STELLE
                gl.glPopMatrix();

                gl.glLoadIdentity();			// LUNA
                gl.glTranslatef(.55f + 3.0f * (float) Math.cos(.35f + j_radius / 2), .4f + 1.25f * (float) Math.sin(.35 + j_radius / 2), -5);
                gl.glRotatef(-45, 0, 0, 1);
                gl.glColor4f(1, 1, 1, 1);
                gl.glBlendFunc(GL2.GL_DST_COLOR, GL2.GL_ZERO);
                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[5]);
                //j_Text[6].use(gl);
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (148 * (j_radius - .35)));
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                //glBindTexture(GL2.GL_TEXTURE_2D,j_Text[ure[3]);
                j_Text[4].use(gl);
                j_drawquad(gl, 1);

                if ((j_radius - .5f < .053) && (j_radius - .5f > -.007f)) {
                    gl.glLoadIdentity();
                    gl.glTranslatef(-2.5f + (j_radius - .5f) * 25, 1.2f + (j_radius - .5f) * 4.7f, -5);

                    j_Text[12].use(gl);
                    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                    gl.glRotatef(100, 0, 0, 1);		// SCIA
                    gl.glScalef(.1f, (float) Math.sin(55 * (j_radius - .5f)) * (float) Math.sin(55 * (j_radius - .5f)), 1);
                    gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (64 * (float) Math.sin(55 * (j_radius - .5f)) * (float) Math.sin(55 * (j_radius - .5f))));
                    j_drawquad(gl, 1);
                }

                if ((j_radius < .08) && (j_radius > .05)) {
                    gl.glLoadIdentity();
                    gl.glTranslatef(-1.0f + (-.05f + j_radius) * 50, 1.5f - (-.05f + j_radius) * 9, -5);
                    j_Text[12].use(gl);
                    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                    gl.glRotatef(80, 0, 0, 1);		// SCIA
                    gl.glScalef(.07f, .5f * (float) Math.sin(100 * (-.05f + j_radius)) * (float) Math.sin(100 * (-.05f + j_radius)), 1);
                    gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (255 * (float) Math.sin(100 * (-.05 + j_radius)) * (float) Math.sin(100 * (-.05 + j_radius))));
                    j_drawquad(gl, 1);
                }

                gl.glLoadIdentity();
                gl.glTranslatef(0.0f, 0.75f, j_zeta - 2.0f);
                gl.glRotatef(80, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(0, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(-90, 0.0f, 0.0f, 1.0f);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                ///////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////
                gl.glDisable(GL2.GL_TEXTURE_2D);
                gl.glPushMatrix();
                gl.glLoadIdentity();
                gl.glTranslatef(0, 1.4f, j_zeta - 2.0f);
                //glRotatef(-90+j_zrot,0.0f,0.0f,1.0f);
                gl.glRotatef(-90, 0, 0, 1);
                j_drawquad10(gl, 100, 3, 10);
                gl.glPopMatrix();
                gl.glEnable(GL2.GL_TEXTURE_2D);



                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[0]);
                j_Text[1].use(gl);
                j_drawquad1(gl, 100, 10, 15);		// NUVOLE
                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[1]);
                j_Text[2].use(gl);

                if (((j_radius > 1.482) && (j_radius < 1.484)) ||
                        ((j_radius > 1.495)) ||
                        ((j_radius > 1.305) && (j_radius < 1.306)) ||
                        ((j_radius > 1.3) && (j_radius < 1.3025)))
                    j_drawquad2(gl, 128 + Math.abs(random.nextInt()) % 128, 128 + Math.abs(random.nextInt()) % 128, 10, 15);

                else if (j_radius > 1.2)
                    j_drawquad2(gl, 190, (byte) (128 - 128 * (j_radius - 1.2) / .3f), 10, 15);
                else
                    j_drawquad2(gl, 190, 128, 10, 15);
                gl.glLoadIdentity();
                gl.glTranslatef(1.5f * (float) Math.cos(2.2 + j_radius), (float) Math.sin(2.2 + j_radius), -3);
                //glBindTexture(GL2.GL_TEXTURE_2D,j_Text[ure[2]);
                j_Text[3].use(gl);
                gl.glPushMatrix();
                gl.glRotatef(j_radius * 300, 0, 0, 1);
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (255 - 50 * j_radius));
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                if (j_radius < 1.1) j_drawquad(gl, 1.0f - j_radius / 1.75f);
                gl.glPopMatrix();

                if (j_radius * 3.0f < 3.1415) {
                    gl.glRotatef(j_radius * 90, 0, 0, 1);

                    gl.glPushMatrix();
                    //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[8]);
                    j_Text[9].use(gl);
                    gl.glColor4ub((byte) 128, (byte) 160, (byte) 255, (byte) (32 * (float) Math.sin(j_radius * 3.0f)));
                    gl.glTranslatef(j_radius / 1.75f, 0, 0);
                    j_drawquad(gl, 1.25f);
                    gl.glPopMatrix();

                    gl.glPushMatrix();
                    //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[8]);
                    j_Text[9].use(gl);
                    gl.glColor4ub((byte) 192, (byte) 48, (byte) 16, (byte) (80 * (float) Math.sin(j_radius * 3.0f)));
                    gl.glTranslatef(j_radius * 1.5f, 0, 0);
                    j_drawquad(gl, .4f);
                    gl.glPopMatrix();

                    gl.glPushMatrix();
                    //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[9]);
                    j_Text[10].use(gl);
                    gl.glColor4ub((byte) 64, (byte) 192, (byte) 96, (byte) (100 * (float) Math.sin(j_radius * 3.0f)));
                    gl.glTranslatef(j_radius * 1.81f, 0, 0);
                    j_drawquad(gl, .2f);
                    gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (100 * (float) Math.sin(j_radius * 3.0f)));
                    j_drawquad(gl, .1f);
                    gl.glPopMatrix();

                    gl.glPushMatrix();
                    //BindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[7]);
                    j_Text[8].use(gl);
                    gl.glColor4ub((byte) 96, (byte) 128, (byte) 192, (byte) (64 * (float) Math.sin(j_radius * 3.0f)));
                    gl.glTranslatef(j_radius * 2.5f, 0, 0);
                    j_drawquad(gl, .45f);
                    gl.glPopMatrix();

                    gl.glPushMatrix();
                    //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[7]);
                    j_Text[8].use(gl);
                    gl.glColor4ub((byte) 192, (byte) 192, (byte) 160, (byte) (64 * (float) Math.sin(j_radius * 3.0f)));
                    gl.glTranslatef(j_radius * 2.1f, 0, 0);
                    j_drawquad(gl, .3f);
                    gl.glPopMatrix();

                    gl.glPushMatrix();
                    //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[8]);
                    j_Text[9].use(gl);
                    gl.glColor4ub((byte) 132, (byte) 160, (byte) 148, (byte) (32 * (float) Math.sin(j_radius * 3.0f)));
                    gl.glTranslatef(j_radius * 1.25f, 0, 0);
                    j_drawquad(gl, .75f);
                    gl.glPopMatrix();
                }
                gl.glLoadIdentity();			// LUNA
                gl.glTranslatef(.55f + 3.0f * (float) Math.cos(.35 + j_radius / 2), .4f + 1.25f * (float) Math.sin(.35 + j_radius / 2), -5);
                gl.glRotatef(-45, 0, 0, 1);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[6]);
                j_Text[7].use(gl);
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (53.3 * j_radius));
                //j_drawquad(3.0f);

            }		//*************************************************************************************************
            else	//*************************************************************************************************
            {
                gl.glLoadIdentity();
                gl.glTranslatef(0.0f, 0.75f, j_zeta - 2.0f);
                gl.glRotatef(80, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(0, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(-90, 0.0f, 0.0f, 1.0f);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[4]);
                j_Text[5].use(gl);
                gl.glLoadIdentity();
                gl.glTranslatef(0, 1.25f, -5);
                if (j_radius > 2.0f) {
                    j_drawquad3(gl, 255, 6.0f, 2.5f);		// STELLE
                    j_drawquad3(gl, 255, 6.0f, 2.5f);		// STELLE
                    gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
                    //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[10]);
                    j_Text[11].use(gl);
                    j_drawquad6(gl, 192, 6.0f, 2.5f);
                } else {
                    j_drawquad3(gl, (int) (50 + 205 * (2 * (j_radius - 1.5f))), 6.0f, 2.5f);
                    j_drawquad3(gl, (int) (50 + 205 * (2 * (j_radius - 1.5f))), 6.0f, 2.5f);
                    gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
                    //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[10]);
                    j_Text[11].use(gl);
                    j_drawquad6(gl, (int) (192 * (2 * (j_radius - 1.5f))), 6.0f, 2.5f);
                }
                gl.glLoadIdentity();			// LUNA
                gl.glTranslatef(.55f + 3.0f * (float) Math.cos(.35f + j_radius / 2), .4f + 1.25f * (float) Math.sin(.35f + j_radius / 2), -5);
                gl.glRotatef(-45, 0, 0, 1);
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
                gl.glBlendFunc(GL2.GL_DST_COLOR, GL2.GL_ZERO);
                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[5]);
                j_Text[6].use(gl);
                j_drawquad(gl, 1);
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                //glBindTexture(GL2.GL_TEXTURE_2D,j_Text[ure[3]);
                j_Text[4].use(gl);
                j_drawquad(gl, 1);

                gl.glLoadIdentity();
                gl.glTranslatef(0.0f, 0.75f, j_zeta - 2.0f);
                gl.glRotatef(80, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(0, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(-90, 0.0f, 0.0f, 1.0f);

                gl.glDisable(GL2.GL_TEXTURE_2D);
                gl.glPushMatrix();
                gl.glLoadIdentity();
                gl.glTranslatef(0, 1.4f, j_zeta - 2.0f);
                //glRotatef(-90+j_zrot,0.0f,0.0f,1.0f);
                gl.glRotatef(-90, 0, 0, 1);
                j_drawquad10(gl, 100, 3, 10);
                gl.glPopMatrix();
                gl.glEnable(GL2.GL_TEXTURE_2D);

                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_COLOR);
                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[0]);
                j_Text[1].use(gl);
                j_drawquad1(gl, 100, 10, 15);		// NUVOLE
                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[1]);
                j_Text[2].use(gl);
                j_drawquad2(gl, 255, 64, 10, 15);			// NUVOLE

                gl.glLoadIdentity();			// LUNA
                gl.glTranslatef(.55f + 3.0f * (float) Math.cos(.35f + j_radius / 2), .4f + 1.25f * (float) Math.sin(.35 + j_radius / 2), -5);
                gl.glRotatef(-45, 0, 0, 1);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[6]);
                j_Text[7].use(gl);
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 80);
                //j_drawquad(3.0f);
            }

            gl.glLoadIdentity();
            gl.glTranslatef(0, .05f, -8);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[12]);
            j_Text[13].use(gl);
            gl.glRotatef(90, 0, 0, 1);
            gl.glScalef(.51f, -10, 1);
            gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
            j_drawquad(gl, 1);

            gl.glLoadIdentity();
            gl.glTranslatef(-.035f, -.645f, -3);
            gl.glDisable(GL2.GL_BLEND);
            //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[14]);
            j_Text[15].use(gl);
            gl.glRotatef(90, 0, 0, 1);
            gl.glScalef(1.2f, -3.4f, 1);
            if (j_radius < 3.9f)
                gl.glColor4ub((byte) (192 - 192 * j_radius / 4), (byte) (192 - 192 * j_radius / 4), (byte) (192 - 192 * j_radius / 4), (byte) 255);
            else
                gl.glColor4ub((byte) (192 - 192 * 3.9f / 4), (byte) (192 - 192 * 3.9f / 4), (byte) (192 - 192 * 3.9f / 4), (byte) 255);
            j_drawquad(gl, 1);
            gl.glEnable(GL2.GL_BLEND);

            if ((j_radius > .8f) && (j_radius < 1.115)) {
                gl.glLoadIdentity();
                gl.glTranslatef(-2.5f, .05f, -5);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[9]);
                j_Text[10].use(gl);
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (160 * (float) Math.sin((j_radius - .8f) * 10) * (float) Math.sin((j_radius - .8f) * 10)));
                j_drawquad(gl, 1.75f);
            }

            //glDisable(GL2.GL_FOG);
            gl.glLoadIdentity();
            //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[15]);
            j_Text[16].use(gl);
            gl.glTranslatef(0, .5f, -10);
            gl.glRotatef(1.5f, 1, 0, 0);

            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            for (int i = 0; i < j_num; i++) {
                gl.glPushMatrix();
                gl.glTranslatef(-1 + parts[i].j_x / 2.3f, -.6f - parts[i].j_y / 10, 0);
                gl.glColor4ub((byte) parts[i].r, (byte) parts[i].g, (byte) parts[i].b, (byte) (j_max * j_max * parts[i].a * (.75 + .25 * (float) Math.sin(parts[i].phase + j_radius * (i)))));
                gl.glRotatef(j_radius * 200, 0, 0, 1);
                j_drawquad(gl, parts[i].rad / 8);
                gl.glPopMatrix();
            }

            //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[13]);
            j_Text[14].use(gl);
            gl.glTranslatef(-5, -1.2f, -10);
            gl.glRotatef(-7, 0, 1, 0);
            for (int i = 0; i < 10; i++) {
                gl.glPushMatrix();
                gl.glTranslatef(i * .75f, 0, 0);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glRotatef(-90, 1, 0, 0);
                j_drawtrail(gl, 5, -90, .9f, 255, 128, 96, 255);
                gl.glPopMatrix();
            }

            gl.glTranslatef(6, .2f, 6);
            gl.glRotatef(45, 0, 1, 0);
            for (int i = 0; i < 5; i++) {
                gl.glPushMatrix();
                gl.glTranslatef(i * .25f, .1f, 0);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glRotatef(-88, 1, 0, 0);
                j_drawtrail(gl, 3, -88, 1.5f, 255, 192, 128, 255);
                gl.glPopMatrix();
            }

            gl.glTranslatef(-2.5f, .2f, 0);
            gl.glRotatef(-55, 0, 1, 0);
            for (int i = 0; i < 10; i++) {
                gl.glPushMatrix();
                gl.glTranslatef(i * .35f, 0, 0);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glRotatef(-84, 1, 0, 0);
                j_drawtrail(gl, 2, -84, 1.5f, 192, 212, 255, 255);
                gl.glPopMatrix();
            }

            gl.glTranslatef(-1, -.27f, 0);
            gl.glRotatef(63, 0, 1, 0);
            for (int i = 0; i < 8; i++) {
                gl.glPushMatrix();
                gl.glTranslatef(i * .2f, 0, 0);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glRotatef(-85, 1, 0, 0);
                j_drawtrail(gl, 3, -85, 2.5f, 192, 212, 255, 255);
                gl.glPopMatrix();
            }

            gl.glLoadIdentity();
            gl.glTranslatef(-1.77f, -.24f, -5);
            j_Text[12].use(gl);
            gl.glPushMatrix();
            gl.glRotatef(30 * (float) Math.sin(j_radius * 10), 0, 0, 1);
            gl.glScalef(.2f, 1, 1);
            gl.glTranslatef(0, .5f, 0);
            gl.glColor4ub((byte) 128, (byte) 128, (byte) 128, (byte) (128 * j_max));
            j_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glPushMatrix();
            gl.glRotatef(20 * (float) Math.sin(1 + j_radius * 15), 0, 0, 1);
            gl.glScalef(.2f, 1.2f, 1);
            gl.glTranslatef(0, .5f, 0);
            gl.glColor4ub((byte) 128, (byte) 128, (byte) 128, (byte) (128 * j_max * j_max));
            j_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glPushMatrix();
            gl.glRotatef(20 * (float) Math.sin(2 + j_radius * 25), 0, 0, 1);
            gl.glScalef(.2f, 1.5f, 1);
            gl.glTranslatef(0, .5f, 0);
            gl.glColor4ub((byte) 128, (byte) 128, (byte) 128, (byte) (128 * j_max * j_max * j_max));
            j_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glLoadIdentity();
            gl.glTranslatef(-.1f, -.075f, -1);
            //glDisable(GL2.GL_TEXTURE_2D);
            j_Text[3].use(gl);
            //glPointSize(4);
            //glBegin(GL2.GL_POINTS);
            for (int i = 0; i < j_num / 5; i++) {
                float time;
                time = (j_time - parts[i].init) / 250000.0f;

                float alpha = parts[i].a;
                if (parts[i].xp > .2f) alpha = parts[i].a * (1.0f - (parts[i].xp - .2f) * 50.0f);
                if (parts[i].xp < -.23f) alpha = parts[i].a * (1.0f - (-parts[i].xp - .23f) * 50.0f);
                alpha = alpha * j_max;
                if (alpha < 0) alpha = 0;
                if ((i % 2) == 0) {
                    if (parts[i].xp > 0.0f)
                        gl.glColor4ub((byte) (128 + 127 * parts[i].xp * 4), (byte) (128 - 128 * parts[i].xp * 4), (byte) (128 - 128 * parts[i].xp * 4), (byte) alpha);
                    else
                        gl.glColor4ub((byte) 128, (byte) 128, (byte) 128, (byte) alpha);
                    //if (parts[i].xp>0.0f) gl.glColor4ub(255*parts[i].xp*4,255-255*parts[i].xp*4,255-255*parts[i].xp*4,alpha);
                    //else gl.glColor4ub(255,255,255,alpha);
                    //glVertex3f(parts[i].xp,.3*parts[i].xp*parts[i].xp,0);
                    if (parts[i].xp > .22f) {
                        parts[i].spd = .25f + .0025f * ((float) (Math.abs(random.nextInt()) % 1000));
                        parts[i].xp = -.25f;
                        parts[i].init = (long) j_time;
                    } else
                        parts[i].xp = -.25f + parts[i].spd * time;
                } else {
                    if (parts[i].xp < 0.0f)
                        gl.glColor4ub((byte) (128 - 127 * parts[i].xp * 4), (byte) (128 + 128 * parts[i].xp * 4), (byte) (128 + 128 * parts[i].xp * 4), (byte) alpha);
                    else
                        gl.glColor4ub((byte) 128, (byte) 128, (byte) 128, (byte) alpha);
                    //if (parts[i].xp<0.0f) gl.glColor4ub(255-255*parts[i].xp*4,255*parts[i].xp*4,255*parts[i].xp*4,alpha);
                    //else gl.glColor4ub(255,255,255,alpha);
                    //glVertex3f(parts[i].xp,.3*parts[i].xp*parts[i].xp,0);

                    if (parts[i].xp < -.25f) {
                        parts[i].spd = .25f + .0025f * ((float) (Math.abs(random.nextInt()) % 1000));
                        parts[i].xp = +0.22f;
                        parts[i].init = (long) j_time;

                    } else
                        parts[i].xp = .22f - parts[i].spd * time;
                }
                gl.glPushMatrix();
                gl.glTranslatef(parts[i].xp, .3f * parts[i].xp * parts[i].xp, 0);
                j_drawquad(gl, .01f);
                j_drawquad(gl, .015f);
                gl.glPopMatrix();
            }
            //glEnd();

            //glEnable
            float credinit = -.6f;
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glColor4f(.75f, .75f, .75f, 1);
            j_Text[18].use(gl);
            gl.glLoadIdentity();
            gl.glScalef(1, -1, 1);
            gl.glTranslatef(1.1f, 0, -3.0f);
            j_drawcred(gl, .45f, 1.25f, credinit + j_radius / 3.1f, .9f);
            j_Text[17].use(gl);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
            //glBindTexture(GL2.GL_TEXTURE_2D, j_Text[ure[1]);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glColor4f(1, 1, 1, 1);
            j_drawcred(gl, .45f, 1.25f, credinit + j_radius / 3.1f, .9f);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_NEAREST);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            //glEnable(GL2.GL_FOG);


            float lime = .75f;
            if (j_radius > lime) {
                if (j_FogColor[0] > 0.2f) j_FogColor[0] = 1.0f - (j_radius - lime);
                if (j_FogColor[1] > 0.2f) j_FogColor[1] = .8f - (j_radius - lime) * .9f;
                if (j_FogColor[2] > 0.2f) j_FogColor[2] = .5f - (j_radius - lime) * .5f;
            }
            gl.glClearColor(.07f - j_radius / 5.0f, 0.1f - j_radius / 5.0f, 0.25f - j_radius / 5.0f, 0.0f);
            if ((j_radius > .5f) && (j_radius < 1.5f)) j_max = j_radius - .5f;
        }
        if (j_radius < .075) {
            float j_fader = .5f * (1.0f + (float) Math.cos(j_radius * 3.1415f / .075f));
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            gl.glColor4f(1, 1, 1, j_fader);
            j_drawquad(gl, 1.2f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }
        if (j_radius > 4.25f) {
            float j_fader = .5f * (1.0f - (float) Math.cos((j_radius - 4.25f) * 3.1415f / .5f));
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_ALPHA);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            gl.glColor4f(1, 1, 1, j_fader);
            j_drawquad(gl, 1.2f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }
        if (j_radius > 4.75f) {
            //******************** FINISH
            //j_Clean();
            return false;
        }
/*	if ((j_radius>1.32)&&(th[0]))
	{
		th[0]=!th[0];
		FSOUND_PlaySound(FSOUND_FREE, th1);
		FSOUND_PlaySound(FSOUND_FREE, th1);
		FSOUND_PlaySound(FSOUND_FREE, th1);
	}
	if ((j_radius>1.48)&&(th[1]))
	{
		th[1]=!th[1];
		FSOUND_PlaySound(FSOUND_FREE, th2);
		FSOUND_PlaySound(FSOUND_FREE, th2);
		FSOUND_PlaySound(FSOUND_FREE, th2);
	}
*/
        return true;
    }

}
