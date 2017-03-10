package ru.savushkin.common;

public class Quat4f {

    public float x = 0.0f;

    public float y = 0.0f;

    public float z = 0.0f;

    public float w = 0.0f;

    public Quat4f() {
        // Nothing
    }

    public Quat4f( float x, float y, float z, float w ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Quat4f conjugate( Quat4f quat ) {
        Quat4f C = new Quat4f();

        C.x = -quat.x;
        C.y = -quat.y;
        C.z = -quat.z;
        C.w = quat.w;

        return C;
    }
    public static Quat4f mult( Quat4f A, Quat4f B ) {
        Quat4f C = new Quat4f();

        C.x = A.w*B.x + A.x*B.w + A.y*B.z - A.z*B.y;
        C.y = A.w*B.y - A.x*B.z + A.y*B.w + A.z*B.x;
        C.z = A.w*B.z + A.x*B.y - A.y*B.x + A.z*B.w;
        C.w = A.w*B.w - A.x*B.x - A.y*B.y - A.z*B.z;

        return C;
    }

    public String toString() {
        return "Quaternion: x: " + x + " y: " + y + " z: " + z + " w: " + w;
    }
}