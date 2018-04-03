package ozog.szumlanski.development.arduino_android_robot_mapping;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    TextView bluetoothstatus, bluetoothPaired;
    Button btnStartMapping;
    ListView availableDevicesList;

    BluetoothAdapter myBluetooth;
    boolean status;
    ArrayList<String> devicesList;
    ArrayList<BluetoothDevice> ListDevices;
    ArrayAdapter<String> adapter;
    OutputStream taOut;

    BluetoothDevice pairedBluetoothDevice = null;
    BluetoothSocket blsocket = null ;

    public static InputStream mmInputStream;
    public static volatile boolean stopWorker;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothstatus = (TextView) findViewById(R.id.bluetooth_state);
        bluetoothPaired = (TextView) findViewById(R.id.bluetooth_paired);
        btnStartMapping = (Button) findViewById(R.id.btnStartMapping);
        availableDevicesList = (ListView) findViewById(R.id.availableDevicesList);

        ListDevices = new ArrayList<>();
        devicesList = new ArrayList<>();

        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, R.id.txtlist, devicesList);
        availableDevicesList.setAdapter(adapter);

        btnStartMapping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MappingDisplay.class);
                startActivity(intent);
                finish();
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        availableDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "item with address: " + devicesList.get(i) + " clicked", Toast.LENGTH_LONG).show();
                pairDevices(ListDevices.get(i));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        status = myBluetooth.isEnabled();
        myBluetooth.startDiscovery();
        if (status)
        {
            bluetoothstatus.setText("Bluetooth State:   Enabled");
            registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
        else {
            bluetoothstatus.setText("Bluetooth State:   Not ready");
        }
    }


    void pairDevices(BluetoothDevice device)
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb") ;
        try {
            blsocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            blsocket.connect();

            try {
                mmInputStream = blsocket.getInputStream();
            }
            catch(Exception e) {}

            pairedBluetoothDevice = device;
            bluetoothPaired.setText("Bluetooth Paired with Device: "+device.getName());
            bluetoothPaired.setTextColor(getResources().getColor(R.color.green));

            btnStartMapping.setEnabled(true);

            Toast.makeText(getApplicationContext(), "Device paired successfully!",Toast.LENGTH_LONG).show();
        }
        catch(IOException ioe)
        {
            Log.e("taha>", "cannot connect to device :( " +ioe);
            Toast.makeText(getApplicationContext(), "Could not connect",Toast.LENGTH_LONG).show();
            pairedBluetoothDevice = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            Log.i("app>", "broadcast received") ;
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                devicesList.add(device.getName() + " @"+device.getAddress());
                ListDevices.add(device);

                adapter.notifyDataSetChanged();
            }
        }
    };

    public void startMapping() {

        Intent intent = new Intent(this, MappingDisplay.class);
        startActivity(intent);
        //finish();
    }
}



