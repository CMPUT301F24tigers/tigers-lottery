package com.example.tigers_lottery.HostedEvents;


import static android.view.View.GONE;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tigers_lottery.DatabaseHelper;
import com.example.tigers_lottery.R;
import com.example.tigers_lottery.models.Event;
import com.example.tigers_lottery.utils.QRCodeGenerator;

import java.util.List;
import java.util.Objects;


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
    private TextView title, description;
    private Button regenerateButton;

    /**
     * Required empty constructor
     */
    public OrganizerQRCodeFragment(){}

    /**
     * Creates a new instance of the organizerQRCodeFragment page and populates it with the event's id.
     * @param eventId event's qrCode to be seen.
     * @return a new instance of the QRCode fragment.
     */
    public static OrganizerQRCodeFragment newInstance(int eventId){
        OrganizerQRCodeFragment fragment = new OrganizerQRCodeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;

    }

    /**
     * Initializes the database helper and gets the event id of the event.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */

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
        title = view.findViewById(R.id.qrCodeText);
        description = view.findViewById(R.id.qrCodeDescription);
        QRImage.setVisibility(View.INVISIBLE);
        loadEventDetails();


        regenerateButton.setOnClickListener(v->{
            regenerateQRCode();
        });
        return view;
    }

    /**
     * Helper method to load the event's details specifically the qrCode, using the database helper.
     */
    private void loadEventDetails(){
        dbHelper.fetchEventById(eventId, new DatabaseHelper.EventsCallback() {
            /**
             * Required database helper method, unused.
             * @param events events.
             */
            @Override
            public void onEventsFetched(List<Event> events) {

            }

            /**
             * On finding the required event, populates the event name, if not find fragment is closed.
             * @param fetchedEvent whose details are to be displayed.
             */

            @Override
            public void onEventFetched(Event fetchedEvent) {
                if(fetchedEvent != null){
                    event = fetchedEvent;
                    String eventName = event.getEventName();
                    title.setText("QR Code\n" +  eventName);
                    setUpQRCode();
                } else{
                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            /**
             * Handles error on event finding.
             * @param e exception catcher.
             */

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * Creates the QRCode for the event using the QRCodeGenerator and displays different texts
     * depending on if the event has a QRCode or not.
     */
    private void setUpQRCode(){
        if(event.getQRCode() != null && !(event.getQRCode().isEmpty())) {
            QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(event);
            Bitmap QRCode = qrCodeGenerator.generateQRCodeFromHashData();
            QRImage.setVisibility(View.VISIBLE);
            QRImage.setImageBitmap(QRCode);

            description.setTextColor(0xFFFFFFFF);
            description.setText("Users may use this QR Code to join the waitlist for your event." +
                    " You may use the 'Regenerate QR Code' button to be given a new QR Code." +
                    " Please note that any previous QR Codes will be rendered unusable.");
            regenerateButton.setText("Regenerate QR Code");
        } else {
            description.setText("Your QR Code has been removed by an administrator for violating app policy. You may use the " +
                    "'Generate a QR Code' button to be given a new QR Code which users may use to join " +
                    "the waitlist for your event.");
            description.setTextColor(0xFFFF0000);
            QRImage.setVisibility(View.VISIBLE);
            regenerateButton.setText("Generate a QR Code");
        }
    }

    private void regenerateQRCode(){
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(event);
        qrCodeGenerator.setHashData();
        event.setQRCode(qrCodeGenerator.getHashData());
        updateEventInDatabase();
        setUpQRCode();
    }

    private void updateEventInDatabase() {
        dbHelper.updateEvent(event, new DatabaseHelper.EventsCallback() {
            /**
             * Navigates back to the dashboard and displays the organizer's events
             * @param events list of events from the organizer.
             */
            @Override
            public void onEventsFetched(List<Event> events) {
                Toast.makeText(getContext(), "You have generated a new QR Code!", Toast.LENGTH_SHORT).show();
            }

            /**
             * Unused in this fragment.
             * @param event single event.
             */

            @Override
            public void onEventFetched(Event event) {
            }

            /**
             * Handles error during event updating.
             * @param e exception catcher for event updating.
             */

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to update event", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
