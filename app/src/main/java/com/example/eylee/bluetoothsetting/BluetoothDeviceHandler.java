package com.example.eylee.bluetoothsetting;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class BluetoothDeviceHandler extends Handler {

    Context context;

    public BluetoothDeviceHandler(Context context){
        this.context = context;
    }
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case BluetoothDeviceData.CONN_STATE_DISCONN:

//                if(BluetoothDeviceData.mConnectedArrayAdapter != null && !BluetoothDeviceData.mConnectedArrayAdapter.isEmpty()){
//                    Bundle bundle = msg.getData();
//                    int delPos = bundle.getInt(BluetoothDeviceData.MESSAGE_DEL_POS);
//
//                    BluetoothDeviceData.mConnectedArrayAdapter.remove(BluetoothDeviceData.mConnectedArrayAdapter.getItem(delPos));
//                    BluetoothDeviceData.mConnectedArrayAdapter.notifyDataSetChanged();
//                }
//                Utils.toast(context, "hey CONN_STATE_DISCONN2");
                if(BluetoothDeviceData.connectedListBaseAdapter != null && !BluetoothDeviceData.connectedListBaseAdapter.isEmpty()){
                    Bundle bundle = msg.getData();
                    int delPos = bundle.getInt(BluetoothDeviceData.MESSAGE_DEL_POS);
                    ConnectedItem connectedItem = (ConnectedItem) BluetoothDeviceData.connectedListBaseAdapter.getItem(delPos);
                    String connectedAddr = connectedItem.getDeviceAddr();
                    int delConi=-1;
                    for(int conI = 0; conI < BluetoothDeviceData.connectedItems.size(); conI++){
                        if( (BluetoothDeviceData.connectedItems.get(conI).getDeviceAddr()).equals(connectedAddr)){
                            Utils.toast(context, "hey connectedItems.get(conI) " +connectedAddr);
                            delConi = conI;
                            break;
                        }
                    }
                    Utils.toast(context, "hey CONN_STATE_DISCONN2 delConi "+ String.valueOf(delConi));
                    if(delConi > -1){
//                        BluetoothDeviceData.connectedItems.remove(BluetoothDeviceData.connectedListBaseAdapter.getItem(delPos));
                        BluetoothDeviceData.connectedItems.remove(delConi);
                        BluetoothDeviceData.connectedListBaseAdapter.notifyDataSetChanged();
                    }

                }
                break;
            case BluetoothDeviceData.MESSAGE_CONNECTED:
                Utils.toast(context, "hey");
                break;
            case BluetoothDeviceData.PRINTER_COMMAND_ERROR:
                Utils.toast(context, "Please select the correct printer instructions");
                break;
            case BluetoothDeviceData.CONN_PRINTER:
                Utils.toast(context, "Please connect the printer first");
                break;
            case BluetoothDeviceData.CONN_STATE_PAIRED:
                Utils.toast(context, "CONN_STATE_PAIRED");
                break;
            default:
                break;
        }
    }


}
