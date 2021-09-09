package com.example.projectindoors.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class PermissionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_permission);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            this.getWindow().setNavigationBarColor(getResources().getColor(R.color.mapbox_blue));
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(this, MainActivities.class));
            finish();
            return;
        }

//         Permission Check Button
        findViewById(R.id.MapPermissionBtn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dexter.withActivity(PermissionActivity.this)
                                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse
                                                                            response) {

                                        startActivity(new Intent(PermissionActivity.this,
                                                MainActivities.class));
                                        finish();

                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse
                                                                           response) {
                                        if (response.isPermanentlyDenied()) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
                                            builder.setTitle("Permission Denied")
                                                    .setMessage("Permission to access device location is" +
                                                            " permanently denied. you need to go to setting to allow the permission.")
                                                    .setNegativeButton("Cancel", null)
                                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent();

                                                            intent.setData(Uri.parse(Settings.ACTION_APPLICATION_DETAILS_SETTINGS));
                                                            intent.setData(Uri.fromParts("package",
                                                                    getPackageName(),
                                                                    null));


                                                        }
                                                    }).show();

                                        } else {
                                            Toast.makeText(PermissionActivity.this,
                                                    "Permission Denied",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }

                                })
                                .check();
                    }
                });
    }
}