package com.example.eylee.bluetoothsetting;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PairedListBaseAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<PairedItem> mItems;
    LayoutInflater inflter;

    private String TAG = this.getClass().getSimpleName();

    PairedListBaseAdapter(Context mContext, ArrayList<PairedItem> connectedItemArrayList){
        this.mContext = mContext;
        this.mItems = connectedItemArrayList;
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
            convertView = inflter.inflate(R.layout.bluetooth_paired_device_item, null);
        }
        TextView tvNm = (TextView) convertView.findViewById(R.id.tvPairedDeviceNm);
        TextView tvAddr = (TextView) convertView.findViewById(R.id.tvPairedDeviceAddr);
        tvNm.setText(mItems.get(position).getDeviceNm());
        tvAddr.setText(mItems.get(position).getDeviceAddr());
        Button conBtn = (Button) convertView.findViewById(R.id.con_paired_button);
        String address = tvAddr.getText().toString();
//        String address = info.substring(info.length() - 17);
        final  String _address = address;

        if(mItems.get(position).isConnected()){
            conBtn.setVisibility(View.INVISIBLE);
        }else{
            conBtn.setVisibility(View.VISIBLE);
        }
        Log.i(TAG, "_address :: 클릭시 :: " + _address);
        conBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                BluetoothDeviceData.mBluetoothAdapter.cancelDiscovery();
                // Get the device MAC address, which is the last 17 chars in the View
                String info = mItems.get(position).getDeviceNm();

                connectDevice(_address, false, position);

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


    private  void connectDevice(String addr, boolean secure, int position){

        if(BluetoothDeviceData.deviceConnHashMap.size() > 0 && BluetoothDeviceData.deviceConnHashMap.containsKey(addr)){
            if(BluetoothDeviceData.deviceConnHashMap.get(addr).isConnected()){
                Utils.toast(mContext,"Already connected!!!!!!!");
                return;
            }

        }

        // Get the device MAC address
        // Get the BluetoothDevice object
        BluetoothDevice device = BluetoothDeviceData.mBluetoothAdapter.getRemoteDevice(addr);

        // Attempt to connect to the device
        BluetoothDeviceData.bluetoothChatService.connect(device, secure, position);



    }
}
