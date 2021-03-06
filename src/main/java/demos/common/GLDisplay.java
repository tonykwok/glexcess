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
package demos.common;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * @author Paolo "Bustard" Martella (C++ version)
 * @author Pepijn Van Eeckhoudt (Ported to JOGL 1.0)
 * @author Tony Guo (Adapted for JOGL 2.0)
 */
public class GLDisplay {
    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;

    private static final int DONT_CARE = -1;

    private JFrame frame;
    private GLCanvas glCanvas;
    private FPSAnimator animator;
    private boolean fullscreen;
    private int width;
    private int height;
    private GraphicsDevice usedDevice;

    private MyHelpOverlayGLEventListener helpOverlayGLEventListener = new MyHelpOverlayGLEventListener();
    private MyExceptionHandler exceptionHandler = new MyExceptionHandler();

    public static GLDisplay createGLDisplay(String title) {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        boolean fullscreen = false;
        if (device.isFullScreenSupported()) {
            int selectedOption = JOptionPane.showOptionDialog(
                    null,
                    "How would you like to run this demo?",
                    title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[]{"Fullscreen", "Windowed"},
                    "Windowed");
            fullscreen = selectedOption == 0;
        }
        return new GLDisplay(title, DEFAULT_WIDTH, DEFAULT_HEIGHT, fullscreen);
    }

    private GLDisplay(String title, int width, int height, boolean fullscreen) {

        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(profile);
        glCanvas = new GLCanvas(caps);
        glCanvas.setSize(width, height);
        glCanvas.setIgnoreRepaint(true);
        glCanvas.addGLEventListener(helpOverlayGLEventListener);

        frame = new JFrame(title);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(glCanvas, BorderLayout.CENTER);

        addKeyListener(new MyKeyAdapter());

        this.fullscreen = fullscreen;
        this.width = width;
        this.height = height;
        animator = new FPSAnimator(glCanvas, 60, true);
    }

    public void start() {
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setUndecorated(fullscreen);
            frame.pack();
            frame.setLocation(
                    (screenSize.width - frame.getWidth()) / 2,
                    (screenSize.height - frame.getHeight()) / 2
            );
            frame.addWindowListener(new MyWindowAdapter());

            if (fullscreen) {
                usedDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                usedDevice.setFullScreenWindow(frame);
                usedDevice.setDisplayMode(
                        findDisplayMode(
                                usedDevice.getDisplayModes(),
                                width, height,
                                usedDevice.getDisplayMode().getBitDepth(),
                                usedDevice.getDisplayMode().getRefreshRate()
                        )
                );
            } else {
                frame.setVisible(true);
            }

            glCanvas.requestFocus();

            animator.start();
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }

    public void stop() {
        try {
            animator.stop();
            if (fullscreen) {
                usedDevice.setFullScreenWindow(null);
                usedDevice = null;
            }
            frame.dispose();
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        } finally {
            System.exit(0);
        }
    }

    private DisplayMode findDisplayMode(DisplayMode[] displayModes, int requestedWidth, int requestedHeight, int requestedDepth, int requestedRefreshRate) {
        // Try to find an exact match
        DisplayMode displayMode = findDisplayModeInternal(displayModes, requestedWidth, requestedHeight, requestedDepth, requestedRefreshRate);

        // Try again, ignoring the requested bit depth
        if (displayMode == null)
            displayMode = findDisplayModeInternal(displayModes, requestedWidth, requestedHeight, DONT_CARE, DONT_CARE);

        // Try again, and again ignoring the requested bit depth and height
        if (displayMode == null)
            displayMode = findDisplayModeInternal(displayModes, requestedWidth, DONT_CARE, DONT_CARE, DONT_CARE);

        // If all else fails try to get any display mode
        if (displayMode == null)
            displayMode = findDisplayModeInternal(displayModes, DONT_CARE, DONT_CARE, DONT_CARE, DONT_CARE);

        return displayMode;
    }

    private DisplayMode findDisplayModeInternal(DisplayMode[] displayModes, int requestedWidth, int requestedHeight, int requestedDepth, int requestedRefreshRate) {
        DisplayMode displayModeToUse = null;
        for (int i = 0; i < displayModes.length; i++) {
            DisplayMode displayMode = displayModes[i];
            if ((requestedWidth == DONT_CARE || displayMode.getWidth() == requestedWidth) &&
                    (requestedHeight == DONT_CARE || displayMode.getHeight() == requestedHeight) &&
                    (requestedHeight == DONT_CARE || displayMode.getRefreshRate() == requestedRefreshRate) &&
                    (requestedDepth == DONT_CARE || displayMode.getBitDepth() == requestedDepth))
                displayModeToUse = displayMode;
        }

        return displayModeToUse;
    }

