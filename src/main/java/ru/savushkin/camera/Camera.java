package ru.savushkin.camera;

import ru.savushkin.common.Quat4f;
import ru.savushkin.common.Vector3d;

public class Camera {

    private Vector3d look  = new Vector3d(-0.05, -0.05, -0.05);

    private Vector3d eye   = new Vector3d(1, 1, 1);

    private Vector3d up    = new Vector3d(0, 1, 0);

    private Vector3d right = new Vector3d(1, 0, 0);

    private double view[] = { 1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0 };

    private double axes[] = { 1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0 };

    private double moveSpeed = 0.25;

    private boolean needUpdate = false;

    private boolean emulateFPS = false;

    private boolean orbit = false;

    private Vector3d orbitPoint = new Vector3d(0, 0, 0);

    private double orbitDistance = 1.0;

    private Vector3d orbitEye = new Vector3d();


    public Camera() {
        needUpdate = true;
        apply();
    }

    public Camera( Vector3d lookVector, Vector3d eyePoint, Vector3d upVector ) {
        setLook(lookVector);
        setEye(eyePoint);
        setUp(upVector);

        apply();
    }

    public void setLook( Vector3d vec ) {
        // Make sure the look vector is not null
        if (vec == null) {
            throw new RuntimeException("Look vector cannot be null.");
        }

        // Make sure the look vector has some length
        if (Math.abs(vec.length()) < 1.0e-6){
            throw new RuntimeException("Look vector cannot have zero length.");
        }

        look.x = vec.x;
        look.y = vec.y;
        look.z = vec.z;

        // Normalize the vector
        look.normalize();

        needUpdate = true;
    }

    public void setEye( Vector3d pt ) {
        // Make sure eye point is not null
        if (pt == null) {
            throw new RuntimeException("Eye point cannot be null.");
        }

        eye.x = pt.x;
        eye.y = pt.y;
        eye.z = pt.z;

        needUpdate = true;
    }

    public void setUp( Vector3d vec ) {
        // Make sure the up vector is not null
        if (vec == null) {
            throw new RuntimeException("Up vector cannot be null.");
        }

        // Make sure the up vector has some length
        if (Math.abs(vec.length()) < 1.0e-6){
            throw new RuntimeException("Up vector cannot have zero length.");
        }

        up.x = vec.x;
        up.y = vec.y;
        up.z = vec.z;

        // Normalize the vector
        up.normalize();

        needUpdate = true;
    }

    public Vector3d getLookCopy() {
        return new Vector3d(look);
    }

    public Vector3d getLook() {
        return look;
    }

    public Vector3d getEyeCopy() {
        return new Vector3d(eye);
    }

    public Vector3d getEye() {
        return eye;
    }

    public Vector3d getUpCopy() {
        return new Vector3d(up);
    }

    public Vector3d getUp() {
        return up;
    }

    public Vector3d getRightCopy() {
        return new Vector3d(right);
    }

    public void lookAt( Vector3d lookPoint, Vector3d upVector ) {
        // Make sure the look point and up vectors are not null
        if (lookPoint == null) {
            throw new RuntimeException("Look point cannot be null.");
        }

        if (upVector == null) {
            throw new RuntimeException("Up vector cannot be null.");
        }

        // Make sure the up vector has some length
        if (Math.abs(upVector.length()) < 1.0e-6){
            throw new RuntimeException("Up vector cannot have zero length.");
        }

        // Calculate the look vector/direction by vector subtraction of the
        // the look at point and the current eye point
        Vector3d lookVector = new Vector3d();
        lookVector.sub(lookPoint, eye);

        System.out.println("---- lookAt() ------------------------------------");
        System.out.println("Look Vector = look point - eye point "
                + "\n   look point: " + lookPoint
                + "\n    eye point: " + eye
                + "\n  look vector: " + lookVector);

        // Ensure the new look vector is not zero length, and if so
        // Make sure the new look vector has some length
        if (Math.abs(lookVector.length()) < 1.0e-6){
            // Force the vector to some default direction and display a warning
            System.err.println("ru.savushkin.camera.Camera look vector cannot have zero length.  "
                    + "This is likely caused by coincidant 'look at' "
                    + "and 'eye' point locations.  Forcing look vector "
                    + "to <1,0,0>");
            lookVector.x = 1.0;  lookVector.y = 0.0;  lookVector.z = 0.0;
        }

        // Update the look vector with the new look vector (nominally, the vector
        // would be normalized, but this occurs inside the code already.
        setLook(lookVector);

        // Update the up vector
        setUp(upVector);

        needUpdate = true;
    }

