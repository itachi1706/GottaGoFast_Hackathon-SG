package com.itachi1706.hackathonsg.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.gson.Gson;
import com.itachi1706.hackathonsg.Database.ProductDB;
import com.itachi1706.hackathonsg.Objects.JSONGeneralStoredProducts;
import com.itachi1706.hackathonsg.Objects.JSONProducts;
import com.itachi1706.hackathonsg.Objects.JSONStoredProducts;
import com.itachi1706.hackathonsg.reference.PebbleEnums;
import com.itachi1706.hackathonsg.reference.ProductStorage;
import com.itachi1706.hackathonsg.reference.StaticReferences;

import java.util.ArrayList;

/**
 * Created by Kenneth on 26/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.Services
 */
public class PebbleComms extends Service {

    PebbleKit.PebbleDataReceiver mReceiver;
    PebbleKit.PebbleNackReceiver mNack;
    PebbleKit.PebbleAckReceiver mAck;
    boolean isProcessing = false;
    boolean hasMarked = false;

    private Looper serviceLooper;
    private ServiceHandler mServiceHandler;

    SharedPreferences sp;

    public PebbleComms() {}


    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("Pebble Comm", "SYSTEM: Started Pebble Communications Service");
        Log.i("Pebble Comm", "UUID: " + StaticReferences.PEBBLE_APP_UUID.toString());

        unregisterPebbleReceivers();
        Log.i("Pebble Comm", "Unregistered. Now reregistering receivers");


        mNack = new PebbleKit.PebbleNackReceiver(StaticReferences.PEBBLE_APP_UUID) {
            @Override
            public void receiveNack(Context context, int i) {
                Log.e("Pebble Comm", "Message failed to send to Pebble");

                //Check variables for any dictionaries not sent, and send them
                if (StaticReferences.dict1 != null) {
                    PebbleKit.sendDataToPebble(getApplicationContext(), StaticReferences.PEBBLE_APP_UUID, StaticReferences.dict1);
                    StaticReferences.extraSend = 1;
                } else if (StaticReferences.dict2 != null){
                    PebbleKit.sendDataToPebble(getApplicationContext(), StaticReferences.PEBBLE_APP_UUID, StaticReferences.dict2);
                    StaticReferences.extraSend = 2;
                } 
            }
        };
        PebbleKit.registerReceivedNackHandler(getApplicationContext(), mNack);
        Log.i("Pebble Comm", "Registered Nack Reciver");

        mAck = new PebbleKit.PebbleAckReceiver(StaticReferences.PEBBLE_APP_UUID) {
            @Override
            public void receiveAck(Context context, int i) {
                Log.i("Pebble Comm", "Sent message successfully to Pebble");

                //After sending is successfully, null the dictionary
                switch (StaticReferences.extraSend){
                    case 1: StaticReferences.extraSend = -1; StaticReferences.dict1 = null; break;
                    case 2: StaticReferences.extraSend = -1; StaticReferences.dict2 = null; break;
                }

                //Check Static Vars. If theres any other message, send them
                if (StaticReferences.dict2 != null){
                    PebbleKit.sendDataToPebble(getApplicationContext(), StaticReferences.PEBBLE_APP_UUID, StaticReferences.dict2);
                    StaticReferences.extraSend = 2;
                } 
            }
        };
        PebbleKit.registerReceivedAckHandler(getApplicationContext(), mAck);
        Log.i("Pebble Comm", "Registered Ack Receiver");

        //Handle Data Receiver
        mReceiver = new PebbleKit.PebbleDataReceiver(StaticReferences.PEBBLE_APP_UUID) {
            @Override
            public void receiveData(Context context, int i, PebbleDictionary pebbleDictionary) {
                Log.i("Pebble Comm", "Received Message from Pebble");
                PebbleKit.sendAckToPebble(getApplicationContext(), i);
                Log.i("Pebble Comm", "Sent ACK to Pebble");

                //Handle stuff
                if (pebbleDictionary.contains(PebbleEnums.KEY_BUTTON_EVENT)){
                    Message msg = mServiceHandler.obtainMessage();
                    msg.arg1 = pebbleDictionary.getUnsignedIntegerAsLong(PebbleEnums.KEY_BUTTON_EVENT).intValue();
                    if (pebbleDictionary.contains(PebbleEnums.MESSAGE_MIN_DATA)){
                        msg.arg2 = pebbleDictionary.getInteger(PebbleEnums.MESSAGE_MIN_DATA).intValue();
                    } else {
                        msg.arg2 = 0;
                    }
                    mServiceHandler.sendMessage(msg);
                }
            }
        };
        PebbleKit.registerReceivedDataHandler(getApplicationContext(), mReceiver);
        Log.i("Pebble Comm", "Register Data Receiver");

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        return START_STICKY;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i("Pebble Comm", "SYSTEM: Created Service");

