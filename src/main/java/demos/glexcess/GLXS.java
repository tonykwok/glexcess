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

import demos.common.GLDisplay;

/**
 * @author Paolo "Bustard" Martella
 * @author Pepijn Van Eeckhoudt
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
public final class GLXS {
    public static void main(String[] args) {
        GLDisplay glDisplay = GLDisplay.createGLDisplay("GLExcess");
        Renderer renderer = new Renderer();
        InputHandler inputHandler = new InputHandler(renderer, glDisplay);
        glDisplay.addGLEventListener(renderer);
        glDisplay.addKeyListener(inputHandler);
        glDisplay.start();
    }
}
