package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;


import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.CardinalDirection;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Distance;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Direction;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Robot.Turn;

/**
 * Class: Explorer
 *
 * Responsibilities: This class is responsible for implementing explorer algorithm
 * which is a random search algorithm. The explorer algorithm retains a memory of
 * where it has been before, and randomly picks a path that is the least traveled.
 * Every position in the maze keeps track of how many times the robot has gone through
 *
 * Collaborators: BasicRobot, MazeContainer, Controller, CardinalDirection
 *
 * @author Yuan Gu/Ziyue Zhou
 */

public class Explorer implements RobotDriver{


    private BasicRobot robot;
    private Distance dist;
    private int w;
    private int h;
    private float initial;
    public int[][] map;



    /**
     * This constructor will initialize a explorer driver
     * with initial battery level setting to 3000 and robot to null
     * @author Yuan Gu/Ziyue Zhou
     */
    public Explorer() {
        robot = null;
        initial = 3000;
        System.out.println("Explorer algorithm");
    }


    /**
     * This constructor will set the robot
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void setRobot(Robot r) {
        // TODO Auto-generated method stub
        this.robot = (BasicRobot)(r);
    }

    /**
     * This method will set the maze dimension (unused)
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void setDimensions(int width, int height) {
        // TODO Auto-generated method stub
        this.w = width;
        this.h = height;
    }

    /**
     * This method will set the distance (unused)
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void setDistance(Distance distance) {
        // TODO Auto-generated method stub
        this.dist = distance;
    }

    /**
     * This method will automatically drives the robot to exit based on a random search algorithm.
     * Pseudocode:
     * First represents the maze as a 2-D array--map, with each position containing how many times
     * the robot has been this position. Initialize all to 0. Every time the robot move to a new position,
     * the corresponding value of the position should be incremented by 1.
     * while the robot is not at exit, get its current position, increment the corresponding value
     * in the map by 1
     * If the robot is outside a room, get its neighbor positions from 4 directions
     * (use distanceToObstacle to check whether there is a wall). Move to the neighbor position which
     * is the least traveled. If there are several neighbor positions having the same number of times
     * being traveled, randomly pick one. Use hash map to get the least traveled position. In hash map,
     * key is the number of being traveled (use value in the map); value is a list of positions which share
     * the same number. Use shuffle to random select a direction.
     * If the robot is inside a room, first scan the room, get all doors' positions, then pick a door
     * least traveled, and go for it. (specific pseudocode will be discussed in later private method)
     * Once the robot is at exit, drives it out and return true.
     * Whenever the robot move/rotate/uses distance sensors, check if the robot is out of energy.
     * if so, return false
     *
     * @exception throw exception if getCurrentPosition/getNeighborPosition returns an invalid position
     * @return true if the robot drives to exit, false if the robot out of energy
     * @author Yuan Gu/Ziyue Zhou
     */


