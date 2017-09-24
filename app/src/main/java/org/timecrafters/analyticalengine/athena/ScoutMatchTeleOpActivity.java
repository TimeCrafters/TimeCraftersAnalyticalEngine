package org.timecrafters.analyticalengine.athena;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.timecrafters.analyticalengine.R;
import org.timecrafters.analyticalengine.apollo.TeleScoresHelper;
import org.timecrafters.analyticalengine.hermes.AppSync;
import org.timecrafters.analyticalengine.hermes.EventStruct;

import java.io.File;

public class ScoutMatchTeleOpActivity extends AppCompatActivity {
    EventStruct relicEvent;

    public Button glyphScored;
    public Button glyphCompletedRow;
    public Button glyphCompletedColumn;
    public Button glyphCompletedCipher;
    public Button glyphMissed;

    public Button robotBalanced;
    public Button robotFailedToBalance;

    public Button relic;
    public ToggleButton relicUpright;

    public ToggleButton deadRobot;

    public Button undo;
    public Button done;

    public TextView currentTeam;
    public TextView scoreText;

    public ScrollView scrollView;
    public TextView eventLog;

    public int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_match_tele_op);

        robotBalanced        = (Button) findViewById(R.id.robot_balanced);
        robotFailedToBalance = (Button) findViewById(R.id.robot_failed_balance);

        deadRobot = (ToggleButton) findViewById(R.id.robot_dead);

        glyphScored          = (Button) findViewById(R.id.glyph_scored);
        glyphCompletedRow    = (Button) findViewById(R.id.glyph_completed_row);
        glyphCompletedColumn = (Button) findViewById(R.id.glyph_completed_column);
        glyphCompletedCipher = (Button) findViewById(R.id.glyph_completed_cipher);
        glyphMissed          = (Button) findViewById(R.id.glyph_missed);

        relic        = (Button) findViewById(R.id.relic);
        relicUpright = (ToggleButton) findViewById(R.id.relic_upright);

        undo = (Button) findViewById(R.id.undo);
        done = (Button) findViewById(R.id.done);

        currentTeam = (TextView) findViewById(R.id.team);
        scoreText = (TextView) findViewById(R.id.score);

        scrollView = (ScrollView) findViewById(R.id.scrollview);
        eventLog   = (TextView) findViewById(R.id.teleop_log);

        currentTeam.setText(""+ AppSync.teamNumber+" | "+AppSync.teamName);

        deadRobot.setText("Alive");
        deadRobot.setTextOn("Alive");
        deadRobot.setTextOff("DEAD");

        glyphScored.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "scored", "glyph", "", TeleScoresHelper.glyphScored, "Scored a glyph");
                refreshEventLog();
            }
        });

        glyphCompletedRow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "scored", "glyph", "row", TeleScoresHelper.glyphRowCompleted, "Completed Row of in Cryptobox");
                refreshEventLog();
            }
        });

        glyphCompletedColumn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "scored", "glyph", "column", TeleScoresHelper.glyphColumnCompleted, "Completed Column in Cryptobox");
                refreshEventLog();
            }
        });

        glyphCompletedCipher.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "scored", "glyph", "cipher", TeleScoresHelper.glyphCipherCompleted, "Completed Cryptobox Cipher");
                glyphCompletedCipher.setEnabled(false);
                refreshEventLog();
            }
        });

        glyphMissed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "missed", "glyph", "", 0, "Missed Scoring Glyph in Cryptobox");
                refreshEventLog();
            }
        });

        robotBalanced.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "scored", "balance", "", TeleScoresHelper.robotBanlanced, "Robot Balanced on Stone");
                robotBalanced.setEnabled(false);
                robotFailedToBalance.setEnabled(false);
                refreshEventLog();
            }
        });

        robotFailedToBalance.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "missed", "balance", "", 0, "Robot Failed to Balance on Stone");
                robotBalanced.setEnabled(false);
                robotFailedToBalance.setEnabled(false);
                refreshEventLog();
            }
        });

        relic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ScoutMatchTeleOpActivity.this, relic);
                popupMenu.getMenuInflater().inflate(R.menu.menu_teleop_relic_state, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.none: {
                                relicEvent = null;
                                relic.setText(item.getTitle());
                                refreshEventLog();
                                break;
                            }
                            case R.id.relic_zone_one: {
                                relicEvent = new EventStruct(AppSync.teamNumber, "teleop", "scored", "relic", "zone_one", TeleScoresHelper.relicInZoneOne, "Relic Scored in Zone 1");
                                relic.setText(item.getTitle());
                                refreshEventLog();
                                break;
                            }
                            case R.id.relic_zone_two: {
                                relicEvent = new EventStruct(AppSync.teamNumber, "teleop", "scored", "relic", "zone_two", TeleScoresHelper.relicInZoneTwo, "Relic Scored in Zone 2");
                                relic.setText(item.getTitle());
                                refreshEventLog();
                                break;
                            }
                            case R.id.relic_zone_three: {
                                relicEvent = new EventStruct(AppSync.teamNumber, "teleop", "scored", "relic", "zone_three", TeleScoresHelper.relicInZoneThree, "Relic Scored in Zone 3");
                                relic.setText(item.getTitle());
                                refreshEventLog();
                                break;
                            }
                            case R.id.missed: {
                                relicEvent = new EventStruct(AppSync.teamNumber, "teleop", "missed", "relic", "", 0, "Missed/Dropped Relic");
                                relic.setText(item.getTitle());
                                refreshEventLog();
                                break;
                            }
                            default: {
                                break;
                            }
                        }

                        refreshEventLog();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        relicUpright.setText("Toppled");
        relicUpright.setTextOn("Upright");
        relicUpright.setTextOff("Toppled");

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppSync.eventsList.size() > 0) {
                    reenableButton(AppSync.eventsList.get(AppSync.eventsList.size()-1));
                    AppSync.eventsList.remove(AppSync.eventsList.size()-1);
                    refreshEventLog();
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!deadRobot.isChecked()) { // If not checked, robot is dead.
                    AppSync.addEvent(0, "teleop", "missed", "robot", "", 0, "Dead Robot");
                }

                if (relicEvent != null) {
                    if (relicUpright.isChecked()) { // If checked, relic is upright.
                        AppSync.addEvent(0, "teleop", "scored", "relic", "upright", TeleScoresHelper.relicUpright, "Relic Upright");
                    }
                    AppSync.eventsList.add(relicEvent);
                }
                AppSync.writeEvents();

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(new File(AppSync.getMatchDir() + File.separator + AppSync.currentMatchPath +".json")));
                sendBroadcast(intent);
                finish();
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

    public void setScore(int score1) {
        score = score1;
        scoreText.setText(""+score+" pts");
    }

    public void refreshEventLog() {
        String string = "";
        int tally = 0;

        if (relicEvent != null) {
            string += "" + relicEvent.type + " " + relicEvent.subtype + " " + relicEvent.points + "pts " + relicEvent.description + "\n";

            tally += relicEvent.points;
        }

        for (EventStruct event : AppSync.eventsList) {
            string += "" + event.type + " " + event.subtype + " " + event.points + "pts " + event.description + "\n";
            tally+=event.points;
        }

        setScore(tally);

        if (string.length() < 1) {
            eventLog.setText("TeleOp Log");
        } else {
            eventLog.setText(string);
        }
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void reenableButton(EventStruct event) {
        // TODO; Make me do helpful things.
        AppSync.puts(""+event);
        if (event.type == "scored") {
            switch (event.subtype) {
                case "balance":
                    robotBalanced.setEnabled(true);
                    robotFailedToBalance.setEnabled(true);
                    break;
                case "glyph":
                    if (event.location == "cipher") {glyphCompletedCipher.setEnabled(true);}
                    break;
                default: {break;}
            }


        } else if (event.type == "missed") {
            switch (event.subtype) {
                case "balance":
                    robotBalanced.setEnabled(true);
                    robotFailedToBalance.setEnabled(true);
                default: {break;}
            }
        }
    }
}
