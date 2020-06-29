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

import java.util.Random;
import java.io.IOException;

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
final class Scene8 implements Scene {
    private final Random random = new Random();
    private Texture[] g_Text;
    private static final int numtexs = 18;
    private static boolean init = true;
    private float g_time = 0;

    private long limit = 0;
    private int g_check = 2;

    private final float[][] g_points = new float[64][64];
    private boolean playjet = true;
    private final int g_num = 50;
    private final int g_num1 = 250;
    private long g_gettime = 0;
    private float g_ext = 10.0f;
    private static final class g_part {
        float size,
                spd,
                z,
                fact,
                r,
                g_a;
    }

    private static final class g_part1 {
        float size,
                spd,
                h,
                r,
                g_a;
        long init;
    }

    private int g_scene = 0;
    private final g_part[] parts = new g_part[g_num];
    private final g_part1[] parts1 = new g_part1[g_num1];

    private float g_litetop = 1.0f;
    private float g_liteleft = 0.5f;
    private float g_literite = 0.25f;

    private float g_rot = 1.5f;
    private float g_rota = 0.0f;
    private float g_zeta = 1;
    private final int[][] g_phase = new int[64][64];
    private final int[][] g_speed = new int[64][64];
    private int g_gx;
    private int g_gy;
    private int g_a = 0;
    private int g_b = 0;
    private int g_c = 1;
    private float g_radius = -6.0f;

    private final float[] g_FogColor = {1.0f, 1.0f, 1.0f, 1.0f};

