package org.timecrafters.ftcscouting.athena;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.apollo.AutoScoresHelper;
import org.timecrafters.ftcscouting.apollo.TeleScoresHelper;
import org.timecrafters.ftcscouting.hermes.AppSync;
import org.timecrafters.ftcscouting.hermes.EventStruct;

public class ScoutMatchTeleOpActivity extends AppCompatActivity {
    EventStruct capballEvent;

    public Button claimBeacon;
    public Button loseBeacon;

    public Button capballState;

    public ToggleButton deadRobot;

    public Button particleInVortex;
    public Button particleInCorner;

    public Button particleMissedVortex;
    public Button particleMissedCorner;

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

        claimBeacon = (Button) findViewById(R.id.claim_beacon);
        loseBeacon = (Button) findViewById(R.id.lost_beacon);

        capballState = (Button) findViewById(R.id.capball_state);

        deadRobot = (ToggleButton) findViewById(R.id.dead_robot);

        particleInVortex = (Button) findViewById(R.id.particle_in_vortex);
        particleInCorner = (Button) findViewById(R.id.particle_in_corner);

        particleMissedVortex = (Button) findViewById(R.id.particle_missed_vortex);
        particleMissedCorner = (Button) findViewById(R.id.particle_missed_corner);

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

        claimBeacon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // _-Magic Smoke Required-_ \\
                AppSync.addEvent(0, "teleop", "score", "beacon", "", TeleScoresHelper.claimBeacon, "Claim Beacon");
                refreshEventLog();
            }
        });

        loseBeacon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // _-Magic Smoke Required-_ \\
                AppSync.addEvent(0, "teleop", "lose", "beacon", "", 0, "Lose Beacon");
                refreshEventLog();
            }
        });

        capballState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ScoutMatchTeleOpActivity.this, capballState);
                popupMenu.getMenuInflater().inflate(R.menu.menu_teleop_capball_state, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.none: {
                                capballEvent = null;
                                capballState.setText(item.getTitle());
                                refreshEventLog();
                                break;
                            }
                            case R.id.off_floor: {
                                capballEvent = new EventStruct(AppSync.teamNumber, "teleop", "score", "capball", "off_floor", TeleScoresHelper.capBallOffGround, "Capball off Floor");
                                capballState.setText(item.getTitle());
                                refreshEventLog();
                                break;
                            }
                            case R.id.above_crossbar: {
                                capballEvent = new EventStruct(AppSync.teamNumber, "teleop", "score", "capball", "above_crossbar", TeleScoresHelper.capBallAboveCrossBar, "Capball Above Crossbar");
                                capballState.setText(item.getTitle());
                                refreshEventLog();
                                break;
                            }
                            case R.id.capped: {
                                capballEvent = new EventStruct(AppSync.teamNumber, "teleop", "score", "capball", "capped", TeleScoresHelper.capBallCapped, "Capball Capped");
                                capballState.setText(item.getTitle());
                                refreshEventLog();
                                break;
                            }
                            case R.id.missed: {
                                capballEvent = new EventStruct(AppSync.teamNumber, "teleop", "miss", "capball", "", 0, "Missed/Dropped Capball");
                                capballState.setText(item.getTitle());
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

        particleInVortex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "score", "particle", "vortex", TeleScoresHelper.particleInVortex, "Scored Particle in Vortex");
                refreshEventLog();
            }
        });

        particleInCorner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "score", "particle", "corner", TeleScoresHelper.particleInCorner, "Scored Particle in Corner");
                refreshEventLog();
            }
        });

        particleMissedVortex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "miss", "particle", "vortex", 0, "Missed Scoring Particle in Vortex");
                refreshEventLog();
            }
        });

        particleMissedCorner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppSync.addEvent(0, "teleop", "miss", "particle", "corner", 0, "Missed Scoring Particle in Corner");
                refreshEventLog();
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppSync.eventsList.size() > 0) {
                    AppSync.eventsList.remove(AppSync.eventsList.size()-1);
                    refreshEventLog();
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!deadRobot.isChecked()) { // If not checked, robot is dead.
                    AppSync.addEvent(0, "teleop", "miss", "robot", "", 0, "Dead Robot");
                }

                if (capballEvent != null) {
                    AppSync.eventsList.add(capballEvent);
                }
                AppSync.writeEvents();
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

        if (capballEvent != null) {
            string += "" + capballEvent.type + " " + capballEvent.subtype + " " + capballEvent.points + "pts " + capballEvent.description + "\n";

            tally += capballEvent.points;
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
}
