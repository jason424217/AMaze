package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
 * Class: PlayManuallyActivity
 *
 * Responsibility: Displays the maze and lets the user manually navigate the robot through the maze.
 * Provides a feature to toggle visibility of the map plus functionality to toggle visibility
 * of the solution on the map. show the whole maze from top or not; show the solution in the maze or not;
 * show the currently visible walls or not; scale the size of the map; Pressing the back button returns to
 * State Title to allow the user to choose different parameter settings and restart.
 *
 * Collaborators: AppCompatActivity, GeneratingActivity, WinningActivity
 *
 * @author Yuan Gu/Ziyue Zhou
 */
public class PlayManuallyActivity extends AppCompatActivity {

    private static final String TAG = "PlayingManuallyActivity";
    private TextView msg;
    private int skill;
    private String driver;
    private String generator;
    private boolean showSolution;
    private boolean showMap;
    private boolean showWalls;
    private Intent intent;
    private TextView energy;

    MazePanel mazePanel;
    MazeConfiguration MazeConfig;
    Controller controller;

    private BasicRobot robot;
    private ManualDriver manualDriver;

    /**
     * This method will receive intent passed from generating stage.
     *
     * @param savedInstanceState
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);
        Bundle extras = getIntent().getExtras();
        skill = extras.getInt("level");
        driver = extras.getString("driver");
        generator = extras.getString("generator");
        msg = findViewById(R.id.msg);
        energy = findViewById(R.id.manuallyenergy);
        energy.setText("Energy: " + Float.toString(BasicRobot.energy));


        mazePanel = (MazePanel) findViewById(R.id.Panel);

        controller = new Controller();
        if ("DFS".equalsIgnoreCase(generator)) {
            Log.v("Maze generation","MazeApplication: maze will be generated with a randomized algorithm.");
            controller.setBuilder(Order.Builder.DFS);
        }
        // Case 2: Prim
        else if ("Prim".equalsIgnoreCase(generator))
        {
            Log.v("Maze generation","MazeApplication: generating random maze with Prim's algorithm.");
            controller.setBuilder(Order.Builder.Prim);
        }
        // Case 3 a and b: Eller, Kruskal or some other generation algorithm
        else if ("Eller".equalsIgnoreCase(generator))
        {
            Log.v("Maze generation","MazeApplication: generating random maze with Eller's algorithm.");
            controller.setBuilder(Order.Builder.Eller);
        }else{
            Log.v("Maze generation","MazeApplication: unknown parameter value");
        }
        controller.setPanel(mazePanel);
        controller.setDrivername("ManualDriver");

        robot = new BasicRobot();
        manualDriver = new ManualDriver();
        robot.setMaze(controller);
        manualDriver.setRobot((Robot)robot);
        controller.setRobotAndDriver(robot, manualDriver);

        showSolution = false;
        showMap = false;
        showWalls = false;


        //TODO: Need to implement how to generator maze first!!!!
        controller.setMazeConfiguration(StaticData.getMC());


        controller.draw(); //draw initial state
        updateShowMap();
        updateShowWalls();
        updateShowSolution();
    }

    /**
     * This method will get the user to the winning stage
     * if the Go2Winning button is clicked
     * @author Yuan Gu/Ziyue Zhou
     */
    public void move2Winning(){
        Log.v(TAG, "Winning");
        intent = new Intent(this, WinningActivity.class);
        intent.putExtra("driver", driver);
        intent.putExtra("generator", generator);
        intent.putExtra("level", skill);
        startActivity(intent);
    }


    public void move2Losing(){
        Log.v(TAG, "Losing");
        intent = new Intent(this, LosingActivity.class);
        intent.putExtra("driver", driver);
        intent.putExtra("generator", generator);
        intent.putExtra("level", skill);
        startActivity(intent);
    }

    /**
     * This method will get the maze to zoom in
     * if the Go2Winning button is clicked
     * @param view
     * @author Yuan Gu/Ziyue Zhou
     */
    public void ZoomIn(View view){
        Log.v(TAG, "Zoomed In");
        controller.adjustMapScale(true);
        controller.draw();
    }

    public void ZoomOut(View view){
        Log.v(TAG, "Zoomed out");
        controller.adjustMapScale(false);
        controller.draw();
    }

