package edu.stlawu.locationgps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import java.util.Observable;
import java.util.Observer;

import edu.stlawu.locationgps.MainFragment.OnFragmentInteractionListener;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    //private TextView tv_lat;
    //private TextView tv_lon;

    private Observable location;
    private final static int PERMISSION_REQUEST_CODE = 999;

    private boolean permissions_granted;
    private final static String LOGTAG =
            MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO get rid of these as they won't populate our xml
        //this.tv_lat = findViewById(R.id.tv_lat);
        //this.tv_lon = findViewById(R.id.tv_lon);

    }

    public boolean isPermissions_granted() {
        return permissions_granted;
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // we have only asked for FINE LOCATION
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.permissions_granted = true;
                Log.i(LOGTAG, "Fine location permission granted.");
            }
            else {
                this.permissions_granted = false;
                Log.i(LOGTAG, "Fine location permission denied.");
            }
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
