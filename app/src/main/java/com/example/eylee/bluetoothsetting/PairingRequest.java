package com.example.eylee.bluetoothsetting;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PairingRequest extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        if (intent.getAction().equals("ACTION_PAIRING_REQUEST")) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            byte[] pinBytes = BluetoothDevice.convertPinToBytes("1234");
//            device.setPin(pinBytes);
            try {
                byte[] pinBytes;
                int pin=intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 1234);
                pinBytes = (""+pin).getBytes("UTF-8");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    final boolean b = device.setPin(pinBytes);
                }
                //setPairing confirmation if neeeded
//                final boolean b = device.setPairingConfirmation(true);
            }catch (Exception e){

            }

        }
    }
}