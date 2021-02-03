package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.R;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.CardinalDirection;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Cells;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeBuilder;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeConfiguration;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Order;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Constants;



/**
 * Class handles the user interaction.
 * It implements an automaton with states for the different stages of the game.
 * It has state-dependent behavior that controls the display and reacts to key board input from a user.
 * At this point user keyboard input is first dealt with a key listener (SimpleKeyListener)
 * and then handed over to a Controller object by way of the keyDown method.
 *
 * The class is part of a state pattern. It has a state object to implement
 * state-dependent behavior.
 * The automaton currently has 4 states.
 * StateTitle: the starting state where the user can pick the skill-level
 * StateGenerating: the state in which the factory computes the maze to play
 * and the screen shows a progress bar.
 * StatePlaying: the state in which the user plays the game and
 * the screen shows the first person view and the map view.
 * StateWinning: the finish screen that shows the winning message.
 * The class provides a specific method for each possible state transition,
 * for example switchFromTitleToGenerating contains code to start the maze
 * generation.
 *
 * This code is refactored code from Maze.java by Paul Falstad,
 * www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 *
 * @author Peter Kemper
 */
public class Controller {
    /**
     * The filename is optional, may be null, and tells
     * if a maze is loaded from this file and not generated.
     */
    String fileName;
    /**
     * The panel is used to draw on the screen for the UI.
     * It can be set to null for dry-running the controller
     * for testing purposes but otherwise panel is never null.
     */
    MazePanel mazePanel;
    /**
     * The builder algorithm to use for generating a maze.
     */
    Order.Builder builder;
    /**
     * Specifies if the maze is perfect, i.e., it has
     * no loops, which is guaranteed by the absence of
     * rooms and the way the generation algorithms work.
     */
    boolean perfect;
    String drivername;

    FirstPersonDrawer firstPersonView;
    MazeConfiguration mazeConfiguration;
    Cells seenCells;
    MapDrawer mapView;

    private boolean showSolution;
    private boolean showMap;
    private boolean showWalls;

    int px, py; // current position on maze grid (x,y)
    int dx, dy;  // current direction
    int walkStep;// counter for intermediate steps within a single step forward or backward
    int angle; // current viewing angle, east == 0 degrees


    public Controller() {
        Log.v("Controller", "controller created");
        fileName = null;
        builder = Order.Builder.DFS; // default
        perfect = false; // default
        showSolution = false;
        showMap = false;
        showWalls = false;
        walkStep = 0; // counts incremental steps during move/rotate operation
        dx = 1;
        dy = 0;
        px = StaticData.getMC().getStartingPosition()[0];
        py = StaticData.getMC().getStartingPosition()[1];


        seenCells = new Cells(StaticData.getMC().getWidth()+1,StaticData.getMC().getHeight()+1) ;

        firstPersonView = new FirstPersonDrawer(Constants.VIEW_WIDTH,
                Constants.VIEW_HEIGHT, Constants.MAP_UNIT,
                Constants.STEP_SIZE, seenCells, StaticData.getMC().getRootnode()) ;
        mapView = new MapDrawer(seenCells, 15, StaticData.getMC());
    }

    public void setBuilder(Order.Builder builder) {
        this.builder = builder;
    }
    /**
     * Internal method to set the current position, the direction
     * and the viewing direction to values consistent with the
     * given maze.
     */
    private void setPositionDirectionViewingDirection() {
        // obtain starting position
        int[] start = StaticData.getMC().getStartingPosition() ;
        setCurrentPosition(start[0],start[1]) ;
        // set current view direction and angle
        angle = 0; // angle matches with east direction,
        // hidden consistency constraint!
        setDirectionToMatchCurrentAngle();
        // initial direction is east, check this for sanity:
    }
    public void setDrivername(String s){
        this.drivername = s;
    }

    public void setPerfect(boolean isPerfect) {
        this.perfect = isPerfect;
    }

    public void setPanel(MazePanel panell) {
        mazePanel = panell;
    }

    public MazePanel getPanel() {
        return mazePanel;
    }

    /**
     * Turns of graphics to dry-run controller for testing purposes.
     * This is irreversible.
     */
    public void turnOffGraphics() {
        mazePanel = null;
    }

    //// Extension in preparation for Project 3: robot and robot driver //////
    /**
     * The robot that interacts with the controller starting from P3
     */
    Robot robot;
    /**
     * The driver that interacts with the robot starting from P3
     */
    RobotDriver driver;

    /**
     * Sets the robot and robot driver
     *
     * @param robot
     * @param robotdriver
     */
    public void setRobotAndDriver(Robot robot, RobotDriver robotdriver) {
        Log.v("Controller", "robot and driver set");
        this.robot = robot;
        driver = robotdriver;

    }

    public void setMazeConfiguration(MazeConfiguration config) {
        mazeConfiguration = config;
    }

    /**
     * Draws the current content on panel to show it on screen.
     */
    protected void draw() {
        Log.v("Controller", "drawn");

        firstPersonView.draw(mazePanel, px, py, walkStep, angle);
        if (showWalls) {
            mapView.draw(mazePanel, px, py, angle, walkStep,
                    showMap, showSolution);
            Log.v("yyyyy", "draw!");
        }
        mazePanel.setcolor(255, 255, 255);
        mazePanel.fillrect(0,0,100,1228);
        // update the screen with the buffer graphics
        mazePanel.update();
    }


