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
final class Scene4 implements Scene {
    private final Random random = new Random();
    private Texture[] d_Text;
    private static final int numtexs = 9;
    private static boolean init = true;
    private int d_time = 0;

    private int d_y;
    private int d_timer1 = 0;
    private int d_timer2 = 0;
    private int d_timer3 = 0;
    private int d_timer4 = 0;
    private int d_timer5 = 0;
    private int d_offset = 0;

    private int d_ct = 0;

    private final int d_num1 = 20;
    private final int d_num2 = 20;
    private final int d_num3 = 20;
    private final int d_num4 = 20;
    private final int d_num5 = 75;

    private final int d_repeat = 11;

    private static final class d_part {
        float size;
        float phase;
        float mod;
        float axrot;
        float spd;
        float x,d_y;
        float fct;
        int r;
        int g;
        int b;
        int a;
    }

    private d_part[][] xp1;
    private d_part[][] xp2;
    private d_part[][] xp3;
    private d_part[][] xp4;
    private d_part[][] xp5;
    private final float[] d_radius = new float[d_repeat];
    private final boolean[] d_sound = new boolean[d_repeat];

    private final float[] d_off = new float[d_repeat];

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        d_Text[1].kill(gl);
        d_Text[2].kill(gl);
        d_Text[3].kill(gl);
        d_Text[4].kill(gl);
        d_Text[5].kill(gl);
        d_Text[6].kill(gl);
        d_Text[7].kill(gl);
        d_Text[8].kill(gl);
        init = true;
    }

    private void init(GLAutoDrawable drawable, GLU glu) {
        d_time = 0;
        d_timer1 = 0;
        d_timer2 = 0;
        d_timer3 = 0;
        d_timer4 = 0;
        d_timer5 = 0;
        d_offset = 0;

        d_ct = 0;

        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 90.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        d_Text = new Texture[numtexs];
        for (int i = 0; i < d_Text.length; i++) {
            d_Text[i] = new Texture();
        }
        try {
            d_Text[1].load(gl, glu, ResourceRetriever.getResourceAsStream("data/envmap1.raw"));
            d_Text[2].load(gl, glu, ResourceRetriever.getResourceAsStream("data/xp1.raw"));
            d_Text[3].load(gl, glu, ResourceRetriever.getResourceAsStream("data/xp2.raw"));
            d_Text[4].load(gl, glu, ResourceRetriever.getResourceAsStream("data/xp9.raw"));
            d_Text[5].load(gl, glu, ResourceRetriever.getResourceAsStream("data/xp4.raw"));
            d_Text[6].load(gl, glu, ResourceRetriever.getResourceAsStream("data/xp8.raw"));
            d_Text[7].load(gl, glu, ResourceRetriever.getResourceAsStream("data/logocol.raw"));
            d_Text[8].load(gl, glu, ResourceRetriever.getResourceAsStream("data/smoke.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glShadeModel(GL2.GL_FLAT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glDisable(GL2.GL_DEPTH_TEST);

        xp1 = new d_part[d_repeat][d_num1];
        xp2 = new d_part[d_repeat][d_num2];
        xp3 = new d_part[d_repeat][d_num3];
        xp4 = new d_part[d_repeat][d_num4];
        xp5 = new d_part[d_repeat][d_num5];
        for (int i = 0; i < d_repeat; i++) {
            xp1[i] = new d_part[d_num1];
            xp2[i] = new d_part[d_num2];
            xp3[i] = new d_part[d_num3];
            xp4[i] = new d_part[d_num4];
            xp5[i] = new d_part[d_num5];

            d_rst1(i);
            d_rst2(i);
            d_rst3(i);
            d_rst4(i);
            d_rst5(i);
            d_sound[i] = true;
            d_radius[i] = -.5f;
        }
        d_rstoff();

        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);	// Really Nice Perspective Calculations
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glFrontFace(GL2.GL_CCW);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glEnable(GL2.GL_BLEND);
    }

    private static void d_drawquad(GL2 gl, float size) {
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

    private void d_drawtri(GL2 gl, int i, int d_y, float size, float xrot, float yrot, float zrot) {
        gl.glRotatef(xp5[i][d_y].mod * xrot, 1, 0, 0);
        gl.glRotatef(xp5[i][d_y].mod * yrot, 0, 1, 0);
        gl.glRotatef(xp5[i][d_y].mod * zrot, 0, 0, 1);
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex3f(-0.5f * size, -0.5f * size, 0.0f);
        gl.glVertex3f(0.5f * size, -0.5f * size * ((float) d_y / d_num5), 0.0f);
        gl.glVertex3f(0.5f * size * ((float) d_y / d_num5), 0.5f * size, 0.0f);
        gl.glEnd();
    }

    private void d_xpls1(GL2 gl, int i) {
        d_Text[5].use(gl);
        if ((d_timer2 > 10) && (d_timer1 < d_num1)) d_timer1++;
        for (int d_y = 0; d_y < d_timer1; d_y++) {


            // DRAW PART
            if (xp1[i][d_y].a > 0) {
                gl.glPushMatrix();
                gl.glColor4ub((byte)xp1[i][d_y].r, (byte)xp1[i][d_y].g, (byte)xp1[i][d_y].b, (byte)xp1[i][d_y].a);
                gl.glRotatef(xp1[i][d_y].phase, 0, 0, 1);
                gl.glTranslatef(xp1[i][d_y].mod, 0, 0);
                gl.glRotatef(xp1[i][d_y].axrot, 0, 0, 1);
                d_drawquad(gl, xp1[i][d_y].size);

                // UPDATE VARS

                xp1[i][d_y].mod += xp1[i][d_y].spd / 3.0f;
                xp1[i][d_y].size += xp1[i][d_y].spd / 2.0f;
                xp1[i][d_y].axrot = xp1[i][d_y].axrot + .125f;
                if (xp1[i][d_y].size > 1.5f * xp1[i][d_y].fct) xp1[i][d_y].spd = xp1[i][d_y].fct * xp1[i][d_y].spd / 1.1f;
                if (xp1[i][d_y].spd < 0.0125f) xp1[i][d_y].spd = 0.0125f;
                if (xp1[i][d_y].r >= 130) xp1[i][d_y].r -= 1 + (int) (xp1[i][d_y].mod);
                if (xp1[i][d_y].g >= 90) xp1[i][d_y].g -= 2 + 2 * (int) (xp1[i][d_y].mod);
                if (xp1[i][d_y].b >= 24) xp1[i][d_y].b -= 4 + 4 * (int) (xp1[i][d_y].mod);
                xp1[i][d_y].a -= (int) (2.0f);

                if (xp1[i][d_y].a < 1) xp1[i][d_y].a = 0;

                gl.glPopMatrix();
            }
        }
    }

    private void d_xpls2(GL2 gl, int i) {
        //glBindTexture(GL2.GL_TEXTURE_2D, d_texture[2]);
        d_Text[3].use(gl);
        if (d_timer2 < d_num2) d_timer2++;
        for (d_y = 0; d_y < d_timer2; d_y++) {
            if (xp2[i][d_y].a > 0) {
                gl.glPushMatrix();

                // DRAW PART

                gl.glColor4ub((byte)xp2[i][d_y].r, (byte)xp2[i][d_y].g, (byte)xp2[i][d_y].b, (byte)xp2[i][d_y].a);
                gl.glRotatef(xp2[i][d_y].phase + 2.0f * ((float) d_y / d_num2), 0, 0, 1);
                gl.glTranslatef(xp2[i][d_y].mod, 0, 0);
                gl.glRotatef(xp2[i][d_y].axrot * 3f, 0, 0, 1);
                d_drawquad(gl, xp2[i][d_y].size);

                // UPDATE VARS

                xp2[i][d_y].mod += xp2[i][d_y].spd / 5.0f;
                xp2[i][d_y].size += xp2[i][d_y].spd * 1.25;
                xp2[i][d_y].axrot = xp2[i][d_y].axrot + .125f;
                if (xp2[i][d_y].size > .75f * xp2[i][d_y].fct) xp2[i][d_y].spd = xp2[i][d_y].fct * xp2[i][d_y].spd / 1.1f;
                if (xp2[i][d_y].spd < 0.0125f) xp2[i][d_y].spd = 0.0125f;
                if (xp2[i][d_y].r >= 130) xp2[i][d_y].r -= 1 + (int) (xp2[i][d_y].mod);
                if (xp2[i][d_y].g >= 90) xp2[i][d_y].g -= 2 + 2 * (int) (xp2[i][d_y].mod);
                if (xp2[i][d_y].b >= 24) xp2[i][d_y].b -= 4 + 4 * (int) (xp2[i][d_y].mod);
                xp2[i][d_y].a -= (int) (2.0f);

                if (xp2[i][d_y].a < 1) xp2[i][d_y].a = 0;

                gl.glPopMatrix();
            }
        }
    }

    private void d_xpls3(GL2 gl, int i) {
        //glBindTexture(GL2.GL_TEXTURE_2D, d_texture[1]);
        d_Text[2].use(gl);
        if (d_timer3 < d_num3) d_timer3++;
        for (d_y = 0; d_y < d_timer3; d_y++) {
            if (xp3[i][d_y].a > 0) {
                gl.glPushMatrix();

                // DRAW PART

                gl.glColor4ub((byte)xp3[i][d_y].r, (byte)xp3[i][d_y].g, (byte)xp3[i][d_y].b, (byte)xp3[i][d_y].a);
                gl.glRotatef(xp3[i][d_y].phase, 0, 0, 1);
                gl.glTranslatef(xp3[i][d_y].mod, 0, 0);
                gl.glRotatef(xp3[i][d_y].axrot * 5f, 0, 0, 1);
                d_drawquad(gl, xp3[i][d_y].size);

                // UPDATE VARS

                xp3[i][d_y].mod += xp3[i][d_y].spd / 5.0f;
                xp3[i][d_y].size += xp3[i][d_y].spd * 1.2;
                xp3[i][d_y].axrot = xp3[i][d_y].axrot + .125f;
                if (xp3[i][d_y].size > .75f * xp3[i][d_y].fct) xp3[i][d_y].spd = xp3[i][d_y].fct * xp3[i][d_y].spd / 1.1f;
                if (xp3[i][d_y].spd < 0.0125f) xp3[i][d_y].spd = 0.0125f;
                if (xp3[i][d_y].r >= 96) xp3[i][d_y].r -= 1 + (int) (xp3[i][d_y].mod);
                if (xp3[i][d_y].g >= 64) xp3[i][d_y].g -= 2 + 2 * (int) (xp3[i][d_y].mod);
                if (xp3[i][d_y].b >= 16) xp3[i][d_y].b -= 4 + 4 * (int) (xp3[i][d_y].mod);
                if ((d_y % 3) == 0) xp3[i][d_y].a -= (int) (2.0f); else if ((d_y % 3) == 1) xp3[i][d_y].a -= (int) (3.0f); else xp3[i][d_y].a -= (int) (4.0f);

                if (xp3[i][d_y].a < 1) xp3[i][d_y].a = 0;

                gl.glPopMatrix();
            }
        }
    }

    private void d_xpls4(GL2 gl, int i) {
        d_Text[6].use(gl);
        if (d_timer4 < d_num4) d_timer4++;
        for (d_y = 0; d_y < d_timer4; d_y++) {
            if (xp4[i][d_y].a > 0) {
                gl.glPushMatrix();

                // DRAW PART

                gl.glColor4ub((byte)xp4[i][d_y].r, (byte)xp4[i][d_y].g, (byte)xp4[i][d_y].b, (byte)xp4[i][d_y].a);
                gl.glRotatef(xp4[i][d_y].phase, 0, 0, 1);
                gl.glTranslatef(xp4[i][d_y].mod, 0, 0);
                if ((xp4[i][d_y].phase < 270.0f) && (xp4[i][d_y].phase > 90.0f))
                    gl.glRotatef(xp4[i][d_y].axrot * 5f + 3.0f * ((float) d_y / d_num4), 0, 0, 1);
                else
                    gl.glRotatef(-xp4[i][d_y].axrot * 5f + 3.0f * ((float) d_y / d_num4), 0, 0, 1);
                d_drawquad(gl, xp4[i][d_y].size);

                // UPDATE VARS

                xp4[i][d_y].mod += xp4[i][d_y].spd / .9f;
                xp4[i][d_y].size += xp4[i][d_y].spd * 1.5;
                xp4[i][d_y].axrot = xp4[i][d_y].axrot + .125f;
                if (xp4[i][d_y].size > .75f * xp4[i][d_y].fct) xp4[i][d_y].spd = xp4[i][d_y].fct * xp4[i][d_y].spd / 1.5f;
                if (xp4[i][d_y].spd < 0.0125f) xp4[i][d_y].spd = 0.0125f;
                xp4[i][d_y].r = (int) (255 * (float) Math.sin(1.5 * xp4[i][d_y].phase) * (float) Math.sin(1.5 * xp4[i][d_y].phase));
                xp4[i][d_y].g = xp4[i][d_y].r;
                xp4[i][d_y].b = xp4[i][d_y].r;
                xp4[i][d_y].a -= (int) (2.0f);
                xp4[i][d_y].phase += .1 * ((float) d_y / d_num4);

                if (xp4[i][d_y].a < 1) xp4[i][d_y].a = 0;

                gl.glPopMatrix();
            }
        }
    }

    private void d_xpls5(GL2 gl, int i) {
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
        //glDisable(GL2.GL_BLEND);
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
        //glBindTexture(GL2.GL_TEXTURE_2D, d_texture[0]);
        d_Text[1].use(gl);
        gl.glDisable(GL2.GL_CULL_FACE);
        //glDisable(GL2.GL_TEXTURE_2D);
        if (d_timer5 < d_num5) d_timer5++;
        for (d_y = 0; d_y < d_timer5; d_y++) {
            if (xp5[i][d_y].a > 0) {
                gl.glPushMatrix();

                // DRAW PART

                gl.glColor4ub((byte)255, (byte)224, (byte)208, (byte)xp5[i][d_y].a);//xp4[i][d_y].a);
                gl.glRotatef(xp5[i][d_y].phase, 0, 0, 1);
                gl.glTranslatef(xp5[i][d_y].mod / 2.0f, 0, xp5[i][d_y].mod);
                if ((d_y % 2) == 0)
                    d_drawtri(gl, i, d_y, xp5[i][d_y].size, xp5[i][d_y].axrot, 0, 0);
                else if ((d_y % 3) == 0)
                    d_drawtri(gl, i, d_y, xp5[i][d_y].size, 0, xp5[i][d_y].axrot, 0);
                else
                    d_drawtri(gl, i, d_y, xp5[i][d_y].size, xp5[i][d_y].axrot, xp5[i][d_y].axrot, 0);

                // UPDATE VARS

                xp5[i][d_y].mod += xp5[i][d_y].spd / 3.0f;
                xp5[i][d_y].spd -= 2 * (float) (d_y / d_num5);
                xp5[i][d_y].axrot = xp5[i][d_y].axrot / 1.005f;
                xp5[i][d_y].a -= (int) (2.0f);

                if (xp5[i][d_y].a < 1) xp5[i][d_y].a = 0;

                gl.glPopMatrix();
            }
        }
        gl.glDisable(GL2.GL_TEXTURE_GEN_S);
        gl.glDisable(GL2.GL_TEXTURE_GEN_T);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_BLEND);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
    }

    private void d_rst1(int i) {
        d_timer1 = 0;
        for (d_y = 0; d_y < d_num1; d_y++) {
            xp1[i][d_y] = new d_part();
            xp1[i][d_y].size = 0.0f;
            xp1[i][d_y].phase = .360f * (float) (Math.abs(random.nextInt()) % 1000);
            xp1[i][d_y].mod = 0.0f;
            xp1[i][d_y].axrot = 0.0f;
            xp1[i][d_y].spd = .075f + .00025f * (float) (Math.abs(random.nextInt()) % 1000);
            xp1[i][d_y].fct = .5f + .0005f * (float) (Math.abs(random.nextInt()) % 1000);
            xp1[i][d_y].r = 255;
            xp1[i][d_y].g = 224;
            xp1[i][d_y].b = 208;
            xp1[i][d_y].a = Math.abs(random.nextInt()) % 255;
        }
    }

    private void d_rst2(int i) {
        d_timer2 = 0;
        for (d_y = 0; d_y < d_num2; d_y++) {
            xp2[i][d_y] = new d_part();
            xp2[i][d_y].size = 0.0f;
            xp2[i][d_y].phase = .360f * (float) (Math.abs(random.nextInt()) % 1000);
            xp2[i][d_y].mod = 0.5f;
            xp2[i][d_y].axrot = 0.0f;
            xp2[i][d_y].spd = .025f + .00025f * (float) (Math.abs(random.nextInt()) % 1000);
            xp2[i][d_y].fct = 1.025f;
            xp2[i][d_y].r = 255;
            xp2[i][d_y].g = 224;
            xp2[i][d_y].b = 208;
            xp2[i][d_y].a = 128 + Math.abs(random.nextInt()) % 127;
        }
    }

    private void d_rst3(int i) {
        d_timer3 = 0;
        for (d_y = 0; d_y < d_num3; d_y++) {
            xp3[i][d_y] = new d_part();
            xp3[i][d_y].size = 0.0f;
            xp3[i][d_y].phase = .360f * (float) (Math.abs(random.nextInt()) % 1000);
            xp3[i][d_y].mod = 0.5f;
            xp3[i][d_y].axrot = 0.0f;
            xp3[i][d_y].spd = .025f + .00025f * (float) (Math.abs(random.nextInt()) % 1000);
            xp3[i][d_y].fct = 1.0f;
            xp3[i][d_y].r = 255;
            xp3[i][d_y].g = 224;
            xp3[i][d_y].b = 208;
            xp3[i][d_y].a = 128 + Math.abs(random.nextInt()) % 127;
        }
    }

    private void d_rst4(int i) {
        d_timer4 = 0;
        for (d_y = 0; d_y < d_num4; d_y++) {
            xp4[i][d_y] = new d_part();
            xp4[i][d_y].size = 0.0f;
            xp4[i][d_y].phase = .360f * (float) (Math.abs(random.nextInt()) % 1000);
            xp4[i][d_y].mod = 0.5f;
            xp4[i][d_y].axrot = 0.0f;
            xp4[i][d_y].spd = .025f + .00025f * (float) (Math.abs(random.nextInt()) % 1000);
            xp4[i][d_y].fct = 1.0f;
            xp4[i][d_y].r = 255;
            xp4[i][d_y].g = 255;
            xp4[i][d_y].b = 255;
            xp4[i][d_y].a = 192 + Math.abs(random.nextInt()) % 63;
        }
    }

    private void d_rst5(int i) {
        d_timer5 = 0;
        for (d_y = 0; d_y < d_num5; d_y++) {
            xp5[i][d_y] = new d_part();
            xp5[i][d_y].size = .1f + .00025f * ((float) (Math.abs(random.nextInt()) % 1000));
            xp5[i][d_y].phase = .360f * (float) (Math.abs(random.nextInt()) % 1000);
            xp5[i][d_y].mod = 0.0f;
            xp5[i][d_y].axrot = .5f * ((float) (Math.abs(random.nextInt()) % 1000));
            xp5[i][d_y].spd = .05f + .0005f * (float) (Math.abs(random.nextInt()) % 1000);
            xp5[i][d_y].a = 128 + Math.abs(random.nextInt()) % 127;
        }
    }

    private void d_rstoff() {
        for (int i = 0; i < d_repeat; i++)
            d_off[i] = .5f + .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
        d_off[0] = .7f;
        d_off[1] = .8f;
        d_off[2] = .7f;
        d_off[3] = .85f;
        d_off[4] = .7f;
        d_off[5] = .9f;
        d_off[6] = .8f;
        d_off[7] = .7f;
        d_off[8] = .9f;
        d_off[9] = .8f;
        d_off[10] = 1.2f;
    }

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float time) {
        if (init) {
            init(drawable, glu);
            init = false;
        }
        d_time++;
        d_ct++;

        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -15);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -15);
        gl.glScalef(2, 1, 1);
        d_Text[7].use(gl);
        gl.glDisable(GL2.GL_BLEND);

        if (d_ct < 300)
            gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)255);
        else
            gl.glColor4ub(
                    (byte) (255 - 2.55f * ((float) (d_ct - 300))),
                    (byte) (255 - 2.55f * ((float) (d_ct - 300))),
                    (byte) (255 - 2.55f * ((float) (d_ct - 300))),
                    (byte)225);
        d_drawquad(gl, 5);
        gl.glEnable(GL2.GL_BLEND);
        gl.glPopMatrix();