    private final GLUT glut = new GLUT();

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        g_Text[0].kill(gl);
        g_Text[1].kill(gl);
        g_Text[2].kill(gl);
        g_Text[3].kill(gl);
        g_Text[4].kill(gl);
        g_Text[5].kill(gl);
        g_Text[6].kill(gl);
        g_Text[7].kill(gl);
        g_Text[8].kill(gl);
        g_Text[9].kill(gl);
        g_Text[10].kill(gl);
        g_Text[11].kill(gl);
        g_Text[12].kill(gl);
        g_Text[13].kill(gl);
        g_Text[14].kill(gl);
        g_Text[15].kill(gl);
        g_Text[16].kill(gl);
        init = true;
    }

    private static void g_drawquad(GL2 gl, float size) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-.5f * size, -.5f * size, 0);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(.5f * size, -.5f * size, 0);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(.5f * size, .5f * size, 0);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-.5f * size, .5f * size, 0);
        gl.glEnd();
    }

    private static void g_drawquadr(GL2 gl, float size) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-.5f * size, -.5f * size, 0);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(.5f * size, -.5f * size, 0);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(.5f * size, .5f * size, 0);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-.5f * size, .5f * size, 0);
        gl.glEnd();
    }

    private void g_rst(int x) {
        parts[x].size = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
        parts[x].spd = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
        parts[x].spd = .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
        parts[x].fact = .00025f * ((float) (Math.abs(random.nextInt()) % 1000));
        parts[x].z = 0.0f;
        parts[x].r = .5f + .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
        parts[x].g_a = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
    }

    private void g_rst1(int x) {
        parts1[x].size = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
        parts1[x].spd = .25f + .00025f * ((float) (Math.abs(random.nextInt()) % 1000));
        parts1[x].init = g_gettime;//FSOUND_Stream_GetTime(stream);
        parts1[x].h = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
        parts1[x].r = .5f + .0005f * ((float) (Math.abs(random.nextInt()) % 1000));
        parts1[x].g_a = .001f * ((float) (Math.abs(random.nextInt()) % 1000));
    }


    private void init(GLAutoDrawable drawable, GLU glu) {
        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = drawable.getGLU();

        g_Text = new Texture[numtexs];
        limit = 0;
        g_check = 2;

        g_scene = 0;
        g_gettime = 0;
        g_litetop = 1.0f;
        g_liteleft = 0.5f;
        g_literite = 0.25f;

        g_gx = 0;
        g_gy = 0;
        g_a = 0;
        g_b = 0;
        g_c = 1;
        g_radius = -6.0f;
        g_ext = 10.0f;
        playjet = true;
        //jet=FSOUND_Sample_LoadMpeg(FSOUND_FREE,"data/jet.mp3",FSOUND_NORMAL);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 40.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glDisable(GL2.GL_FOG);

        for (int i = 0; i < g_Text.length; i++) {
            g_Text[i] = new Texture();
        }
        try {
            g_Text[0].load(gl, glu, ResourceRetriever.getResourceAsStream("data/esaflr.raw"));
            g_Text[1].load(gl, glu, ResourceRetriever.getResourceAsStream("data/senv36.raw"));  //MOON
            g_Text[2].load(gl, glu, ResourceRetriever.getResourceAsStream("data/sky7.raw"));	  //MOON
            g_Text[11].load(gl, glu, ResourceRetriever.getResourceAsStream("data/env26.raw")); //DUSK
            g_Text[12].load(gl, glu, ResourceRetriever.getResourceAsStream("data/sky27.raw")); //DUSK
            g_Text[13].load(gl, glu, ResourceRetriever.getResourceAsStream("data/env17.raw")); //DAY
            g_Text[14].load(gl, glu, ResourceRetriever.getResourceAsStream("data/sky17.raw")); //DAY
            g_Text[15].load(gl, glu, ResourceRetriever.getResourceAsStream("data/basic2.raw"));
            g_Text[16].load(gl, glu, ResourceRetriever.getResourceAsStream("data/white.raw"));
            g_Text[17].load(gl, glu, ResourceRetriever.getResourceAsStream("data/moon1.raw"));
            g_Text[3].load(gl, glu, ResourceRetriever.getResourceAsStream("data/f16.raw"));
            g_Text[4].load(gl, glu, ResourceRetriever.getResourceAsStream("data/f16mask.raw"));
            g_Text[5].load(gl, glu, ResourceRetriever.getResourceAsStream("data/basic2.raw"));
            g_Text[6].load(gl, glu, ResourceRetriever.getResourceAsStream("data/sun2.raw"));
            g_Text[7].load(gl, glu, ResourceRetriever.getResourceAsStream("data/trail.raw"));
            g_Text[8].load(gl, glu, ResourceRetriever.getResourceAsStream("data/trailleft.raw"));
            g_Text[9].load(gl, glu, ResourceRetriever.getResourceAsStream("data/trailright.raw"));
            g_Text[10].load(gl, glu, ResourceRetriever.getResourceAsStream("data/floodmask.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

        gl.glShadeModel(GL2.GL_FLAT);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.5f);
        gl.glClearColor(.4862f, .4352f, .2627f, 0.5f);
        gl.glClearColor(0, 0, 0, 0.5f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glFrontFace(GL2.GL_CCW);

        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 63; y++) {
                g_phase[x][y] = (int)(.001f * (Math.abs(random.nextInt()) % 5000));
                g_speed[x][y] = (int)(.1f + .001f * (Math.abs(random.nextInt()) % 10000));
            }
        }
        for (int x = 0; x < 64; x++) {
            g_phase[x][63] = g_phase[x][0];
            g_speed[x][63] = g_speed[x][0];
        }

        for (int x = 0; x < g_num; x++) {
            parts[x] = new g_part();
            g_rst(x);
        }

        for (int x = 0; x < g_num1; x++) {
            parts1[x] = new g_part1();
            g_rst1(x);
        }

        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);

        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);

        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_BLEND);
        g_zeta = 66.0f + 45.0f * ((((float) g_gettime)) / 13000.0f) * ((((float) g_gettime)) / 13000.0f);
        g_rota = -.3f + ((float) (g_gettime - limit)) / 2500.0f;
        g_rot = 1.5f + (g_zeta - 1.0f) / 15.0f;
    }

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float globtime) {
        if (init) {
            init(drawable, glu);
            init = false;
        }

        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();

        g_time = 5 * globtime;

        if (((g_scene == 1) && (g_check == 2)) || ((g_scene == 2) && (g_check == 1))) {
            g_check--;
            limit = g_gettime;
            g_a = 0;
            g_b = 0;
            g_c = 1;
            //g_rota=-0.5f;
        }
//	if (g_rota<0.0f) gl.glClearColor(-g_rota,-g_rota,-g_rota,1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glColor4f(1, 1, 1, 1);
        gl.glLoadIdentity();
        //glRotatef(g_yrot,1.0f,0.0f,0.0f);
        //glRotatef(25,0.0f,0.0f,1.0f);
        gl.glTranslatef(0, -3, -5 + g_zeta);

        //gluLookAt(16,0,.05,16,16,0,0,1,0);
        glu.gluLookAt(16, 0, .05, 16, 16, 0, 0, 1, 0);
        //glRotatef(45,1,0,0);
        glut.glutSolidSphere(/*glu, */0, 10, 10);

        //glBindTexture(GL2.GL_TEXTURE_2D, g_Text[ure[0]);
        if (g_scene == 0)
            g_Text[1].use(gl);
        else if (g_scene == 1)
            g_Text[11].use(gl);
        else
            g_Text[13].use(gl);
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
        if (((int) g_zeta) / 64 == g_c) {
            if ((g_c % 2) == 0) g_b++; else g_a++;
            g_c++;
        }
        gl.glPushMatrix();
        gl.glTranslatef(-5, (126 * g_a), 0);
        gl.glBegin(GL2.GL_QUADS);
        for (g_gx = 0; g_gx < 63; g_gx++) {
            for (g_gy = 0; g_gy < 63; g_gy++) {
                gl.glVertex3f( (float)(g_gx) / 1.5f, (float)(g_gy), g_points[g_gx][g_gy]);
                gl.glVertex3f( (float)(g_gx + 1) / 1.5f, (float)(g_gy), g_points[g_gx + 1][g_gy]);
                gl.glVertex3f( (float)(g_gx + 1) / 1.5f, (float)(g_gy + 1), g_points[g_gx + 1][g_gy + 1]);
                gl.glVertex3f( (float)(g_gx) / 1.5f, (float)(g_gy + 1), g_points[g_gx][g_gy + 1]);
            }
        }
        gl.glEnd();
        gl.glPopMatrix();
        gl.glTranslatef(-5, 63 + (126 * g_b), 0);
        gl.glBegin(GL2.GL_QUADS);
        for (g_gx = 0; g_gx < 63; g_gx++) {
            for (g_gy = 0; g_gy < 63; g_gy++) {
                gl.glVertex3f( (float)(g_gx) / 1.5f, (float)(g_gy), g_points[g_gx][g_gy]);
                gl.glVertex3f( (float)(g_gx + 1) / 1.5f, (float)(g_gy), g_points[g_gx + 1][g_gy]);
                gl.glVertex3f( (float)(g_gx + 1) / 1.5f, (float)(g_gy + 1), g_points[g_gx + 1][g_gy + 1]);
                gl.glVertex3f( (float)(g_gx) / 1.5f, (float)(g_gy + 1), g_points[g_gx][g_gy + 1]);
            }
        }
        gl.glEnd();
        //glDisable(GL2.GL_FOG);
        gl.glLoadIdentity();

        gl.glTranslatef(0, -3, -40);
        gl.glDisable(GL2.GL_CULL_FACE);

        //glBindTexture(GL2.GL_TEXTURE_2D, g_Text[ure[0]);
        if (g_scene == 0)
            g_Text[2].use(gl);
        else if (g_scene == 1)
            g_Text[12].use(gl);
        else
            g_Text[14].use(gl);
        gl.glDisable(GL2.GL_TEXTURE_GEN_S);
        gl.glDisable(GL2.GL_TEXTURE_GEN_T);

        gl.glRotatef(g_rot, 1, 0, 0);
        //glTranslatef(-8+g_rot/2,9+g_rot/2,0);
        gl.glTranslatef(-0, 8.8f + g_rot / 5, 0);
        //glScalef(30,8+g_rot/2,1);
        gl.glScalef(22.1f, 8.8f + g_rot / 5, 1);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(0, 1);
        gl.glVertex3f(-1.0f, -1.0f, 0.0f);
        gl.glTexCoord2d(1, 1);
        gl.glVertex3f(1.0f, -1.0f, 0.0f);
        gl.glTexCoord2d(1, 0);
        gl.glVertex3f(1.0f, 1.0f, 0.0f);
        gl.glTexCoord2d(0, 0);
        gl.glVertex3f(-1.0f, 1.0f, 0.0f);
        gl.glEnd();
        for (g_gy = 0; g_gy < 64; g_gy++) for (g_gx = 0; g_gx < 64; g_gx++) g_points[g_gx][g_gy] = .025f * (float)Math.sin(g_speed[g_gx][g_gy] * g_rota + g_phase[g_gx][g_gy]);

        gl.glEnable(GL2.GL_BLEND);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        g_Text[10].use(gl);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        if (g_scene != 2)
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
        else
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glLoadIdentity();
        if (g_scene == 0)
            gl.glTranslatef(.15f, -.11f, -1.0f);//MOON
        else if (g_scene == 1)
            gl.glTranslatef(0, -.1075f, -1.0f);//DUSK
        else
            gl.glTranslatef(0, -.095f, -1.0f);//DAY
        if (g_scene == 0)
            gl.glScalef(2.0f, .07f, 1);
        else if (g_scene == 1)
            gl.glScalef(1.5f, .07f, 1);
        else
            gl.glScalef(2.5f, .05f, 1);
        gl.glColor4f(1, 1, 1, 1);
        g_drawquadr(gl, 1);
        gl.glDisable(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_DEPTH_TEST);


        g_rota = -.3f + ((float) (g_gettime - limit)) / 2500.0f;
        g_rot = 1.5f + (g_zeta - 1.0f) / 15.0f;
        //else if (!g_scene) g_rot=1.5f+(g_zeta-1.0f)/15.0f;

        gl.glEnable(GL2.GL_CULL_FACE);
        if ((g_radius > -6.0f) && (g_radius < 3.0f)) {
            gl.glLoadIdentity();
            gl.glTranslatef(-4, 1, -5);

            gl.glScalef(2, 1.25f, 1);
            gl.glRotatef(-45, 0, 0, 1);

            gl.glTranslatef(2 * (float)Math.cos(g_radius), .5f * (float)Math.sin(g_radius), -g_radius * 1.5f);

            gl.glRotatef(45 + g_radius * 15, 0, 0, 1);
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_DST_COLOR, GL2.GL_ZERO);
            //glBindTexture(GL2.GL_TEXTURE_2D, g_Text[ure[3]);
            g_Text[4].use(gl);
            g_drawquad(gl, 1);

            gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE);
            //glBindTexture(GL2.GL_TEXTURE_2D, g_Text[ure[2]);
            g_Text[3].use(gl);
            g_drawquad(gl, 1);

            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);/////////////////////////////////////////
            gl.glColor4f(1.0f, 1.0f, 1.0f, g_litetop);
            //glBindTexture(GL2.GL_TEXTURE_2D, g_Text[ure[6]);
            g_Text[7].use(gl);
            g_drawquad(gl, 1);////////////////////////////////////////////
            g_litetop -= .05f;
            if (g_litetop < 0) g_litetop = 1.0f;

            gl.glColor4f(.10f, .75f, .25f, g_liteleft);
            //glBindTexture(GL2.GL_TEXTURE_2D, g_Text[ure[7]);
            g_Text[8].use(gl);
            g_drawquad(gl, 1);////////////////////////////////////////////
            g_liteleft -= .25f;
            if (g_liteleft < -2) g_liteleft = 1.0f;

            gl.glColor4f(1.0f, .25f, .25f, g_liteleft);
            //glBindTexture(GL2.GL_TEXTURE_2D, g_Text[ure[8]);
            g_Text[9].use(gl);
            g_drawquad(gl, 1);////////////////////////////////////////////
            g_literite -= .1f;
            if (g_literite < -1.5) g_literite = 1.0f;

            gl.glLoadIdentity();
            gl.glTranslatef(-4, 1, -5);
            gl.glScalef(2, 1.25f, 1);
            gl.glRotatef(-45, 0, 0, 1);
            gl.glTranslatef(2 * (float)Math.cos(g_radius), .5f * (float)Math.sin(g_radius), -g_radius * 1.5f);

            gl.glRotatef(-45, 0, 0, 1);

            gl.glEnable(GL2.GL_TEXTURE_2D);
            //glBindTexture(GL2.GL_TEXTURE_2D, g_Text[ure[4]);
            g_Text[5].use(gl);
            //glDisable(GL2.GL_FOG);

            gl.glDisable(GL2.GL_DEPTH_TEST);

            gl.glLoadIdentity();
            if (g_radius < 0.0f)
                gl.glTranslatef(-4.075f, .8f, -5);
            else
                gl.glTranslatef(-4.075f + g_radius / 11, .8f, -5);
            gl.glScalef(2, 1.25f, 1);
            gl.glRotatef(-45, 0, 0, 1);
            gl.glTranslatef(2 * (float)Math.cos(g_radius), .5f * (float)Math.sin(g_radius), -g_radius * 1.5f);

            gl.glRotatef(45, 0, 0, 1);
            gl.glRotatef(20 + g_radius * 20, 1, 0, 0);

            if (g_radius < 0.0f)
                gl.glRotatef(-5, 0, 1, 0);
            else
                gl.glRotatef(-5 + 12.5f * g_radius, 0, 1, 0);

            for (int i = 0; i < g_num + 1; i++) {
                gl.glPushMatrix();
                if (i == g_num) {
                    gl.glColor4f(1, 1, 1, .25f + .0005f * ((float) (Math.abs(random.nextInt()) % 1000)));
                    gl.glRotatef(20 + g_radius * 20, 1, 0, 0);
                    if (g_radius < 0.0f)
                        gl.glRotatef(5, 0, 1, 0);
                    else
                        gl.glRotatef(5 - 12.5f * g_radius, 0, 1, 0);
                    gl.glRotatef(-20 - g_radius * 20, 1, 0, 0);
                    //glBindTexture(GL2.GL_TEXTURE_2D, g_Text[ure[5]);
                    g_Text[6].use(gl);
                    gl.glScalef(.35f, .7f, 1);
                    gl.glRotatef(-g_radius * 50, 0, 0, 1);
                    g_drawquad(gl, 1 + (float)Math.cos(g_radius) * (float)Math.cos(g_radius));
                } else {
                    gl.glTranslatef(0, 0, parts[i].z / 25);
                    gl.glColor4f(parts[i].r, parts[i].r / 2, parts[i].r / 4, parts[i].g_a);
                    gl.glRotatef(-5 - g_radius * 8, 0, 1, 0);
                    gl.glRotatef(-20 - g_radius * 20, 1, 0, 0);
                    gl.glScalef(.35f, .5f, 1);
                    g_drawquad(gl, parts[i].size / 2);
                    parts[i].g_a -= .15;
                    parts[i].z += parts[i].spd * 5;
                    if ((parts[i].g_a < 0.0f) || (parts[i].size < 0.0f)) g_rst(i);
                }
                gl.glPopMatrix();
            }

            gl.glLoadIdentity();
            gl.glTranslatef(-3 + 2 * (float)Math.cos(g_radius * 1.25), -1, -2 - 2 * g_radius);
            gl.glColor4f(.2f, .2f, .2f, .5f);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glRotatef(-90, 1, 0, 0);
            //glBindTexture(GL2.GL_TEXTURE_2D, g_Text[ure[4]);
            g_Text[5].use(gl);
            gl.glRotatef(25 * g_radius, 0, 0, 1);
            gl.glScalef(1, 3, 1);
            g_drawquad(gl, 1);
        }
        gl.glDisable(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        if (g_scene == 0)
            g_zeta = 66.0f + 45.0f * ((((float) g_gettime - limit)) / 13000.0f) * ((((float) g_gettime - limit)) / 13000.0f);
        else if (g_scene == 1)
            g_zeta = 15.0f + 25.0f * (((float) g_gettime - limit) / 1500.0f);
        else if (g_rota < 3.0f)
            g_zeta = 15.0f + 25.0f * (((float) g_gettime - limit) / 1500.0f);
        else
            g_zeta = 15.0f + 25.0f * (((float) g_gettime - limit) / 1500.0f) - 30.0f * (1.0f - (float)Math.cos((g_rota - 3.0f) / 5.0f * 3.1415f));

        if ((g_scene == 2) && (g_radius < 3)) g_radius = -6.0f + (g_zeta - 20.0f) * (g_zeta - 20.0f) / 2000.0f;

        gl.glEnable(GL2.GL_BLEND);
        gl.glDisable(GL2.GL_DEPTH_TEST);

        g_Text[0].use(gl);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glLoadIdentity();

        if (g_scene == 0)
            gl.glTranslatef(.23f, -.06f, -1.0f); // MOON
        else if (g_scene == 1)
            gl.glTranslatef(.07f, -.07f, -1.0f); // DUSK
        else
            gl.glTranslatef(.325f, -.08f, -1.0f); // SKY

        if (g_scene == 1) g_ext = 10.0f; else g_ext = 10.0f;

        for (int l = 0; l < g_num1; l++) {
            float time;
            time = ((float) (g_gettime - parts1[l].init)) / 1000.0f;
            if (50.0f * parts1[l].spd * time > 2.0f * 3.1415f)
                g_rst1(l);
            else {
                gl.glPushMatrix();
                if (g_scene == 0)
                    gl.glColor4f(parts1[l].r / 4, parts1[l].r / 2, parts1[l].r, .5f * parts1[l].h * parts1[l].g_a * (1.0f - (float)Math.cos(50.0f * parts1[l].spd * time))); // MOON
                else if (g_scene == 1)
                    gl.glColor4f(parts1[l].r, parts1[l].r, parts1[l].r / 2, .5f * parts1[l].h * parts1[l].g_a * (1.0f - (float)Math.cos(50.0f * parts1[l].spd * time)));   // DUSK
                else
                    gl.glColor4f(parts1[l].r / 4, parts1[l].r / 2, parts1[l].r, 1.0f * parts1[l].h * parts1[l].g_a * (1.0f - (float)Math.cos(50.0f * parts1[l].spd * time)));   // DUSK

                if ((l % 2) == 0)
                    gl.glTranslatef(parts1[l + 1].h * parts1[l].h / g_ext, 0, 0);
                else
                    gl.glTranslatef(-parts1[l - 1].h * parts1[l].h / g_ext, 0, 0);

                if (l > g_num1 * (3.0f / 4.0f))
                    gl.glTranslatef(0, -parts1[l].h / 3.0f, 0);
                else if (l > g_num1 * (2.0f / 4.0f))
                    gl.glTranslatef(0, -parts1[l].h / 4.0f, 0);
                else if (l > g_num1 * (1.0f / 4.0f))
                    gl.glTranslatef(0, -parts1[l].h / 5.0f, 0);
                else
                    gl.glTranslatef(0, -parts1[l].h / 6.0f, 0);
                gl.glRotatef(500.0f * time * parts1[l].spd, 0, 0, 1);
                g_drawquad(gl, .015f + ((1.0f - (float)Math.cos(50.0f * parts1[l].spd * time))) * parts1[l].size * parts1[l].h / 30.0f);
                if (l < g_num1 / 10) {
                    gl.glLoadIdentity();
                    if (g_scene == 0)
                        gl.glTranslatef(.42f, -.25f, -1.0f); // MOON
                    else if (g_scene == 1)
                        gl.glTranslatef(.12f, -.22f, -1.0f); // DUSK
                    else
                        gl.glTranslatef(.6f, -.2f, -1.0f); // DAY
                    gl.glTranslatef(0, -((float) l / (g_num1 / 10)) / 1.5f, -1.0f);
                    if (g_scene == 0)
                        gl.glColor4f(.25f, .75f, 1.0f, .25f);// MOON
                    else if (g_scene == 1)
                        gl.glColor4f(1.0f, 1.0f, .5f, .5f);// DUSK
                    else
                        gl.glColor4f(.25f, .75f, 1.0f, .25f);// DAY
                    if ((l % 2) == 0)
                        gl.glTranslatef(((float) l / (g_num1 / 10)) / 10.0f + parts1[l].h / 30.0f, 0, 0);
                    else
                        gl.glTranslatef(((float) l / (g_num1 / 10)) / 10.0f - parts1[l].h / 30.0f, 0, 0);
                    g_drawquad(gl, .015f + ((1.0f - (float)Math.cos(50.0f * parts1[l].spd * time))) * parts1[l].size * parts1[l].h / 1.5f);
                }
                gl.glPopMatrix();
            }
        }

        float cips = (((float) g_gettime - limit)) / 1500.0f;
        gl.glLoadIdentity();
        if (g_scene == 0) {
            g_Text[15].use(gl);
            gl.glTranslatef(.2f + g_rota / 1000.0f, .185f + g_rota / 1100.0f, -1.0f);
            gl.glColor4f(1, 1, 1, .5f);
            g_drawquad(gl, .2f + .05f * (float)Math.sin(cips));
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glColor4f(1, 1, 1, 1);
            g_Text[17].use(gl);
            g_drawquad(gl, .08f);
        } else if (g_scene == 1) {
            g_Text[16].use(gl);
            if (g_scene == 1) gl.glTranslatef(.068f, .04f + g_rota / 800.0f, -1.0f);
            //else gl.glTranslatef(.3,.5,-1.0f);
            gl.glColor4f(1, 1, 1, .35f);
            //glColor4f(0,0,0,0);
            gl.glRotatef((float)Math.sin(cips / 2.0f) * 50, 0, 0, 1);
            g_drawquad(gl, .4f + .75f * (float)Math.sin(cips / 1.5f) * (float)Math.sin(cips / 1.5f));
            gl.glRotatef((float)Math.sin(cips / 4.0f) * 100, 0, 0, 1);
            g_Text[0].use(gl);
            g_drawquad(gl, .2f + .3f * (float)Math.cos(cips) * (float)Math.cos(cips));
        } else {
            g_Text[16].use(gl);
            //if (g_scene==1) gl.glTranslatef(.068,.04,-1.0f);
            //else
            gl.glTranslatef(.3f, .3f, -1.0f);
            gl.glColor4f(1, 1, 1, .3f);
            gl.glRotatef((float)Math.sin(cips / 2.0f) * 50, 0, 0, 1);
            g_drawquad(gl, .3f + .5f * (float)Math.sin(cips / 1.5f) * (float)Math.sin(cips / 1.5f));
            gl.glRotatef((float)Math.sin(cips / 4.0f) * 100, 0, 0, 1);
            g_Text[0].use(gl);
            g_drawquad(gl, .2f + .3f * (float)Math.cos(cips) * (float)Math.cos(cips));
            g_Text[15].use(gl);
            g_drawquad(gl, .3f);//+.1*(float)Math.cos(cips)*(float)Math.cos(cips));
        }

        gl.glEnable(GL2.GL_BLEND);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        if ((g_rota < 1.0f) ||
                ((g_rota > 9.2f) && (g_scene == 0)) ||
                ((g_rota > 5.7f) && (g_scene == 1)) ||
                ((g_rota > 4.0f) && (g_scene == 2))
        ) {
            if ((g_scene == 0) && (g_rota < 1.0f))
                gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_ALPHA);
            else
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -.9f);
            if (g_rota < 0)
                gl.glColor4f(1, 1, 1, 1);
            else if (g_rota < 1.0f) gl.glColor4f(1, 1, 1, .5f * (1.0f + (float)Math.cos(g_rota * 3.1415f)));
            if (g_rota > 9.2) gl.glColor4f(1, 1, 1, .5f * (1.0f + (float)Math.cos(-3.1415f + (g_rota - 9.2f) * 4.0f * 3.1415f)));
            if ((g_scene == 1) && (g_rota > 5.7f)) gl.glColor4f(1, 1, 1, .5f * (1.0f + (float)Math.cos(-3.1415f + (g_rota - 5.7f) * 4.0f * 3.1415f)));
            if ((g_scene == 2) && (g_rota > 4.0f)) gl.glColor4f(1, 1, 1, .5f * (1.0f + (float)Math.cos(-3.1415f + (g_rota - 4.0f) * 3.1415f)));
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glScalef(1.1f, .8f, 1);
            g_drawquad(gl, 1);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }
        //glEnable(GL2.GL_DEPTH_TEST);
        gl.glDisable(GL2.GL_BLEND);
        if ((g_scene == 2) && (g_rota > 1.6) && (playjet)) {
            playjet = false;
//		FSOUND_PlaySound(FSOUND_FREE, jet);
        }
        //if (((!g_scene)&&(g_rota>11.75))||
        if (((g_scene == 0) && (g_rota > 9.45)) ||
                ((g_scene == 1) && (g_rota > 5.95))
        )
            g_scene++;


        if ((g_scene == 2) && (g_check == 0) && (g_rota > 5.0f)) {
            //******************* FINISH
            //g_Clean();
            return false;
        }
        //MessageBox(NULL,"","",0);
        g_gettime = (long)g_time;
        return true;
    }
}
