package calculatricefragmentv2.moi.android.fr.tp5_google_maps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, LocationListener {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private TextView currentLatText;
    private TextView currentLngText;
    private TextView markerLatText;
    private TextView markerLngText;

    private Marker currentMarker;
    private Marker clickedMarker;

    private LocationManager locationManager;
    private String provider;

    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private int USER_LOCATION_REQUESTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        currentLatText = (TextView) findViewById(R.id.currentLatText);
        currentLngText = (TextView) findViewById(R.id.currentLngText);
        markerLatText = (TextView) findViewById(R.id.markerLatText);
        markerLngText = (TextView) findViewById(R.id.markerLngText);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Test if we can access the location throught Wifi or 4G
        if(locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)){
            provider = locationManager.NETWORK_PROVIDER;
        } // Otherwise, use GPS even if its less precise
        else if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
            provider = locationManager.GPS_PROVIDER;
        }
        else{
            Toast toast = Toast.makeText(this,"Please activate either Cellular or Wifi", Toast.LENGTH_LONG);
            toast.show();
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {

            mapFragment.getMapAsync(this);
        }
        else // Si on ne les as pas, on les demande et ça appelle ensuite "onRequestPermissionsResult"
        {
            ActivityCompat.requestPermissions(this, permissions, USER_LOCATION_REQUESTCODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == USER_LOCATION_REQUESTCODE)
        {
            // Si on a bien reçu les permissions de l'utilisateur
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mapFragment.getMapAsync(this);

                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        findCurrentLocation();

    }

    public void findCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Location location = locationManager.getLastKnownLocation(provider);

            if(location != null) {
                int lat = (int) (location.getLatitude());
                int lng = (int) (location.getLongitude());
                // Remove the previous marker if there was any
                if(currentMarker != null)
                {
                    currentMarker.remove();
                }

                LatLng position = new LatLng(lat, lng);
                currentLatText.setText("Lat : " + String.valueOf(lat));
                currentLngText.setText("Lng : " + String.valueOf(lng));
                currentMarker = mMap.addMarker(new MarkerOptions().position(position).title("Ma position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            else
            {
                currentLatText.setText("Erreur");
                currentLngText.setText("Erreur");
            }
        }
        else // Si on ne les as pas, on les demande et ça appelle ensuite "onRequestPermissionsResult"
        {
            Toast toast = Toast.makeText(this, "Veuillez autoriser la localisation", Toast.LENGTH_LONG);
            toast.show();
        }
    }



    // Fonction automatiquement appelée lorsqu'il y a un changement de localisation
    @Override
    public void onLocationChanged(Location location) {
        findCurrentLocation();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(latLng != null) {

            if(clickedMarker != null)
            {
                clickedMarker.remove();
            }

            clickedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Marqueur cliqué").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            int lat = (int) (latLng.latitude);
            int lng = (int) (latLng.longitude);

            markerLatText.setText("Lat : " + String.valueOf(lat));
            markerLngText.setText("Lng : " + String.valueOf(lng));
        }
    }



    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        // Ce if semble obligatoire pour que requestLocationUpdates fonctionne, même s'il est bizarre
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
        }
        else // Si on ne les as pas, on les demande et ça appelle ensuite "onRequestPermissionsResult"
        {
            ActivityCompat.requestPermissions(this, permissions, USER_LOCATION_REQUESTCODE);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
        }
        locationManager.removeUpdates(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calculatrice) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("calculatricefragmentv2.moi.android.fr.tp4_fragments_calculatricev2");
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}
