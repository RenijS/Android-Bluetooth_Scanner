package com.example.lab5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int FINE_LOCATION_REQUEST_CODE = 8;
    private Spinner resultsSpinner;
    private Button scanBtn;
    private String TAG = "act";
    private BluetoothAdapter BA;
    private BroadcastReceiver BTrx;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultsSpinner = (Spinner) findViewById(R.id.resultSpinner);
        scanBtn = (Button) findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }

        });
        BA = BluetoothAdapter.getDefaultAdapter();
        //filter and register RX
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.device.action.FOUND");
        BTrx = new MyBluetoothReceiver();
        registerReceiver(BTrx, filter);

        ArrayList list = new ArrayList();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
        //specify the layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resultsSpinner.setAdapter(adapter);
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Permission not set, so requesting now");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Permission already granted");
            BA.startDiscovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                BA.startDiscovery();
                Log.d(TAG, "Permission granted initiating scan");
            } else {
                Log.d(TAG, "Permission denied");
            }
        }
    }

    public class MyBluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //This method is called when the BroadcastReceiver is receiving
            String action = intent.getAction();
            //finding devices
            Log.d(TAG, "h");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //add name and address to array
                adapter.add(device.getName() + "\n" + device.getAddress());
                Log.d(TAG, "Found" + device.getName());
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(BTrx);
    }
}