//        if (true) {
//            gl.glPopMatrix();
//            return true;
//        }
        if ((d_offset <= d_repeat - 1) && (d_time > 20)) {
            d_offset++;
            d_time = 0;
        }
        for (int i = 0; i < d_offset; i++) {
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glPushMatrix();
            if (i != d_repeat - 1)
                gl.glTranslatef(4f * (float) Math.cos(6.28f * ((float) i / (d_repeat - 1))), 2f * (float) Math.sin(6.28f * ((float) i / (d_repeat - 1))), 0);
            //else MessageBox(NULL,"i vale repeat-1","i vale repeat-1",0);
            gl.glScalef(d_off[i], d_off[i], 1);
            gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)255);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            //glBindTexture(GL2.GL_TEXTURE_2D, d_texture[7]);
            d_Text[8].use(gl);
            gl.glPushMatrix();
            gl.glRotatef(4f * d_radius[i], 0, 0, 1);
            d_drawquad(gl, d_radius[i] / 4f);
            gl.glPopMatrix();
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            //glBindTexture(GL2.GL_TEXTURE_2D, d_texture[3]);
            d_Text[4].use(gl);
            if ((1.0f - (d_radius[i] / 2.5f)) > 0.0f) {
                gl.glColor4f(.95f, .9f, .75f, 1.0f - (d_radius[i] / 2.5f));
                d_drawquad(gl, 4.0f + d_radius[i] * 5.0f);
            }
            gl.glRotatef(d_radius[i] * 20f, 0, 0, 1);
            if ((1.0f - (d_radius[i] / 2.5f)) / 2.0f > 0.0f) {
                gl.glColor4f(.95f, .9f, .75f, (1.0f - (d_radius[i] / 2.5f)) / 2.0f);
                d_drawquad(gl, 4.0f + d_radius[i] * 8.0f);
            }
            gl.glPopMatrix();
            if (d_radius[i] > 0.0f) {
                /*	if (d_sound[i])
                    {
                        if (i==d_repeat-1)
                        {
                            FSOUND_PlaySound(FSOUND_FREE,xp02);
                            //FSOUND_PlaySound(FSOUND_FREE,xp02);
                            FSOUND_PlaySound(FSOUND_FREE,xp03);
                            //FSOUND_PlaySound(FSOUND_FREE,xp04);
                            FSOUND_PlaySound(FSOUND_FREE,xp05);
                            FSOUND_PlaySound(FSOUND_FREE,xp05);
                        }
                        else
                        for (int ax=0; ax<1; ax++)
                        {
                        float rundmc=((float)(Math.abs(random.nextInt())%1000))*.001;
                        if (rundmc<.25f) FSOUND_PlaySound(FSOUND_FREE, xp01);
                        else if (rundmc<.5f) FSOUND_PlaySound(FSOUND_FREE, xp02);
                        else if (rundmc<.75f) FSOUND_PlaySound(FSOUND_FREE, xp03);
                        else FSOUND_PlaySound(FSOUND_FREE, xp04);
                        }
                        //if ((i==1)||(i==5)||(i==9))
                        if (((i%3)==0)||(i==d_repeat-1))
                        FSOUND_PlaySound(FSOUND_FREE,crash);

                        d_sound[i]=false;
                    }*/
                gl.glPushMatrix();
                if (i != d_repeat - 1)
                    gl.glTranslatef(4f * (float) Math.cos(6.28f * ((float) i / (d_repeat - 1f))), 2f * (float) Math.sin(6.28f * ((float) i / (d_repeat - 1f))), 0);
                gl.glScalef(d_off[i], d_off[i], 1);
                //glDisable(GL2.GL_TEXTURE_2D);
                d_xpls1(gl, i);

                d_xpls2(gl, i);

                d_xpls3(gl, i);

                d_xpls4(gl, i);

                d_xpls5(gl, i);

                gl.glPopMatrix();
            }
            d_radius[i] += .25f;
        }
        if (d_ct > 400) {
            //****************************************** FINISH
            //	d_Clean();
            return false;
        }
        return true;
    }
}
