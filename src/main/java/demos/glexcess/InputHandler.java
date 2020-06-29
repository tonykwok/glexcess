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

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
final class InputHandler extends KeyAdapter {
    private final Renderer renderer;

    public InputHandler(Renderer renderer, GLDisplay display) {
        this.renderer = renderer;
        display.registerKeyStrokeForHelp(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "Next scene");
        display.registerKeyStrokeForHelp(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "Speed up");
        display.registerKeyStrokeForHelp(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), "Slow down");
    }

    public final void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                renderer.nextScene();
                break;

            case KeyEvent.VK_A:
                renderer.increaseStep();
                break;

            case KeyEvent.VK_Z:
                renderer.decreaseStep();
                break;
        }
    }
}
