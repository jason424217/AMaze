package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.R;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeBuilder;
import android.support.constraint.ConstraintLayout;

/**
 * Class: LosingActivity
 *
 * Responsibility: Displays the finish page for the case of winning the game
 * and informs the user what happened and how to restart the game. Shows the overall energy consumption.
 * Shows the length of the path taken and the length of the shortest possible path.
 * Visualizes if robot stopped for lack of energy, or if it is broken.
 * Pressing the back button returns to State Title.
 *
 * Collaborators: AppCompatActivity, AMazeActivity
 *
 * @author Yuan Gu/Ziyue Zhou
 */
public class LosingActivity extends AppCompatActivity {


    private static final String TAG = "LosingActivity";
    private float energy;
    private int pathLength;
    private int shortestPath;
    private String driver;
    private TextView reasontext;
    private TextView energytext;
    private TextView pathtext;
    private TextView shortestpathtext;
    private boolean broken;

    /**
     * This method will create energy, shortest path, path length and reason for lose
     * and find text view's id.
     * @param savedInstanceState saved instance state
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_losing);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.losingLayout);
        AMazeActivity.setTheme(layout, AMazeActivity.theme);
        Log.v(TAG, "created");

        energy = 3000-BasicRobot.energy;
        pathLength = BasicRobot.meter;
        shortestPath = shortestPath = StaticData.getMC().getDistanceToExit(StaticData.getMC().getStartingPosition()[0],
                StaticData.getMC().getStartingPosition()[1]);
        energytext = findViewById(R.id.LosingEnergy);
        pathtext = findViewById(R.id.LosingPath);
        shortestpathtext = findViewById(R.id.LosingShortestPath);
        reasontext = findViewById(R.id.LosingReason);
        broken = BasicRobot.manualfault;
        setText();
    }

    /**
     * Set text of four text view. Print the energy text, the path text and shortest path.
     * If crashed not for broken reason, print reason text no energy else print broken.
     *  @author Yuan Gu/Ziyue Zhou
     */
    private void  setText(){
        Log.v(TAG, "Text set");
        energytext.setText("Energy Comsumption is "+energy);
        pathtext.setText("The length of path taken is "+pathLength);
        shortestpathtext.setText("The length of shortest path is "+shortestPath);
        if(broken){
            reasontext.setText("Robot stopped for lack of energy.");
        }else{
            reasontext.setText("Robot stopped because it is broken");
        }
    }

    /**
     * If pressing the back button, then returns to State Title.
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void onBackPressed(){
        Log.v(TAG, "Back Pressed");
        BasicRobot.energy = 3000;
        BasicRobot.meter = 0;
        BasicRobot.stopped = false;
        BasicRobot.winning = false;
        BasicRobot.manualfault = true;
        PlayAnimationActivity.explorer = false;
        Log.v(TAG, "Moving back to AMazeActivity class");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        this.finish();
    }
}

