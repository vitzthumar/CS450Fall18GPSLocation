package edu.stlawu.locationgps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class MainFragment extends Fragment implements Observer {


    private OnFragmentInteractionListener mListener;
    private TextView scrollableText = null;
    private LocationHandler handler = null;
    private final static int PERMISSION_REQUEST_CODE = 999;
    private Button startStopButton;
    private static final int EARTH_RADIUS = 6371;
    private int buttonState; // 0 means the button says "start", 1 means it says "stop"
    private double startLatitude;
    private double startLongitude;
    private double stopLatitude;
    private double stopLongitude;
    private Date startDate;

    public MainFragment() {
        // required empty public constructor
    }

    // onCreate gets called when the fragment is created
    // before the UI views are constructed
    // Initialize data needed for the fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (handler == null) {
            this.handler = new LocationHandler(getActivity());
            this.handler.addObserver(this);
        }

        // check permissions
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_REQUEST_CODE
            );
        }

        // set the instance variables
        buttonState = 0;
        startLatitude = 0;
        startLongitude = 0;
        stopLatitude = 0;
        stopLongitude = 0;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // make the scrollable text scrollable
        this.scrollableText = rootView.findViewById(R.id.scrollableText);
        scrollableText.setMovementMethod(new ScrollingMovementMethod());


        View aboutButton = rootView.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.about_title_text);
                builder.setMessage(R.string.about);
                builder.setPositiveButton(R.string.close,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                return;
                            }
                        });
                builder.show();
            }
        });

        // StartStop
        startStopButton = rootView.findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the state of the button
                if (buttonState == 0) {
                    // button says start - so we start and rememeber the location of the individual
                    handler.getLocation();
                    buttonState = 1;
                    startStopButton.setBackgroundColor(Color.RED);
                    startStopButton.setText("Stop");
                } else {
                    // button says stop - so we stop and do the appropriate calculations
                    handler.getLocation();
                    buttonState = 0;
                    startStopButton.setBackgroundColor(Color.GREEN);
                    startStopButton.setText("Start");
                }
            }
        });


        return rootView;
    }




    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(Observable observable,
                       Object o) {
        if (observable instanceof LocationHandler) {
            Location l = (Location) o;
            final double lat = l.getLatitude();
            final double lon = l.getLongitude();

            // Check which state the button is in
            if (buttonState == 0) {
                startLatitude = lat;
                startLongitude = lon;
                startDate = new Date();
            } else {
                stopLatitude = lat;
                stopLongitude = lon;
                long[] timeDifferences = Time.calculateTimeDifference(startDate, new Date());
                double dist = Haversine.distance(startLatitude, startLongitude, stopLatitude, stopLongitude);
                System.out.println("Difference in Seconds: " + timeDifferences[0]);
                System.out.println("Difference in Minutes: " + timeDifferences[1]);
                System.out.println("Difference in Hours: " + timeDifferences[2]);
                scrollableText.append("Distance Travelled: " + dist + " KM\n");
            }
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

