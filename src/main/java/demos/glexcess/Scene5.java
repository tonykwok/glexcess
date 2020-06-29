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
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.gl2.GLUT;
import demos.common.ResourceRetriever;

import java.io.IOException;
import java.util.Random;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
final class Scene5 implements Scene {
    private static final float MAX_EMBOSS = 0.01f;
    private final Random random = new Random();

    private Texture[] c_Text;
    private static final int numtexs = 15;
    private static boolean init = true;
    private static boolean c_first = true;

    private float c_time = 0;
    private GLUquadric c_quadratic;

    private final boolean c_emboss = false;
    private boolean c_fader = false;

    private final float[] c_FogColor = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
    private int c_x;
    private int c_y;

    private int c_count = 0;
    private float c_xrot;				// X Rotation ( NEW )
    private float c_yrot;				// Y Rotation ( NEW )
    private float c_zrot;				// Z Rotation ( NEW )
    private float c_zeta = 0.0f;
    private float c_factor = 1.0f;
    private float c_maxshd = 1.0f;

    private int c_maxnum = 0;

    private float c_shad = 1.0f;
    private final float[] c_data = new float[]{
        // FRONT FACE
        0.0f, 0.0f, -1.0f, -1.0f, +1.0f,
        1.0f, 0.0f, +1.0f, -1.0f, +1.0f,
        1.0f, 1.0f, +1.0f, +1.0f, +1.0f,
        0.0f, 1.0f, -1.0f, +1.0f, +1.0f,
    };

    private final int[] c_text = new int[1];
    private final int[] c_bump = new int[1];
    private final int[] c_invbump = new int[1];
    private float c_radius = 0.0f;

    private static final int c_num = 50;
    private static final int c_numpart = 10;
    private final int[] c_ci = new int[c_numpart];
    private final GLUT glut = new GLUT();

