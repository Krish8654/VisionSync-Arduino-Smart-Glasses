package com.example.visionsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent) {

        try {

            String state =
                    intent.getStringExtra(
                            TelephonyManager.EXTRA_STATE
                    );

            String number =
                    intent.getStringExtra(
                            TelephonyManager.EXTRA_INCOMING_NUMBER
                    );

            if(state == null) return;

            // INCOMING CALL
            if(state.equals(
                    TelephonyManager.EXTRA_STATE_RINGING)) {

                String callerName =
                        getContactName(context, number);

                if(callerName == null ||
                        callerName.equals("")) {

                    callerName = number;
                }

                MainActivity.sendData(
                        "CALL " + callerName
                );
            }

            // CALL ENDED
            else if(state.equals(
                    TelephonyManager.EXTRA_STATE_IDLE)) {

                MainActivity.sendData(
                        "ENDCALL"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // GET CONTACT NAME
    private String getContactName(
            Context context,
            String phoneNumber) {

        Uri uri =
                Uri.withAppendedPath(
                        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(phoneNumber)
                );

        Cursor cursor =
                context.getContentResolver().query(
                        uri,
                        new String[]{
                                ContactsContract.PhoneLookup.DISPLAY_NAME
                        },
                        null,
                        null,
                        null
                );

        if(cursor != null) {

            if(cursor.moveToFirst()) {

                String name =
                        cursor.getString(0);

                cursor.close();

                return name;
            }

            cursor.close();
        }

        return phoneNumber;
    }
}