    public void apply() {
        if (!needUpdate) {
            return;
        }

        updateView();

        // If 'orbiting' then offset the rotation point based on the 'orbitPoint'
        if (orbit) {
            double pre[] = { 1.0, 0.0, 0.0, 0.0,
                    0.0, 1.0, 0.0, 0.0,
                    0.0, 0.0, 1.0, 0.0,
                    orbitPoint.x, orbitPoint.y, orbitPoint.z, 1.0 };
            double R1[] = matmult(pre, view);
            // Update the [view] matrix
            for (int i=0; i<16; i++) {
                view[i] = R1[i];
            }
        }

        // Multiply the view matrix by any other matrix to alter the matrix axes
        double R[] = matmult(axes, view);
        // Update the [view] matrix
        for (int i=0; i<16; i++) {
            view[i] = R[i];
        }

        // Store the current view matrix position for orbiting purposes
        orbitEye.x = view[12];
        orbitEye.y = view[13];
        orbitEye.z = view[14];

        // If 'orbiting' then offset back the rotation point based
        // on the 'orbitPoint'
        if (orbit) {
            double post[] = { 1.0, 0.0, 0.0, 0.0,
                    0.0, 1.0, 0.0, 0.0,
                    0.0, 0.0, 1.0, 0.0,
                    -orbitPoint.x, -orbitPoint.y, -orbitPoint.z, 1.0 };

            double R2[] = matmult(post, view);
            // Update the [view] matrix
            for (int i=0; i<16; i++) {
                view[i] = R2[i];
            }
        }
    }

    public void updateAxes( double a[] ) {
        for (int i=0; i<16; i++) {
            axes[i] = a[i];
        }
    }

    public void resetAxes() {
        setIdentity(axes);

        needUpdate = true;
    }

    private void setIdentity( double mat[] ) {
        for (int i=0; i<16; i++) {
            mat[i] = 0.0;
        }

        mat[0] = mat[5] = mat[10] = mat[15] = 1.0;
    }

