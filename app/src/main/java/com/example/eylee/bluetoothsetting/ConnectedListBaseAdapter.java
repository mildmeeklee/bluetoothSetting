package com.example.eylee.bluetoothsetting;

import android.app.Activity;
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
import java.util.List;

public class ConnectedListBaseAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<ConnectedItem> mItems;
    LayoutInflater inflter;

    private String TAG = this.getClass().getSimpleName();

    ConnectedListBaseAdapter(Context mContext, ArrayList<ConnectedItem> connectedItemArrayList){
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
            convertView = inflter.inflate(R.layout.bluetooth_connected_device_item, null);
        }
        TextView tvNm = (TextView) convertView.findViewById(R.id.tvConnectedDeviceNm);
        TextView tvAddr = (TextView) convertView.findViewById(R.id.tvConnectedDeviceAddr);
        tvNm.setText(mItems.get(position).getDeviceNm());
        tvAddr.setText(mItems.get(position).getDeviceAddr());
        Button disconBtn = (Button) convertView.findViewById(R.id.discon_button);
        String address = tvAddr.getText().toString();
//        String address = info.substring(info.length() - 17);
        final  String _address = address;

        Log.i(TAG, "_address :: 클릭시 :: " + _address);
        disconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ToastUtils.toast(mContext,"hih disconn" + mItems.get(position).getDeviceNm());

                    ToastUtils.toast(mContext, "address :: 클릭시 :: " + _address);
                    disconnectDevice(_address, position);
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


    private  void disconnectDevice(String addr, int position){

        if(BluetoothDeviceData.connectedItems.size() == 0 ){
            ToastUtils.toast(mContext,"No Connected Device!!!!!!");
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
                ToastUtils.toast(mContext,"Disconnect device :: "+device);
                BluetoothDeviceData.bluetoothChatService.disconnected(device, position);
                return;
            }
        }
        if(!isContains){
            ToastUtils.toast(mContext,"Already disconnected!!!!!!!");
            return;
        }

    }
}
