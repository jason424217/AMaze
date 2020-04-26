package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;

public class Constants {

    // The panel used to display the maze has a fixed dimension
    public static final int VIEW_WIDTH = 1370;
    public static final int VIEW_HEIGHT = 1228;
    public static final int MAP_UNIT = 128;
    public static final int VIEW_OFFSET = MAP_UNIT/8;
    public static final int STEP_SIZE = MAP_UNIT/4;
    // Skill-level
    // The user picks a skill level between 0 - 9, a-f
    // The following arrays transform this into corresponding dimensions (x,y) for the resulting maze as well as the number of rooms and parts
    public static int[] SKILL_X =     { 4, 12, 15, 20, 25, 25, 35, 35, 40, 60, 70, 80, 90, 110, 150, 300 };
    public static int[] SKILL_Y =     { 4, 12, 15, 15, 20, 25, 25, 35, 40, 60, 70, 75, 75,  90, 120, 240 };
    public static int[] SKILL_ROOMS = { 0,  2,  2,  3,  4,  5, 10, 10, 20, 45, 45, 50, 50,  60,  80, 160 };
    public static int[] SKILL_PARTCT = { 60, 600, 900, 1200, 2100, 2700, 3300,
            5000, 6000, 13500, 19800, 25000, 29000, 45000, 85000, 85000*4 };

    // Possible states of the GUI
    // these are states of an automaton that the graphical user interface implements
    public enum StateGUI { STATE_TITLE, STATE_GENERATING, STATE_PLAY, STATE_FINISH; }

    // Possible user input
    public enum UserInput {ReturnToTitle, Start, Up, Down, Left, Right, Jump, ToggleLocalMap, ToggleFullMap, ToggleSolution, ZoomIn, ZoomOut };

    // fixing a value matching the escape key
    final static int ESCAPE = 27;

}