    private final float[] c_cf = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] n = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] s = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] t = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] l = new float[4];
    private final float[] Minv = new float[16];

    private static final class c_part {
        float size;
        float phase;
        float freq;
        float amp;
        float spd;
        float c_y;
        boolean twice;
        int r;
        int g;
        int b;
        int a;
    }

    private final c_part[] c_parts = new c_part[2 * c_num];
    private final c_part[][] c_fire = new c_part[c_numpart][c_num];

    private final float[] c_LightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
    private final float[] c_LightDiffuse = {0.5f, 0.5f, 0.5f, 1.0f};
    private final float[] c_LightPosition = {0.0f, 8.0f, -20.0f, 1.0f};

    private void c_initLights(GL2 gl) {
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, GLBuffers.newDirectFloatBuffer(c_LightAmbient));		// Load Light-Parameters into GL2.GL_LIGHT1
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, GLBuffers.newDirectFloatBuffer(c_LightDiffuse));
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, GLBuffers.newDirectFloatBuffer(c_LightPosition));
        gl.glEnable(GL2.GL_LIGHT1);
    }

    private void init(GLAutoDrawable drawable, GLU glu) {
        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();

        c_Text = new Texture[numtexs];
        c_maxnum = 0;
        c_zeta = 0.0f;
        c_factor = 1.0f;
        c_maxshd = 1.0f;
        c_radius = 0.0f;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 30.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        for (int i = 0; i < c_Text.length; i++) {
            c_Text[i] = new Texture();
        }
        try {
            c_Text[1].load(gl, glu, ResourceRetriever.getResourceAsStream("data/star.raw"));
            c_Text[2].load(gl, glu, ResourceRetriever.getResourceAsStream("data/esaflr.raw"));
            c_Text[3].load(gl, glu, ResourceRetriever.getResourceAsStream("data/rusty3.raw"));
            c_Text[4].load(gl, glu, ResourceRetriever.getResourceAsStream("data/noise1.raw"));
            c_Text[5].load(gl, glu, ResourceRetriever.getResourceAsStream("data/lightmask.raw"));
            c_Text[6].load(gl, glu, ResourceRetriever.getResourceAsStream("data/text.raw"));
            c_Text[7].load(gl, glu, ResourceRetriever.getResourceAsStream("data/spot.raw"));
            c_Text[8].load(gl, glu, ResourceRetriever.getResourceAsStream("data/envmap.raw"));
            c_Text[9].load(gl, glu, ResourceRetriever.getResourceAsStream("data/sh1.raw"));
            c_Text[10].load(gl, glu, ResourceRetriever.getResourceAsStream("data/bump5.raw"));
            c_Text[11].load(gl, glu, ResourceRetriever.getResourceAsStream("data/floor1.raw"));
            c_Text[12].load(gl, glu, ResourceRetriever.getResourceAsStream("data/bumphalf.raw"));
            c_Text[13].load(gl, glu, ResourceRetriever.getResourceAsStream("data/mamor.raw"));
            c_Text[14].load(gl, glu, ResourceRetriever.getResourceAsStream("data/bumpinvhalf.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT1);
        for (int i = 0; i < c_numpart; i++) {
            for (int j = 0; j < c_fire[i].length; j++) {
                c_fire[i][j] = new c_part();
            }
        }

        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);	// Really Nice Perspective Calculations
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glEnable(GL2.GL_TEXTURE_2D);


        gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
        gl.glFogf(GL2.GL_FOG_START, 9.0f);
        gl.glFogf(GL2.GL_FOG_END, 28.0f);
        gl.glFogf(GL2.GL_FOG_DENSITY, 0.075f);
        c_FogColor[0] = 0.0f;
        c_FogColor[1] = 0.0f;
        c_FogColor[2] = 0.0f;
        gl.glFogfv(GL2.GL_FOG_COLOR, GLBuffers.newDirectFloatBuffer(c_FogColor));
        gl.glEnable(GL2.GL_FOG);

        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_BLEND);

        for (c_x = 0; c_x < c_numpart; c_x++) {
            for (c_y = 0; c_y < c_num; c_y++) {
                if ((c_x == 0) || (c_x == 1)) {
                    c_parts[c_y + 50 * c_x] = new c_part();
                    c_parts[c_y + 50 * c_x].size = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                    c_parts[c_y + 50 * c_x].phase = 3.1415f + .002f * ((float) (Math.abs(random.nextInt()) % 1000));
                    c_parts[c_y + 50 * c_x].freq = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                    c_parts[c_y + 50 * c_x].spd = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                    c_parts[c_y + 50 * c_x].amp = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                    c_parts[c_y + 50 * c_x].c_y = 0.0f;
                    c_parts[c_y + 50 * c_x].r = 192 + Math.abs(random.nextInt()) % 15;
                    c_parts[c_y + 50 * c_x].g = 192 + Math.abs(random.nextInt()) % 15;
                    c_parts[c_y + 50 * c_x].b = 224 + Math.abs(random.nextInt()) % 31;
                    c_parts[c_y + 50 * c_x].a = 192 + Math.abs(random.nextInt()) % 63;
                }

                c_fire[c_x][c_y] = new c_part();
                c_fire[c_x][c_y].size = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
                c_fire[c_x][c_y].freq = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                c_fire[c_x][c_y].spd = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                c_fire[c_x][c_y].amp = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                c_fire[c_x][c_y].c_y = 0.0f;
                c_fire[c_x][c_y].r = 128 + Math.abs(random.nextInt()) % 128;
                c_fire[c_x][c_y].g = 64 + Math.abs(random.nextInt()) % 64;
                c_fire[c_x][c_y].b = 32 + Math.abs(random.nextInt()) % 32;
                c_fire[c_x][c_y].a = Math.abs(random.nextInt()) % 128;
                c_fire[c_x][c_y].twice = true;
            }
            c_ci[c_x] = 0;
        }

        c_Text[1].use(gl);
        c_quadratic = glu.gluNewQuadric();
        glu.gluQuadricNormals(c_quadratic, GLU.GLU_SMOOTH);
        glu.gluQuadricTexture(c_quadratic, true);
        c_initLights(gl);

        c_parts[0].size = 1.75f;
        c_parts[0].phase = 3.1415f / .9f;
        c_parts[0].freq = -.5f;
        c_parts[0].a = 255;
        c_parts[0].spd = .25f;

        c_parts[1].size = 1.75f;
        c_parts[1].phase = -3.1415f / .8f;
        c_parts[1].freq = -.5f;
        c_parts[1].a = 255;
        c_parts[1].spd = .25f;
    }

    private static void c_VMatMult(float[] M, float[] v) {
        float rx, ry, rz;
        rx = M[0] * v[0] + M[1] * v[1] + M[2] * v[2] + M[3] * v[3];
        ry = M[4] * v[0] + M[5] * v[1] + M[6] * v[2] + M[7] * v[3];
        rz = M[8] * v[0] + M[9] * v[1] + M[10] * v[2] + M[11] * v[3];

        v[0] = rx;
        v[1] = ry;
        v[2] = rz;
        v[3] = M[15];			// homogenous coordinate
    }

    private static void c_SetUpBumps(float[] n, float[] c_ci, float[] l, float[] s, float[] t) {
        float vx, vy, vz;							// vertex from current position to light
        float lenQ;							// used to normalize

        // calculate v from current vector c_ci to lightposition and normalize v
        vx = l[0] - c_ci[0];
        vy = l[1] - c_ci[1];
        vz = l[2] - c_ci[2];
        lenQ = (float) Math.sqrt(vx * vx + vy * vy + vz * vz);
        vx /= lenQ;
        vy /= lenQ;
        vz /= lenQ;
        // project v such that we get two values along each c_texture-coordinat axis.
        c_ci[0] = (s[0] * vx + s[1] * vy + s[2] * vz) * MAX_EMBOSS;
        c_ci[1] = (t[0] * vx + t[1] * vy + t[2] * vz) * MAX_EMBOSS;
    }

    private static void c_drawtrap(GL2 gl, float top, float bot, float h) {
        gl.glPushMatrix();
        gl.glTranslatef(0, -0.5f * h, 0);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-0.5f * bot, -0.5f * h, 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(0.5f * bot, -0.5f * h, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(0.5f * top, 0.5f * h, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-0.5f * top, 0.5f * h, 0.0f);
        gl.glEnd();
        gl.glPopMatrix();
    }

    private static void c_drawcyl1(GL2 gl, int subdiv, float fact, float ratio, float angle) {
        float a = 1.0f;
        float b = 1.0f;
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= subdiv; i++) {
            gl.glTexCoord2f(1.0f, ratio * ((float) i) / ((float) subdiv));
            gl.glVertex3f(fact, a * (float) Math.cos((angle / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float) Math.sin((angle / subdiv) * i * 2 * 3.1415 / 360.0f));
            gl.glTexCoord2f(0.0f, ratio * ((float) i) / ((float) subdiv));
            gl.glVertex3f(-fact, a * (float) Math.cos((angle / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float) Math.sin((angle / subdiv) * i * 2 * 3.1415 / 360.0f));
        }
        gl.glEnd();
    }

    private static void c_drawdisk(GL2 gl, int subdiv, float fact, float ratio) {
        float a = fact;
        float b = fact;
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glTexCoord2f(0.5f, 0.5f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        for (int i = 0; i <= subdiv; i++) {
            gl.glTexCoord2f(.5f + .5f * (float) Math.cos((360.0f / subdiv) * i * 2 * 3.1415 / 360.0f), .5f + .5f * (float) Math.sin((360.0f / subdiv) * i * 2 * 3.1415f / 360.0f));
            gl.glVertex3f(a * (float) Math.cos((360.0f / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float) Math.sin((360.0f / subdiv) * i * 2 * 3.1415 / 360.0f), 0);
        }
        gl.glEnd();
    }

    private static void c_drawquad(GL2 gl, float size) {
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

    private static void c_drawquadm(GL2 gl, float size, float tex, float tey) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-0.5f * size, -0.5f * size, 0.0f);
        gl.glTexCoord2f(1.0f * tex, 0.0f);
        gl.glVertex3f(0.5f * size, -0.5f * size, 0.0f);
        gl.glTexCoord2f(1.0f * tex, 1.0f * tey);
        gl.glVertex3f(0.5f * size, 0.5f * size, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f * tey);
        gl.glVertex3f(-0.5f * size, 0.5f * size, 0.0f);
        gl.glEnd();
    }

    private static void c_drawquadm0(GL2 gl, float size, float tex, float tey, int cl, float off) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4ub((byte) cl, (byte) cl, (byte) cl, (byte) cl);
        gl.glTexCoord2f(0.0f, 0.0f + off);
        gl.glVertex3f(-0.5f * size, -0.5f * size, 0.0f);
        gl.glTexCoord2f(1.0f * tex, 0.0f + off);
        gl.glVertex3f(0.5f * size, -0.5f * size, 0.0f);
        gl.glTexCoord2f(1.0f * tex, 1.0f * tey + off);
        gl.glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 255);
        gl.glVertex3f(0.5f * size, 0.5f * size, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f * tey + off);
        gl.glVertex3f(-0.5f * size, 0.5f * size, 0.0f);
        gl.glEnd();
    }

    private void c_drawquada(GL2 gl, float size, float tex) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4f(0.25f, 0.25f, 0.25f, 1.0f);
        gl.glTexCoord2f(0.0f, 0.0f - c_zeta);
        gl.glVertex3f(-0.5f * size, -0.5f * size, 0);
        gl.glTexCoord2f(1.0f, 0.0f - c_zeta);
        gl.glVertex3f(0.5f * size, -0.5f * size, 0);
        gl.glTexCoord2f(1.0f, 1.0f * tex - c_zeta);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glVertex3f(0.5f * size, 0.5f * size, 0);
        gl.glTexCoord2f(0.0f, 1.0f * tex - c_zeta);
        gl.glVertex3f(-0.5f * size, 0.5f * size, 0);
        gl.glEnd();
    }

    private static void c_drawquad0(GL2 gl, int subdiv, float fact, float ratio) {
        float a = 3.0f;
        float b = 1.75f;
        gl.glBegin(GL2.GL_QUAD_STRIP);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(fact, a, -b);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-fact, a, -b);
        for (int i = 0; i <= subdiv; i++) {
            gl.glTexCoord2f(1.0f, .5f + .5f * ((float) i) / ((float) subdiv));
            gl.glVertex3f(fact, a * (float) Math.cos((75.0f / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float) Math.sin((75.0f / subdiv) * i * 2 * 3.1415 / 360.0f));
            gl.glTexCoord2f(0.0f, .5f + .5f * ((float) i) / ((float) subdiv));
            gl.glVertex3f(-fact, a * (float) Math.cos((75.0f / subdiv) * i * 2 * 3.1415 / 360.0f), b * (float) Math.sin((75.0f / subdiv) * i * 2 * 3.1415 / 360.0f));
        }
        gl.glEnd();
    }

    private void c_drawcone(GL2 gl, GLU glu, int sgn, float val) {
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glColor4f(.2f + val, .2f + val, .2f + val, 1.0f);
        gl.glPushMatrix();
        gl.glScalef(.5f, 1, 1);
        gl.glTranslatef(sgn * .75f, -.1f, 0);
        gl.glRotatef(90, 0, 1, 0);
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
        c_Text[8].use(gl);
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
        gl.glRotatef(c_radius * 8, 0, 0, 1);
        glut.glutSolidTorus(/*gl,*/ .1, .05, 4, 20);
        gl.glPopMatrix();
        gl.glColor4f(.5f + val / 2, .5f + val / 2, .5f + val / 2, 1.0f);
        gl.glPushMatrix();
        if (sgn == 1)
            gl.glTranslatef(.075f, -.1f, 0);
        else
            gl.glTranslatef(-.4f, -.1f, 0);
        gl.glRotatef(90, 0, 1, 0);
        //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[7]);
        glu.gluCylinder(c_quadratic, .03, .03, .35, 10, 1);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glRotatef(sgn * 30, 0, 0, 1);
        gl.glRotatef(90, 0, 0, 1);
        gl.glRotatef(-90, 0, 1, 0);
        gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
        //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[3]);
        c_Text[4].use(gl);
        glut.glutSolidCone(/*glu, */.05f, .5f, 4, 4);
        gl.glTranslatef(0, 0, .07f);
        glu.gluCylinder(c_quadratic, .06, .05, .1, 10, 1);
        gl.glDisable(GL2.GL_TEXTURE_GEN_S);
        gl.glDisable(GL2.GL_TEXTURE_GEN_T);
        gl.glPopMatrix();
        gl.glDisable(GL2.GL_DEPTH_TEST);
    }

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        c_Text[1].kill(gl);
        c_Text[2].kill(gl);
        c_Text[3].kill(gl);
        c_Text[4].kill(gl);
        c_Text[5].kill(gl);
        c_Text[6].kill(gl);
        c_Text[7].kill(gl);
        c_Text[8].kill(gl);
        c_Text[9].kill(gl);
        c_Text[10].kill(gl);
        c_Text[11].kill(gl);
        c_Text[12].kill(gl);
        c_Text[13].kill(gl);
        c_Text[14].kill(gl);
        init = true;
    }

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float globtime) {
        if (init) {
            init(drawable, glu);
            init = false;
        }

        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();

        c_time = 5 * globtime;
        if (-c_radius < 20.0f) {
            c_count++;
        } else if (c_first) {
            c_factor = 800.0f / (float) c_count;
            c_first = false;
        }

        if (-c_radius < 108.15f)
            c_zeta = .095f * (-c_radius - 20.0f) + 1.5f;
        else if (-c_radius < 113.15f) c_zeta = 9.575f + 0.3f * (-(float) Math.cos(((-c_radius - 108.15f) / 5.0f) * 3.1415f / 2.0f + 3.1415f / 2.0f) + 1.0f);

        if (-c_radius < 10.0f) {
            c_xrot = -2.0f - 7.0f * ((float) Math.cos((-c_radius / 10.0f) * 3.1415f) + 1.0f);
            c_yrot = -10.0f;
            c_zrot = -3.0f;
            c_zeta = 1.0f;
        } else if (-c_radius < 15.0f) {
            c_zrot = -3.0f - .5f * (-(float) Math.cos(((-c_radius - 10.0f) / 5.0f) * 3.1415f) + 1.0f);
            c_yrot = -10.0f + 3.5f * (-(float) Math.cos(((-c_radius - 10.0f) / 5.0f) * 3.1415f) + 1.0f);
            c_zeta = 1.0f;
        } else if (-c_radius < 20.0f) {
            c_zrot = -4.0f + 2.0f * (-(float) Math.cos(((-c_radius - 15.0f) / 5.0f) * 3.1415f) + 1.0f);
            c_xrot = -2.0f + 1.0f * (-(float) Math.cos(((-c_radius - 15.0f) / 5.0f) * 3.1415f) + 1.0f);
            c_zeta = 1.0f + 0.5f * (-(float) Math.cos(((-c_radius - 15.0f) / 5.0f) * 3.1415f / 2.0f) + 1.0f);
        } else if (-c_radius < 30.0f) {
            c_yrot = -3.0f + 4.0f * (-(float) Math.cos(((-c_radius - 20.0f) / 5.0f) * 3.1415f) + 1.0f) - 2.5f * (-(float) Math.cos(((-c_radius - 20.0f) / 10.0f) * 3.1415f) + 1.0f);
            c_xrot = -1.0f + 1.0f * ((float) Math.cos(((-c_radius - 20.0f) / 5.0f) * 3.1415f));
        } else if (-c_radius < 40.0f) {
            c_yrot = -8.0f + 3.0f * (-(float) Math.cos(((-c_radius - 30.0f) / 5.0f) * 3.1415f) + 1.0f) + 4.0f * (-(float) Math.cos(((-c_radius - 30.0f) / 10.0f) * 3.1415f) + 1.0f);
            c_xrot = 2.0f - 2.0f * ((float) Math.cos(((-c_radius - 30.0f) / 5.0f) * 3.1415f));
        } else if (-c_radius < 90.0f) {
            c_yrot = ((float) Math.cos((-c_radius - 65.0f) * 3.1415f / 25.0f) + 1.0f) * 1.5f * ((float) Math.sin(((-c_radius - 40.0f) / 20.0f) * 3.1415f)) * ((float) Math.sin(((-c_radius - 40.0f) / 20.0f) * 3.1415f));
            c_xrot = 1.0f * (-(float) Math.cos(((-c_radius - 40.0f) / 12.5f) * 3.1415f) + 1.0f) - .5f * (-(float) Math.cos(((-c_radius - 40.0f) / 6.25f) * 3.1415f) + 1.0f);
        }

        if ((-c_radius > 35.0f) && (-c_radius < 75.0f)) {
            c_zrot = .5f * (-(float) Math.cos(((-c_radius - 35.0f) / 10.0f) * 3.1415f) + 1.0f) - 1.0f * (-(float) Math.cos(((-c_radius - 35.0f) / 5.0f) * 3.1415f) + 1.0f);
        }

        if ((-c_radius > 130.0f) && (-c_radius < 150.0f)) {
            c_xrot = 9.0f * (-(float) Math.cos((-c_radius - 130.0f) * 3.1415f / 40.0f) + 1.0f);
            c_zeta = 10.1745f + .5f * (-(float) Math.cos((-c_radius - 130.0f) * 3.1415f / 40.0f) + 1.0f);
            c_fader = true;
        }
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL2.GL_FOG);

        if (c_ci[9] >= 2) {
            float[] c_ci = c_cf;

             // Build inverse Modelview Matrix c_first. This substitutes one Push/Pop with one gl.glLoadIdentity();
            // Simply build it by doing all transformations negated and in reverse order.
            gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
            gl.glLoadIdentity();
            gl.glRotatef(-c_yrot, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(-c_xrot, 1.0f, 0.0f, 0.0f);
            gl.glTranslatef(0.0f, 0.0f, -.1f);
            gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, GLBuffers.newDirectFloatBuffer(Minv));

            gl.glLoadIdentity();
            gl.glTranslatef(0, -5 + .015f * (float) Math.sin(c_radius) + .015f * (float) Math.sin(30 * c_zeta), 0);
            gl.glRotatef(5 * c_xrot, 1, 0, 0);
            gl.glRotatef(5 * c_yrot, 0, 1, 0);
            gl.glRotatef(5 * c_zrot + .5f * (float) Math.sin(.5f * c_radius), 0, 0, 1);
            gl.glTranslatef(0.0f, 4.5f, -88 + 8.22f * c_zeta);
            gl.glRotatef(-60, 1, 0, 0);

            // Transform the Lightposition into object coordinates:
            l[0] = c_LightPosition[0];
            l[1] = c_LightPosition[1];
            l[2] = c_LightPosition[2];
            l[3] = 1.0f;					// homogenous coordinate
            c_VMatMult(Minv, l);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            //	PASS#1: Use c_texture "c_bump"				No Blend				No Lighting				No offset c_texture-coordinates
            c_Text[12].use(gl);
            gl.glDisable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_LIGHTING);
            c_drawdisk(gl, 30, 1, 0);

            gl.glPushMatrix();
            gl.glScalef(2, 1.0f, 1);
            //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[12]);
            c_Text[13].use(gl);
            gl.glTranslatef(0, -.5f, -.4f);
            c_drawquadm(gl, 2, 1, 1);
            gl.glPopMatrix();
            gl.glPushMatrix();
            gl.glScalef(2, 1, 1);
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[6]);
            c_Text[7].use(gl);
            gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
            gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
            gl.glTranslatef(0, -.5f, -.4f);
            c_drawquadm0(gl, 2, 2, 2, 255, 0);
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glDisable(GL2.GL_BLEND);
            gl.glPopMatrix();

            gl.glPushMatrix();
            //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[12]);
            c_Text[13].use(gl);
            gl.glTranslatef(0, -1.5f, -.4f);
            gl.glRotatef(90, 1, 0, 0);
            gl.glPushMatrix();
            gl.glScalef(4, .1f, 1f);
            gl.glTranslatef(0, -.5f, 0);
            gl.glRotatef(180, 1, 0, 0);
            c_drawquadm0(gl, 1, 1, .05f, 255, .5f);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            gl.glEnable(GL2.GL_BLEND);
            //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[6]);
            c_Text[7].use(gl);
            gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 250);
            c_drawquadm0(gl, 1, 1, .05f, 192, .5f);
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glDisable(GL2.GL_BLEND);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glPopMatrix();
            gl.glPopMatrix();

            gl.glPushMatrix();
            //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[12]);
            c_Text[13].use(gl);
            gl.glTranslatef(0, -1.5f, -.5f);
            gl.glRotatef(60, 1, 0, 0);
            gl.glPushMatrix();
            gl.glScalef(4, 1.25f, 1);
            gl.glTranslatef(0, -.5f, 0);
            gl.glColor4ub((byte) 128, (byte) 128, (byte) 128, (byte) 255);
            gl.glRotatef(180, 1, 0, 0);
            c_drawquadm0(gl, 1, 1, .5f, 255, 0);
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            gl.glEnable(GL2.GL_BLEND);
            //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[5]);
            c_Text[6].use(gl);
            c_drawquadm0(gl, 1, 2, 1, 128, 0);
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glDisable(GL2.GL_BLEND);
            gl.glPopMatrix();
            gl.glScalef(4, .2f, 1.5f);
            gl.glTranslatef(0, -5.5f, 0);
            for (int steps = 0; steps < 3; steps++) {
                gl.glTranslatef(0, -.5f, .5f);
                gl.glRotatef(90, 1, 0, 0);
                gl.glColor4f(1, 1, 1, 1);
                //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[12]);	// HORIZ
                c_Text[13].use(gl);
                gl.glPushMatrix();
                gl.glRotatef(180, 1, 0, 0);
                c_drawquadm0(gl, 1, 1, .25f, 255, ((float) steps) / 4.0f);
                gl.glPopMatrix();
                gl.glEnable(GL2.GL_TEXTURE_GEN_S);
                gl.glEnable(GL2.GL_TEXTURE_GEN_T);
                gl.glEnable(GL2.GL_BLEND);
                //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[10]);
                c_Text[11].use(gl);
                gl.glPushMatrix();
                gl.glRotatef(180, 1, 0, 0);
                c_drawquadm0(gl, 1, 1, .25f, 160, 0);
                gl.glPopMatrix();
                gl.glDisable(GL2.GL_TEXTURE_GEN_S);
                gl.glDisable(GL2.GL_TEXTURE_GEN_T);
                gl.glDisable(GL2.GL_BLEND);
                gl.glTranslatef(0, .5f, .5f);
                gl.glRotatef(-90, 1, 0, 0);
                gl.glColor4ub((byte) 128, (byte) 128, (byte) 128, (byte) 255);
                //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[12]);	// VERT
                c_Text[13].use(gl);
                gl.glPushMatrix();
                gl.glRotatef(180, 1, 0, 0);
                c_drawquadm0(gl, 1, 1, .125f, 228, ((float) steps) / 4.0f);
                gl.glPopMatrix();
            }
            gl.glPopMatrix();

            gl.glColor4f(1, 1, 1, 1);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            // PASS#2:	Use c_texture "c_invbump"				Blend GL2.GL_ONE to GL2.GL_ONE				No Lighting				offset c_texture coordinates


            c_Text[14].use(gl);
            gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE);
            gl.glDepthFunc(GL2.GL_LEQUAL);
            gl.glEnable(GL2.GL_BLEND);

            gl.glBegin(GL2.GL_QUADS);
            // Front Face
            n[0] = 0.0f;
            n[1] = 0.0f;
            n[2] = 1.0f;
            s[0] = 1.0f;
            s[1] = 0.0f;
            s[2] = 0.0f;
            t[0] = 0.0f;
            t[1] = 1.0f;
            t[2] = 0.0f;
            for (int i = 0; i < 4; i++) {
                c_cf[0] = c_data[5 * i + 2];
                c_cf[1] = c_data[5 * i + 3];
                c_cf[2] = c_data[5 * i + 4];
                c_SetUpBumps(n, c_cf, l, s, t);
                gl.glTexCoord2f(c_data[5 * i] + c_ci[0], c_data[5 * i + 1] + c_ci[1]);
                gl.glVertex3f(c_data[5 * i + 2], c_data[5 * i + 3], 0);
            }
            gl.glEnd();

            // PASS#3:	Use c_texture "Base"				Blend GL2.GL_DST_COLOR to GL2.GL_SRC_COLOR (multiplies by 2)				Lighting enabled				no offset c_texture-coordinates
            if (!c_emboss) {
                gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
                c_Text[10].use(gl);
                gl.glBlendFunc(GL2.GL_DST_COLOR, GL2.GL_SRC_COLOR);
                c_drawdisk(gl, 30, 1, 0);
            }
            gl.glEnable(GL2.GL_DEPTH_TEST);

            c_LightPosition[0] = 2 * (float) Math.cos(c_radius / 3);
            c_LightPosition[1] = 2 * (float) Math.sin(2 * c_radius / 3);
            c_LightPosition[2] = .1f;

            gl.glPushMatrix();
            //glBindTexture(GL2.GL_TEXTURE_2D,c_texture[2]);
            c_Text[3].use(gl);
            gl.glDisable(GL2.GL_BLEND);
            gl.glTranslatef(0, 0, -.2f);
            gl.glRotatef(90, 0, 1, 0);

            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            //glEnable(GL2.GL_DEPTH_TEST);
            gl.glTranslatef(.35f, 0, 0);
            gl.glScalef(-1, 1, 1);
            gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 96);

            gl.glPushMatrix();
            if (-c_radius < 130.0f) {
                gl.glRotatef(90, 1, 0, 0);
                c_drawcyl1(gl, 30, .15f, 5, 180);
            } else {
                gl.glRotatef(90 + (-c_radius - 130.0f) * 3.5f, 1, 0, 0);
                c_drawcyl1(gl, 30, .15f, 5 - (-c_radius - 130) / 35, 180 - (-c_radius - 130) * 7);
            }
            gl.glPopMatrix();

            gl.glRotatef(90, 1, 0, 0);
            gl.glScalef(-1, 1, 1);
            gl.glDisable(GL2.GL_BLEND);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glTranslatef(-.35f, 0, 0);
            gl.glColor4ub((byte) 160, (byte) 160, (byte) 160, (byte) 255);

            c_drawcyl1(gl, 30, .2f, 5, 180.0f);

            gl.glEnable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_DEPTH_TEST);

            gl.glPopMatrix();

            if (c_zeta > 9.5) {
                gl.glPushMatrix();
                gl.glTranslatef(c_LightPosition[0] / 2, c_LightPosition[1] / 2, 0);
                //glBindTexture(GL2.GL_TEXTURE_2D,c_texture[4]);
                c_Text[5].use(gl);
                gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
                gl.glColor4f((c_zeta - 9.5f) * 2, (c_zeta - 9.5f) * 2, (c_zeta - 9.5f) * 2, (c_zeta - 9.5f) * 2);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                gl.glRotatef(60 - 5 * c_xrot, 1, 0, 0);
                c_drawquad(gl, 7.6f);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                //glBindTexture(GL2.GL_TEXTURE_2D,c_texture[0]);
                c_Text[1].use(gl);
                gl.glColor4f(1.0f, 1.0f, 1.0f, (c_zeta - 9.5f) * 2);
                c_drawquad(gl, .5f);
                //////////////////////////////////////////////////////////////////
                gl.glTranslatef(0, -.5f, 0);
                if ((c_LightPosition[0] < 1.5) && (c_LightPosition[0] > -1.5)) {
                    if (c_LightPosition[1] < -1.7) {
                        if (c_shad < ((float) 220) / 255) c_shad += ((float) 30 * c_factor) / 255;
                    } else {
                        if (c_shad > ((float) 50) / 255) c_shad -= ((float) 50 * c_factor) / 255; else c_shad = 0.0f;
                    }
                } else if (c_LightPosition[1] < .1) {
                    if (c_shad < ((float) 220) / 255) c_shad += ((float) 30 * c_factor) / 255;
                } else {
                    if (c_shad > ((float) 30) / 255) c_shad -= ((float) 30 * c_factor) / 255; else c_shad = 0.0f;
                }
                gl.glColor4f(1.0f, 1.0f, 1.0f, (c_zeta - 9.5f) * c_shad);
                c_drawquad(gl, .35f);
                gl.glPopMatrix();

                gl.glPushMatrix();
                gl.glEnable(GL2.GL_LIGHT1);
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, GLBuffers.newDirectFloatBuffer(c_LightPosition));
                gl.glPopMatrix();
            }
            gl.glDisable(GL2.GL_LIGHTING);
        }

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glLoadIdentity();
        gl.glTranslatef(0, -5 + .015f * (float) Math.sin(30 * c_zeta) + .015f * (float) Math.sin(c_radius), 0);
        gl.glRotatef(5 * c_xrot, 1, 0, 0);
        gl.glRotatef(5 * c_yrot, 0, 1, 0);
        gl.glRotatef(5 * c_zrot + .5f * (float) Math.sin(.5f * c_radius), 0, 0, 1);
