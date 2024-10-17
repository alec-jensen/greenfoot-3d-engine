import greenfoot.Color;
import greenfoot.GreenfootImage;

/**
 * Write a description of class TextureMaterial here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TextureMaterial extends Material 
{
    GreenfootImage texture;

    private int width;
    private int height;

    public TextureMaterial(GreenfootImage texture, double ambient, double diffuse, double specular, double shininess, double roughness) {
        super(null, ambient, diffuse, specular, shininess, roughness);
        this.texture = texture;

        width = texture.getWidth();
        height = texture.getHeight();
    }

    public Color getColor(double u, double v) {
        // Scale the texture coordinates to the image size
        u = u * width;
        v = v * height;

        // Wrap the texture coordinates
        u = (u % width + width) % width;
        v = (v % height + height) % height;

        return texture.getColorAt((int) u, (int) v);
    }
}
