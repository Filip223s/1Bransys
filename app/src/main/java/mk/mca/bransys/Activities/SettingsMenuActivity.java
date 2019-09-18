package mk.mca.bransys.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import mk.mca.bransys.R;

public class SettingsMenuActivity extends AppCompatActivity {

    private Button button;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen_activity);

        final NumberPicker timePicker = findViewById(R.id.timepicker);
        timePicker.setVisibility(View.VISIBLE);
        final SharedPreferences sheredpref = getSharedPreferences("sharedpref", Context.MODE_PRIVATE);
        time = sheredpref.getInt("time", 0);

        timePicker.setMaxValue(60);
        timePicker.setValue(time);
        timePicker.setMinValue(15);
        timePicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        timePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                SharedPreferences.Editor editor;

                editor = sheredpref.edit();

                editor.remove("time").commit();
                editor.putInt("time", newVal);
                editor.commit();
                time = newVal;


            }
        });


        button = findViewById(R.id.buttonOK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);

                intent.putExtra("name", time);
                startActivity(intent);
            }
        });
    }
}
