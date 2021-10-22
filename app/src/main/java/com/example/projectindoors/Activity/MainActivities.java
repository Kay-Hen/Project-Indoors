package com.example.projectindoors.Activity;


import static android.content.ContentValues.TAG;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivities extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {

    MapView mapView;
    MapboxMap mapboxMap;
    PermissionsManager permissionsManager;
    LocationComponent locationComponent;
    public FloatingActionButton centerloc;
    DirectionsRoute currentRoute;
    NavigationMapRoute navigationMapRoute;
    Button button;
    private FloatingActionButton fab_location_search;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private String geoJsonSourceLayerId = "GeoJsonSourceLayerId";
    private String symbolIconId = "SymbolIconId";
    private int status=0;
    private Point originalPoint, destinationPoint;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         Connectioncheck();

        //getSupportActionBar().hide();

        Mapbox.getInstance(this, "sk.eyJ1Ijoia3dhYm5hIiwiYSI6ImNrdGFkbTJmcDFrdGsydmxhMGNydGxyZW0ifQ.KLmz4uP8KRHQiVXbKMNWfg");
        setContentView(R.layout.activity_main);
        centerloc = (FloatingActionButton) findViewById(R.id.centerLoc);

        //Initiation of MapView
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }

    private void Connectioncheck() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null != activeNetwork) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

                Toast.makeText(this, "wifi Enabled", Toast.LENGTH_SHORT).show();
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                Toast.makeText(this, "Data Network Enabled", Toast.LENGTH_SHORT).show();
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivities.this);
            builder.setMessage("Please connect to the internet to use Map")
                    .setCancelable(false)
                    .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            System.exit(0);

                        }
                    });


        }
    }

    public void onClick(View view) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                        locationComponent.getLastKnownLocation().getLongitude()))
                .zoom(16)
                .build();
        //    mapboxMap.setCameraPosition(cameraPosition);
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),1000);
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
       // this.mapboxMap.setMinZoomPreference(6);
        mapboxMap.setStyle(new Style.Builder()
                        .fromUri("mapbox://styles/kwabna/ckthnzkea0a1h18l5r7ohbjr8"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                        addDestinationIconSymbolLayer(style);

                        mapboxMap.addOnMapClickListener(MainActivities.this);
                        button = findViewById(R.id.Button);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try
                                {
                                    if (status !=1){
                                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                                .directionsRoute(currentRoute)
                                                .shouldSimulateRoute(true)
                                                .build();
                                        NavigationLauncher.startNavigation(MainActivities.this, options);
                                    }else if (status ==1){
                                        status = 0;
                                        getRoute(originalPoint, destinationPoint);
                                    }

                                }catch(Exception e)
                                {
                                    //do something when an error is detected....
                                    new AlertDialog.Builder(MainActivities.this)
                                            .setTitle("No internet connection")
                                            .setMessage("Your require internet to enable navigation")
                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();

                                                }
                                            });

                                }




                                //boolean simulateRoute = true;

                            }



                        });

                        initSearchfab();
                        setUpSource(style);
                        setUpLayer(style);
                        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_location_on_purple, null);
                        Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                        style.addImage(symbolIconId, bitmap);

                    }
                });

    }


    private void setUpLayer(Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geoJsonSourceLayerId).withProperties(iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})));
    }

    private void setUpSource(Style loadedMapStyle)
    {
        loadedMapStyle.addSource(new GeoJsonSource(geoJsonSourceLayerId));
    }

    private void initSearchfab()
    {
        fab_location_search = findViewById(R.id.fab_location_search);
        fab_location_search.setOnClickListener(view -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.access_token))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(5)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        });
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE){
            /*Retrieve selected location's CarmenFeature*/
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            /*Create a new FeatureCollection and add a new feature to it using selectedCarmenFeature above.
             *then retrieve and update the source designated for showing a selected location's symbol layer icon*/
            if (mapboxMap != null){
                Style style = mapboxMap.getStyle();
                if (style != null){
                    GeoJsonSource source = style.getSourceAs(geoJsonSourceLayerId);
                    if (source != null){
                        source.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }
                    /*Move map camera to the selected location*/
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                    ((Point) selectedCarmenFeature.geometry()).longitude())).zoom(24)
                            .build()),4000);

                    }

                }

            }

        }





    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
                //iconOffset(new Float[] {0f, -9f})

        );

        loadedMapStyle.addLayer(destinationSymbolLayer);
    }


    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        double latitude = point.getLatitude();
        double longitude = point.getLongitude();
       Toast.makeText(MainActivities.this, "latitude: "+latitude+", longitude: "+longitude, Toast.LENGTH_SHORT).show();

        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel);

        if (features.size()>0)
        {
            Feature feature = features.get(0);
            if (feature.properties()!=null)
            {

                JSONObject locationData = new JSONObject();
                for (Map.Entry<String, JsonElement> entry: feature.properties().entrySet()){
                    try {
                        locationData.put(entry.getKey(),entry.getValue().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                try {
                    String location_name = locationData.getString("name");
                    String description = locationData.getString("description");
                    Toast.makeText(MainActivities.this, "location_name: "+location_name+", description: "+description, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, String.format("%s = %s ", location_name, description));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
         Point originalPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null)
        {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

       getRoute(originalPoint, destinationPoint);
        button.setEnabled(true);
        //button.setBackgroundResource(R.color.mapbox_blue);
        return true;
    }

    private void getRoute(Point originalPoint, Point destinationPoint)
    {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(originalPoint)
                .destination(destinationPoint)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call,
                                           Response<DirectionsResponse> response) {
                        Log.d(TAG, "Response code: "+response.code());

                        if (response.body() ==null) {
                            Log.d(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        }
                            else if (response.body().routes().size() < 1)
                            {
                                Log.e(TAG, "No routes found");
                                return;
                            }

                            currentRoute = response.body().routes().get(0);

                            //Drawing route on map
                            if (navigationMapRoute != null)
                            {
                                navigationMapRoute.removeRoute();
                            }
                            else
                                {
                                    navigationMapRoute = new NavigationMapRoute(null,mapView,mapboxMap,
                                            R.style.NavigationMapRoute);

                            }
                            navigationMapRoute.addRoute(currentRoute);

                        }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                      Toast.makeText(MainActivities.this,"error"+t.toString(),Toast.LENGTH_SHORT).show();

                    }
                });


    }

    @SuppressWarnings({"MissingPermission"})
    public void enableLocationComponent(@NonNull Style loadedMapStyle) {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            //Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            //activate with options
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this,
                    loadedMapStyle).build());

            //Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            //set the component camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);



            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED){


                return;
            }

        }
        else
        {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
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
    @SuppressWarnings( {"MissingPermission"})
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        inflater.inflate(R.menu.share_menu,menu);
        return true;

    }



    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Share:
                Intent intent = new Intent(MainActivities.this,Share_Activity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_street:
                mapboxMap.setStyle(Style.MAPBOX_STREETS);
                return true;
            case R.id.menu_outdoors:
                mapboxMap.setStyle(Style.OUTDOORS);
                return true;
            case R.id.menu_light:
                mapboxMap.setStyle(Style.LIGHT);
                return true;
            case R.id.menu_dark:
                mapboxMap.setStyle(Style.DARK);
                return true;
            case R.id.menu_satellite:
                mapboxMap.setStyle(Style.SATELLITE);
                return true;
            case R.id.menu_traffic_day:
                mapboxMap.setStyle(Style.TRAFFIC_DAY);
                return true;
            case R.id.menu_traffic_night:
                mapboxMap.setStyle(Style.TRAFFIC_NIGHT);
                return true;
            case R.id.menu_indoor:
             //   mapboxMap.setStyle((R.id.mapView);
             //   mapboxMap.addOnMapClickListener(MainActivities.this);
              //  setContentView(R.raw.style);
                mapView = (MapView) findViewById(R.id.mapView);
                mapView.getMapAsync(this);
                return true;


            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}