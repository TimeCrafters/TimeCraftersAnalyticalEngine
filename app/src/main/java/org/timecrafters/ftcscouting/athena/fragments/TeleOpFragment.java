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

public class TeleOpFragment extends Fragment {
    TextView team;

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
    }
}
