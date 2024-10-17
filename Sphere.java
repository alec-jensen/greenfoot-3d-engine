import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Sphere here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Sphere extends Actor3D
{
    double radius;

    public Sphere(double x, double y, double z, Material material, double radius)
    {
        super(x, y, z, 0, 0, 0, material);
        this.radius = radius;
    }

    /**
     * Act - do whatever the Sphere wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Add your action code here.
    }

    @Override
    public Vec3 getCollision(Ray ray)
    {
        // Sphere center
        Vec3 center = new Vec3(x, y, z);

        // Direction from ray origin to the sphere center
        Vec3 oc = ray.getOrigin().subtract(center);

        // Compute the coefficients of the quadratic equation
        double a = ray.getDirection().dot(ray.getDirection());
        double b = 2.0 * oc.dot(ray.getDirection());
        double c = oc.dot(oc) - radius * radius;

        // Discriminant to determine if the ray intersects the sphere
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            // No real solutions, the ray misses the sphere
            return null;
        } else {
            double root1 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
            double root2 = (-b + Math.sqrt(discriminant)) / (2.0 * a);

            // Only consider roots that are greater than zero
            if (root1 > 0 && root2 > 0) {
                return ray.getOrigin().add(ray.getDirection().multiply(Math.min(root1, root2)));
            } else if (root1 > 0) {
                return ray.getOrigin().add(root1).multiply(ray.getDirection());
            } else if (root2 > 0) {
                return ray.getOrigin().add(root2).multiply(ray.getDirection());
            } else {
                return null;
            }
        }
    }

    @Override
    public Vec3 getNormal(Vec3 intersectionPoint)
    {
        // Calculate the vector from the center of the sphere to the intersection point
        Vec3 normal = intersectionPoint.subtract(new Vec3(x, y, z));

        // Normalize the vector
        return normal.multiply(-1.0).normalize();
    }

    @Override
    public Vec3 getUV(Vec3 intersectionPoint)
    {
        // Calculate the vector from the center of the sphere to the intersection point
        Vec3 normal = intersectionPoint.subtract(new Vec3(x, y, z));

        // Normalize the vector
        normal = normal.normalize();

        // Calculate the spherical coordinates
        double u = 0.5 + Math.atan2(normal.z, normal.x) / (2 * Math.PI);
        double v = 0.5 - Math.asin(normal.y) / Math.PI;

        return new Vec3(u, v, 0);
    }
}
