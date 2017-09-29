package org.timecrafters.analyticalengine.athena;

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
import org.timecrafters.analyticalengine.R;
import org.timecrafters.analyticalengine.hermes.AppSync;

import java.io.File;
import java.util.HashMap;

public class ScoutTeamAutonomousActivity extends AppCompatActivity {
    CheckBox canScoreJewel;
    CheckBox canScoreInCryptobox;
    CheckBox canReadCryptoboxKey;
    EditText maxGlyphsScorable;
    CheckBox canParkInSafeZone;

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

        canScoreJewel       = (CheckBox) findViewById(R.id.can_score_jewel);
        canScoreInCryptobox = (CheckBox) findViewById(R.id.can_score_in_cryptobox);
        canReadCryptoboxKey = (CheckBox) findViewById(R.id.can_read_cryptobox_key);
        maxGlyphsScorable   = (EditText) findViewById(R.id.max_glyphs_scorable);
        canParkInSafeZone   = (CheckBox) findViewById(R.id.can_park_in_safe_zone);

        teamHasAutonomous = (ToggleButton) findViewById(R.id.no_autonomous);
        autonomousNotes   = (EditText) findViewById(R.id.autonomous_notes);

        teleOp = (Button) findViewById(R.id.teleOp);

        teamSelection = (Button) findViewById(R.id.team_selection);

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
                        scoutingData.put("can_score_jewel", canScoreJewel.isChecked());
                        scoutingData.put("can_score__in_cryptobox", canScoreInCryptobox.isChecked());
                        scoutingData.put("max_glyphs_scorable", Integer.parseInt(maxGlyphsScorable.getText().toString()));
                        scoutingData.put("can_park_in_safe_zone", canParkInSafeZone.isChecked());
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
                        if (data.getBoolean("can_score_jewel")) {
                            canScoreJewel.setChecked(true);
                        }
                        if (data.getBoolean("can_score_in_cryptobox")) {
                            canScoreInCryptobox.setChecked(true);
                        }
                        if (data.getInt("max_glyphs_scorable") != 0) {
                            maxGlyphsScorable.setText("" + data.getInt("max_glyphs_scorable"));
                        }
                        if (data.getBoolean("can_park_in_safe_zone")) {
                            canParkInSafeZone.setChecked(true);
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
                autonomousNotes.setText("");
            }
        }
    }

    public void enableButtons() {
        teamHasAutonomous.setEnabled(true);

        teleOp.setEnabled(true);
    }

    public void hasNoAutonomous() {
//        claimBeacons.setEnabled(false);

        teleOp.setEnabled(true);
    }
}
