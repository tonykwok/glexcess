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
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.gl2.GLUT;
import demos.common.ResourceRetriever;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
final class Scene7 implements Scene {
    private final Random random = new Random();
    private Texture[] f_Text;
    private static final int numtexs = 7;
    private static boolean init = true;
    private float f_time = 0;

    private int f_cycle;

    private boolean f_play = true;
    private boolean f_play1 = true;
    private boolean f_play2 = true;

    private float f_rot = 0.0f;
    private float f_timer = 0.0f;
    private float f_factor = 1.0f;
    private long f_frames = 0;
    private float f_zeta = -15.0f;//.001;
    private float f_end = 1.0f;
    private final int[][] f_phase = new int[64][64];
    private final int[][] f_speed = new int[64][64];

    private int f_shade;
    private int f_shadetop;
    private int f_flare;

    private final int[][][] f_side = new int[10][10][10];
    private final int f_num = 500;
    private final int f_acn = 250;

    private static final class f_particle {
        int alfa,f_shade;
        float mod,f_speed;
    }

    private static final class f_acc {
        int arot;
        long ainit;
        float amod,aspeed,aalfa,arad;
    }

    private final f_particle[] particles = new f_particle[f_num];
    private final f_acc[] accs = new f_acc[f_acn];

    private final float[][][] f_angle = new float[10][10][10];

    private final float[] f_FogColor = {1.0f, 1.0f, 1.0f, 1.0f};
    private float f_density = 0.025f;
    private final GLUT glut = new GLUT();

    private void f_initacc(int naccs) {
        accs[naccs] = new f_acc();
        accs[naccs].amod = .001f * (float) (Math.abs(random.nextInt()) % 1000);
        accs[naccs].arot = Math.abs(random.nextInt()) % 360;
        accs[naccs].aalfa = .001f * (float) (Math.abs(random.nextInt()) % 1000);
        accs[naccs].aspeed = .00075f * (float) (Math.abs(random.nextInt()) % 1000);
        accs[naccs].ainit = (long) f_time;
        accs[naccs].arad = .05f + .00035f * (float) (Math.abs(random.nextInt()) % 1000);
    }

