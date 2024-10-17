/**
 * Write a description of class Ray here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Ray  
{
    public double x;
    public double y;
    public double z;
    public double dx;
    public double dy;
    public double dz;
    public double t;

    public Ray(double x, double y, double z, double dx, double dy, double dz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public Ray add(Ray ray) {
        return new Ray(x + ray.x, y + ray.y, z + ray.z, dx + ray.dx, dy + ray.dy, dz + ray.dz);
    }

    public Ray subtract(Ray ray) {
        return new Ray(x - ray.x, y - ray.y, z - ray.z, dx - ray.dx, dy - ray.dy, dz - ray.dz);
    }

    public Ray multiply(Ray ray) {
        return new Ray(x * ray.x, y * ray.y, z * ray.z, dx * ray.dx, dy * ray.dy, dz * ray.dz);
    }

    public Ray multiply(double scalar) {
        return new Ray(x * scalar, y * scalar, z * scalar, dx * scalar, dy * scalar, dz * scalar);
    }

    public Ray divide(Ray ray) {
        return new Ray(x / ray.x, y / ray.y, z / ray.z, dx / ray.dx, dy / ray.dy, dz / ray.dz);
    }

    public Ray divide(double scalar) {
        return new Ray((int)(x / scalar), (int)(y / scalar), (int)(z / scalar), (int)(dx / scalar), (int)(dy / scalar), (int)(dz / scalar));
    }

    public double magnitude() {
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public Ray normalize()
    {
        return divide(magnitude());
    }

    public Vec3 getOrigin() {
        return new Vec3(x, y, z);
    }

    public Vec3 getDirection() {
        return new Vec3(dx, dy, dz);
    }

    public Ray rotateX(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Ray(x, y * cos - z * sin, y * sin + z * cos, dx, dy * cos - dz * sin, dy * sin + dz * cos);
    }

    public Ray rotateY(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Ray(x * cos + z * sin, y, -x * sin + z * cos, dx * cos + dz * sin, dy, -dx * sin + dz * cos);
    }

    public Ray rotateZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Ray(x * cos - y * sin, x * sin + y * cos, z, dx * cos - dy * sin, dx * sin + dy * cos, dz);
    }

    @Override
    public String toString()
    {
        return "Ray: (" + x + ", " + y + ", " + z + ") -> (" + dx + ", " + dy + ", " + dz + ")";
    }
}
