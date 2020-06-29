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
final class Scene2 implements Scene {
    private final Random random = new Random();
    private static boolean init = true;
    private static boolean first = true;
    private float a_time = 0.0f;

    private Texture[] a_Text;
    private static final int numtexs = 5;

    private long a_gets = 0;

    private float gendep = 1.55f;
    private static final int size = 64;
    private final float[][][] norm = new float[size][size][3];
    private final float[][][] a_points = new float[size][size][3];
    private final float[] camera = new float[]{
        -12.8f, 12.8f, 5
    };
    private final float[] cameraray = new float[3];
    private final float[] rray = new float[3];
    private final float[][][] newcoord = new float[size][size][2];
    private float coeff = 7.1f;
    private int a_x, a_y;
    private float a_xrot;
    private float a_yrot;
    private float a_zrot;
    private float quantos = -1.0f;
    private float a_zeta = -1.0f;

    private static final int a_num = 200;

    private static final class a_part {
        float a_x,a_y,z;
        float a_mod;
        float speed,speedlim;
        int r,g,b,a;
        int angle;
        int time;
    }

    private final a_part[] parts = new a_part[a_num];

    private float a_counter = 0;
    private float a_mod;

    private final float[] a_diffuse = {0.2f, 0.2f, 0.2f, 1.0f};
    private final float[] a_ambient = {0.1f, 0.1f, 0.1f, 1.0f};
    private final float[] a_specular = {.750f, .750f, .750f, 1.0f};
    private final float[] a_emission = {0.2f, 0.2f, 0.2f, 1.0f};

    private final float[] a_LightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
    private final float[] a_LightDiffuse = {0.5f, 0.5f, 0.5f, 1.0f};
    private final float[] a_LightSpecular = {.5f, .5f, .5f, 1.0f};
    private final float[] a_LightPosition = {0.0f, 8.0f, -20.0f, 1.0f};
    private final float[] a_Sinus = new float[3];

    private static void copy(float[] vec0, float[] vec1) {
        vec0[0] = vec1[0];
        vec0[1] = vec1[1];
        vec0[2] = vec1[2];
    }

    private static void sub(float[] vec0, float[] vec1, float[] vec2) {
        vec0[0] = vec1[0] - vec2[0];
        vec0[1] = vec1[1] - vec2[1];
        vec0[2] = vec1[2] - vec2[2];
    }

    private static void scalDiv(float[] vec, float c) {
        vec[0] /= c;
        vec[1] /= c;
        vec[2] /= c;
    }

    private static void cross(float[] vec0, float[] vec1, float[] vec2) {
        vec0[0] = vec1[1] * vec2[2] - vec1[2] * vec2[1];
        vec0[1] = vec1[2] * vec2[0] - vec1[0] * vec2[2];
        vec0[2] = vec1[0] * vec2[1] - vec1[1] * vec2[0];
    }

    private static void normz(float[] vec) {
        float c = (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
        scalDiv(vec, c);
    }

    private void MakeNorm() {
        float[] a = new float[3];
        float[] b = new float[3];
        float[] c = new float[3];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                if (i != size - 1 && j != size - 1) {
                    sub(a, a_points[i][j + 1], a_points[i][j]);
                    sub(b, a_points[i + 1][j], a_points[i][j]);
                    cross(c, a, b);
                    normz(c);
                } else if (i == 0 && j == size - 1) {
                    sub(a, a_points[i][j - 1], a_points[i][j]);
                    sub(b, a_points[i + 1][j], a_points[i][j]);
                    cross(c, a, b);
                    normz(c);
                    c[0] = -c[0];
                    c[1] = -c[1];
                    c[2] = -c[2];
                } else if (i == size - 1 && j == 0) {
                    sub(a, a_points[i - 1][j], a_points[i][j]);
                    sub(b, a_points[i][j + 1], a_points[i][j]);
                    cross(c, a, b);
                    normz(c);
                } else {
                    sub(a, a_points[i][j - 1], a_points[i][j]);
                    sub(b, a_points[i - 1][j], a_points[i][j]);
                    cross(c, a, b);
                    normz(c);
                }

                copy(norm[i][j], c);
            }
    }

