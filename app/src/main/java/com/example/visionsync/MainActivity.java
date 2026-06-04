package com.example.visionsync;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static BluetoothSocket bluetoothSocket;

    public static OutputStream outputStream;

    TextView timeText;

    Button btnConnect;

    BluetoothAdapter bluetoothAdapter;

    Handler handler = new Handler();

    // HC-05 MAC ADDRESS
    String address = "00:23:00:01:6D:EA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        timeText =
                findViewById(R.id.timeText);

        btnConnect =
                findViewById(R.id.btnConnect);

        bluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter();

        // PERMISSIONS
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.S) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{

                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CONTACTS

                    },
                    1
            );
        }

        // NOTIFICATION ACCESS
        startActivity(
                new Intent(
                        Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                )
        );

        // CLOCK START
        startClock();

        // CONNECT BUTTON
        btnConnect.setOnClickListener(
                v -> connectBluetooth());
    }

    // CLOCK
    private void startClock() {

        handler.post(new Runnable() {

            @Override
            public void run() {

                String time =
                        new SimpleDateFormat(
                                "hh:mm",
                                Locale.getDefault()
                        ).format(new Date());

                timeText.setText(time);

                sendData("TIME " + time);

                handler.postDelayed(this,1000);
            }
        });
    }

    // BLUETOOTH CONNECT
    private void connectBluetooth() {

        try {

            if (Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.S) {

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
            }

            BluetoothDevice device =
                    bluetoothAdapter.getRemoteDevice(
                            address);

            bluetoothSocket =
                    device.createRfcommSocketToServiceRecord(
                            UUID.fromString(
                                    "00001101-0000-1000-8000-00805F9B34FB"
                            )
                    );

            bluetoothAdapter.cancelDiscovery();

            bluetoothSocket.connect();

            outputStream =
                    bluetoothSocket.getOutputStream();

            Toast.makeText(
                    this,
                    "Bluetooth Connected",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Connection Failed",
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }

    // SEND DATA TO ARDUINO
    public static void sendData(String text) {

        try {

            if(outputStream != null) {

                outputStream.write(
                        (text + "\n").getBytes()
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}