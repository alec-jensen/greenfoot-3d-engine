import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Cube extends RectPrism
{
    double size;

    public Cube(double x, double y, double z, double rotationX, double rotationY, double rotationZ, Material material, double size) {
        super(x, y, z, rotationX, rotationY, rotationZ, material, size, size, size);
        this.size = size;
    }

    public void act()
    {
        // Add your action code here.
    }
}
