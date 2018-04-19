package com.example.eylee.bluetoothsetting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;

public class BluetoothChatService {

    // Debugging
    private String TAG = this.getClass().getSimpleName();

    Context mContext;
    // Member fields
    private final BluetoothAdapter mAdapter;
    private final BluetoothDeviceHandler mHandler;
    private int mState;
    private int mNewState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 1000;       // we're doing nothing
    public static final int STATE_LISTEN = 1001;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 1002; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 1003;  // now connected to a remote device


    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE  = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Member fields
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    public BluetoothChatService(Context context, BluetoothDeviceHandler handler){
        this.mContext = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
        mHandler = handler;
    }


    private static final int MAX_THREADS = 10;



    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    //start accept
    public synchronized  void start(){

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    /*
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        // Update UI title
//        updateUserInterfaceTitle();
    }

    public synchronized  void disconnected(BluetoothDevice device){
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
//         Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }
    */

    public synchronized void connect(BluetoothDevice device, boolean secure) {
       if(BluetoothDeviceData.deviceConnHashMap.containsKey(device.getAddress())){
           return;
       }
       BluetoothDeviceData.deviceConnHashMap.put(device.getAddress(), new ConnectionThread(device, secure));
       BluetoothDeviceData.deviceConnHashMap.get(device.getAddress()).start();

        // Update UI title
//        updateUserInterfaceTitle();
    }
    public synchronized void connect(BluetoothDevice device, boolean secure, int position){
        if(BluetoothDeviceData.deviceConnHashMap.containsKey(device.getAddress())){
            return;
        }
        if(BluetoothDeviceData.deviceConnHashMap.size() > 0 && BluetoothDeviceData.deviceConnHashMap.containsKey(device)){
            Utils.toast(mContext, "Already Connected222!!");
            return;
        }
        //여기서 무조건 insert 하는 구문으로 페어링 돼 있지만, 꺼져있는 기기에 대한 고려 없이 (성공 여부에 관계없이) insert
        //connected 부분에 더블체크해야함(service 에서)
        BluetoothDeviceData.deviceConnHashMap.put(device.getAddress(), new ConnectionThread(device, secure));
        BluetoothDeviceData.deviceConnHashMap.get(device.getAddress()).setPaired_pos(position);
        BluetoothDeviceData.deviceConnHashMap.get(device.getAddress()).start();

        // Update UI title
//        updateUserInterfaceTitle();
    }
    public synchronized  void disconnected(BluetoothDevice device, int position){
        if(!BluetoothDeviceData.deviceConnHashMap.containsKey(device.getAddress())){
            return;
        }
//        Utils.toast(mContext, "service disconnect :: " + device.getAddress());
        BluetoothDeviceData.deviceConnHashMap.get(device.getAddress()).setPosition(position);
        BluetoothDeviceData.deviceConnHashMap.get(device.getAddress()).cancel();
    }
    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }


        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
//        Message msg = mHandler.obtainMessage(BluetoothDeviceData.MESSAGE_DEVICE_NAME);
//        Message msg = mHandler.obtainMessage(BluetoothDeviceData.MESSAGE_CONNECTED);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothDeviceData.DEVICE_NAME, device.getName());
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
        // Update UI title
//        updateUserInterfaceTitle();


//        Message msg = mHandler.obtainMessage();
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothDeviceData.EXTRA_DEVICE_ADDRESS, device.getAddress());
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
        sendStateBroadcast(BluetoothDeviceData.CONN_STATE_CONNECTED, device.getAddress(), device.getName());

    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothDeviceData.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothDeviceData.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = STATE_NONE;
        // Update UI title
//        updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }


    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothDeviceData.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothDeviceData.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = STATE_NONE;
        // Update UI title
//        updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }
    private void sendStateBroadcast(int state, String id, String nm) {
        Intent intent = new Intent(BluetoothDeviceData.ACTION_CONN_STATE);
        intent.putExtra(BluetoothDeviceData.STATE, state);
        intent.putExtra(BluetoothDeviceData.DEVICE_ID, id);
        intent.putExtra(BluetoothDeviceData.DEVICE_NAME, nm);
        mContext.sendBroadcast(intent);
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Create a new listening server socket
            try {
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                            NAME_INSECURE, MY_UUID_INSECURE);

            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
            mState = STATE_LISTEN;
        }

        public void run() {
            Log.d(TAG, "Socket Type: " + mSocketType +
                    "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(),
                                        mSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

        }

        public void cancel() {
            Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }


    public class ConnectionThread extends  Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        int position;
        int paired_pos;
        boolean isConnected;
        boolean dblChkConnected;


        public ConnectionThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";
            mState = STATE_CONNECTED;
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(
                        MY_UUID_INSECURE);
                if(BluetoothDeviceData.deviceConnHashMap.containsKey(device.getAddress())){
                    BluetoothDeviceData.deviceConnHashMap.get(device.getAddress()).setDblChkConnected(true);
                }
            } catch (IOException e) {
                BluetoothDeviceData.deviceConnHashMap.get(device.getAddress()).setDblChkConnected(false);
                if(BluetoothDeviceData.deviceConnHashMap.containsKey(device.getAddress())){
                    BluetoothDeviceData.deviceConnHashMap.remove(device.getAddress());
                }
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;



        }

        public boolean isDblChkConnected() {
            return dblChkConnected;
        }

        public void setDblChkConnected(boolean dblChkConnected) {
            this.dblChkConnected = dblChkConnected;
        }

        public boolean isConnected() {
            return isConnected;
        }

        public void setConnected(boolean connected) {
            isConnected = connected;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getPaired_pos() {
            return paired_pos;
        }

        public void setPaired_pos(int paired_pos) {
            this.paired_pos = paired_pos;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }
            goToBroadcast(BluetoothDeviceData.CONN_STATE_CONNECTED, mmDevice);

        }


        public void cancel() {
            try {
                mmSocket.close();
//                BluetoothDeviceData.deviceConnHashMap.remove(mmDevice.getAddress());
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    public  synchronized  void goToBroadcast(int state, BluetoothDevice device){
        sendStateBroadcast(state, device.getAddress(), device.getName());
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);

            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }


    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(BluetoothDeviceData.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(BluetoothDeviceData.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

}
