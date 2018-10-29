package com.example.mikita.ppo_lab;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;


public class ImeiVersionActivity extends AppCompatActivity {

    static final int REQUEST_READ_PHONE_STATE_PERMISSION = 345;
    TextView imeiView;
    TextView versionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imei_version);

        imeiView = findViewById(R.id.imei_version__imeiTextView);
        versionView = findViewById(R.id.imei_version__versionTextView);

        versionView.setText(BuildConfig.VERSION_NAME);
        getPhoneImei();
    }


    private void getPhoneImei()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage("The app needs a permission to access your phone functions," +
                        "because the app has to show you your device IMEI");
                dialogBuilder.setTitle("Hey!");
                dialogBuilder.setCancelable(false);
                final Activity thisActivity = this;
                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(thisActivity,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                REQUEST_READ_PHONE_STATE_PERMISSION);
                    }
                });

                dialogBuilder.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_READ_PHONE_STATE_PERMISSION);
            }

        } else {
            showImei();
        }

    }

    private void showImei()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)
            return;
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        imeiView.setText(imei);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImei();
                } else {
                    imeiView.setText("NO PERMISSION -> NO IMEI");
                }
                return;
            }
        }
    }
}
