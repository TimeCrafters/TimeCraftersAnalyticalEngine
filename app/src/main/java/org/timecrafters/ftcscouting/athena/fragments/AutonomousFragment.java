package org.timecrafters.ftcscouting.athena.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.timecrafters.ftcscouting.R;
import org.timecrafters.ftcscouting.hermes.AppSync;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AutonomousFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AutonomousFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutonomousFragment extends Fragment {
    TextView team;

    public AutonomousFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AutonomousFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        team = (TextView) getView().findViewById(R.id.team);
        team.setText(""+ AppSync.teamNumber+ " | "+ AppSync.teamName);
    }
}
