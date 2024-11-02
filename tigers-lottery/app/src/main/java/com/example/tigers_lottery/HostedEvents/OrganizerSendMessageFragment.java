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

public class OrganizerSendMessageFragment extends Fragment {
    Boolean sendToSelected, sendToCancelled, sendToWaiting;

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
