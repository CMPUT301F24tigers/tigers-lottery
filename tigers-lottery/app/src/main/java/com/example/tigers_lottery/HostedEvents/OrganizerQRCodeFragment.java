package com.example.tigers_lottery.HostedEvents;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.utils.QRCodeGenerator;

import java.util.List;


/**
 * Organizer view of the QRCode for their event. The organizer has
 * access to a button to regenerate to a new unique QRCode.
 */
public class OrganizerQRCodeFragment extends Fragment {
    DatabaseHelper dbHelper;
    private static final String ARG_EVENT_ID = "event_id";
    private int eventId;
    private Event event;
    private ImageView QRImage;
    private Button regenerateButton;

    public OrganizerQRCodeFragment(){}

    public static OrganizerQRCodeFragment newInstance(int eventId){
        OrganizerQRCodeFragment fragment = new OrganizerQRCodeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            eventId=getArguments().getInt(ARG_EVENT_ID);
        }
        dbHelper = new DatabaseHelper(requireContext());

    }
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
        regenerateButton = view.findViewById(R.id.qrCodeRegenerateButton);
        QRImage = view.findViewById(R.id.qrCodeImageLarge);

        loadEventDetails();

        regenerateButton.setOnClickListener(v->{

        });
        return view;
    }
    private void loadEventDetails(){
        dbHelper.fetchEventById(eventId, new DatabaseHelper.EventsCallback() {
            @Override
            public void onEventsFetched(List<Event> events) {

            }

            @Override
            public void onEventFetched(Event fetchedEvent) {
                if(fetchedEvent != null){
                    event = fetchedEvent;
                    setUpQRCode();
                } else{
                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    private void setUpQRCode(){
        String eventId = String.valueOf(event.getEventId());
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
        Bitmap QRCode = qrCodeGenerator.generateQRCode(eventId);
        QRImage.setImageBitmap(QRCode);

    }
}
