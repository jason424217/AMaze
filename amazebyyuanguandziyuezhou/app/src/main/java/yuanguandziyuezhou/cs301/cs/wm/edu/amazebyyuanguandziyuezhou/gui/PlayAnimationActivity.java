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
import java.lang.Thread;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.R;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.CardinalDirection;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Cells;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeBuilder;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeConfiguration;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Order;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui.Constants;


import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.R;

/**
 * Class: PlayAnimationActivity
 *
 * Responsibility: Displays the maze and lets the user watch a robot exploring the maze.
 * Displays the remaining energy, consider using a ProgressBar for this. Show the whole
 * maze from top or not. show the solution in the maze or not. show the currently visible
 * walls or not. scale the size of the map. Screen provides a start/pause button to start
 * the exploration and to pause the animation. Introduce a button "Go2Winning" to
 * directly move the UI to State Winning, and a corresponding button “Go2Loosing".
 *
 * Collaborator: AppCompatActivity, AMazeActivity, WinningActivity, LosingActivity
 *
 * @author Yuan Gu/Ziyue Zhou
 */
public class PlayAnimationActivity extends AppCompatActivity {

    private static final String TAG = "PlayAnimationActivity";
    private boolean showMap = false;
    private boolean showWalls = false;
    private boolean showSolution = false;
    private int skill;
    private String driver;
    private String generator;
    private TextView text;
    private TextView energy;
    private Intent intent;


    public Thread t = null;
    public int a123;
    public Object mPauseLock = new Object();


    MazePanel mazePanel;
    MazeConfiguration MazeConfig;
    Controller controller;

    private BasicRobot robot;
    private RobotDriver robotDriver;

    public boolean started = false;
    boolean win = false;
    static boolean explorer = false;


