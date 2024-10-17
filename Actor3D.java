import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Actor3D here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Actor3D extends Actor
{
    double x;
    double y;
    double z;
    double rotationX;
    double rotationY;
    double rotationZ;
    Material material;

    public World3D world;

    public Actor3D(double x, double y, double z, double rotationX, double rotationY, double rotationZ, Material material) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.material = material;
    }

    public Vec3 getNormal(Vec3 intersectionPoint) {
        return null;
    }

    public Vec3 getCollision(Ray ray) {
        return null;
    }

    public Vec3 getUV(Vec3 intersectionPoint) {
        return null;
    }
}
