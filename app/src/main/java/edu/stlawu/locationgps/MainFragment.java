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
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class MainFragment extends Fragment implements Observer {


    private OnFragmentInteractionListener mListener;
    private TextView scrollableText = null;
    private TextView tv_lat;
    private TextView tv_lon;
    private LocationHandler handler = null;
    private final static int PERMISSION_REQUEST_CODE = 999;
    private Button startStopButton;
    private Button statsButton;
    private static final int EARTH_RADIUS = 6371;
    private int buttonState; // 0 means the button says "start", 1 means it says "stop"
    private int logCount = 0;
    private double startLatitude;
    private double startLongitude;
    private double stopLatitude;
    private double stopLongitude;
    private int[] timeTracker = new int[3];
    private float[] distanceTracker = new float[2];
    private Date startDate;
    private String elapsedTime = null;
    private String totalDistance = null;
    private String averageVelocity = null;
    private TextView currentVelocity = null;
    private String totalStats;

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
        elapsedTime = "Total Time: 0 Hrs, 0 Mins, 0 Secs";
        totalDistance = "Total Distance: 0 KM, 0 M";
        averageVelocity = "Average Velocity: 0 M/Sec";
        totalStats = "%s\n\n%s\n\n%s\n";
        totalStats = String.format(totalStats, elapsedTime, totalDistance, averageVelocity);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // decimal rounding format (to 3 decimal places)
        final DecimalFormat decimalFormat = new DecimalFormat("#.###");

        // make the scrollable text scrollable
        this.scrollableText = rootView.findViewById(R.id.scrollableText);
        scrollableText.setMovementMethod(new ScrollingMovementMethod());

        tv_lat = rootView.findViewById(R.id.tv_lat);
        tv_lon = rootView.findViewById(R.id.tv_lon);
        // get the horizontal text views IDs
        currentVelocity = rootView.findViewById(R.id.currentVelocity);

        Button statsButton = rootView.findViewById(R.id.stats_button);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setMessage(totalStats);
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
        // set the startStop to its initial START state
        buttonState = 0;
        startStopButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.buttonStrat));
        startStopButton.setText("Start");
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the state of the button
                if (buttonState == 0) {
                    // button says start - so we start and rememeber the location of the individual
                    Location l = handler.getLocation();
                    buttonState = 1;
                    startStopButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.buttonStop));
                    startStopButton.setText("Stop");
                    startLatitude = l.getLatitude();
                    startLongitude = l.getLongitude();
                    startDate = new Date();
                } else {
                    // increment the logCount
                    logCount++;
                    // button says stop - so we stop and do the appropriate calculations
                    Location l = handler.getLocation();
                    buttonState = 0;
                    startStopButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.buttonStrat));
                    startStopButton.setText("Start");
                    stopLatitude = l.getLatitude();
                    stopLongitude = l.getLongitude();
                    long[] timeDifferences = Time.calculateTimeDifference(startDate, new Date());
                    double dist = Haversine.distance(startLatitude, startLongitude, stopLatitude, stopLongitude);
                    System.out.println("Difference in Seconds: " + timeDifferences[0]);
                    System.out.println("Difference in Minutes: " + timeDifferences[1]);
                    System.out.println("Difference in Hours: " + timeDifferences[2]);

                    // only append if over 1 kilometer was traveled
                    if (dist >= 1) {
                        scrollableText.append(logCount + ": Distance traveled: " + decimalFormat.format(dist) + " KM\n");
                    }

                    // Test the in build API
                    Location locA = new Location("start");
                    locA.setLatitude(startLatitude);
                    locA.setLongitude(startLongitude);

                    Location locB = new Location("end");
                    locB.setLatitude(stopLatitude);
                    locB.setLongitude(stopLongitude);
                    float distanceInMeters = locA.distanceTo(locB);
                    // only append if under 1 kilometer was traveled
                    if (distanceInMeters < 1000) {
                        scrollableText.append(logCount + ": Distance traveled: " + decimalFormat.format(distanceInMeters) + " M\n");
                    }
                    // was it over 2 hours?
                    if (timeDifferences[2] >= 2) {
                        scrollableText.append("    Time spent traveling: " + timeDifferences[2] + " hours and " + timeDifferences[1] + " minutes\n");
                    } else {
                        // was it over 2 minutes?
                        if (timeDifferences[1] >= 2) {
                            scrollableText.append("    Time spent traveling: " + timeDifferences[1] + " minutes and "  + timeDifferences[0] + " seconds\n");
                        } else {
                            // under 2 minutes
                            scrollableText.append("    Time spent traveling: " + timeDifferences[0] + " seconds\n");
                        }
                    }
                    scrollableText.append("------------------------\n");

                    /* UPDATE THE HORIZONTAL SCROLLVIEW TEXT VIEWS */
                    // add the seconds, minutes, and hours to the time tracker
                    timeTracker[0] += timeDifferences[0];
                    timeTracker[1] += timeDifferences[1];
                    timeTracker[2] += timeDifferences[2];
                    // find the remainder and modulo for min and hr
                    timeTracker[1] += timeTracker[0] / 60;
                    timeTracker[0] %= 60;
                    timeTracker[2] += timeTracker[1] / 60;
                    timeTracker[1] %= 60;
                    // display the time
                    elapsedTime = "Total Time: " + timeTracker[2] + " Hrs, " + timeTracker[1] + " Mins, " + timeTracker[0] + " Secs";


                    // add the km and m to the distance tracker
                    distanceTracker[0] += distanceInMeters;
                    // find the remainder and module for the km
                    distanceTracker[1] += distanceTracker[0] / 1000;
                    distanceTracker[0] %= 1000;
                    totalDistance = "Total Distance: " + (int)distanceTracker[1] + " KM, " + decimalFormat.format(distanceTracker[0]) + " M";


                    // add the average velocity
                    float totalTrackedSeconds = timeTracker[0] + (timeTracker[1] * 60) + (timeTracker[2] * 3600);
                    float totalTrackedDistance = distanceTracker[0] + ((int)distanceTracker[1] * 1000);
                    averageVelocity = "Average Velocity: " + decimalFormat.format(totalTrackedDistance / totalTrackedSeconds) + " M/Sec";
                    totalStats = "%s\n\n%s\n\n%s\n";
                    totalStats = String.format(totalStats, elapsedTime, totalDistance, averageVelocity);
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

            tv_lat.setText("Latitude: " + Double.toString(lat));
            tv_lon.setText("Longitude: " + Double.toString(lon));

            Location currentLocation = handler.getLocation();
            String currentVelocityString = "Current Velocity: " + Float.toString(currentLocation.getSpeed()) + " M/Sec";
            currentVelocity.setText(currentVelocityString);
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

