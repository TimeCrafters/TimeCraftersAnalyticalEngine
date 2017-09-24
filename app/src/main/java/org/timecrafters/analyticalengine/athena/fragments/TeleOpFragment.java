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

public class TeleOpFragment extends Fragment {
    TextView team;
    Button menu;
    TextView dataset;

    TextView beaconsClaimed;
    TextView beaconsStolen;
    TextView beaconsSuccessPercentage;

    TextView particlesScoredInVortex;
    TextView particlesScoredInCorner;
    TextView particlesMissedVortex;
    TextView particlesMissedCorner;
    TextView particleVortexSuccessPercentage;
    TextView particleCornerSuccessPercentage;

    TextView capballOffFloor;
    TextView capballAboveCrossbar;
    TextView capballCapped;
    TextView capballMissed;
    TextView capballSuccessPercentage;

    TextView robotDead;
    boolean monoMatch = false;

    TeamStatisticsActivity localActivity;

    public TeleOpFragment() {
        // Required empty public constructor
    }

    public static TeleOpFragment newInstance() {
        TeleOpFragment fragment = new TeleOpFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tele_op, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        localActivity = TeamStatisticsActivity.contextForFragment;

        team = (TextView) getView().findViewById(R.id.team);
        team.setText(""+ AppSync.teamNumber+ " | "+ AppSync.teamName);
        menu = (Button) getView().findViewById(R.id.match);
        dataset = (TextView) getView().findViewById(R.id.dataset);

        beaconsClaimed = (TextView) getView().findViewById(R.id.beacons_claimed);
        beaconsStolen = (TextView) getView().findViewById(R.id.beacons_lost);
        beaconsSuccessPercentage = (TextView) getView().findViewById(R.id.beacons_success_percentage);

        particlesScoredInVortex = (TextView) getView().findViewById(R.id.particles_scored_in_vortex);
        particlesScoredInCorner = (TextView) getView().findViewById(R.id.particles_scored_in_corner);
        particlesMissedVortex = (TextView) getView().findViewById(R.id.particles_missed_vortex);
        particlesMissedCorner = (TextView) getView().findViewById(R.id.particles_missed_corner);
        particleVortexSuccessPercentage = (TextView) getView().findViewById(R.id.particles_vortex_success_percentage);
        particleCornerSuccessPercentage = (TextView) getView().findViewById(R.id.particles_corner_success_percentage);

        capballOffFloor = (TextView) getView().findViewById(R.id.capball_off_floor);
        capballAboveCrossbar = (TextView) getView().findViewById(R.id.capball_above_crossbar);
        capballCapped = (TextView) getView().findViewById(R.id.capball_capped);
        capballMissed = (TextView) getView().findViewById(R.id.capball_missed);
        capballSuccessPercentage = (TextView) getView().findViewById(R.id.capball_success_percentage);

        robotDead = (TextView) getView().findViewById(R.id.robot_dead);

        if (AppSync.teamHasMatchData() && TeamStatisticsActivity.contextForFragment.teleOpData != null && TeamStatisticsActivity.contextForFragment.teleOpData.size() > 0) {
            dataset.setText(""+(TeamStatisticsActivity.contextForFragment.teleOpData.size()-1)+" in dataset");

            populateMenu();
            populateTeleOPData(TeamStatisticsActivity.contextForFragment.teleOpData.get(TeamStatisticsActivity.contextForFragment.teleOpData.size()-1));
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

                for (int i = 0; i < localActivity.teleOpData.size(); i++) {
                    if ((i+1) == localActivity.teleOpData.size()) {
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
                            populateTeleOPData(localActivity.teleOpData.get(localActivity.teleOpData.size()-1));
                        } else {
                            int index = Integer.parseInt(item.getTitle().toString()) - 1;
                            monoMatch = true;
                            populateTeleOPData(localActivity.teleOpData.get(index));
                        }
                        menu.setText(item.getTitle());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    public void populateTeleOPData(MatchStruct match) {
        DecimalFormat decimalFormat = new DecimalFormat("###.##");

        int totalBeacons, totalParticlesVortex, totalParticlesCorner, totalCapball;
        double beaconsPercentage;
        double particlesVortexPercentage;
        double particlesCornerPercentage;
        double capballPercentage;

        totalBeacons = match.beaconsClaimed+match.beaconsMissed;
        beaconsPercentage = ((double) match.beaconsClaimed) / (double) totalBeacons * 100;

        totalParticlesVortex = match.scoredInVortex+match.missedVortex;
        particlesVortexPercentage = ((double) match.scoredInVortex) / (double) totalParticlesVortex * 100;

        totalParticlesCorner = match.scoredInCorner+match.missedCorner;
        particlesCornerPercentage = (double) match.scoredInCorner / (double) totalParticlesCorner * 100;


        totalCapball = match.capballOffFloor + match.capballAboveCrossbar + match.capballCapped + match.capballMissed;
        capballPercentage = ((double) match.capballOffFloor + (double) match.capballAboveCrossbar + (double) match.capballCapped) / (double) totalCapball * 100;

        beaconsClaimed.setText(""+match.beaconsClaimed);
        beaconsStolen.setText(""+match.beaconsStolen);
        beaconsSuccessPercentage.setText(""+decimalFormat.format(beaconsPercentage)+"%");

        particlesScoredInVortex.setText(""+match.scoredInVortex);
        particlesScoredInCorner.setText(""+match.scoredInCorner);
        particlesMissedVortex.setText(""+match.missedVortex);
        particlesScoredInCorner.setText(""+match.missedCorner);
        particleVortexSuccessPercentage.setText(""+decimalFormat.format(particlesVortexPercentage)+"%");
        particleCornerSuccessPercentage.setText(""+decimalFormat.format(particlesCornerPercentage)+"%");

        capballOffFloor.setText(""+match.capballOffFloor);
        capballAboveCrossbar.setText(""+match.capballAboveCrossbar);
        capballCapped.setText(""+match.capballCapped);
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
