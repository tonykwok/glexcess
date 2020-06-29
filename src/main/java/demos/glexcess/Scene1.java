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
final class Scene1 implements Scene {
    private int cnt = 0;
    private int checker = 1;
    private float random;
    private Texture[] z_Text;
    private static final int numtexs = 17;
    private float z_time = 0;

    private static boolean init = true;

    private void init(GLAutoDrawable drawable, GLU glu) {
        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();
        int width = drawable.getSurfaceWidth();
        int height = drawable.getSurfaceHeight();

        random = (float) Math.random();
        checker = 1;
        cnt = 0;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) width / (float) height, 0.1f, 100.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        z_Text = new Texture[numtexs];
        for (int i = 0; i < z_Text.length; i++) {
            z_Text[i] = new Texture();
        }
        try {
            z_Text[0].load(gl, glu, ResourceRetriever.getResourceAsStream("data/introducing.raw"));
            z_Text[1].load(gl, glu, ResourceRetriever.getResourceAsStream("data/introducings.raw"));
            z_Text[2].load(gl, glu, ResourceRetriever.getResourceAsStream("data/opengl.raw"));
            z_Text[3].load(gl, glu, ResourceRetriever.getResourceAsStream("data/openglb.raw"));
            z_Text[4].load(gl, glu, ResourceRetriever.getResourceAsStream("data/glxcess.raw"));
            z_Text[5].load(gl, glu, ResourceRetriever.getResourceAsStream("data/glxcesss.raw"));
            z_Text[6].load(gl, glu, ResourceRetriever.getResourceAsStream("data/wholenew.raw"));
            z_Text[7].load(gl, glu, ResourceRetriever.getResourceAsStream("data/wholenews.raw"));
            z_Text[8].load(gl, glu, ResourceRetriever.getResourceAsStream("data/experience.raw"));
            z_Text[9].load(gl, glu, ResourceRetriever.getResourceAsStream("data/experiences.raw"));
            z_Text[10].load(gl, glu, ResourceRetriever.getResourceAsStream("data/featuring.raw"));
            z_Text[11].load(gl, glu, ResourceRetriever.getResourceAsStream("data/featurings.raw"));
            z_Text[12].load(gl, glu, ResourceRetriever.getResourceAsStream("data/back.raw"));
            z_Text[13].load(gl, glu, ResourceRetriever.getResourceAsStream("data/linenoise.raw"));
            z_Text[14].load(gl, glu, ResourceRetriever.getResourceAsStream("data/dust1.raw"));
            z_Text[15].load(gl, glu, ResourceRetriever.getResourceAsStream("data/dust2.raw"));
            z_Text[16].load(gl, glu, ResourceRetriever.getResourceAsStream("data/sh1.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0, 0, 0, 0);
        gl.glClearDepth(1.0f);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
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
        z_Text[0].kill(gl);
        z_Text[1].kill(gl);
        z_Text[2].kill(gl);
        z_Text[3].kill(gl);
        z_Text[4].kill(gl);
        z_Text[5].kill(gl);
        z_Text[6].kill(gl);
        z_Text[7].kill(gl);
        z_Text[8].kill(gl);
        z_Text[9].kill(gl);
        z_Text[10].kill(gl);
        z_Text[11].kill(gl);
        z_Text[12].kill(gl);
        z_Text[13].kill(gl);
        z_Text[14].kill(gl);
        z_Text[15].kill(gl);
        z_Text[16].kill(gl);
        init = true;
    }

    private static void z_drawrect(GL2 gl, float b, float h) {
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

    private static void z_drawrectb(GL2 gl, float b, float h, float shs, float sht) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f + shs, 0.0f + sht);
        gl.glVertex3f(-b / 2, -h / 2, 0.0f);
        gl.glTexCoord2f(1.0f + shs, 0.0f + sht);
        gl.glVertex3f(b / 2, -h / 2, 0.0f);
        gl.glTexCoord2f(1.0f + shs, 1.0f + sht);
        gl.glVertex3f(b / 2, h / 2, 0.0f);
        gl.glTexCoord2f(0.0f + shs, 1.0f + sht);
        gl.glVertex3f(-b / 2, h / 2, 0.0f);
        gl.glEnd();
    }

    private static void z_drawrectc(GL2 gl, float b, float h) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, .9f);
        gl.glVertex3f(-b / 2, 0, 0.0f);
        gl.glTexCoord2f(1.0f, 0.9f);
        gl.glVertex3f(b / 2, 0, 0.0f);
        gl.glColor4f(0, 0, 0, 0);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(b / 2, h, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-b / 2, h, 0.0f);
        gl.glEnd();
    }

    private static void z_draw(GL2 gl, float z_w, float z_h, float z_fact, float z_tlt) {
        gl.glPushMatrix();
        gl.glTranslatef(-z_tlt, -z_tlt * z_h / z_w, 0.0f);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);

        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f * z_w, 1.0f * z_h, 0.0f);

        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f * z_w, 1.0f * z_h, 0.0f);

        gl.glTexCoord2f(0.0f, .125f);
        gl.glVertex3f(-1.0f * z_w, -.75f * z_h, 0.0f);

        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f * z_w - z_fact, -1.0f * z_h - z_fact * z_h / z_w, 0.0f);

        gl.glEnd();
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(z_tlt, z_tlt * z_h / z_w, 0.0f);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);

        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f * z_w, -1.0f * z_h, 0.0f);

        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f * z_w, -1.0f * z_h, 0.0f);

        gl.glTexCoord2f(1.0f, 0.825f);
        gl.glVertex3f(1.0f * z_w, .75f * z_h, 0.0f);

        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f * z_w + z_fact, 1.0f * z_h + z_fact * z_h / z_w, 0.0f);

        gl.glEnd();
        gl.glPopMatrix();
    }

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float time) {
        GL2 gl = drawable.getGL().getGL2();
        if (init) {
            init(drawable, glu);
            init = false;
        }
        z_time = time * .0001f;
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        gl.glDisable(GL2.GL_BLEND);
        gl.glColor4f(1, 1, 1, 1);
        z_Text[12].use(gl);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -1 + .1f * (float) Math.sin(z_time * 10));
        gl.glRotatef(z_time * 50, 0, 0, 1);
        z_drawrect(gl, 1, 1);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(1, 1, 1, .25f);
        gl.glRotatef(-z_time * 100, 0, 0, 1);
        z_drawrect(gl, 1, 1);

        float z_offset;

        /////////////////////// INTRODUCING

        z_offset = 0.1f;
        if (((z_time - z_offset) > 0.0f) && ((z_time - z_offset) * 25.0f < 6.283f)) {
            gl.glLoadIdentity();
            gl.glTranslatef(.75f - (z_time - z_offset) * 2.0f, -.5f, -3.0f);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            z_Text[1].use(gl);
            float menne = .375f * (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f));
            gl.glColor4f(menne, menne, menne, 1);
            gl.glPushMatrix();
            gl.glTranslatef(.05f, 0, 0);
            z_drawrect(gl, 1.3f, .4f);
            gl.glPopMatrix();
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            z_Text[0].use(gl);
            for (int i = 1; i <= 5; i++) {
                if (i != 1)
                    gl.glColor4f(1, 1, 1, (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) / (5 + i * 5));
                else
                    gl.glColor4f(1, 1, 1, (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) * .5f);
                gl.glPushMatrix();
                gl.glTranslatef((float) i / (50.0f - (z_time - z_offset) * 135.0f), 0, 0);
                if ((z_time - z_offset) < .1f)
                    z_draw(gl, .5f, .1f, 0, 0);
                else
                    z_draw(gl, .5f, .1f, .05f * (1.0f - (float) Math.cos(((z_time - z_offset) - .1f) * 12.5f)), .05f * (1.0f - (float) Math.cos(((z_time - z_offset) - .1f) * 12.5f)));
                gl.glPopMatrix();
            }
        }

        //////////////////  WHOLE NEW

        z_offset = 0.2f;
        if (((z_time - z_offset) > 0.0f) && ((z_time - z_offset) * 25.0f < 6.283f)) {
            gl.glLoadIdentity();
            gl.glTranslatef(-.35f, .5f - (z_time - z_offset), -2.0f);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            z_Text[7].use(gl);
            float menne = .5f * (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f));
            gl.glColor4f(menne, menne, menne, 1);
            gl.glPushMatrix();
            gl.glTranslatef(-.05f, -.025f, 0);
            z_drawrect(gl, 1.1f, .35f);
            gl.glPopMatrix();
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            z_Text[6].use(gl);
            gl.glRotatef(180, 1, 0, 0);
            gl.glTranslatef(-(z_time - z_offset) * .5f, 0, 0);
            for (int i = 1; i <= 5; i++) {
                if (i != 1)
                    gl.glColor4f(1, 1, 1, (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) / (5 + i * 5));
                else
                    gl.glColor4f(1, 1, 1, (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) * .5f);
                gl.glPushMatrix();
                gl.glTranslatef(0, (float) i / (50.0f - (z_time - z_offset) * 150.0f), 0);
                if ((z_time - z_offset) < .1f)
                    z_draw(gl, .5f, .09f, 0, 0);
                else
                    z_draw(gl, .5f, .09f, .05f * (1.0f - (float) Math.cos(((z_time - z_offset) - .1f) * 12.5f)), .05f * (1.0f - (float) Math.cos(((z_time - z_offset) - .1f) * 12.5f)));
                gl.glPopMatrix();
            }
        }

        ////////////////// EXPERIENCE

        z_offset = 0.3f;

        if (((z_time - z_offset) > 0.0f) && ((z_time - z_offset) * 25.0f < 6.283f)) {
            gl.glLoadIdentity();
            gl.glScalef(1, -1, 1);
            gl.glTranslatef(-.5f + (z_time - z_offset) * 1.5f, 0.25f, -2.5f);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            z_Text[9].use(gl);
            float menne = .5f * (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f));
            gl.glColor4f(menne, menne, menne, 1);
            gl.glPushMatrix();
            gl.glTranslatef(.05f, 0, 0);
            z_drawrect(gl, 1.2f, .35f);
            gl.glPopMatrix();
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            z_Text[8].use(gl);
            gl.glRotatef(180, 1, 0, 0);
            for (int i = 1; i <= 5; i++) {
                if (i != 1)
                    gl.glColor4f(1, 1, 1, (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) / (5 + i * 5));
                else
                    gl.glColor4f(1, 1, 1, (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) * .5f);
                gl.glPushMatrix();
                gl.glTranslatef((float) i / (50.0f - (z_time - z_offset) * 135.0f), 0, 0);
                if ((z_time - z_offset) < .1f)
                    z_draw(gl, .5f, .09f, 0, 0);
                else
                    z_draw(gl, .5f, .09f, .05f * (1.0f - (float) Math.cos(((z_time - z_offset) - .1f) * 12.5f)), .05f * (1.0f - (float) Math.cos(((z_time - z_offset) - .1f) * 12.5f)));
                gl.glPopMatrix();
            }
        }

        //////////////////// FEATURING

        z_offset = 0.4f;
        if (((z_time - z_offset) > 0.0f) && ((z_time - z_offset) * 25.0f < 6.283f)) {
            gl.glLoadIdentity();
            gl.glScalef(1, -1, 1);
            gl.glTranslatef(.25f - (z_time - z_offset) * 2.0f, -.5f, -3.0f);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            z_Text[11].use(gl);
            float menne = .45f * (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f));
            gl.glColor4f(menne, menne, menne, 1);
            gl.glPushMatrix();
            gl.glTranslatef(.05f, .01f, 0);
            z_drawrect(gl, 1.3f, .4f);
            gl.glPopMatrix();
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            z_Text[10].use(gl);
            for (int i = 1; i <= 5; i++) {
                if (i != 1)
                    gl.glColor4f(1, 1, 1, (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) / (5 + i * 5));
                else
                    gl.glColor4f(1, 1, 1, (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) * .5f);
                gl.glPushMatrix();
                gl.glTranslatef((float) i / (50.0f - (z_time - z_offset) * 135.0f), 0, 0);
                if ((z_time - z_offset) < .1f)
                    z_draw(gl, .5f, .1f, 0, 0);
                else
                    z_draw(gl, .5f, .1f, .05f * (1.0f - (float) Math.cos(((z_time - z_offset) - .1f) * 12.5f)), .05f * (1.0f - (float) Math.cos(((z_time - z_offset) - .1f) * 12.5f)));
                gl.glPopMatrix();
            }
        }

        ///////////////////// OPENGL

        z_offset = 0.5f;
        if (((z_time - z_offset) > 0.0f) && ((z_time - z_offset) * 25.0f < 6.283f)) {
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -.9f);
            z_Text[3].use(gl);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            float menne = (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) * .5f;
            gl.glColor4f(menne, menne, menne, 1);
            z_drawrect(gl, .5f, .25f);
            //glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_COLOR);
            z_Text[2].use(gl);
            //menne=(1.0f-cos((z_time-z_offset)*25.0f))*.5f;
            gl.glColor4f(menne, menne, menne, menne);
            z_drawrect(gl, .5f, .25f);
        }

        ////////////////////// GL EXCESS

        z_offset = .7f;
        if (((z_time - z_offset) > 0.0f) && ((z_time - z_offset) * 25.0f < 6.283f)) {
            gl.glLoadIdentity();
            gl.glTranslatef(.05f - (z_time - z_offset) / 2.0f, 0.0f, -1.5f);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            z_Text[5].use(gl);
            float menne = .45f * (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f));
            gl.glColor4f(menne, menne, menne, 1);
            gl.glPushMatrix();
            //glTranslatef(0,0,0);
            z_drawrect(gl, 1.2f, .6f);
            gl.glPopMatrix();
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            z_Text[4].use(gl);
            for (int i = 1; i <= 5; i++) {
                if (i != 1)
                    gl.glColor4f(1, 1, 1, (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) / (5 + i * 5));
                else
                    gl.glColor4f(1, 1, 1, (1.0f - (float) Math.cos((z_time - z_offset) * 25.0f)) * .5f);
                gl.glPushMatrix();
                gl.glTranslatef((float) i / (100.0f - (z_time - z_offset) * 350.0f), 0, 0);
                if ((z_time - z_offset) < .1f)
                    z_draw(gl, .5f, .15f, 0, 0);
                else
                    z_draw(gl, .5f, .15f, .05f * (1.0f - (float) Math.cos(((z_time - z_offset) - .1f) * 12.5f)), .05f * (1.0f - (float) Math.cos(((z_time - z_offset) - .1f) * 12.5f)));
                gl.glPopMatrix();
            }
        }

        if (z_time < .1f) {
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            float fader = .5f + .5f * (float) Math.cos(z_time * 31.415f);
            gl.glColor4f(fader, fader, fader, 1);
            z_drawrect(gl, 1.33f, 1.0f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        }

        gl.glLoadIdentity();
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glTranslatef(0, 0, -1.5f);
        z_Text[13].use(gl);
        float ran = (float) Math.random() / 4f;
        gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_COLOR);
        float shaderf = .16f + .16f * (float) Math.sin(z_time * 100.0f);
        gl.glColor4f(.33f + shaderf + ran, .33f + shaderf + ran, .33f + shaderf + ran, 1);
        //z_drawrecta(2,2,2.0f*sin(z_time*10.0f),1.0f+.5f*cos(z_time*50.0f));
        //z_drawrecta(1.5,1.5,-1.5f*cos(z_time*22.0f),1.0f+.75f*cos(z_time*13.0f));
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, .25f + .25f * (float) Math.cos(z_time * 20.0f));
        z_drawrectb(gl, 1.5f, 4.5f, 0, z_time * 100);
        gl.glPopMatrix();
        gl.glColor4f(.33f - shaderf + ran, .33f - shaderf + ran, .33f - shaderf + ran, 1);
        gl.glScalef(-1, 1, 1);
        gl.glTranslatef(1, 0, -.75f - .75f * (float) Math.sin(z_time * 40.0f));
        z_drawrectb(gl, 3, 3, 0, -z_time * 100);
        //glEnable(GL2.GL_DEPTH_TEST);
        gl.glLoadIdentity();
        if ((int) (z_time * 50) == checker) {
            if (cnt >= 2) {
                checker++;
                cnt = 0;
                random = (float) Math.random();
            } else if (random > .2) cnt++; else cnt += 2;
            gl.glRotatef(360.0f * random, 0, 0, 1);
            gl.glTranslatef(.25f * random, 0, -1);
            if (random > .5) z_Text[14].use(gl); else z_Text[15].use(gl);
            gl.glRotatef(360.0f * random * random, 0, 0, 1);
            z_drawrect(gl, .1f + random / 3, .1f + random / 3);

            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            gl.glColor4f(1, 1, 1, (float) Math.random() * .05f);
            gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_ALPHA);
            z_drawrect(gl, 1.33f, 1.0f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }

        if (z_time > .92f) {
            float fader = (z_time - .92f) * 75.0f;
            if (fader > 1.0f) {
                //FINISH*****************************************************************
//			return TRUE;
                //z_Clean();
                return false;
            }
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1.0f);
            gl.glColor4f(1, 1, 1, fader);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            z_drawrect(gl, 1.33f, 1.0f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }

        z_offset = .8f;
        if (z_time > z_offset) {
            //if (swi) {FSOUND_PlaySound(FSOUND_FREE, cut);swi=false;}
            gl.glLoadIdentity();
            gl.glDisable(GL2.GL_DEPTH_TEST);
            z_Text[16].use(gl);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            gl.glTranslatef(-.05f, -.0175f, -1);
            gl.glRotatef(16, 0, 0, 1);
            gl.glRotatef(85, 1, 0, 0);
            gl.glColor4f(1, 1, 1, .95f + (float) Math.random() * .05f);
            if (z_time - z_offset < .0125f)
                z_drawrectc(gl, .75f * (float) Math.sin((z_time - z_offset) * 80 * 3.1415f / 2.0f), 1.5f);
            else
                z_drawrectc(gl, .75f, 1.5f);
            gl.glRotatef(8.5f, 1, 0, 0);
            gl.glColor4f(1, 1, 1, .95f + (float) Math.random() * .05f);
            if (z_time - z_offset < .0125f)
                z_drawrectc(gl, .75f * (float) Math.sin((z_time - z_offset) * 80 * 3.1415f / 2.0f), 1.5f);
            else
                z_drawrectc(gl, .75f, 1.5f);
        }
        return true;
    }
}