/////////////////////////////////////////////////////////////////////////////////
        gl.glPushMatrix();
        //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[10]);
        c_Text[11].use(gl);
        gl.glTranslatef(0, 1.5f, -35);
        gl.glScalef(6, 3, 40);
        gl.glRotatef(90, 1, 0, 0);
        gl.glTranslatef(0, .5f, 0);
        gl.glColor4f(1, 1, 1, 1);
        c_drawquada(gl, 1, 5);
        gl.glPopMatrix();

        gl.glPushMatrix();
        //glTranslatef(0,0,-35);//c_zeta-7.5);
        gl.glTranslatef(0, 1.5f, -81.07f + c_zeta * 8.22f);
        gl.glScalef(5.5f, 1, 3);
        gl.glRotatef(90, 1, 0, 0);
        //glTranslatef(0,.5,0);
        c_Text[9].use(gl);
        gl.glEnable(GL2.GL_BLEND);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
        //glBlendFunc(GL2.GL_ONE,GL2.GL_ONE);
        gl.glColor4f(.7f, .7f, .7f, 1);
        //glScalef(1.0/1.7,1.0/20,1.0/20);
        gl.glRotatef(180, 1, 0, 0);
        c_drawquad(gl, 1);
        gl.glPopMatrix();
        gl.glEnable(GL2.GL_DEPTH_TEST);
        //glDisable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);


        for (int i = 0; i < c_numpart; i++)			// GROUND
        {
            int sign1 = 1;
            for (int k = 0; k < i; k++) sign1 = -sign1;
            gl.glLoadIdentity();
            gl.glTranslatef(0, -5 + .015f * (float) Math.sin(30 * c_zeta) + .015f * (float) Math.sin(c_radius), 0);
            gl.glRotatef(5 * c_xrot, 1, 0, 0);
            gl.glRotatef(5 * c_yrot, 0, 1, 0);
            gl.glRotatef(5 * c_zrot + .5f * (float) Math.sin(.5f * c_radius), 0, 0, 1);

            if ((i % 2) == 0) {
                if ((c_zeta * 8.22 / (30.0 * c_ci[i] + 30.0 - 3.0f * i)) > .98f) c_ci[i]++;
            } else {
                if ((c_zeta * 8.22 / (30.0 * c_ci[i] + 30.0 - 3.0f * i)) > 1.07f) c_ci[i]++;
            }
            if (c_ci[i] < 2) gl.glTranslatef(sign1 * 2.0f, 5, -30 + 3 * i - c_ci[i] * 30 + c_zeta * 8.22f);


            gl.glEnable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            if (c_ci[i] < 2) {
                gl.glPushMatrix();				// TOP
                gl.glScalef(-sign1, 1, 1);
                gl.glTranslatef(2.6f, .25f, 0);
                gl.glRotatef(90, 0, 1, 0);
                gl.glRotatef(-90, 1, 0, 0);
                int shade = 0;
                for (int p = 0; p < c_num; p++) if (c_fire[i][p].c_y < .25) shade++;
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) ((int) ((float) 255) * ((float) shade) / ((float) c_num)));
                //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[6]);
                c_Text[7].use(gl);
                c_drawquad0(gl, 10, 2.75f, 2.6f);
                gl.glPopMatrix();

                gl.glPushMatrix();				// SIDE
                gl.glPushMatrix();
                gl.glTranslatef(sign1 * .4f, -.1f, 0);
                gl.glRotatef(90, 0, 1, 0);
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 96);
                gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[8]);
                c_Text[9].use(gl);
                gl.glRotatef(10.0f * ((float) shade) / ((float) c_num) * (float) Math.sin(c_radius / 3), 0, 0, 1);
                c_drawtrap(gl, .35f, .15f, -.5f + 1.5f * ((float) shade) / ((float) c_num));
                c_drawtrap(gl, .05f, 0.2f, 2.0f * ((float) shade) / ((float) c_num));
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 96);
                gl.glRotatef(7.5f * ((float) shade) / ((float) c_num) * (float) Math.sin(c_radius / 5), 0, 0, 1);
                c_drawtrap(gl, .35f, .15f, -.5f + 1.5f * ((float) shade) / ((float) c_num));
                c_drawtrap(gl, .05f, .2f, 2.0f * ((float) shade) / ((float) c_num));
                gl.glEnable(GL2.GL_DEPTH_TEST);
                gl.glPopMatrix();
                gl.glEnable(GL2.GL_TEXTURE_2D);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);

                gl.glDisable(GL2.GL_BLEND);
                c_drawcone(gl, glu, sign1, ((float) shade) / ((float) c_num));			// CONE


                gl.glRotatef(-5 * c_zrot, 0, 0, 1);
                gl.glRotatef(-5 * c_yrot, 0, 1, 0);
                gl.glRotatef(-5 * c_xrot, 1, 0, 0);
                gl.glColor4f(1, 1, 1, .5f);
                gl.glEnable(GL2.GL_BLEND);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                gl.glRotatef(10 * i + c_radius * 5, 0, 0, 1);
                //glBindTexture(GL2.GL_TEXTURE_2D, c_texture[1]);
                c_Text[2].use(gl);
                c_drawquad(gl, -.25f + 2 * ((float) shade) / ((float) c_num));
                ///////////////////////////////
                ///////////////////////////////
                ///////////////////////////////
                // BRIGHT
                gl.glPopMatrix();
