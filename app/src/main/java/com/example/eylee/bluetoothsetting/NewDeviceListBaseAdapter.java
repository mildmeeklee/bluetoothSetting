package com.example.eylee.bluetoothsetting;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class NewDeviceListBaseAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<ConnectedItem> mItems;
    LayoutInflater inflter;

    private String TAG = this.getClass().getSimpleName();

    NewDeviceListBaseAdapter(Context mContext, ArrayList<ConnectedItem> newDevicetemArrayList){
        this.mContext = mContext;
        this.mItems = newDevicetemArrayList;
        this.inflter = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return mItems.size();
    }


    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.bluetooth_new_device_item, null);
        }
        TextView tvNm = (TextView) convertView.findViewById(R.id.tvNewDeviceNm);
        TextView tvAddr = (TextView) convertView.findViewById(R.id.tvNewDeviceAddr);
        tvNm.setText(mItems.get(position).getDeviceNm());
        tvAddr.setText(mItems.get(position).getDeviceAddr());
        Button conBtn = (Button) convertView.findViewById(R.id.con_button);
        String address = tvAddr.getText().toString();
//        String address = info.substring(info.length() - 17);
        final  String _address = address;

        Log.i(TAG, "_address :: 클릭시 :: " + _address);
        conBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Utils.toast(mContext,"hih disconn" + mItems.get(position).getDeviceNm());

                    Utils.toast(mContext, "address :: 클릭시 :: " + _address);
                    paringDevice(_address, position);
                }catch (Exception e){
                    Log.e(TAG, e.toString());
                }

            }
        });
        return convertView;
        /*
        convertView = inflter.inflate(R.layout.bluetooth_connected_device_item, null);
        TextView tv = (TextView) convertView.findViewById(R.id.tvConnectedDeviceNm);
        tv.setText(mItems.get(position).getDeviceNm());
        return convertView;
        */



        /**
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.bluetooth_connected_device_item, null);
        }

        connectedItemView.setText(mItems.get(position).getDeviceNm());
        Button disconBtn = (Button) connectedItemView.findViewById(R.id.discon_button);
        disconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toast(mContext,"hih disconn" + mItems.get(position).getDeviceNm());
            }
        });
        return connectedItemView;
         */


    } //end of getView
    private final BroadcastReceiver mPairingRequestReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        device.createBond();
                    }
                    /**
                    int pin=intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 1234);
                    //the pin in case you need to accept for an specific pin
                    Log.d(TAG, "Start Auto Pairing. PIN = " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY",1234));
                    byte[] pinBytes;
                    pinBytes = (""+pin).getBytes("UTF-8");
//                    device.setPin(pinBytes);
                    //setPairing confirmation if neeeded
//                    device.setPairingConfirmation(true);
                     */
                } catch (Exception e) {
                    Log.e(TAG, "Error occurs when trying to auto pair");
                    e.printStackTrace();
                }
            }
        }
    };

    private  void paringDevice(String addr, int position){
//        http://allstuffon.blogspot.com/2015/03/how-to-programmatically-pair-bluetooth.html
//        https://www.programcreek.com/java-api-examples/?class=android.bluetooth.BluetoothDevice&method=ACTION_PAIRING_REQUEST
        Utils.toast(mContext,"paringDevice Device!!!!!!");
        BluetoothDevice device = BluetoothDeviceData.mBluetoothAdapter.getRemoteDevice(addr);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            device.createBond();
        }else{
            Utils.toast(mContext,"paringDevice Device!!!!!!");
        }

        /*
        Intent intent = new Intent(BluetoothDevice.ACTION_PAIRING_REQUEST);
        BluetoothDevice device = BluetoothDeviceData.mBluetoothAdapter.getRemoteDevice(addr);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        int PAIRING_VARIANT_PIN = 0;
        intent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        */
//        startActivityForResult(intent, BluetoothDeviceData.REQUEST_ENABLE_PAIR);
//        sendBroadcast(intent);
        /*
        BluetoothDevice device = BluetoothDeviceData.mBluetoothAdapter.getRemoteDevice(addr);
        // Actually set it in response to ACTION_PAIRING_REQUEST.
        Intent intent = new Intent(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        int PAIRING_VARIANT_PIN = 0;
        intent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
        mContext.sendBroadcast(intent);
        */
        /*
        if(BluetoothDeviceData.connectedItems.size() == 0 ){
            Utils.toast(mContext,"No Connected Device!!!!!!");
            return;
        }
        Boolean isContains =false;
        for(ConnectedItem item: BluetoothDeviceData.connectedItems){
            if(item.getDeviceAddr() == addr){
                isContains = true;
                // Get the device MAC address
                // Get the BluetoothDevice object
                BluetoothDevice device = BluetoothDeviceData.mBluetoothAdapter.getRemoteDevice(addr);
                // Attempt to connect to the device
                Utils.toast(mContext,"Disconnect device :: "+device);
                BluetoothDeviceData.bluetoothChatService.disconnected(device, position);
                return;
            }
        }
        if(!isContains){
            Utils.toast(mContext,"Already disconnected!!!!!!!");
            return;
        }
        */
    }
}