    private void init(GLAutoDrawable drawable, GLU glu) {
        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();
        f_Text = new Texture[numtexs];
        f_density = 0.025f;
        f_cycle = 0;
        f_play = true;
        f_play1 = true;
        f_play2 = true;

        f_zeta = -15.0f;
        f_shade = 0;
        f_shadetop = 0;
        f_flare = 0;


        f_rot = 0.0f;
        f_timer = 0.0f;
        f_factor = 1.0f;
        f_frames = 0;

        f_end = 1.0f;

        f_frames = 0;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 100.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

//	FILE* f_in=NULL;
//	f_in=fopen("Data\\data","r");

        for (int i = 0; i < f_Text.length; i++) {
            f_Text[i] = new Texture();
        }
        try {
            f_Text[1].load(gl, glu, ResourceRetriever.getResourceAsStream("data/text.raw"));
            f_Text[2].load(gl, glu, ResourceRetriever.getResourceAsStream("data/white.raw"));
            f_Text[3].load(gl, glu, ResourceRetriever.getResourceAsStream("data/circle.raw"));
            f_Text[4].load(gl, glu, ResourceRetriever.getResourceAsStream("data/circleempty.raw"));
            f_Text[5].load(gl, glu, ResourceRetriever.getResourceAsStream("data/circlefill.raw"));
            f_Text[6].load(gl, glu, ResourceRetriever.getResourceAsStream("data/sground.raw"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        for (int f_x = 0; f_x < 64; f_x++) {
            for (int f_y = 0; f_y < 63; f_y++) {
                f_phase[f_x][f_y] = Math.abs(random.nextInt()) % 5;
                f_speed[f_x][f_y] = Math.abs(random.nextInt()) % 10;
            }
        }
        for (int f_x = 0; f_x < 64; f_x++) {
            f_phase[f_x][63] = f_phase[f_x][0];
            f_speed[f_x][63] = f_speed[f_x][0];
        }

        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
        gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_EXP2);
        gl.glFogf(GL2.GL_FOG_DENSITY, .025f);
        f_FogColor[0] = 0.0f;
        f_FogColor[1] = 0.0f;
        f_FogColor[2] = 0.0f;
        gl.glFogfv(GL2.GL_FOG_COLOR, GLBuffers.newDirectFloatBuffer(f_FogColor));
        gl.glEnable(GL2.GL_FOG);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                for (int k = 0; k < 10; k++) {
                    float f_a = Scene7Data.datas[(i * 100 + j * 10 + k) * 4];
                    float f_b = Scene7Data.datas[(i * 100 + j * 10 + k) * 4 + 1];
                    float f_c = Scene7Data.datas[(i * 100 + j * 10 + k) * 4 + 2];
                    int f_d = Scene7Data.datas[(i * 100 + j * 10 + k) * 4 + 3];

                    //f_a-=48;				f_b-=48;				f_c-=48;				f_d-=48;

                    f_angle[i][j][k] = 100 * f_a + 10 * f_b + f_c;
                    f_side[i][j][k] = f_d;
                }

        for (f_cycle = 0; f_cycle < f_num; f_cycle++) {
            particles[f_cycle] = new f_particle();
            particles[f_cycle].mod = 0.0f;
            particles[f_cycle].alfa = Math.abs(random.nextInt()) % 360;
            particles[f_cycle].f_shade = Math.abs(random.nextInt()) % 128;
            particles[f_cycle].f_speed = .00000075f * (float) (Math.abs(random.nextInt()) % 10000);
        }

        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        for (f_cycle = 0; f_cycle < f_acn; f_cycle++) f_initacc(f_cycle);
    }

    public final void clean(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        f_Text[1].kill(gl);
        f_Text[2].kill(gl);
        f_Text[3].kill(gl);
        f_Text[4].kill(gl);
        f_Text[5].kill(gl);
        f_Text[6].kill(gl);
        init = true;
    }

    private static void f_drawquad(GL2 gl, float size) {
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

    public final boolean drawScene(GLAutoDrawable drawable, GLU glu, float globtime) {
        if (init) {
            init(drawable, glu);
            init = false;
        }

        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = g.getGLU();

        f_time = 10 * globtime;
        if (f_zeta < 32.5f) {
            if (f_timer < 1.0f)
                f_frames++;
            else
                f_factor = 20.0f / (float) f_frames;
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            gl.glLoadIdentity();
            glu.gluLookAt(5, 5, -3f + f_zeta, 10, 10, 10, 0, 1, 0);

            gl.glPushMatrix();
            gl.glTranslatef(10, 10, 10);
            f_Text[1].use(gl);
            gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
            gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
            gl.glScalef(30, 30, 30);
            gl.glFrontFace(GL2.GL_CW);
            gl.glColor3ub((byte) 128, (byte) 160, (byte) 192);
            gl.glPushMatrix();
            gl.glRotatef(f_rot, 1, 0, 0);
            glut.glutSolidSphere(/*glu, */1, 50, 50);
            gl.glPopMatrix();
            gl.glPopMatrix();

            gl.glFrontFace(GL2.GL_CCW);
            f_Text[1].use(gl);
            gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
            gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
            gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 128);
            for (int f_x = 9; f_x >= 0; f_x--)
                for (int f_y = 9; f_y >= 0; f_y--)
                    for (int f_z = 9; f_z >= 0; f_z--) {
                        gl.glPushMatrix();
                        gl.glTranslatef(2f * (float)f_x / f_end, 2f * (float)f_y / f_end, 2f * (float)f_z / f_end);
                        if (f_side[f_x][f_y][f_z] == 0) gl.glRotatef(f_rot, 1, 0, 0);
                        if (f_side[f_x][f_y][f_z] == 1) gl.glRotatef(f_rot, 0, 1, 0);
                        if (f_side[f_x][f_y][f_z] == 2) gl.glRotatef(f_rot, 0, 0, 1);
                        if (f_side[f_x][f_y][f_z] == 3) gl.glRotatef(f_rot, 0, 1, 1);
                        if (f_side[f_x][f_y][f_z] == 4) gl.glRotatef(f_rot, 1, 0, 1);
                        if (f_side[f_x][f_y][f_z] == 5) gl.glRotatef(f_rot, 1, 1, 0);
                        if (f_side[f_x][f_y][f_z] == 6) gl.glRotatef(f_rot, 1, 1, 1);

                        if ((f_timer > 12.75) && (f_x == 3) && (f_y == 3) && (f_z == 4) && (f_timer < 17)) {
                            gl.glColor4f(1, .65f, .35f, .75f);
                            gl.glDisable(GL2.GL_DEPTH_TEST);
                            gl.glDisable(GL2.GL_CULL_FACE);
                            gl.glEnable(GL2.GL_BLEND);
                            float value = (f_angle[f_x][f_y][f_z] / (360f * 2f));
                            float tot = 5;
                            for (float times = 0; times < tot; times++) {
                                if (times == 0) gl.glColor4f(1, .65f, .35f, 1.0f);
                                gl.glColor4f(1, .65f, .35f, .5f - .5f * times / tot);
                                glut.glutSolidCube(/*gl, */value + .035f * times / tot);
                            }
                            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
                            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
                            if (true) {
                                f_Text[5].use(gl);
                                gl.glColor4f(1, 1, 1, 1);
                                gl.glRotatef(-70, 0, 1, 0);
                                gl.glRotatef(45, 1, 0, 0);
                                long tm;
                                tm = (long) f_time;
                                for (int nac = 0; nac < f_acn; nac++) {
                                    float asker = 1.5f * accs[nac].aspeed * ((float) (tm - accs[nac].ainit) * (tm - accs[nac].ainit)) / 1000000.0f;
                                    gl.glPushMatrix();
                                    gl.glRotatef(accs[nac].arot, 0, 0, 1);
                                    gl.glTranslatef(.35f * (accs[nac].amod - asker), 0, 0);
                                    gl.glColor4f(.5f, .5f, .5f, accs[nac].aalfa * asker * 3.5f);
                                    f_drawquad(gl, accs[nac].arad / 8.0f + asker / 10.0f);
                                    gl.glPopMatrix();
                                    if (.35 * (accs[nac].amod - asker) < 0) f_initacc(nac);
                                }
                                gl.glColor4f(1, 1, 1, 1);
                                f_drawquad(gl, .2f + .1f * (float) Math.sin(f_timer));
                            }
                            gl.glEnable(GL2.GL_DEPTH_TEST);
                            gl.glEnable(GL2.GL_CULL_FACE);
                            gl.glDisable(GL2.GL_BLEND);
                            gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 128);
                        } else {
                            float value = (f_angle[f_x][f_y][f_z] / (360f * 2f));
                            glut.glutSolidCube(/*gl, */value);
                        }
                        gl.glPopMatrix();
                    }

            gl.glFrontFace(GL2.GL_CW);

            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -15);
            gl.glEnable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
            gl.glDisable(GL2.GL_DEPTH_TEST);
            if (f_zeta > 0.0f) f_shadetop = (int) (255 * ((float) Math.sin((f_zeta / 40) * 1.5f * 3.1415f)));
            gl.glRotatef(-100 * (float) Math.sqrt(Math.sqrt(f_zeta * f_zeta)), 0, 0, 1);
            if (((f_zeta > 0) && (f_zeta < .9)) ||
                    ((f_zeta > 2.2) && (f_zeta < 3.5)) ||
                    ((f_zeta > 4.75) && (f_zeta < 6.05)) ||
                    ((f_zeta > 7.6) && (f_zeta < 8.3)) ||
                    ((f_zeta > 10) && (f_zeta < 10.8)) ||
                    ((f_zeta > 12.7) && (f_zeta < 13.0)) ||
                    ((f_zeta > 15.3) && (f_zeta < 15.6)) ||
                    ((f_zeta > 20.3) && (f_zeta < 20.5)) ||
                    ((f_zeta > 22.2) && (f_zeta < 23.5))) {
                if ((f_zeta > 10) && (f_zeta < 10.8)) f_factor = f_factor / 4.5f;
                f_shade -= (int) (20.0f * f_factor);
                if (f_shade < 0) f_shade = 0;
                f_flare -= (int) (30.0f * f_factor);
                if (f_flare < 0) f_flare = 0;
            } else {
                if ((f_zeta > 10.8) && (f_zeta < 12.7)) f_factor = f_factor / 2.0f;
                f_shade += (int) (20.0f * f_factor);
                if (f_shade > f_shadetop) f_shade = f_shadetop;
                if (f_zeta > 0.0f) f_flare += (int) (40.0f * f_factor);
                if (f_flare > 255) f_flare = 255;
            }
            gl.glPushMatrix();
            gl.glRotatef(30, 0, 0, 1);
            gl.glTranslatef(-.2f * f_zeta, 0, 0);
            gl.glColor4ub((byte) 128, (byte) 96, (byte) 64, (byte) f_shade);

            f_Text[5].use(gl);
            f_drawquad(gl, 1.75f);
            gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) f_shade);
            f_drawquad(gl, 1);

