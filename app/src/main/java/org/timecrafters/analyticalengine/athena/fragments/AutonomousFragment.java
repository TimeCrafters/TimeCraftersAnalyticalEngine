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

    TextView jewelScored;
    TextView jewelMissed;
    TextView jewelPercentageText;

    TextView glyphScored;
    TextView glyphReadCryptoboxKey;
    TextView glyphMissed;
    TextView glyphPercentageText;

    TextView parkingSafeZone;
    TextView parkingMissed;
    TextView parkingPercentageText;

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

        jewelScored = (TextView) getView().findViewById(R.id.autonomous_jewel_scored);
        jewelMissed = (TextView) getView().findViewById(R.id.autonomous_jewel_missed);
        jewelPercentageText = (TextView) getView().findViewById(R.id.autonomous_jewel_success_percentage);

        glyphScored = (TextView) getView().findViewById(R.id.autonomous_glyph_scored);
        glyphReadCryptoboxKey = (TextView) getView().findViewById(R.id.autonomous_glyph_read_cryptobox_key);
        glyphMissed = (TextView) getView().findViewById(R.id.autonomous_glyph_missed);
        glyphPercentageText = (TextView) getView().findViewById(R.id.autonomous_glyph_success_percentage);

        parkingSafeZone = (TextView) getView().findViewById(R.id.autonomous_park_in_safe_zone);
        parkingMissed   = (TextView) getView().findViewById(R.id.autonomous_park_missed);
        parkingPercentageText = (TextView) getView().findViewById(R.id.autonomous_parking_success_percentage);

        robotDead = (TextView) getView().findViewById(R.id.autonomous_robot_robot);

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
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        AppSync.puts("MATCHSTRUCT", match.toString());

        int totalJewels, totalGlyphs, totalCryptoKeys, totalParking;
        double jewelPercentage;
        double glyphPercentage;
        double parkingPercentage;

        totalJewels = match.jewelScored+match.jewelMissed;
        jewelPercentage = ((double) match.jewelScored) / (double) totalJewels * 100;

        totalGlyphs = match.glyphScored+match.glyphMissed;
        glyphPercentage = ((double) match.glyphScored) / (double) totalGlyphs * 100;

        totalParking = match.parkSafeZone+match.parkMissed;
        parkingPercentage = (double) match.parkSafeZone / (double) totalParking * 100;

        jewelScored.setText(""+match.jewelScored);
        jewelMissed.setText(""+match.jewelMissed);
        jewelPercentageText.setText(""+decimalFormat.format(jewelPercentage)+"%");

        glyphScored.setText(""+match.glyphScored);
        glyphReadCryptoboxKey.setText(""+match.glyphCryptoboxKey);
        glyphMissed.setText(""+match.glyphMissed);
        glyphPercentageText.setText(""+decimalFormat.format(glyphPercentage)+"%");

        parkingSafeZone.setText(""+match.parkSafeZone);
        parkingMissed.setText(""+match.parkMissed);
        parkingPercentageText.setText(""+decimalFormat.format(parkingPercentage)+"%");

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
