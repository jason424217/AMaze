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
 * Class: WinningActivity
 *
 * Responsibilities: Displays the finish page for the case of winning the game and informs the user what
 * happened and how to restart the game. Shows the overall consumption. Shows the Shows the length of
 * the path taken and the length of the shortest possible path.If the user played the game manually,
 * only shows the length of the path taken and the length of the shortest possible path.
 * If pressing the back button, then returns to State Title.
 *
 * Collaborators: AppCompatActivity, AMazeActivity
 *
 * @author Yuan Gu/Ziyue Zhou
 */
public class WinningActivity extends AppCompatActivity {

    private static final String TAG = "WinningActivity";
    private float energy;
    private int pathLength;
    private int shortestPath;
    private String driver;
    private TextView energytext;
    private TextView pathtext;
    private TextView shortestpathtext;

    /**
     * This method will create energy, shortest path and path length
     * and find text view's id. Get driver.
     * @param savedInstanceState saved instance state
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.winningLayout);
        AMazeActivity.setTheme(layout, AMazeActivity.theme);

        energy = 3000-BasicRobot.energy;
        pathLength = BasicRobot.meter;
        shortestPath = StaticData.getMC().getDistanceToExit(StaticData.getMC().getStartingPosition()[0],
                StaticData.getMC().getStartingPosition()[1]);
        energytext = findViewById(R.id.WinningEnergy);
        pathtext = findViewById(R.id.WinningPath);
        shortestpathtext = findViewById(R.id.WinningShortest);

        Bundle extras = getIntent().getExtras();
        driver = extras.getString("driver");
        setText();
    }

    /**
     * Set text of three text view. If driver is manual, then do not print energy text.If
     * not, then print energy text. Then, print the path text and shortest path.
     * @author Yuan Gu/Ziyue Zhou
     */
    private void  setText(){
        Log.v(TAG, "Text set");
        energytext.setText("Energy Comsumption is "+energy);
        pathtext.setText("The length of path taken is "+pathLength);
        shortestpathtext.setText("The length of shortest path is "+shortestPath);
    }

    /**
     * If pressing the back button, then returns to State Title.
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
        PlayAnimationActivity.explorer = false;
        Log.v(TAG, "Moving back to AMazeActivity class");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        this.finish();
    }
}

