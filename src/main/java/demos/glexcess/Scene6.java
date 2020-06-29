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
import com.jogamp.opengl.util.gl2.GLUT;
import demos.common.ResourceRetriever;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
final class Scene6 implements Scene {
    private Texture[] e_Text;
    private static final int numtexs = 10;
    private static boolean init = true;
    private float e_time = 0;

    private final boolean[] dum = new boolean[4];
    private boolean e_lasers = false;
    private boolean e_scene = false;

    private final float[] e_FogColor = {1.0f, 1.0f, 1.0f, 1.0f};

    private float e_xrot;
    private float e_yrot;
    private float e_zrot;
    private float e_timer = -1.25f;
    private float e_speed = 0.0f;

    private float e_zeta = 0.0f;
    private float e_fade = 0.0f;

    private float e_rocca = 1.0f;
    private float e_depth = 60.0f;

    private float e_radius = 0.0f;
    private int shiplist;

    private final GLUT glut = new GLUT();

    private static int genship(GL2 gl) {
        int[][] face_indicies = null;
        float[][] vertices = null;
        float[][] normals = null;

        try {
            InputStream fighter = ResourceRetriever.getResourceAsStream("data/fighter.dat");
            BufferedReader in = new BufferedReader(new InputStreamReader(fighter));
            String line = in.readLine();
            while (line != null) {
                StringTokenizer headertok = new StringTokenizer(line);
                String type = headertok.nextToken();
                int dim1 = Integer.parseInt(headertok.nextToken());
                int dim2 = Integer.parseInt(headertok.nextToken());
                if (type.equals("face_indicies")) {
                    face_indicies = new int[dim1][dim2];
                    for (int i = 0; i < dim1; i++) {
                        StringTokenizer tok = new StringTokenizer(in.readLine());
                        for (int j = 0; j < dim2; j++) {
                            face_indicies[i][j] = Integer.parseInt(tok.nextToken());
                        }
                    }
                } else if (type.equals("vertices")) {
                    vertices = new float[dim1][dim2];
                    for (int i = 0; i < dim1; i++) {
                        StringTokenizer tok = new StringTokenizer(in.readLine());
                        for (int j = 0; j < dim2; j++) {
                            vertices[i][j] = Float.parseFloat(tok.nextToken());
                        }
                    }
                } else if (type.equals("normals")) {
                    normals = new float[dim1][dim2];
                    for (int i = 0; i < dim1; i++) {
                        StringTokenizer tok = new StringTokenizer(in.readLine());
                        for (int j = 0; j < dim2; j++) {
                            normals[i][j] = Float.parseFloat(tok.nextToken());
                        }
                    }
                }
                line = in.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int i,j;
        int lid = gl.glGenLists(1);
        gl.glNewList(lid, GL2.GL_COMPILE);

        gl.glBegin(GL2.GL_TRIANGLES);
        for (i = 0; i < face_indicies.length; i++) {
            if ((i % 1) == 0) {
                for (j = 0; j < 3; j++) {
                    int vi = face_indicies[i][j];
                    int ni = face_indicies[i][j + 3];//Normal index
                    gl.glNormal3f(normals[ni][0], normals[ni][1], normals[ni][2]);
                    gl.glVertex3f(vertices[vi][0], vertices[vi][1], vertices[vi][2]);
                }
            }
        }
        gl.glEnd();
        gl.glEndList();
        return lid;
    };

    private void init(GLAutoDrawable drawable, GLU glu) {
        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();

        e_Text = new Texture[numtexs];
        shiplist = genship(gl);
        for (int ev = 0; ev < 4; ev++) dum[ev] = true;
        e_lasers = false;
        e_scene = false;
        e_xrot = 0;
        e_yrot = 0;
        e_zrot = 0;
        e_timer = -1.25f;
        e_speed = 0.0f;
        e_zeta = 0.0f;
        e_fade = 0.0f;
        e_rocca = 1.0f;
        e_depth = 60.0f;
        e_radius = 0.0f;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 90.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        for (int i = 0; i < e_Text.length; i++) {
            e_Text[i] = new Texture();
        }
        try {
            e_Text[1].load(gl, glu, ResourceRetriever.getResourceAsStream("data/star.raw"));
            e_Text[2].load(gl, glu, ResourceRetriever.getResourceAsStream("data/cl.raw"));
            e_Text[3].load(gl, glu, ResourceRetriever.getResourceAsStream("data/mtop.raw"));
            e_Text[4].load(gl, glu, ResourceRetriever.getResourceAsStream("data/text2.raw"));
            e_Text[5].load(gl, glu, ResourceRetriever.getResourceAsStream("data/metal.raw"));
            e_Text[6].load(gl, glu, ResourceRetriever.getResourceAsStream("data/rusty2.raw"));
            e_Text[7].load(gl, glu, ResourceRetriever.getResourceAsStream("data/mfloor1.raw"));
            e_Text[8].load(gl, glu, ResourceRetriever.getResourceAsStream("data/ship.raw"));
            e_Text[9].load(gl, glu, ResourceRetriever.getResourceAsStream("data/sground.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        //glFogf(GL2.GL_FOG_MODE,GL2.GL_LINEAR);
        gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_EXP2);
        gl.glFogf(GL2.GL_FOG_START, 15.0f);
        gl.glFogf(GL2.GL_FOG_END, 25.0f);
        //glFogf(GL2.GL_FOG_DENSITY,0.175f);
        gl.glFogf(GL2.GL_FOG_DENSITY, 0.075f);
        e_FogColor[0] = 0.0f;
        e_FogColor[1] = 0.0f;
        e_FogColor[2] = 0.0f;
        gl.glFogfv(GL2.GL_FOG_COLOR, GLBuffers.newDirectFloatBuffer(e_FogColor));
        gl.glEnable(GL2.GL_FOG);
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glEnable(GL2.GL_FOG);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        e_Text[1].use(gl);
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
    }

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        e_Text[1].kill(gl);
        e_Text[2].kill(gl);
        e_Text[3].kill(gl);
        e_Text[4].kill(gl);
        e_Text[5].kill(gl);
        e_Text[6].kill(gl);
        e_Text[7].kill(gl);
        e_Text[8].kill(gl);
        e_Text[9].kill(gl);
        init = true;
    }

    private static void e_drawquad(GL2 gl, float size) {
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

    private void e_drawmquad(GL2 gl, float size, float mtex) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f - .1f * e_zeta * e_rocca);
        gl.glVertex3f(-0.5f * size, -0.5f * size, 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f - .1f * e_zeta * e_rocca);
        gl.glVertex3f(0.5f * size, -0.5f * size, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f * mtex - .1f * e_zeta * e_rocca);
        gl.glVertex3f(0.5f * size, 0.5f * size, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f * mtex - .1f * e_zeta * e_rocca);
        gl.glVertex3f(-0.5f * size, 0.5f * size, 0.0f);
        gl.glEnd();
    }

    private void e_drawmquad1(GL2 gl, float size, float mtex) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f + .1f * e_zeta * e_rocca, 0.0f);
        gl.glVertex3f(-0.5f * size, -0.5f * size, 0.0f);
        gl.glTexCoord2f(1.0f * mtex + .1f * e_zeta * e_rocca, 0.0f);
        gl.glVertex3f(0.5f * size, -0.5f * size, 0.0f);
        gl.glTexCoord2f(1.0f * mtex + .1f * e_zeta * e_rocca, 1.0f);
        gl.glVertex3f(0.5f * size, 0.5f * size, 0.0f);
        gl.glTexCoord2f(0.0f + .1f * e_zeta * e_rocca, 1.0f);
        gl.glVertex3f(-0.5f * size, 0.5f * size, 0.0f);
        gl.glEnd();
    }

