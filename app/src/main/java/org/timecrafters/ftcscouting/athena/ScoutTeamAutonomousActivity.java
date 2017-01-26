package org.timecrafters.ftcscouting.athena;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.NumberPicker;

import org.timecrafters.ftcscouting.R;

public class ScoutTeamAutonomousActivity extends AppCompatActivity {
    CheckBox scoreInVortex;
    CheckBox scoreInCorner;
    NumberPicker particlesScoredInVortex;
    NumberPicker particlesScoredInCorner;
    CheckBox capballOnFloor;
    CheckBox parkOnPlatform;
    CheckBox parkCompletelyOnPlatform;
    CheckBox parkOnRamp;
    CheckBox parkCompletelyOnRamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_team_autonomous);

        scoreInVortex = (CheckBox) findViewById(R.id.score_in_vortex);
        scoreInCorner = (CheckBox) findViewById(R.id.score_in_corner);
    }
}
