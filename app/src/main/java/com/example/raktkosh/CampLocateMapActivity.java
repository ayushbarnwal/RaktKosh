package com.example.raktkosh;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class CampLocateMapActivity extends AppCompatActivity {

    SupportMapFragment smp;
    private  GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camp_locate_map);

        String latitude = getIntent().getStringExtra("latitude").toString();
        String longitude = getIntent().getStringExtra("longitude").toString();
        String address = getIntent().getStringExtra("address").toString();

        smp = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);

                smp.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        mMap = googleMap;

                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(address);


                        googleMap.addMarker(markerOptions);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                    }
                });



    }
}