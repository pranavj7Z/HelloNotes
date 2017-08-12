package com.pranavj7.android.hellonote.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pranavj7.android.hellonote.Utility.NotificationBuilder;

import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = AlarmReceiver.class.getSimpleName();

    public static final String EXTRA_TODO_ID = "todo_id";
    public static final String EXTRA_NOTE_ID = "note_id";
    public static final String EXTRA_NOTE_TITLE = "note_title";
    public static final String EXTRA_TODO_TITLE = "todo_title";



    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG, "Received alarm intent");

        int todoId = intent.getIntExtra(EXTRA_TODO_ID, -1);
        int noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1);
        String noteTitle = intent.getStringExtra(EXTRA_NOTE_TITLE);
        String todoTitle = intent.getStringExtra(EXTRA_TODO_TITLE);

        if (todoId == -1) {
            Log.e(LOG_TAG, "Alarm intent missing reminder ID");
            return;
        }

        if (noteId == -1) {
            Log.e(LOG_TAG, "Alarm intent missing habit ID");
            return;
        }

         NotificationBuilder.showReminderNotification(context, todoId, noteId, noteTitle , todoTitle);
    }
}