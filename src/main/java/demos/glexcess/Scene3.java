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
import java.io.DataInputStream;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import demos.common.ResourceRetriever;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
final class Scene3 implements Scene {
    private Texture[] b_Text;
    private static final int numtexs = 17;
    private static boolean init = true;
    private float b_time = 0;

    private float[][][] b_points;
    private float b_zeta = -10.2f;
    private float b_count = 0;


    private boolean b_switch = true;
    private boolean flag = true;
    private boolean b_switch2 = true;
    private int face;
    private final int facesize = 128;

    private void dolist(GL2 gl) {
        b_points = new float[facesize][facesize][3];
        for (int a = 0; a < facesize; a++) {
            for (int b = 0; b < facesize; b++) {
                b_points[a][b][0] = 0;
                b_points[a][b][1] = 0;
                b_points[a][b][2] = 0;
            }
        }

        int[] data;
        try {
            DataInputStream din = new DataInputStream(
                    ResourceRetriever.getResourceAsStream("data/face.dat")
            );

            int size = facesize * facesize;
            data = new int[size];
            for (int i = 0; i < data.length; i++) {
                data[i] = din.readUnsignedByte();
            }
            din.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int x = 0; x < facesize; x++) {
            for (int y = 0; y < facesize; y++) {
                b_points[x][y][0] = (x / 5.0f) - 12.8f;
                b_points[x][y][1] = (y / 5.0f) - 12.8f;
                int temp = data[x * facesize + y];
                if (temp < 0) temp += 255;
                b_points[facesize - 1 - x][facesize - 1 - y][2] = temp / (25.6f * 2.5f);
            }
        }

        face = gl.glGenLists(1);
        gl.glNewList(face, GL2.GL_COMPILE);
        gl.glBegin(GL2.GL_QUADS);
        for (int x = 0; x < 127; x++) {
            for (int y = 0; y < 127; y++) {
                float float_x = x / 127.0f;
                float float_y = y / 127.0f;
                float float_xb = (x + 1) / 127.0f;
                float float_yb = (y + 1) / 127.0f;

                gl.glTexCoord2f(float_x, float_y);
                gl.glVertex3f(b_points[x][y][0], b_points[x][y][1], b_points[x][y][2]);

                gl.glTexCoord2f(float_x, float_yb);
                gl.glVertex3f(b_points[x][y + 1][0], b_points[x][y + 1][1], b_points[x][y + 1][2]);

                gl.glTexCoord2f(float_xb, float_yb);
                gl.glVertex3f(b_points[x + 1][y + 1][0], b_points[x + 1][y + 1][1], b_points[x + 1][y + 1][2]);

                gl.glTexCoord2f(float_xb, float_y);
                gl.glVertex3f(b_points[x + 1][y][0], b_points[x + 1][y][1], b_points[x + 1][y][2]);
            }
        }
        gl.glEnd();
        gl.glEndList();
    }

