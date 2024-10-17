/**
 * Write a description of class Vec3 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Vec3  
{
    double x;
    double y;
    double z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 add(Vec3 vec) {
        return new Vec3(x + vec.x, y + vec.y, z + vec.z);
    }

    public Vec3 add(double scalar) {
        return new Vec3(x + scalar, y + scalar, z + scalar);
    }

    public Vec3 add(double x, double y, double z) {
        return new Vec3(this.x + x, this.y + y, this.z + z);
    }

    public Vec3 subtract(Vec3 vec) {
        return new Vec3(x - vec.x, y - vec.y, z - vec.z);
    }

    public Vec3 subtract(double x, double y, double z) {
        return new Vec3(this.x - x, this.y - y, this.z - z);
    }

    public Vec3 multiply(Vec3 vec) {
        return new Vec3(x * vec.x, y * vec.y, z * vec.z);
    }

    public Vec3 multiply(double scalar) {
        return new Vec3(x * scalar, y * scalar, z * scalar);
    }

    public Vec3 divide(Vec3 vec) {
        return new Vec3(x / vec.x, y / vec.y, z / vec.z);
    }

    public Vec3 divide(double scalar) {
        return new Vec3(x / scalar, y / scalar, z / scalar);
    }

    public double dot(Vec3 vec) {
        return x * vec.x + y * vec.y + z * vec.z;
    }

    public Vec3 cross(Vec3 vec) {
        return new Vec3(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y - y * vec.x);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3 normalize()
    {
        return divide(magnitude());
    }

    public Vec3 rotateX(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vec3(x, y * cos - z * sin, y * sin + z * cos);
    }

    public Vec3 rotateY(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vec3(x * cos + z * sin, y, -x * sin + z * cos);
    }

    public Vec3 rotateZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vec3(x * cos - y * sin, x * sin + y * cos, z);
    }

    public Vec3 inverse() {
        return new Vec3(1 / x, 1 / y, 1 / z);
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
