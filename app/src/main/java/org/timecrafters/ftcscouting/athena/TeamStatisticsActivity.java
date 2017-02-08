package org.timecrafters.ftcscouting.athena;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.athena.fragments.AutonomousFragment;
import org.timecrafters.ftcscouting.athena.fragments.TeamScoutingDataFragment;
import org.timecrafters.ftcscouting.athena.fragments.TeleOpFragment;
import org.timecrafters.ftcscouting.hermes.AppSync;
import org.timecrafters.ftcscouting.hermes.EventStruct;
import org.timecrafters.ftcscouting.hermes.MatchStruct;

import java.util.ArrayList;

public class TeamStatisticsActivity extends AppCompatActivity {
    public static TeamStatisticsActivity contextForFragment;
    public ArrayList<ArrayList<EventStruct>> matchData;
    public ArrayList<MatchStruct> autonomousData = new ArrayList<>();
    public ArrayList<MatchStruct> teleOpData = new ArrayList<>();

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        contextForFragment = this;
        if (AppSync.teamHasMatchData()) {
            AppSync.puts("STATS", "Team has Match Data!");

            matchData = AppSync.teamMatchData();
            MatchStruct allAutonomousMatches = new MatchStruct();
            MatchStruct allTeleOpMatches = new MatchStruct();

            for (ArrayList events : matchData) {
                MatchStruct autonomousMatch = new MatchStruct();
                MatchStruct teleOpMatch = new MatchStruct();

                for (int n = 0; n < events.size(); n++) {
                    EventStruct event = (EventStruct) events.get(n);

                    if (event.period.equals("autonomous")) {
                        if (event.type.equals("score")) {
                            if (event.subtype.equals("beacon")) {
                                allAutonomousMatches.beaconsClaimed++;
                                autonomousMatch.beaconsClaimed++;
                            }

                            if (event.subtype.equals("particle")) {
                                if (event.location.equals("vortex")) {
                                    allAutonomousMatches.scoredInVortex++;
                                    autonomousMatch.scoredInVortex++;
                                }
                                if (event.location.equals("corner")) {
                                    allAutonomousMatches.scoredInCorner++;
                                    autonomousMatch.scoredInCorner++;
                                }
                            }

                            if (event.subtype.equals("capball")) {
                                if (event.location.equals("floor")) {
                                    allAutonomousMatches.capballOnFloor++;
                                    autonomousMatch.capballOnFloor++;
                                }
                            }

                            if (event.subtype.equals("parking")) {
                                if (event.location.equals("on_platform")) {
                                    allAutonomousMatches.completelyOnPlatform++;
                                    autonomousMatch.completelyOnPlatform++;
                                }
                                if (event.location.equals("on_ramp")) {
                                    allAutonomousMatches.completelyOnRamp++;
                                    autonomousMatch.completelyOnRamp++;
                                }
                                if (event.location.equals("platform")) {
                                    allAutonomousMatches.onPlatform++;
                                    autonomousMatch.onPlatform++;
                                }
                                if (event.location.equals("ramp")) {
                                    allAutonomousMatches.onRamp++;
                                    autonomousMatch.onRamp++;
                                }
                            }
                        }

                        if (event.type.equals("miss")) {
                            if (event.subtype.equals("beacon")) {
                                allAutonomousMatches.beaconsMissed++;
                                autonomousMatch.beaconsMissed++;
                            }

                            if (event.subtype.equals("particle")) {
                                if (event.location.equals("vortex")) {
                                    allAutonomousMatches.missedVortex++;
                                    autonomousMatch.missedVortex++;
                                }
                                if (event.location.equals("corner")) {
                                    allAutonomousMatches.missedCorner++;
                                    autonomousMatch.missedCorner++;
                                }
                            }

                            if (event.subtype.equals("parking")) {
                                allAutonomousMatches.missedParking++;
                                autonomousMatch.missedParking++;
                            }

                            if (event.subtype.equals("capball")) {
                                allAutonomousMatches.capballMissed++;
                                autonomousMatch.capballMissed++;
                            }

                            if (event.subtype.equals("robot")) {
                                allAutonomousMatches.deadRobot++;
                                autonomousMatch.deadRobot++;
                                autonomousMatch.is_deadRobot = true;
                            }
                        }
                    } else {
                        if (event.type.equals("score")) {
                            if (event.subtype.equals("beacon")) {
                                AppSync.puts("STATS", "Score Beacon");
                                allTeleOpMatches.beaconsClaimed++;
                                teleOpMatch.beaconsClaimed++;
                            }

                            if (event.subtype.equals("particle")) {
                                if (event.location.equals("vortex")) {
                                    allTeleOpMatches.scoredInVortex++;
                                    teleOpMatch.scoredInVortex++;
                                }
                                if (event.location.equals("corner")) {
                                    allTeleOpMatches.scoredInCorner++;
                                    teleOpMatch.scoredInCorner++;
                                }
                            }

                            if (event.subtype.equals("capball")) {
                                if (event.location.equals("off_floor")) {
                                    allTeleOpMatches.capballOffFloor++;
                                    teleOpMatch.capballOffFloor++;
                                }

                                if (event.location.equals("above_crossbar")) {
                                    allTeleOpMatches.capballAboveCrossbar++;
                                    teleOpMatch.capballAboveCrossbar++;
                                }

                                if (event.location.equals("capped")) {
                                    allTeleOpMatches.capballCapped++;
                                    teleOpMatch.capballCapped++;
                                }
                            }
                        }

                        if (event.type.equals("miss")) {
                            if (event.subtype.equals("beacon")) {
                                allTeleOpMatches.beaconsMissed++;
                                teleOpMatch.beaconsMissed++;
                            }

                            if (event.subtype.equals("particle")) {
                                if (event.location.equals("vortex")) {
                                    allTeleOpMatches.missedVortex++;
                                    teleOpMatch.missedVortex++;
                                }
                                if (event.location.equals("corner")) {
                                    allTeleOpMatches.missedCorner++;
                                    teleOpMatch.missedCorner++;
                                }
                            }

                            if (event.subtype.equals("capball")) {
                                allTeleOpMatches.capballMissed++;
                                teleOpMatch.capballMissed++;
                            }

                            if (event.subtype.equals("robot")) {
                                allTeleOpMatches.deadRobot++;
                                teleOpMatch.deadRobot++;
                                teleOpMatch.is_deadRobot = true;
                            }
                        }
                    }
                }
                autonomousData.add(autonomousMatch);
                teleOpData.add(teleOpMatch);
            }
            autonomousData.add(allAutonomousMatches);
            teleOpData.add(allTeleOpMatches);
        } else { matchData = null; AppSync.puts("STATS", "No Team Match Data!"); }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return TeamScoutingDataFragment.newInstance();
                case 1:
                    return AutonomousFragment.newInstance();
                case 2:
                    return TeleOpFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Scouting";
                case 1:
                    return "Autonomous";
                case 2:
                    return "TeleOp";
            }
            return null;
        }
    }
}
