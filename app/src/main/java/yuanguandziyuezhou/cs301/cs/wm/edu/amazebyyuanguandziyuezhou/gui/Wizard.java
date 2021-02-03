package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;


import android.util.Log;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Distance;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.CardinalDirection;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Direction;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Turn;

/**
 * Class: Wizard
 *
 * Responsibilities: This class is responsible for implementing wizard algorithm
 * which automatically drives the robot to the exit. The wizard algorithm depends on
 * a method in mazecontainer class, which will tells the robot which of its neighbor
 * cell closer to the exit. Robot will find the exit based on the information provided.
 *
 * Collaborators: BasicRobot, MazeContainer, Controller, CardinalDirection
 *
 * @author Yuan Gu/Ziyue Zhou
 */

public class Wizard implements RobotDriver{

    private BasicRobot robot;
    private Distance dist;
    private int w;
    private int h;
    private float initial;

    /**
     * This constructor will initialize a wizard driver
     * with initial battery level setting to 3000 and robot to null
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    public Wizard() {
        robot = null;
        initial = 3000;
        System.out.println("Wizard driver algorithm");
    }

    /**
     * This constructor will set the robot
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void setRobot(Robot r) {
        // TODO Auto-generated method stub
        robot = (BasicRobot)(r);
    }

    /**
     * This method will set the maze dimension (unused)
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void setDimensions(int width, int height) {
        // TODO Auto-generated method stub
        if (width >= 0 && height >= 0) {
            w = width;
            h = height;
        }
    }

    /**
     * This method will set the distance (unused)
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void setDistance(Distance distance) {
        // TODO Auto-generated method stub
        if (distance != null) {
            dist = distance;
        }
    }

    /**
     * This method will automatically drives the robot to exit based on information
     * the maze configuration provides.
     * Pseudocode:
     * while the robot is not at exit, get its neighbor position closer to exit by calling
     * getNeighborCloserToExit from MazeContainer class. Then turn the robot to the
     * neighbor position. Once the robot is at exit, drive it out and return true
     * If the robot is out of energy, return false
     *
     * @exception throw exception if getNeighborCloserToExit returns an invalid position
     * @return true if the robot drives to exit, false if the robot out of energy
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void drive2Exit() throws Exception {
        Log.v("Wizard", "driving to exit");
        if(!robot.isAtExit()) {
            Log.v("Wizard", "driving0");
            if (robot.hasStopped()) {
                throw new Exception("Interrupted");
            }

            CardinalDirection cd = robot.getCurrentDirection();
            int x = robot.getCurrentPosition()[0];
            int y = robot.getCurrentPosition()[1];
            int x1 = robot.controller.getMazeConfiguration().getNeighborCloserToExit(x, y)[0];
            int y1 = robot.controller.getMazeConfiguration().getNeighborCloserToExit(x, y)[1];
            int xd = x1 - x;
            int yd = y1 - y;
            if (xd == 0) {
                if (yd == 1) {
                    turn2Direction(cd, CardinalDirection.South);
                    robot.move(1, false);
                }
                if (yd == -1) {
                    turn2Direction(cd, CardinalDirection.North);
                    robot.move(1, false);
                }
            }
            if (yd == 0) {
                if (xd == 1) {
                    turn2Direction(cd, CardinalDirection.East);
                    robot.move(1, false);
                }
                if (xd == -1) {

                    turn2Direction(cd, CardinalDirection.West);
                    robot.move(1, false);
                }
            }
        }
        if(robot.isAtExit()){
            robot.lastStepMove();}
            Log.v("aaaaaa", "last step move?");
        if(robot.hasStopped()|| robot.controller.isOutside(robot.controller.getCurrentPosition()[0], robot.controller.getCurrentPosition()[1])){
            throw new Exception("Interrupted");}
    }


    /**
     * This private method will help the robot turns to the right direction.
     * This will be called when the neighbor cell closer to exit is found, and
     * this method will navigate the robot facing towards the neighbor cell
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    private void turn2Direction(CardinalDirection cd, CardinalDirection rightcd) {
        Log.v("Wizard", "turned");
        if (rightcd == cd.oppositeDirection()) {
            robot.rotate(Turn.AROUND);
        }
        if (rightcd == cd.rotateCounterClockwise()) {
            robot.rotate(Turn.RIGHT);
        }
        if (rightcd == cd.rotateClockwise()) {
            robot.rotate(Turn.LEFT);
        }
    }

    /**
     * Get energy consumed
     *
     * @return energy consumed
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public float getEnergyConsumption() {
        // TODO Auto-generated method stub
        return initial - robot.getBatteryLevel();
    }

    /**
     * Get path length traveled
     *
     * @return distance traveled
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public int getPathLength() {
        // TODO Auto-generated method stub
        return robot.getOdometerReading();
    }

}

