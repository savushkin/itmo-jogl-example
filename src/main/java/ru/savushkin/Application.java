package ru.savushkin;

import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import ru.savushkin.common.Vector3d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application extends JPanel {

    private GLJPanel canvas;

    private Renderer renderer;

    private static final int DEFAULT_WIDTH = 640;

    private static final int DEFAULT_HEIGHT = 480;

    private int width;

    private int height;

    private FPSAnimator animator;

    public static final boolean ANIMATOR_OFF = false;
    public static final boolean ANIMATOR_ON = true;

    protected boolean useAnimator = ANIMATOR_ON;


    public Application() {
        this(true);
    }

    public Application(boolean useAnimator ) {
        this.useAnimator = useAnimator;

        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        renderer = new Renderer(width, height);

        MouseHandler inputMouseHandler = new MouseHandler(renderer, this);
        addMouseListener(inputMouseHandler);
        addMouseMotionListener(inputMouseHandler);

        KeyHandler inputKeyHandler = new KeyHandler(renderer, this);
        setFocusable(true);
        addKeyListener(inputKeyHandler);

        setLayout(new BorderLayout());

        canvas = new GLJPanel() {
            public void paintComponent( Graphics g ) {
                super.paintComponent(g);
            }
        };
        canvas.setOpaque(false);

        canvas.addGLEventListener(renderer);

        canvas.setIgnoreRepaint(true);

        add(canvas);
        setPreferredSize(new Dimension(width, height));

        if (useAnimator) {
            animator = new FPSAnimator(canvas, 30, true);
            start();
        }
    }

    public void start() {
        try {
            canvas.requestFocus();
            animator.start();
        } catch (Exception e) {
            System.out.println("Caught Exception: " + e);
        }
    }

    public void stop() {
        try {
            animator.stop();
        } catch (Exception e) {
            System.out.println("Caught Exception: " + e);
        } finally {
            System.exit(0);
        }
    }

    public void drawabledisplay() {
        try {
            renderer.camera.apply();
            canvas.display();
        } catch (Exception e) {
            System.out.println("drawabledisplay(): Exception caught: " + e);
        }
    }

    public static void main( String[] args ) {
        // Create an instance of the Prox3D class
        Application proxView = new Application( Application.ANIMATOR_ON );

        // Create a JFrame to hold the JPanel for test display purposes
        JFrame mFrame = new JFrame("BMW e30");

        // Set a layout for the frame
        mFrame.getContentPane().setLayout(new BoxLayout(mFrame.getContentPane(), BoxLayout.Y_AXIS));

        // Add the Prox3D instance to the frame
        mFrame.getContentPane().add(proxView);

        // Add a listener to determine what to do in event of a main window
        // closing
        mFrame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Activate the frame and make the map visible
        mFrame.pack();

        // Display the frame
        mFrame.setVisible(true);
    }

    private static Timer testTimer;

    private static class TestAnimator implements ActionListener {
        private Application thePanel = null;
        private Renderer renderer = null;
        private int count = 0;

        private TestAnimator(Application thePanel, Renderer renderer ) {
            this.thePanel = thePanel;
            this.renderer = renderer;
        }

        public void actionPerformed(ActionEvent e) {
            if (thePanel != null) {

                double angle = count*2.0*Math.PI/180.0;
                double c = 2.0*Math.cos(angle);
                double s = 2.0*Math.sin(angle);
                //renderer.camera.setEye(new ru.savushkin.common.Vector3d(0, -1-(count*0.01), 0));
                renderer.camera.setEye(new Vector3d(s, c, 0));
                renderer.camera.lookAt(new Vector3d(0, 0, 0), new Vector3d(0, 0, -1));

                if (!thePanel.useAnimator) {
                    thePanel.drawabledisplay();
                }


                // Update the counter
                count++;
                if (count > 180) {
                    testTimer.stop();
                }
            }
        }
    }

}
