package ru.savushkin;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import ru.savushkin.loader.GLModel;
import ru.savushkin.loader.ModelLoaderOBJ;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class JavaRenderer implements GLEventListener, KeyListener {
    private static final GLU glu = new GLU();
    private GL2 gl;
    private float rotateLeftRight = 0.0f, rotateUpDown = 0.0f, scale = 0.0125f;
    private boolean rotate = false;
    private String objFileName, mtlFileName;

    private GLModel car = null;

    public void display(GLAutoDrawable gLDrawable) {
        this.gl.glLoadIdentity();
        this.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        this.gl.glTranslatef(0, 0, -50);
        this.gl.glScalef(scale, scale, scale);
        this.gl.glRotatef(rotateUpDown, 1.0f, 0.0f, 0.0f);
//        this.gl.glRotatef(rotateLeftRight, 0.0f, 0.0f, 1.0f);
        this.gl.glRotatef(rotateLeftRight, 0.0f, 1.0f, 0.0f);

//        glu.gluLookAt(400, 400, 400, 0, 0, 0, 0, 100, 0);

        car.opengldraw(this.gl);

        this.gl.glFlush();
        if (rotate) {
            rotateUpDown += 0.5;
            rotateLeftRight += 0.5;
        }
    }

    public void init(GLAutoDrawable gLDrawable) {
        System.out.println("init");
        this.gl = gLDrawable.getGL().getGL2();
        this.gl.glEnable(GL2.GL_DEPTH_TEST);
        this.gl.glShadeModel(GL2.GL_SMOOTH);
        this.gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        this.gl.glEnable(GL2.GL_CULL_FACE);
        this.gl.glEnable(GL2.GL_NORMALIZE);
        this.gl.glMatrixMode(GL2.GL_PROJECTION);
        this.gl.glLoadIdentity();
        this.gl.glMatrixMode(GL2.GL_MODELVIEW);
        this.gl.glLoadIdentity();
        if (false == loadModels(gl)) {
            System.exit(1);
        }

        setLight(gl);
//        glu.gluPerspective(1, (double) getWidth() / getHeight(), 0.3, 50);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
        System.out.println("reshape");
        GL2 gl = gLDrawable.getGL().getGL2();
        if(height <= 0) {
            height = 1;
        }
        float h = (float)width / (float)height;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(90, h, 0.1, 100);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
//        gl.glLoadIdentity();
//        glu.gluLookAt(300, 300, 300, 0, 0, 0, 0, 100, 0);
    }

    public void dispose(GLAutoDrawable arg0) {

    }

    private Boolean loadModels(GL2 gl) {
        car = ModelLoaderOBJ.LoadModel(objFileName, mtlFileName, gl);
        if (car == null) {
            return false;
        }
        return true;
    }

    private void setLight(GL2 gl) {
        gl.glEnable(GL2.GL_LIGHTING);
        float SHINE_ALL_DIRECTIONS = 1;
        float[] lightPos = { -30, 30, 30, SHINE_ALL_DIRECTIONS };
        float[] lightColorAmbient = { 0.02f, 0.02f, 0.02f, 1f };
        float[] lightColorSpecular = { 0.9f, 0.9f, 0.9f, 1f };
        // Set light parameters.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightColorAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightColorSpecular, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightColorSpecular, 0);
        gl.glEnable(GL2.GL_LIGHT0);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_UP:
                if (!rotate)
                    rotateUpDown -= 1.5;
                break;
            case KeyEvent.VK_DOWN:
                if (!rotate)
                    rotateUpDown += 1.5;
                break;
            case KeyEvent.VK_LEFT:
                if (!rotate)
                    rotateLeftRight -= 1.5;
                break;
            case KeyEvent.VK_RIGHT:
                if (!rotate)
                    rotateLeftRight += 1.5;
                break;
            case KeyEvent.VK_R:
                rotate = !rotate;
                break;
            case KeyEvent.VK_0:
                if(scale > 0)
                scale -= 0.001;
                break;
            case KeyEvent.VK_9:
                scale += 0.001;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}