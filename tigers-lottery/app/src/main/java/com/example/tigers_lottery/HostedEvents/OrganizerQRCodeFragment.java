package com.example.tigers_lottery.HostedEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.R;

public class OrganizerQRCodeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.organizer_qrcode_fragment, container, false);
        Button regenerateButton = view.findViewById(R.id.qrCodeRegenerateButton);
        ImageView imageView = view.findViewById(R.id.qrCodeImage);

        regenerateButton.setOnClickListener(v->{

        });
        return view;
    }
}
