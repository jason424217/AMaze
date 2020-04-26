package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;

import android.util.Log;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Distance;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Direction;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.CardinalDirection;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Turn;

/**
 * Class: WallFollower
 *
 * Responsibilities: This class is responsible for implementing wall follower algorithm
 * which automatically drives the robot to the exit. The robot will have a distance
 * sensor at the front and at one side (here: pick left) to perform. It follows the
 * wall on its left hand side.
 *
 * Collaborators: BasicRobot, Controller, CardinalDirection
 *
 * @author Yuan Gu/Ziyue Zhou
 */

public class WallFollower implements RobotDriver{

    BasicRobot robot;
    Distance dist;
    int w;
    int h;
    float initial;

    /**
     * This constructor will initialize a wall follower driver
     * with initial battery level setting to 3000 and robot to null
     * @author Yuan Gu/Ziyue Zhou
     */
    public WallFollower() {
        robot = null;
        initial = 3000;
        System.out.println("Wall follower driver algorithm");
    }

    /**
     * This method will set the robot
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
     * This method will drives the robot to exit by following the left hand side
     * wall.
     * Pseudocode:
     * while the robot is not at exit, if there is no wall on the left, turn left
     * and move forward; if there is wall on the left and no wall in the front,
     * move forward; if there are walls on the left and in the front, turn right
     * Once the robot is at exit, drive it out and return true. If the robot runs out
     * of energy, return false
     *
     * @exception throw exception if the robot is not supported by a sensor given
     * a direction
     * @return true if the robot successfully reaches the exit, false otherwise
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void drive2Exit() throws Exception {
        Log.v("WallFollower", "driving to exit");

        if (!robot.isAtExit()) {

            if (robot.hasStopped()) {
                throw new Exception("Interrupted");
            }

            if (robot.distanceToObstacle(Direction.LEFT)>0) {
                Log.v("WallFollower", "turn and move");
                robot.rotate(Turn.LEFT);
                robot.move(1, false);
            }
            else if (robot.distanceToObstacle(Direction.LEFT)==0) {
                if (robot.distanceToObstacle(Direction.FORWARD)>0) {
                    Log.v("WallFollower", "move");
                    robot.move(1, false);
                }
                else if (robot.distanceToObstacle(Direction.FORWARD)==0) {
                    Log.v("WallFollower", "turn");
                    robot.rotate(Turn.RIGHT);
                }
            }
        }

        if(robot.isAtExit()){
            robot.lastStepMove();}
        Log.v("WallFollower", "last step move?");
        if(robot.hasStopped()|| robot.controller.isOutside(robot.controller.getCurrentPosition()[0], robot.controller.getCurrentPosition()[1])){
            throw new Exception("Interrupted");}
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

