package org.timecrafters.analyticalengine.athena.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.timecrafters.analyticalengine.R;
import org.timecrafters.analyticalengine.athena.TeamStatisticsActivity;
import org.timecrafters.analyticalengine.hermes.AppSync;
import org.timecrafters.analyticalengine.hermes.MatchStruct;

import java.text.DecimalFormat;

public class AutonomousFragment extends Fragment {
    TextView team;
    Button menu;
    TextView dataset;

    TextView beaconsClaimed;
    TextView beaconsMissed;
    TextView beaconsSuccessPercentage;

    TextView particlesScoredInVortex;
    TextView particlesScoredInCorner;
    TextView particlesMissedVortex;
    TextView particlesMissedCorner;
    TextView particleVortexSuccessPercentage;
    TextView particleCornerSuccessPercentage;

    TextView parkedCompletelyOnPlatform;
    TextView parkedCompletelyOnRamp;
    TextView parkedOnPlatform;
    TextView parkedOnRamp;
    TextView parkedMissed;
    TextView parkedSuccessPercentage;

    TextView capballOnFloor;
    TextView capballMissed;
    TextView capballSuccessPercentage;

    TextView robotDead;

    TeamStatisticsActivity localActivity;

    boolean monoMatch = false;

    public AutonomousFragment() {
        // Required empty public constructor
    }