    /**
     * @return the robot, may be null
     */
    public Robot getRobot() {
        return robot;
    }

    /**
     * @return the driver, may be null
     */
    public RobotDriver getDriver() {
        return driver;
    }

    public MazeConfiguration getMazeConfiguration() {
        return StaticData.getMC();
    }

    public int[] getCurrentPosition() {
        int[] result = new int[2];
        result[0] = px;
        result[1] = py;
        return result;
    }

    protected CardinalDirection getCurrentDirection() {
        return CardinalDirection.getDirection(dx, dy);
    }


    void setCurrentPosition(int x, int y) {
        px = x;
        py = y;
    }

    void setCurrentDirection(int x, int y) {
        dx = x;
        dy = y;
    }

    public boolean checkMove(int dir) {
        CardinalDirection cd = null;
        switch (dir) {
            case 1: // forward
                cd = getCurrentDirection();
                break;
            case -1: // backward
                cd = getCurrentDirection().oppositeDirection();
                break;
            default:
                throw new RuntimeException("Unexpected direction value: " + dir);
        }
        return !StaticData.getMC().hasWall(px, py, cd);
    }

    synchronized public void walk(int dir) {
        // check if there is a wall in the way
        if (!checkMove(dir))
            return;
        // walkStep is a parameter of FirstPersonDrawer.draw()
        // it is used there for scaling steps
        // so walkStep is implicitly used in slowedDownRedraw
        // which triggers the draw operation in
        // FirstPersonDrawer and MapDrawer
        for (int step = 0; step != 4; step++) {
            walkStep += dir;
            slowedDownRedraw();
        }
        setCurrentPosition(px + dir * dx, py + dir * dy);
        walkStep = 0; // reset counter for next time
        //logPosition(); // debugging
        Log.v("Controller", "walked");
    }

    /**
     * Performs a rotation with 4 intermediate views,
     * updates the screen and the internal direction
     *
     * @param dir for current direction, values are either 1 or -1
     */
    synchronized public void rotate(int dir) {
        final int originalAngle = angle;
        final int steps = 4;

        for (int i = 0; i != steps; i++) {
            // add 1/4 of 90 degrees per step
            // if dir is -1 then subtract instead of addition
            angle = originalAngle + dir * (90 * (i + 1)) / steps;
            angle = (angle + 1800) % 360;
            // draw method is called and uses angle field for direction
            // information.
            slowedDownRedraw();
        }
        // update maze direction only after intermediate steps are done
        // because choice of direction values are more limited.
        setDirectionToMatchCurrentAngle();
        //logPosition(); // debugging
        Log.v("Controller", "rotated");
    }

    /**
     * Draws and waits. Used to obtain a smooth appearance for rotate and move operations
     */
    public void slowedDownRedraw() {
        draw();
        try {
            Thread.sleep(25);
        } catch (Exception e) {
            // may happen if thread is interrupted
            // no reason to do anything about it, ignore exception
        }
    }

    /**
     * Sets fields dx and dy to be consistent with
     * current setting of field angle.
     */
    public void setDirectionToMatchCurrentAngle() {
        setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle)));
    }

    final double radify(int x) {
        return x * Math.PI / 180;
    }

    /**
     * Adjusts the internal map scale setting for the map view.
     *
     * @param increment if true increase, otherwise decrease scale for map
     */
    public void adjustMapScale(boolean increment) {
        if (increment) {
            mapView.incrementMapScale();
        } else {
            mapView.decrementMapScale();
        }
    }

    boolean isInMapMode() {
        return showWalls;
    }

    boolean isInShowMazeMode() {
        return showMap;
    }

    boolean isInShowSolutionMode() {
        return showSolution;
    }

    public void setShowWalls(boolean wall) {
        showWalls = wall;
    }

    public void setShowMap(boolean map) {
        showMap = map;
    }

    public void setShowSolution(boolean solution) {
        showSolution = solution;
    }
    /**
     * Checks if the given position is outside the maze
     * @param x coordinate of position
     * @param y coordinate of position
     * @return true if position is outside, false otherwise
     */
    public boolean isOutside(int x, int y) {
        return !StaticData.getMC().isValidPosition(x, y) ;
    }

    public void keydown(Constants.UserInput key) {
        Log.v("Controller", "key inputed");

        switch (key) {
            case Up: // move forward
                walk(1);

                // check termination, did we leave the maze?
                Log.v("yyyyyy", "Read");
                if (isOutside(px, py)) {
                    Log.v("yyyyyy", "outside");
                    BasicRobot.stopped = true;
                    BasicRobot.winning = true;
                }
                break;

            case Left: // turn left
                rotate(1);
                break;
            case Right: // turn right
                rotate(-1);
                break;
            case Down: // move backward
                walk(-1);
                // check termination, did we leave the maze?
                if (isOutside(px, py)) {
                    BasicRobot.stopped = true;
                    BasicRobot.winning = true;
                }
        }



    }




}
