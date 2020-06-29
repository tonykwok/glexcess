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
import com.jogamp.opengl.util.GLBuffers;
import demos.common.ResourceRetriever;

import java.io.IOException;
import java.util.Random;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
final class Scene9 implements Scene {
    private final Random random = new Random();
    private Texture[] h_Text;
    private static final int numtexs = 16;
    private static boolean init = true;
    private float h_time = 0;

    private final float[] h_FogColor = {1.0f, 1.0f, 1.0f, 1.0f};
    private int h_y;


    private float h_timer = 0.0f;

    private float h_zeta = 0.0f;
    private float h_radius = 0.0f;

    private final int h_num = 100;
    private final int h_num1 = 150;

    private static final class h_part {
        long init;
        float size;
        float phase;
        float rotspd;
        float spd;
        float h_y;
        float a;
        float a1;
    }

    private static final class h_part1 {
        float size;
        float phase;
        float amp;
        float spd;
        float x,h_y,xpos;
        int r;
        int g;
        int b;
        int a;

        long init;
    }

    private final h_part[] parts = new h_part[h_num];
    private final h_part1[] parts1 = new h_part1[h_num1];

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        h_Text[1].kill(gl);
        h_Text[2].kill(gl);
        h_Text[3].kill(gl);
        h_Text[4].kill(gl);
        h_Text[5].kill(gl);
        h_Text[6].kill(gl);
        h_Text[7].kill(gl);
        h_Text[8].kill(gl);
        h_Text[9].kill(gl);
        h_Text[10].kill(gl);
        h_Text[11].kill(gl);
        h_Text[12].kill(gl);
        h_Text[13].kill(gl);
        h_Text[14].kill(gl);
        h_Text[15].kill(gl);
        init = true;
    }

    private void init(GLAutoDrawable drawable, GLU glu) {
        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();
        h_Text = new Texture[numtexs];
        h_timer = 0.0f;
        h_time = 0.0f;
        h_zeta = 0.0f;
        h_radius = 0.0f;

        h_y = 0;
        h_timer = 0.0f;
        h_zeta = 0.0f;
        h_radius = 0.0f;

        for (int i = 0; i < h_Text.length; i++) {
            h_Text[i] = new Texture();
        }
        try {
            h_Text[1].load(gl,glu,ResourceRetriever.getResourceAsStream("data/fallfront1.raw"));
            h_Text[2].load(gl,glu,ResourceRetriever.getResourceAsStream("data/water.raw"));
            h_Text[3].load(gl,glu,ResourceRetriever.getResourceAsStream("data/fallleft1.raw"));
            h_Text[4].load(gl,glu,ResourceRetriever.getResourceAsStream("data/cl.raw"));
            h_Text[5].load(gl,glu,ResourceRetriever.getResourceAsStream("data/fallleftmask.raw"));
            h_Text[6].load(gl,glu,ResourceRetriever.getResourceAsStream("data/fallright.raw"));
            h_Text[7].load(gl,glu,ResourceRetriever.getResourceAsStream("data/fallrightmask.raw"));
            h_Text[8].load(gl,glu,ResourceRetriever.getResourceAsStream("data/floodmask1.raw"));
            h_Text[9].load(gl,glu,ResourceRetriever.getResourceAsStream("data/smoke.raw"));
            h_Text[10].load(gl,glu,ResourceRetriever.getResourceAsStream("data/circlefill.raw"));
            h_Text[11].load(gl,glu,ResourceRetriever.getResourceAsStream("data/cl1.raw"));
            h_Text[12].load(gl,glu,ResourceRetriever.getResourceAsStream("data/noise1.raw"));
            h_Text[13].load(gl,glu,ResourceRetriever.getResourceAsStream("data/circleempty.raw"));
            h_Text[14].load(gl,glu,ResourceRetriever.getResourceAsStream("data/watpt.raw"));
            h_Text[15].load(gl,glu,ResourceRetriever.getResourceAsStream("data/water1.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 25.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_EXP2);
        gl.glFogf(GL2.GL_FOG_START, 8.5f);
        gl.glFogf(GL2.GL_FOG_END, 9.0f);
        gl.glFogf(GL2.GL_FOG_DENSITY, 0.175f);
        h_FogColor[0] = 0.0f;
        h_FogColor[1] = 0.0f;
        h_FogColor[2] = 0.0f;
        gl.glFogfv(GL2.GL_FOG_COLOR, GLBuffers.newDirectFloatBuffer(h_FogColor));

        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_BLEND);
        gl.glDisable(GL2.GL_FOG);

        for (int i = 0; i < h_num; i++) {
            parts[i] = new h_part();
            parts[i].size = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
            parts[i].phase = 3.1415f + .001f * ((float) (Math.abs(random.nextInt()) % 1000));
            parts[i].rotspd = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
            parts[i].spd = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
            parts[i].h_y = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
            parts[i].a = ((float) (Math.abs(random.nextInt()) % 128)) / 255.0f;
        }
        for (h_y = 0; h_y < h_num1; h_y++) {
            parts1[h_y] = new h_part1();
            parts1[h_y].amp = 0.0f;
            parts1[h_y].spd = .1f + .00025f * ((float) (Math.abs(random.nextInt()) % 1000));
            parts1[h_y].size = .5f + .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
            parts1[h_y].r = 64 + Math.abs(random.nextInt()) % 64;
            parts1[h_y].g = parts1[h_y].r;
            parts1[h_y].b = parts1[h_y].r + Math.abs(random.nextInt()) % 16;
            parts1[h_y].x = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
            parts1[h_y].xpos = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
        }
        for (int xs = 0; xs < h_num; xs++) parts[xs].init = (long)h_time;
        for (int xs = 0; xs < h_num1; xs++) parts1[xs].init = (long)h_time;
    }

    private static void h_drawquad(GL2 gl, float size) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-0.5f * size, -0.5f * size, 0);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(0.5f * size, -0.5f * size, 0);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(0.5f * size, 0.5f * size, 0);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-0.5f * size, 0.5f * size, 0);
        gl.glEnd();
    }

    private void h_drawquad0(GL2 gl, int subdiv, float fact, int shd) {
        float a = 3.0f;
        float b = 3.0f;
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)shd);
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 1; i < subdiv; i++) {
            gl.glTexCoord2f(1.0f, h_radius + fact * ((float) i) / ((float) subdiv));
            gl.glVertex3f(.25f + .0025f * ((float) subdiv / i), a * (float)Math.cos((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float)Math.sin((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f));
            gl.glTexCoord2f(0.0f, h_radius + fact * ((float) i) / ((float) subdiv));
            gl.glVertex3f(-.25f - .0025f * ((float) subdiv / i), a * (float)Math.cos((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float)Math.sin((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f));
        }
        gl.glEnd();
    }

    private void h_drawquad1(GL2 gl, int subdiv, float fact, int shd, float offset) {
        float a = 3.0f;
        float b = 3.0f;
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)shd);
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 1; i < subdiv; i++) {
            gl.glTexCoord2f(1.0f + offset + h_radius / 5, h_radius + fact * ((float) i) / ((float) subdiv));
            gl.glVertex3f(.25f - .01f * ((float) subdiv / i), a * (float)Math.cos((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float)Math.sin((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f));
            gl.glTexCoord2f(0.0f + offset + h_radius / 5, h_radius + fact * ((float) i) / ((float) subdiv));
            gl.glVertex3f(-.25f + .01f * ((float) subdiv / i), a * (float)Math.cos((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float)Math.sin((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f));
        }
        gl.glEnd();
    }

    private void h_drawquad2(GL2 gl, float size, float shade) {
        float shift = h_radius * .75f;
        float ix = .5f;
        float iy = .35f;
        gl.glBegin(GL2.GL_QUAD_STRIP);
        gl.glTexCoord2f(0.0f * 1 - (float)Math.cos(shift) * ix, 0.0f * 2 + (float)Math.sin(2.0f * shift) * iy);
        gl.glColor4f(0, 0, 0, 0);
        gl.glVertex3f(-.5f * size, -.5f * size, 0);
        gl.glTexCoord2f(1.0f * 1 - (float)Math.cos(shift) * ix, 0.0f * 2 + (float)Math.sin(2.0f * shift) * iy);
        gl.glVertex3f(.5f * size, -.5f * size, 0);

        gl.glColor4f(1, 1, 1, shade);
        gl.glTexCoord2f(0.0f * 1 - (float)Math.cos(shift) * ix, 0.5f * 2 + (float)Math.sin(2.0f * shift) * iy);
        gl.glVertex3f(-.5f * size, 0, 0);
        gl.glTexCoord2f(1.0f * 1 - (float)Math.cos(shift) * ix, 0.5f * 2 + (float)Math.sin(2.0f * shift) * iy);
        gl.glVertex3f(.5f * size, 0, 0);

        gl.glTexCoord2f(0.0f * 1 - (float)Math.cos(shift) * ix, 1.0f * 2 + (float)Math.sin(2.0f * shift) * iy);
        gl.glVertex3f(-.5f * size, .5f * size, 0);
        gl.glTexCoord2f(1.0f * 1 - (float)Math.cos(shift) * ix, 1.0f * 2 + (float)Math.sin(2.0f * shift) * iy);
        gl.glVertex3f(.5f * size, .5f * size, 0);

        gl.glEnd();
    }

    private void h_drawquad2b(GL2 gl, float size, float shade) {
        gl.glBegin(GL2.GL_QUAD_STRIP);
        gl.glTexCoord2f(0.0f * 2 + h_radius / 2.5f, 0.0f * 2 + h_radius / 2);
        gl.glColor4f(1, 1, 1, shade);
        gl.glVertex3f(-.5f * size, -.5f * size, 0);
        gl.glTexCoord2f(1.0f * 2 + h_radius / 2.5f, 0.0f * 2 + h_radius / 2);
        gl.glVertex3f(.5f * size, -.5f * size, 0);

        gl.glTexCoord2f(0.0f * 2 + h_radius / 2.5f, 0.5f * 2 + h_radius / 2);
        gl.glVertex3f(-.5f * size, 0, 0);
        gl.glTexCoord2f(1.0f * 2 + h_radius / 2.5f, 0.5f * 2 + h_radius / 2);
        gl.glVertex3f(.5f * size, 0, 0);

        gl.glColor4f(0, 0, 0, 0);
        gl.glTexCoord2f(0.0f * 2 + h_radius / 2.5f, 1.0f * 2 + h_radius / 2);
        gl.glVertex3f(-.5f * size, .5f * size, 0);
        gl.glTexCoord2f(1.0f * 2 + h_radius / 2.5f, 1.0f * 2 + h_radius / 2);
        gl.glVertex3f(.5f * size, .5f * size, 0);

        gl.glEnd();
    }

    private void h_drawquad3(GL2 gl, int subdiv, float fact, int shd, float offset) {
        float a = 3.0f;
        float b = 3.0f;
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)shd);
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 1; i < subdiv; i++) {
            gl.glTexCoord2f(1.0f + offset, h_radius + fact * ((float) i) / ((float) subdiv));
            gl.glVertex3f(.25f - .01f * ((float) subdiv / i), a * (float)Math.cos((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float)Math.sin((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f));
            gl.glTexCoord2f(0.0f + offset, h_radius + fact * ((float) i) / ((float) subdiv));
            gl.glVertex3f(-.25f + .01f * ((float) subdiv / i), a * (float)Math.cos((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float)Math.sin((90.0f / subdiv) * i * 2 * 3.1415 / 360.0f));
        }
        gl.glEnd();
    }

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float globtime) {
        if (init) {
            init(drawable, glu);
            init = false;
        }
        h_time = 10 * globtime;

        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glEnable(GL2.GL_BLEND);
        //glDisable(GL2.GL_DEPTH_TEST);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 1.9f + h_zeta / 175.0f, -10);
        h_Text[14].use(gl);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_COLOR);
        for (int zx = 0; zx < h_num1; zx++) {
            float time;
            time = (h_time - parts1[zx].init);
            gl.glPushMatrix();
            if ((zx % 2) == 0)
                gl.glTranslatef(-parts1[zx].xpos / 1.5f - parts1[zx].x / 4.0f, -.03f + parts1[zx].h_y, (float) (zx % 10) / 10);
            else
                gl.glTranslatef(parts1[zx].xpos / 1.5f + parts1[zx].x / 4.0f, -.03f + parts1[zx].h_y, (float) (zx % 10) / 10);
            gl.glColor4ub((byte)parts1[zx].r, (byte)parts1[zx].g, (byte)parts1[zx].b, (byte)(255 + 255 * ((parts1[zx].h_y) / 2.5f)));
            gl.glTranslatef(0, -.5f, 0);
            if ((zx % 3) == 0) gl.glRotatef(180, 1, 0, 0);
            h_drawquad(gl, parts1[zx].amp);
            if (parts1[zx].amp < parts1[zx].spd * parts1[zx].size * 5.0f) parts1[zx].amp = .001f * time;
            parts1[zx].x = parts1[zx].spd * time / 500.0f;
            parts1[zx].h_y = -1.75f * parts1[zx].x * parts1[zx].x;

            if (parts1[zx].h_y < -2.5) {
                parts1[zx].init = (long)h_time;

                parts1[zx].spd = .05f + .00025f * ((float) (Math.abs(random.nextInt()) % 1000));
                parts1[zx].xpos = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                parts1[zx].amp = 0.0f;
                parts1[zx].x = 0.0f;
                parts1[zx].h_y = 0.0f;
                parts1[zx].r = Math.abs(random.nextInt()) % 64;
                parts1[zx].g = parts1[zx].r + Math.abs(random.nextInt()) % 8;
                parts1[zx].b = parts1[zx].g + Math.abs(random.nextInt()) % 16;
            }
            gl.glPopMatrix();
        }

        gl.glLoadIdentity();
        gl.glTranslatef(1, 0, -17);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);

        gl.glPushMatrix();
        gl.glScalef(1.5f, 1.0f, 1.0f);
        gl.glTranslatef(1.55f, 1.9f, .1f + h_zeta / 20.0f);
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)255);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[0]);
        h_Text[1].use(gl);

        h_drawquad(gl, 10.2f);									// FRONT

        gl.glPopMatrix();
        gl.glEnable(GL2.GL_BLEND);
