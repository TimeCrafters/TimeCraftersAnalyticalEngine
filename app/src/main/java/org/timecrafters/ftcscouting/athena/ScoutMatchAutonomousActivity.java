package org.timecrafters.ftcscouting.athena;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.apollo.AutoScoresHelper;
import org.timecrafters.ftcscouting.hermes.AppSync;
import org.timecrafters.ftcscouting.hermes.EventStruct;

import java.io.File;
import java.util.HashMap;

public class ScoutMatchAutonomousActivity extends AppCompatActivity {

    EventStruct parkingEvent;
    EventStruct capballEvent;

    Button teamSelection;

    Button jewelScored;
    Button jewelMissed;

    Button glyphScored;
    Button glyphMissed;
    Button glyphCryptokey;

    Button parkingSafeZone;
    Button parkingMissed;

    ToggleButton deadRobot;

    Button undo;
    Button teleOp;

    TextView eventLog;
    TextView scoreText;
    ScrollView scrollView;


    public int score = 0;
    public int teamNumber;
    public String teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_match_autonomous);

       teamSelection = (Button) findViewById(R.id.team_selection);

        glyphScored = (Button) findViewById(R.id.glyph_scored);
        glyphMissed = (Button) findViewById(R.id.glyph_missed);
        glyphCryptokey = (Button) findViewById(R.id.glyph_crytokey);

        parkingSafeZone = (Button) findViewById(R.id.parking_safezone);
        parkingMissed   = (Button) findViewById(R.id.parking_missed);

        jewelScored = (Button) findViewById(R.id.jewel_scored);
        jewelMissed = (Button) findViewById(R.id.jewel_missed);

        deadRobot = (ToggleButton) findViewById(R.id.robot_dead);

        undo   = (Button) findViewById(R.id.undo);
        teleOp = (Button) findViewById(R.id.teleop);

        eventLog = (TextView) findViewById(R.id.eventLog);
        scoreText = (TextView) findViewById(R.id.score);

        glyphScored.setEnabled(false);
        glyphMissed.setEnabled(false);
        glyphCryptokey.setEnabled(false);

        parkingSafeZone.setEnabled(false);
        parkingMissed.setEnabled(false);

        jewelScored.setEnabled(false);
        jewelMissed.setEnabled(false);

        deadRobot.setEnabled(false);
        deadRobot.setText("Alive");
        deadRobot.setTextOn("Alive");
        deadRobot.setTextOff("DEAD");

        undo.setEnabled(false);
        teleOp.setEnabled(false);

        scrollView = (ScrollView) findViewById(R.id.scrollview);

        // Select Team
        teamSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ScoutMatchAutonomousActivity.this, teamSelection);
                popupMenu.getMenu().setQwertyMode(false);

                for(HashMap.Entry<Integer, String> entry : AppSync.teamsList.entrySet()) {
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

                        AppSync.currentMatchPath = AppSync.getMatchDir()+ File.separator +"-"+AppSync.matchID+ System.currentTimeMillis()/1000;
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        // Glyph
        glyphScored.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "score", "glyph", "glyph", AutoScoresHelper.glyphScored, "Scored Glyph");
                refreshEventLog();
            }
        });

        glyphMissed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "miss", "glyph", "", 0, "Missed Glyph");
                refreshEventLog();
            }
        });

        glyphCryptokey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "score", "glyph", "cryptokey", AutoScoresHelper.glyphScoreCryptokey, "Score Glyph Cryptokey");
                glyphCryptokey.setEnabled(false);
                refreshEventLog();
            }
        });

        // Parking
        parkingSafeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "scored", "parking", "", AutoScoresHelper.parkingSafeZone, "Parked in Safe Zone");
                parkingSafeZone.setEnabled(false);
                parkingMissed.setEnabled(false);
                refreshEventLog();
            }
        });

        parkingMissed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "missed", "parking", "", 0, "Missed parking in Safe Zone");
                parkingSafeZone.setEnabled(false);
                parkingMissed.setEnabled(false);
                refreshEventLog();
            }
        });

        // Jewel
        jewelScored.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "scored", "jewel", "", 0, "Scored jewel");
                jewelScored.setEnabled(false);
                jewelMissed.setEnabled(false);
                refreshEventLog();
            }
        });

        jewelMissed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "missed", "jewel", "", 0, "Missed jewel");
                jewelScored.setEnabled(false);
                jewelMissed.setEnabled(false);
                refreshEventLog();
            }
        });

        // Undo button
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

        teleOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SAVE DATA

                if (!deadRobot.isChecked()) { // If not checked, robot is dead.
                    AppSync.addEvent(0, "autonomous", "miss", "robot", "", 0, "Dead Robot");
                }

                if (parkingEvent != null) {
                    AppSync.eventsList.add(parkingEvent);
                }

                AppSync.writeEvents();
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(new File(AppSync.getMatchDir() + File.separator + AppSync.currentMatchPath +".json")));
                sendBroadcast(intent);

                finish();
                startActivity(new Intent(getBaseContext(), ScoutMatchTeleOpActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (AppSync.eventsList.size() > 0) {
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
        } else {
            finish();
        }
    }

    public void lockTeamIn() {
        teamSelection.setEnabled(false);
    }

    public void refreshEventLog() {
        String string = "";
        int tally = 0;

        for (EventStruct event : AppSync.eventsList) {
            string += "" + event.type + " " + event.subtype + " " + event.points + "pts " + event.description + "\n";
            tally+=event.points;
        }

        setScore(tally);

        if (string.length() < 1) {
            eventLog.setText("Autonomous Log");
        } else {
            eventLog.setText(string);
        }
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void setScore(int score1) {
        score = score1;
        scoreText.setText(""+score+" pts");
    }

    public void enableButtons() {
        glyphScored.setEnabled(true);
        glyphMissed.setEnabled(true);
        glyphCryptokey.setEnabled(true);

        parkingSafeZone.setEnabled(true);
        parkingMissed.setEnabled(true);

        jewelScored.setEnabled(true);
        jewelMissed.setEnabled(true);

        deadRobot.setEnabled(true);

        undo.setEnabled(true);
        teleOp.setEnabled(true);
    }

    public void reenableButton(EventStruct event) {
        // TODO; Make me do helpful things.
    }
}
