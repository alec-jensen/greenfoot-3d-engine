/**
 * Write a description of class Camera3D here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Camera3D  
{
    double x;
    double y;
    double z;
    double rotationX;
    double rotationY;
    double rotationZ;
    int screenWidth;
    int screenHeight;
    double focalLength;

    public Camera3D(double x, double y, double z, double rotationX, double rotationY, double rotationZ, int screenWidth, int screenHeight, double focalLength) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.focalLength = focalLength;
    }

    public Ray generateRay(int x, int y) {
        double fov = Math.toRadians(focalLength); // Field of view in radians
        double halfHeight = Math.tan(fov / 2);
        double halfWidth = halfHeight * ((double) screenWidth / screenHeight); // Aspect ratio

        // Convert screen coordinates to world coordinates
        double ndcX = (2.0 * x) / screenWidth - 1.0; // Normalized device coordinates
        double ndcY = 1.0 - (2.0 * y) / screenHeight;

        double worldX = ndcX * halfWidth;
        double worldY = ndcY * halfHeight;

        // Generate the direction of the ray (not affecting the camera's position)
        Vec3 direction = new Vec3(worldX, worldY, 1).normalize();

        // Rotate the direction vector based on the camera's rotation
        direction = direction.rotateX(Math.toRadians(rotationX));
        direction = direction.rotateY(Math.toRadians(rotationY));
        direction = direction.rotateZ(Math.toRadians(rotationZ));

        // Keep the camera's position fixed
        return new Ray(this.x, this.y, this.z, direction.x, direction.y, direction.z);
    }

    public boolean isVisible(Vec3 point) {
        // Translate the point to the camera's local space
        Vec3 localPoint = new Vec3(point.x - x, point.y - y, point.z - z);

        // Rotate the point to the camera's local space
        localPoint = localPoint.rotateX(Math.toRadians(-rotationX))
                .rotateY(Math.toRadians(-rotationY))
                .rotateZ(Math.toRadians(-rotationZ));

        // Check if the point is in front of the camera
        return localPoint.z > 0;
    }

    public boolean isVisible(double x, double y, double z) {
        return isVisible(new Vec3(x, y, z));
    }
}
