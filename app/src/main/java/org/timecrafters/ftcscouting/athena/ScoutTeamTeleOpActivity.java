package org.timecrafters.ftcscouting.athena;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.timecrafters.ftcscouting.MainActivity;
import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.hermes.AppSync;

import java.io.File;

public class ScoutTeamTeleOpActivity extends AppCompatActivity {
    TextView team;
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

        team = (TextView) findViewById(R.id.team);
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

        Button save = (Button) findViewById(R.id.save);

        team.setText(""+AppSync.teamNumber+" | "+ AppSync.teamName);
        maxBeaconsClaimable.setEnabled(false);
        maxParticlesScoredInVortex.setEnabled(false);
        maxParticlesScoredInCorner.setEnabled(false);

        populateFields();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject scoutingData = new JSONObject();
                try {
                    if (maxBeaconsClaimable.getText().length() < 1) {
                        maxBeaconsClaimable.setText("0");
                    }
                    if (maxParticlesScoredInVortex.getText().length() < 1) {
                        maxParticlesScoredInVortex.setText("0");
                    }
                    if (maxParticlesScoredInCorner.getText().length() < 1) {
                        maxParticlesScoredInCorner.setText("0");
                    }

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

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(new File(AppSync.getTeamDir() + File.separator + "teleop.json")));
                    sendBroadcast(intent);
                } catch (JSONException error) {
                    AppSync.puts("TELE", "Failed to write teleOp data: " +error.getMessage());
                }
                finish();
                startActivityIfNeeded(new Intent(getBaseContext(), MainActivity.class), 99);
            }
        });

        canClaimBeacons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    maxBeaconsClaimable.setEnabled(true);
                } else {
                    maxBeaconsClaimable.setEnabled(false);
                }
            }
        });

        canScoreInVortex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    maxParticlesScoredInVortex.setEnabled(true);
                } else {
                    maxParticlesScoredInVortex.setEnabled(false);
                }
            }
        });

        canScoreInCorner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    maxParticlesScoredInCorner.setEnabled(true);
                } else {
                    maxParticlesScoredInCorner.setEnabled(false);
                }
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

    public void populateFields() {
        if (AppSync.teamHasScoutingData()) {
            JSONObject data = AppSync.teamScoutingData("teleop");

            if (data != null) {
                try {
                    if (data.getBoolean("can_claim_beacons")) {
                        canClaimBeacons.setChecked(true);
                        maxBeaconsClaimable.setText("" + data.getInt("max_beacons_claimable"));
                        maxBeaconsClaimable.setEnabled(true);
                    }
                    if (data.getBoolean("can_score_in_vortex")) {
                        canScoreInVortex.setChecked(true);
                        maxParticlesScoredInVortex.setText("" + data.getInt("max_particles_scored_in_vortex"));
                        maxParticlesScoredInVortex.setEnabled(true);
                    }
                    if (data.getBoolean("can_score_in_corner")) {
                        canScoreInCorner.setChecked(true);
                        maxParticlesScoredInCorner.setText("" + data.getInt("max_particles_scored_in_corner"));
                        maxParticlesScoredInCorner.setEnabled(true);
                    }
                    if (data.getBoolean("capball_off_floor")) {
                        capballOffFloor.setChecked(true);
                    }
                    if (data.getBoolean("capball_above_crossbar")) {
                        capballAboveCrossbar.setChecked(true);
                    }
                    if (data.getBoolean("capball_capped")) {
                        capballCapped.setChecked(true);
                    }
                    if (data.getString("teleop_notes").length() > 0) {
                        teleOpNotes.setText(data.getString("teleop_notes"));
                    }
                } catch (JSONException | NullPointerException error) {
                    Toast.makeText(this, "An error occurred: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                canClaimBeacons.setChecked(false);
                maxBeaconsClaimable.setText("");

                canScoreInVortex.setChecked(false);
                maxParticlesScoredInVortex.setText("");
                canScoreInCorner.setChecked(false);
                maxParticlesScoredInCorner.setText("");

                capballOffFloor.setChecked(false);
                capballAboveCrossbar.setChecked(false);
                capballCapped.setChecked(false);

                teleOpNotes.setText("");
            }
        }
    }
}