    public void addGLEventListener(GLEventListener glEventListener) {
        this.helpOverlayGLEventListener.addGLEventListener(glEventListener);
    }

    public void removeGLEventListener(GLEventListener glEventListener) {
        this.helpOverlayGLEventListener.removeGLEventListener(glEventListener);
    }

    public void addKeyListener(KeyListener l) {
        glCanvas.addKeyListener(l);
    }

    public void addMouseListener(MouseListener l) {
        glCanvas.addMouseListener(l);
    }

    public void addMouseMotionListener(MouseMotionListener l) {
        glCanvas.addMouseMotionListener(l);
    }

    public void removeKeyListener(KeyListener l) {
        glCanvas.removeKeyListener(l);
    }

    public void removeMouseListener(MouseListener l) {
        glCanvas.removeMouseListener(l);
    }

    public void removeMouseMotionListener(MouseMotionListener l) {
        glCanvas.removeMouseMotionListener(l);
    }

    public void registerKeyStrokeForHelp(KeyStroke keyStroke, String description) {
        helpOverlayGLEventListener.registerKeyStroke(keyStroke, description);
    }

    public void registerMouseEventForHelp(int id, int modifiers, String description) {
        helpOverlayGLEventListener.registerMouseEvent(id, modifiers, description);
    }

    public String getTitle() {
        return frame.getTitle();
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }

    private class MyKeyAdapter extends KeyAdapter {
        public MyKeyAdapter() {
            registerKeyStrokeForHelp(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "Show/hide help message");
            registerKeyStrokeForHelp(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Quit demo");
            registerKeyStrokeForHelp(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "Toggle frame rate limit");
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    stop();
                    break;
                case KeyEvent.VK_C:
                    if (animator.isPaused()) {
                        animator.resume();
                    } else if (animator.isAnimating()) {
                        animator.pause();
                    }
                    break;
                case KeyEvent.VK_F1:
                    helpOverlayGLEventListener.toggleHelp();
                    break;
            }
        }
    }

    private class MyWindowAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            stop();
        }
    }

    private class MyExceptionHandler implements ExceptionHandler {
        public void handleException(final Exception e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    e.printStackTrace(printWriter);
                    JOptionPane.showMessageDialog(frame, stringWriter.toString(), "Exception occurred", JOptionPane.ERROR_MESSAGE);
                    stop();
                }
            });
        }
    }

    private static class MyHelpOverlayGLEventListener implements GLEventListener {
        private java.util.List eventListeners = new ArrayList();
        private HelpOverlay helpOverlay = new HelpOverlay();
        private boolean showHelp = false;

        public void toggleHelp() {
            showHelp = !showHelp;
        }

        public void registerKeyStroke(KeyStroke keyStroke, String description) {
            helpOverlay.registerKeyStroke(keyStroke, description);
        }

        public void registerMouseEvent(int id, int modifiers, String description) {
            helpOverlay.registerMouseEvent(id, modifiers, description);
        }

        public void addGLEventListener(GLEventListener glEventListener) {
            eventListeners.add(glEventListener);
        }

        public void removeGLEventListener(GLEventListener glEventListener) {
            eventListeners.remove(glEventListener);
        }

        public void display(GLAutoDrawable GLAutoDrawable) {
            for (int i = 0; i < eventListeners.size(); i++) {
                ((GLEventListener) eventListeners.get(i)).display(GLAutoDrawable);
            }
            if (showHelp)
                helpOverlay.display(GLAutoDrawable);
        }

//        public void dispose(GLAutoDrawable GLAutoDrawable, boolean b, boolean b1) {
//            for (int i = 0; i < eventListeners.size(); i++) {
//                ((GLEventListener) eventListeners.get(i)).displayChanged(GLAutoDrawable, b, b1);
//            }
//        }

        public void init(GLAutoDrawable drawable) {
            for (int i = 0; i < eventListeners.size(); i++) {
                ((GLEventListener) eventListeners.get(i)).init(drawable);
            }
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {

        }

        public void reshape(GLAutoDrawable drawable, int i0, int i1, int i2, int i3) {
            for (int i = 0; i < eventListeners.size(); i++) {
                ((GLEventListener) eventListeners.get(i)).reshape(drawable, i0, i1, i2, i3);
            }
        }
    }
}
