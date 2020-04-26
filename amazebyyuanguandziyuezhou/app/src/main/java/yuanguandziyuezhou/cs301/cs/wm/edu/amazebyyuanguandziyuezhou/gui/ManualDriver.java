package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;

import android.util.Log;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Distance;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Constants.UserInput;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Turn;


/**
 *
 * Class Responsibility:
 * This class is responsible for implementing a driver class
 * that operates the robot API manually. I think there are two methods
 * listed in the interface are not unnecessary for this algorithm. But
 * in order to receive user input from the simple key listener, this class
 * have a manualKeyDown method, which will be called in simple key listener.
 * The four input(left, right, up, down) from user will manually guide the robot.
 *
 *  @author Yuan Gu/Ziyue Zhou
 */

public class ManualDriver implements RobotDriver{

    private BasicRobot robot;
    private float initial;
    private int w;
    private int h;
    private Distance dist = null;




    /**
     * This constructor will initialize a manual driver
     * with initial battery level setting to 3000 and robot to null
     *
     * @author Yuan Gu/Ziyue Zhou
     *
     */
    public ManualDriver(){
        robot = null;
        initial = 3000;
    }

    /**
     *
     * Assigns a robot platform to the driver.
     * The driver uses a robot to perform, this method provides it with this necessary information.
     * @param r robot to operate
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void setRobot(Robot r){
        this.robot = (BasicRobot)(r);
    }

    /**
     *
     * Provides the robot driver with information on the dimensions of the 2D maze
     * measured in the number of cells in each direction.
     * @param width of the maze
     * @param height of the maze
     * @precondition 0 <= width, 0 <= height of the maze.
     *
     * @author Yuan Gu/Ziyue Zhou
     */

    @Override
    public void setDimensions(int width, int height){
        // Unnecessary for the driver
        this.w = width;
        this.h = height;
    }
    /**
     *
     * Provides the robot driver with information on the distance to the exit.
     * Only some drivers such as the wizard rely on this information to find the exit.
     * @param distance gives the length of path from current position to the exit.
     * @precondition null != distance, a full functional distance object for the current maze.
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void setDistance(Distance distance){
        // Unnecessary for the driver
        this.dist = distance;
    }
    /**
     *
     * Drives the robot towards the exit given it exists and
     * given the robot's energy supply lasts long enough.
     * @return true if driver successfully reaches the exit, false otherwise
     * @throws Exception if robot stopped due to some problem, e.g. lack of energy
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void drive2Exit() throws Exception{

        if (robot.hasStopped()){
            throw new Exception();
        }
        if (robot.isAtExit()){
            return;
        }
        return;
    }

    /**
     *
     * Returns the total energy consumption of the journey, i.e.,
     * the difference between the robot's initial energy level at
     * the starting position and its energy level at the exit position.
     * This is used as a measure of efficiency for a robot driver.
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public float getEnergyConsumption(){
        return initial - robot.getBatteryLevel();
    }

    /**
     *
     * Returns the total length of the journey in number of cells traversed.
     * Being at the initial position counts as 0.
     * This is used as a measure of efficiency for a robot driver.
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public int getPathLength(){
        return this.robot.getOdometerReading();
    }
    /**
     *
     * This method receives input from the simple key listener
     * and are responsible for manually driving the robot.
     * If the user manages to exit or loses the game due to lack of energy,
     * switch from the playing mode to the winning/finish mode
     *
     * @param uikey: user's keyboard input
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    public void manualKeyDown(UserInput uikey){
        Log.v("ManualDriver", "key inputed");
        switch (uikey){
            case Up:
                robot.move(1, true);
                break;

            case Down:
                robot.goDown(1, true);
                break;

            case Left:
                robot.rotate (Turn.LEFT);
                break;

            case Right:
                robot.rotate (Turn.RIGHT);
                break;

            default:
                assert false : "Oops, Unknown Key Input!";
                break;
        }

    }
}

