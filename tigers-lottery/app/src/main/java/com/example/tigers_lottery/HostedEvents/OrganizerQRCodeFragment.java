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


/**
 * Organizer view of the QRCode for their event. The organizer has
 * access to a button to regenerate to a new unique QRCode.
 */
public class OrganizerQRCodeFragment extends Fragment {
    /**
     * Inflates the layout for the image displaying the QRCode, and the button to regenerate
     * the QRCode.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the fragment view's UI.
     */
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