    /**
     * This method will update the text when user turned on switch
     * @author Yuan Gu/Ziyue Zhou
     */
    public void updateShowMap() {
        Log.v(TAG, "Map updated");

        // Store the view as a Switch.
        final Switch mapSwitch = (Switch)findViewById(R.id.maze);
        mapSwitch.setChecked(showMap);

        mapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mapSwitch.setChecked(true);
                    showMap = true;
                    controller.setShowMap(true);
                    msg.setText("Maze Shown");
                    controller.draw();
                }else{
                    mapSwitch.setChecked(false);
                    showMap = false;
                    controller.setShowMap(false);
                    msg.setText("");
                    controller.draw();

                }
            }
        });

        // Log what we're doing.
        Log.v(TAG, "Show Map: " + showMap);
    }


    /**
     * This method will update the text when user turned on switch
     * @author Yuan Gu/Ziyue Zhou
     */
    public void updateShowWalls() {
        Log.v(TAG, "Wall updated");
        // Store the view as a Switch.
        final Switch wallSwitch = (Switch)findViewById(R.id.wall);
        wallSwitch.setChecked(showWalls);

        wallSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    wallSwitch.setChecked(true);
                    showWalls = true;
                    controller.setShowWalls(true);
                    msg.setText("Wall Shown");
                    controller.draw();
                }else{
                    wallSwitch.setChecked(false);
                    showWalls = false;
                    controller.setShowWalls(false);
                    msg.setText("");
                    controller.draw();
                }
            }
        });
        // Log what we're doing.
        Log.v(TAG, "Show Walls: " + showWalls);
    }

    /**
     * This method will update the text when user turned on switch
     * @author Yuan Gu/Ziyue Zhou
     */
    public void updateShowSolution() {
        // Store the view as a Switch.
        Log.v(TAG, "Solution updated");
        final Switch mapSolution = (Switch)findViewById(R.id.solution);
        mapSolution.setChecked(showSolution);

        mapSolution.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mapSolution.setChecked(true);
                    showSolution = true;
                    controller.setShowSolution(true);
                    msg.setText("Solution Shown");
                    controller.draw();
                }else{
                    mapSolution.setChecked(false);
                    showSolution = false;
                    controller.setShowSolution(false);
                    msg.setText("");
                    controller.draw();

                }
            }
        });
        // Log what we're doing.
        Log.v(TAG, "Show Solution: " + showSolution);
    }


    /**
     * This method will update the text when user clicked "up" button
     * @author Yuan Gu/Ziyue Zhou
     */
    public void showUp(View view){
        Log.v(TAG, "Go Front");
        msg.setText(R.string.go_f_msg);
        manualDriver.manualKeyDown(Constants.UserInput.Up);
        energy.setText("Energy: " + Float.toString(BasicRobot.energy));
        if(robot.hasStopped()){
            if (BasicRobot.winning){
                move2Winning();
            }else{
                move2Losing();
            }
        }
    }

    /**
     * This method will update the text when user clicked "left" button
     * @author Yuan Gu/Ziyue Zhou
     */
    public void showLeft(View view){
        Log.v(TAG, "Turn Left");
        msg.setText(R.string.go_l_msg);
        manualDriver.manualKeyDown(Constants.UserInput.Left);
        energy.setText("Energy: " + Float.toString(BasicRobot.energy));
        if(robot.hasStopped()){
            if (BasicRobot.winning){
                move2Winning();
            }else{
                move2Losing();
            }
        }
    }

    /**
     * This method will update the text when user clicked "right" button
     * @author Yuan Gu/Ziyue Zhou
     */
    public void showRight(View view){
        Log.v(TAG, "Turn Right");
        msg.setText(R.string.go_r_msg);
        manualDriver.manualKeyDown(Constants.UserInput.Right);
        energy.setText("Energy: " + Float.toString(BasicRobot.energy));
        if(robot.hasStopped()){
            if (BasicRobot.winning){
                move2Winning();
            }else{
                move2Losing();
            }
        }
    }

    /**
     * This method will take the user back the title interface
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void onBackPressed(){
        Log.v(TAG, "BackPressed");
        BasicRobot.energy = 3000;
        BasicRobot.meter = 0;
        BasicRobot.stopped = false;
        BasicRobot.winning = false;
        BasicRobot.manualfault = true;
        Intent i = new Intent(this, AMazeActivity.class);
        startActivity(i);
        this.finish();
    }

}