//	glPushMatrix();	///////////////////////////////
                ///////////////////////////////
                ///////////////////////////////
                //glBindTexture(GL2.GL_TEXTURE_2D, c_Text[ure[0]);
                c_Text[1].use(gl);
                for (int p = 0; p < c_num; p++) {
                    gl.glPushMatrix();
                    gl.glTranslatef(.1f * (c_fire[i][p].amp * (float) Math.sin(c_fire[i][p].freq * c_radius + c_fire[i][p].phase)), c_fire[i][p].c_y, 0);
                    gl.glColor4ub((byte) c_fire[i][p].r, (byte) c_fire[i][p].g, (byte) c_fire[i][p].b, (byte) c_fire[i][p].a);
                    gl.glRotatef(-5 * c_zrot, 0, 0, 1);
                    gl.glRotatef(-5 * c_yrot, 0, 1, 0);
                    gl.glRotatef(-5 * c_xrot, 1, 0, 0);

                    c_drawquad(gl, c_fire[i][p].size * 1.2f);
                    c_fire[i][p].c_y += c_fire[i][p].spd / 80;

                    if (c_fire[i][p].size < .2)//&&((p%4)!=0))
                    {
                        if (c_fire[i][p].twice)
                            c_fire[i][p].a -= 2;
                        else
                            c_fire[i][p].a -= 5;
                    } else {
                        c_fire[i][p].a -= 1;
                    }
                    if (c_fire[i][p].a < 0) {
                        if (c_fire[i][p].size < .2) {
                            if (c_fire[i][p].twice == false) {
                                c_fire[i][p].size = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
                                c_fire[i][p].phase = 3.1415f + .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                                c_fire[i][p].freq = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                                c_fire[i][p].spd = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
                                c_fire[i][p].amp = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                                c_fire[i][p].c_y = 0.0f;
                                c_fire[i][p].r = 128 + Math.abs(random.nextInt()) % 128;
                                c_fire[i][p].g = 64 + Math.abs(random.nextInt()) % 64;
                                c_fire[i][p].b = 32 + Math.abs(random.nextInt()) % 32;
                                c_fire[i][p].a = Math.abs(random.nextInt()) % 255;
                                c_fire[i][p].twice = true;
                            } else {
                                c_fire[i][p].a = 128 + Math.abs(random.nextInt()) % 128;
                                c_fire[i][p].spd = c_fire[i][p].spd / 2;
                                c_fire[i][p].twice = false;
                            }
                        } else {
                            c_fire[i][p].size = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
                            c_fire[i][p].phase = 3.1415f + .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                            c_fire[i][p].freq = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                            c_fire[i][p].spd = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
                            c_fire[i][p].amp = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                            c_fire[i][p].c_y = 0.0f;
                            c_fire[i][p].r = 128 + Math.abs(random.nextInt()) % 128;
                            c_fire[i][p].g = 64 + Math.abs(random.nextInt()) % 64;
                            c_fire[i][p].b = 32 + Math.abs(random.nextInt()) % 32;
                            c_fire[i][p].a = Math.abs(random.nextInt()) % 255;
                            c_fire[i][p].twice = true;
                        }
                    }

                    gl.glPopMatrix();
                }


            } else if (i < 2) {
                int shade = 0;
                int p;
                gl.glScalef(2, 2, 2);
                gl.glTranslatef(sign1 * .75f, 3, -44 + c_zeta * 4.11f);
                gl.glEnable(GL2.GL_DEPTH_TEST);
                gl.glPushMatrix();				// TOP
                gl.glScalef(sign1, 1, 1);
                gl.glTranslatef(0, -.25f, -.4f);
                for (p = 0; p < c_num; p++) if (c_fire[i][p].c_y < .25) shade++;
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) ((int) ((float) 255) * ((float) shade) / ((float) c_num)));
                //glBindTexture(GL2.GL_TEXTURE_2D, c_Text[ure[6]);
                c_Text[7].use(gl);
                c_drawquad(gl, 1);
                gl.glPopMatrix();

                gl.glEnable(GL2.GL_BLEND);
                gl.glDisable(GL2.GL_DEPTH_TEST);

                gl.glPushMatrix();				// SIDE

                gl.glRotatef(sign1 * 90, 0, 1, 0);

                // SHADOW
                gl.glPushMatrix();

                gl.glTranslatef(sign1 * .4f, -.1f, 0);
                gl.glRotatef(90, 0, 1, 0);
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 96);
                gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                //glBindTexture(GL2.GL_TEXTURE_2D, c_Text[ure[8]);
                c_Text[9].use(gl);
                gl.glRotatef(10.0f * ((float) shade) / ((float) c_num) * (float) Math.sin(2 * i + c_radius / 3), 0, 0, 1);
                c_drawtrap(gl, .35f, .15f, -.5f + 1.5f * ((float) shade) / ((float) c_num));
                c_drawtrap(gl, .05f, .2f, 1.0f * ((float) shade) / ((float) c_num));
                gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 96);
                gl.glRotatef(7.5f * ((float) shade) / ((float) c_num) * (float) Math.sin(2 * i + c_radius / 5), 0, 0, 1);
                c_drawtrap(gl, .35f, .15f, -.5f + 1.5f * ((float) shade) / ((float) c_num));
                c_drawtrap(gl, .05f, .2f, 1.0f * ((float) shade) / ((float) c_num));
                gl.glEnable(GL2.GL_DEPTH_TEST);
                gl.glPopMatrix();

                gl.glEnable(GL2.GL_TEXTURE_2D);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);

                gl.glDisable(GL2.GL_BLEND);
                c_drawcone(gl, glu, sign1, ((float) shade) / ((float) c_num));			// CONE

                gl.glRotatef(-5 * c_zrot, 0, 0, 1);
                gl.glRotatef(-5 * c_yrot, 0, 1, 0);
                gl.glRotatef(-5 * c_xrot, 1, 0, 0);
                gl.glColor4f(1, 1, 1, .5f);
                gl.glEnable(GL2.GL_BLEND);
                gl.glDisable(GL2.GL_DEPTH_TEST);

                gl.glPushMatrix();
                gl.glRotatef(10 * i + sign1 * c_radius * 5, 1, 0, 0);
                //glBindTexture(GL2.GL_TEXTURE_2D, c_Text[ure[1]);
                c_Text[2].use(gl);
                gl.glRotatef(90, 0, 1, 0);
                c_drawquad(gl, -.25f + 2 * ((float) shade) / ((float) c_num));
                gl.glPopMatrix();
                gl.glPopMatrix();

                //glBindTexture(GL2.GL_TEXTURE_2D, c_Text[ure[0]);
                c_Text[1].use(gl);
                for (p = 0; p < c_num; p++) {
                    gl.glPushMatrix();
                    gl.glTranslatef(.1f * (c_fire[i][p].amp * (float) Math.sin(c_fire[i][p].freq * c_radius + c_fire[i][p].phase)), c_fire[i][p].c_y, 0);
                    gl.glColor4ub((byte) c_fire[i][p].r, (byte) c_fire[i][p].g, (byte) c_fire[i][p].b, (byte) c_fire[i][p].a);
                    gl.glRotatef(-5 * c_zrot, 0, 0, 1);
                    gl.glRotatef(-5 * c_yrot, 0, 1, 0);
                    gl.glRotatef(-5 * c_xrot, 1, 0, 0);

                    c_drawquad(gl, c_fire[i][p].size * 1.2f);
                    c_fire[i][p].c_y += c_fire[i][p].spd / 80;

                    if (c_fire[i][p].size < .2) {
                        if (c_fire[i][p].twice)
                            c_fire[i][p].a -= 2;
                        else
                            c_fire[i][p].a -= 5;
                    } else {
                        c_fire[i][p].a -= 1;
                    }
                    if (c_fire[i][p].a < 0) {
                        if (c_fire[i][p].size < .2) {
                            if (c_fire[i][p].twice == false) {
                                c_fire[i][p].size = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
                                c_fire[i][p].phase = 3.1415f + .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                                c_fire[i][p].freq = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                                c_fire[i][p].spd = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
                                c_fire[i][p].amp = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                                c_fire[i][p].c_y = 0.0f;
                                c_fire[i][p].r = 128 + Math.abs(random.nextInt()) % 128;
                                c_fire[i][p].g = 64 + Math.abs(random.nextInt()) % 64;
                                c_fire[i][p].b = 32 + Math.abs(random.nextInt()) % 32;
                                c_fire[i][p].a = Math.abs(random.nextInt()) % 255;
                                c_fire[i][p].twice = true;
                            } else {
                                c_fire[i][p].a = 128 + Math.abs(random.nextInt()) % 128;
                                c_fire[i][p].spd = c_fire[i][p].spd / 2;
                                c_fire[i][p].twice = false;
                            }
                        } else {
                            c_fire[i][p].size = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
                            c_fire[i][p].phase = 3.1415f + .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                            c_fire[i][p].freq = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                            c_fire[i][p].spd = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
                            c_fire[i][p].amp = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                            c_fire[i][p].c_y = 0.0f;
                            c_fire[i][p].r = 128 + Math.abs(random.nextInt()) % 128;
                            c_fire[i][p].g = 64 + Math.abs(random.nextInt()) % 64;
                            c_fire[i][p].b = 32 + Math.abs(random.nextInt()) % 32;
                            c_fire[i][p].a = Math.abs(random.nextInt()) % 255;
                            c_fire[i][p].twice = true;
                        }
                    }

                    gl.glPopMatrix();
                }
            }
        }