    private void init(GLAutoDrawable drawable, GLU glu) {
        b_zeta = -10.2f;
        b_count = 0;
        b_switch = true;
        flag = true;
        b_switch2 = true;

        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        // order TONY
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 100.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        b_Text = new Texture[numtexs];
        for (int i = 0; i < b_Text.length; i++) {
            b_Text[i] = new Texture();
        }
        try {
            b_Text[1].load(gl, glu, ResourceRetriever.getResourceAsStream("data/face.raw"));
            b_Text[2].load(gl, glu, ResourceRetriever.getResourceAsStream("data/glglow.raw"));
            b_Text[3].load(gl, glu, ResourceRetriever.getResourceAsStream("data/xs1.raw"));
            b_Text[4].load(gl, glu, ResourceRetriever.getResourceAsStream("data/logocol.raw"));
            b_Text[5].load(gl, glu, ResourceRetriever.getResourceAsStream("data/art.raw"));
            b_Text[6].load(gl, glu, ResourceRetriever.getResourceAsStream("data/g1.raw"));
            b_Text[7].load(gl, glu, ResourceRetriever.getResourceAsStream("data/trilogy1.raw"));
            b_Text[8].load(gl, glu, ResourceRetriever.getResourceAsStream("data/s.raw"));
            b_Text[9].load(gl, glu, ResourceRetriever.getResourceAsStream("data/t.raw"));
            b_Text[10].load(gl, glu, ResourceRetriever.getResourceAsStream("data/y.raw"));
            b_Text[11].load(gl, glu, ResourceRetriever.getResourceAsStream("data/l.raw"));
            b_Text[12].load(gl, glu, ResourceRetriever.getResourceAsStream("data/e.raw"));
            b_Text[13].load(gl, glu, ResourceRetriever.getResourceAsStream("data/design.raw"));
            b_Text[14].load(gl, glu, ResourceRetriever.getResourceAsStream("data/designs.raw"));
            b_Text[15].load(gl, glu, ResourceRetriever.getResourceAsStream("data/technique.raw"));
            b_Text[16].load(gl, glu, ResourceRetriever.getResourceAsStream("data/techniques.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
        gl.glFogf(GL2.GL_FOG_START, 40.0f);
        gl.glFogf(GL2.GL_FOG_END, 55.0f);
        gl.glFogf(GL2.GL_FOG_DENSITY, 0.175f);
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glFrontFace(GL2.GL_CW);

        gl.glDisable(GL2.GL_LIGHTING);
        dolist(gl);
    }

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        b_Text[1].kill(gl);
        b_Text[2].kill(gl);
        b_Text[3].kill(gl);
        b_Text[4].kill(gl);
        b_Text[5].kill(gl);
        b_Text[6].kill(gl);
        b_Text[7].kill(gl);
        b_Text[8].kill(gl);
        b_Text[9].kill(gl);
        b_Text[10].kill(gl);
        b_Text[11].kill(gl);
        b_Text[12].kill(gl);
        b_Text[13].kill(gl);
        b_Text[14].kill(gl);
        b_Text[15].kill(gl);
        b_Text[16].kill(gl);
        init = true;
    }

    private static void b_drawrect(GL2 gl, float b, float h) {
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

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float globtime) {
        if (init) {
            init(drawable, glu);
            init = false;
        }

        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();

        b_time = 4 * globtime;
        int x, y;
        float float_x, float_y, float_xb, float_yb;
        if ((b_zeta > 92.5f) && (b_zeta < 100.0f)) {
            float canc;
            if (b_zeta < 94.5f)
                canc = .375f * (1.0f - (float)Math.cos((b_zeta - 92.5f) * 3.1415f / 2.0f));
            else if (b_zeta > 98.0f)
                canc = .375f * (1.0f + (float)Math.cos((b_zeta - 98.0f) * 3.1415f / 2.0f));
            else
                canc = .75f;
            gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            gl.glColor4f(0, 0, 0, 1.0f - canc);
            b_drawrect(gl, 1.2f, 1.2f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        } else
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        if (b_zeta < 6.0f) {
            glu.gluLookAt(13 * (float)Math.sin(b_zeta / 10), 2 - (b_zeta + 2) * (b_zeta + 2) / 50, -9 + 10 * (float)Math.cos(b_zeta / 10),
                    13 * (float)Math.sin(.5 + b_zeta / 10), 1.5, -9 + 10 * (float)Math.cos(.5 + b_zeta / 10),
                    0, 1, 0);
            gl.glRotatef(-90, 1, 0, 0);
            gl.glRotatef(-90, 0, 0, 1);
        } else if (b_zeta < 37.0f) {
            if (b_switch) {
                gl.glFrontFace(GL2.GL_CCW);
                b_switch = false;
            }
            glu.gluLookAt(3 * (float)Math.cos((b_zeta - 7.5f) / 4.0f), 1 + (float)Math.cos((b_zeta - 8.5f) / 6.0f) * (float)Math.cos((b_zeta - 8.5f) / 6.0f), -16 + (b_zeta - 6.5f),
                    -1 + 3 * (float)Math.cos(b_zeta / 4.8f), -2, 3,
                    0, 1, 0);
            gl.glRotatef(180, 0, 0, 1);
            gl.glRotatef(10 * (float)Math.cos(b_zeta / 2.0f), 0, 0, 1);
            gl.glRotatef(-90, 1, 0, 0);
            gl.glRotatef(-90, 0, 0, 1);
        } else if (b_zeta < 80.0f) {
            gl.glTranslatef(0, 1, -13.0f + 3 * (float)Math.cos(b_zeta / 6));
            gl.glRotatef(100, 1, 0, 0);
            gl.glRotatef(b_zeta * 5.0f, 0, 0, 1);
        } else {
            if ((b_zeta - 81.8 > 0) && (b_zeta - 81.8 < 1))
                gl.glTranslatef(0, 1, -13.0f + (3.0f + .3f * (b_zeta - 80) * (b_zeta - 80)) * (float)Math.cos(b_zeta / 6) + .5f * (1.0f - (float)Math.cos((b_zeta - 81.8f) * 3.1415 * 2.0f)));
            else if ((b_zeta - 84.4 > 0) && (b_zeta - 84.4 < .35))
                gl.glTranslatef(0, 1, -13.0f + (3.0f + .3f * (b_zeta - 80) * (b_zeta - 80)) * (float)Math.cos(b_zeta / 6) + .25f * (1.0f - (float)Math.cos((b_zeta - 84.4f) * 3.1415 * 5.714f)));
            else if ((b_zeta - 84.75 > 0) && (b_zeta - 84.75 < 1.5))
                gl.glTranslatef(0, 1, -13.0f + (3.0f + .3f * (b_zeta - 80) * (b_zeta - 80)) * (float)Math.cos(b_zeta / 6) + 1.5f * (1.0f - (float)Math.cos((b_zeta - 84.75f) * 3.1415 * 1.33f)));
            else
                gl.glTranslatef(0, 1, -13.0f + (3.0f + .3f * (b_zeta - 80) * (b_zeta - 80)) * (float)Math.cos(b_zeta / 6));
            gl.glRotatef(100.0f + (float)Math.sin((b_zeta - 80.0f) / 7.0f) * (float)Math.sin((b_zeta - 80.0f) / 7.0f) * 80.0f, 1.0f, 0.0f, 0.0f);
            if (b_zeta < 88.4) {
                gl.glRotatef(80.0f * 5.0f + (b_zeta - 80.0f) * 8.9f * (float)Math.cos((b_zeta - 80) / 10), 0, 0, 1);
            } else {
                gl.glRotatef(90, 0, 0, 1);
            }
        }

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        /////////////////		ART			////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////

        float offset = 5.0f;
        if ((b_zeta > -offset) && (b_zeta < 6)) {
            gl.glPushMatrix();
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            b_Text[5].use(gl);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
            gl.glLoadIdentity();
            gl.glTranslatef(-.25f, .25f + (b_zeta + offset) / 30.0f, -2.0f);
            gl.glRotatef(180, 1, 0, 0);
            if ((b_zeta + offset) < 2) {
                gl.glColor4f(1, 1, 1, (b_zeta + offset) / 4.0f);
                b_drawrect(gl, .5f, (4.5f - 4.0f * (float)Math.sin((b_zeta + offset) * 3.1415f / 4.0f)));
            } else if ((b_zeta + offset) > 8) {
                gl.glColor4f(1, 1, 1, 1 - (b_zeta + offset - 8) / 2.5f);
                b_drawrect(gl, .5f, (.5f + 1.0f * (1.0f - (float)Math.cos((b_zeta + offset - 8) * 3.1415f / 4.0f))));
            } else {
                gl.glColor4f(1, 1, 1, (b_zeta + offset) / 4.0f);
                b_drawrect(gl, .5f, .5f);
            }
            gl.glLoadIdentity();
            gl.glTranslatef(.5f - (b_zeta + offset) / 10.0f, .25f + (b_zeta + offset) / 30.0f, -2.0f);
            b_Text[6].use(gl);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glColor4f((b_zeta + offset) / 4, (b_zeta + offset) / 4, (b_zeta + offset) / 4, 1);
            gl.glRotatef(180, 1, 0, 0);
            if ((b_zeta + offset) > 8)
                b_drawrect(gl, 1.5f - (b_zeta + offset) / 20.0f, (.5f + 1.0f * (1.0f - (float)Math.cos((b_zeta + offset - 8) * 3.1415 / 4.0f))));
            else
                b_drawrect(gl, 1.5f - (b_zeta + offset) / 20.0f, .5f);
            gl.glDisable(GL2.GL_BLEND);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glPopMatrix();
        }

        //////////////////////////////////////////////////////////

        if (b_zeta < 98.0f) {
            gl.glColor4f(b_count / 90.0f, b_count / 90.0f, b_count / 90.0f, 0);
            b_Text[1].use(gl);
            if (b_zeta < 92.5f) {
                if (!flag) {
                    gl.glCallList(face);
                } else {
                    gl.glBegin(GL2.GL_LINES);
                    for (x = 0; x < 127; x++) {
                        for (y = 0; y < 127; y++) {
                            float_x = x / 127.0f;
                            float_y = y / 127.0f;
                            float_xb = (x + 1) / 127.0f;
                            float_yb = (y + 1) / 127.0f;
                            float raiser = (float)Math.sin(b_count * 2 * 3.14 / 360.0);
                            gl.glTexCoord2f(float_x, float_y);
                            gl.glVertex3f(b_points[x][y][0], b_points[x][y][1], b_points[x][y][2] * raiser);
                            gl.glTexCoord2f(float_x, float_yb);
                            gl.glVertex3f(b_points[x][y + 1][0], b_points[x][y + 1][1], b_points[x][y + 1][2] * raiser);
                            gl.glTexCoord2f(float_xb, float_yb);
                            gl.glVertex3f(b_points[x + 1][y + 1][0], b_points[x + 1][y + 1][1], b_points[x + 1][y + 1][2] * raiser);
                            gl.glTexCoord2f(float_xb, float_y);
                            gl.glVertex3f(b_points[x + 1][y][0], b_points[x + 1][y][1], b_points[x + 1][y][2] * raiser);
                        }
                    }
                    gl.glEnd();
                }
            } else {
                gl.glLoadIdentity();
                gl.glFrontFace(GL2.GL_CW);
                gl.glScalef(-1, 1, 1);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                gl.glTranslatef(-0.001f, .028f, -1.2f - (b_zeta - 91.0f) / 3.5f);
                gl.glEnable(GL2.GL_BLEND);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - (b_zeta - 92.5f) / 5.5f);
                gl.glRotatef(-90, 0, 0, 1);
                b_drawrect(gl, .66f, .649f);
                gl.glEnable(GL2.GL_DEPTH_TEST);
                gl.glFrontFace(GL2.GL_CCW);
                gl.glDisable(GL2.GL_BLEND);
            }
        }
        if (-b_zeta > 5.2f) b_count = -(-b_zeta - 10.2f) * 18.0f;
        if ((b_zeta < -9.0f) || ((b_zeta > -3.5f) && (b_zeta < -2.5f)) || ((b_zeta > 5.5f) && (b_zeta < 6.5f)) || ((b_zeta > 36.0f) && (b_zeta < 38.0f)) ||
                ((b_zeta > 47.35f) && (b_zeta < 48.35f)) || ((b_zeta > 58.35f) && (b_zeta < 60.35f)) || ((b_zeta > 86.35) && (b_zeta < 87.35))) {
            gl.glLoadIdentity();
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glTranslatef(0, 0, -0.5f);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            if (b_zeta < -9)
                gl.glColor4f(1.0f, 1.0f, 1.0f, -b_zeta - 9.0f);
            else if (b_zeta < -2.5f)
                gl.glColor4f(1.0f, 1.0f, 1.0f, (float)Math.sin((b_zeta - 2.5) * 3.1415f));
            else if (b_zeta < 6.5f)
                gl.glColor4f(1.0f, 1.0f, 1.0f, (float)Math.sin((b_zeta - 5.5f) * 3.1415f));
            else if (b_zeta < 38.0f)
                gl.glColor4f(1.0f, 1.0f, 1.0f, .5f * (1.0f - (float)Math.cos((b_zeta - 36.0f) * 3.1415f)));
            else if (b_zeta < 48.35f)
                gl.glColor4f(1.0f, 1.0f, 1.0f, .85f * (float)Math.sin((b_zeta - 47.35f) * 3.1415f));
            else if (b_zeta < 60.35f)
                gl.glColor4f(1.0f, 1.0f, 1.0f, (float)Math.sin((b_zeta - 58.35f) * 3.1415f / 2));
            else
                gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - (b_zeta - 86.35f));
            if (b_switch) gl.glScalef(1, -1, 1);
            b_drawrect(gl, .6f, .45f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glDisable(GL2.GL_BLEND);
            gl.glEnable(GL2.GL_DEPTH_TEST);
        }

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        //////////////			GL				/////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////

        if (b_zeta > 92.5f) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glLoadIdentity();
            if (b_zeta < 97.5f) {
                b_Text[2].use(gl);
                for (int az = 0; az < 6; az++) {
                    float arg = (b_zeta - 92.5f - (float) az / 10.0f) * 3.1415f / 10.0f;
                    gl.glPushMatrix();
                    gl.glTranslatef(3.0f - 4.65f * (float)Math.sin(arg),
                            -0.075f * (float)Math.sin(arg),
                            -15.0f * (float)Math.sin(arg));
                    if (az == 0) gl.glColor4f(1, 1, 1, 1); else gl.glColor4f(1, 1, 1, (.6f - (float) az * .1f) * (1.0f - (b_zeta - 92.5f) / 5.0f));
                    gl.glRotatef(60 - 60 * (float)Math.sin((b_zeta - 92.5 - (float) az / 10.0f) * 3.1415 / 10), 0, 1, 0);
                    b_drawrect(gl, 4.9f, 2.5f);
                    gl.glPopMatrix();
                }
                if (b_zeta - 92.5f < 1.0f) {
                    gl.glDisable(GL2.GL_TEXTURE_2D);
                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - (b_zeta - 92.5f));
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, 0, -.75f);
                    b_drawrect(gl, 1.0f, .75f);
                }
            } else {
                gl.glTranslatef(-1.65f, -0.075f, -15.0f);
                b_Text[2].use(gl);
                if (b_zeta < 105.0f)
                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                else
                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - (b_zeta - 105.0f) / 5.0f);
                b_drawrect(gl, 4.9f, 2.5f);
            }
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_BLEND);
        }

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        //////////////			EXCESS			////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////

        if (b_zeta > 95.0f) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glLoadIdentity();
            if (b_zeta < 100.0f) {

                b_Text[3].use(gl);
                for (int az = 0; az < 6; az++) {
                    float arg = (b_zeta - 95.0f - (float) az / 10.0f) * 3.1415f / 10.0f;
                    gl.glPushMatrix();
                    gl.glTranslatef(-4.0f + 6.17f * (float)Math.sin(arg),
                            0.05f * (float)Math.sin(arg),
                            -15.0f * (float)Math.sin(arg));
                    if (az == 0) gl.glColor4f(1, 1, 1, 1); else gl.glColor4f(1, 1, 1, (.6f - (float) az * .1f) * (1.0f - (b_zeta - 95.0f) / 5.0f));
                    gl.glRotatef(-60 + 60 * (float)Math.sin((b_zeta - 95.0 - (float) az / 10.0f) * 3.1415 / 10), 0, 1, 0);
                    b_drawrect(gl, 5.6f, 5.35f);
                    gl.glPopMatrix();
                }
            } else {
                gl.glTranslatef(2.17f, 0.05f, -15.0f);
                b_Text[3].use(gl);
                if (b_zeta < 105.0f)
                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                else
                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - (b_zeta - 105.0f) / 5.0f);
                b_drawrect(gl, 5.6f, 5.35f);
            }
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_BLEND);
        }


        if (b_zeta > 100.0f) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -15.0f);
            b_Text[4].use(gl);
            gl.glColor4f(1.0f, 1.0f, 1.0f, (b_zeta - 100.0f) / 10.0f);
            b_drawrect(gl, 10, 5);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_BLEND);
        }

        b_zeta = -10.2f + (b_time) / 400.0f;
        if (b_zeta > 110.0f) {
            //b_Clean();
            return false;
            //***************************FINISH
        }

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        /////////////////		TRILOGY		///////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////

        offset = 59.35f;
        if ((b_zeta - offset > 0) && (b_zeta - offset < 20)) {
            float factor = 1.0f;
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glLoadIdentity();
            gl.glTranslatef(-.25f, 0, -1.5f);
            b_Text[7].use(gl);
            if ((b_zeta - offset) > 15) factor = 1.0f - (b_zeta - offset - 15) / 5.0f;
            for (int i = 0; i < 5; i++) {
                gl.glPushMatrix();
                gl.glRotatef(-((b_zeta - offset) - (float) i / 20.0f) * ((b_zeta - offset) - (float) i / 20.0f) * 5, 0, 0, 1);
                if (i != 0)
                    gl.glColor4f(1, 1, 1, .35f * factor * (1.0f - (float) i / 5.0f));
                else
                    gl.glColor4f(1, 1, 1, factor);
                b_drawrect(gl, .5f, .5f);
                gl.glPopMatrix();
            }
            gl.glDisable(GL2.GL_BLEND);
            gl.glEnable(GL2.GL_DEPTH_TEST);
        }

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        /////////////////		DESIGN		///////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        offset = 16.0f;
        if ((b_zeta - offset > 0) && (b_zeta - offset < 12)) {
            gl.glEnable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glLoadIdentity();
            gl.glTranslatef(.35f - (b_zeta - offset) / 20.0f, -.2f, -1.0f);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            if ((b_zeta - offset) < 4)
                gl.glColor4f((b_zeta - offset) / 10, (b_zeta - offset) / 10, (b_zeta - offset) / 10, 1);
            else if ((b_zeta - offset) > 8)
                gl.glColor4f(.4f - (b_zeta - offset - 8) * .1f, .4f - (b_zeta - offset - 8) * .1f, .4f - (b_zeta - offset - 8) * .1f, 1);
            else
                gl.glColor4f(.4f, .4f, .4f, 1);
            b_Text[14].use(gl);
            if ((b_zeta - offset) < 2)
                b_drawrect(gl, (4.55f - 4.0f * (float)Math.sin((b_zeta - offset) * 3.1415 / 4.0f)), .25f);
            else if ((b_zeta - offset) > 10)
                b_drawrect(gl, (.55f + 1.0f * (1.0f - (float)Math.cos((b_zeta - offset - 10) * 3.1415 / 4.0f))), .25f);
            else
                b_drawrect(gl, .55f, .25f);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            b_Text[13].use(gl);
            if ((b_zeta - offset) < 4)
                gl.glColor4f(1, 1, 1, (b_zeta - offset) / 6);
            else if ((b_zeta - offset) > 8)
                gl.glColor4f(1, 1, 1, .66f - (b_zeta - offset - 8) * .66f / 4);
            else
                gl.glColor4f(1, 1, 1, .66f);
            if ((b_zeta - offset) < 2)
                b_drawrect(gl, (4.5f - 4.0f * (float)Math.sin((b_zeta - offset) * 3.1415 / 4.0f)), .25f);
            else if ((b_zeta - offset) > 10)
                b_drawrect(gl, (.5f + 1.0f * (1.0f - (float)Math.cos((b_zeta - offset - 10) * 3.1415 / 4.0f))), .25f);
            else
                b_drawrect(gl, .5f, .25f);
            gl.glDisable(GL2.GL_BLEND);
            gl.glEnable(GL2.GL_DEPTH_TEST);
        }

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        /////////////////		STYLE		///////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        offset = 38.0f;
        if ((b_zeta - offset > 0) && (b_zeta - offset < 21.0f)) {
            float zoomer;
            //if (b_zeta-offset<10) zoomer=1-.1*(1.0f-(float)Math.cos((b_zeta-offset)*3.1415/5));
            if (b_zeta - offset < 10)
                zoomer = .8f + .1f * (1.0f - (float)Math.cos((b_zeta - offset) * 3.1415 / 10));
            else
                zoomer = 1;
            b_zeta = b_zeta - offset - 10.0f;
            if (b_zeta < 6) {
                float fall = 0.0f;
                if (b_zeta > 0) fall = b_zeta * b_zeta * b_zeta / 5.0f;
                gl.glEnable(GL2.GL_BLEND);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                gl.glLoadIdentity();
                gl.glTranslatef(0, 1.25f - fall, -5.0f * zoomer);
                b_Text[8].use(gl);
                float sizer;
                if (-b_zeta > 8.2) {
                    sizer = (float)Math.sin((b_zeta + 10.2) * 3.1415f / 4.0f);
                    gl.glColor4f(1, 1, 1, sizer);
                } else {
                    sizer = 1.0f;
                    gl.glColor4f(1, 1, 1, 1 - fall / 2.0f);
                }

                gl.glRotatef(90.0f - 90.0f * sizer, 0, 1, 0);
                gl.glRotatef(-15.0f * fall, 0, 0, 1);
                gl.glRotatef(-15.0f * fall, 1, 0, 0);
                b_drawrect(gl, .5f, .5f);
                gl.glDisable(GL2.GL_BLEND);
                gl.glEnable(GL2.GL_DEPTH_TEST);
            }
            if (b_zeta > -9.2f) {
                float fall = 0.0f;
                if (b_zeta > .5) fall = (b_zeta - .5f) * (b_zeta - .5f) * (b_zeta - .5f) / 5.0f;
                gl.glEnable(GL2.GL_BLEND);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                gl.glLoadIdentity();
                gl.glTranslatef(.5f, 1.25f - fall, -5.0f * zoomer);
                b_Text[9].use(gl);
                float sizer;
                if (-b_zeta > 7.2) {
                    sizer = (float)Math.sin((b_zeta + 9.2) * 3.1415f / 4.0f);
                    gl.glColor4f(1, 1, 1, sizer);
                } else {
                    sizer = 1.0f;
                    gl.glColor4f(1, 1, 1, 1 - fall / 2.0f);
                }
                gl.glRotatef(90.0f - 90.0f * sizer, 0, 1, 0);
                gl.glRotatef(25.0f * fall, 0, 0, 1);
                gl.glRotatef(25.0f * fall, 1, 0, 0);
                b_drawrect(gl, .5f, .5f);
                gl.glDisable(GL2.GL_BLEND);
                gl.glEnable(GL2.GL_DEPTH_TEST);
            }
            if (b_zeta > -8.2f) {
                float fall = 0.0f;
                if (b_zeta > 1.0) fall = (b_zeta - 1) * (b_zeta - 1) * (b_zeta - 1) / 5.0f;
                gl.glEnable(GL2.GL_BLEND);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                gl.glLoadIdentity();
                gl.glTranslatef(1, 1.25f - fall, -5.0f * zoomer);
                b_Text[10].use(gl);
                float sizer;
                if (-b_zeta > 6.2) {
                    sizer = (float)Math.sin((b_zeta + 8.2) * 3.1415f / 4.0f);
                    gl.glColor4f(1, 1, 1, sizer);
                } else {
                    sizer = 1.0f;
                    gl.glColor4f(1, 1, 1, 1 - fall / 2.0f);
                }
                gl.glRotatef(90.0f - 90.0f * sizer, 0, 1, 0);
                gl.glRotatef(10.0f * fall, 0, 0, 1);
                gl.glRotatef(10.0f * fall, 1, 0, 0);
                b_drawrect(gl, .5f, .5f);
                gl.glDisable(GL2.GL_BLEND);
                gl.glEnable(GL2.GL_DEPTH_TEST);
            }
            if (b_zeta > -7.2f) {
                float fall = 0.0f;
                if (b_zeta > 1.5) fall = (b_zeta - 1.5f) * (b_zeta - 1.5f) * (b_zeta - 1.5f) / 5.0f;
                gl.glEnable(GL2.GL_BLEND);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                gl.glLoadIdentity();
                gl.glTranslatef(1.5f, 1.25f - fall, -5.0f * zoomer);
                b_Text[11].use(gl);
                float sizer;
                if (-b_zeta > 5.2) {
                    sizer = (float)Math.sin((b_zeta + 7.2) * 3.1415f / 4.0f);
                    gl.glColor4f(1, 1, 1, sizer);
                } else {
                    sizer = 1.0f;
                    gl.glColor4f(1, 1, 1, 1 - fall / 2.0f);
                }
                gl.glRotatef(90.0f - 90.0f * sizer, 0, 1, 0);
                gl.glRotatef(-30.0f * fall, 0, 0, 1);
                gl.glRotatef(-30.0f * fall, 1, 0, 0);
                b_drawrect(gl, .5f, .5f);
                gl.glDisable(GL2.GL_BLEND);
                gl.glEnable(GL2.GL_DEPTH_TEST);
            }
            if (b_zeta > -6.2f) {
                float fall = 0.0f;
                if (b_zeta > 2.0) fall = (b_zeta - 2) * (b_zeta - 2) * (b_zeta - 2) / 5.0f;
                gl.glEnable(GL2.GL_BLEND);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                gl.glDisable(GL2.GL_DEPTH_TEST);
                gl.glLoadIdentity();
                gl.glTranslatef(2.0f, 1.25f - fall, -5.0f * zoomer);
                b_Text[12].use(gl);
                float sizer;
                if (-b_zeta > 4.2) {
                    sizer = (float)Math.sin((b_zeta + 6.2) * 3.1415f / 4.0f);
                    gl.glColor4f(1, 1, 1, sizer);
                } else {
                    sizer = 1.0f;
                    gl.glColor4f(1, 1, 1, 1 - fall / 2.0f);
                }
                gl.glRotatef(90.0f - 90.0f * sizer, 0, 1, 0);
                gl.glRotatef(20.0f * fall, 0, 0, 1);
                gl.glRotatef(20.0f * fall, 1, 0, 0);
                b_drawrect(gl, .5f, .5f);
                gl.glDisable(GL2.GL_BLEND);
                gl.glEnable(GL2.GL_DEPTH_TEST);
            }
            b_zeta = b_zeta + offset + 10;
        }

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        /////////////////	  TECHNIQUE		////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        offset = 62.5f;
        if ((b_zeta - offset > 0) && (b_zeta - offset < 15)) {
            b_zeta = b_zeta - offset - 10;
            gl.glEnable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.25f);
            float rot;
            if (-b_zeta > 6.2)
                rot = 1.0f - (float)Math.sin((b_zeta + 10.2) * 3.1415 / 8.0f);
            else if ((b_zeta + 10) > 10.0f)
                rot = -.5f * (1.0f - (float)Math.cos((b_zeta) * 3.1415f / 10.0f));
            else
                rot = 0.0f;
            gl.glRotatef(90.0f * rot, 1, 0, 0);
            gl.glTranslatef(0, 0, .25f);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            b_Text[16].use(gl);
            if (-b_zeta > 6.2)
                gl.glColor4f(1 - rot, 1 - rot, 1 - rot, 1);
            else
                gl.glColor4f(1 + 2 * rot, 1 + 2 * rot, 1 + 2 * rot, 1);
            b_drawrect(gl, .55f, .11f);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            b_Text[15].use(gl);
            if (-b_zeta > 6.2)
                gl.glColor4f(1, 1, 1, 1 - rot);
            else
                gl.glColor4f(1, 1, 1, 1 + 2 * rot);
            b_drawrect(gl, .5f, .0625f);
            gl.glDisable(GL2.GL_BLEND);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            b_zeta = b_zeta + offset + 10;
        }
        if ((flag) && (b_zeta > -3.0f)) flag = false;
        if (!b_switch2) b_zeta = -8;
        return true;
    }

}
