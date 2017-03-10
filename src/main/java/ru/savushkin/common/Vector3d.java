package ru.savushkin.common;

public class Vector3d {

    public double x = 0.0;

    public double y = 0.0;

    public double z = 0.0;

    public Vector3d() {
        // Nothing
    }

    public Vector3d( double x, double y, double z ) {
        set(x, y, z);
    }

    public Vector3d( Vector3d vector ) {
        set(vector.x, vector.y, vector.z);
    }

    public void set( double x, double y, double z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void cross( Vector3d Result, Vector3d v1, Vector3d v2 ) {
        Result.x = v1.y*v2.z - v1.z*v2.y;
        Result.y = v1.z*v2.x - v1.x*v2.z;
        Result.z = v1.x*v2.y - v1.y*v2.x;
    }

    public void cross( Vector3d v1, Vector3d v2 ) {
        this.x = v1.y*v2.z - v1.z*v2.y;
        this.y = v1.z*v2.x - v1.x*v2.z;
        this.z = v1.x*v2.y - v1.y*v2.x;
    }

    public static double dot( Vector3d v1, Vector3d v2 ) {
        return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z);
    }

    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public void sub( Vector3d one, Vector3d two ) {
        this.x = one.x - two.x;
        this.y = one.y - two.y;
        this.z = one.z - two.z;
    }

    public void normalize() {
        double length = length();
        
        if (length < 1E-9) {
            throw new RuntimeException("Cannot normalize vector whose length is zero");
        }
        
        x /= length;
        y /= length;
        z /= length;
    }

    public void negate() {
      x *= -1.0;
      y *= -1.0;
      z *= -1.0;
    }

    public void mul( double value ) {
        this.x *= value;
        this.y *= value;
        this.z *= value;
    }

    public String toString() {
        return "x: " + x + " y: " + y + " z: " + z;
    }
}