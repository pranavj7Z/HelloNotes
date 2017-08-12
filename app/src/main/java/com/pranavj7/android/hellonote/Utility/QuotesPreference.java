package com.pranavj7.android.hellonote.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;


import com.pranavj7.android.hellonote.R;
public class QuotesPreference {
    //getting quotes in shared preference
    public static String getQuoteOfDay(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.quotes_of_day),
                context.getString(R.string.default_quote_of_day));
    }
    //updating with the latest quote
    public static void setQuoteOfDay(Context context, String quote) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.quotes_of_day), quote);
        editor.apply();
    }
}