    private void init(GLAutoDrawable drawable, GLU glu) {
        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();
        gl.glDisable(GL2.GL_LIGHT0);
        a_time = 2.0f;
        a_gets = 0;
        gendep = 1.55f;
        cameraray[0] = 0;
        cameraray[1] = 0;
        cameraray[2] = 0;
        rray[0] = 0;
        rray[1] = 0;
        rray[2] = 0;
        a_x = 0;
        a_y = 0;
        a_xrot = 0;
        a_yrot = 0;
        a_zrot = 0;
        quantos = -1.0f;
        a_zeta = -1.0f;
        a_time = 2.0f;
        a_counter = 0;
        a_mod = 0;

        a_mod = 0;
        a_diffuse[0] = 0.2f;
        a_diffuse[1] = 0.2f;
        a_diffuse[2] = 0.2f;
        a_diffuse[3] = 1.0f;
        a_ambient[0] = 0.1f;
        a_ambient[1] = 0.1f;
        a_ambient[2] = 0.1f;
        a_ambient[3] = 1.0f;
        a_specular[0] = .75f;
        a_specular[1] = .75f;
        a_specular[2] = .75f;
        a_specular[3] = 1.0f;
        a_emission[0] = 0.2f;
        a_emission[1] = 0.2f;
        a_emission[2] = 0.2f;
        a_emission[3] = 1.0f;

        a_LightAmbient[0] = 0.5f;
        a_LightAmbient[1] = 0.5f;
        a_LightAmbient[2] = 0.5f;
        a_LightAmbient[3] = 1.0f;
        a_LightDiffuse[0] = 0.5f;
        a_LightDiffuse[1] = 0.5f;
        a_LightDiffuse[2] = 0.5f;
        a_LightDiffuse[3] = 1.0f;
        a_LightSpecular[0] = .5f;
        a_LightSpecular[1] = .5f;
        a_LightSpecular[2] = .5f;
        a_LightSpecular[3] = 1.0f;
        a_LightPosition[0] = 0.0f;
        a_LightPosition[1] = 8.0f;
        a_LightPosition[2] = -20.0f;
        a_LightPosition[3] = 1.0f;
        a_Sinus[0] = 0;
        a_Sinus[1] = 0;
        a_Sinus[2] = 0;

        a_xrot = 0;
        a_yrot = 0;
        a_zrot = 0;
        a_counter = 0;

        quantos = -1.0f;
        a_zeta = -1.0f;

        a_counter = 0;
        coeff = 7.1f;
        a_gets = 0;
        gendep = 1.55f;

        camera[0] = -12.8f;
        camera[1] = 12.8f;
        camera[2] = 5;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 100.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glClearDepth(1.0f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

        a_Text = new Texture[numtexs];
        for (int i = 0; i < a_Text.length; i++) {
            a_Text[i] = new Texture();
        }
        try {
            a_Text[1].load(gl, glu, ResourceRetriever.getResourceAsStream("data/logoxs.raw"));
            a_Text[2].load(gl, glu, ResourceRetriever.getResourceAsStream("data/white.raw"));
            a_Text[3].load(gl, glu, ResourceRetriever.getResourceAsStream("data/sun2.raw"));
            a_Text[4].load(gl, glu, ResourceRetriever.getResourceAsStream("data/star.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT1);

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, GLBuffers.newDirectFloatBuffer(a_LightDiffuse));
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, GLBuffers.newDirectFloatBuffer(a_LightAmbient));
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, GLBuffers.newDirectFloatBuffer(a_LightSpecular));
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, GLBuffers.newDirectFloatBuffer(a_LightPosition));

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, GLBuffers.newDirectFloatBuffer(a_diffuse));
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, GLBuffers.newDirectFloatBuffer(a_ambient));
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, GLBuffers.newDirectFloatBuffer(a_specular));
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 10.0f);

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        for (int a_x = 0; a_x < size; a_x++) {
            for (int a_y = 0; a_y < size; a_y++) {
                a_points[a_x][a_y][0] = a_x / (1.25f * size / 32);
                a_points[a_x][a_y][1] = a_y / (1.25f * size / 32);
                a_points[a_x][a_y][2] = 0;
            }
        }

        for (int p = 0; p < a_num; p++) {
            parts[p] = new a_part();
            parts[p].r = 128 + Math.abs(random.nextInt()) % 128;
            parts[p].g = 128 + Math.abs(random.nextInt()) % 128;
            parts[p].b = 128 + Math.abs(random.nextInt()) % 128;
            parts[p].a = -1;
            parts[p].angle = Math.abs(random.nextInt()) % 90;
            parts[p].a_mod = 0.0f;
            parts[p].speedlim = .005f + .0001f * ((float) (Math.abs(random.nextInt()) % 1000));
            parts[p].speed = parts[p].speedlim;
            parts[p].a_x = 0.0f;
            parts[p].a_y = 0.0f;
            parts[p].z = 0.0f;
        }

        a_mod = 1.0f;
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glEnable(GL2.GL_BLEND);

        gl.glDisable(GL2.GL_DEPTH_TEST);
        a_setpart();
    }

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        a_Text[1].kill(gl);
        a_Text[2].kill(gl);
        a_Text[3].kill(gl);
        a_Text[4].kill(gl);
        init = true;
    }

    private void a_setpart() {
        int time = (int) (a_time * 500 - 2);//**********************************************
        for (int a = 0; a < a_num; a++)
            parts[a].time = time;
    }

    private static void a_drawquad(GL2 gl, float size) {
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

    private void calcul(int xx, int yy) {
        if ((xx == 0) && (yy == 0)) MakeNorm();
        sub(cameraray, camera, a_points[xx][yy]);
        normz(cameraray);
        rray[0] = norm[xx][yy][0] * coeff + cameraray[0];
        rray[1] = norm[xx][yy][1] * coeff + cameraray[1];
        rray[2] = norm[xx][yy][2] * coeff + cameraray[2];
        scalDiv(rray, -1);
        normz(rray);

        float depth = gendep + a_points[xx][yy][2];
        float t = depth / rray[2];
        float mapx = a_points[xx][yy][0] + rray[0] * t;
        float mapz = a_points[xx][yy][1] + rray[1] * t;

        newcoord[xx][yy][0] = -(mapx - a_points[0][0][0]) / 25.6f;
        newcoord[xx][yy][1] = (mapz - a_points[size - 1][size - 1][1]) / 25.6f;

    }

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float globtime) {
        if (init) {
            init(drawable, glu);
            init = false;
        }

        GL2 gl = drawable.getGL().getGL2();

        a_time = 2.0f + globtime * .01f;
        // MOTION
        if (a_time < 10.0f) {
            a_zeta = 25.0f * (float) Math.cos((3.1415f / 2.0f) * (1 + a_time / 10.0f));
            a_xrot = -45.0f * (float) Math.cos((3.1415f / 2.0f) * (1 + a_time / 10.0f));
        } else {
            a_xrot = 45.0f - 30.0f * (float) Math.sin((a_time - 10.0f) / 20.0f) * (float) Math.sin((a_time - 10.0f) / 20.0f);
            a_zrot = 360.0f * (float) Math.sin((a_time - 10.0f) / 50.0f) * (float) Math.sin((a_time - 10.0f) / 50.0f);
            a_zeta = -25.0f + 5.0f * (float) Math.sin((a_time - 10.0f) / 10.0f) * (float) Math.sin((a_time - 10.0f) / 10.0f);
        }

        if (a_time > 90.0f) a_zeta = -20.0f + 10.0f * (1.0f - (float) Math.cos((a_time - 90.0f) * 3.1415f / 10.0f));

        if (a_zeta > -2.5f) a_zeta = -2.5f;

        if (a_mod > 0.5f) a_mod = 1.0f - .03f * (a_time - a_gets); else a_mod = .5f - 0.015f * (a_time - a_gets);
        if (a_mod < 0.0f) a_mod = 0.0f;
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -5);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        if (a_zeta > -20.0f)
            gl.glColor4f(0, 0, 0, -(a_zeta + 20.0f) / 40.0f + .25f);
        else
            gl.glColor4f(0, 0, 0, .25f);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_BLEND);
        a_drawquad(gl, 6);
        if (first)//a_time<3.01f)
        {
            gl.glDisable(GL2.GL_BLEND);
            gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)255);
            a_drawquad(gl, 6);
            gl.glEnable(GL2.GL_BLEND);
            first = false;
        }
        if (a_time > 95.0f) {
            gl.glColor4f(1.0f, 1.0f, 1.0f, (a_time - 95.0f) / 1.5f);
            a_drawquad(gl, 6);
        }
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glLoadIdentity();
        if (a_time > 30.0)
            gl.glTranslatef(0.0f, 1.5f, a_zeta);
        else
            gl.glTranslatef(0.0f, .5f + .5f * (1.0f - (float) Math.cos((a_time - 2.0f) * 3.1415f / 28.0f)), a_zeta);
        gl.glRotatef(-90 + 2 * a_xrot, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(a_yrot, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(a_zrot, 0.0f, 0.0f, 1.0f);


        for (int a_xx = 0; a_xx < size; a_xx++)
            for (int a_yy = 0; a_yy < size; a_yy++) {
                double raggio;
                double value;
                double arg;

                if (quantos > 0.0f) {
                    raggio = .5 * Math.sqrt((double) ((a_points[a_xx][a_yy][0] - a_points[size / 2][size / 2][0]) * (a_points[a_xx][a_yy][0] - a_points[size / 2][size / 2][0])
                            + (a_points[a_xx][a_yy][1] - a_points[size / 2][size / 2][1]) * (a_points[a_xx][a_yy][1] - a_points[size / 2][size / 2][1])));
                    arg = 2.5 * raggio - quantos * 2 + 30;
                    if ((arg < -2 * 6.28) || (arg > 4 * 6.28))
                        value = 0;
                    else
                        value = .05 * (float) Math.sin(arg) * (float) Math.sin(arg) * Math.exp(arg / 7);
                    a_points[a_xx][a_yy][2] = (float) value;
                }
                if (quantos > 10) {
                    raggio = .5 * Math.sqrt((double) ((a_points[a_xx][a_yy][0] - a_points[48][48][0]) * (a_points[a_xx][a_yy][0] - a_points[48][48][0])
                            + (a_points[a_xx][a_yy][1] - a_points[48][48][1]) * (a_points[a_xx][a_yy][1] - a_points[48][48][1])));
                    arg = 2.5 * raggio - (quantos - 10) * 3 + 30;
                    if ((arg < -2 * 6.28) || (arg > 4 * 6.28))
                        value = 0;
                    else
                        value = .025 * (float) Math.sin(arg) * (float) Math.sin(arg) * Math.exp(arg / 7);
                    a_points[a_xx][a_yy][2] += value;
                }
                if (quantos > 24) {
                    raggio = .5 * Math.sqrt((double) ((a_points[a_xx][a_yy][0] - a_points[50][22][0]) * (a_points[a_xx][a_yy][0] - a_points[50][22][0])
                            + (a_points[a_xx][a_yy][1] - a_points[50][22][1]) * (a_points[a_xx][a_yy][1] - a_points[50][22][1])));
                    arg = 3.0 * raggio - (quantos - 24) * 4 + 30;
                    if ((arg < -2 * 6.28) || (arg > 4 * 6.28))
                        value = 0;
                    else
                        value = .02 * (float) Math.sin(arg) * (float) Math.sin(arg) * Math.exp(arg / 7);
                    a_points[a_xx][a_yy][2] += value;
                }
                if (quantos > 32) {
                    raggio = .5 * Math.sqrt((double) ((a_points[a_xx][a_yy][0] - a_points[32][32][0]) * (a_points[a_xx][a_yy][0] - a_points[32][32][0])
                            + (a_points[a_xx][a_yy][1] - a_points[32][32][1]) * (a_points[a_xx][a_yy][1] - a_points[32][32][1])));
                    arg = 2.5 * raggio - (quantos - 32) * 3 + 30;
                    if ((arg < 0 * 6.28) || (arg > 4 * 6.28))
                        value = 0;
                    else
                        value = .035 * (float) Math.sin(arg) * (float) Math.sin(arg) * Math.exp(arg / 7);
                    a_points[a_xx][a_yy][2] += value;
                }

                calcul(a_xx, a_yy);
            }
        if (a_time > 34.0f) quantos = 2.0f + (a_time - 34.0f) / 1.5f;

        a_Text[1].use(gl);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, GLBuffers.newDirectFloatBuffer(a_diffuse));
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, GLBuffers.newDirectFloatBuffer(a_ambient));
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, GLBuffers.newDirectFloatBuffer(a_specular));
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 10.0f);
        gl.glPushMatrix();
        //glDisable(GL2.GL_LIGHTING);
        gl.glScalef(-1, -1, 1);
        gl.glColor4f(1, 1, 1, 1);

        gl.glPushMatrix();
        gl.glTranslatef(-12.8f, 12.8f, 0);


        gl.glNormal3f(0, 0, 1);
        for (int cc = 0; cc < 1; cc++) {
            if ((cc % 2) == 0) {
                gl.glScalef(1, -1, 1);
                gl.glFrontFace(GL2.GL_CCW);
            } else {
                gl.glScalef(-1, 1, 1);
                gl.glFrontFace(GL2.GL_CW);
            }
            gl.glBegin(GL2.GL_QUADS);
            for (a_x = 0; a_x < size - 1; a_x++) {
                if (true)//a_x%2==0)
                {
                    for (a_y = 0; a_y < size - 1; a_y++) {
                        gl.glTexCoord2f(newcoord[a_x][a_y][0], newcoord[a_x][a_y][1]);
                        //glNormal3f(-norm[a_x][a_y][0],-norm[a_x][a_y][1],-norm[a_x][a_y][2]);
                        gl.glVertex3f(a_points[a_x][a_y][0], a_points[a_x][a_y][1], a_points[a_x][a_y][2]);

                        gl.glTexCoord2f(newcoord[a_x][a_y + 1][0], newcoord[a_x][a_y + 1][1]);
                        //glNormal3f(-norm[a_x][a_y+1][0],-norm[a_x][a_y+1][1],-norm[a_x][a_y+1][2]);
                        gl.glVertex3f(a_points[a_x][a_y + 1][0], a_points[a_x][a_y + 1][1], a_points[a_x][a_y + 1][2]);

                        gl.glTexCoord2f(newcoord[a_x + 1][a_y + 1][0], newcoord[a_x + 1][a_y + 1][1]);
                        //glNormal3f(-norm[a_x+1][a_y+1][0],-norm[a_x+1][a_y+1][1],-norm[a_x+1][a_y+1][2]);
                        gl.glVertex3f(a_points[a_x + 1][a_y + 1][0], a_points[a_x + 1][a_y + 1][1], a_points[a_x + 1][a_y + 1][2]);

                        gl.glTexCoord2f(newcoord[a_x + 1][a_y][0], newcoord[a_x + 1][a_y][1]);
                        //glNormal3f(-norm[a_x+1][a_y][0],-norm[a_x+1][a_y][1],-norm[a_x+1][a_y][2]);
                        gl.glVertex3f(a_points[a_x + 1][a_y][0], a_points[a_x + 1][a_y][1], a_points[a_x + 1][a_y][2]);
                    }
                } else {
                    for (a_y = size - 2; a_y >= 0; a_y--) {
                        gl.glTexCoord2f(newcoord[a_x][a_y][0], newcoord[a_x][a_y][1]);
                        gl.glVertex3f(a_points[a_x][a_y][0], a_points[a_x][a_y][1], a_points[a_x][a_y][2]);

                        gl.glTexCoord2f(newcoord[a_x][a_y + 1][0], newcoord[a_x][a_y + 1][1]);
                        gl.glVertex3f(a_points[a_x][a_y + 1][0], a_points[a_x][a_y + 1][1], a_points[a_x][a_y + 1][2]);

                        gl.glTexCoord2f(newcoord[a_x + 1][a_y + 1][0], newcoord[a_x + 1][a_y + 1][1]);
                        gl.glVertex3f(a_points[a_x + 1][a_y + 1][0], a_points[a_x + 1][a_y + 1][1], a_points[a_x + 1][a_y + 1][2]);

                        gl.glTexCoord2f(newcoord[a_x + 1][a_y][0], newcoord[a_x + 1][a_y][1]);
                        gl.glVertex3f(a_points[a_x + 1][a_y][0], a_points[a_x + 1][a_y][1], a_points[a_x + 1][a_y][2]);
                    }
                }
            }
            gl.glEnd();
        }
        gl.glPushMatrix();
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glPopMatrix();
        gl.glDisable(GL2.GL_LIGHTING);
        a_Text[4].use(gl);
        a_counter = a_time * 10.0f;

        for (int p = 0; p < a_num; p++) {
            float time;
            time = a_time * 500 - 2.0f - parts[p].time;//********************************************************
            gl.glPushMatrix();
            gl.glTranslatef(parts[p].a_x, parts[p].a_y, parts[p].z);
            gl.glRotatef(-a_zrot, 0, 0, 1);
            gl.glRotatef(90 - 2.0f * a_xrot, 1, 0, 0);
            gl.glRotatef(parts[p].angle - 135, 0, 0, 1);
            gl.glTranslatef(parts[p].a_mod, 0, 0);

            if (a_time < 20.0f)
                gl.glColor4ub((byte)parts[p].r, (byte)parts[p].g, (byte)parts[p].b, (byte)((int) ((parts[p].a - (int) (time / 8.0f)) * (a_time - 6.0f) / 14.0)));
            else
                gl.glColor4ub((byte)parts[p].r, (byte)parts[p].g, (byte)parts[p].b, (byte)(parts[p].a - (int) (time / 8.0f)));

            if (a_time > 6.0) a_drawquad(gl, 1.125f - .75f * p / a_num);
            parts[p].a_mod = parts[p].speed * time / 35.0f;
            parts[p].speed = parts[p].speedlim - time / 2500000.0f;
            if (parts[p].speed < 0.005f) parts[p].speed = 0.005f;
            if (parts[p].a - (int) (time / 8.0f) < 3) {
                parts[p].a_x = 10.0f * (float) Math.sin(a_counter * 4.0f * 3.14f / 360.0f);
                parts[p].a_y = 0.0f + 10.0f * (float) Math.sin(a_counter * 2 * 3.14 / 360.0);
                parts[p].z = a_Sinus[2] = 3.0f - 2.5f * (float) Math.cos(a_counter * 8.0f * 3.14f / 360.0f);
                parts[p].r = 128 + Math.abs(random.nextInt()) % 128;
                parts[p].g = 128 + Math.abs(random.nextInt()) % 128;
                parts[p].b = 128 + Math.abs(random.nextInt()) % 128;
                parts[p].a = Math.abs(random.nextInt()) % 255;
                parts[p].a_mod = 0.0f;
                parts[p].speedlim = .005f + .0001f * ((float) (Math.abs(random.nextInt()) % 1000));
                parts[p].speed = parts[p].speedlim;
                parts[p].time = (int) (a_time * 500 - 2);//*********************************
            }

            gl.glPopMatrix();
        }
        gl.glPushMatrix();
        a_Sinus[0] = 10.0f * (float) Math.sin(a_counter * 4.0f * 3.14f / 360.0f);
        a_Sinus[1] = 0.0f + 10.0f * (float) Math.sin(a_counter * 2 * 3.14 / 360.0);
        a_Sinus[2] = 3.0f - 2.5f * (float) Math.cos(a_counter * 8.0f * 3.14f / 360.0f);
        gl.glTranslatef(a_Sinus[0], a_Sinus[1], a_Sinus[2]);

        gl.glRotatef(-a_zrot, 0, 0, 1);
        gl.glRotatef(90 - 2 * a_xrot, 1, 0, 0);
        gl.glColor4ub((byte)255, (byte)128, (byte)255, (byte)255);
        gl.glColor4ub((byte)128, (byte)192, (byte)255, (byte)255);
        gl.glRotatef(2 * a_counter, 0, 0, 1);

        a_LightPosition[0] = 0;//a_Sinus[0];//10.0f*(float)Math.sin(a_counter*4.0f*3.14f/360.0f);
        a_LightPosition[1] = 0;//a_Sinus[1];//0.0f+10.0f*(float)Math.sin(a_counter*2*3.14/360.0);
        a_LightPosition[2] = 0;//a_Sinus[2];//3.0f-2.5f*(float)Math.cos(a_counter*8.0f*3.14f/360.0f);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, GLBuffers.newDirectFloatBuffer(a_LightPosition));
        gl.glDisable(GL2.GL_LIGHTING);
        a_Text[2].use(gl);
        a_drawquad(gl, 1.0f + (float) Math.sin(a_counter * 12.0f * 3.1415f / 360.0f) + 3.1415f / 2.0f);
        a_Text[3].use(gl);
        a_drawquad(gl, 3.0f + 2 * (float) Math.sin(a_counter * 6.0f * 3.1415f / 360.0f));
        gl.glPopMatrix();

/*
	a_LightPosition[0]=10.0f*(float)Math.sin(a_counter*4.0f*3.14f/360.0f);
	a_LightPosition[1]=0.0f+10.0f*(float)Math.sin(a_counter*2*3.14/360.0);
	a_LightPosition[2]=3.0f-2.5f*(float)Math.cos(a_counter*8.0f*3.14f/360.0f);
//	glTranslatef(a_LightPosition[0],a_LightPosition[1],a_LightPosition[2]);
	glEnable(GL2.GL_LIGHTING);
	glLightfv(GL2.GL_LIGHT1,GL2.GL_POSITION,a_LightPosition);
	*/

//	a_time=2.0f+(1)/500.0f;//************************************


        if (a_time > 96.0f) {
            //****************************** FINISH
            //a_Clean();
            return false;
        }
        return true;
    }
}
