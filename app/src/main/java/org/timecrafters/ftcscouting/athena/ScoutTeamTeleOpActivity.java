package org.timecrafters.ftcscouting.athena;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.timecrafters.ftcscouting.MainActivity;
import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.hermes.AppSync;

import java.io.File;

public class ScoutTeamTeleOpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_team_tele_op);
        final CheckBox canClaimBeacons = (CheckBox) findViewById(R.id.can_claim_beacons);
        final EditText maxBeaconsClaimable = (EditText) findViewById(R.id.max_beacons_claimable);

        final CheckBox canScoreInVortex = (CheckBox) findViewById(R.id.can_score_in_vortex);
        final EditText maxParticlesScoredInVortex = (EditText) findViewById(R.id.max_particles_scored_in_vortex);

        final CheckBox canScoreInCorner = (CheckBox) findViewById(R.id.can_score_in_corner);
        final EditText maxParticlesScoredInCorner = (EditText) findViewById(R.id.max_particles_scored_in_corner);

        final CheckBox capballOffFloor = (CheckBox) findViewById(R.id.capball_off_floor);
        final CheckBox capballAboveCrossbar = (CheckBox) findViewById(R.id.capball_above_crossbar);
        final CheckBox capballCapped = (CheckBox) findViewById(R.id.capball_capped);



        Button done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject scoutingData = new JSONObject();
                try {
                    scoutingData.put("can_claim_beacons", canClaimBeacons.isChecked());
                    scoutingData.put("max_beacons_claimable", Integer.parseInt(maxBeaconsClaimable.getText().toString()));

                    scoutingData.put("can_score_in_vortex", canScoreInVortex.isChecked());
                    scoutingData.put("max_particles_scored_in_vortex", Integer.parseInt(maxParticlesScoredInVortex.getText().toString()));

                    scoutingData.put("can_score_in_corner", canScoreInCorner.isChecked());
                    scoutingData.put("max_particles_scored_in_corner", Integer.parseInt(maxParticlesScoredInCorner.getText().toString()));

                    scoutingData.put("capball_off_floor", capballOffFloor.isChecked());
                    scoutingData.put("capball_above_crossbar", capballAboveCrossbar.isChecked());
                    scoutingData.put("capball_capped", capballCapped.isChecked());

                    AppSync.createDirectory(AppSync.getTeamDir()); // Ensure directory exists
                    AppSync.writeJSON(scoutingData, AppSync.getTeamDir()+ File.separator +"teleop.json", false);
                } catch (JSONException error) {}
            }
        });
    }

    @Override
    public void onBackPressed() {
        MainActivity.MainActivityContext.createConfirmDialog("Are you sure?", "You will lose your input.", new Runnable() {
            @Override
            public void run() {
                //
            }
        }, new Runnable() {
            @Override
            public void run() {
                //
            }
        });
    }
}