    public static AutonomousFragment newInstance() {
        AutonomousFragment fragment = new AutonomousFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_autonomous, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        localActivity = TeamStatisticsActivity.contextForFragment;

        team = (TextView) getView().findViewById(R.id.team);
        team.setText(""+ AppSync.teamNumber+ " | "+ AppSync.teamName);
        menu = (Button) getView().findViewById(R.id.match);
        dataset = (TextView) getView().findViewById(R.id.dataset);

        beaconsClaimed = (TextView) getView().findViewById(R.id.beacons_claimed);
        beaconsMissed = (TextView) getView().findViewById(R.id.beacons_missed);
        beaconsSuccessPercentage = (TextView) getView().findViewById(R.id.beacons_success_percentage);

        particlesScoredInVortex = (TextView) getView().findViewById(R.id.particles_scored_in_vortex);
        particlesScoredInCorner = (TextView) getView().findViewById(R.id.particles_scored_in_corner);
        particlesMissedVortex = (TextView) getView().findViewById(R.id.particles_missed_vortex);
        particlesMissedCorner = (TextView) getView().findViewById(R.id.particles_missed_corner);
        particleVortexSuccessPercentage = (TextView) getView().findViewById(R.id.particles_vortex_success_percentage);
        particleCornerSuccessPercentage = (TextView) getView().findViewById(R.id.particles_corner_success_percentage);

        parkedCompletelyOnPlatform = (TextView) getView().findViewById(R.id.parking_completely_on_platform);
        parkedCompletelyOnRamp = (TextView) getView().findViewById(R.id.parking_completely_on_ramp);
        parkedOnPlatform = (TextView) getView().findViewById(R.id.parking_on_platform);
        parkedOnRamp = (TextView) getView().findViewById(R.id.parking_on_ramp);
        parkedMissed = (TextView) getView().findViewById(R.id.parking_missed);
        parkedSuccessPercentage = (TextView) getView().findViewById(R.id.parking_success_percentage);

        capballOnFloor = (TextView) getView().findViewById(R.id.capball_on_floor);
        capballMissed = (TextView) getView().findViewById(R.id.capball_missed);
        capballSuccessPercentage = (TextView) getView().findViewById(R.id.capball_success_percentage);

        robotDead = (TextView) getView().findViewById(R.id.robot_dead);

        if (AppSync.teamHasMatchData() && localActivity.autonomousData != null && localActivity.autonomousData.size() > 0) {
            dataset.setText(""+(localActivity.autonomousData.size()-1)+" in dataset");

            populateMenu();
            populateAutonomousData(localActivity.autonomousData.get(localActivity.autonomousData.size()-1));
        } else {
            // No match data!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
    }

    public void populateMenu() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu = new PopupMenu(localActivity, menu);
                popupMenu.getMenu().setQwertyMode(false);

                for (int i = 0; i < localActivity.autonomousData.size(); i++) {
                    if ((i+1) == localActivity.autonomousData.size()) {
                        popupMenu.getMenu().add("ALL");
                    } else {
                        popupMenu.getMenu().add("" + (i + 1));
                    }
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("ALL")) {
                            monoMatch = false;
                            populateAutonomousData(localActivity.autonomousData.get(localActivity.autonomousData.size()-1));
                        } else {
                            int index = Integer.parseInt(item.getTitle().toString()) - 1;
                            monoMatch = true;
                            populateAutonomousData(localActivity.autonomousData.get(index));
                        }
                        menu.setText(item.getTitle());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    public void populateAutonomousData(MatchStruct match) {
        AppSync.puts("STATS", "Match: "+match.scoredInVortex);
        DecimalFormat decimalFormat = new DecimalFormat("###.##");

        int totalBeacons, totalParticlesVortex, totalParticlesCorner, totalParking, totalCapball;
        double beaconsPercentage;
        double particlesPercentage;
        double particlesVortexPercentage;
        double particlesCornerPercentage;
        double parkingPercentage;
        double capballPercentage;

        totalBeacons = match.beaconsClaimed+match.beaconsMissed;
        beaconsPercentage = ((double) match.beaconsClaimed) / (double) totalBeacons * 100;

        totalParticlesVortex = match.scoredInVortex+match.missedVortex;
        particlesVortexPercentage = ((double) match.scoredInVortex) / (double) totalParticlesVortex * 100;

        totalParticlesCorner = match.scoredInCorner+match.missedCorner;
        particlesCornerPercentage = (double) match.scoredInCorner / (double) totalParticlesCorner * 100;

        particlesPercentage = ((double) match.missedVortex+match.missedCorner) / (double) totalParticlesVortex+totalParticlesCorner * 100;

        totalParking = match.completelyOnPlatform+match.completelyOnRamp + match.onPlatform+match.onRamp + match.missedParking;
        parkingPercentage = (double) (match.completelyOnPlatform+match.completelyOnRamp + match.onPlatform+match.onRamp) / (double) totalParking * 100;

        totalCapball = match.capballOnFloor+match.capballMissed;
        capballPercentage = ((double) match.capballOnFloor) / totalCapball * 100;

        beaconsClaimed.setText(""+match.beaconsClaimed);
        beaconsMissed.setText(""+match.beaconsMissed);
        beaconsSuccessPercentage.setText(""+decimalFormat.format(beaconsPercentage)+"%");

        particlesScoredInVortex.setText(""+match.scoredInVortex);
        particlesScoredInCorner.setText(""+match.scoredInCorner);
        particlesMissedVortex.setText(""+match.missedVortex);
        particlesMissedCorner.setText(""+match.missedCorner);
        particleVortexSuccessPercentage.setText(""+decimalFormat.format(particlesVortexPercentage)+"%");
        particleCornerSuccessPercentage.setText(""+decimalFormat.format(particlesCornerPercentage)+"%");

        parkedCompletelyOnPlatform.setText(""+match.completelyOnPlatform);
        parkedCompletelyOnRamp.setText(""+match.completelyOnRamp);
        parkedOnPlatform.setText(""+match.onPlatform);
        parkedOnRamp.setText(""+match.onRamp);
        parkedMissed.setText(""+match.missedParking);
        parkedSuccessPercentage.setText(""+decimalFormat.format(parkingPercentage)+"%");

        capballOnFloor.setText(""+match.capballOnFloor);
        capballMissed.setText(""+match.capballMissed);
        capballSuccessPercentage.setText(""+decimalFormat.format(capballPercentage)+"%");

        if (monoMatch) {
            if (match.is_deadRobot) {
                robotDead.setText("Yes");
                robotDead.setTextColor(Color.RED);
            } else {
                robotDead.setText("No");
                robotDead.setTextColor(Color.BLACK);
            }
        } else {
            robotDead.setText(""+match.deadRobot);
            robotDead.setTextColor(Color.BLACK);
        }
    }
}
