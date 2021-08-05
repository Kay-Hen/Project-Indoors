package com.example.projectindoors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;


import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    MapView mapView;
    public MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;

    public FloatingActionButton centerloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, "sk.eyJ1Ijoia3dhYm5hIiwiYSI6ImNrcnVneHViMjFhMWEyb21qcXJmajB0emYifQ.FBtzCPcg6RAvlnrLsIOpHw");
        setContentView(R.layout.activity_main);
        centerloc = (FloatingActionButton) findViewById(R.id.centerLoc);

        //Initiation of MapView
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        centerloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target
                        (new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude()))
                        .zoom(12)
                        .build();

                mapboxMap.setCameraPosition(cameraPosition);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            this.getWindow().setNavigationBarColor(Color.WHITE);

        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);

            }
        });

    }

    public void enableLocationComponent(@NonNull Style loadedMapStyle) {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent
                    (LocationComponentActivationOptions.builder(this,loadedMapStyle).build());

            //locationComponent.setLocationComponentEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationComponent.setLocationComponentEnabled(true);
             // Set the component's camera mode
             locationComponent.setCameraMode(CameraMode.TRACKING);

             locationComponent.setRenderMode(RenderMode.COMPASS);


         } else {
             permissionsManager = new PermissionsManager(this);
             permissionsManager.requestLocationPermissions(this);
         }
     }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }



}