/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
        if (c_zeta < 8) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
            //glBindTexture(GL2.GL_TEXTURE_2D, c_Text[ure[0]);
            c_Text[1].use(gl);
            if (c_maxnum <= 97) c_maxnum = 3 * (int) (-c_radius - 36.0f);
            if ((-c_radius > 30.0f) && (-c_radius < 38.0f)) {
                if (-c_radius < 33.0f) c_maxnum = 1; else c_maxnum = 2;
            }
            for (int pp = 0; pp < c_maxnum; pp++) {
                gl.glLoadIdentity();
                gl.glRotatef(5 * c_yrot, 0, 1, 0);
                gl.glRotatef(5 * c_xrot, 1, 0, 0);
                gl.glTranslatef((1 - c_zeta / 5.0f + c_parts[pp].amp) * (float) Math.cos(c_radius * 2 * c_parts[pp].freq + c_parts[pp].phase),
                        (1 - c_zeta / 5.0f + c_parts[pp].amp) * (float) Math.sin(c_radius * 2 * c_parts[pp].freq + c_parts[pp].phase),
                        c_parts[pp].c_y + c_zeta);
                //glColor4ub(c_parts[pp].r,c_parts[pp].g,c_parts[pp].b,c_parts[pp].a);

                if (c_parts[pp].a >= 0) c_parts[pp].a = (int) (255.0f * ((((float) c_parts[pp].a) / 255.0f) - .01f * c_parts[pp].spd * c_factor));
                gl.glColor4f(c_parts[pp].r / 255.0f, c_parts[pp].g / 255.0f, c_parts[pp].b / 255.0f, c_parts[pp].a / 255.0f);
                gl.glRotatef(-5 * c_xrot, 1, 0, 0);
                gl.glRotatef(-5 * c_yrot, 0, 1, 0);
                gl.glRotatef(c_radius * 75.0f * c_parts[pp].spd, 0, 0, 1);
                if ((pp % 3) == 0)
                    c_drawquad(gl, .5f * c_parts[pp].size);
                else if ((pp % 3) == 1)
                    c_drawquad(gl, .5f * c_parts[pp].size + .25f * c_parts[pp].size * (float) Math.sin(c_parts[pp].spd * c_radius * 7.5f));
                else
                    c_drawquad(gl, .5f * c_parts[pp].size + .25f * c_parts[pp].size * (float) Math.sin(c_parts[pp].spd * c_radius * 15.0f));
                if ((-c_radius > 30.0f) && (-c_radius < 45.0f)) {
                    gl.glPushMatrix();
                    gl.glRotatef(-c_radius * 75.0f * c_parts[pp].spd, 0, 0, 1);
                    if (pp == 1)
                        c_drawquad(gl, .25f * c_parts[pp].size);
                    else if (pp == 0) c_drawquad(gl, .25f * c_parts[pp].size + .25f * c_parts[pp].size * (float) Math.sin(c_parts[pp].spd * c_radius * 7.5f));
                    gl.glPopMatrix();
                }
                c_parts[pp].c_y -= c_factor * c_parts[pp].spd / 2.0f;
                if (c_parts[pp].a < 0)//&&(c_zeta<6.0f))
                {
                    c_parts[pp].size = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                    //c_parts[pp].spd=.001*((float)(Math.abs(random.nextInt())%1000));
                    c_parts[pp].phase = 3.1415f + .002f * ((float) (Math.abs(random.nextInt()) % 1000));
                    c_parts[pp].freq = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                    c_parts[pp].amp = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
                    c_parts[pp].r = 192 + Math.abs(random.nextInt()) % 15;
                    c_parts[pp].g = 192 + Math.abs(random.nextInt()) % 15;
                    c_parts[pp].b = 224 + Math.abs(random.nextInt()) % 31;
                    c_parts[pp].a = 192 + Math.abs(random.nextInt()) % 63;
                    c_parts[pp].c_y = 0.0f;
                }
            }
        } else if (c_zeta < 10) {
            for (int pp = 0; pp < c_maxnum; pp++) {
                if (c_zeta > 9.0f) c_maxshd = 1.0f - (c_zeta - 9.0f);
                gl.glLoadIdentity();
                gl.glRotatef(5 * c_zrot, 0, 0, 1);
                gl.glRotatef(5 * c_yrot, 0, 1, 0);
                gl.glRotatef(5 * c_xrot, 1, 0, 0);
                gl.glTranslatef((1 - c_zeta / 5.0f + c_parts[pp].amp) * (float) Math.cos(c_radius * 2 * c_parts[pp].freq + c_parts[pp].phase),
                        (1 - c_zeta / 5.0f + c_parts[pp].amp) * (float) Math.sin(c_radius * 2 * c_parts[pp].freq + c_parts[pp].phase),
                        c_parts[pp].c_y + c_zeta);
                gl.glColor4f(c_parts[pp].r / 255.0f, c_parts[pp].g / 255.0f, c_parts[pp].b / 255.0f, c_maxshd * c_parts[pp].a / 255.0f);
                if (c_parts[pp].a > 0) c_drawquad(gl, .5f * c_parts[pp].size);
                c_parts[pp].c_y -= c_factor * c_parts[pp].spd / 4;
            }
        }

        if ((c_zeta > 5.0f) && (c_zeta < 10.0f)) {
            //glBindTexture(GL2.GL_TEXTURE_2D, c_Text[ure[0]);
            c_Text[1].use(gl);
            gl.glLoadIdentity();
            gl.glRotatef(5 * c_zrot, 0, 0, 1);
            gl.glRotatef(5 * c_yrot, 0, 1, 0);
            gl.glRotatef(5 * c_xrot, 1, 0, 0);
            gl.glTranslatef(0, 0, -5);
            if (c_zeta < 9)
                gl.glColor4f(1.0f, 1.0f, 1.0f, (c_zeta - 5.0f) / 3.0f);
            else
                gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - (c_zeta - 9.0f) * 2);
            for (int c_ci = 0; c_ci < 4; c_ci++) {
                gl.glRotatef((c_ci + 1) * c_radius, 0, 0, 1);
                c_drawquad(gl, ((c_ci + 1) * ((c_zeta - 5.0f) / 10.0f + (c_zeta - 5.0f) / 10.0f * (float) Math.sin(c_radius) * (float) Math.sin(c_radius))) / 2.0f);
            }
        }

        if (c_fader) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -.1f);
            gl.glColor4f(1.0f, 1.0f, 1.0f, (-c_radius - 140.0f) / 3.0f);
            c_drawquad(gl, 1);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_DEPTH_TEST);
        }


        if (((-c_radius > 29.4) && (-c_radius < 31.4)) || ((-c_radius > 108.5) && (-c_radius < 110.5))) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -.1f);
            if (-c_radius < 31.4)
                gl.glColor4f(1.0f, 1.0f, 1.0f, .5f - .5f * (float) Math.cos((-c_radius - 29.4f) * 3.1415f));
            else
                gl.glColor4f(1.0f, 1.0f, 1.0f, .5f - .5f * (float) Math.cos((-c_radius - 108.5) * 3.1415f));
            c_drawquad(gl, 1);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_DEPTH_TEST);
        }


/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////

        c_radius = -.0015f * (c_time);

        if (-c_radius > 143.0f) {
            //************************* FINISH
            //c_Clean();
            return false;
        }
        return true;
    }
}
