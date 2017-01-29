package org.timecrafters.ftcscouting.athena;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.timecrafters.ftcscouting.MainActivity;
import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.apollo.AutoScoresHelper;
import org.timecrafters.ftcscouting.hermes.AppSync;
import org.timecrafters.ftcscouting.hermes.EventStruct;

import java.util.HashMap;

public class ScoutMatchAutonomousActivity extends AppCompatActivity {

    Button teamSelection;


    Button onRamp;
    Button touchingRamp;

    Button onPlatform;
    Button touchingPlatform;

    Button triggerLightBox;
    Button missedLightBox;

    Button particleInVortex;
    Button particleInCorner;

    Button particleMissedVortex;
    Button particleMissedCorner;

    Button bumpedBall;

    Button statistics;
    Button teleOp;

    TextView eventLog;
    TextView scoreText;


    public int score = 0;
    public int numberOfLightBoxesHit = 0;
    public int teamNumber;
    public String teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_match_autonomous);

       teamSelection = (Button) findViewById(R.id.team_selection);


        onRamp = (Button) findViewById(R.id.on_ramp);
        touchingRamp = (Button) findViewById(R.id.touching_ramp);

        onPlatform = (Button) findViewById(R.id.on_platform);
        touchingPlatform = (Button) findViewById(R.id.touching_platform);

        triggerLightBox = (Button) findViewById(R.id.claim_beacon);
        missedLightBox = (Button) findViewById(R.id.missed_lightbox);

        particleInVortex = (Button) findViewById(R.id.in_vortex);
        particleInCorner = (Button) findViewById(R.id.in_corner);

        particleMissedVortex = (Button) findViewById(R.id.missed_vortex);
        particleMissedCorner = (Button) findViewById(R.id.missed_corner);

        bumpedBall = (Button) findViewById(R.id.bump_ball);

        statistics = (Button) findViewById(R.id.statistics);
        teleOp = (Button) findViewById(R.id.teleop);

        eventLog = (TextView) findViewById(R.id.eventLog);
        scoreText = (TextView) findViewById(R.id.score);

        onRamp.setEnabled(false);
        onPlatform.setEnabled(false);
        touchingRamp.setEnabled(false);
        touchingPlatform.setEnabled(false);

        triggerLightBox.setEnabled(false);
        missedLightBox.setEnabled(false);

        particleInVortex.setEnabled(false);
        particleInCorner.setEnabled(false);
        particleMissedVortex.setEnabled(false);
        particleMissedCorner.setEnabled(false);

        bumpedBall.setEnabled(false);

        statistics.setEnabled(false);
        teleOp.setEnabled(false);

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
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        // Light Box
        triggerLightBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                numberOfLightBoxesHit++;
                if (numberOfLightBoxesHit < 3) {
                    setScore(AutoScoresHelper.triggerLightBox);
                    if (numberOfLightBoxesHit == 2) { triggerLightBox.setEnabled(false); }
                } else {
                    triggerLightBox.setEnabled(false);
                }
                refreshEventLog();
            }
        });

        missedLightBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "miss", "beacon", "", 0, "Missed Beacon");
                refreshEventLog();
            }
        });

        // Parking Locations
        onRamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "score", "parking", "on ramp", AutoScoresHelper.onRamp, "Parked Completely on Ramp");
                refreshEventLog();

                setScore(AutoScoresHelper.onRamp);
                disableParkedGroup();
            }
        });

        onPlatform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "score", "parking", "on platform", AutoScoresHelper.onPlatform, "Parked Completely on Platform");
                refreshEventLog();

                setScore(AutoScoresHelper.onPlatform);
                disableParkedGroup();
            }
        });

        touchingRamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "score", "parking", "touching ramp", AutoScoresHelper.touchingRamp, "Parked on Ramp");
                refreshEventLog();

                setScore(AutoScoresHelper.touchingRamp);
                disableParkedGroup();
            }
        });

        touchingPlatform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "score", "parking", "touching platform", AutoScoresHelper.touchingPlatform, "Parked on Platform");
                refreshEventLog();

                setScore(AutoScoresHelper.touchingPlatform);
                disableParkedGroup();
            }
        });

        bumpedBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTeamIn();
                AppSync.addEvent(0, "autonomous", "score", "capball", "floor", AutoScoresHelper.capBallBumped, "Capball on Floor");
                refreshEventLog();

                setScore(AutoScoresHelper.capBallBumped);
                bumpedBall.setEnabled(false);
            }
        });

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

        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DOSTUFF
            }
        });

        teleOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SAVE DATA
                AppSync.writeEvents();
                startActivity(new Intent(getBaseContext(), ScoutMatchTeleOpActivity.class));
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

        eventLog.setText(string);
    }

    public void setScore(int score1) {
        score+=score1;
        scoreText.setText(""+score+" pts");
    }

    public void disableParkedGroup() {
        onRamp.setEnabled(false);
        touchingRamp.setEnabled(false);
        onPlatform.setEnabled(false);
        touchingPlatform.setEnabled(false);
    }

    public void enableButtons() {
        onRamp.setEnabled(true);
        onPlatform.setEnabled(true);
        touchingRamp.setEnabled(true);
        touchingPlatform.setEnabled(true);

        triggerLightBox.setEnabled(true);
        missedLightBox.setEnabled(true);

        particleInVortex.setEnabled(true);
        particleInCorner.setEnabled(true);
        particleMissedVortex.setEnabled(true);
        particleMissedCorner.setEnabled(true);

        bumpedBall.setEnabled(true);

        statistics.setEnabled(true);
        teleOp.setEnabled(true);
    }
}
