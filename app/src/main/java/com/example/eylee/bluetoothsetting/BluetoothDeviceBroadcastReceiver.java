package com.example.eylee.bluetoothsetting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class BluetoothDeviceBroadcastReceiver extends BroadcastReceiver {

    Context context;
    private String TAG = this.getClass().getSimpleName();

    public BluetoothDeviceBroadcastReceiver(){

    }
    public BluetoothDeviceBroadcastReceiver(Context context){
        this.context = context;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        BluetoothDevice _device= null;
        BluetoothClass bluetoothClass = null;
        int classId = 0;
        int deviceClassID = 0;

//        if(classId == BluetoothClass.Device.Major.)
//        switch (bluetoothClass.getMajorDeviceClass()) {
//            case BluetoothClass.Device.Major.PERIPHERAL:
//                switch (bluetoothClass.getDeviceClass() & 0x05C0) {
//                    case 0x0540: // Keyboard - 1314
//                        break;
//                    case 0x05C0: // Keyboard + mouse combo.
//                        break;
//                    case 0x0580: // Mouse - 1408
//                        break;
//                    default: // Other.
//                        break;
//                }
//        }
        Log.e(TAG, "BroadcastReceiver() action :: " + action);
        switch (action) {
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                Utils.toast(context, "ACTION_ACL_DISCONNECTED");
                int delPosition;
                if(BluetoothDeviceData.deviceConnHashMap.containsKey(device.getAddress())){
                    delPosition = BluetoothDeviceData.deviceConnHashMap.get(device.getAddress()).getPosition();
//                    Utils.toast(context, "receiver :: device - "+device.getAddress());
                    BluetoothDeviceData.deviceConnHashMap.remove(device.getAddress());

                    Message message = BluetoothDeviceData.bluetoothDeviceHandler.obtainMessage(BluetoothDeviceData.CONN_STATE_DISCONN);
                    Bundle bundle = new Bundle();
                    bundle.putInt(BluetoothDeviceData.MESSAGE_DEL_POS, delPosition);
                    message.setData(bundle);
                    BluetoothDeviceData.bluetoothDeviceHandler.sendMessage(message);

//                    BluetoothDeviceData.bluetoothDeviceHandler.obtainMessage(BluetoothDeviceData.CONN_STATE_DISCONN).sendToTarget();
                }


                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                Utils.toast(context, "ACTION_ACL_DISCONNECT_REQUESTED");
                BluetoothDeviceData.bluetoothDeviceHandler.obtainMessage(BluetoothDeviceData.CONN_STATE_DISCONN).sendToTarget();
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                Utils.toast(context, "ACTION_STATE_CHANGED");
                BluetoothDeviceData.bluetoothDeviceHandler.obtainMessage(BluetoothDeviceData.CONN_STATE_DISCONN).sendToTarget();
                break;
            case BluetoothDeviceData.ACTION_CONN_STATE:
                int state = intent.getIntExtra(BluetoothDeviceData.STATE, -1);
                String deviceId = intent.getStringExtra(BluetoothDeviceData.DEVICE_ID);
                String devicenm = intent.getStringExtra(BluetoothDeviceData.DEVICE_NAME);
                Log.e("MainActivity", "BroadcastReceiver() state :: " + String.valueOf(state));
                switch (state) {
                    case BluetoothDeviceData.CONN_STATE_DISCONNECT:
                        Utils.toast(context, "CONN_STATE_DISCONNECT");
                        if ("" == deviceId) {
                            // tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                        }
                        break;
                    case BluetoothDeviceData.CONN_STATE_CONNECTING:
                        Utils.toast(context, "CONN_STATE_CONNECTING");
                        // tvConnState.setText(getString(R.string.str_conn_state_connecting));
                        break;
                    case BluetoothDeviceData.CONN_STATE_CONNECTED:
                        Utils.toast(context, "CONN_STATE_CONNECTED Hello");
                        Log.e("MainActivity", "BroadcastReceiver() action CONN_STATE_CONNECTED!! :: " + action);
//                        BluetoothDeviceData.mConnectedArrayAdapter.add(devicenm+ "\n" + deviceId);


//                        BluetoothDeviceData.connectedListBaseAdapter.add(new ConnectedItem(null, devicenm+ "\n" + deviceId));
                        BluetoothDeviceData.connectedItems.add(new ConnectedItem(null, devicenm + "\n" + deviceId, deviceId));
                        BluetoothDeviceData.connectedListBaseAdapter.notifyDataSetChanged();
                        _device = BluetoothDeviceData.mBluetoothAdapter.getRemoteDevice(deviceId);
                        bluetoothClass = _device.getBluetoothClass();
                        classId = bluetoothClass.getMajorDeviceClass();

                        deviceClassID = bluetoothClass.getDeviceClass();
                        if(classId == BluetoothClass.Device.Major.IMAGING){
                            Utils.toast(context, "classId - " + String.valueOf(classId)+", classId - " + String.valueOf(deviceClassID));
                        }


                        break;
                    case BluetoothDeviceData.CONN_STATE_FAILED:
                        Utils.toast(context, "CONN_STATE_FAILED");
                        // ToastUtils.toast(context, "The connection failed!");
                        // tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                        break;
                    default:
                        break;
                }
                break;
            case BluetoothDeviceData.ACTION_QUERY_PRINTER_STATE:
                Log.e("MainActivity", "BroadcastReceiver() ACTION_QUERY_PRINTER_STATE :: " + action);
                if (BluetoothDeviceData.counts > 0) {// 인쇄 완료가 안 됐을 때
                    Log.e("MainActivity",
                            "BroadcastReceiver() counts :: " + String.valueOf(BluetoothDeviceData.counts) + ", 인쇄 완료가  안 됐을 때");
                    // to do
                    // sendContinuityPrint();
                } else {
                    Log.e("MainActivity",
                            "BroadcastReceiver() counts :: " + String.valueOf(BluetoothDeviceData.counts) + ", 인쇄완료!!!!");
                }
                break;
            case BluetoothDevice.ACTION_FOUND:
                // Log.e("receiver", "hello founded!!");
                // ToastUtils.toast(context, "founded!!");

//			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {// 찾아진 블루투스 디바이스가 페어링 되어 있지 않으면
                    Utils.toast(context,
                            "찾아진 블루투스 디바이스가 페어링 되어 있지 않으면 :: " + device.getName() + "\n" + device.getAddress());
                    // mNewDevicesArrayAdapter.add(device.getName()+ "\n"
                    // + device.getAddress());
//                    BluetoothDeviceData.mNewDevicesArrayAdapter.add(device.getName()+ "\n" + device.getAddress());
                    BluetoothDeviceData.newDeviceItems.add(new ConnectedItem(null, device.getName() , device.getAddress()));
                    BluetoothDeviceData.newDeviceListBaseAdapter.notifyDataSetChanged();
                } else {
                    Utils.toast(context, "device.getBondState() :: " + String.valueOf(device.getBondState()) + ", "
                            + device.getName() + "\n" + device.getAddress());


                }

                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                Utils.toast(context, "ACTION_DISCOVERY_FINISHED!!");
                break;

            case BluetoothDevice.ACTION_PAIRING_REQUEST:
                try {
                    Utils.toast(context, "ACTION_PAIRING_REQUEST");
                    /**
                    int pin=intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 1234);
                    //the pin in case you need to accept for an specific pin
                    Log.d(TAG, "Start Auto Pairing. PIN = " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY",1234));
                    byte[] pinBytes;
                    pinBytes = (""+pin).getBytes("UTF-8");
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        device.setPin(pinBytes);
                    }
                    //setPairing confirmation if neeeded
                    */

                } catch (Exception e) {
                    Log.e(TAG, "Error occurs when trying to auto pair");
                    e.printStackTrace();
                }
                break;

            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:


                Utils.toast(context, "ACTION_BOND_STATE_CHANGED :: "  + action + " "+device.getAddress() + " " + device.getName());
                if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                    Message message = BluetoothDeviceData.bluetoothDeviceHandler.obtainMessage(BluetoothDeviceData.CONN_STATE_PAIRED);
                    Bundle bundle = new Bundle();
                    bundle.putString(BluetoothDeviceData.DEVICE_ID, device.getAddress());
                    message.setData(bundle);
                    BluetoothDeviceData.bluetoothDeviceHandler.sendMessage(message);
                }
                break;
            default:
                break;
        }
    }
}