    public void drive2Exit() throws Exception {

        if (PlayAnimationActivity.explorer) {
            map = new int[robot.controller.getMazeConfiguration().getWidth()][robot.controller.getMazeConfiguration().getHeight()];
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    map[i][j] = 0;
                }
            }
            PlayAnimationActivity.explorer = false;
        }



        if (!robot.isAtExit()) {


            if (robot.hasStopped()) {
                throw new Exception("Interrupted");
            }




            int[] curPos = robot.getCurrentPosition();
            int x = curPos[0];
            int y = curPos[1];
            map[x][y]++;


            if (!robot.isInsideRoom()) {
                HashMap<Integer, List<CardinalDirection>> hmap = new HashMap<Integer, List<CardinalDirection>>();
                for (Direction d: Direction.values()) {


                    if (robot.distanceToObstacle(d) != 0) {
                        CardinalDirection cd = getCardinalDirection(d);
                        int[] next = getNeighborPosition(curPos, cd);
                        int newx = next[0];
                        int newy = next[1];
                        int key = map[newx][newy];
                        if (!hmap.containsKey(key)) {
                            List<CardinalDirection> cd_list = new ArrayList<CardinalDirection>();
                            cd_list.add(cd);
                            hmap.put(key, cd_list);
                        }
                        else {
                            hmap.get(key).add(cd);
                        }
                    }
                }


                Set<Integer> keySet = hmap.keySet();
                List<Integer> key_list = new ArrayList<Integer>(keySet);
                int min = Collections.min(key_list);
                List<CardinalDirection> min_list = hmap.get(min);


                // Randomly pick a direction from the least traveled cells
                Collections.shuffle(min_list);
                CardinalDirection cdTarget = min_list.get(0);
                CardinalDirection curCd = robot.getCurrentDirection();
                turn2Direction(curCd, cdTarget);
                if (robot.hasStopped()) {
                    throw new Exception("Interrupted");
                }
                robot.move(1, false);
                if (robot.hasStopped()) {
                    throw new Exception("Interrupted");
                }
            }
            else {


                // scan the room and get all door coordinates
                List<int[]> door_list = scanRoom(map);
                if(door_list == null){
                    throw new Exception("Interrupted");
                }
                HashMap<Integer, List<int[]>> hm = new HashMap<Integer, List<int[]>>();
                for (int i = 0; i < door_list.size(); i++) {
                    int[] door = door_list.get(i);
                    int doorX = door[0];
                    int doorY = door[1];
                    int key = map[doorX][doorY];
                    if (!hm.containsKey(key)) {
                        List<int[]> doorsWithSameKey = new ArrayList<int[]>();
                        doorsWithSameKey.add(door);
                        hm.put(key, doorsWithSameKey);
                    }
                    else {
                        hm.get(key).add(door);
                    }
                }
                Set<Integer> doorSet = hm.keySet();
                List<Integer> keyList = new ArrayList<Integer>(doorSet);
                int mini = Collections.min(keyList);
                List<int[]> min_door_list = hm.get(mini);


                // Randomly pick a direction from the least traveled cells
                Collections.shuffle(min_door_list);
                int[] targetDoor = min_door_list.get(0);
                leaveRoom(robot.getCurrentPosition(), targetDoor);
            }
        }


        if(robot.isAtExit()){
            if(!robot.hasStopped()){
                robot.lastStepMove();
                Log.v("Explorer", "last step move?");}}
        if(robot.hasStopped()|| robot.controller.isOutside(robot.controller.getCurrentPosition()[0], robot.controller.getCurrentPosition()[1])){
            Log.v("Explorer", "Exited");
            throw new Exception("Interrupted");}

    }


    /**
     * This private method will help the robot get the cardinal direction
     * (in the absolute sense) given a local direction (in a relative sense)
     *
     * @param d is the robot's local direction (Left/right/forward/backward)
     * @return cdd: cardinal direction
     * @author Yuan Gu/Ziyue Zhou
     */
    private CardinalDirection getCardinalDirection(Direction d) {
        CardinalDirection cd = robot.getCurrentDirection();
        CardinalDirection cdd = null;
        switch (d) {
            case FORWARD:
                cdd = cd;
                break;
            case BACKWARD:
                cdd = cd.oppositeDirection();
                break;
            case LEFT:
                cdd = cd.rotateClockwise();
                break;
            case RIGHT:
                cdd = cd.rotateCounterClockwise();
                break;
        }
        return cdd;
    }


    /**
     * This private method will help the robot get its neighbor position in the
     * cardinal direction of the robot
     *
     * @param cur is the robot's current position
     * @param cd is in which cardinal direction of the robot
     * @return next: the neighbor position
     * @author Yuan Gu/Ziyue Zhou
     */
    private int[] getNeighborPosition(int[] cur, CardinalDirection cd) {
        int x = cur[0];
        int y = cur[1];
        int[] next = new int[2];
        switch (cd) {
            case South:
                y++;
                break;
            case North:
                y--;
                break;
            case East:
                x++;
                break;
            case West:
                x--;
                break;
            default:
                break;
        }
        next[0] = x;
        next[1] = y;
        return next;
    }


    /**
     * This private method will help the robot turns to the right direction given
     * its current cardinal direction and the target cardinal direction it should
     * head to.
     *
     * @param cd is the cardinal direction the robot is facing right now;
     * @param rightcd is the cardinal direction the robot should go for.
     * @author Yuan Gu/Ziyue Zhou
     */
    private void turn2Direction(CardinalDirection cd, CardinalDirection rightcd) {
        if (rightcd == cd.oppositeDirection()) {
            robot.rotate(Turn.AROUND);
        }
        else if (rightcd == cd.rotateCounterClockwise()) {
            robot.rotate(Turn.RIGHT);
        }
        else if (rightcd == cd.rotateClockwise()) {
            robot.rotate(Turn.LEFT);
        }
    }


    /**
     * This private method will return a local direction to the robot given
     * two cardinal directions provided (current and target)
     * Very similar to the turn2Direction, but instead of performing the
     * rotation, it simply gives which direction the robot should rotate
     *
     * @param cd is the cardinal direction the robot is facing right now;
     * @param rightcd is the cardinal direction the robot should go for.
     * @return direction: local direction to the robot
     * @author Yuan Gu/Ziyue Zhou
     */
    private Direction getDirection(CardinalDirection cd, CardinalDirection rightcd) {
        Direction direction = Direction.FORWARD;


        if (rightcd == cd.oppositeDirection()) {
            direction = Direction.BACKWARD;
        }
        else if (rightcd == cd.rotateCounterClockwise()) {
            direction = Direction.RIGHT;
        }
        else if (rightcd == cd.rotateClockwise()) {
            direction = Direction.LEFT;
        }
        return direction;
    }


    /**
     * This private method will scan the room, looking for all doors and
     * return them as a list of positions. Uses hash map to store the list of
     * doors which share the same number of times being traveled. Use shuffle
     * to randomly pick the door least traveled
     * Pseudocode:
     * Keep track of its original position before scanning
     * Rotate left, and keep track of its current cardinal direction.
     * While the origin is not reached again(either position or cardinal direction
     * is different), applies left wall follower algorithm here. But two things special:
     * 1. whenever the robot's left hand side does not have a wall, get its left neighbor's
     * position, store it as a door's position
     * 2. We don't want to leave the room before it is fully scanned. So when there is no
     * wall in front, we also need to check it still inside the room, if so, move forward;
     * if not, that indicates the robot is at the corner, so turn right.
     *
     * @param map is the 2-d array representing times of being traveled
     * @return door_list: a list of doors' positions
     * @throws throw exception if getCurrentPosition/getNeighborPosition returns an invalid position
     * @author Yuan Gu/Ziyue Zhou
     */
    private List<int[]> scanRoom(int[][] map) throws Exception {
        List<int[]> door_list = new ArrayList<int[]>();
        int[] origin = robot.getCurrentPosition();
        robot.rotate(Turn.LEFT);


        if (robot.hasStopped()) {
            return null;
        }


        CardinalDirection originCd = robot.getCurrentDirection();




        if (robot.distanceToObstacle(Direction.LEFT)!=0) {
            int[] currentPos = robot.getCurrentPosition();
            CardinalDirection cd = getCardinalDirection(Direction.LEFT);
            int[] doorPos = getNeighborPosition(currentPos, cd);
            door_list.add(doorPos);
        }


        // If there is no wall in front and the front position is still inside the maze, move forward
        if((robot.distanceToObstacle(Direction.FORWARD)!=0)
                && (robot.controller.getMazeConfiguration().getMazecells().isInRoom(getNeighborPosition(robot.getCurrentPosition(),
                robot.getCurrentDirection())[0], getNeighborPosition(robot.getCurrentPosition(), robot.getCurrentDirection())[1]))) {
            robot.move(1, false);
            if (robot.hasStopped()) {
                throw new Exception("Stopped");
            }
            int[] newPos = robot.getCurrentPosition();
            map[newPos[0]][newPos[1]]++;
        }
        else {
            robot.rotate(Turn.RIGHT);
        }


        if (robot.hasStopped()) {
            return null;
        }


        // While origin is not reached again, start the above process again
        while (!((Arrays.equals(origin, robot.getCurrentPosition()))&&(originCd == robot.getCurrentDirection()))) {


            if (robot.distanceToObstacle(Direction.LEFT)!=0) {
                int[] currentPos = robot.getCurrentPosition();
                CardinalDirection cd = getCardinalDirection(Direction.LEFT);
                int[] doorPos = getNeighborPosition(currentPos, cd);
                door_list.add(doorPos);
            }


            if ((robot.distanceToObstacle(Direction.FORWARD)!=0)
                    && (robot.controller.getMazeConfiguration().getMazecells().isInRoom(getNeighborPosition(robot.getCurrentPosition(),
                    robot.getCurrentDirection())[0], getNeighborPosition(robot.getCurrentPosition(), robot.getCurrentDirection())[1]))){
                robot.move(1, false);
                if (robot.hasStopped()) {
                    throw new Exception("Stopped");
                }
                int[] newPos = robot.getCurrentPosition();
                map[newPos[0]][newPos[1]]++;
            }
            else {
                robot.rotate(Turn.RIGHT);
            }


            if (robot.hasStopped()) {
                throw new Exception("Stopped");
            }
        }
        return door_list;
    }


    /**
     * This private method will drive the robot leaving the room via one of doors.
     *
     * Pseudocode:
     * First calculate the difference of their x and y coordinates. Determine which
     * cases the situation belongs to. There are nine cases in total, with five of
     * them special (the current position and the target door is in a parallel line
     * so only need to move in one-direction or the original position is chosen to be
     * the door). For generic cases, we need to consider moves in two directions
     * (East-West and South-North). In order to prevent the robot from being crashed,
     * we need to check the distance to wall in a given direction and compare it with
     * the difference of coordinates on that direction. If difference is less or equal
     * to the distance, we can go for that direction first: move(difference, false);
     * then, go for another direction.
     *
     * @param current is the robot's current position (origin)
     * @param target is the door position that the robot is leaving for
     * @throws throw exception if getCurrentPosition/getNeighborPosition returns an invalid position
     * @author Yuan Gu/Ziyue Zhou
     */
    private void leaveRoom(int[] current, int[] target) throws Exception{
        int currentX = current[0];
        int currentY = current[1];
        int targetX = target[0];
        int targetY = target[1];
        int differenceEW = targetX - currentX;
        int differenceSN = targetY - currentY;
        int dist2_SN_Wall = 0;
        int dist2_EW_Wall = 0;
        int condition;
        boolean genericCase = true;
        Direction directionEW;
        Direction directionSN;




        // 0-3 are generic cases which need two-direction moves; 4-7 are cases when
        // the target and the origin are in the parallel line; 8 are cases when the
        // target is the origin
        if (differenceEW > 0) {
            if (differenceSN > 0) {
                condition = 0;
            }
            else if (differenceSN < 0)condition = 1;
            else {
                genericCase = false;
                condition = 4;
            }
        }
        else if (differenceEW < 0) {
            if (differenceSN > 0)condition = 2;
            else if (differenceSN < 0)condition = 3;
            else {
                genericCase = false;
                condition = 5;
            }
        }
        else {
            if (differenceSN > 0) {
                genericCase = false;
                condition = 6;
            }
            else {
                genericCase = false;
                condition = 7;
            }
            if ((differenceSN == 1) || (differenceSN == -1)) condition = 8;
        }


        if ((differenceSN == 0) && (differenceEW == 1)) condition = 8;
        if ((differenceSN == 0) && (differenceEW == -1)) condition = 8;




        // This switch statement should perform all moves for special cases.
        // Generic cases are handled later.
        switch (condition) {
            case 0:
                directionEW = getDirection(robot.getCurrentDirection(), CardinalDirection.East);
                dist2_EW_Wall = getDist2Wall(directionEW, Math.abs(differenceEW));
                if (robot.hasStopped()) {
                    return;
                }
                directionSN = getDirection(robot.getCurrentDirection(), CardinalDirection.South);
                dist2_SN_Wall = getDist2Wall(directionSN, Math.abs(differenceSN));
                if (robot.hasStopped()) {
                    return;
                }
                break;
            case 1:
                directionEW = getDirection(robot.getCurrentDirection(), CardinalDirection.East);
                dist2_EW_Wall = getDist2Wall(directionEW, Math.abs(differenceEW));
                if (robot.hasStopped()) {
                    return;
                }
                directionSN = getDirection(robot.getCurrentDirection(), CardinalDirection.North);
                dist2_SN_Wall = getDist2Wall(directionSN, Math.abs(differenceSN));
                if (robot.hasStopped()) {
                    return;
                }
                break;
            case 2:
                directionEW = getDirection(robot.getCurrentDirection(), CardinalDirection.West);
                dist2_EW_Wall = getDist2Wall(directionEW, Math.abs(differenceEW));
                if (robot.hasStopped()) {
                    return;
                }
                directionSN = getDirection(robot.getCurrentDirection(), CardinalDirection.South);
                dist2_SN_Wall = getDist2Wall(directionSN, Math.abs(differenceSN));
                if (robot.hasStopped()) {
                    return;
                }
                break;
            case 3:
                directionEW = getDirection(robot.getCurrentDirection(), CardinalDirection.West);
                dist2_EW_Wall = getDist2Wall(directionEW, Math.abs(differenceEW));
                if (robot.hasStopped()) {
                    return;
                }
                directionSN = getDirection(robot.getCurrentDirection(), CardinalDirection.North);
                dist2_SN_Wall = getDist2Wall(directionSN, Math.abs(differenceSN));
                if (robot.hasStopped()) {
                    return;
                }
                break;
            // Five special cases are directly handled here
            case 4:
                directionEW = getDirection(robot.getCurrentDirection(), CardinalDirection.East);
                moveHorizontally(4, Math.abs(differenceEW));
                if (robot.hasStopped()) {
                    return;
                }
                break;
            case 5:
                directionEW = getDirection(robot.getCurrentDirection(), CardinalDirection.West);
                moveHorizontally(5, Math.abs(differenceEW));
                if (robot.hasStopped()) {
                    return;
                }
                break;
            case 6:
                directionSN = getDirection(robot.getCurrentDirection(), CardinalDirection.South);
                moveVertically(6, Math.abs(differenceSN));
                if (robot.hasStopped()) {
                    return;
                }
                break;
            case 7:
                directionSN = getDirection(robot.getCurrentDirection(), CardinalDirection.North);
                moveVertically(7, Math.abs(differenceSN));
                if (robot.hasStopped()) {
                    return;
                }
                break;
            case 8:
                robot.rotate(Turn.LEFT);
                if (robot.hasStopped()) {
                    return;
                }
                robot.move(1, false);
                if (robot.hasStopped()) {
                    return;
                }
                break;
            default:
                break;
        }




        // Generic cases: determine which direction goes first
        if (genericCase) {
            if (Math.abs(differenceEW) <= dist2_EW_Wall) {
                moveHorizontally(condition, Math.abs(differenceEW));
                moveVertically(condition, Math.abs(differenceSN));
            }


            else if (Math.abs(differenceSN) <= dist2_SN_Wall) {
                moveVertically(condition, Math.abs(differenceSN));
                moveHorizontally(condition, Math.abs(differenceEW));
            }
        }
    }


    /**
     * This private method will compute the distance to the wall given a direction.
     * However, we don't want to leave the room. So if the robot's position after move
     * in the given direction is not inside the room, return -1, so the distance to
     * wall is always less than the difference. The robot will never go to that
     * direction first and, therefore, We can prevent the robot from leaving the room
     *
     * @param direction is which local direction we want to check
     * @param difference is the x or y coordinate difference from origin to target
     * @return dist2Wall is the distance to wall if there is one inside room
     * @throws throw exception if getCurrentPosition returns an invalid position
     * @author Yuan Gu/Ziyue Zhou
     */
    private int getDist2Wall(Direction direction, int difference) throws Exception{


        int dist2Wall;
        CardinalDirection cd = getCardinalDirection(direction);


        int[] next = getPositionafterMove(robot.getCurrentPosition(), cd, difference);
        if (robot.controller.getMazeConfiguration().getMazecells().isInRoom(next[0], next[1])){
            dist2Wall = robot.distanceToObstacle(direction);
        }
        else return -1;


        return dist2Wall;
    }


    /**
     * This private method will get the position after moves of some distance in
     * a particular cardinal direction. This method is useful to deal with one of
     * special cases during leaving the room. We will check if the position after
     * move is still inside the room. Similar to getNeighborPosition but not moving
     * jus 1 step
     *
     * @param cur is the current position
     * @param cd is the cardinal direction the robot moves towards
     * @param difference is the distance being moved
     * @return next is the position after moves
     * @author Yuan Gu/Ziyue Zhou
     */
    private int[] getPositionafterMove(int[] cur, CardinalDirection cd, int difference) {
        int x = cur[0];
        int y = cur[1];
        int[] next = new int[2];
        switch (cd) {
            case South:
                y = y + difference;
                break;
            case North:
                y = y - difference;
                break;
            case East:
                x = x + difference;
                break;
            case West:
                x = x - difference;
                break;
            default:
                break;
        }
        next[0] = x;
        next[1] = y;
        return next;
    }


    /**
     * This private method will handle the horizontal move on the East-West direction.
     *
     * @param condition is which case the situation belongs to
     * @param distance is difference of x coordinates (on E-W direction)
     * @throws throw exception if getCurrentPosition/getNeighborPosition returns an invalid position
     * @author Yuan Gu/Ziyue Zhou
     */
    private void moveHorizontally(int condition, int distance) throws Exception{
        if ((condition == 0) || (condition == 1) || (condition == 4)) {
            turn2Direction(robot.getCurrentDirection(), CardinalDirection.East);
        }
        else if ((condition == 2) || (condition == 3) || (condition == 5)){
            turn2Direction(robot.getCurrentDirection(), CardinalDirection.West);
        }
        robot.move(distance, false);
        if (robot.hasStopped()) {
            return;
        }
    }


    /**
     * This private method will handle the vertical move on the South-North direction.
     *
     * @param condition is which case the situation belongs to
     * @param distance is difference of y coordinates (on S-N direction)
     * @throws throw exception if getCurrentPosition/getNeighborPosition returns an invalid position
     * @author Yuan Gu/Ziyue Zhou
     */
    private void moveVertically(int condition, int distance) throws Exception{
        if ((condition == 0) || (condition == 2) || (condition == 6)) {
            turn2Direction(robot.getCurrentDirection(), CardinalDirection.South);
        }
        else if ((condition == 1) || (condition == 3) || (condition == 7)){
            turn2Direction(robot.getCurrentDirection(), CardinalDirection.North);
        }
        robot.move(distance, false);
        if (robot.hasStopped()) {
            return;
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
        return robot.getOdometerReading();
    }
}
