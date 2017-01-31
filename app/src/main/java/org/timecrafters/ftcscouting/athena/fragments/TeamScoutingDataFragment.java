package org.timecrafters.ftcscouting.athena.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.timecrafters.ftcscouting.MainActivity;
import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.hermes.AppSync;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeamScoutingDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeamScoutingDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamScoutingDataFragment extends Fragment {
    TextView team;

    // TODO: Rename and change types of parameters

    public TeamScoutingDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TeamScoutingDataFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        team = (TextView) container.findViewById(R.id.team);
        team.setText(""+ AppSync.teamNumber+ " | "+ AppSync.teamName);
        return inflater.inflate(R.layout.fragment_team_scouting_data, container, false);
    }
}