//glutSwapBuffers();	h_time+=1;return;
        gl.glPushMatrix();
        gl.glScalef(1.75f, 2.0f, 1);
        gl.glTranslatef(-.8f, -1.72f, h_zeta / 20);
                //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[3]);
                h_Text[4].use(gl);
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)128);
        h_drawquad1(gl, 20, .2f, 128, .7f);							// FALL
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(1, 2, 1);
        gl.glTranslatef(-2.1f, -1.71f, h_zeta / 20);
                //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[10]);
                h_Text[11].use(gl);
        h_drawquad3(gl, 20, .3f, 128, .2f);							// FALL
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(1, 2, 1);
        gl.glTranslatef(.1f, -1.71f, h_zeta / 20);
                //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[10]);
//	h_Text[11].use(gl);
                h_drawquad3(gl, 20, .4f, 128, .2f);							// FALL
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(3, 2, 1);
        gl.glTranslatef(-.15f, -1.71f, h_zeta / 20);
                //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[3]);
                h_Text[4].use(gl);
        h_drawquad1(gl, 20, .6f, 128, .5f);							// FALL
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(4.5f, 2, 1);
        gl.glTranslatef(-.24f, -1.725f, h_zeta / 20);
                //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[10]);
                h_Text[11].use(gl);
        h_drawquad0(gl, 20, 1.0f, 128);								// FALL
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(.5f, 2.2f, 1);
        gl.glTranslatef(4, -1.5f, h_zeta / 20);
                //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[11]);
                h_Text[12].use(gl);
        //glTexParameterf(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        h_drawquad3(gl, 20, .2f, 192, 0.0f);							// FALL
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(.3f, 3, 1);
        gl.glTranslatef(-13, -1.2f, h_zeta / 20);
                //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[11]);
                //h_Text[12].use(gl);
                h_drawquad3(gl, 20, .1f, 160, .3f);							// FALL
        gl.glPopMatrix();

        gl.glPushMatrix();
        //glEnable(GL2.GL_FOG);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[1]);
        h_Text[2].use(gl);
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)128);
        gl.glTranslatef(-1.0f, -3.0f, 4.4f);//+h_zeta/20);
        gl.glRotatef(90, 1, 0, 0);
        gl.glScalef(1.55f, 1.5f, 1);
        h_drawquad2(gl, 7, .25f + .1f * (float)Math.cos(h_timer / 2.0f));				// WATER
        //glRotatef(90,0,0,1);
        gl.glRotatef(180, 1, 0, 0);
        h_Text[15].use(gl);
        h_drawquad2b(gl, 7, .25f + .1f * (float)Math.sin(h_timer / 5.0f));
        gl.glDisable(GL2.GL_FOG);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(0, -1, -5 + h_zeta / 55);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[9]);			// BIG SPOT
        h_Text[10].use(gl);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glRotatef(90, 1, 0, 0);
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)255);
        h_drawquad(gl, 3.5f);
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)128);
        h_drawquad(gl, 1.2f);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(1.065f, -.987f, -5.0f + h_zeta / 55);					// SPOT
        gl.glRotatef(90, 1, 0, 0);
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)128);
        h_drawquad(gl, .25f);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[12]);
        h_Text[13].use(gl);
        for (int p = 0; p < 5; p++) {
            gl.glColor4f(1, 1, 1, parts[p].a1 / 2.0f);
            h_drawquad(gl, parts[p].size / 1.5f);
        }
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(-1.03f, -1.027f, -5.0f + h_zeta / 55);				// SPOT
        gl.glRotatef(90, 1, 0, 0);
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)128);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[9]);
        h_Text[10].use(gl);
        h_drawquad(gl, .15f);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[12]);
        h_Text[13].use(gl);
        for (int p = 5; p < 10; p++) {
            gl.glColor4f(1, 1, 1, parts[p].a1 / 2.0f);
            h_drawquad(gl, parts[p].size / 1.5f);
        }
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(0, -1.0f, -5 + h_zeta / 55);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[8]);			// PARTS
        h_Text[9].use(gl);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        for (int p = 0; p < h_num; p++) {
            long time;
            time = (long)(h_time - parts[p].init);

            gl.glPushMatrix();
            gl.glTranslatef(-.6f + 1.2f * (float) p / h_num, parts[p].h_y, 0);
            //glColor4ub(255,255,255,parts[p].a);
            parts[p].a1 = parts[p].a - ((float) time / 11000.0f);
            gl.glColor4f(1.0f, 1.0f, 1.0f, parts[p].a1);
            if ((p % 2) == 0) {
                gl.glRotatef(parts[p].phase * 100 + 1000 * h_radius * parts[p].rotspd, 0, 0, 1);
                //glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);
            } else {
                gl.glRotatef(parts[p].phase * 100 - 1000 * h_radius * parts[p].rotspd, 0, 0, 1);
                //glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE);
            }
            h_drawquad(gl, parts[p].size);
            gl.glPopMatrix();

            parts[p].h_y = parts[p].spd * (float) time / 6000.0f;//parts[p].spd/10;
            parts[p].size = parts[p].rotspd * (float) time / 2500.0f;//parts[p].rotspd/50;
            //parts[p].a=1.0f
            //if (h_factor<1.0f) parts[p].a--; else
            //parts[p].a-=(int)(.5f+h_factor);

            //if (parts[p].a<0)
            if (parts[p].a1 < 0.0f) {
                parts[p].init = (long)h_time;

                parts[p].h_y = 0.0f;
                parts[p].size = 0.0f;
                parts[p].phase = 3.1415f + .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                parts[p].rotspd = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                parts[p].spd = .25f + .00075f * ((float) (Math.abs(random.nextInt()) % 1000));
                parts[p].a = ((float) (Math.abs(random.nextInt()) % 128)) / 255.0f;
            }
        }
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glScalef(2.0f, .58f, 1);
        gl.glTranslatef(0, -2.32f, -5.4f);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[7]);			// MASK
        h_Text[8].use(gl);
        gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
        h_drawquad(gl, 3);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(2, 4, 1);
        gl.glTranslatef(-3.0f - h_zeta / 30, 0, 2.5f + h_zeta / 10);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[4]);			// LEFT
        h_Text[5].use(gl);
        //glBlendFunc(GL2.GL_DST_COLOR,GL2.GL_ZERO);
        gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
        gl.glColor4f(1, 1, 1, 1);
        h_drawquad(gl, 3);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[2]);
        h_Text[3].use(gl);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        //glBlendFunc(GL2.GL_SRC_COLOR,GL2.GL_ONE_MINUS_SRC_COLOR);
        gl.glColor4f(.99f, .99f, .99f, 1);
        h_drawquad(gl, 3);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(4, 4, 1);
        gl.glTranslatef(.35f + h_zeta / 25, -.2f, 2 + h_zeta / 10);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[6]);			// RIGHT
        h_Text[7].use(gl);
        gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
        gl.glColor4f(1, 1, 1, 1);
        h_drawquad(gl, 3);
        //glBindTexture(GL2.GL_TEXTURE_2D, h_Text[ure[5]);
        h_Text[6].use(gl);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glColor4f(.99f, .99f, .99f, 1);
        h_drawquad(gl, 3);
        gl.glPopMatrix();


        if (h_timer < 4.0f) {
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glColor4f(1, 1, 1, .5f * (1.0f + (float)Math.cos(h_timer * 3.1415 / 4.0f)));
            h_drawquad(gl, 1.2f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }
        if (h_timer > 52.0f) {
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            float shader = .5f * (1.0f - (float)Math.cos((h_timer - 52.0f) * 3.1415 / 8.0f));
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glColor4f(shader, shader, shader, .5f);
            h_drawquad(gl, 1.2f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        h_radius = -h_timer / 10.0f;

        if (h_timer < 3.0f)
            h_zeta = 2.0f * (1.0f - (float)Math.cos(h_timer * 3.1415f / 6.0f));
        else
            h_zeta = 2.0f + (h_timer - 3.0f);

        h_timer = (h_time) / 950.0f;
        if (h_timer > 60.0f) {
            //***************** FINISH
            //h_Clean();
            return false;
        }
        return true;
    }
}
