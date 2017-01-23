package org.timecrafters.ftcscouting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ScoutMatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_match);

        final Button driversButton = (Button) findViewById(R.id.team_selection);
        driversButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ScoutMatchActivity.this, driversButton);

                int[] localTeamList;
                localTeamList[0] = 8962;

                for(int i = 0; i < localTeamList.length; i++) {
                    popupMenu.getMenu().add(""+localTeamList[i]);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                    }
                });
                popupMenu.show();
            }
        });
    }
}
