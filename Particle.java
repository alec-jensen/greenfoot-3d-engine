public class Particle extends Actor3D {
    private Vec3 velocity;
    public double lifespan;

    private final Vec3 normal = new Vec3(1, 1, 1);

    public Particle(double x, double y, double z, Vec3 velocity, double lifespan, Material material) {
        super(x, y, z, 0, 0, 0, material);
        this.velocity = velocity;
        this.lifespan = lifespan;
    }

    @Override
    public void act() {
        // Randomly change horizontal velocity slightly
        velocity = velocity.add(new Vec3(Math.random() * 0.1 - 0.05, 0, Math.random() * 0.1 - 0.05));
        velocity = velocity.normalize();

        // Update position based on velocity
        x += velocity.x * world.deltaT;
        y += velocity.y * world.deltaT;
        z += velocity.z * world.deltaT;

        // Decrease lifespan
        lifespan -= world.deltaT;

        // Remove particle if lifespan is over
        if (lifespan <= 0) {
            world.removeObject(this);
            world.particles.remove(this);
        }
    }

    @Override
    public Vec3 getCollision(Ray ray) {
        // Calculate the vector from the ray origin to the particle position
        Vec3 toParticle = new Vec3(x, y, z).subtract(ray.getOrigin());

        // Project the vector onto the ray direction
        double t = toParticle.dot(ray.getDirection());

        // Find the closest point on the ray to the particle
        Vec3 closestPoint = ray.getOrigin().add(ray.getDirection().multiply(t));

        // Calculate the distance from the closest point to the particle
        double distance = closestPoint.subtract(new Vec3(x, y, z)).magnitude();

        // Check if the distance is less than the particle's radius (assuming a small radius)
        double radius = 0.5; // Example radius
        if (distance <= radius) {
            return closestPoint;
        } else {
            return null;
        }
    }

    @Override
    public Vec3 getNormal(Vec3 intersectionPoint)
    {
        return normal;
    }

    @Override
    public Vec3 getUV(Vec3 intersectionPoint)
    {
        return new Vec3(0, 0, 0);
    }
}