    private void e_drawtrail(GL2 gl, float tsz) {
        //glBindTexture(GL2.GL_TEXTURE_2D, e_texture[0]);
        e_Text[1].use(gl);
        gl.glScalef(1 / .75f, 1 / .25f, 1 / .05f);

        for (int zx = 0; zx < 4; zx++) {
            gl.glPushMatrix();
            gl.glTranslatef(-.3f + (float) zx / 5, 0, 0);
            gl.glRotatef(-5 * e_yrot, 0, 1, 0);
            gl.glRotatef(-90, 0, 1, 0);
            gl.glRotatef(-45 - e_yrot * 2, 0, 0, 1);
            e_drawquad(gl, tsz);
            gl.glPopMatrix();
        }
    }

    private void e_drawtrailup(GL2 gl, float tsz) {
        //glBindTexture(GL2.GL_TEXTURE_2D, e_texture[0]);
        e_Text[1].use(gl);
        gl.glScalef(1 / .25f, 1 / .75f, 1 / .05f);

        for (int zx = 0; zx < 4; zx++) {
            gl.glPushMatrix();
            gl.glTranslatef(0, -.3f + (float) zx / 5, 0);
            gl.glRotatef(-5 * e_xrot, 1, 0, 0);
            gl.glRotatef(-90, 1, 0, 0);
            gl.glRotatef(-45 - e_xrot * 2, 0, 0, 1);
            e_drawquad(gl, tsz);
            gl.glPopMatrix();
        }
    }

