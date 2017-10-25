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

    TextView glyphScored;
    TextView glyphMissed;
    TextView glyphSuccessPercentage;

    TextView relicZoneOne;
    TextView relicZoneTwo;
    TextView relicZoneThree;
    TextView relicUpright;
    TextView relicMissed;
    TextView relicSuccessPercentage;

    TextView parkingBalanced;
    TextView parkingMissed;
    TextView ParkingSuccessPercentage;

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

        glyphScored = (TextView) getView().findViewById(R.id.teleop_glyph_scored);
        glyphMissed = (TextView) getView().findViewById(R.id.teleop_glyph_missed);
        glyphSuccessPercentage = (TextView) getView().findViewById(R.id.teleop_glyph_success_percentage);

        relicZoneOne   = (TextView) getView().findViewById(R.id.teleop_relic_zone_1);
        relicZoneTwo   = (TextView) getView().findViewById(R.id.teleop_relic_zone_2);
        relicZoneThree = (TextView) getView().findViewById(R.id.teleop_relic_zone_3);
        relicUpright   = (TextView) getView().findViewById(R.id.teleop_relic_upright);
        relicMissed    = (TextView) getView().findViewById(R.id.teleop_relic_missed);
        relicSuccessPercentage = (TextView) getView().findViewById(R.id.teleop_relic_success_percentage);

        parkingBalanced = (TextView) getView().findViewById(R.id.teleop_parking_balanced);
        parkingMissed   = (TextView) getView().findViewById(R.id.teleop_parking_missed);
        ParkingSuccessPercentage = (TextView) getView().findViewById(R.id.teleop_parking_success_percentage);

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

        int totalGlyphs, totalRelicZones, totalRelicUpright, totalParking;
        double glyphPercentage;
        double relicPercentage;
        double parkingPercentage;

        totalGlyphs     = match.glyphScored+match.glyphMissed;
        glyphPercentage = ((double) match.glyphScored) / (double) totalGlyphs * 100;

        totalRelicZones = match.relicZone1+match.relicZone2+match.relicZone3+match.relicMissed;
        relicPercentage = ((double) match.relicZone1+match.relicZone2+match.relicZone3) / (double) totalRelicZones * 100;

        totalParking = match.parkSafeZone+match.parkMissed;
        parkingPercentage = ((double) match.parkSafeZone) / (double) totalParking * 100;

        glyphScored.setText(""+match.glyphScored);
        glyphMissed.setText(""+match.glyphMissed);
        glyphSuccessPercentage.setText(""+decimalFormat.format(glyphPercentage)+"%");

        relicZoneOne.setText(""+match.relicZone1);
        relicZoneTwo.setText(""+match.relicZone2);
        relicZoneThree.setText(""+match.relicZone3);
        relicUpright.setText(""+match.relicUpright);
        relicMissed.setText(""+match.relicMissed);
        relicSuccessPercentage.setText(""+decimalFormat.format(relicPercentage)+"%");

        parkingBalanced.setText(""+match.parkSafeZone);
        parkingMissed.setText(""+match.parkMissed);
        ParkingSuccessPercentage.setText(""+decimalFormat.format(parkingPercentage)+"%");

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
