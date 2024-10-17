import greenfoot.*;

public class Material  
{
    public Color color;
    public double ambient;
    public double diffuse;
    public double specular;
    public double shininess;
    public double roughness;

    public Material(Color color, double ambient, double diffuse, double specular, double shininess, double roughness) {
        this.color = color;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
        this.roughness = roughness;
    }

    public Color getColor(double u, double v) {
        return color;
    }
}
