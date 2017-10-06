package org.timecrafters.analyticalengine.athena.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.timecrafters.analyticalengine.R;
import org.timecrafters.analyticalengine.athena.TeamStatisticsActivity;
import org.timecrafters.analyticalengine.hermes.AppSync;

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
        TextView canScoreJewel       = (TextView) getView().findViewById(R.id.autonomous_score_jewel);
        TextView canScoreInCryptobox = (TextView) getView().findViewById(R.id.autonomous_score_in_cryptobox);
        TextView canReadCryptoboxKey = (TextView) getView().findViewById(R.id.autonomous_read_cryptobox_key);
        TextView maxGlyphsScorable   = (TextView) getView().findViewById(R.id.autonomous_max_glyphs);
        TextView canParkInSafeZone   = (TextView) getView().findViewById(R.id.autonomous_park_in_safe_zone);

        TextView autonomous_notes = (TextView) getView().findViewById(R.id.autonomous_notes);

        try {
            if (autonomous != null && autonomous.getBoolean("has_autonomous")) {
                if (autonomous.getBoolean("can_score_jewel")) {
                    canScoreJewel.setText("Yes");
                    canScoreJewel.setTextColor(greenColor);
                } else {
                    canScoreJewel.setText("No");
                    canScoreJewel.setTextColor(redColor);
                }

                if (autonomous.getBoolean("can_score_in_cryptobox")) {
                    canScoreInCryptobox.setText("Yes");
                    canScoreInCryptobox.setTextColor(greenColor);
                    maxGlyphsScorable.setText("" + autonomous.getInt("max_glyphs_scorable"));
                    maxGlyphsScorable.setTextColor(greenColor);
                } else {
                    canScoreInCryptobox.setText("No");
                    canScoreInCryptobox.setTextColor(redColor);
                    maxGlyphsScorable.setText("0");
                    maxGlyphsScorable.setTextColor(redColor);
                }

                if (autonomous.getBoolean("can_read_cryptobox_key")) {
                    canReadCryptoboxKey.setText("Yes");
                    canReadCryptoboxKey.setTextColor(greenColor);
                } else {
                    canReadCryptoboxKey.setText("No");
                    canReadCryptoboxKey.setTextColor(redColor);
                }

                if (autonomous.getBoolean("can_park_in_safe_zone")) {
                    canParkInSafeZone.setText("Yes");
                    canParkInSafeZone.setTextColor(greenColor);
                } else {
                    canParkInSafeZone.setText("No");
                    canParkInSafeZone.setTextColor(redColor);
                }
            }
        } catch (JSONException error) {
            AppSync.puts("AUTO", "JSON Exception Error: "+error.getMessage());
            Toast.makeText(TeamStatisticsActivity.contextForFragment, "Failed to process Autonomous data!", Toast.LENGTH_LONG).show();
        }

    }

    protected void renderTeleOpData(JSONObject teleOp) {
        TextView canScoreInCryptobox = (TextView) getView().findViewById(R.id.teleop_score_in_cryptobox);
        TextView maxGlyphsScorable   = (TextView) getView().findViewById(R.id.teleop_max_glyphs);
        TextView canCompleteCipher   = (TextView) getView().findViewById(R.id.teleop_complete_cipher);
        TextView canScoreRelic       = (TextView) getView().findViewById(R.id.teleop_score_relic);
        TextView relicZones          = (TextView) getView().findViewById(R.id.teleop_relic_zones);
        TextView relicPlacedUpright  = (TextView) getView().findViewById(R.id.teleop_relic_upright);
        TextView canBalanceOnStone   = (TextView) getView().findViewById(R.id.teleop_balance_on_stone);

        TextView telep_notes            = (TextView) getView().findViewById(R.id.teleop_notes);

        try {
            if (teleOp != null) {
                if (teleOp.getBoolean("can_score_in_cryptobox")) {
                    canScoreInCryptobox.setText("Yes");
                    maxGlyphsScorable.setText("" + teleOp.getInt("max_scorable_glyphs"));
                    canScoreInCryptobox.setTextColor(greenColor);
                    maxGlyphsScorable.setTextColor(greenColor);
                } else {
                    canScoreInCryptobox.setText("No");
                    maxGlyphsScorable.setText("0");
                    canScoreInCryptobox.setTextColor(redColor);
                    maxGlyphsScorable.setTextColor(redColor);
                }

                if (teleOp.getBoolean("can_complete_cipher")) {
                    canCompleteCipher.setText("Yes");
                    canCompleteCipher.setTextColor(greenColor);
                } else {
                    canCompleteCipher.setText("No");
                    canCompleteCipher.setTextColor(redColor);
                }

                if (teleOp.getBoolean("can_score_relic")) {
                    canScoreRelic.setText("Yes");
                    canScoreRelic.setTextColor(greenColor);
                } else {
                    canScoreRelic.setText("No");
                    canScoreRelic.setTextColor(redColor);
                }

                if (teleOp.getBoolean("relic_upright")) {
                    relicPlacedUpright.setText("Yes");
                    relicPlacedUpright.setTextColor(greenColor);
                } else {
                    relicPlacedUpright.setText("No");
                    relicPlacedUpright.setTextColor(redColor);
                }

                if (teleOp.getBoolean("can_balance_on_stone")) {
                    canBalanceOnStone.setText("Yes");
                    canBalanceOnStone.setTextColor(greenColor);
                } else {
                    canBalanceOnStone.setText("No");
                    canBalanceOnStone.setTextColor(redColor);
                }

                if (teleOp.getString("teleop_notes").length() > 0) {
                    telep_notes.setText(teleOp.getString("teleop_notes"));
                }
            }
        } catch (JSONException | NullPointerException error) {
            AppSync.puts("TELE", "Exception Error: "+error.getMessage());
            Toast.makeText(TeamStatisticsActivity.contextForFragment, "Failed to process TeleOp data!", Toast.LENGTH_LONG).show();
        }
    }
}
