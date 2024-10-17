import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class RectPrism here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RectPrism extends Actor3D
{
    double width;
    double height;
    double depth;

    public RectPrism(double x, double y, double z, double rotationX, double rotationY, double rotationZ, Material material, double width, double height, double depth)
    {
        super(x, y, z, rotationX, rotationY, rotationZ, material);
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    public Vec3 getCollision(Ray ray)
    {
        // Translate ray to prism's local space (relative to its center)
        Ray localRay = new Ray(ray.x - x, ray.y - y, ray.z - z, ray.dx, ray.dy, ray.dz);

        // Rotate the ray to the local space of the prism
        localRay = localRay.rotateX(Math.toRadians(rotationX))
                .rotateY(Math.toRadians(rotationY))
                .rotateZ(Math.toRadians(rotationZ));

        // Adjusted cube bounds relative to the center
        double halfWidth = width / 2.0;
        double halfHeight = height / 2.0;
        double halfDepth = depth / 2.0;

        double tmin = 0.0, tmax = Double.POSITIVE_INFINITY;

        double epsilon = 1e-6;

        // Check the X-axis bounds
        if (Math.abs(localRay.dx) < epsilon) {
            // Ray is parallel to the X-axis planes
            if (localRay.x < -halfWidth || localRay.x > halfWidth) {
                return null; // No intersection if the origin is outside the prism's X bounds
            }
        } else {
            double t1 = (-halfWidth - localRay.x) / localRay.dx;
            double t2 = (halfWidth - localRay.x) / localRay.dx;

            tmin = Math.max(tmin, Math.min(t1, t2));
            tmax = Math.min(tmax, Math.max(t1, t2));
        }

        // Check the Y-axis bounds
        if (Math.abs(localRay.dy) < epsilon) {
            // Ray is parallel to the Y-axis planes
            if (localRay.y < -halfHeight || localRay.y > halfHeight) {
                return null; // No intersection if the origin is outside the prism's Y bounds
            }
        } else {
            double t1 = (-halfHeight - localRay.y) / localRay.dy;
            double t2 = (halfHeight - localRay.y) / localRay.dy;

            tmin = Math.max(tmin, Math.min(t1, t2));
            tmax = Math.min(tmax, Math.max(t1, t2));
        }

        // Check the Z-axis bounds
        if (Math.abs(localRay.dz) < epsilon) {
            // Ray is parallel to the Z-axis planes
            if (localRay.z < -halfDepth || localRay.z > halfDepth) {
                return null; // No intersection if the origin is outside the prism's Z bounds
            }
        } else {
            double t1 = (-halfDepth - localRay.z) / localRay.dz;
            double t2 = (halfDepth - localRay.z) / localRay.dz;

            tmin = Math.max(tmin, Math.min(t1, t2));
            tmax = Math.min(tmax, Math.max(t1, t2));
        }

        // Final check to determine if there is a valid intersection
        if (tmax < Math.max(tmin, 0.0)) {
            return null; // No intersection
        }


        // Calculate the intersection point
        Vec3 intersection = localRay.getOrigin().add(localRay.getDirection().multiply(tmin));

        // Rotate the intersection point back to world space
        intersection = intersection.rotateX(-Math.toRadians(rotationX))
                .rotateY(-Math.toRadians(rotationY))
                .rotateZ(-Math.toRadians(rotationZ));

        // Translate the intersection point back to world space
        intersection = intersection.add(x, y, z);

        return intersection;
    }

    @Override
    public Vec3 getNormal(Vec3 intersectionPoint) {
        // Translate intersection point to prism's local space (relative to its center)
        Vec3 localIntersection = new Vec3(intersectionPoint.x - x, intersectionPoint.y - y, intersectionPoint.z - z);

        // Rotate the intersection point to the local space of the prism
        localIntersection = localIntersection.rotateX(-Math.toRadians(-rotationX))
                .rotateY(-Math.toRadians(-rotationY))
                .rotateZ(-Math.toRadians(-rotationZ));

        Vec3 normal;
        double epsilon = 1e-6;  // Smaller epsilon for better precision

        // X-axis face normals
        if (Math.abs(localIntersection.x - width / 2.0) < epsilon) {
            normal = new Vec3(1, 0, 0); // Right face
        } else if (Math.abs(localIntersection.x + width / 2.0) < epsilon) {
            normal = new Vec3(-1, 0, 0); // Left face
        }
        // Y-axis face normals
        else if (Math.abs(localIntersection.y - height / 2.0) < epsilon) {
            normal = new Vec3(0, 1, 0); // Top face
        } else if (Math.abs(localIntersection.y + height / 2.0) < epsilon) {
            normal = new Vec3(0, -1, 0); // Bottom face
        }
        // Z-axis face normals
        else if (Math.abs(localIntersection.z - depth / 2.0) < epsilon) {
            normal = new Vec3(0, 0, 1); // Front face
        } else if (Math.abs(localIntersection.z + depth / 2.0) < epsilon) {
            normal = new Vec3(0, 0, -1); // Back face
        } else {
            normal = new Vec3(0, 0, 0); // Default return value, should never be reached
        }

        // Rotate the normal back to world space
        normal = normal.rotateX(Math.toRadians(rotationX))
                .rotateY(Math.toRadians(rotationY))
                .rotateZ(Math.toRadians(rotationZ));

        // Normalize the normal vector
        return normal.normalize();
    }

    @Override
    public Vec3 getUV(Vec3 intersectionPoint)
    {
        // Translate intersection point to prism's local space (relative to its center)
        Vec3 localIntersection = new Vec3(intersectionPoint.x - x, intersectionPoint.y - y, intersectionPoint.z - z);

        // Rotate the intersection point to the local space of the prism
        localIntersection = localIntersection.rotateX(Math.toRadians(rotationX))
                .rotateY(Math.toRadians(rotationY))
                .rotateZ(Math.toRadians(rotationZ));

        double epsilon = 1e-6;  // Smaller epsilon for better precision

        // X-axis face UV coordinates
        if (Math.abs(localIntersection.x - width / 2.0) < epsilon) {
            return new Vec3((localIntersection.z + depth / 2.0) / depth, 1 - (localIntersection.y + height / 2.0) / height, 0);
        } else if (Math.abs(localIntersection.x + width / 2.0) < epsilon) {
            return new Vec3((localIntersection.z + depth / 2.0) / depth, 1 - (localIntersection.y + height / 2.0) / height, 0);
        }
        // Y-axis face UV coordinates
        else if (Math.abs(localIntersection.y - height / 2.0) < epsilon) {
            return new Vec3((localIntersection.x + width / 2.0) / width, 1 - (localIntersection.z + depth / 2.0) / depth, 0);
        } else if (Math.abs(localIntersection.y + height / 2.0) < epsilon) {
            return new Vec3((localIntersection.x + width / 2.0) / width, 1 - (localIntersection.z + depth / 2.0) / depth, 0);
        }
        // Z-axis face UV coordinates
        else if (Math.abs(localIntersection.z - depth / 2.0) < epsilon) {
            return new Vec3((localIntersection.x + width / 2.0) / width, 1 - (localIntersection.y + height / 2.0) / height, 0);
        } else if (Math.abs(localIntersection.z + depth / 2.0) < epsilon) {
            return new Vec3((localIntersection.x + width / 2.0) / width, 1 - (localIntersection.y + height / 2.0) / height, 0);
        } else {
            return new Vec3(0, 0, 0); // Default return value, should never be reached
        }
    }
}
