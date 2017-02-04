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
    public ArrayList<EventStruct> matchData;
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
            matchData = AppSync.teamMatchData();
            MatchStruct allAutonomousMatches = new MatchStruct();
            for (EventStruct event : matchData) {
                if (event.period.equals("autonomous")) {
                    if (event.type.equals("score")){
                        if (event.subtype.equals("beacon")) {
                            allAutonomousMatches.beaconsClaimed++;
                        }

                        if (event.subtype.equals("particle")) {
                            if (event.location.equals("vortex")) {
                                allAutonomousMatches.scoredInVortex++;
                            }
                            if (event.location.equals("corner")) {
                                allAutonomousMatches.scoredInCorner++;
                            }
                        }

                        if (event.subtype.equals("parking")) {
                            if (event.location.equals("on_platform")) {
                                allAutonomousMatches.completelyOnPlatform++;
                            }
                            if (event.location.equals("on_ramp")) {
                                allAutonomousMatches.completelyOnRamp++;
                            }
                            if (event.location.equals("platform")) {
                                allAutonomousMatches.onPlatform++;
                            }
                            if (event.location.equals("ramp")) {
                                allAutonomousMatches.onRamp++;
                            }
                        }
                    }

                    if (event.type.equals("miss")){
                        if (event.subtype.equals("beacon")) {
                            allAutonomousMatches.beaconsMissed++;
                        }

                        if (event.subtype.equals("particle")) {
                            if (event.location.equals("vortex")) {
                                allAutonomousMatches.missedVortex++;
                            }
                            if (event.location.equals("corner")) {
                                allAutonomousMatches.missedCorner++;
                            }
                        }

                        if (event.subtype.equals("parking")) {
                            if (event.location.equals("on_platform")) {
                                allAutonomousMatches.missedPlatform++;
                            }
                            if (event.location.equals("on_ramp")) {
                                allAutonomousMatches.missedRamp++;
                            }
                            if (event.location.equals("platform")) {
                                allAutonomousMatches.missedPlatform++;
                            }
                            if (event.location.equals("ramp")) {
                                allAutonomousMatches.missedRamp++;
                            }
                        }
                    }
                }
            }

            autonomousData.add(allAutonomousMatches);
        } else { matchData = null; }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            AppSync.createConfirmDialog(this, "Are you sure?", "This will reload the parsed data and may take a moment.", new Runnable() {
                @Override
                public void run() {
                    // yes
                }
            }, new Runnable() {
                @Override
                public void run() {
                    // no
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
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
