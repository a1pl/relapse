package cc.squall.relapse.utils.vector;


import lombok.Setter;
@Setter
public class Vector3D {

    public double x;
    public double y;
    public double z;

    public Vector3D(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D add(final double x, final double y, final double z) {
        return new Vector3D(this.x + x, this.y + y, this.z + z);
    }

    public Vector3D add(final Vector3D vector) {
        return add(vector.x, vector.y, vector.z);
    }

    public Vector3D subtract(final double x, final double y, final double z) {
        return add(-x, -y, -z);
    }

    public Vector3D subtract(final Vector3D vector) {
        return add(-vector.x, -vector.y, -vector.z);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3D multiply(final double v) {
        return new Vector3D(x * v, y * v, z * v);
    }

    public double distance(Vector3D vector3d) {
        return Math.sqrt(Math.pow(vector3d.x - x, 2) + Math.pow(vector3d.y - y, 2) + Math.pow(vector3d.z - z, 2));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3D vector)) return false;

        return ((Math.floor(x) == Math.floor(vector.x)) && Math.floor(y) == Math.floor(vector.y) && Math.floor(z) == Math.floor(vector.z));
    }
}