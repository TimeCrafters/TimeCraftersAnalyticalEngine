package org.timecrafters.ftcscouting.athena;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
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
    ToggleButton teamHasAutonomous;
    EditText autonomousNotes;

    Button teleOp;
    Button teamSelection;

    int teamNumber;
    String teamName;
    boolean hasAutonomous = true;

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
        teamHasAutonomous = (ToggleButton) findViewById(R.id.no_autonomous);
        autonomousNotes = (EditText) findViewById(R.id.autonomous_notes);

        teleOp = (Button) findViewById(R.id.teleOp);

        teamSelection = (Button) findViewById(R.id.team_selection);

        claimBeacons.setEnabled(false);
        beaconsClaimed.setEnabled(false);
        scoreInVortex.setEnabled(false);
        scoreInCorner.setEnabled(false);
        particlesScoredInVortex.setEnabled(false);
        particlesScoredInCorner.setEnabled(false);
        capballOnFloor.setEnabled(false);
        parkOnPlatform.setEnabled(false);
        parkCompletelyOnPlatform.setEnabled(false);
        parkOnRamp.setEnabled(false);
        parkCompletelyOnRamp.setEnabled(false);
        teamHasAutonomous.setEnabled(false);

        teleOp.setEnabled(false);

        teamHasAutonomous.setText("Have Autonomous");
        teamHasAutonomous.setTextOff("No Autonomous");
        teamHasAutonomous.setTextOn("Have Autonomous");
        teamHasAutonomous.setChecked(true);

        // Select Team
        teamSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ScoutTeamAutonomousActivity.this, teamSelection);
                popupMenu.getMenu().setQwertyMode(false);

                for(HashMap.Entry<Integer, String> entry : AppSync.teamsList.entrySet()) {
                    AppSync.puts(entry.getKey().toString());
                    MenuItem temp = popupMenu.getMenu().add("");
                    SpannableString team_number = new SpannableString(""+entry.getKey()+ " | " +""+entry.getValue());

                    if (AppSync.teamHasScoutingData(entry.getKey())) {
                        team_number.setSpan(new ForegroundColorSpan(Color.rgb(0, 150, 0)), 0, team_number.length(), 0);
                    } else {
                        team_number.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, team_number.length(), 0);
                    }

                    temp.setTitle(TextUtils.concat(team_number));

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

                        populateFields();
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

        claimBeacons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    beaconsClaimed.setEnabled(true);
                } else {
                    beaconsClaimed.setEnabled(false);
                }
            }
        });

        teamHasAutonomous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasAutonomous) {
                    hasAutonomous = false;
                    hasNoAutonomous();
                } else {
                    hasAutonomous = true;
                    enableButtons();
                }
            }
        });

        teleOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject scoutingData = new JSONObject();
                try {
                    AppSync.puts("SCOUTING_AUTO", "TEAM HAS AUTONOMOUS? "+ teamHasAutonomous.isChecked());
                    if (teamHasAutonomous.isChecked()) {
                        if (beaconsClaimed.getText().length() < 1) {
                            beaconsClaimed.setText("0");
                        }
                        if (particlesScoredInVortex.getText().length() < 1) {
                            particlesScoredInVortex.setText("0");
                        }
                        if (particlesScoredInCorner.getText().length() < 1) {
                            particlesScoredInCorner.setText("0");
                        }

                        scoutingData.put("can_claim_beacons", claimBeacons.isChecked());
                        scoutingData.put("max_beacons_claimable", Integer.parseInt(beaconsClaimed.getText().toString()));

                        scoutingData.put("can_score_in_vortex", scoreInVortex.isChecked());
                        scoutingData.put("max_particles_scored_in_vortex", Integer.parseInt(particlesScoredInVortex.getText().toString()));

                        scoutingData.put("can_score_in_corner", scoreInCorner.isChecked());
                        scoutingData.put("max_particles_scored_in_corner", Integer.parseInt(particlesScoredInCorner.getText().toString()));

                        scoutingData.put("capball_on_floor", capballOnFloor.isChecked());

                        scoutingData.put("park_completely_on_platform", parkCompletelyOnPlatform.isChecked());
                        scoutingData.put("park_on_platform", parkOnPlatform.isChecked());
                        scoutingData.put("park_completely_on_ramp", parkCompletelyOnRamp.isChecked());
                        scoutingData.put("park_on_ramp", parkOnRamp.isChecked());
                    }
                    scoutingData.put("has_autonomous", teamHasAutonomous.isChecked());
                    scoutingData.put("autonomous_notes", autonomousNotes.getText()); // Notes always get written

                    AppSync.createDirectory(AppSync.getTeamDir()); // Ensure directory exists
                    AppSync.writeJSON(scoutingData, AppSync.getTeamDir() + File.separator + "autonomous.json", false);

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(new File(AppSync.getTeamDir() + File.separator + "autonomous.json")));
                    sendBroadcast(intent);

                } catch (JSONException error) {}

                startActivity(new Intent(getBaseContext(), ScoutTeamTeleOpActivity.class));

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
            JSONObject data = AppSync.teamScoutingData("autonomous");

            if (data != null) {
                try {
                    if (data.getBoolean("has_autonomous")) {
                        if (data.getBoolean("can_claim_beacons")) {
                            claimBeacons.setChecked(true);
                            beaconsClaimed.setText("" + data.getInt("max_beacons_claimable"));
                        }
                        if (data.getBoolean("can_score_in_vortex")) {
                            scoreInVortex.setChecked(true);
                            particlesScoredInVortex.setText("" + data.getInt("max_particles_scored_in_vortex"));
                        }
                        if (data.getBoolean("can_score_in_corner")) {
                            scoreInCorner.setChecked(true);
                            particlesScoredInCorner.setText("" + data.getInt("max_particles_scored_in_corner"));
                        }
                        if (data.getBoolean("capball_on_floor")) {
                            capballOnFloor.setChecked(true);
                        }
                        if (data.getBoolean("park_completely_on_platform")) {
                            parkCompletelyOnPlatform.setChecked(true);
                        }
                        if (data.getBoolean("park_completely_on_ramp")) {
                            parkCompletelyOnRamp.setChecked(true);
                        }
                        if (data.getBoolean("park_on_platform")) {
                            parkOnPlatform.setChecked(true);
                        }
                        if (data.getBoolean("park_on_ramp")) {
                            parkOnRamp.setChecked(true);
                        }
                        if (data.getString("autonomous_notes").length() > 0) {
                            autonomousNotes.setText(data.getString("autonomous_notes"));
                        }
                    } else {
                        hasNoAutonomous();
                        teamHasAutonomous.setChecked(false);
                    }
                } catch (JSONException | NullPointerException error) {
                    Toast.makeText(this, "An error occurred: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                claimBeacons.setChecked(false);
                beaconsClaimed.setText("");

                scoreInVortex.setChecked(false);
                particlesScoredInVortex.setText("");
                scoreInCorner.setChecked(false);
                particlesScoredInCorner.setText("");

                capballOnFloor.setChecked(false);

                parkCompletelyOnPlatform.setChecked(false);
                parkCompletelyOnRamp.setChecked(false);
                parkOnPlatform.setChecked(false);
                parkOnRamp.setChecked(false);

                autonomousNotes.setText("");
            }
        }
    }

    public void enableButtons() {
        claimBeacons.setEnabled(true);
        scoreInVortex.setEnabled(true);
        scoreInCorner.setEnabled(true);
        capballOnFloor.setEnabled(true);
        parkOnPlatform.setEnabled(true);
        parkCompletelyOnPlatform.setEnabled(true);
        parkOnRamp.setEnabled(true);
        parkCompletelyOnRamp.setEnabled(true);
        teamHasAutonomous.setEnabled(true);

        if (claimBeacons.isChecked()) { beaconsClaimed.setEnabled(true); }
        if (scoreInVortex.isChecked()) { particlesScoredInVortex.setEnabled(true); }
        if (scoreInCorner.isChecked()) { particlesScoredInCorner.setEnabled(true); }

        teleOp.setEnabled(true);
    }

    public void hasNoAutonomous() {
        claimBeacons.setEnabled(false);
        beaconsClaimed.setEnabled(false);
        scoreInVortex.setEnabled(false);
        particlesScoredInVortex.setEnabled(false);
        scoreInCorner.setEnabled(false);
        particlesScoredInCorner.setEnabled(false);
        capballOnFloor.setEnabled(false);
        parkOnPlatform.setEnabled(false);
        parkCompletelyOnPlatform.setEnabled(false);
        parkOnRamp.setEnabled(false);
        parkCompletelyOnRamp.setEnabled(false);

        teleOp.setEnabled(true);
    }
}
