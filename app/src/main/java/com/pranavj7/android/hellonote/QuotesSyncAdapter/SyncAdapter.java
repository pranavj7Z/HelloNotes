package com.pranavj7.android.hellonote.QuotesSyncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.Utility.QuotesPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL    = 60*60; // 60(seconds) *  60(minutes) = 1 hour;
    public static final int SYNC_FLEXTIME    = SYNC_INTERVAL/3;
             //obtaininf forismatic quotes url
    private static final String QUOTES_URL =
            "http://api.forismatic.com/api/1.0/?method=getQuote&format=json&lang=en";


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        final String QUOTE = "quoteText";
        final String AUTHOR = "quoteAuthor";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(QUOTES_URL);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            if (builder.length() == 0) {
                Log.e(LOG_TAG, "Quote JSON was empty.");
                return;
            }

            JSONObject json = new JSONObject(builder.toString());

            String quote = json.getString(QUOTE).trim() + "\n- " + json.getString(AUTHOR).trim();

            QuotesPreference.setQuoteOfDay(getContext(), quote);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error fetching quote", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing quote JSON", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.i("MySyncAdapter", "syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),context.getString(R.string.authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE); // Get an instance of the Android account manager
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type)); // Create the account type and default account

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                Log.e("MySyncAdapter", "getSyncAccount Failed to create new account.");
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.i("MySyncAdapter", "onAccountCreated");
       SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        Log.d("MySyncAdapter", "initializeSyncAdapter");
        getSyncAccount(context);
    }
}