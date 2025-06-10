package com.example.vipayee;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.google.android.gms.auth.api.phone.SmsRetriever;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public SmsBroadcastReceiverListener smsBroadcastReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                if (consentIntent != null && smsBroadcastReceiverListener != null) {
                    smsBroadcastReceiverListener.onSuccess(consentIntent);
                } else {
                    smsBroadcastReceiverListener.onFailure();
                }
            }
        }
    }

    public interface SmsBroadcastReceiverListener {
        void onSuccess(Intent intent);
        void onFailure();
    }
}