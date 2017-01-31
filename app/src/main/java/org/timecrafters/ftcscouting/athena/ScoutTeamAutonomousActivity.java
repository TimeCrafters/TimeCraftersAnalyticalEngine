package org.timecrafters.ftcscouting.athena;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.hermes.AppSync;

import java.io.File;
import java.util.HashMap;

public class ScoutTeamAutonomousActivity extends AppCompatActivity {
    CheckBox claimBeacons;
    EditText beaconsClaimed;
    CheckBox scoreInVortex;
    CheckBox scoreInCorner;
    EditText particlesScoredInVortex;
    EditText particlesScoredInCorner;
    CheckBox capballOnFloor;
    CheckBox parkOnPlatform;
    CheckBox parkCompletelyOnPlatform;
    CheckBox parkOnRamp;
    CheckBox parkCompletelyOnRamp;
    CheckBox noAutonomous;

    Button teleOp;
    Button teamSelection;

    int teamNumber;
    String teamName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_team_autonomous);

        claimBeacons  = (CheckBox) findViewById(R.id.claim_beacons);
        beaconsClaimed= (EditText) findViewById(R.id.beacons_claimed);
        scoreInVortex = (CheckBox) findViewById(R.id.score_in_vortex);
        scoreInVortex = (CheckBox) findViewById(R.id.score_in_vortex);
        scoreInCorner = (CheckBox) findViewById(R.id.score_in_corner);
        particlesScoredInVortex = (EditText) findViewById(R.id.particles_scored_in_vortex);
        particlesScoredInCorner = (EditText) findViewById(R.id.particles_scored_in_corner);
        capballOnFloor = (CheckBox) findViewById(R.id.capball_on_floor);
        parkOnPlatform = (CheckBox) findViewById(R.id.park_on_platform);
        parkCompletelyOnPlatform = (CheckBox) findViewById(R.id.completely_on_platform);
        parkOnRamp = (CheckBox) findViewById(R.id.park_on_ramp);
        parkCompletelyOnRamp = (CheckBox) findViewById(R.id.completely_on_ramp);
        noAutonomous = (CheckBox) findViewById(R.id.no_autonomous);

        teleOp = (Button) findViewById(R.id.teleOp);

        teamSelection = (Button) findViewById(R.id.team_selection);

        scoreInVortex.setEnabled(false);
        scoreInCorner.setEnabled(false);
        particlesScoredInVortex.setEnabled(false);
        particlesScoredInCorner.setEnabled(false);
        capballOnFloor.setEnabled(false);
        parkOnPlatform.setEnabled(false);
        parkCompletelyOnPlatform.setEnabled(false);
        parkOnRamp.setEnabled(false);
        parkCompletelyOnRamp.setEnabled(false);
        noAutonomous.setEnabled(false);

        teleOp.setEnabled(false);

        // Select Team
        teamSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ScoutTeamAutonomousActivity.this, teamSelection);
                popupMenu.getMenu().setQwertyMode(false);

                for(HashMap.Entry<Integer, String> entry : AppSync.teamsList.entrySet()) {
                    AppSync.puts(entry.getKey().toString());
                    popupMenu.getMenu().add(""+entry.getKey()+" | "+entry.getValue());
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        teamSelection.setText(item.getTitle());
                        String[] t = teamSelection.getText().toString().split("\\|");
                        teamNumber = Integer.parseInt(t[0].replaceAll("\\s+",""));
                        teamName = t[1].substring(2); //.replace(" ","");
                        AppSync.teamNumber = teamNumber;
                        AppSync.teamName = teamName;
                        enableButtons();
                        return true;
                    }
                });
                popupMenu.show();
            }

        });

        scoreInVortex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppSync.puts("TeamAuto", "ScoreInVortex state: "+b);
                if (b) {
                    particlesScoredInVortex.setEnabled(true);
                } else {
                    particlesScoredInVortex.setEnabled(false);
                }
            }
        });

        scoreInCorner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppSync.puts("TeamAuto", "ScoreInCorner state: "+b);
                if (b) {
                    particlesScoredInCorner.setEnabled(true);
                } else {
                    particlesScoredInCorner.setEnabled(false);
                }
            }
        });

        noAutonomous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppSync.puts("TeamAuto", "autonomous state: "+b);
                if (b) {
                    hasNoAutonomous();
                } else {
                    enableButtons();
                }
            }
        });

        teleOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject scoutingData = new JSONObject();
                try {
                    scoutingData.put("claim_beacons", claimBeacons.isChecked());
                    scoutingData.put("beacons_claimed", Integer.parseInt(beaconsClaimed.getText().toString()));
                    scoutingData.put("score_in_vortex", scoreInVortex.isChecked());
                    scoutingData.put("score_in_corner", scoreInCorner.isChecked());
                    scoutingData.put("particles_scored_in_vortex", Integer.parseInt(particlesScoredInVortex.getText().toString()));
                    scoutingData.put("particles_scored_in_corner", Integer.parseInt(particlesScoredInCorner.getText().toString()));
                    scoutingData.put("capball_on_floor", capballOnFloor.isChecked());
                    scoutingData.put("park_completely_on_platform", parkCompletelyOnPlatform.isChecked());
                    scoutingData.put("park_on_platform", parkOnPlatform.isChecked());
                    scoutingData.put("park_completely_on_ramp", parkCompletelyOnRamp.isChecked());
                    scoutingData.put("park_on_ramp", parkOnRamp.isChecked());
                    scoutingData.put("has_autonomous", noAutonomous.isChecked());

                    AppSync.createDirectory(AppSync.getTeamDir()); // Ensure directory exists
                    AppSync.writeJSON(scoutingData, AppSync.getTeamDir()+ File.separator +"autonomous.json", false);
                } catch (JSONException error) {}

                startActivity(new Intent(getBaseContext(), ScoutTeamTeleOpActivity.class));

            }
        });
    }

    public void enableButtons() {
        scoreInVortex.setEnabled(true);
        scoreInCorner.setEnabled(true);
        capballOnFloor.setEnabled(true);
        parkOnPlatform.setEnabled(true);
        parkCompletelyOnPlatform.setEnabled(true);
        parkOnRamp.setEnabled(true);
        parkCompletelyOnRamp.setEnabled(true);
        noAutonomous.setEnabled(true);

        teleOp.setEnabled(true);
    }

    public void hasNoAutonomous() {
        scoreInVortex.setEnabled(false);
        scoreInCorner.setEnabled(false);
        capballOnFloor.setEnabled(false);
        parkOnPlatform.setEnabled(false);
        parkCompletelyOnPlatform.setEnabled(false);
        parkOnRamp.setEnabled(false);
        parkCompletelyOnRamp.setEnabled(false);

        teleOp.setEnabled(true);
    }
}
