package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;


import android.util.Log;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Direction;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Turn;

/**
 * Class: Pledge
 *
 * Responsibilities: This class is responsible for implementing pledge algorithm
 * which automatically drives the robot to the exit. The algorithm is actually
 * a refined version of wall follower, which will be trapped inside a loop.
 *
 * Collaborators: BasicRobot, Controller, CardinalDirection, WallFollower
 *
 * @author Yuan Gu/Ziyue Zhou
 */

public class Pledge extends WallFollower implements RobotDriver{

    private int count;

    /**
     * This constructor will inherit all fields from the wall follower constructor
     * Initialize the new variable count to 0
     * @author Yuan Gu/Ziyue Zhou
     */
    public Pledge() {
        super();
        count = 0;
        System.out.println("Pledge algorithm");
    }

    /**
     * This Pledge driver is able to run around and leave an obstacle.
     *
     * Pseudocode:
     * while the robot is not at exit, if count is 0, ignore the wall follower
     * rule: if there is wall in the front, turn right; otherwise, move forward.
     * If the count is not 0, applies wall following rule. Whenever the robot turns
     * right, the count is incremented by 1; whenever the robot turns left, the count
     * is decremented by 1. If the robot is at exit, drive it out and return true.
     * If the robot runs out of energy, return false
     *
     * @exception throw exception if the robot is not supported by a sensor given
     * a direction
     * @return true if the robot successfully reaches the exit, false otherwise
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void drive2Exit() throws Exception {
        Log.v("Pledge", "driving to exit");

        if (!robot.isAtExit()) {
            if (robot.hasStopped()) {
                throw new Exception("Interrupted");
            }

            if (count == 0) {
                if (robot.distanceToObstacle(Direction.FORWARD)>0) {
                    robot.move(1, false);
                }
                else {
                    robot.rotate(Turn.RIGHT);
                    count += 1;
                }
            }
            else {
                wallFollower();
            }
        }

        if(robot.isAtExit()){
            robot.lastStepMove();}
        Log.v("Pledge", "last step move?");
        if(robot.hasStopped()|| robot.controller.isOutside(robot.controller.getCurrentPosition()[0], robot.controller.getCurrentPosition()[1])){
            throw new Exception("Interrupted");}
    }

    /**
     * This private method is a wall follower algorithm. It will be called while
     * count is not 0
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    private void wallFollower() {

        if (robot.distanceToObstacle(Direction.LEFT)>0) {
            robot.rotate(Turn.LEFT);
            count -= 1;
            robot.move(1, false);
        }
        else if (robot.distanceToObstacle(Direction.LEFT)==0) {
            if (robot.distanceToObstacle(Direction.FORWARD)>0) {
                robot.move(1, false);
            }
            else if (robot.distanceToObstacle(Direction.FORWARD)==0) {
                robot.rotate(Turn.RIGHT);
                count += 1;
            }
        }
    }
}
