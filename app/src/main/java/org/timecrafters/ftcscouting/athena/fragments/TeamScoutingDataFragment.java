package org.timecrafters.ftcscouting.athena.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.timecrafters.ftcscouting.MainActivity;
import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.athena.TeamStatisticsActivity;
import org.timecrafters.ftcscouting.hermes.AppSync;

public class TeamScoutingDataFragment extends Fragment {
    TextView team;
    int greenColor = Color.parseColor("#008800");
    int redColor   = Color.parseColor("#990000");

    public TeamScoutingDataFragment() {
        // Required empty public constructor
    }

    public static TeamScoutingDataFragment newInstance() {
        TeamScoutingDataFragment fragment = new TeamScoutingDataFragment();
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
        return inflater.inflate(R.layout.fragment_team_scouting_data, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        team = (TextView) getView().findViewById(R.id.team);
        team.setText(""+ AppSync.teamNumber+ " | "+ AppSync.teamName);

        if (AppSync.teamHasScoutingData()) {
            JSONObject autonomous = AppSync.teamScoutingData("autonomous");
            JSONObject teleop     = AppSync.teamScoutingData("teleop");

            renderAutonomousData(autonomous);
            renderTeleOpData(teleop);
        } else {
            Toast.makeText(TeamStatisticsActivity.contextForFragment, "Unable to find scouting data.", Toast.LENGTH_LONG).show();
        }
    }

    protected void renderAutonomousData(JSONObject autonomous) {
        TextView can_claim_beacons   = (TextView) getView().findViewById(R.id.autonomous_can_claim_beacons);
        TextView max_beacons_claimed = (TextView) getView().findViewById(R.id.autonomous_max_beacons_claimed);

        TextView can_score_in_vortex = (TextView) getView().findViewById(R.id.autonomous_can_score_in_vortex);
        TextView can_score_in_corner = (TextView) getView().findViewById(R.id.autonomous_can_score_in_corner);
        TextView max_scored_in_vortex= (TextView) getView().findViewById(R.id.autonomous_max_scored_in_vortex);
        TextView max_scored_in_corner= (TextView) getView().findViewById(R.id.autonomous_max_scored_in_corner);

        TextView can_park_completely_on_platform = (TextView) getView().findViewById(R.id.autonomous_can_park_completely_on_platform);
        TextView can_park_completely_on_ramp     = (TextView) getView().findViewById(R.id.autonomous_can_park_completely_on_ramp);
        TextView can_park_on_platform = (TextView) getView().findViewById(R.id.autonomous_can_park_on_platform);
        TextView can_park_on_ramp     = (TextView) getView().findViewById(R.id.autonomous_can_park_on_ramp);

        TextView can_put_capball_on_floor = (TextView) getView().findViewById(R.id.autonomous_can_put_capball_on_floor);

        TextView autonomous_notes = (TextView) getView().findViewById(R.id.autonomous_notes);

        try {
            if (autonomous.getBoolean("has_autonomous")) {
                if (autonomous.getBoolean("can_claim_beacons")) {
                    can_claim_beacons.setText("Yes");
                    max_beacons_claimed.setText(""+autonomous.getInt("max_beacons_claimable"));
                    can_claim_beacons.setTextColor(greenColor);
                    max_beacons_claimed.setTextColor(greenColor);
                } else {
                    can_claim_beacons.setText("No");
                    max_beacons_claimed.setText("0");
                    can_claim_beacons.setTextColor(redColor);
                    max_beacons_claimed.setTextColor(redColor);
                }
            }

            if (autonomous.getBoolean("can_score_in_vortex")) {
                can_score_in_vortex.setText("Yes");
                can_score_in_vortex.setTextColor(greenColor);
                max_scored_in_vortex.setText(""+autonomous.getInt("max_particles_scored_in_vortex"));
                max_scored_in_vortex.setTextColor(greenColor);
            } else {
                can_score_in_vortex.setText("No");
                can_score_in_vortex.setTextColor(redColor);
                max_scored_in_vortex.setText("0");
                max_scored_in_vortex.setTextColor(redColor);
            }

            if (autonomous.getBoolean("can_score_in_corner")) {
                can_score_in_corner.setText("Yes");
                can_score_in_corner.setTextColor(greenColor);
                max_scored_in_corner.setText(""+autonomous.getInt("max_particles_scored_in_corner"));
                max_scored_in_corner.setTextColor(greenColor);
            } else {
                can_score_in_corner.setText("No");
                can_score_in_corner.setTextColor(redColor);
                max_scored_in_corner.setText("0");
                max_scored_in_corner.setTextColor(redColor);
            }

            if (autonomous.getBoolean("park_completely_on_platform")) {
                can_park_completely_on_platform.setText("Yes");
                can_park_completely_on_platform.setTextColor(greenColor);
            } else {
                can_park_completely_on_platform.setText("No");
                can_park_completely_on_platform.setTextColor(redColor);
            }

            if (autonomous.getBoolean("park_on_platform")) {
                can_park_on_platform.setText("Yes");
                can_park_on_platform.setTextColor(greenColor);
            } else {
                can_park_on_platform.setText("No");
                can_park_on_platform.setTextColor(redColor);
            }

            if (autonomous.getBoolean("park_completely_on_ramp")) {
                can_park_completely_on_ramp.setText("Yes");
                can_park_completely_on_ramp.setTextColor(greenColor);
            } else {
                can_park_completely_on_ramp.setText("No");
                can_park_completely_on_ramp.setTextColor(redColor);
            }

            if (autonomous.getBoolean("park_on_ramp")) {
                can_park_on_ramp.setText("Yes");
                can_park_on_ramp.setTextColor(greenColor);
            } else {
                can_park_on_ramp.setText("No");
                can_park_on_ramp.setTextColor(redColor);
            }

            if (autonomous.getBoolean("capball_on_floor")) {
                can_put_capball_on_floor.setText("Yes");
                can_put_capball_on_floor.setTextColor(greenColor);
            } else {
                can_put_capball_on_floor.setText("No");
                can_put_capball_on_floor.setTextColor(redColor);
            }

            if (autonomous.getString("autonomous_notes").length() > 0) {
                autonomous_notes.setText(autonomous.getString("autonomous_notes"));
            }
        } catch (JSONException error) {
            AppSync.puts("AUTO", "JSON Exception Error: "+error.getMessage());
            Toast.makeText(TeamStatisticsActivity.contextForFragment, "Failed to process Autonomous data!", Toast.LENGTH_LONG).show();
        }

    }

    protected void renderTeleOpData(JSONObject teleOp) {
        TextView particles_scored_in_vortex = (TextView) getView().findViewById(R.id.teleop_max_scored_in_vortex);
        TextView particles_scored_in_corner = (TextView) getView().findViewById(R.id.teleop_max_scored_in_corner);

        TextView can_claim_beacons   = (TextView) getView().findViewById(R.id.teleop_can_claim_beacons);
        TextView max_beacons_claimed = (TextView) getView().findViewById(R.id.teleop_max_claimed_beacons);

        TextView capball_off_floor      = (TextView) getView().findViewById(R.id.teleop_capball_off_floor);
        TextView capball_above_crossbar = (TextView) getView().findViewById(R.id.teleop_capball_above_crossbar);
        TextView capball_capped         = (TextView) getView().findViewById(R.id.teleop_capball_capped);

        TextView telep_notes            = (TextView) getView().findViewById(R.id.teleop_notes);

        try {
            if (teleOp.getBoolean("can_claim_beacons")) {
                can_claim_beacons.setText("Yes");
                max_beacons_claimed.setText("" + teleOp.getInt("max_beacons_claimable"));
                can_claim_beacons.setTextColor(greenColor);
                max_beacons_claimed.setTextColor(greenColor);
            } else {
                can_claim_beacons.setText("No");
                max_beacons_claimed.setText("0");
                can_claim_beacons.setTextColor(redColor);
                max_beacons_claimed.setTextColor(redColor);
            }

            if (teleOp.getBoolean("can_score_in_vortex")) {
                particles_scored_in_vortex.setText("" + teleOp.getInt("max_particles_scored_in_vortex"));
                particles_scored_in_vortex.setTextColor(greenColor);
            } else {
                particles_scored_in_vortex.setText("0");
                particles_scored_in_vortex.setTextColor(redColor);
            }

            if (teleOp.getBoolean("can_score_in_corner")) {
                particles_scored_in_corner.setText("" + teleOp.getInt("max_particles_scored_in_corner"));
                particles_scored_in_corner.setTextColor(greenColor);
            } else {
                particles_scored_in_corner.setText("0");
                particles_scored_in_corner.setTextColor(redColor);
            }

            if (teleOp.getBoolean("capball_off_floor")) {
                capball_off_floor.setText("Yes");
                capball_off_floor.setTextColor(greenColor);
            } else {
                capball_off_floor.setText("No");
                capball_off_floor.setTextColor(redColor);
            }

            if (teleOp.getBoolean("capball_above_crossbar")) {
                capball_above_crossbar.setText("Yes");
                capball_above_crossbar.setTextColor(greenColor);
            } else {
                capball_above_crossbar.setText("No");
                capball_above_crossbar.setTextColor(redColor);
            }


            if (teleOp.getBoolean("capball_capped")) {
                capball_capped.setText("Yes");
                capball_capped.setTextColor(greenColor);
            } else {
                capball_capped.setText("No");
                capball_capped.setTextColor(redColor);
            }

            if (teleOp.getString("teleop_notes").length() > 0) {
                telep_notes.setText(teleOp.getString("teleop_notes"));
            }
        } catch (JSONException error) {
            AppSync.puts("TELE", "JSON Exception Error: "+error.getMessage());
            AppSync.puts("TELE", "JSON Data: "+teleOp.toString());
            Toast.makeText(TeamStatisticsActivity.contextForFragment, "Failed to process TeleOp data!", Toast.LENGTH_LONG).show();
        }
    }
}