        /*
        Starts the thread handling service in another thread to not block the main UI thread.
        Also, it has background priority so CPU intensive work will not disrupt the main UI
         */
        HandlerThread thread = new HandlerThread("PebbleCommunications", Thread.MIN_PRIORITY);
        thread.start();

        serviceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(serviceLooper);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("Pebble Comm", "SYSTEM: Killed Service gracefully");

        unregisterPebbleReceivers();
    }

    private void unregisterPebbleReceivers(){

        if (mReceiver != null) {
            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                Log.e("PebbleComm", "Already Unregistered. Ignoring");
            }
        }
        if (mAck != null) {
            try {
                unregisterReceiver(mAck);
            } catch (IllegalArgumentException e) {
                Log.e("PebbleComm", "Already Unregistered. Ignoring");
            }
        }
        if (mNack != null) {
            try {
                unregisterReceiver(mNack);
            } catch (IllegalArgumentException e) {
                Log.e("PebbleComm", "Already Unregistered. Ignoring");
            }
        }

        mReceiver = null;
        mAck = null;
        mNack = null;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    private class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg){
            //Do stuff here
            if (isProcessing){
                Log.e("Pebble Comm", "Processing already. Ignoring data");
                return;
            }

            isProcessing = true;

            int buttonPress = msg.arg1;
            int currentPage = msg.arg2;

            switch (buttonPress){
                case PebbleEnums.BUTTON_PREVIOUS: Log.d("Pebble Comm", "Going Previous!"); prevHandler(currentPage); break;
                case PebbleEnums.BUTTON_NEXT: Log.d("Pebble Comm", "Going Next!"); nextHandler(currentPage); break;
                case PebbleEnums.BUTTON_REFRESH: Log.d("Pebble Comm", "Mark Completed!"); refreshHandler(currentPage); break;
                case PebbleEnums.BUTTON_NEW_DATA: Log.d("Pebble Comm", "Repoll and send raw!"); repollHandler(); break;
            }
        }

        private void repollPage(){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(PebbleComms.this);
            String json = sp.getString("storedPurchases", "null");
            if (json.equals("null"))
            {
                StaticReferences.products = new ArrayList<>();
                return;
            }

            Gson gson = new Gson();
            JSONStoredProducts[] prods = gson.fromJson(json, JSONGeneralStoredProducts.class).getStorage();

            ArrayList<JSONStoredProducts> tmp = new ArrayList<>();
            for (JSONStoredProducts p : prods){
                if (!p.isPurchased()) tmp.add(p);
            }

            StaticReferences.products = tmp;

        }

        private void repollHandler(){
            repollPage();
            PebbleDictionary dict1 = new PebbleDictionary();
            if (StaticReferences.products.size() == 0) {
                //Send Empty set
                dict1.addInt32(PebbleEnums.ERROR_NO_DATA, 1);
                PebbleKit.sendDataToPebble(getApplicationContext(), StaticReferences.PEBBLE_APP_UUID, dict1);
                StaticReferences.extraSend = 1;

                StaticReferences.dict1 = dict1;
                isProcessing = false;
                return;
            }

            JSONStoredProducts obj = StaticReferences.products.get(0);

            ProductDB db = new ProductDB(PebbleComms.this);
            JSONProducts prod = db.getJSONProductByKey(obj.getKey());

            String price = prod.getRetailPrice();
            if (prod.getOfferPrice() != null && !prod.getOfferPrice().equals(""))
            {
                price = prod.getOfferPrice();
            }

            //Push to pebble
            PebbleDictionary dict2 = new PebbleDictionary();
            dict1.addInt32(PebbleEnums.MESSAGE_DATA_EVENT, 1);
            dict2.addInt32(PebbleEnums.MESSAGE_DATA_EVENT, 2);

            dict1.addString(PebbleEnums.MESSAGE_TITLE, prod.getTitle());
            dict1.addInt32(PebbleEnums.MESSAGE_MAX_DATA, StaticReferences.products.size());
            dict1.addInt32(PebbleEnums.MESSAGE_MIN_DATA, 1);

            dict2.addInt32(PebbleEnums.MESSAGE_AVAIL, prod.getStockInt());
            dict2.addString(PebbleEnums.MESSAGE_LOCATION, prod.getStore());
            dict2.addString(PebbleEnums.MESSAGE_PRICE, price);

            PebbleKit.sendDataToPebble(getApplicationContext(), StaticReferences.PEBBLE_APP_UUID, dict1);
            StaticReferences.extraSend = 1;

            StaticReferences.dict1 = dict1;
            StaticReferences.dict2 = dict2;
            isProcessing = false;
        }

        private void nextHandler(int page){
            if (page <= StaticReferences.products.size()){
                //Get Next
                repollPage();
                if (page > StaticReferences.products.size()){
                    isProcessing = false;
                    return;
                }
                JSONStoredProducts obj = StaticReferences.products.get(page-1);

                ProductDB db = new ProductDB(PebbleComms.this);
                JSONProducts prod = db.getJSONProductByKey(obj.getKey());

                String price = prod.getRetailPrice();
                if (prod.getOfferPrice() != null && !prod.getOfferPrice().equals(""))
                {
                    price = prod.getOfferPrice();
                }

                //Push to pebble
                PebbleDictionary dict1 = new PebbleDictionary();
                PebbleDictionary dict2 = new PebbleDictionary();
                dict1.addInt32(PebbleEnums.MESSAGE_DATA_EVENT, 1);
                dict2.addInt32(PebbleEnums.MESSAGE_DATA_EVENT, 2);

                dict1.addString(PebbleEnums.MESSAGE_TITLE, prod.getTitle());
                dict1.addInt32(PebbleEnums.MESSAGE_MAX_DATA, StaticReferences.products.size());
                dict1.addInt32(PebbleEnums.MESSAGE_MIN_DATA, page + 1);

                dict2.addInt32(PebbleEnums.MESSAGE_AVAIL, prod.getStockInt());
                dict2.addString(PebbleEnums.MESSAGE_LOCATION, prod.getStore());
                dict2.addString(PebbleEnums.MESSAGE_PRICE, price);

                PebbleKit.sendDataToPebble(getApplicationContext(), StaticReferences.PEBBLE_APP_UUID, dict1);
                StaticReferences.extraSend = 1;

                StaticReferences.dict1 = dict1;
                StaticReferences.dict2 = dict2;
                isProcessing = false;
            }
            isProcessing = false;
        }

        private void prevHandler(int page){
            if (page > 1){
                //Get Previous
                repollPage();
                if (page <= 1){
                    isProcessing = false;
                    return;
                }
                JSONStoredProducts obj = StaticReferences.products.get(page - 2);

                ProductDB db = new ProductDB(PebbleComms.this);
                JSONProducts prod = db.getJSONProductByKey(obj.getKey());

                String price = prod.getRetailPrice();
                if (prod.getOfferPrice() != null && !prod.getOfferPrice().equals(""))
                {
                    price = prod.getOfferPrice();
                }

                //Push to pebble
                PebbleDictionary dict1 = new PebbleDictionary();
                PebbleDictionary dict2 = new PebbleDictionary();
                dict1.addInt32(PebbleEnums.MESSAGE_DATA_EVENT, 1);
                dict2.addInt32(PebbleEnums.MESSAGE_DATA_EVENT, 2);

                dict1.addString(PebbleEnums.MESSAGE_TITLE, prod.getTitle());
                dict1.addInt32(PebbleEnums.MESSAGE_MAX_DATA, StaticReferences.products.size());
                dict1.addInt32(PebbleEnums.MESSAGE_MIN_DATA, page - 1);

                dict2.addInt32(PebbleEnums.MESSAGE_AVAIL, prod.getStockInt());
                dict2.addString(PebbleEnums.MESSAGE_LOCATION, prod.getStore());
                dict2.addString(PebbleEnums.MESSAGE_PRICE, price);

                PebbleKit.sendDataToPebble(getApplicationContext(), StaticReferences.PEBBLE_APP_UUID, dict1);
                StaticReferences.extraSend = 1;

                StaticReferences.dict1 = dict1;
                StaticReferences.dict2 = dict2;
                isProcessing = false;
            }
            isProcessing = false;
        }

        private void refreshHandler(int page){
            Log.d("PebbleComm", "Received intent to mark complete :D");

            String json = sp.getString("storedPurchases", "wot");
            Log.d("FAVOURITES", "Favourites Pref: " + json);

            if (hasMarked){
                Log.e("PebbleComm", "Has marked within the previous 5 seconds. ignoring this now");
                return;
            }
            ProductStorage.markPurchased(PreferenceManager.getDefaultSharedPreferences(PebbleComms.this), StaticReferences.products.get(page - 1));
            hasMarked = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hasMarked = false;
                }
            }, 5000);
            repollPage();
            isProcessing = false;
        }
    }
}
