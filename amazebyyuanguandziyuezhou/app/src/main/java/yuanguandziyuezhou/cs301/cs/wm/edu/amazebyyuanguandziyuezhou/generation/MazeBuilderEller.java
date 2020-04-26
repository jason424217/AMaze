package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation;

import android.util.Log;

import java.util.*;



public class MazeBuilderEller extends MazeBuilder implements Runnable {

    private int[][] matrix;
    CardinalDirection cdE = CardinalDirection.getDirection(1, 0);
    CardinalDirection cdS = CardinalDirection.getDirection(0, 1);
    CardinalDirection cdW = CardinalDirection.getDirection(-1, 0);
    CardinalDirection cdN = CardinalDirection.getDirection(0, -1);

    /**
     * Build a new maze randomly using Eller's algorithm to generate maze.
     */
    public MazeBuilderEller() {
        super();
        Log.v("MazeBuilderEller", "MazeBuilded");
    }


    /**
     * Build a new maze deterministically or not using Eller's algorithm to generate maze
     * @param det if this argument is `true`, the maze will be built deterministically
     */
    public MazeBuilderEller(boolean det) {
        super(det);
        Log.v("MazeBuilderEller", "MazeBuilded");
    }

    /**
     * This method start to generate a maze by creating and merging the first line.
     * Then, go down and merge following rows until reach the last row.
     * This is the overall method to generate maze using Eller's algorithm.
     */
    @Override
    public void generatePathways() {
        matrix = new int[width][height];
        for(int i = 0; i < width; i++) matrix[i][0] = i + 1;
        merge(0);
        for(int j = 1; j < height-1; j++) {
            goDown(j);
            merge(j);
        }
        goDown(height - 1);
        mergeLastRow();
    }

    /**
     * randomly merge adjacent sets of the given row by breaking horizontal walls
     * @param row the current row of the maze
     */
    private void merge(int row) {
        Random randomlizer = new Random();
        for(int i =0; i < width - 1; i ++) {
            if(randomlizer.nextBoolean() || cells.isInRoom(i+1, row) && cells.canGo(new Wall(i, row, cdE))) {
                breakwallHorizontally(i, row, 1);
            }
            if(i != 0 && !cells.canGo(new Wall(i, row, cdS))){
                breakwallHorizontally(i, row, -1);
                breakwallHorizontally(i, row, 1);
            }

        }
    }

    /**
     * Delete wall between cells in a given direction and update the sets in the given row
     * @param x	width of the cell
     * @param y height of the cell
     * @param d direction, 1 is East, -1 is West
     */

    private void breakwallHorizontally(int x, int y, int d) {
        int old1;
        int new1;
        if(matrix[x][y] == matrix[x+d][y])return;

        if(matrix[x][y]<matrix[x+d][y]) {
            old1 = matrix[x+d][y];
            new1 = matrix[x][y];
            matrix[x+d][y] = matrix[x][y];
        }else {
            old1 = matrix[x][y];
            new1 = matrix[x+d][y];
            matrix[x][y] = matrix[x+d][y];
        }

        for(int i = 0; i < width; i++) {
            if(matrix[i][y] == old1)
                matrix[i][y] = new1;
        }

        CardinalDirection cd = CardinalDirection.getDirection(d, 0);
        cells.deleteWall(new Wall(x, y, cd));

    }

    /**
     * (1)For each row, create a hashmap that put the index of the given row
     * that is in same sets with other index together to as a list. The key of the hashmap
     * is the element in set, and value of the hashmap is the list that contains all index
     * with the same elements with the key in the given row.
     * {1,2,2,2,3,4,4} will be like {1:{0}, 2:{1,2,3}, 3:{4}, 4:{5,6}}
     * (2)Randomly choose delete which down wall by shuffle the value of keys and take first
     * random value to become the list remove wall later.
     * Also, add a cell in a room had no boundary between to remove wall too
     * (3)Remove all down walls in remove list
     * @param row the index of the row that needed to go down
     */
    private void goDown(int row) {
        int prevRow = row - 1;
        int[] prev = new int[width];

        for (int i = 0; i < width; i ++) {
            prev[i] = matrix[i][prevRow];
        }

        HashMap<Integer, List<Integer>> hmap = new HashMap<Integer, List<Integer>>();
        for(int i = 0 ; i < prev.length; i ++) {
            if(hmap.get(prev[i]) == null) {
                hmap.put(prev[i], new ArrayList<Integer>());
                if(cells.canGo(new Wall(i, prevRow, cdS)))
                    hmap.get(prev[i]).add(i);
            }
        }

        Random randomlizer = new Random();
        Collection<Integer> keys = hmap.keySet();
        List<Integer> remove = new ArrayList<Integer>();
        for(Object key: keys) {
            List<Integer> list = hmap.get(key);
            Collections.shuffle(list);
            int take = randomlizer.nextInt(list.size()) + 1;
            remove.addAll(hmap.get(key).subList(0, take));
            for(int i = 0; i < list.size(); i++) {
                if(cells.isInRoom(list.get(i), row) && !remove.contains(list.get(i)))
                    remove.add(list.get(i));
            }
        }


        for (int i = 0; i < remove.size(); i++) {
            cells.deleteWall(new Wall(remove.get(i), prevRow, cdS));
            matrix[remove.get(i)][row] = matrix[remove.get(i)][prevRow];
        }


        int Max = max(row);
        for (int i = 0; i < width; i++)
            if (matrix[i][row] == 0) {

                if (!cells.hasWall(i, row, cdW) && matrix[i - 1][row] != 0)
                    matrix[i][row] = matrix[i - 1][row];
                else
                    matrix[i][row] = ++Max;
            }
    }
    /**
     * combine adjacent and disjoint sets of the last row of maze
     */
    private void mergeLastRow() {
        int y = height - 1;
        for (int x = 0; x < width - 1; x++) {
            if (matrix[x][y] != matrix[x + 1][y] && cells.canGo(new Wall(x, y, cdE))) {
                cells.deleteWall(new Wall(x, y, cdE));
                matrix[x + 1][y] = matrix[x][y];
            }
        }
    }

    /**
     * Return the largest value of the row
     * @param index row index
     * @return the largest in the row
     */
    private int max(int index) {
        int max = matrix[0][index];
        for (int i = 0; i < width; i++)
            if (matrix[i][index] > max) max = matrix[i][index];

        return max;
    }



}
