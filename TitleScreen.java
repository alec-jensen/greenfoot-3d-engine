import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.awt.*;

/**
 * Write a description of class TitleScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TitleScreen extends World
{

    /**
     * Constructor for objects of class TitleScreen.
     * 
     */
    public TitleScreen()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1);

        showText("Press 'Enter' to start", getWidth() / 2, getHeight() / 2);
        showText("A stickman is chasing you. Eat as many blue spheres as you can.", 300, 10);
        showText("Use WASD to move and the arrow keys to rotate the camera.", 300, 30);
        showText("Use Shift to sprint. Sprinting consumes stamina.", 300, 50);
        showText("This game is best experienced with sound on.", 300, 70);
        showText("Game created by Alec Jensen", getWidth() / 2, getHeight() - 20);
        prepare();
    }

    @Override
    public void act()
    {
        if (Greenfoot.isKeyDown("enter")) {
            try
            {
                Greenfoot.setWorld(new World3D());
            } catch (AWTException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
    }
}
