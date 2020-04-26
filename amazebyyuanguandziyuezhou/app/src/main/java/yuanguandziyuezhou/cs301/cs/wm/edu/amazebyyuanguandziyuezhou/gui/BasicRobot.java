package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;


import android.util.Log;

import android.os.Handler;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.CardinalDirection;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Constants.UserInput;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Direction;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Turn;

/**@author Yuan Gu/Ziyue Zhou
 *
 * Class Responsibility:
 * To operate a robot that is inside a maze at a particular location and
 * looking in a particular direction.This will support a robot with certain sensors.
 * A robot is given an existing maze (a controller) to be operational.
 * It provides an operating platform for a robot driver that experiences a maze (the real world)
 * through the sensors and actuators of this robot interface.
 *
 * A robot comes with a battery level that is depleted during operations
 * such that a robot may actually stop if it runs out of energy.
 * This interface supports energy consideration.
 * A robot may also stop when hitting an obstacle.
 *
 */
public class BasicRobot implements Robot{

    Controller controller;
    static float energy;
    static int meter;
    static boolean stopped;
    static boolean winning = false;
    static boolean manualfault = true;


    // This is for test purpose
    private int energyMove;


    public BasicRobot() {
        super();
        Log.v("BasicRobot", "created");
        this.stopped = false;
        this.winning = false;
        this.controller = null;
        this.setBatteryLevel(3000);
        this.setMoveEnergy(5);
        this.resetOdometer();
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Turn robot on the spot for amount of degrees.
     * @param direction to turn and relative to current forward direction.
     */


    public void rotate(Turn turn) {

        Log.v("BasicRobot", "rotated");
        if(this.hasStopped()) {
            return;
        }

        switch(turn) {
            case LEFT:
                if(energy >= 3) {
                    this.controller.keydown(UserInput.Left);
                    energy -= 3;
                }else {
                    stopped = true;
                }
                break;
            case RIGHT:
                if(energy >= 3) {
                    this.controller.keydown(UserInput.Right);
                    energy -= 3;
                }else {
                    stopped = true;
                }
                break;
            case AROUND:
                if(energy >= 6) {
                    this.controller.keydown(UserInput.Left);
                    this.controller.keydown(UserInput.Left);
                    energy -= 6;
                }else {
                    stopped = true;
                }
                break;
            default:
                break;
        }
    }


    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Moves robot forward a given number of steps.
     * @param distance is the number of cells to move in the robot's current forward direction
     * @param manual is true if robot is operated manually by user, false otherwise
     */
    public void move(int distance, boolean manual) {
        Log.v("BasicRobot", "moved");

        if(this.hasStopped()) {
            return;
        }
        if(energy < energyMove) {
            stopped = true;
            return;
        }
        CardinalDirection cdd = this.controller.getCurrentDirection();
        int x = this.controller.getCurrentPosition()[0];
        int y = this.controller.getCurrentPosition()[1];

        while(distance > 0) {
            if(this.controller.getMazeConfiguration().hasWall(x, y, cdd)) {
                if(!manual) {
                    stopped = true;
                    this.manualfault = false;
                    System.out.println("This message indicates that the automated driver fails (crashed the wall)!");
                    return;
                }
                return;
            }else if(energy < energyMove){
                stopped = true;
                return;
            }else {
                this.controller.keydown(UserInput.Up);
                meter ++;
                distance --;
                energy -= energyMove;
            }
        }

    }

    /**@author Ziyue Zhou/Yuan Gu
     * get the boolean value if this is manually operated or not
     * @return true if it is manually operate else otherwise
     */
    public boolean getmanualfault() {
        return this.manualfault;
    }

    /**@author Yuan Gu/Ziyue Zhou
     * Go down a given number of steps.
     * @param distance is the number of cells to move in the robot's current forward direction
     * @param manual is true if robot is operated manually by user, false otherwise
     */

    public void goDown(int distance, boolean manual) {
        if(this.hasStopped()) {
            return;
        }
        if(energy < energyMove) {
            stopped = true;
            return;
        }
        CardinalDirection cdd = this.controller.getCurrentDirection();
        int x = this.controller.getCurrentPosition()[0];
        int y = this.controller.getCurrentPosition()[1];
        cdd = cdd.oppositeDirection();

        while(distance > 0) {
            if(this.controller.getMazeConfiguration().hasWall(x, y, cdd)) {
                if(!manual) {
                    stopped = true;
                    this.manualfault = false;
                    return;
                }
                return;
            }else if(energy < energyMove){
                stopped = true;
                return;
            }else {
                this.controller.keydown(UserInput.Down);
                meter ++;
                distance --;
                energy -= energyMove;
            }
        }
    }



    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Provides the current position as (x,y) coordinates
     * @throws Exception if position is outside of the maze
     */
    public int[] getCurrentPosition() throws Exception {
        int[] current = this.controller.getCurrentPosition();
        if(!this.controller.getMazeConfiguration().isValidPosition(current[0], current[1])) {
            throw new Exception();
        }else return current;
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Provides the robot with a reference to the controller to cooperate with.
     */
    public void setMaze(Controller controller) {

        this.controller = controller;

    }
    /**@author Yuan Gu/Ziyue Zhou
     * Get controller
     * @return return controller
     */
    public Controller getMaze() {

        return this.controller;

    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Tells if current position (x,y) is right at the exit but still inside the maze.
     */
    public boolean isAtExit() {
        if(this.controller.getMazeConfiguration().getDistanceToExit(
                this.controller.getCurrentPosition()[0], this.controller.getCurrentPosition()[1])==1) {
            return true;
        }else return false;
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Tells if a sensor can identify the exit in given direction
     */
    public boolean canSeeExit(Direction direction) throws UnsupportedOperationException {
        if(this.hasDistanceSensor(direction)) {
            if(this.distanceToObstacle(direction) == Integer.MAX_VALUE) {
                return true;
            }else return false;
        }else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Tells if current position is inside a room.
     * @throws UnsupportedOperationException if not supported by robot
     */
    public boolean isInsideRoom() throws UnsupportedOperationException {
        try{
            return this.controller.getMazeConfiguration().getMazecells().isInRoom(this.controller.getCurrentPosition()[0], this.controller.getCurrentPosition()[1]);
        }catch(Exception e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Tells if the robot has a room sensor.
     */
    public boolean hasRoomSensor() {
        return true;
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * return Cardinal Direction now
     */
    public CardinalDirection getCurrentDirection() {
        return this.controller.getCurrentDirection();
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Get battery level now
     */
    public float getBatteryLevel() {
        return energy;
    }
    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Sets the current battery level.
     * @param level is the current battery level
     */
    public void setBatteryLevel(float level) {
        energy = level;
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Gets the distance traveled by the robot.
     */
    public int getOdometerReading() {
        return meter;
    }


    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Resets the odomoter counter to zero.
     */
    public void resetOdometer() {
        meter = 0;
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Gives the energy consumption for a full 360 degree rotation.
     */
    public float getEnergyForFullRotation() {
        return 12;
    }

    /**@author Yuan Gu/Ziyue Zhou
     * Gives the energy consumption for a 90 degree rotation.
     */
    public float getEnergyForOneTurn() {
        return 3;
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Gives the energy consumption for moving forward for a distance of 1 step.
     */
    public float getEnergyForStepForward() {
        return energyMove;
    }

    /**@author Yuan Gu/Ziyue Zhou
     * Gives the energy consumption for sensing distance.
     */
    public float getEnergyForSensing() {
        return 1;
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Tells if the robot has stopped.
     */
    public boolean hasStopped() {
        if(energy <= 0) {
            stopped = true;
        }
        return stopped;
    }

    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Tells the distance to an obstacle. Use assert()
     * @param direction the direction we want to check
     * @throws UnsupportedOperationException if not supported by robot
     */
    public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
        if(this.hasDistanceSensor(direction)) {
            if(energy >= 1) {
                energy -= 1;
            }else {
                stopped = true;
            }
            if(!stopped) {
                CardinalDirection cdd = null;
                CardinalDirection cd = this.controller.getCurrentDirection();
                switch(direction) {
                    case LEFT:
                        cdd = cd.rotateClockwise();
                        break;
                    case RIGHT:
                        cdd = cd.rotateCounterClockwise();
                        break;
                    case FORWARD:
                        cdd = cd;
                        break;
                    case BACKWARD:
                        cdd = cd.oppositeDirection();
                        break;
                }

                int count = 0;
                int x = this.controller.getCurrentPosition()[0];
                int y = this.controller.getCurrentPosition()[1];


                while(true) {
                    // If outside of maze, the robot faces forwards the exit
                    if (!this.controller.getMazeConfiguration().isValidPosition(x, y)) {
                        return Integer.MAX_VALUE;
                    }
                    switch(cdd) {
                        case North:
                            if(this.controller.getMazeConfiguration().hasWall(x, y, cdd))return count;
                            y--;
                            break;
                        case South:
                            if(this.controller.getMazeConfiguration().hasWall(x, y, cdd))return count;
                            y++;
                            break;
                        case East:
                            if(this.controller.getMazeConfiguration().hasWall(x, y, cdd))return count;
                            x++;
                            break;
                        case West:
                            if(this.controller.getMazeConfiguration().hasWall(x, y, cdd))return count;
                            x--;
                            break;
                        default:
                            assert false : "Oops, Unknown Cardinal direction!";
                            break;
                        //assert method use here
                    }
                    count++;
                }
            }else{
                throw new UnsupportedOperationException();
            }
        }else throw new UnsupportedOperationException();
    }



    @Override
    /**@author Yuan Gu/Ziyue Zhou
     * Tells if the robot has a distance sensor for the given direction.
     */
    public boolean hasDistanceSensor(Direction direction) {
        return true;
    }

    /**
     * This is responsible for performing the last step move at the exit.
     * This will be called in all driver algorithm.
     * Refactored by Yuan Gu
     * @author Yuan Gu/Ziyue Zhou
     */
    public void lastStepMove() {

        if (this.canSeeExit(Direction.RIGHT)) {
            this.rotate(Turn.RIGHT);
        }
        if (this.canSeeExit(Direction.LEFT)) {
            this.rotate(Turn.LEFT);
        }
        if (this.canSeeExit(Direction.BACKWARD)) {
            this.rotate(Turn.AROUND);
        }

        this.move(1, false);
    }


    ////////////////////////////Setter and getter for testing purpose/////////////////////////////////

    /**
     * Set the energy consumed for move 1 step
     * @author Yuan Gu/Ziyue Zhou
     */
    public void setMoveEnergy(int move) {
        energyMove = move;
    }

    /**
     * Get the energy consumed for move 1 step
     * @return energyMove
     * @author Yuan Gu/Ziyue Zhou
     */
    public int getMoveEnergy() {
        return energyMove;
    }

}