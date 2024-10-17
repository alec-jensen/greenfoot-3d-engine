import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import greenfoot.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Write a description of class World3D here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class World3D extends World
{
    private Camera3D camera;
    private final ArrayList<Actor3D> actors = new ArrayList<Actor3D>();
    private final ArrayList<Light> lights = new ArrayList<Light>();

    private final long startTime = System.nanoTime();
    public double runTime = 0;

    private long fpsTime = System.nanoTime();
    private int frames = 0;

    private double averageFPS = 0;
    private int totalFrames = 0;
    private long fpsIntervalStartTime = System.nanoTime();

    private long lastMillis = System.currentTimeMillis();
    public double deltaT = 1.0;

    public int score = 0;

    public static final int MOVEMENT_SPEED = 50;
    public static final int SPRINT_SPEED = 100;
    public static final double MAX_STAMINA = 400;
    public static final double STAMINA_COOLDOWN_TIME = 3000;
    public static final int ROTATION_SPEED = 90;
    public static final double BASE_AMBIENT = 0.1;
    public static final int WIN_SCORE = 20;
    public static final Vec3 POSITIVE_BOUNDS = new Vec3(249, Double.POSITIVE_INFINITY, 249);
    public static final Vec3 NEGATIVE_BOUNDS = new Vec3(-249, 0, -249);

    private final String[] winDialog = {
        "How could you do this to me?",
        "I thought we were friends...",
        "I trusted you...",
        "I thought you were different...",
        "I thought you were better than this...",
        "WHY DID YOU EAT ALL THE ORBS?",
        "I'm so disappointed in you...",
        "I thought you were better than this...",
        "You're a monster...",
        "You will pay for this...",
    };

    private int winDialogIndex = 0;

    public final ArrayList<Particle> particles = new ArrayList<>();
    public Cube cube;

    // DEBUG OPTIONS

    public boolean FREE_CAMERA = false;
    public boolean DEBUG_NORMALS = false;
    public boolean DEBUG_UV = false;
    public boolean DRAW_LIGHT_POSITIONS = false;
    public boolean DEBUG_LIGHTING = false;
    public boolean DEBUG_CUBE_POSITION = false;
    public boolean DISABLE_FANCY_VISUALS = false;
    public boolean GOD_MODE = false;
    public boolean DISABLE_PARTICLES = false;
    public boolean DISABLE_POST_PROCESSING = false;

    private long lastOptimizationAttempt = System.currentTimeMillis();

    private final Light light;

    public ArrayList<GreenfootSound> ambience = new ArrayList<GreenfootSound>();
    public ArrayList<GreenfootSound> breathing = new ArrayList<GreenfootSound>();
    public ArrayList<GreenfootSound> randomEffects = new ArrayList<GreenfootSound>();

    long lastHeartbeat = System.currentTimeMillis();
    long lastBreath = System.currentTimeMillis();
    long lastRandomEffect = System.currentTimeMillis();

    long lastCubeTeleport = System.currentTimeMillis();

    long staminaCooldown = System.currentTimeMillis() - (long) STAMINA_COOLDOWN_TIME;

    long winTime = -1;

    long lastKeypress = System.currentTimeMillis();

    double cubeSpeed = 10;
    double stamina = MAX_STAMINA;

    GreenfootSound collectSound = new GreenfootSound("sounds/point-earned.mp3");
    GreenfootSound walkingSound = new GreenfootSound("sounds/running.mp3");
    GreenfootSound winMusic = new GreenfootSound("sounds/win-music.mp3");

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Constructor for objects of class World3D.
     *
     */
    public World3D() throws AWTException
    {
        super(600, 400, 1);

        ambience.add(new GreenfootSound("sounds/ambience-1.mp3"));
        ambience.add(new GreenfootSound("sounds/ambience-2.mp3"));
        ambience.add(new GreenfootSound("sounds/ambience-3.mp3"));
        ambience.add(new GreenfootSound("sounds/ambience-4.mp3"));
        ambience.add(new GreenfootSound("sounds/ambience-5.mp3"));

        breathing.add(new GreenfootSound("sounds/breathing-1.mp3"));
        breathing.add(new GreenfootSound("sounds/breathing-2.mp3"));
        breathing.add(new GreenfootSound("sounds/breathing-3.mp3"));

        randomEffects.add(new GreenfootSound("sounds/alarm.mp3"));
        randomEffects.add(new GreenfootSound("sounds/ominous-breath.mp3"));
        randomEffects.add(new GreenfootSound("sounds/whoosh.mp3"));
        randomEffects.add(new GreenfootSound("sounds/whisper.mp3"));
        randomEffects.add(new GreenfootSound("sounds/horn.mp3"));
        randomEffects.add(new GreenfootSound("sounds/approaching-footsteps.mp3"));
        randomEffects.add(new GreenfootSound("sounds/clock.mp3"));
        randomEffects.add(new GreenfootSound("sounds/metallic-sweep.mp3"));
        randomEffects.add(new GreenfootSound("sounds/echoing-noises.mp3"));
        randomEffects.add(new GreenfootSound("sounds/music-box.mp3"));
        randomEffects.add(new GreenfootSound("sounds/demon.mp3"));

        for (GreenfootSound sound : ambience)
        {
            sound.setVolume(20);
        }

        for (GreenfootSound sound : breathing)
        {
            sound.setVolume(50);
        }

        collectSound.setVolume(30);
        walkingSound.setVolume(30);

        camera = new Camera3D(0, 25, 0, 0, 0, 0, getWidth(), getHeight(), 90);
        setCamera(camera);

        Material floorMaterial = new TiledTextureMaterial(new GreenfootImage("images/textures/black-and-white-tile.jpg"), 20,
                0.2, 0.4, 0.4, 0.4, 0.1);
        Material cubeMaterial = new TextureMaterial(new GreenfootImage("images/textures/stickman.jpg"), 0.2, 0.4, 0.4, 0.4, 0.1);

        // RectPrism for the floor
        addObject(new RectPrism(0, 0, 0, 0, 0, 0, floorMaterial, 500, 1, 500));

        // Get random coordinates at least 100 units away from the camera
        int randomX, randomZ;
        Vec3 randomVec;
        do {
            randomX = Greenfoot.getRandomNumber(400) - 200;
            randomZ = Greenfoot.getRandomNumber(400) - 200;
            randomVec = new Vec3(randomX, 0, randomZ);
        } while (randomVec.subtract(new Vec3(camera.x, camera.y, camera.z)).magnitude() < 100);

        cube = new Cube(randomX, 20, randomZ, 0, 45, 0, cubeMaterial, 15);
        addObject(cube);

        spawnOrb();

        light = new Light(0, 60, 0, Color.WHITE, 0.5);
        addLight(light);
    }

    public void addObject(Actor3D object) {
        actors.add(object);
        object.world = this;
    }

    public void removeObject(Actor3D object)
    {
        actors.remove(object);
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public void removeLight(Light light) {
        lights.remove(light);
    }

    public void setCamera(Camera3D camera) {
        this.camera = camera;
    }

    public void jumpscare()
    {
        if (walkingSound.isPlaying())
        {
            walkingSound.stop();
        }

        GreenfootSound jumpscareSound = new GreenfootSound("sounds/jumpscare.mp3");
        jumpscareSound.play();

        setBackground("images/jumpscare.jpg");

        Greenfoot.delay(1000);

        while (jumpscareSound.isPlaying())
        {
            // Wait for the jumpscare sound to finish
        }
    }

    public void generateParticle() {
        double x, y, z;
        do
        {
            x = Greenfoot.getRandomNumber(500) - 250;
            y = Greenfoot.getRandomNumber(500) - 250;
            z = Greenfoot.getRandomNumber(500) - 250;

        } while (!camera.isVisible(x, y, z));

        // Particle falls down with a random velocity
        Vec3 velocity = new Vec3(Greenfoot.getRandomNumber(10) - 5, Greenfoot.getRandomNumber(10) - 10, Greenfoot.getRandomNumber(10) - 5);
        double lifespan = Greenfoot.getRandomNumber(20) + 10;
        Material material = new Material(Color.WHITE, 0.2, 0.4, 0.4, 0.4, 0.1);
        Particle particle = new Particle(x, y, z, velocity, lifespan, material);
        addObject(particle);
        particles.add(particle);
    }

    public void spawnOrb()
    {
        int randomX = Greenfoot.getRandomNumber(400) - 200;
        int randomZ = Greenfoot.getRandomNumber(400) - 200;

        addObject(new Sphere(randomX, 25, randomZ, new Material(new Color(0, 213, 255), 0.2, 0.4, 0.4, 0.4, 0.1), 5));
    }


    @Override
    public void act()
    {
        // Remove instructions
        showText("", 300, 10);
        showText("", 300, 30);
        showText("", 300, 50);
        showText("", 300, 70);

        ArrayList<Actor3D> tempActors = new ArrayList<Actor3D>(actors);

        if (!DISABLE_PARTICLES)
        {
            // Update particles
            ArrayList<Particle> tempParticles = new ArrayList<>(particles);
            for (Particle particle : tempParticles)
            {
                particle.act();

                // Remove particle if it's not visible
                if (!camera.isVisible(new Vec3(particle.x, particle.y, particle.z)))
                {
                    removeObject(particle);
                    particles.remove(particle);
                }
            }

            // Refresh particles
            while (particles.size() < 20)
            {
                generateParticle();
            }
        }
        else
        {
            for (Particle particle : particles)
            {
                removeObject(particle);
            }

            particles.clear();
        }

        int width = getWidth();
        int height = getHeight();

        GreenfootImage frame = new GreenfootImage(width, height);
        frame.setColor(Color.WHITE);
        frame.fill();

        // Trace rays
        // Greenfoot is not thread-safe, but I'm using threads anyway because it makes the game run faster

        Future<?>[] futures = new Future<?>[width];
        for (int x = 0; x < width; x++) {
            final int finalX = x;
            futures[x] = executor.submit(() ->
            {
                for (int y = 0; y < height; y++)
                {
                    double red = 0;
                    double green = 0;
                    double blue = 0;

                    // Generate a ray for the current pixel
                    Ray ray = camera.generateRay(finalX, y);

                    if (DRAW_LIGHT_POSITIONS)
                    {
                        Material material = new Material(Color.YELLOW, 1.0, 0.0, 0.0, 0.0, 0.0);
                        for (Light light : lights)
                        {
                            tempActors.add(new Sphere(light.x, light.y, light.z, material, 4));
                        }
                    }

                    // Check if the ray intersects with any of the actors and save the closest intersection
                    Actor3D closestActor = null;
                    double closestDistance = Double.MAX_VALUE;
                    Vec3 closestIntersection = null;
                    for (Actor3D actor : tempActors)
                    {
                        Vec3 intersection = actor.getCollision(ray);
                        if (intersection != null)
                        {
                            Vec3 rayToIntersection = intersection.subtract(ray.getOrigin());
                            if (ray.getDirection().dot(rayToIntersection) > 0)
                            { // Check if the intersection point is in front of the camera
                                double distance = rayToIntersection.magnitude();
                                if (distance < closestDistance)
                                {
                                    closestDistance = distance;
                                    closestActor = actor;
                                    closestIntersection = intersection;
                                }
                            }
                        }
                    }

                    if (DEBUG_NORMALS)
                    {
                        if (closestActor != null)
                        {
                            Vec3 normal = closestActor.getNormal(closestIntersection);
                            red = (normal.x + 1) * 127;
                            green = (normal.y + 1) * 127;
                            blue = (normal.z + 1) * 127;

                            // Clamp color values to the range [0, 255]
                            red = Math.min(255, red);
                            green = Math.min(255, green);
                            blue = Math.min(255, blue);

                            Color finalColor = new Color((int) red, (int) green, (int) blue);
                            frame.setColorAt(finalX, y, finalColor);

                            continue;
                        }
                    } else if (DEBUG_UV)
                    {
                        // Draw a gradient texture on each object for debugging purposes
                        if (closestActor != null)
                        {
                            // Get the UV coordinates of the intersection point
                            Vec3 uv = closestActor.getUV(closestIntersection);

                            // Map the UV coordinates to the range [0, 255]
                            red = uv.x * 255;
                            green = uv.y * 255;

                            // Clamp color values to the range [0, 255]
                            red = Math.min(255, red);
                            green = Math.min(255, green);

                            Color finalColor = new Color((int) red, (int) green, 0);
                            frame.setColorAt(finalX, y, finalColor);

                            continue;
                        }
                    }

                    // is Actor a particle?
                    if (closestActor instanceof Particle)
                    {
                        red = 255;
                        green = 255;
                        blue = 255;
                    } else if (closestActor != null)
                    {
                        Vec3 uv = closestActor.getUV(closestIntersection);
                        Color color = closestActor.material.getColor(uv.x, uv.y);

                        red = color.getRed();
                        green = color.getGreen();
                        blue = color.getBlue();

                        Vec3 normal = closestActor.getNormal(closestIntersection).normalize(); // Make sure the normal is normalized

                        for (Light light : lights)
                        {
                            Vec3 lightPosition = new Vec3(light.x, light.y, light.z);
                            Vec3 lightDirection = lightPosition.subtract(closestIntersection).normalize(); // Normalize the light direction
                            double distance = lightPosition.subtract(closestIntersection).magnitude();
                            double attenuation = 1.0 / (1.0 + 0.005 * distance + 0.0005 * distance * distance); // Calculate attenuation

                            if (DISABLE_FANCY_VISUALS)
                            {
                                attenuation = 1.0;
                            }

                            // Diffuse lighting: Only apply light if dot product is positive
                            double diffuse = Math.max(0.0, lightDirection.dot(normal));

                            // Specular lighting
                            Vec3 reflectionDirection = lightDirection.subtract(normal.multiply(2 * lightDirection.dot(normal))).normalize();
                            Vec3 viewDirection = ray.getDirection().multiply(-1);
                            double specular = Math.pow(Math.max(0.0, viewDirection.dot(reflectionDirection)), closestActor.material.shininess);

                            if (DEBUG_LIGHTING)
                            {
                                red = diffuse * 255;
                                green = specular * 255;
                                blue = 0;

                                // Clamp color values to the range [0, 255]
                                red = Math.min(255, red);
                                green = Math.min(255, green);

                                continue;
                            }

                            // Ambient + Diffuse + Specular Lighting Calculation
                            red += light.color.getRed() * (BASE_AMBIENT + closestActor.material.ambient + closestActor.material.diffuse * diffuse + closestActor.material.specular * specular);
                            green += light.color.getGreen() * (BASE_AMBIENT + closestActor.material.ambient + closestActor.material.diffuse * diffuse + closestActor.material.specular * specular);
                            blue += light.color.getBlue() * (BASE_AMBIENT + closestActor.material.ambient + closestActor.material.diffuse * diffuse + closestActor.material.specular * specular);

                            // Apply attenuation
                            red *= light.intensity * attenuation;
                            green *= light.intensity * attenuation;
                            blue *= light.intensity * attenuation;
                        }
                    }

                    // Clamp color values to the range [0, 255]
                    red = Math.min(255, red);
                    green = Math.min(255, green);
                    blue = Math.min(255, blue);

                    Color finalColor = new Color((int) red, (int) green, (int) blue);
                    frame.setColorAt(finalX, y, finalColor);
                }
            });
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // VHS-style effect

        if (!DISABLE_FANCY_VISUALS && !DISABLE_POST_PROCESSING)
        {
            GreenfootImage temp = new GreenfootImage(frame);
            frame.clear();

            int blockSize = 5;  // Number of rows in each "glitch" block

            for (int y = 0; y < height; y += blockSize) {
                if (Greenfoot.getRandomNumber(100) < 8) continue;  // Skip some rows to create the glitch effect
                int displacement = Greenfoot.getRandomNumber(6) - 3;  // Horizontal shift amount for the entire block
                int redShift = Greenfoot.getRandomNumber(4) - 2;  // Separate shift for the red channel
                int greenShift = Greenfoot.getRandomNumber(4) - 2;  // Separate shift for the green channel
                int blueShift = Greenfoot.getRandomNumber(4) - 2;  // Separate shift for the blue channel

                for (int x = 0; x < width; x++) {
                    for (int dy = 0; dy < blockSize; dy++) {  // Apply displacement to multiple rows
                        int newY = y + dy;
                        if (newY >= height) continue;  // Skip rows that are out of bounds

                        // Ensure x + displacement for each color is within the bounds
                        int newRedX = Math.min(Math.max(0, x + redShift + displacement), width - 1);
                        int newGreenX = Math.min(Math.max(0, x + greenShift + displacement), width - 1);
                        int newBlueX = Math.min(Math.max(0, x + blueShift + displacement), width - 1);

                        // Get the colors with separate displacements for each channel
                        int red = temp.getColorAt(newRedX, newY).getRed();
                        int green = temp.getColorAt(newGreenX, newY).getGreen();
                        int blue = temp.getColorAt(newBlueX, newY).getBlue();

                        // Add noise to each color channel
                        int noise = Greenfoot.getRandomNumber(20) - 10;
                        red = Math.min(255, Math.max(0, red + noise));
                        green = Math.min(255, Math.max(0, green + noise));
                        blue = Math.min(255, Math.max(0, blue + noise));

                        // Set the color at the current position
                        frame.setColorAt(x, newY, new Color(red, green, blue));
                    }
                }
            }
        }

        setBackground(frame);

        for (Actor3D actor : tempActors) {
            actor.act();
        }

        // Win screen
        if (score >= WIN_SCORE)
        {
            if (winTime == -1)
            {
                winTime = System.currentTimeMillis();
            }

            for (Actor3D actor : tempActors)
            {
                if (actor instanceof Sphere)
                {
                    removeObject(actor);
                }
            }

            if (!winMusic.isPlaying())
            {
                winMusic.playLoop();
            }

            cube.x = 0;
            cube.z = 0;
            cube.rotationY = 0;

            camera.x = 0;
            camera.z = -50;
            camera.rotationY = 0;

            light.x = 0;
            light.z = 0;

            // Fade in the light
            light.intensity = System.currentTimeMillis() - winTime < 5000 ? (System.currentTimeMillis() - winTime) / 5000.0 : 1.0;

            if (light.intensity > 0.5)
            {
                light.intensity = 0.5;
            }

            for (GreenfootSound sound : ambience)
            {
                sound.stop();
            }

            for (GreenfootSound sound : breathing)
            {
                sound.stop();
            }

            walkingSound.stop();

            showText("", 50, 30);
            showText("", 35, height - 20);
            showText("", 50, 50);

            showText("Press any key to continue", 300, 250);
            showText(winDialog[winDialogIndex], 300, 200);

            if (Greenfoot.getKey() != null && System.currentTimeMillis() - lastKeypress > 200 && System.currentTimeMillis() - winTime > 3000)
            {
                winDialogIndex++;
                lastKeypress = System.currentTimeMillis();
            }

            if (winDialogIndex >= winDialog.length)
            {
                showText("", 300, 250);
                showText("", 300, 200);

                winMusic.stop();

                jumpscare();

                Greenfoot.setWorld(new TitleScreen());
            }
        }
        else
        {
            showText("Score: " + score + "/" + WIN_SCORE, 50, 30);

            boolean ambiencePlaying = false;

            for (GreenfootSound sound : ambience)
            {
                if (sound.isPlaying())
                {
                    ambiencePlaying = true;
                }
            }

            if (!ambiencePlaying)
            {
                int randomAmbience = Greenfoot.getRandomNumber(ambience.size());
                ambience.get(randomAmbience).play();
            }

            runTime = (System.nanoTime() - startTime) / 1e9;

            frames++;
            long now = System.nanoTime();
            if (now - fpsTime >= 1e9)
            {
                showText("FPS: " + frames + " (" + (int) averageFPS + "avg)", 70, 50);
                fpsTime = now;
                frames = 0;
            }

            // Calculate average FPS over 2 seconds
            totalFrames++;
            if (now - fpsIntervalStartTime >= 2e9)
            {
                averageFPS = totalFrames / 2.0;
                totalFrames = 0;
                fpsIntervalStartTime = now;
            }

            long currentMillis = System.currentTimeMillis();
            deltaT = (currentMillis - lastMillis) / 1000.0;
            lastMillis = currentMillis;

            // Calculate X and Z movement based on camera rotation

            double dx = 0;
            double dz = 0;

            int speed = MOVEMENT_SPEED;

            if (stamina <= 0)
            {
                staminaCooldown = currentMillis;
            }

            boolean inStaminaCooldown = currentMillis - staminaCooldown < STAMINA_COOLDOWN_TIME;

            if (Greenfoot.isKeyDown("shift") && !inStaminaCooldown)
            {
                speed = SPRINT_SPEED;
                stamina -= 80 * deltaT;

                camera.focalLength = 120;
            } else if (stamina < MAX_STAMINA)
            {
                stamina += 40 * deltaT;

                camera.focalLength = 90;
            }

            // Draw stamina bar
            showText("Stamina", 35, height - 20);

            if (inStaminaCooldown)
            {
                frame.setColor(Color.RED);
                double barWidth = (stamina / MAX_STAMINA) * width;
                frame.fillRect(0, height - 10, (int) barWidth, 20);
            } else if (stamina > 1)
            {
                frame.setColor(Color.GREEN);
                double barWidth = (stamina / MAX_STAMINA) * width;
                frame.fillRect(0, height - 10, (int) barWidth, 20);
            }

            double rotationYRadians = Math.toRadians(camera.rotationY);

            if (Greenfoot.isKeyDown("w"))
            {
                dx += speed * Math.sin(rotationYRadians) * deltaT;
                dz += speed * Math.cos(rotationYRadians) * deltaT;
            }
            if (Greenfoot.isKeyDown("s"))
            {
                dx -= speed * Math.sin(rotationYRadians) * deltaT;
                dz -= speed * Math.cos(rotationYRadians) * deltaT;
            }
            if (Greenfoot.isKeyDown("a"))
            {
                dx -= speed * Math.cos(rotationYRadians) * deltaT;
                dz += speed * Math.sin(rotationYRadians) * deltaT;
            }
            if (Greenfoot.isKeyDown("d"))
            {
                dx += speed * Math.cos(rotationYRadians) * deltaT;
                dz -= speed * Math.sin(rotationYRadians) * deltaT;
            }

            camera.x += dx;
            camera.z += dz;

            // Walking sound

            if (dx != 0 || dz != 0)
            {
                if (!walkingSound.isPlaying())
                {
                    walkingSound.playLoop();
                }
            } else
            {
                walkingSound.stop();
            }

            if (FREE_CAMERA)
            {
                if (Greenfoot.isKeyDown("q"))
                {
                    camera.y -= speed * deltaT;
                }
                if (Greenfoot.isKeyDown("e"))
                {
                    camera.y += speed * deltaT;
                }

                if (Greenfoot.isKeyDown("up"))
                {
                    camera.rotationX -= ROTATION_SPEED * deltaT;
                }
                if (Greenfoot.isKeyDown("down"))
                {
                    camera.rotationX += ROTATION_SPEED * deltaT;
                }
            }
            // Game camera
            if (Greenfoot.isKeyDown("left"))
            {
                camera.rotationY -= ROTATION_SPEED * deltaT;
            }
            if (Greenfoot.isKeyDown("right"))
            {
                camera.rotationY += ROTATION_SPEED * deltaT;
            }

            if (Greenfoot.isKeyDown("f"))
            {
                FREE_CAMERA = !FREE_CAMERA;
                showText("FREE CAMERA", 70, 100);
            }

            if (Greenfoot.isKeyDown("n"))
            {
                DEBUG_NORMALS = !DEBUG_NORMALS;
                showText("DEBUG NORMALS", 90, 150);
            }

            if (Greenfoot.isKeyDown("u"))
            {
                DEBUG_UV = !DEBUG_UV;
                showText("DEBUG UV", 70, 200);
            }

            if (Greenfoot.isKeyDown("l"))
            {
                DRAW_LIGHT_POSITIONS = !DRAW_LIGHT_POSITIONS;
                showText("DRAW LIGHT POSITIONS", 120, 250);
            }

            if (Greenfoot.isKeyDown("o"))
            {
                DEBUG_LIGHTING = !DEBUG_LIGHTING;
                showText("DEBUG LIGHTING", 80, 300);
            }

            if (Greenfoot.isKeyDown("c"))
            {
                DEBUG_CUBE_POSITION = !DEBUG_CUBE_POSITION;
            }

            if (Greenfoot.isKeyDown("p"))
            {
                DISABLE_FANCY_VISUALS = !DISABLE_FANCY_VISUALS;
                showText("DISABLE FANCY VISUALS", 120, 350);
            }

            if (Greenfoot.isKeyDown("g"))
            {
                GOD_MODE = !GOD_MODE;
                showText("GOD MODE", 540, 30);
            }

            if (!FREE_CAMERA)
            {
                camera.y = 25;
                camera.rotationX = 0;
                showText("", 70, 100);

                if (camera.x > POSITIVE_BOUNDS.x)
                {
                    camera.x = POSITIVE_BOUNDS.x;
                }

                if (camera.x < NEGATIVE_BOUNDS.x)
                {
                    camera.x = NEGATIVE_BOUNDS.x;
                }

                if (camera.z > POSITIVE_BOUNDS.z)
                {
                    camera.z = POSITIVE_BOUNDS.z;
                }

                if (camera.z < NEGATIVE_BOUNDS.z)
                {
                    camera.z = NEGATIVE_BOUNDS.z;
                }
            }

            if (!DEBUG_NORMALS)
            {
                showText("", 90, 150);
            }

            if (!DEBUG_UV)
            {
                showText("", 70, 200);
            }

            if (!DRAW_LIGHT_POSITIONS)
            {
                showText("", 120, 250);
            }

            if (!DEBUG_LIGHTING)
            {
                showText("", 80, 300);
            }

            if (!DEBUG_CUBE_POSITION)
            {
                showText("", 300, 50);
                showText("", 300, 70);
            }

            if (!DISABLE_FANCY_VISUALS)
            {
                showText("", 70, 350);
            }

            if (!GOD_MODE)
            {
                showText("", 540, 30);
            }

            if (Greenfoot.isKeyDown("escape"))
            {
                Greenfoot.stop();
            }

            light.x = camera.x;
            light.y = camera.y;
            light.z = camera.z;

            // Check if the player has collided with a sphere
            for (Actor3D actor : tempActors)
            {
                if (actor instanceof Sphere)
                {
                    Vec3 center = new Vec3(actor.x, actor.y, actor.z);
                    Vec3 player = new Vec3(camera.x, camera.y, camera.z);

                    if (center.subtract(player).magnitude() < ((Sphere) actor).radius)
                    {
                        score++;
                        removeObject(actor);
                        spawnOrb();

                        if (collectSound.isPlaying())
                        {
                            collectSound.stop();
                        }
                        collectSound.play();

                        cubeSpeed += 1.5;
                    }
                }
            }

            // Check if the player has collided with a cube
            if (!GOD_MODE)
            {
                Vec3 player = new Vec3(camera.x, camera.y, camera.z);

                boolean pointInCube = player.x > cube.x - cube.size / 2 && player.x < cube.x + cube.size / 2 &&
                        player.y > cube.y - cube.size / 2 && player.y < cube.y + cube.size / 2 &&
                        player.z > cube.z - cube.size / 2 && player.z < cube.z + cube.size / 2;

                if (pointInCube && score < WIN_SCORE)
                {
                    showText("You have lost!", 300, 200);

                    jumpscare();

                    for (GreenfootSound sound : ambience)
                    {
                        sound.stop();
                    }

                    for (GreenfootSound sound : breathing)
                    {
                        sound.stop();
                    }

                    walkingSound.stop();

                    Greenfoot.stop();
                }
            }

            // Teleport the cube closer to the player every once in a while
            double cubeDistance = new Vec3(cube.x, cube.y, cube.z).subtract(new Vec3(camera.x, camera.y, camera.z)).magnitude();

            if (System.currentTimeMillis() - lastCubeTeleport > 15000 && Greenfoot.getRandomNumber(100) < 1 && cubeDistance > 150)
            {
                int randomX, randomZ;
                Vec3 randomVec;
                do
                {
                    randomX = Greenfoot.getRandomNumber(400) - 200;
                    randomZ = Greenfoot.getRandomNumber(400) - 200;
                    randomVec = new Vec3(randomX, 0, randomZ);
                    cubeDistance = randomVec.subtract(new Vec3(camera.x, camera.y, camera.z)).magnitude();
                } while ((cubeDistance < 100 || cubeDistance > 150) && !camera.isVisible(randomX, 20, randomZ));

                cube.x = randomX;
                cube.z = randomZ;

                lastCubeTeleport = System.currentTimeMillis();

                GreenfootSound teleportSound = new GreenfootSound("sounds/teleport.mp3");
                teleportSound.play();
            }

            // Make the cube chase the player
            Vec3 direction = new Vec3(camera.x, camera.y, camera.z).subtract(new Vec3(cube.x, cube.y, cube.z)).normalize();

            cube.x += direction.x * cubeSpeed * deltaT;
            cube.z += direction.z * cubeSpeed * deltaT;

            cube.rotationY = 360 - Math.toDegrees(Math.atan2(camera.x - cube.x, camera.z - cube.z));

            // Calculate the distance between the player and the cube
            double distance = (new Vec3(camera.x, camera.y, camera.z)).subtract(new Vec3(cube.x, cube.y, cube.z)).magnitude();

            if (DEBUG_CUBE_POSITION)
            {
                showText("Cube X: " + cube.x + " Z: " + cube.z, 300, 50);
                showText("Cube distance: " + distance, 300, 70);
            }

            // Calculate the delay for the heartbeat sound based on the distance
            long heartbeatDelay = 20 * (long) distance;

            heartbeatDelay = Math.max(heartbeatDelay, 400);

            // Play heartbeat sound when close to the cube, gets faster as you get closer
            if (System.currentTimeMillis() - lastHeartbeat > heartbeatDelay)
            {
                if (distance < 150)
                {
                    GreenfootSound heartbeat = new GreenfootSound("sounds/heartbeat.mp3");
                    heartbeat.play();
                }

                lastHeartbeat = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - lastBreath > 10000 && (Greenfoot.getRandomNumber(100) < 2 || Greenfoot.isKeyDown("shift")))
            {
                boolean isPlaying = false;
                for (GreenfootSound sound : breathing)
                {
                    if (sound.isPlaying())
                    {
                        isPlaying = true;
                    }
                }

                if (!isPlaying)
                {
                    int randomBreath = Greenfoot.getRandomNumber(breathing.size());
                    breathing.get(randomBreath).play();
                    lastBreath = System.currentTimeMillis();
                }
            }

            if (System.currentTimeMillis() - lastRandomEffect > 20000 && Greenfoot.getRandomNumber(100) < 1)
            {
                boolean isPlaying = false;
                for (GreenfootSound sound : randomEffects)
                {
                    if (sound.isPlaying())
                    {
                        isPlaying = true;
                    }
                }

                if (!isPlaying)
                {
                    int randomEffect = Greenfoot.getRandomNumber(randomEffects.size());
                    randomEffects.get(randomEffect).play();
                    lastRandomEffect = System.currentTimeMillis();
                }
            }

            if (Greenfoot.isKeyDown("r"))
            {
                System.out.println("test");
            }

            if (score >= WIN_SCORE && winTime == -1)
            {
                winTime = System.currentTimeMillis();
            }

            long timeSinceOptimization = System.currentTimeMillis() - lastOptimizationAttempt;
            if (averageFPS < 20 && averageFPS != 0 && timeSinceOptimization > 5000)
            {
                if (!DISABLE_PARTICLES)
                {
                    showText("Low FPS detected. Disabling particles...", 300, 11);
                    DISABLE_PARTICLES = true;
                    lastOptimizationAttempt = System.currentTimeMillis();
                }
                else if (!DISABLE_POST_PROCESSING)
                {
                    showText("Low FPS detected. Disabling post-processing...", 300, 11);
                    DISABLE_POST_PROCESSING = true;
                    lastOptimizationAttempt = System.currentTimeMillis();
                }
            }

            if (timeSinceOptimization > 5000)
            {
                showText("", 300, 11);
            }
        }
    }
}
