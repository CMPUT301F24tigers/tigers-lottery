package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.R;

public class OrganizerChosenEntrantsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.organizer_chosenentrants_fragment, container, false);
        Button cancelButton = view.findViewById(R.id.cancelRemainingButton);
        SearchView searchView = view.findViewById(R.id.chosenEntrantsSearch);
        ListView listView = view.findViewById(R.id.chosenEntrantsList);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                handleSearch(s);
                return true;
            }
        });



        cancelButton.setOnClickListener(v->{

        });
        return view;
    }
    private void handleSearch(String s){

    }
}