            f_Text[4].use(gl);
            gl.glTranslatef(-.1f * f_zeta, 0, 0);
            gl.glColor4ub((byte) 192, (byte) 64, (byte) 64, (byte) f_shade);
            f_drawquad(gl, 2.75f);

            f_Text[5].use(gl);
            gl.glTranslatef(-.15f * f_zeta, 0, 0);
            gl.glColor4ub((byte) 128, (byte) 212, (byte) 64, (byte) f_shade);
            f_drawquad(gl, .5f);

            f_Text[3].use(gl);
            gl.glTranslatef(.35f * f_zeta, 0, 0);
            gl.glColor4ub((byte) 128, (byte) 128, (byte) 128, (byte) (f_shade / 2));
            f_drawquad(gl, .5f);
            gl.glTranslatef(-.025f * f_zeta, 0, 0);
            gl.glColor4ub((byte) 96, (byte) 128, (byte) 192, (byte) (f_shade / 2));
            f_drawquad(gl, 1);
            gl.glPopMatrix();

            gl.glPushMatrix();
            gl.glRotatef(30, 0, 0, 1);
            gl.glTranslatef(.15f * f_zeta, 0, 0);
            gl.glColor4ub((byte) 128, (byte) 128, (byte) 128, (byte) (f_shade / 2));
            f_drawquad(gl, 1.75f);
            gl.glTranslatef(.05f * f_zeta, 0, 0);
            f_drawquad(gl, 1.25f);

