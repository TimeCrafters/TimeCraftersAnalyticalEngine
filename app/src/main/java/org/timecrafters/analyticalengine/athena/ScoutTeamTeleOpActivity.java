package org.timecrafters.analyticalengine.athena;

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
import org.timecrafters.analyticalengine.MainActivity;
import org.timecrafters.analyticalengine.R;
import org.timecrafters.analyticalengine.hermes.AppSync;

import java.io.File;

public class ScoutTeamTeleOpActivity extends AppCompatActivity {
    TextView team;
    CheckBox glyphCanScoreGlyphs;
    EditText glyphMaxScorable;
    CheckBox glyphCanCompleteCipher;

    CheckBox relicCanScoreRelic;
    CheckBox relicZoneOne;
    CheckBox relicZoneTwo;
    CheckBox relicZoneThree;
    CheckBox relicUpright;

    CheckBox balanceCanBalance;

    EditText teleOpNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_team_tele_op);

        team = (TextView) findViewById(R.id.team);

        glyphCanScoreGlyphs   = (CheckBox) findViewById(R.id.glyph_can_score);
        glyphMaxScorable      = (EditText) findViewById(R.id.glyph_max_scorable);
        glyphCanCompleteCipher= (CheckBox) findViewById(R.id.glyph_can_complete_cipher);

        relicCanScoreRelic = (CheckBox) findViewById(R.id.relic_can_score);
        relicZoneOne       = (CheckBox) findViewById(R.id.relic_zone_1);
        relicZoneTwo       = (CheckBox) findViewById(R.id.relic_zone_2);
        relicZoneThree     = (CheckBox) findViewById(R.id.relic_zone_3);
        relicUpright       = (CheckBox) findViewById(R.id.relic_upright);

        balanceCanBalance = (CheckBox) findViewById(R.id.balance_on_stone);

        teleOpNotes = (EditText) findViewById(R.id.teleop_notes);

        Button save = (Button) findViewById(R.id.save);

        team.setText(""+AppSync.teamNumber+" | "+ AppSync.teamName);
        glyphMaxScorable.setEnabled(false);
        glyphCanCompleteCipher.setEnabled(false);

        relicZoneOne.setEnabled(false);
        relicZoneTwo.setEnabled(false);
        relicZoneThree.setEnabled(false);
        relicUpright.setEnabled(false);

        populateFields();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject scoutingData = new JSONObject();
                try {
                    if (glyphMaxScorable.getText().length() < 1) {
                        glyphMaxScorable.setText("0");
                    }

                    scoutingData.put("can_score_in_cryptobox", glyphCanScoreGlyphs.isChecked());
                    scoutingData.put("max_scorable_glyphs", Integer.parseInt(glyphMaxScorable.getText().toString()));
                    scoutingData.put("can_complete_cipher", glyphCanCompleteCipher.isChecked());

                    scoutingData.put("can_score_relic", relicCanScoreRelic.isChecked());
                    scoutingData.put("relic_zone_1", relicZoneOne.isChecked());
                    scoutingData.put("relic_zone_2", relicZoneTwo.isChecked());
                    scoutingData.put("relic_zone_3", relicZoneThree.isChecked());
                    scoutingData.put("relic_upright", relicUpright.isChecked());

                    scoutingData.put("can_balance_on_stone", balanceCanBalance.isChecked());

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

        glyphCanScoreGlyphs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    glyphMaxScorable.setEnabled(true);
                    glyphCanCompleteCipher.setEnabled(true);
                } else {
                    glyphMaxScorable.setEnabled(false);
                    glyphCanCompleteCipher.setEnabled(false);
                }
            }
        });

        relicCanScoreRelic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    relicZoneOne.setEnabled(true);
                    relicZoneTwo.setEnabled(true);
                    relicZoneThree.setEnabled(true);
                    relicUpright.setEnabled(true);
                } else {
                    relicZoneOne.setEnabled(false);
                    relicZoneTwo.setEnabled(false);
                    relicZoneThree.setEnabled(false);
                    relicUpright.setEnabled(false);
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
                    if (data.getBoolean("can_score_in_cryptobox")) {
                        glyphCanScoreGlyphs.setChecked(true);
                        glyphMaxScorable.setText("" + data.getInt("max_scorable_glyphs"));
                        glyphMaxScorable.setEnabled(true);
                        glyphCanCompleteCipher.setEnabled(true);
                    }
                    if (data.getBoolean("can_complete_cipher")) {
                        glyphCanCompleteCipher.setChecked(true);
                    }
                    if (data.getBoolean("can_score_relic")) {
                        relicCanScoreRelic.setChecked(true);
                        relicZoneOne.setEnabled(true);
                        relicZoneTwo.setEnabled(true);
                        relicZoneThree.setEnabled(true);
                        relicUpright.setEnabled(true);
                    }
                    if (data.getBoolean("relic_zone_1")) {
                        relicZoneOne.setChecked(true);
                    }
                    if (data.getBoolean("relic_zone_2")) {
                        relicZoneTwo.setChecked(true);
                    }
                    if (data.getBoolean("relic_zone_3")) {
                        relicZoneThree.setChecked(true);
                    }
                    if (data.getBoolean("relic_upright")) {
                        relicUpright.setChecked(true);
                    }
                    if (data.getBoolean("can_balance_on_stone")) {
                        balanceCanBalance.setChecked(true);
                    }
                    if (data.getString("teleop_notes").length() > 0) {
                        teleOpNotes.setText(data.getString("teleop_notes"));
                    }
                } catch (JSONException | NullPointerException error) {
                    Toast.makeText(this, "An error occurred: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                glyphCanScoreGlyphs.setChecked(false);
                glyphMaxScorable.setText("");
                glyphCanCompleteCipher.setChecked(false);

                relicCanScoreRelic.setChecked(false);
                relicZoneOne.setChecked(false);
                relicZoneTwo.setChecked(false);
                relicZoneThree.setChecked(false);
                relicUpright.setChecked(false);

                balanceCanBalance.setChecked(false);

                teleOpNotes.setText("");
            }
        }
    }
}
