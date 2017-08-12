package com.pranavj7.android.hellonote.Authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.pranavj7.android.hellonote.Authenticator.Authenticator;

public class AuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        Log.d("MyAuthenticatorService", "onCreate");
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MyAuthenticatorService", "onBind");
        return mAuthenticator.getIBinder();
    }
}