            f_Text[5].use(gl);
            gl.glTranslatef(.1f * f_zeta, 0, 0);
            gl.glColor4ub((byte) 64, (byte) 64, (byte) 192, (byte) f_shade);
            f_drawquad(gl, 1);
            gl.glColor4ub((byte) 64, (byte) 128, (byte) 64, (byte) f_shade);
            f_drawquad(gl, .8f);
            gl.glColor4ub((byte) 64, (byte) 64, (byte) 128, (byte) f_shade);
            f_drawquad(gl, .6f);
            gl.glPopMatrix();

            f_Text[6].use(gl);
            gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (((float) f_flare) / 1.5f));
            gl.glPushMatrix();
            gl.glRotatef(3 * f_rot, 0, 0, 1);
            f_drawquad(gl, 5 + 10 * (float) Math.cos(f_rot / 10) * (float) Math.cos(f_rot / 10));
            f_Text[2].use(gl);
            gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) f_flare);
            f_drawquad(gl, 5 + 5 * (float) Math.cos(f_rot / 10) * (float) Math.cos(f_rot / 10));
            gl.glRotatef(f_rot, 0, 0, 1);
            f_Text[2].use(gl);
            f_drawquad(gl, 2 + 5 * (float) Math.sin(f_rot / 10) * (float) Math.sin(f_rot / 10));
            f_Text[5].use(gl);
            gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (f_shade / 2));
            gl.glPopMatrix();
            f_drawquad(gl, 30);

            if ((f_shadetop < 0) && (f_zeta > 0.0f)) {
/*		if (f_play)
		{
			FSOUND_PlaySound(FSOUND_FREE, exps);
			f_play=false;
		}*/

                for (f_cycle = 0; f_cycle < f_num; f_cycle++) {
                    gl.glPushMatrix();
                    gl.glRotatef(particles[f_cycle].mod * 10.0f * (1.0f + 100.0f * particles[f_cycle].f_speed), 0, 0, 1);//(float)f_cycle/f_num),0,0,1);
                    gl.glRotatef(particles[f_cycle].alfa, 0, 0, 1);
                    gl.glTranslatef((float) f_cycle * (1.0f + 2.0f * (f_zeta - 28.5f)) / (float) f_num + particles[f_cycle].mod, 0, 0);
                    gl.glColor4ub((byte) (f_cycle / 2), (byte) (f_cycle / 2), (byte) (f_cycle / 2), (byte) particles[f_cycle].f_shade);
                    if (f_zeta > 28.5f) f_drawquad(gl, .1f + .5f * (float) (particles[f_cycle].f_shade) / 128);
                    gl.glPopMatrix();
                    particles[f_cycle].mod = (f_zeta - 28.5f) * (f_zeta - 28.5f) / 1.2f;
                    f_end += .000005f * f_factor;
                }


            }
            f_rot = 15.0f * f_timer;

            if (f_zeta > 29.5) {
/*		if (f_play1)
		{
			FSOUND_PlaySound(FSOUND_FREE, revs);
			f_play1=false;
		}
*/
                f_density = .025f * (1.0f + (f_zeta - 29.5f) / 10.0f);
                gl.glFogf(GL2.GL_FOG_DENSITY, f_density);
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -1);
                gl.glColor4f(1.0f, 1.0f, 1.0f, (f_zeta - 29.5f) / 3.0f);

                gl.glDisable(GL2.GL_TEXTURE_2D);
                f_drawquad(gl, 1.5f);
                gl.glEnable(GL2.GL_TEXTURE_2D);

            }
        } else {
            gl.glClearColor(1.0f - 2.0f * (f_zeta - 32.5f), 1.0f - 2.0f * (f_zeta - 32.5f), 1.0f - 2.0f * (f_zeta - 32.5f), 1);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        }
        if (f_timer < 1.0f) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glEnable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -.35f);
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - f_timer * 2.0f);
            f_drawquad(gl, .5f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }

        if ((f_timer > 2.1f) && (f_timer < 3.1f)) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glEnable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -.35f);
            gl.glColor4f(1.0f, 1.0f, 1.0f, .45f * (1.0f - (float) Math.cos((f_timer - 2.1f) * 3.1415f * 2.0f)));
            f_drawquad(gl, .5f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }

        if ((f_timer > 12.25f) && (f_timer < 13.25f)) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glEnable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -.35f);
            gl.glColor4f(1.0f, 1.0f, 1.0f, .45f * (1.0f - (float) Math.cos((f_timer - 12.25f) * 3.1415f * 2.0f)));
            f_drawquad(gl, .5f);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }

        if ((f_timer > 22.3f) && (f_timer < 28.3)) {
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();
            if (f_timer < 23.3f)
                glu.gluPerspective(45.0f + 25 * (1.0 - (float) Math.cos((f_timer - 22.3) * 3.1415)), (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 100.0f);
            else
                glu.gluPerspective(45.0f + 25 * (1.0 + (float) Math.cos((f_timer - 23.3) * 3.1415 / 5.0)), (float) (float) drawable.getSurfaceWidth() / (float) drawable.getSurfaceHeight(), 0.1f, 100.0f);
            gl.glMatrixMode(GL2.GL_MODELVIEW);
        }

        gl.glDisable(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glFrontFace(GL2.GL_CCW);

/*	if ((f_timer>12.2)&&(f_play2))
	{
		FSOUND_PlaySound(FSOUND_FREE, pby);
		f_play2=false;
	}
*/
        if (f_timer < 1.4f)
            f_zeta = -15.0f + 15.0f * ((float) Math.sin(f_timer * 3.1415f / 2.8f));//(float)Math.cos(3.1415f+f_timer*3.1415f/2.0f));
        else
            f_zeta = 0.0f + (f_timer - 1.4f) / 1.24f;
        f_timer = (f_time) / 1500.0f;
        if (f_zeta > 33.0f) {
            //************************** FINISH
            //f_Clean();
            return false;
        }
        return true;
    }
}