    private void updateView() {
        // Normalize the look vector. setLook() method does this already but
        // we can't assume that the reference wasn't changed so this is likely
        // precautionary and redundant....but can't take chances.
        // Make sure the look vector has some length
        if (Math.abs(look.length()) < 1.0e-6){
            throw new RuntimeException("Look vector cannot have zero length.");
        }
        look.normalize();

        // Cross product of the 'look' and 'up' vectors gets the 'right' vector.
        Vector3d.cross(right, look, up);

        // Make sure the right vector has some length
        if (Math.abs(right.length()) < 1.0e-6){
            throw new RuntimeException("Right vector cannot have zero length.");
        }
        right.normalize();

        // Cross product of the 'right' and 'look' vectors gets the 'up' vector.
        Vector3d.cross(up, right, look);

        // Make sure the up vector has some length
        if (Math.abs(up.length()) < 1.0e-6){
            throw new RuntimeException("Up vector cannot have zero length.");
        }
        up.normalize();

        // Place the results into a matrix format for use with the OpenGL call
        // to glMultMatrixd().
        view[0] = right.x;
        view[1] = up.x;
        view[2] = -look.x;
        view[3] = 0.0;

        view[4] = right.y;
        view[5] = up.y;
        view[6] = -look.y;
        view[7] = 0.0;

        view[8] = right.z;
        view[9] = up.z;
        view[10] = -look.z;
        view[11] = 0.0;

        // If not orbiting, but in free fly-through mode, then set the position
        // portion of the view matrix equal to the dot product sums
        if (!orbit) {
            view[12] = -Vector3d.dot(right, eye);
            view[13] = -Vector3d.dot(up, eye);
            view[14] = Vector3d.dot(look, eye);
            view[15] = 1.0;

            // Else, rotating around a desired orbit point then set the position
            // portion of the view matrix to the orbit eye point that has been
            // calculated and multiply the view matrix by the desired rotation/orbit
            // point offset
        } else {
            view[12] = orbitEye.x;
            view[13] = orbitEye.y;
            view[14] = orbitEye.z;
            view[15] = 1.0;

            // multiply the view matrix by the orbit point translation.
            double matrotation[] = { 1.0, 0.0, 0.0, 0.0,
                    0.0, 1.0, 0.0, 0.0,
                    0.0, 0.0, 1.0, 0.0,
                    -orbitPoint.x, -orbitPoint.y, -orbitPoint.z, 1.0};
            double R[] = matmult(matrotation, view);
            // Update the [view] matrix
            for (int i=0; i<16; i++) {
                view[i] = R[i];
            }
        }

        System.out.println("---- updateView() --------------------------------");
        System.out.println("   look: " + look
                + "\n    eye: " + eye
                + "\n     up: " + up
                + "\n  right: " + right);

        System.out.println("   view matrix:");
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                System.out.print("  " + view[4*i+j]);
            }
            System.out.println("");
        }

        needUpdate = false;
    }

    public double[] getViewCopy() {
        // First do a check if an update is needed
        checkUpdate();

        double viewCopy[] = new double[16];

        for (int i=0; i<16; i++) {
            viewCopy[i] = view[i];
        }

        return viewCopy;
    }

    public double[] getView() {
        // First do a check if an update is needed
        checkUpdate();

        return view;
    }

    private void checkUpdate() {
        if (needUpdate) {
            apply();
        }
    }

    public void rotateAroundLookVector( double angle ) {
        if (!emulateFPS) {
            // First calculate the updated up vector that was rotated about the
            // look vector.
            setUp( rotateVector(angle, up, look) );

            // The last vector of the axis triple, the right vector, will be
            // automatically calculated when the apply() method is called.
        }
    }

    public void rotateAroundUpVector( double angle ) {
//    // The last vector of the axis triple, the right vector, will be
//    // automatically calculated when the apply() method is called.


        double matrotation[] = { 1.0, 0.0, 0.0, 0.0,
                0.0, 1.0, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0 };

        if (emulateFPS) {
            matrotation = matRotate(matrotation, angle, new Vector3d(0.0, 1.0, 0.0));
        } else {
            matrotation = matRotate(matrotation, angle, up);
        }
        look = transformVector(matrotation, look);
        up = transformVector(matrotation, up);
        right = transformVector(matrotation, right);

        needUpdate = true;
    }

    private double[] matRotate( double mat[], double angle, Vector3d axis ) {
        double s = Math.sin(angle * Math.PI / 180.0);
        double c = Math.cos(angle * Math.PI / 180.0);

        axis.normalize();

        //ERROR: mat[0] = c + (1 - c) * axis.x;
        mat[0] = c + (1 - c) * Math.pow(axis.x, 2);
        mat[1] = (1 - c) * axis.x * axis.y + s * axis.z;
        mat[2] = (1 - c) * axis.x * axis.z - s * axis.y;
        mat[3] = 0.0;

        mat[4] = (1 - c) * axis.y * axis.x - s * axis.z;
        mat[5] = c + (1 - c) * Math.pow(axis.y, 2);
        mat[6] = (1 - c) * axis.y * axis.z + s * axis.x;
        mat[7] = 0.0;

        mat[8] = (1 - c) * axis.z * axis.x + s * axis.y;
        //ERROR: mat[9] = (1 - c) * axis.z * axis.z - s * axis.x;
        mat[9] = (1 - c) * axis.y * axis.z - s * axis.x;
        mat[10] = c + (1 - c) * Math.pow(axis.z, 2);
        mat[11] = 0.0;

        mat[12] = 0.0;
        mat[13] = 0.0;
        mat[14] = 0.0;
        mat[15] = 1.0;

        return mat;
    }

    private Vector3d transformVector( double mat[], Vector3d vec ) {
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;

        vec.x = x * mat[0] +
                y * mat[4] +
                z * mat[8];

        vec.y = x * mat[1] +
                y * mat[5] +
                z * mat[9];

        vec.z = x * mat[2] +
                y * mat[6] +
                z * mat[10];

        return vec;
    }

    private double[] matmult( double one[], double two[] ) {
        double R[] = { 1.0, 0.0, 0.0, 0.0,
                0.0, 1.0, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0 };

        R[0] = one[0]*two[0] + one[1]*two[4] + one[2]*two[8] + one[3]*two[12];
        R[1] = one[0]*two[1] + one[1]*two[5] + one[2]*two[9] + one[3]*two[13];
        R[2] = one[0]*two[2] + one[1]*two[6] + one[2]*two[10] + one[3]*two[14];
        R[3] = one[0]*two[3] + one[1]*two[7] + one[2]*two[11] + one[3]*two[15];

        R[4] = one[4]*two[0] + one[5]*two[4] + one[6]*two[8] + one[7]*two[12];
        R[5] = one[4]*two[1] + one[5]*two[5] + one[6]*two[9] + one[7]*two[13];
        R[6] = one[4]*two[2] + one[5]*two[6] + one[6]*two[10] + one[7]*two[14];
        R[7] = one[4]*two[3] + one[5]*two[7] + one[6]*two[11] + one[7]*two[15];

        R[8] = one[8]*two[0] + one[9]*two[4] + one[10]*two[8] + one[11]*two[12];
        R[9] = one[8]*two[1] + one[9]*two[5] + one[10]*two[9] + one[11]*two[13];
        R[10] = one[8]*two[2] + one[9]*two[6] + one[10]*two[10] + one[11]*two[14];
        R[11] = one[8]*two[3] + one[9]*two[7] + one[10]*two[11] + one[11]*two[15];

        R[12] = one[12]*two[0] + one[13]*two[4] + one[14]*two[8] + one[15]*two[12];
        R[13] = one[12]*two[1] + one[13]*two[5] + one[14]*two[9] + one[15]*two[13];
        R[14] = one[12]*two[2] + one[13]*two[6] + one[14]*two[10] + one[15]*two[14];
        R[15] = one[12]*two[3] + one[13]*two[7] + one[14]*two[11] + one[15]*two[15];

        return R;
    }

    public void rotateAroundRightVector( double angle ) {
        // First calculate the updated look vector that was rotated about the
        // right vector.
        setLook( rotateVector(angle, look, right) );

        // Using the new look vector (ie the one rotated about the right vector)
        // calculate the new up vector.  Cross product of the 'right' and 'look'
        // vectors gets the 'up' vector.
        Vector3d.cross(up, right, look);

        // Make sure the look vector has some length
        if (Math.abs(up.length()) < 1.0e-6){
            throw new RuntimeException("Up vector cannot have zero length.");
        }
        up.normalize();
    }

    public void stepLeft() {
        moveLeftRight(-moveSpeed);
    }


    public void stepRight() {
        moveLeftRight(moveSpeed);
    }


    public void moveLeft( double step ) {
        moveLeftRight(-step);
    }

    public void moveRight( double step ) {
        moveLeftRight(step);
    }

    public void moveLeftRight( double step ) {
        if (!orbit) {
            Vector3d tmpRight = getRightCopy();

            eye.x += tmpRight.x * step;
            eye.y += tmpRight.y * step;
            eye.z += tmpRight.z * step;
        } else {
            orbitEye.x -= step;
        }

        needUpdate = true;
    }

    public void stepFwd() {
        moveFwdBack(moveSpeed);
    }

    public void stepBack() {
        moveFwdBack(-moveSpeed);
    }

    public void moveFwd( double step ) {
        moveFwdBack(step);
    }

    public void moveBack( double step ) {
        moveFwdBack(-step);
    }

    public void moveFwdBack( double step ) {
        if (!orbit) {
            Vector3d tmpLook = getLookCopy();

            eye.x += tmpLook.x * step;
            eye.y += tmpLook.y * step;
            eye.z += tmpLook.z * step;
        } else {
            orbitEye.z += step;
        }

        needUpdate = true;
    }

    public void stepUp() {
        moveUpDown(moveSpeed);
    }

    public void stepDown() {
        moveUpDown(-moveSpeed);
    }

    public void moveUp( double step ) {
        moveUpDown(step);
    }

    public void moveDown( double step ) {
        moveUpDown(-step);
    }

    public void moveUpDown( double step ) {
        if (!orbit) {
            if (emulateFPS) {
                eye.y += step;
            } else {
                Vector3d tmpUp = getUpCopy();

                eye.x += tmpUp.x * step;
                eye.y += tmpUp.y * step;
                eye.z += tmpUp.z * step;
            }
        } else {
            orbitEye.y -= step;
        }

        needUpdate = true;
    }

    private Vector3d rotateVector( double angle, Vector3d rotationVector,
                                   Vector3d rotateAboutVector ) {
        // Convert angle to degrees
        double a = angle * Math.PI/180.0;

        // Create a quaternion from the vector to rotate
        Quat4f q1 = new Quat4f((float)rotationVector.x,
                (float)rotationVector.y,
                (float)rotationVector.z,
                0.0f);

        // Create a quaternion from the vector to rotate about
        Quat4f q2 = new Quat4f((float)(rotateAboutVector.x * Math.sin(a/2.0)),
                (float)(rotateAboutVector.y * Math.sin(a/2.0)),
                (float)(rotateAboutVector.z * Math.sin(a/2.0)),
                (float)(Math.cos(a/2)));

        // Create the 3rd quaternion that is the result of the multiplication
        // of the previous rotation vector:
        //
        //    q3 = q2 q1 q2*
        //
        //       where * = conjugate  (in this case, the conjugate of q2)
        //
        Quat4f q3 = new Quat4f();
        q3 = Quat4f.mult(q2, Quat4f.mult(q1, Quat4f.conjugate(q2)));

        // Extract the rotated vector from the calculated quaternion
        Vector3d rotatedVector = new Vector3d(q3.x, q3.y, q3.z);

        // Make sure the look vector has some length
        if (Math.abs(rotatedVector.length()) < 1.0e-6){
            throw new RuntimeException("Rotated vector cannot have zero length.");
        }
        rotatedVector.normalize();

        return rotatedVector;
    }

    public void setMoveSpeed( double speed ) {
        moveSpeed = speed;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public void setEmulateFPS( boolean state ) {
        emulateFPS = state;
    }

    public boolean getEmulateFPS() {
        return emulateFPS;
    }

    public boolean getOrbit() {
        return orbit;
    }

    public void setOrbit( boolean state ) {
        orbit = state;

        if (orbit) {
            orbitPoint.x = eye.x;
            orbitPoint.y = eye.y;
            orbitPoint.z = eye.z;

            needUpdate = true;
        }
    }

    public Vector3d getOrbitPoint() {
        return orbitPoint;
    }

    public Vector3d getOrbitPointCopy() {
        return new Vector3d(orbitPoint);
    }

    public void setOrbitPoint( Vector3d pt ) {
        // Make sure eye point is not null
        if (pt == null) {
            throw new RuntimeException("Orbit point cannot be null.");
        }

        orbitPoint.x = pt.x;
        orbitPoint.y = pt.y;
        orbitPoint.z = pt.z;

        needUpdate = true;
    }

    public double getOrbitDistance() {
        return -orbitEye.z;
    }

    public void setOrbitDistance( double distance ) {
        orbitDistance = distance;

        orbitEye.x = 0.0;
        orbitEye.y = 0.0;
        orbitEye.z = -orbitDistance;

        needUpdate = true;
    }

    @Override
    public String toString() {
        return new String("ru.savushkin.camera.Camera Properties:"
                + "\n  Look Vector: " + look
                + "\n   Eye Vector: " + eye
                + "\n    Up Vector: " + up
                + "\n Right Vector: " + right);
    }
}