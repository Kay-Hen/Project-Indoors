package com.example.projectindoors.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ConnectionCheck extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        checkConnection();

        Intent intent = new Intent(ConnectionCheck.this,MainActivities.class);
        startActivity(intent);
        finish();

    }

    private void checkConnection() {
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionCheck.this);
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
}
