import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Light here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Light extends Actor
{
    double x;
    double y;
    double z;
    Color color;
    double intensity;

    public Light(double x, double y, double z, Color color, double intensity) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.intensity = intensity;
    }
}
