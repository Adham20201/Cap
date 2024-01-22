package com.leo.h19305;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothConnectionActivity extends AppCompatActivity {

    private MediaPlayer mp;
    public int flag = 0;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    private String key_2 = "Adham's project";

    FloatingActionButton goProfile;

    private static final int PERMISSION_CODE = 5;

    Button btnSend, btnDiscover;
    TextView status;

    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mmSocket;

    public static CreateConnectThread createConnectThread;
    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status

    static boolean faceDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        btnSend = findViewById(R.id.btnSend);
        btnDiscover = findViewById(R.id.btnFindUnpairedDevices);
        status = findViewById(R.id.status);
        goProfile = findViewById(R.id.goToProfile);

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-message".
        LocalBroadcastManager.getInstance(BluetoothConnectionActivity.this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BluetoothConnectionActivity.this, VoiceActivity.class);
                i.putExtra(key_2, DateFormat.getDateTimeInstance().format(new Date()));
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bluetooth Setup
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                // Get List of Paired Bluetooth Device
                if (ActivityCompat.checkSelfPermission(BluetoothConnectionActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestBlePermissions(BluetoothConnectionActivity.this, PERMISSION_CODE);
                }
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                List<Object> deviceList = new ArrayList<>();
                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        DeviceInfoModel deviceInfoModel = new DeviceInfoModel(deviceName, deviceHardwareAddress);
                        deviceList.add(deviceInfoModel);
                    }
                    // Display paired device using recyclerView
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewDevice);
                    recyclerView.setLayoutManager(new LinearLayoutManager(BluetoothConnectionActivity.this));
                    DeviceListAdapter deviceListAdapter = new DeviceListAdapter(BluetoothConnectionActivity.this, deviceList);
                    recyclerView.setAdapter(deviceListAdapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                } else {
                    View btnView = findViewById(R.id.recyclerViewDevice);
                    Snackbar snackbar = Snackbar.make(btnView, "Activate Bluetooth or pair a Bluetooth device", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                    snackbar.show();
                }
            }
        });

        goProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothConnectionActivity.this, VoiceActivity.class);
                startActivity(intent);
            }
        });


        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONNECTING_STATUS:
                        switch (msg.arg1) {
                            case 1:
                                status.setText("Connected");
                                break;
                            case -1:
                                status.setText("Connection Failed");
                                break;
                        }
                        break;
                }
            }
        };
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            deviceName = intent.getStringExtra("deviceName");
            deviceAddress = intent.getStringExtra("deviceAddress");

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceAddress, BluetoothConnectionActivity.this, BluetoothConnectionActivity.this);
            createConnectThread.start();
        }
    };


    private static final String[] BLE_PERMISSIONS = new String[]{
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
    };

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            try {
                ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            try {
                ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);

            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        Context context;
        Activity activity;

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address, Context context, Activity activity) {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket temporary = null;
            this.activity = activity;
            this.context = context;
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestBlePermissions(activity, PERMISSION_CODE);
            }
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                temporary = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "(Error) Failed", e);
            }
            mmSocket = temporary;
        }

        public void run() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestBlePermissions(activity, PERMISSION_CODE);
            }
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {

                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */

    /*
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
     */
}