package org.timecrafters.ftcscouting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public HashMap<Integer, HashMap<String, Object>> teamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button scoutMatch = (Button) findViewById(R.id.scout_match);
        scoutMatch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ScoutMatchActivity.class));
            }
        });
    }
}
