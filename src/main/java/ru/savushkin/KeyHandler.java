package ru.savushkin;

import ru.savushkin.common.Vector3d;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyHandler extends KeyAdapter {

    private Renderer renderer;

    private Application thePanel;

    private boolean up = false;

    private boolean down = false;

    private boolean left = false;

    private boolean right = false;

    private boolean pageup = false;

    private boolean pagedown = false;

    private boolean rollleft = false;

    private boolean rollright = false;

    private boolean ctrldown = false;

    public KeyHandler( Renderer renderer, Application thePanel ) {
        this.thePanel = thePanel;
        this.renderer = renderer;
    }

    public void keyTyped( KeyEvent e ) {
        // Nothing
    }

    public void keyPressed( KeyEvent e ) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_CONTROL:
                ctrldown = true;
                break;

            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_COMMA:
            case KeyEvent.VK_PERIOD:

                if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
                    up = true;
                }

                if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
                    down = true;
                }

                if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
                    left = true;
                }

                if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
                    right = true;
                }

                if (keyCode == KeyEvent.VK_PAGE_UP) {
                    pageup = true;
                }

                if (keyCode == KeyEvent.VK_PAGE_DOWN) {
                    pagedown = true;
                }

                if (keyCode == KeyEvent.VK_COMMA) {
                    rollleft = true;
                }

                if (keyCode == KeyEvent.VK_PERIOD) {
                    rollright = true;
                }

                e.consume();
                cameraUpdate();
                break;

            default:
                processKeyEvent(e);
        }
    }

    public void keyReleased( KeyEvent e ) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_CONTROL:
                ctrldown = false;
                break;

            // Process the FPS control actions for camera movement
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_COMMA:
            case KeyEvent.VK_PERIOD:

                if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
                    up = false;
                } else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
                    down = false;
                } else if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
                    left = false;
                } else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
                    right = false;
                } else if (keyCode == KeyEvent.VK_PAGE_UP) {
                    pageup = false;
                } else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
                    pagedown = false;
                } else if (keyCode == KeyEvent.VK_COMMA) {
                    rollleft = false;
                } else if (keyCode == KeyEvent.VK_PERIOD) {
                    rollright = false;
                }

                e.consume();

                cameraUpdate();
                break;

            default:
        }
    }

    private void processKeyEvent( KeyEvent e ) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_Z:
                if (renderer.rotateWheel < 45f)
                    renderer.rotateWheel += 0.9f;
                break;
            case KeyEvent.VK_X:
                if (renderer.rotateWheel > -45f)
                    renderer.rotateWheel -= 0.9f;
                break;


            // Toggle the control modes between strict FPS and Fly Through
            case KeyEvent.VK_F:
                renderer.camera.setEmulateFPS(!renderer.camera.getEmulateFPS());
                break;


            // Toggle orbit control mode
            case KeyEvent.VK_O:
                renderer.camera.setOrbit(!renderer.camera.getOrbit());
                break;

            case KeyEvent.VK_U:
                Vector3d pos1 = renderer.camera.getOrbitPointCopy();
                pos1.x -= 0.25;
                renderer.camera.setOrbitPoint(pos1);
                break;
            case KeyEvent.VK_I:
                Vector3d pos2 = renderer.camera.getOrbitPointCopy();
                pos2.x += 0.25;
                renderer.camera.setOrbitPoint(pos2);
                break;

            case KeyEvent.VK_J:
                Vector3d pos3 = renderer.camera.getOrbitPointCopy();
                pos3.y -= 0.25;
                renderer.camera.setOrbitPoint(pos3);
                break;
            case KeyEvent.VK_K:
                Vector3d pos4 = renderer.camera.getOrbitPointCopy();
                pos4.y += 0.25;
                renderer.camera.setOrbitPoint(pos4);
                break;

            case KeyEvent.VK_N:
                Vector3d pos5 = renderer.camera.getOrbitPointCopy();
                pos5.z -= 0.25;
                renderer.camera.setOrbitPoint(pos5);
                break;
            case KeyEvent.VK_M:
                Vector3d pos6 = renderer.camera.getOrbitPointCopy();
                pos6.z += 0.25;
                renderer.camera.setOrbitPoint(pos6);
                break;


            // Position the camera along -Y axis, looking at XZ plane with -Z up
            case KeyEvent.VK_1:
                renderer.camera.setEye(new Vector3d(0, -2, 0));
                renderer.camera.lookAt(new Vector3d(0, 0, 0), new Vector3d(0, 0, -1));
                break;

            // Position the camera along -X axis, looking at YZ plane with -Z up
            case KeyEvent.VK_2:
                renderer.camera.setEye(new Vector3d(-2, 0, 0));
                renderer.camera.lookAt(new Vector3d(0, 0, 0), new Vector3d(0, 0, -1));
                break;

            // Position the camera along -Z axis, looking at XZ plane with X up
            case KeyEvent.VK_3:
                renderer.camera.setEye(new Vector3d(0, 0, -2));
                renderer.camera.lookAt(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0));
                break;

            case KeyEvent.VK_6:
                renderer.camera.setOrbit(true);
                renderer.camera.setOrbitPoint(new Vector3d(50, 0, 0));
                renderer.camera.setOrbitDistance(2.0);
                break;
            case KeyEvent.VK_7:
                renderer.camera.setOrbit(true);
                renderer.camera.setOrbitPoint(new Vector3d(0, 50, 0));
                renderer.camera.setOrbitDistance(2.0);
                break;
            case KeyEvent.VK_8:
                renderer.camera.setOrbit(true);
                renderer.camera.setOrbitPoint(new Vector3d(0, 0, 50));
                renderer.camera.setOrbitDistance(2.0);
                break;
            case KeyEvent.VK_9:
                renderer.camera.setOrbit(true);
                renderer.camera.setOrbitPoint(new Vector3d(50, 50, 50));
                renderer.camera.setOrbitDistance(2.0);
                break;
            case KeyEvent.VK_0:
                renderer.camera.setOrbit(true);
                renderer.camera.setOrbitPoint(new Vector3d(0, 0, 0));
                renderer.camera.setOrbitDistance(2.0);
                break;
            default:
        }

        // Update the scene - redraw the display following a keyboard event, when
        // not using an animator, else the animator automatically performs updates.
        if (!thePanel.useAnimator) {
            thePanel.drawabledisplay();
        }
    }

    private void cameraUpdate() {
        if (up) {
            if (ctrldown) {
                renderer.camera.rotateAroundRightVector(-1.0);
            } else {
                renderer.camera.stepFwd();
            }
        }

        if (down) {
            if (ctrldown) {
                renderer.camera.rotateAroundRightVector(1.0);
            } else {
                renderer.camera.stepBack();
            }
        }

        if (left) {
            if (ctrldown) {
                renderer.camera.rotateAroundUpVector(1.0);
            } else {
                renderer.camera.stepLeft();
            }
        }

        if (right) {
            if (ctrldown) {
                renderer.camera.rotateAroundUpVector(-1.0);
            } else {
                renderer.camera.stepRight();
            }
        }

        if (pageup) {
            renderer.camera.stepUp();
        }

        if (pagedown) {
            renderer.camera.stepDown();
        }

        if (rollleft) {
            renderer.camera.rotateAroundLookVector(-1.0);
        }

        if (rollright) {
            renderer.camera.rotateAroundLookVector(1.0);
        }

        // Update the scene - redraw the display following a keyboard event, when
        // not using an animator, else the animator automatically performs updates.
        if (!thePanel.useAnimator) {
            thePanel.drawabledisplay();
        }
    }

}