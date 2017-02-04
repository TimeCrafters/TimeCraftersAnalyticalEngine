package org.timecrafters.ftcscouting.athena.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.athena.TeamStatisticsActivity;
import org.timecrafters.ftcscouting.hermes.AppSync;
import org.timecrafters.ftcscouting.hermes.MatchStruct;

import java.text.DecimalFormat;

public class TeleOpFragment extends Fragment {
    TextView team;
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

    TextView parkedCompletelyOnPlatform;
    TextView parkedCompletelyOnRamp;
    TextView parkedOnPlatform;
    TextView parkedOnRamp;
    TextView parkedMissedPlatform;
    TextView parkedMissedRamp;
    TextView parkedSuccessPercentage;

    TextView capballOffFloor;
    TextView capballAboveCrossbar;
    TextView capballCapped;
    TextView capballMissed;
    TextView capballSuccessPercentage;

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
        team = (TextView) getView().findViewById(R.id.team);
        team.setText(""+ AppSync.teamNumber+ " | "+ AppSync.teamName);
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

        if (AppSync.teamHasMatchData() && TeamStatisticsActivity.contextForFragment.teleOpData != null && TeamStatisticsActivity.contextForFragment.teleOpData.size() > 0) {
            dataset.setText(""+(TeamStatisticsActivity.contextForFragment.teleOpData.size()-1)+" in dataset");

            populateTeleOPData(TeamStatisticsActivity.contextForFragment.teleOpData.get(TeamStatisticsActivity.contextForFragment.teleOpData.size()-1));
        } else {
            // No match data!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
    }

    public void populateTeleOPData(MatchStruct match) {
        DecimalFormat decimalFormat = new DecimalFormat("###.##");

        int totalBeacons, totalParticlesVortex, totalParticlesCorner, totalCapball;
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
    }
}
