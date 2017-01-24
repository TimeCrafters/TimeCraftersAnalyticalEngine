package org.timecrafters.ftcscouting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoutMatchTeleOpActivity extends AppCompatActivity {

    public Button claimBeacon;
    public Button looseBeacon;

    public Button capBallOffFloor;
    public Button capBallAboveCrossBar;

    public Button capBallCapped;

    public Button particleInVortex;
    public Button particleInCorner;

    public Button particleMissedVortex;
    public Button particleMissedCorner;

    public Button statistics;
    public Button done;

    public TextView currentTeam;
    public TextView scoreText;

    public int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_match_tele_op);

        claimBeacon = (Button) findViewById(R.id.claim_beacon);
        looseBeacon = (Button) findViewById(R.id.lost_beacon);

        capBallOffFloor = (Button) findViewById(R.id.cap_ball_off_floor);
        capBallAboveCrossBar = (Button) findViewById(R.id.cap_ball_above_crossbar);
        capBallCapped = (Button) findViewById(R.id.cap_ball_capped);

        particleInVortex = (Button) findViewById(R.id.particle_in_vortex);
        particleInCorner = (Button) findViewById(R.id.particle_in_corner);

        particleMissedVortex = (Button) findViewById(R.id.particle_missed_vortex);
        particleMissedCorner = (Button) findViewById(R.id.particle_missed_corner);

        statistics = (Button) findViewById(R.id.statistics);
        done = (Button) findViewById(R.id.done);

        currentTeam = (TextView) findViewById(R.id.team);
        scoreText = (TextView) findViewById(R.id.score);

        currentTeam.setText(""+AppSync.teamNumber+" | "+AppSync.teamName);

        claimBeacon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // _-Magic Smoke Required-_ \\
                setScore(TeleScoresHelper.claimBeacon);
            }
        });

        looseBeacon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // _-Magic Smoke Required-_ \\
                setScore(TeleScoresHelper.claimBeacon*-1);
            }
        });

        capBallOffFloor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setScore(TeleScoresHelper.capBallOffGround);
                lockCapball();
            }
        });

        capBallAboveCrossBar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setScore(TeleScoresHelper.capBallAboveCrossBar);
                lockCapball();
            }
        });

        capBallCapped.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setScore(TeleScoresHelper.capBallCapped);
                lockCapball();
            }
        });

        particleInVortex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setScore(TeleScoresHelper.particleInVortex);
            }
        });

        particleInCorner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setScore(TeleScoresHelper.particleInCorner);
            }
        });

        particleMissedVortex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                setScore(TeleScoresHelper.capBallCapped);
            }
        });

        particleMissedCorner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                setScore(TeleScoresHelper.capBallCapped);
            }
        });
    }

    public void lockCapball() {
        capBallOffFloor.setEnabled(false);
        capBallAboveCrossBar.setEnabled(false);
        capBallCapped.setEnabled(false);
    }

    public void setScore(int score1) {
        score+=score1;
        scoreText.setText(""+score+" pts");
    }
}
