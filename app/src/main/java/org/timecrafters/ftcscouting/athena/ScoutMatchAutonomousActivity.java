package org.timecrafters.ftcscouting.athena;

import android.content.Intent;
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

    Button teamSelection;

    Button claimBeacon;
    Button missedBeacon;

    Button particleInVortex;
    Button particleInCorner;

    Button particleMissedVortex;
    Button particleMissedCorner;

    Button parkingState;

    ToggleButton bumpedBall;
    ToggleButton deadRobot;

    Button undo;
    Button teleOp;

    TextView eventLog;
    TextView scoreText;
    ScrollView scrollView;


    public int score = 0;
    public int numberOfBeaconsHit = 0;
    public int teamNumber;
    public String teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_match_autonomous);

       teamSelection = (Button) findViewById(R.id.team_selection);

        claimBeacon = (Button) findViewById(R.id.claim_beacon);
        missedBeacon = (Button) findViewById(R.id.missed_lightbox);

        particleInVortex = (Button) findViewById(R.id.in_vortex);
        particleInCorner = (Button) findViewById(R.id.in_corner);

        particleMissedVortex = (Button) findViewById(R.id.missed_vortex);
        particleMissedCorner = (Button) findViewById(R.id.missed_corner);

        bumpedBall = (ToggleButton) findViewById(R.id.bump_ball);
        deadRobot = (ToggleButton) findViewById(R.id.dead_robot);

        parkingState = (Button) findViewById(R.id.parking_state);
        undo   = (Button) findViewById(R.id.undo);
        teleOp = (Button) findViewById(R.id.teleop);

        eventLog = (TextView) findViewById(R.id.eventLog);
        scoreText = (TextView) findViewById(R.id.score);

        claimBeacon.setEnabled(false);
        missedBeacon.setEnabled(false);

        particleInVortex.setEnabled(false);
        particleInCorner.setEnabled(false);
        particleMissedVortex.setEnabled(false);
        particleMissedCorner.setEnabled(false);

        bumpedBall.setEnabled(false);
        bumpedBall.setText("Not on Floor");
        bumpedBall.setTextOn("On Floor");
        bumpedBall.setTextOff("Not on Floor");

        deadRobot.setEnabled(false);
        deadRobot.setText("Alive");
        deadRobot.setTextOn("Alive");
        deadRobot.setTextOff("DEAD");

        parkingState.setEnabled(false);
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

                        AppSync.currentMatchPath = AppSync.getMatchDir()+ File.separator +"-"+AppSync.matchID+ System.currentTimeMillis()/1000;
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        // Beacons
        claimBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                // Enable if necessarily
//                numberOfBeaconsHit++;
//                if (numberOfBeaconsHit < 3) {
//                    setScore(AutoScoresHelper.claimBeacon);
//                    if (numberOfBeaconsHit == 2) { claimBeacon.setEnabled(false); }
//                } else {
//                    claimBeacon.setEnabled(false);
//                }
                AppSync.addEvent(0, "autonomous", "score", "beacon", "", AutoScoresHelper.claimBeacon, "Claimed Beacon");

                refreshEventLog();
            }
        });

        missedBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "miss", "beacon", "", 0, "Missed Beacon");
                refreshEventLog();
            }
        });

        // Parking Locations
        parkingState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ScoutMatchAutonomousActivity.this, parkingState);
                popupMenu.getMenuInflater().inflate(R.menu.menu_autonomous_parking, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        lockTeamIn();

                        switch (item.getItemId()) {
                            case R.id.not_applicable: {
                                parkingEvent = null;
                                parkingState.setText(item.getTitle());
                                break;
                            }
                            case R.id.completely_on_platform: {
                                parkingEvent = new EventStruct(AppSync.teamNumber, "autonomous", "score", "parking", "on_platform", AutoScoresHelper.completelyOnPlatform, "Parked Completely on Platform");
                                parkingState.setText(item.getTitle());
                                break;
                            }
                            case R.id.completely_on_ramp: {
                               parkingEvent = new EventStruct(AppSync.teamNumber, "autonomous", "score", "parking", "on_ramp", AutoScoresHelper.completelyOnRamp, "Parked on Completely on Ramp");
                                parkingState.setText(item.getTitle());
                                break;
                            }
                            case R.id.on_platform: {
                                parkingEvent = new EventStruct(AppSync.teamNumber, "autonomous", "score", "parking", "platform", AutoScoresHelper.onPlatform, "Parked on Platform");
                                parkingState.setText(item.getTitle());
                                break;
                            }
                            case R.id.on_ramp: {
                                parkingEvent = new EventStruct(AppSync.teamNumber, "autonomous", "score", "parking", "ramp", AutoScoresHelper.onRamp, "Parked on Ramp");
                                parkingState.setText(item.getTitle());
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
        // End

        // Particles
        particleInVortex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
//                writeEvent("team# match#: eventtype-event name | score#");
                AppSync.addEvent(0, "autonomous", "score", "particle", "vortex", AutoScoresHelper.particleInVortex, "Scored Particle in Vortex");
                refreshEventLog();

                setScore(AutoScoresHelper.particleInVortex);
            }
        });

        particleInCorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "score", "particle", "corner", AutoScoresHelper.particleInCorner, "Scored Particle in Corner");
                refreshEventLog();

                setScore(AutoScoresHelper.particleInCorner);
            }
        });

        particleMissedVortex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "miss", "particle", "vortex", 0, "Missed Scoring Particle in Vortex");
                refreshEventLog();

//                setScore(AutoScoresHelper.particleInVortex);
            }
        });

        particleMissedCorner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "miss", "particle", "corner", 0, "Missed Scoring Particle in Corner");

//                setScore(AutoScoresHelper.particleInCorner);
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

        teleOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SAVE DATA

                if (bumpedBall.isChecked()) {
                    AppSync.addEvent(0, "autonomous", "score", "capball", "floor", AutoScoresHelper.capBallBumped, "Capball on Floor");
                }

                if (!deadRobot.isChecked()) { // If not checked, robot is dead.
                    AppSync.addEvent(0, "autonomous", "miss", "robot", "", 0, "Dead Robot");
                }

                if (parkingEvent != null) {
                    AppSync.eventsList.add(parkingEvent);
                }

                AppSync.writeEvents();
                finish();
                startActivity(new Intent(getBaseContext(), ScoutMatchTeleOpActivity.class));
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

    public void lockTeamIn() {
        teamSelection.setEnabled(false);
    }

    public void refreshEventLog() {
        String string = "";
        for (EventStruct event : AppSync.eventsList) {
            string += "" + event.type + " " + event.subtype + " " + event.points + "pts " + event.description + "\n";
        }

        if (string.length() < 1) {
            eventLog.setText("Autonomous Log");
        } else {
            eventLog.setText(string);
        }
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void setScore(int score1) {
        score+=score1;
        scoreText.setText(""+score+" pts");
    }

    public void enableButtons() {
        claimBeacon.setEnabled(true);
        missedBeacon.setEnabled(true);

        particleInVortex.setEnabled(true);
        particleInCorner.setEnabled(true);
        particleMissedVortex.setEnabled(true);
        particleMissedCorner.setEnabled(true);

        bumpedBall.setEnabled(true);
        deadRobot.setEnabled(true);

        parkingState.setEnabled(true);
        undo.setEnabled(true);
        teleOp.setEnabled(true);
    }
}
