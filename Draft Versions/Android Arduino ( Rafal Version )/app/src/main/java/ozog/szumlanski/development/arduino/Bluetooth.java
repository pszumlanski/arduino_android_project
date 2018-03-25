package ozog.szumlanski.development.arduino;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Bluetooth {

    Context c;

    private static final String TAG = "BluetoothConnection";
    private static final String appName = "Arduino";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("751ea30e-2e66-11e8-b467-0ed5f89f718b");

    private final BluetoothAdapter mBluetoothAdapter;
    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private BluetoothDevice mDevice;
    private UUID deviceUUID;
    ProgressDialog progressDialog;

    public Bluetooth(Context c) {

        this.c = c;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // This thread runs while listening... waiting for sth that will try to connect with our app.
    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket mServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
            } catch (IOException e) {
            }
            mServerSocket =tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            try {
                socket = mServerSocket.accept();
                Log.d("INFO", "Connection has been established!");
            }
            catch (IOException e){
                Log.d("INFO", "Connection cannot be established :(");
            }

            if (socket != null) {
                connected(socket, mDevice);
            }
        }

        public void cancel() {
            try {
                mServerSocket.close();
            }
            catch (IOException e){}
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d("INFO", "ConnectThread started.");
            mDevice = device;
            deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tem = null;

            try {
                Log.d("INFO", "Trying to create InsecureRFcommSocket using UUID..." + MY_UUID_INSECURE);
                tem = mDevice.createRfcommSocketToServiceRecord(deviceUUID);
            }
            catch (IOException e){
                Log.d("INFO", "Couldn't create Insecure RFcommSocket.. :(");
            }

            mSocket = tem;

            // Always clear that...
            mBluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
            }
            catch (IOException e){

                try {
                    mSocket.close();
                }
                catch (IOException e){
                }

            }

            connected(mSocket, mDevice);

        }

        public synchronized void start() {

            Log.d("INFO", "Start.");

            if (mConnectThread != null){
                //mConnectThread.cancel();
                mConnectThread = null;
            }
            if (mInsecureAcceptThread == null){
                mInsecureAcceptThread = new AcceptThread();
                mInsecureAcceptThread.start();
            }
        }

        public void startClient(BluetoothDevice device, UUID uuid) {

            progressDialog = ProgressDialog.show(c, "Connecting Bluetooth...", "Please wait...", true);
            mConnectThread = new ConnectThread(device, uuid);
            mConnectThread.start();
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mSocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        public ConnectedThread(BluetoothSocket socket) {

            mSocket = socket;
            InputStream inp = null;
            OutputStream out = null;

            progressDialog.dismiss();

            try {
                inp = mSocket.getInputStream();
                out = mSocket.getOutputStream();
            }
            catch (IOException e) {}

            mInputStream = inp;
            mOutputStream = out;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {

                try {
                    bytes = mInputStream.read(buffer);
                }
                catch (IOException e) {}


                String incomingMessage = new String(buffer, 0, bytes);
            }

        }

    }
}
