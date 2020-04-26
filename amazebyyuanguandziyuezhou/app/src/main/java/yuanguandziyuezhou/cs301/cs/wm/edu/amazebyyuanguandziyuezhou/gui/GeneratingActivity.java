package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.constraint.ConstraintLayout;

import java.io.File;
import java.io.IOException;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.R;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.BSPNode;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Cells;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Distance;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeBuilder;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeConfiguration;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeContainer;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeFactory;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.Order;

/**
 * Class: GeneratingActivity
 *
 * Responsibility:
 * This class is responsible for generating an actual maze.
 *
 * Collaborators: AppCompatActivity, PlayingManuallyActivity, PlayingAnimationActivity
 *
 * @author Yuan Gu/Ziyue Zhou
 * */

public class GeneratingActivity extends AppCompatActivity implements Order{

    private static final String TAG = "GeneratingActivity";
    private ProgressBar progressBar = null;
    private int skill;
    private int genMode;
    private String driver;
    private String generator;
    private Builder builder;
    private TextView txt;

    private MazeFactory mazeFactory;
    private MazeConfiguration mazeConfiguration;
    private MazeFileReader mfr;
    private int w, h, rooms, expected, startX, startY;
    private BSPNode root;
    private int[][] dists;
    private Cells cells;
    private String filename;
    private boolean perfect;
    private Thread mazeThread;
    private Handler myHandler;
    /**
     * This method will receive user's input from title interface.
     * Initialize progress bar and execute UI thread
     *
     * @param savedInstanceState
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.generatingLayout);
        AMazeActivity.setTheme(layout, AMazeActivity.theme);

        Bundle extras = getIntent().getExtras();
        skill = extras.getInt("level");
        driver = extras.getString("driver");
        generator = extras.getString("generator");
        builder = getBuilder();
        genMode = extras.getInt("mode");
        Log.v("mode", "Mode: " + genMode);

        myHandler = new Handler();
        progressBar = (ProgressBar) findViewById(R.id.pb);
        txt = (TextView) findViewById(R.id.percentage);

        // initialize all variables to default
        mazeFactory = new MazeFactory();

        cells = null;
        root = null;
        mfr = null;
        perfect = false; // can be perfect or not

        if (genMode == 0) mazeThread = mazeGenerationThread();
        else if (genMode == 1) mazeThread = mazeLoadingThread();

        mazeThread.start();
    }

    private Thread mazeGenerationThread(){

        return new Thread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(0);
                mazeFactory.order(GeneratingActivity.this);
                mazeFactory.waitTillDelivered();
                mazeConfiguration = StaticData.getMC();
                Log.v(TAG, "New maze is generated");
                // if selected skill level is 0-3, save the maze as xml file
                if (skill < 4) saveMaze();
            }
        });
    }

    private Thread mazeLoadingThread(){

        return new Thread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(0);
                try{
                    // there is a file existed
                    updateProgress(20);
                    String filename1 = AMazeActivity.context.getFilesDir().getPath() + "/maze" + skill + ".xml";
                    updateProgress(40);
                    mfr = new MazeFileReader(filename1);
                    updateProgress(60);
                    mazeConfiguration = mfr.getMazeConfiguration();
                    updateProgress(80);
                    StaticData.setMC(mazeConfiguration);
                    updateProgress(100);
                    deliver(StaticData.getMC());

                    Log.v(TAG, "file existed, loading maze" + skill);

                }catch(Exception e) {
                    // loading failed; No file existed; generate a new one
                    Log.v(TAG, "No maze stored for " + skill + "-level maze");
                    Log.v(TAG, "Then generating and saving a new Maze file...");
                    mazeThread = mazeGenerationThread();
                    mazeThread.start();

                    try {
                        mazeThread.join();
                    } catch (Exception exc) {
                        Log.e(TAG, "Maze generation thread being interrupted");
                    }
                }
            }
        });
    }

    private void saveMaze(){
        w = mazeConfiguration.getWidth();
        h = mazeConfiguration.getHeight();
        root = mazeConfiguration.getRootnode();
        cells = mazeConfiguration.getMazecells();
        startX = mazeConfiguration.getStartingPosition()[0];
        startY = mazeConfiguration.getStartingPosition()[1];
        Distance distance = new Distance(w, h);
        dists = distance.getAllDistanceValues();
        filename = "maze" + skill + ".xml";
        rooms = Constants.SKILL_ROOMS[skill];
        expected = Constants.SKILL_PARTCT[skill];

        String filePath = AMazeActivity.context.getFilesDir().getPath() + "/" + filename;

        MazeFileWriter.store(filePath, w, h, rooms, expected, root, cells, dists, startX, startY);
        Log.v("save", "saving maze");
    }

    @Override
    public int getSkillLevel() {
        return skill;
    }

    @Override
    public Builder getBuilder() {
        switch (generator){
            case "DFS" :
                builder = Order.Builder.DFS;
                break;
            case "Prim":
                builder = Order.Builder.Prim;
                break;
            case "Eller":
                builder = Order.Builder.Eller;
                break;
            default:
                Log.v(TAG, "Maze not built");
                builder = null;
                break;
        }
        return builder;
    }

    @Override
    public boolean isPerfect() {
        return perfect;
    }

    @Override
    public void deliver(MazeConfiguration mazeConfig) {

        move2Playing();
    }

    @Override
    public void updateProgress(int progress) {
        final int num = progress;

        myHandler.post(new Runnable() {
            public void run() {
                progressBar.setProgress(num);
                txt.setText("Generating maze: " + num + "% completed");
                if (mazeThread.isInterrupted())return;
            }
        });
    }

    /**
     * This method will move to playing stage based on driver's type
     * @author Yuan Gu/Ziyue Zhou
     */
    private void move2Playing(){

        if (driver.contentEquals("Manual")){
            Log.v(TAG, "Moving to PlayManuallyActivity class");
            Intent intent = new Intent(this, PlayManuallyActivity.class);
            intent.putExtra("driver", driver);
            intent.putExtra("generator", generator);
            intent.putExtra("level", skill);
            startActivity(intent);
        }
        else {
            Log.v(TAG, "Moving to PlayAnimationActivity class");
            Intent intent = new Intent(this, PlayAnimationActivity.class);
            intent.putExtra("driver", driver);
            intent.putExtra("generator", generator);
            intent.putExtra("level", skill);
            startActivity(intent);
        }
    }

    /**
     * This method will get the user back to title interface if
     * he or she presses back button
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void onBackPressed(){

        mazeThread.interrupt();
        mazeFactory.cancel();

        Log.v(TAG, "Moving back to AMazeActivity");

        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);

        this.finish();
    }
}