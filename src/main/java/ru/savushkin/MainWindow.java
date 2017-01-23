package ru.savushkin;

import com.jogamp.opengl.awt.GLCanvas;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow implements Runnable {
    private static Thread displayT = new Thread(new MainWindow());
    private static boolean bQuit = false;

    public static void main(String[] args) {
        displayT.start();
    }

    public void run() {
        Frame frame = new Frame("BMV E30");
        GLCanvas canvas = new GLCanvas();
        int size = frame.getExtendedState();

        JavaRenderer javaRenderer = new JavaRenderer();
        canvas.addGLEventListener(javaRenderer);
        canvas.addKeyListener(javaRenderer);
        frame.add(canvas);
        frame.setUndecorated(true);
        size |= Frame.MAXIMIZED_BOTH;
        frame.setExtendedState(size);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                bQuit = true;
                System.exit(0);
            }
        });

        frame.setVisible(true);
        canvas.requestFocus();
        while( !bQuit ) {
            canvas.display();
        }
    }
}