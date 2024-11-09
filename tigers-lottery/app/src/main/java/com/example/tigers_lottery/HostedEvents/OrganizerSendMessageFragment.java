package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.R;

/**
 * Fragment for sending a message to a group of entrants tied to the organizer's event
 * The organizer may pick which group they want to send the messages to.
 */

public class OrganizerSendMessageFragment extends Fragment {
    Boolean sendToSelected, sendToCancelled, sendToWaiting;

    /**
     * Inflates the layout for the options to send a message to entrants tied to
     * the event.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the send message screen UI.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.organizer_sendmessage_fragment, container, false);
        Button sendButton = view.findViewById(R.id.sendMessageSendButton);
        CheckBox selectedBox = view.findViewById(R.id.sendMessageSelectedEntrants);
        CheckBox cancelledBox = view.findViewById(R.id.sendMessageCancelledEntrants);
        CheckBox waitingBox = view.findViewById(R.id.sendMessageWaitingList);

        EditText editText = view.findViewById(R.id.sendMessageEditText);
        String input = editText.getText().toString();

        selectedBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                sendToSelected=true;
        } else {
                sendToSelected=false;
            }
        });

        cancelledBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                sendToCancelled=true;
            } else {
                sendToCancelled=false;
            }
        });

        waitingBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                sendToWaiting=true;
            } else {
                sendToWaiting=false;
            }
        });


        sendButton.setOnClickListener(v->{

        });
        return view;
    }
}
