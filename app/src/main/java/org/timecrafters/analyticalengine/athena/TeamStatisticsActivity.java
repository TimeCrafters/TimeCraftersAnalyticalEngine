package org.timecrafters.analyticalengine.athena;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.timecrafters.analyticalengine.R;
import org.timecrafters.analyticalengine.athena.fragments.AutonomousFragment;
import org.timecrafters.analyticalengine.athena.fragments.TeamScoutingDataFragment;
import org.timecrafters.analyticalengine.athena.fragments.TeleOpFragment;
import org.timecrafters.analyticalengine.hermes.AppSync;
import org.timecrafters.analyticalengine.hermes.EventStruct;
import org.timecrafters.analyticalengine.hermes.MatchStruct;

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
                        if (event.type.equals("scored")) {
                            if (event.subtype.equals("jewel")) {
                                allAutonomousMatches.jewelScored++;
                                autonomousMatch.jewelScored++;
                            }

                            if (event.subtype.equals("glyph")) {
                                if (event.location.equals("glyph")) {
                                    allAutonomousMatches.glyphScored++;
                                    autonomousMatch.glyphScored++;
                                }
                                if (event.location.equals("cryptokey")) {
                                    allAutonomousMatches.glyphCryptoboxKey++;
                                    autonomousMatch.glyphCryptoboxKey++;
                                }
                            }

                            if (event.subtype.equals("parking")) {
                                 allAutonomousMatches.parkSafeZone++;
                                 autonomousMatch.parkSafeZone++;
                            }
                        }

                        if (event.type.equals("missed")) {
                            if (event.subtype.equals("jewel")) {
                                allAutonomousMatches.jewelMissed++;
                                autonomousMatch.jewelMissed++;
                            }

                            if (event.subtype.equals("glyph")) {
                                allAutonomousMatches.glyphMissed++;
                                autonomousMatch.glyphMissed++;
                            }

                            if (event.subtype.equals("parking")) {
                                allAutonomousMatches.parkMissed++;
                                autonomousMatch.parkMissed++;
                            }

                            if (event.subtype.equals("robot")) {
                                allAutonomousMatches.deadRobot++;
                                autonomousMatch.deadRobot++;
                                autonomousMatch.is_deadRobot = true;
                            }
                        }
                    } else {
                        if (event.type.equals("scored")) {
                            if (event.subtype.equals("glyph")) {
                                allTeleOpMatches.glyphScored++;
                                teleOpMatch.glyphScored++;
                            }

                            if (event.subtype.equals("relic")) {
                                if (event.location.equals("zone_one")) {
                                    allTeleOpMatches.relicZone1++;
                                    teleOpMatch.relicZone1++;
                                }
                                if (event.location.equals("zone_two")) {
                                    allTeleOpMatches.relicZone2++;
                                    teleOpMatch.relicZone2++;
                                }
                                if (event.location.equals("zone_three")) {
                                    allTeleOpMatches.relicZone3++;
                                    teleOpMatch.relicZone3++;
                                }
                                if (event.location.equals("upright")) {
                                    allTeleOpMatches.relicUpright++;
                                    teleOpMatch.relicUpright++;
                                    teleOpMatch.is_relicUpright = true;
                                }
                            }

                            if (event.subtype.equals("balance")) {
                                allTeleOpMatches.parkSafeZone++;
                                teleOpMatch.parkSafeZone++;
                            }
                        }

                        if (event.type.equals("missed")) {
                            if (event.subtype.equals("glyph")) {
                                allTeleOpMatches.glyphMissed++;
                                teleOpMatch.glyphMissed++;
                            }

                            if (event.subtype.equals("relic")) {
                                allTeleOpMatches.relicMissed++;
                                teleOpMatch.relicMissed++;
                            }

                            if (event.subtype.equals("parking")) {
                                allTeleOpMatches.parkMissed++;
                                teleOpMatch.parkMissed++;
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
