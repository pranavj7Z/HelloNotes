package com.pranavj7.android.hellonote.QuotesSyncAdapter;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class SyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter mySyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyServiceSync", "onCreate");
        synchronized (sSyncAdapterLock) {
            if (mySyncAdapter == null) {
                mySyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MyServiceSync", "onBind");
        return mySyncAdapter.getSyncAdapterBinder();
    }
}