package com.example.projectindoors.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Share_Activity extends AppCompatActivity {

    EditText txt_pNumber, txt_Rt_begn, txt_Rt_Dest;
    Button Send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        txt_Rt_begn = (EditText) findViewById(R.id.rt_beginning);
        txt_Rt_Dest = (EditText) findViewById(R.id.rt_destination);
        txt_pNumber = (EditText) findViewById(R.id.txt_phone_number);
        Send = findViewById(R.id.rt_Send);
    }

    public void btn_send(View view) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            RouteMessage();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);

        }
    }

    private void RouteMessage() {
        String phoneNumber = txt_pNumber.getText().toString().trim();
        String RouteBegin = txt_Rt_begn.getText().toString().trim();
        String RouteEnd = txt_Rt_Dest.getText().toString().trim();

        if (!txt_pNumber.getText().toString().equals("") || !txt_Rt_begn.getText().toString().equals("") ||
                !txt_Rt_Dest.getText().toString().equals("")) {


            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(phoneNumber, null, RouteBegin, null, null);
            smsManager.sendTextMessage(phoneNumber, null, RouteEnd, null, null);

            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please Enter Number and Routes", Toast.LENGTH_SHORT).show();
        }
    }


    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            switch (requestCode){


                case 0:

                    if (grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                        RouteMessage();

                    }

                    else {
                        Toast.makeText(this, "You don't have the Required Permission to make this Action", Toast.LENGTH_SHORT).show();
                    }

                    break;

            }


    }

    public void Close(View view) {
        Intent intent = new Intent(getApplicationContext(),MainActivities.class);
        intent.putExtra("EXIT",true);
        startActivity(intent);
        finish();


    }
}