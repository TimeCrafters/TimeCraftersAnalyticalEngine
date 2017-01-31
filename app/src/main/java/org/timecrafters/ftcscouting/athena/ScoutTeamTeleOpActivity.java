package org.timecrafters.ftcscouting.athena;

import android.content.Intent;
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
    CheckBox canClaimBeacons;
    EditText maxBeaconsClaimable;

    CheckBox canScoreInVortex;
    EditText maxParticlesScoredInVortex;

    CheckBox canScoreInCorner;
    EditText maxParticlesScoredInCorner;

    CheckBox capballOffFloor;
    CheckBox capballAboveCrossbar;
    CheckBox capballCapped;
    EditText teleOpNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_team_tele_op);
        canClaimBeacons = (CheckBox) findViewById(R.id.can_claim_beacons);
        maxBeaconsClaimable = (EditText) findViewById(R.id.max_beacons_claimable);

        canScoreInVortex = (CheckBox) findViewById(R.id.can_score_in_vortex);
        maxParticlesScoredInVortex = (EditText) findViewById(R.id.max_particles_scored_in_vortex);

        canScoreInCorner = (CheckBox) findViewById(R.id.can_score_in_corner);
        maxParticlesScoredInCorner = (EditText) findViewById(R.id.max_particles_scored_in_corner);

        capballOffFloor = (CheckBox) findViewById(R.id.capball_off_floor);
        capballAboveCrossbar = (CheckBox) findViewById(R.id.capball_above_crossbar);
        capballCapped = (CheckBox) findViewById(R.id.capball_capped);
        teleOpNotes = (EditText) findViewById(R.id.teleop_notes);

        maxBeaconsClaimable.setFocusableInTouchMode(true);
        maxBeaconsClaimable.requestFocus();



        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
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

                    scoutingData.put("teleop_notes", teleOpNotes.getText());

                    AppSync.createDirectory(AppSync.getTeamDir()); // Ensure directory exists
                    AppSync.writeJSON(scoutingData, AppSync.getTeamDir()+ File.separator +"teleop.json", false);
                } catch (JSONException error) {}
                finish();
                startActivityIfNeeded(new Intent(getBaseContext(), MainActivity.class), 99);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AppSync.createConfirmDialog(this, "Are you sure?", "You will lose your input from here.", new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, new Runnable() {
            @Override
            public void run() {
                // no
            }
        });
    }
}
