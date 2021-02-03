package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.File;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.R;

/**
 * Class: AmazeActivity
 *
 * Responsibility:
 * This class is responsible for presenting user a title interface. User can
 * select parameter settings: size, generation algorithm and driver algorithm.
 * I use seek bar to present options for size and spinner to present options for
 * two algorithm selections. Once the "explore" button is clicked, move to the
 * generation stage with an intent
 *
 * Collaborators: AppCompatActivity, GeneratingActivity
 *
 * @author Yuan Gu/Ziyue Zhou
 * */
public class AMazeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "AMazeActivity";
    private SeekBar sizeBar;
    private Spinner gen_spinner;
    private Spinner driver_spinner;
    private Spinner theme_spinner;
    private TextView txt;
    private TextView selectedText;
    private int genMode;
    static String theme;
    static Context context;

    /**
     * This method will create seek bar for size selection and
     * spinner for algorithm selection
     *
     * @param savedInstanceState
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaze);

        context = getApplicationContext();
        context.getDir("maze_files", Context.MODE_PRIVATE); //Creating an internal directory;

        setSize();
        set_spinner();
    }

    static void setTheme(ConstraintLayout layout, String theme){
        switch (theme){
            case "Beach":
                layout.setBackgroundResource(R.drawable.beach);
                break;
            case "Blur City":
                layout.setBackgroundResource(R.drawable.city);
                break;
            case "Star":
                layout.setBackgroundResource(R.drawable.star);
                break;
            case "Default":
                layout.setBackgroundResource(R.drawable.myback);
            default:
                break;
        }
    }
    /**
     * This private method will set up the seek bar for
     * size selection. Set default value to 0 and max value
     * to 15. Override setOnSeekBarChangeListener interface:
     * Once the user changes seek bar, present the current level
     * the user selected.
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    private void setSize(){
        Log.v(TAG, "size set");
        sizeBar = (SeekBar) findViewById(R.id.seekBar);
        sizeBar.setMax(15);
        sizeBar.setProgress(0);
        txt = findViewById(R.id.progress);
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt.setText(String.format("Level is %d/15", progress));
                selectedText.setText("Level selected: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * This private method will set up the spinner for algorithm selections
     * For two spinners, override onItemSelected callback method: Once user makes
     * a choice, display user's choice on the screen.
     *
     * @author Yuan Gu/Ziyue Zhou
     */
    private void set_spinner(){
        Log.v(TAG, "spinner get");
        gen_spinner = (Spinner) findViewById(R.id.gen_spin);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.gen_algo, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gen_spinner.setAdapter(adapter1);
        gen_spinner.setOnItemSelectedListener(this);


        driver_spinner = (Spinner) findViewById(R.id.drivespin);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.driver_algo, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driver_spinner.setAdapter(adapter2);
        driver_spinner.setOnItemSelectedListener(this);

        theme_spinner = (Spinner) findViewById(R.id.themeSpin);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.theme_algo, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theme_spinner.setAdapter(adapter3);
        theme_spinner.setOnItemSelectedListener(this);
    }

    /**
     * This private method will set up the seek bar for
     * size selection. Set default value to 0 and max value
     * to 15. Override setOnSeekBarChangeListener interface:
     * Once the user changes seek bar, present the current level
     * the user selected.
     * @param view is the view of button
     * @author Yuan Gu/Ziyue Zhou
     */
    public void move2GeneratingPlaying(View view){
        Log.v(TAG, "Moving to GeneratingActivity class");


        // Get the maze generator and size values.
        String generator = gen_spinner.getSelectedItem().toString();
        String driver = driver_spinner.getSelectedItem().toString();
        int level = sizeBar.getProgress();
        genMode = 0;

        Log.v(TAG, "Generator: " + generator + ", Driver algorithm: " + driver + ", Skill Level: " + level);

        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("generator", generator);
        intent.putExtra("driver", driver);
        intent.putExtra("level", level);
        intent.putExtra("mode", genMode);
        intent.putExtra("theme", theme);
        startActivity(intent);
    }

    public void move2GeneratingPlayingRevisit(View view){
        Log.v(TAG, "Moving to GeneratingActivity class, Revisiting");


        // Get the maze generator and size values.
        String generator = gen_spinner.getSelectedItem().toString();
        String driver = driver_spinner.getSelectedItem().toString();
        int level = sizeBar.getProgress();
        genMode = 1;

        Log.v(TAG, "Skill Level: " + level + "");

        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("generator", generator);
        intent.putExtra("driver", driver);
        intent.putExtra("level", level);
        intent.putExtra("mode", genMode);
        startActivity(intent);
    }

    /**
     * This method override response to user's selection in spinner.
     * Once user selected an option, a corresponding message will be
     * presented on screen to confirm the selected item is indeed selected.
     *
     * @param parent is the corresponding spinner
     * @param position is where is the selected item in the spinner
     * @param view is the what we see
     * @author Yuan Gu/Ziyue Zhou
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.v(TAG, "Selected");
        selectedText = findViewById(R.id.selected);
        String selected = (String) parent.getItemAtPosition(position);
        switch (parent.getId()){
            case R.id.gen_spin:
                selectedText.setText("Builder selected: " + selected);
                break;
            case R.id.drivespin:
                selectedText.setText("Driver selected: " + selected);
                break;
            case R.id.themeSpin:
                selectedText.setText("Theme Selected: " + selected);
                theme = selected;
                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.amazeLayout);
                setTheme(layout, theme);
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }
}