    private static void e_Clear(GL2 gl, float quad) {
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -1.0f);
        gl.glColor4f(0, 0, 0, 1 - quad);
        e_drawquad(gl, 1.2f);
        gl.glDisable(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        gl.glEnable(GL2.GL_TEXTURE_2D);
    }

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float globtime) {
        if (init) {
            init(drawable, glu);
            init = false;
        }

        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();
        e_time = 3 * globtime;
        float eoffset = 4.75f;
        //glClearColor(e_FogColor[0]+.005,e_FogColor[0]+.005,e_FogColor[0]+.005,1);
        if ((e_timer < eoffset) || (e_timer > eoffset + 2.0))
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        else {
            gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
            e_Clear(gl, .5f - .5f * (float) Math.cos((e_timer - eoffset) * 3.1415f));
        }


        e_timer = -2.0f + (e_time) / 3300.0f;
        if (e_scene) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glLoadIdentity();
            gl.glDisable(GL2.GL_FOG);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glRotatef(5 * e_zrot, 0, 0, 1);
            gl.glRotatef(5 * e_yrot, 0, 1, 0);
            gl.glRotatef(5 * e_xrot, 1, 0, 0);
            gl.glTranslatef(0, 0, -40);
            gl.glColor4f(e_FogColor[0], e_FogColor[0], e_FogColor[0], 1);
            //glColor4f(1,1,1,1);
            if ((e_timer < 1) || (e_timer > 13)) e_drawquad(gl, 10);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_FOG);


            e_depth = 30 + 30 * e_radius;

            if (e_timer > -1.0f) {
                if ((e_timer < eoffset) || (e_timer > eoffset + 2)) gl.glDisable(GL2.GL_BLEND); else gl.glEnable(GL2.GL_BLEND);
                gl.glLoadIdentity();

                gl.glTranslatef(.75f * (float) Math.sin(e_timer), .35f * (float) Math.cos(e_timer), 0);

                gl.glRotatef(5 * e_zrot, 0, 0, 1);
                gl.glRotatef(5 * e_yrot, 0, 1, 0);
                gl.glRotatef(5 * e_xrot, 1, 0, 0);

                if ((e_timer > 6.0f) && (e_timer < 10.0)) e_xrot = (6.0f - ((e_timer - 6.0f) * 1.5f)) * ((float) Math.cos((e_timer - 6.0f) * 3.1415f / 2.0f) - 1.0f);
                if ((e_timer > 5.0f) && (e_timer < 9.0)) e_yrot = (36.0f - ((e_timer - 5.0f) * 9.0f)) * ((float) Math.cos((e_timer - 5.0f) * 3.1415f / 2.0f) - 1.0f);
                if ((e_timer > 2.0f) && (e_timer < 6.0)) e_zrot = -(9.0f - ((e_timer - 2.0f) * 4.5f)) * ((float) Math.cos((e_timer - 2.0f) * 3.1415f / 2.0f) - 1.0f);

                if ((e_timer > 10.0f) && (e_timer < 12.0)) e_xrot = -(6.0f - ((e_timer - 10.0f) * 3.0f)) * ((float) Math.cos((e_timer - 10.0f) * 3.1415f) - 1.0f);
                if ((e_timer > 12.0f) && (e_timer < 14.0)) e_xrot = (6.0f - ((e_timer - 12.0f) * 3.0f)) * ((float) Math.cos((e_timer - 12.0f) * 3.1415f) - 1.0f);
                if ((e_timer > 9.0f) && (e_timer < 13.0)) e_yrot = -(6.0f - ((e_timer - 9.0f) * 1.5f)) * ((float) Math.cos((e_timer - 9.0f) * 3.1415f / 2.0f) - 1.0f);
                if ((e_timer > 6.0f) && (e_timer < 10.0)) e_zrot = -(16.0f - ((e_timer - 6.0f) * 4.0f)) * ((float) Math.cos((e_timer - 6.0f) * 3.1415f / 2.0f) - 1.0f);
                if ((e_timer > 10.0f) && (e_timer < 16.0)) e_zrot = (6.0f - ((e_timer - 10.0f) * 1.0f)) * ((float) Math.cos((e_timer - 10.0f) * 3.1415f / 3.0f) - 1.0f);
                if ((e_timer > 16.0f) && (e_timer < 20.0)) e_zrot = (9.0f - ((e_timer - 16.0f) * 4.5f)) * ((float) Math.cos((e_timer - 16.0f) * 3.1415f / 3.0f) - 1.0f);

                if ((e_timer > 13.5f) && (e_timer < 15.0f)) e_radius = ((float) Math.cos((e_timer - 13.5f) * 3.1415f / 1.5f + 3.1415f) + 1.0f) / 2.0f;

                if ((e_timer > 13.25f) && (e_timer < 19.25f)) e_speed = .125f + ((float) Math.cos(3.1415f + (e_timer - 13.25f) * 3.1415f / 4.0f) + 1.0f) / 50.0f;//e_speed=.125+(float)Math.sin(3.1415f*.5f*(e_timer-13.25))*.0125f;//e_speed=.125+((float)Math.cos(3.1415f+(e_timer-13.25f)*3.1415f/2.0f)+1.0f)/50.0f;

                if (e_timer > 16.0f) e_fade = ((float) Math.cos((e_timer - 16.0f) * 3.1415f / 2.0f + 3.1415f) + 1.0f) * 20.0f;

                if ((e_timer > 15.0f) && (dum[0])) {
                    dum[0] = false;
                    e_lasers = true;
                    //FSOUND_PlaySound(FSOUND_FREE, blam);
                }

                int lim = 2;
                if (e_timer > 15.0f) lim = 1;

                for (int fb = 0; fb < lim; fb++) {
                    gl.glTranslatef(0, 0, e_fade - e_depth / 2 + fb * 3 * e_depth / 2);

                    gl.glPushMatrix();
                    //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[2]);
                    e_Text[3].use(gl);
                    gl.glScalef(1.122f, 1, e_depth);
                    gl.glTranslatef(0, 2.591f, 0);
                    gl.glRotatef(90, 1, 0, 0);
                    if (e_timer < 1.0f)
                        gl.glColor4f(e_timer / 2.0f, e_timer / 2.0f, e_timer / 2.0f, e_timer / 2.0f);
                    else
                        gl.glColor4f(.5f, .5f, .5f, .5f);
                    e_drawmquad(gl, 1, 6);				// ROOF TOP
                    gl.glPopMatrix();

                    gl.glPushMatrix();
                    //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[12]);
                    e_Text[7].use(gl);
                    gl.glScalef(2, 1, e_depth);
                    gl.glTranslatef(0, -1, 0);
                    gl.glRotatef(90, 1, 0, 0);
                    if (e_timer < 1.0f)
                        gl.glColor4f(e_timer, e_timer, e_timer, e_timer);
                    else
                        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    e_drawmquad(gl, 1, 6);				// FLOOR
                    gl.glPopMatrix();

                    for (int pp = 0; pp < 2; pp++) {
                        gl.glPushMatrix();
                        if (pp == 1) gl.glScalef(-1, 1, 1);
                        //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[11]);
                        e_Text[6].use(gl);
                        gl.glScalef(1, 1, e_depth);
                        gl.glTranslatef(-1.35f, -.65f, 0);
                        gl.glRotatef(45, 0, 0, 1);
                        gl.glRotatef(90, 0, 1, 0);
                        e_drawmquad1(gl, 1, 3);				// BOTT LEFT
                        gl.glPopMatrix();

                        gl.glPushMatrix();
                        if (pp == 1) gl.glScalef(-1, 1, 1);
                        //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[9]);
                        e_Text[5].use(gl);
                        gl.glScalef(1, 3, e_depth);
                        gl.glTranslatef(-1.13f, .383f, 0);
                        gl.glRotatef(-50, 0, 0, 1);
                        gl.glRotatef(90, 0, 1, 0);
                        gl.glScalef(1, 1.5f, 1);
                        e_drawmquad1(gl, 1, 6);				// TOP LEFT
                        gl.glPopMatrix();
                    }
                }

                gl.glTranslatef(0, 0, e_zeta / 1.75f);
                //glColor4ub(255,255,255,255);
                gl.glEnable(GL2.GL_BLEND);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                gl.glEnable(GL2.GL_TEXTURE_GEN_S);
                gl.glEnable(GL2.GL_TEXTURE_GEN_T);
                //glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
                gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);

                if ((e_timer > 1.0f) && (e_timer < 15.0f)) {
                    float var = 0;
                    if ((e_timer > 3) && (e_timer < 3.4)) var = .5f - .5f * (float) Math.cos((e_timer - 3) * 3.1415f * 5);
                    for (int pp = 0; pp < 2; pp++) {
                        gl.glPushMatrix();
                        //if (pp) gl.glScalef(-1,1,1);
                        //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[5]);
                        e_Text[4].use(gl);
                        gl.glScalef(1, 1, 3);
                        gl.glTranslatef(-1.75f + pp * 3.5f, -.65f, 0);

                        gl.glRotatef(90, 0, 1, 0);

                        gl.glScalef(.75f, .25f, .05f);
                        gl.glTranslatef(0, 2, .4f);
                        for (int zx = 0; zx < 21; zx++)			// LIGHTS SIDE
                        {

                            gl.glPushMatrix();
                            gl.glTranslatef(20.0f + zx * 2.9f, 0, 0);
                            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
                            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
                            //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[5]);
                            e_Text[4].use(gl);


                            gl.glColor4f(1.0f, 1.0f, 1.0f, .25f + .75f * var);
                            glut.glutSolidCube(/*gl,*/ 1);
                            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
                            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
                            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                            if (e_timer > 3)
                                e_drawtrail(gl, .75f + 1.5f * var);
                            else if (e_timer > 2.625) e_drawtrail(gl, 2 * (e_timer - 2.625f));
                            gl.glPopMatrix();
                        }
                        gl.glPopMatrix();
                    }

                    gl.glPushMatrix();
                    //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[5]);
                    e_Text[4].use(gl);
                    gl.glScalef(1.122f, 1, 3);
                    gl.glTranslatef(0, 2.591f, 0);
                    gl.glRotatef(90, 1, 0, 0);
                    // LIGHTS TOP
                    gl.glScalef(.25f, .75f, .05f);
                    gl.glTranslatef(0, 0, .4f);
                    for (int zx = 0; zx < 21; zx++) {

                        gl.glPushMatrix();
                        gl.glTranslatef(0, -20.0f - zx * 3.0f, 0);
                        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
                        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
                        //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[5]);
                        e_Text[4].use(gl);

                        gl.glColor4f(1.0f, 1.0f, 1.0f, .25f + .75f * var);
                        glut.glutSolidCube(/*gl,*/ 1);
                        gl.glDisable(GL2.GL_TEXTURE_GEN_S);
                        gl.glDisable(GL2.GL_TEXTURE_GEN_T);
                        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        if (e_timer > 3)
                            e_drawtrailup(gl, .75f + 1.5f * var);
                        else if (e_timer > 2.625) e_drawtrailup(gl, 2 * (e_timer - 2.625f));
                        gl.glPopMatrix();
                    }
                    gl.glPopMatrix();
                }
                gl.glDisable(GL2.GL_TEXTURE_GEN_S);
                gl.glDisable(GL2.GL_TEXTURE_GEN_T);

                gl.glTranslatef(0, 0, 1.9f - e_zeta / 1.75f);

                //glDisable(GL2.GL_BLEND);
                gl.glEnable(GL2.GL_DEPTH_TEST);
                if (e_timer < 0.0f)
                    e_radius = (float) Math.cos(e_timer * 3.1415 / 2.0f - 3.1415 / 2.0f) + 1.0f;//+3.1415/2.0f)+1.0f;
                else if (e_timer < 1.0f) e_radius = (float) Math.cos(e_timer * 3.1415 / 2.0f + 3.1415 / 2.0f) + 1.0f;
                //else if (e_timer<1.5f) e_radius=.5f-.5f*(float)Math.sin((e_timer-1.0f)*3.1415f);
                //glClearColor(e_radius,e_radius,e_radius,1.0f);
                e_FogColor[0] = e_radius;
                e_FogColor[1] = e_radius;
                e_FogColor[2] = e_radius;
                gl.glFogfv(GL2.GL_FOG_COLOR, GLBuffers.newDirectFloatBuffer(e_FogColor));
                //glFogf(GL2.GL_FOG_DENSITY,e_radius*20);
                if (e_timer > 16.5) gl.glFogf(GL2.GL_FOG_DENSITY, .075f + (e_timer - 16.5f) * (e_timer - 16.5f) * 3.0f);
                gl.glFogf(GL2.GL_FOG_START, 10 - 11 * e_radius);

                if (e_timer < 1.0f) {
                    gl.glLoadIdentity();		// LIGHT MASK
                    gl.glTranslatef(0, 0, -.1f);
                    gl.glRotatef(e_timer * 200, 0, 0, 1);
                    gl.glRotatef(180, 1, 0, 0);
                    gl.glEnable(GL2.GL_TEXTURE_GEN_S);
                    gl.glEnable(GL2.GL_TEXTURE_GEN_T);
                    //glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
                    gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
                    //glEnable(GL2.GL_BLEND);
                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - e_timer / 1.0f);
                    //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[0]);
                    e_Text[1].use(gl);
                    glut.glutSolidCone(/*glu, */.2f, e_timer / 3.5f, 20, 20);
                    gl.glDisable(GL2.GL_TEXTURE_GEN_S);
                    gl.glDisable(GL2.GL_TEXTURE_GEN_T);
                }

                if (e_lasers)				// e_lasers
                {
                    gl.glEnable(GL2.GL_TEXTURE_GEN_S);
                    gl.glEnable(GL2.GL_TEXTURE_GEN_T);
                    //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[13]);
                    e_Text[4].use(gl);
                    gl.glDisable(GL2.GL_DEPTH_TEST);
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, 0, -.1f);

                    gl.glRotatef(5 * e_zrot, 0, 0, 1);
                    gl.glRotatef(5 * e_yrot, 0, 1, 0);
                    gl.glRotatef(5 * e_xrot, 1, 0, 0);

                    gl.glPushMatrix();
                    gl.glRotatef(e_zeta * 2.5f, 0, 0, 1);
                    gl.glRotatef(179.5f, 1, 0, 0);

                    gl.glColor4f(1.0f, 1.0f, 1.0f, .25f + (e_timer - 16.0f) / 8.0f);
                    glut.glutSolidCone(/*glu, */.25f, 100 - (e_zeta / 10.0f - 100) / 2.0f, 10, 5);
                    gl.glPopMatrix();

                    //glBindTexture(GL2.GL_TEXTURE_2D, e_Text[ure[1]);

                    e_Text[2].use(gl);

                    gl.glPushMatrix();
                    gl.glRotatef(-e_zeta * 5.0f, 0, 0, 1);
                    gl.glRotatef(179.25f, 1, 0, 0);
                    gl.glColor4ub((byte) 128, (byte) 255, (byte) 128, (byte) 64);
                    gl.glColor4f(0.25f, 0.5f, 1.0f, .5f);
                    //	glColor4f(1.0f,1.0f,1.0f,.5f);
                    glut.glutSolidCone(/*glu, */.25f, 200 - (e_zeta / 1.5f - 200), 10, 3);
                    gl.glPopMatrix();

                    gl.glDisable(GL2.GL_TEXTURE_GEN_S);
                    gl.glDisable(GL2.GL_TEXTURE_GEN_T);
                    gl.glEnable(GL2.GL_TEXTURE_2D);
                }
            }
        } else {
            if (e_timer < -1.25f) {
                gl.glEnable(GL2.GL_BLEND);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glDisable(GL2.GL_TEXTURE_2D);
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -1.0f);
                gl.glColor4f(1, 1, 1, -2.0f * (e_timer + 1.25f));
                e_drawquad(gl, 1.2f);
                gl.glEnable(GL2.GL_TEXTURE_2D);
                gl.glDisable(GL2.GL_BLEND);
            } else
                e_scene = true;
        }


        if ((e_timer > 6.5) && (e_timer < 9.5)) {
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(45.0f + 50 * (.5f - .5f * (float) Math.cos((e_timer - 6.5f) * 3.1415f * 2.0f / 3.0f)), (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 90.0f);
            gl.glMatrixMode(GL2.GL_MODELVIEW);
        }

        if ((e_timer > 13) && (e_timer < 15)) {
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(45.0f + 15 * (.5f - .5f * (float) Math.cos((e_timer - 13) * 3.1415f / 2.0f)), (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 90.0f);
            gl.glMatrixMode(GL2.GL_MODELVIEW);
        }

        if (((e_timer > 5) && (e_timer < 9.75)) || ((e_timer > 12) && (e_timer < 14.1))) {
            if ((e_timer > 6.5) && (dum[1])) {
                dum[1] = false;//FSOUND_PlaySound(FSOUND_FREE, woosh);
            }
            if ((e_timer > 12.4) && (dum[3])) {
                dum[3] = false;//FSOUND_PlaySound(FSOUND_FREE, woosh1);
            }
            gl.glLoadIdentity();
            //float acc=0;
            gl.glTranslatef(.75f * (float) Math.sin(e_timer), .35f * (float) Math.cos(e_timer), 0);
            gl.glRotatef(5 * e_zrot, 0, 0, 1);
            gl.glRotatef(5 * e_yrot, 0, 1, 0);
            gl.glRotatef(5 * e_xrot, 1, 0, 0);
            //if (e_timer>7.7) acc=1.0f;
            //if (e_timer<7.7) gl.glTranslatef(-.25-.5*(float)Math.sin(e_timer),.5-.5*(float)Math.cos(e_timer),46-6*e_timer-25*acc*(e_timer-7.7)*(e_timer-7.7));
            if (e_timer < 7.7)
                gl.glTranslatef(-.25f - .5f * (float) Math.sin(e_timer), .5f - .5f * (float) Math.cos(e_timer), 46 - 6 * e_timer);
            else if (e_timer < 8.7)
                gl.glTranslatef(-.25f - .5f * (float) Math.sin(e_timer), .5f - .5f * (float) Math.cos(e_timer), 46 - 6 * e_timer + 4 * (.5f - .5f * (float) Math.cos((e_timer - 7.7f) * 3.1415f)));
            else if (e_timer < 11)
                gl.glTranslatef(-.25f - .5f * (float) Math.sin(e_timer) + (e_timer - 8.7f) * (e_timer - 8.7f), .5f - .5f * (float) Math.cos(e_timer) - 5 * (e_timer - 8.7f) * (e_timer - 8.7f), 50 - 6 * e_timer - 250 * (e_timer - 8.7f) * (e_timer - 8.7f) * (e_timer - 8.7f));
            else
                gl.glTranslatef(.5f - .5f * (float) Math.sin(e_timer), 1 - .25f * (e_timer - 12) * (e_timer - 12), -4 * (e_timer - 12) * (e_timer - 12) * (e_timer - 12));
            gl.glPushMatrix();
            if (e_timer < 11)
                gl.glRotatef(60 * (float) Math.sin(e_timer * 2.5) * (float) Math.sin(e_timer * 1.5), 0, 0, 1);
            else
                gl.glRotatef(-70 - 120 * (float) Math.sin((e_timer - 11.7) * 4.0), 0, 0, 1);
            gl.glDisable(GL2.GL_BLEND);
            gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            e_Text[8].use(gl);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glEnable(GL2.GL_CULL_FACE);
            //glDisable(GL2.GL_TEXTURE_2D);
            gl.glColor4f(1, 1, 1, 1);
            //glPushMatrix();
            gl.glScalef(1.5f, 1.5f, 1.5f);
            gl.glCallList(shiplist);
            gl.glPopMatrix();
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_CULL_FACE);
            gl.glEnable(GL2.GL_BLEND);
            e_Text[1].use(gl);
            gl.glTranslatef(0, 0, .5f);
            if (e_timer < 7.7) gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glRotatef(-5 * e_yrot, 0, 1, 0);
            if (e_timer > 8.6) {
                if (dum[2]) {
                    dum[2] = false;//FSOUND_PlaySound(FSOUND_FREE, sweep);}
                }
                e_Text[9].use(gl);
                if (e_timer < 13)
                    gl.glColor4f(1, 1, 1, .5f);
                else
                    gl.glColor4f(1, 1, 1, .5f - .5f * (e_timer - 13.0f) * 1.1f);
                if (e_timer < 8.7) {
                    e_drawquad(gl, (e_timer - 8.6f) * 20);
                    gl.glRotatef(e_timer * 100, 0, 0, 1);
                    e_drawquad(gl, (e_timer - 8.6f) * 50);
                } else {
                    e_drawquad(gl, 2);
                    gl.glRotatef(e_timer * 100, 0, 0, 1);
                    e_drawquad(gl, 5);
                }
            }
            gl.glTranslatef(0, 0, .05f);
            if (e_timer < 13)
                gl.glColor4f(1, 1, 1, 1);
            else
                gl.glColor4f(1, 1, 1, 1 - 1 * (e_timer - 13.0f) * 1.1f);
            e_Text[1].use(gl);
            e_drawquad(gl, 1);
        }

        if ((e_timer > 1.0f) && (e_timer < 2.0f)) e_speed = (float) Math.sin(3.1415f * .5f * (e_timer - 1.0)) * .125f;
        //if ((e_timer>2.0f)&&(e_timer<13.25f)) e_speed=.125f+.1f;

        e_zeta = e_speed * (e_timer - 1.0f) * 174.0f;

//	e_timer+=.005f;
//	e_timer=-1.75f+((float)(FSOUND_Stream_GetTime(stream)-limit))/3500.0f;

        if (e_timer > 16.75f) {
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            gl.glColor4f(1, 1, 1, 4.0f * (e_timer - 16.75f));
            e_drawquad(gl, 1.8f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glDisable(GL2.GL_BLEND);
            //glClearColor(-4.0f*(e_timer+1.0f),-4.0f*(e_timer+1.0f),-4.0f*(e_timer+1.0f),-4.0f*(e_timer+1.0f));
        }


        if (e_timer > 17.0f) {
            //*********************************** FINISH
            //e_Clean();
            return false;
        }
        return true;
    }
}
