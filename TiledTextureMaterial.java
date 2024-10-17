import greenfoot.GreenfootImage;
import greenfoot.Color;

/**
 * Write a description of class TiledTextureMaterial here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TiledTextureMaterial extends TextureMaterial 
{
    double tileRatio;

    public TiledTextureMaterial(GreenfootImage texture, double tileRatio, double ambient, double diffuse, double specular, double shininess, double roughness) {
        super(texture, ambient, diffuse, specular, shininess, roughness);
        this.tileRatio = tileRatio;
    }

    public Color getColor(double u, double v) {
        // Scale the texture coordinates to the image size
        u = u * texture.getWidth() * tileRatio;
        v = v * texture.getHeight() * tileRatio;

        return texture.getColorAt((int) u % texture.getWidth(), (int) v % texture.getHeight());
    }
}
