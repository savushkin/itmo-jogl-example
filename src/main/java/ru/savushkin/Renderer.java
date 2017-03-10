package ru.savushkin;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import ru.savushkin.camera.Camera;
import ru.savushkin.common.Vector3d;
import ru.savushkin.loader.GLModel;
import ru.savushkin.loader.ModelLoaderOBJ;

import java.awt.*;


public class Renderer implements GLEventListener {

    private GLU glu = new GLU();

    private float lightAmbient[] = {0.08f, 0.08f, 0.08f, 0.0f};

    private float light0Diffuse[] = {1.0f, 1.0f, 1.0f, 0.0f};
    private float light1Diffuse[] = {0.0f, 1.0f, 0.0f, 0.0f};
    private float light2Diffuse[] = {0.0f, 0.0f, 1.0f, 0.0f};

    private float lightSpecular[] = {0.4f, 0.4f, 0.4f, 1.0f};

    private float lightAmbientDiffuse[] = {0.65f, 0.65f, 0.65f, 0.0f};

    private float light0Position[] = {100.0f, 100.0f, 0.0f, 1.0f};
    private float light1Position[] = {100.0f, -100.0f, 0.0f, 1.0f};
    private float light2Position[] = {-100.0f, -100.0f, 0.0f, 1.0f};
    private float light3Position[] = {-100.0f, 100.0f, 0.0f, 1.0f};

    public double nearPlane = 0.001;

    public double farPlane = 2000000.0;

    private int _width;

    private int _height;

    private GLModel car = null, wheel1 = null, wheel3 = null;

    public boolean reset = false;

    public Point lastMousePoint = new Point();

    public Point currentMousePoint = new Point();

    public Camera camera = new Camera();

    public float rotateWheel = 0.0f, moveForward = 0.0f;

    public Renderer(int width, int height) {
        this._width = width;
        this._height = height;

        camera.setEye(new Vector3d(0, 0, -2));
        camera.lookAt(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0));
    }

    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();

        gl.setSwapInterval(1);

        //
        // View Settings
        //
        gl.glViewport(0, 0, 320, 240);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the Field-of-View, Aspect Ratio, Near & Far clipping planes
        glu.gluPerspective(65.0, 320.0 / 240.0, nearPlane, farPlane);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        //
        // Lighting
        //
        // Enable Smooth Shading
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, lightSpecular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, lightAmbientDiffuse, 0);
        // Background - black
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // Depth Buffer Setup
        gl.glClearDepth(1.0f);
        // Clear The Stencil Buffer To 0
        gl.glClearStencil(0);
        // Enables Depth Testing
        gl.glEnable(GL2.GL_DEPTH_TEST);
        // Really Nice Perspective Calculations
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0Diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0Position, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightSpecular, 0);
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_CONSTANT_ATTENUATION, 1.0f);
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_LINEAR_ATTENUATION, 0.0f);
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_QUADRATIC_ATTENUATION, 0.0f);

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, light1Diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, light1Position, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightSpecular, 0);
        gl.glLightf(GL2.GL_LIGHT1, GL2.GL_CONSTANT_ATTENUATION, 1.0f);
        gl.glLightf(GL2.GL_LIGHT1, GL2.GL_LINEAR_ATTENUATION, 0.0f);
        gl.glLightf(GL2.GL_LIGHT1, GL2.GL_QUADRATIC_ATTENUATION, 0.0f);

        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, light2Diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, light2Position, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, lightSpecular, 0);
        gl.glLightf(GL2.GL_LIGHT2, GL2.GL_CONSTANT_ATTENUATION, 1.0f);
        gl.glLightf(GL2.GL_LIGHT2, GL2.GL_LINEAR_ATTENUATION, 0.0f);
        gl.glLightf(GL2.GL_LIGHT2, GL2.GL_QUADRATIC_ATTENUATION, 0.0f);

        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_DIFFUSE, light0Diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, light3Position, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_SPECULAR, lightSpecular, 0);
        gl.glLightf(GL2.GL_LIGHT3, GL2.GL_CONSTANT_ATTENUATION, 1.0f);
        gl.glLightf(GL2.GL_LIGHT3, GL2.GL_LINEAR_ATTENUATION, 0.0f);
        gl.glLightf(GL2.GL_LIGHT3, GL2.GL_QUADRATIC_ATTENUATION, 0.0f);

        gl.glLightModelfv(gl.GL_LIGHT_MODEL_AMBIENT, lightAmbient, 0);
        // Enable Lighting
        gl.glEnable(GL2.GL_LIGHTING);
        // Enable Light 0
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHT1);
        gl.glEnable(GL2.GL_LIGHT2);
        gl.glEnable(GL2.GL_LIGHT3);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glLightModelf(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);

        gl.glLoadIdentity();
        car = ModelLoaderOBJ.LoadModel("./model/e30/car.obj", "./model/e30/car.mtl", gl);
        wheel1 = ModelLoaderOBJ.LoadModel("./model/e30/wheel1.obj", "./model/e30/wheel1.mtl", gl);
        wheel3 = ModelLoaderOBJ.LoadModel("./model/e30/wheel3.obj", "./model/e30/wheel3.mtl", gl);
    }

    public void dispose(GLAutoDrawable drawable) {

    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        if (reset) {
            int width = _width;
            int height = _height;
            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();
            // Set the Field-of-View, Aspect Ratio, Near & Far clipping planes
            glu.gluPerspective(65.0, (double) width / (double) height, nearPlane, farPlane);
            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();
            reset = false;
        }

        // Clear Screen And Depth Buffer
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        updateView(gl);

        // Update light position
        // 0 in the last component causes jagged terminator on planets, 1 results
        // in a smooth terminator but the location of the sunlight is no longer
        // correct...not sure why.  Old code used a 0 (zero) very successfully,
        // what changed?!
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0Position, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, light1Position, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, light2Position, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, light3Position, 0);

        GLUT glut = new GLUT();

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        // Draw car with wheels
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
        gl.glScaled(0.01, 0.01, 0.01);
        car.opengldraw(gl);
        gl.glPushMatrix();
        gl.glTranslated(900, -500, 1875);
        gl.glRotatef(rotateWheel, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(moveForward, 1.0f, 0.0f, 0.0f);
        wheel1.opengldraw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslated(-900, -500, 1875);
        gl.glRotatef(rotateWheel, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(moveForward, 1.0f, 0.0f, 0.0f);
        wheel3.opengldraw(gl);
        gl.glPopMatrix();

        // Only display the rotation point when in 'orbit' mode.
        if (camera.getOrbit()) {
            gl.glPushMatrix();
            gl.glColor3f(1f, 0f, 1f);
            gl.glTranslated(camera.getOrbitPointCopy().x, camera.getOrbitPointCopy().y, camera.getOrbitPointCopy().z);
            glut.glutWireSphere(0.1, 16, 16);
            gl.glColor3f(1f, 1f, 1f);
            gl.glPopMatrix();
        }

        gl.glFlush();
        moveForward += 1.25f;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        // Prevent division by 0 in aspect ratio
        if (height <= 0) {
            height = 1;
        }

        this._height = height;
        this._width = width;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        // Set the Field-of-View, Aspect Ratio, Near & Far clipping planes
        glu.gluPerspective(65.0, (double) width / (double) height, nearPlane, farPlane);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private void updateView(GL2 gl) {
        gl.glMultMatrixd(camera.getView(), 0);
    }
}