    /**
     * Set up value of skill, driver and generator. Also set up text
     * then update the text through switch
     *
     * @param savedInstanceState saved Instances state
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);
        text = findViewById(R.id.AnimationText);
        Bundle extras = getIntent().getExtras();
        skill = extras.getInt("level");
        driver = extras.getString("driver");
        generator = extras.getString("generator");
        energy = findViewById(R.id.AnimationEnergy);


        mazePanel = (MazePanel) findViewById(R.id.Panel2);

        controller = new Controller();
        if ("DFS".equalsIgnoreCase(generator)) {
            Log.v("Maze generation", "MazeApplication: maze will be generated with a randomized algorithm.");
            controller.setBuilder(Order.Builder.DFS);
        }
        // Case 2: Prim
        else if ("Prim".equalsIgnoreCase(generator)) {
            Log.v("Maze generation", "MazeApplication: generating random maze with Prim's algorithm.");
            controller.setBuilder(Order.Builder.Prim);
        }
        // Case 3 a and b: Eller, Kruskal or some other generation algorithm
        else if ("Eller".equalsIgnoreCase(generator)) {
            Log.v("Maze generation", "MazeApplication: generating random maze with Eller's algorithm.");
            controller.setBuilder(Order.Builder.Eller);
        } else {
            Log.v("Maze generation", "MazeApplication: unknown parameter value");
        }
        controller.setPanel(mazePanel);
        controller.setDrivername("ManualDriver");

        robot = new BasicRobot();
        switch (driver) {
            case "Wizard":
                Log.v("yyyyyy", "Created");
                Wizard driver1 = new Wizard();
                robot.setMaze(controller);
                driver1.setRobot((Robot) robot);
                controller.setRobotAndDriver(robot, driver1);
                robotDriver = (RobotDriver) driver1;
                break;
            case "WallFollower":
                WallFollower driver2 = new WallFollower();
                robot.setMaze(controller);
                driver2.setRobot((Robot) robot);
                controller.setRobotAndDriver(robot, driver2);
                robotDriver = (RobotDriver) driver2;
                break;
            case "Pledge":
                Pledge driver3 = new Pledge();
                robot.setMaze(controller);
                driver3.setRobot((Robot) robot);
                controller.setRobotAndDriver(robot, driver3);
                robotDriver = (RobotDriver) driver3;
                break;
            case "Explorer":
                Explorer driver4 = new Explorer();
                robot.setMaze(controller);
                driver4.setRobot((Robot) robot);
                controller.setRobotAndDriver(robot, driver4);
                robotDriver = (RobotDriver) driver4;
                explorer = true;
                break;
        }

        showSolution = false;
        showMap = false;
        showWalls = false;
        energy.setText("Energy: " + Float.toString(BasicRobot.energy));

        //TODO: Need to implement how to generator maze first!!!!
        controller.setMazeConfiguration(StaticData.getMC());


        controller.draw(); //draw initial state
        updateShowMap();
        updateShowWalls();
        updateShowSolution();

        Button sizeIncrease = (Button) findViewById(R.id.AnimationIncrease);
        sizeIncrease.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                ZoomIn(v);
            }
        });

        Button sizeDecrease = (Button) findViewById(R.id.AnimationDecrease);
        sizeDecrease.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                ZoomOut(v);
            }
        });

        Button Start = (Button) findViewById(R.id.AnimationPause);
        Start.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                try {
                    startandPause(v);
                } catch (Exception e) {
                    Log.e("yyyyyy", "I got an error", e);
                }
            }
        });
    }


    public void ZoomIn(View view) {
        controller.adjustMapScale(true);
        controller.draw();
    }

    public void ZoomOut(View view) {
        controller.adjustMapScale(false);
        controller.draw();
    }

    public void updateShowMap() {
        // Store the view as a Switch.
        final Switch mapSwitch = (Switch) findViewById(R.id.AnimationMaze);
        mapSwitch.setChecked(showMap);

        mapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mapSwitch.setChecked(true);
                    showMap = true;
                    controller.setShowMap(true);
                    text.setText("Maze Shown");
                    controller.draw();
                } else {
                    mapSwitch.setChecked(false);
                    showMap = false;
                    controller.setShowMap(false);
                    text.setText("");
                    controller.draw();

                }
            }
        });

        // Log what we're doing.
        Log.v(TAG, "Show Map: " + showMap);
    }


    /**
     * This method will update the text when user turned on switch
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    public void updateShowWalls() {
        // Store the view as a Switch.
        final Switch wallSwitch = (Switch) findViewById(R.id.AnimationWall);
        wallSwitch.setChecked(showWalls);

        wallSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wallSwitch.setChecked(true);
                    showWalls = true;
                    controller.setShowWalls(true);
                    text.setText("Wall Shown");
                    controller.draw();
                } else {
                    wallSwitch.setChecked(false);
                    showWalls = false;
                    controller.setShowWalls(false);
                    text.setText("");
                    controller.draw();
                }
            }
        });
        // Log what we're doing.
        Log.v(TAG, "Show Walls: " + showWalls);

    }

    /**
     * This method will update the text when user turned on switch
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    public void updateShowSolution() {
        // Store the view as a Switch.
        final Switch mapSolution = (Switch) findViewById(R.id.AnimationSolution);
        mapSolution.setChecked(showSolution);

        mapSolution.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mapSolution.setChecked(true);
                    showSolution = true;
                    controller.setShowSolution(true);
                    text.setText("Solution Shown");
                    controller.draw();

                } else {
                    mapSolution.setChecked(false);
                    showSolution = false;
                    controller.setShowSolution(false);
                    text.setText("");
                    controller.draw();

                }
            }
        });
        // Log what we're doing.
        Log.v(TAG, "Show Solution: " + showSolution);
    }

    /**
     * This method will get the user to the winning stage
     * if the Go2Winning button is clicked
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    public void move2Winning() {
        Log.v(TAG, "Moving to Winning class");
        intent = new Intent(this, WinningActivity.class);
        intent.putExtra("driver", driver);
        intent.putExtra("generator", generator);
        intent.putExtra("level", skill);
        startActivity(intent);
    }


    public void move2Losing() {
        Log.v(TAG, "Losing");
        intent = new Intent(this, LosingActivity.class);
        intent.putExtra("driver", driver);
        intent.putExtra("generator", generator);
        intent.putExtra("level", skill);
        startActivity(intent);
    }

    public void startandPause(View view) throws Exception {
        Log.v(TAG, "started0");
        a123 = 0;
        started = !started;

        if (t == null) {
                /*synchronized (t){
                    t.notifyAll();
                    Log.v("Thread", "thread notified");
                }
            }else{
                Log.v("Thread", "thread created");*/
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean running = true;
                    while (running  && (a123 == 0)) {
                        try {
                            if (!started) {
                                synchronized (mPauseLock) {
                                    try {
                                        mPauseLock.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            energy.setText("Energy: " + Float.toString(BasicRobot.energy));
                            robotDriver.drive2Exit();
                            Thread.sleep(20);
                        } catch (Exception e) {
                            if(e.getMessage() == "Interrupted") {
                                running = false;
                            }
                        }
                    }


                    //while (!robot.hasStopped()&& !controller.isOutside(controller.getCurrentPosition()[0], controller.getCurrentPosition()[1])){
                    //   try{
                    //       Thread.sleep(200);}
                    //    catch(Exception e){ Log.e("aaaaaa", "error2", e);}}
                    if (a123 == 0 && controller.isOutside(controller.getCurrentPosition()[0], controller.getCurrentPosition()[1])) {
                        Log.v("winnnn", "winnnn");
                        a123 = 1;
                        move2Winning();}
                    if(a123 == 0 && BasicRobot.stopped && !controller.isOutside(controller.getCurrentPosition()[0], controller.getCurrentPosition()[1])) {
                        Log.v("losingggg", Boolean.toString(BasicRobot.stopped));
                        a123 = 1;
                        move2Losing();
                    }
                }
            });
            t.start();
        }else{
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!started) {
                            Log.v("sleep", "waited");
                            synchronized (mPauseLock) {}
                        }else {
                            synchronized (mPauseLock) {
                                if(a123 == 0) {
                                    Log.v("sleep", "awaked");
                                    mPauseLock.notifyAll();
                                }
                            }
                        }
                    }
                });
            }
        }




    @Override
    public void onBackPressed() {

        a123 = 1;

        Log.v(TAG, "Moving back to AMazeActivity");
        if (Thread.interrupted()) return;
        BasicRobot.energy = 3000;
        BasicRobot.meter = 0;
        BasicRobot.stopped = false;
        BasicRobot.winning = false;
        BasicRobot.manualfault = true;
        explorer = false;

        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);

        this.finish();
    }
}


