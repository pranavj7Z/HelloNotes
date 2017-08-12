package com.pranavj7.android.hellonote.notifications;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.SparseArray;

import com.pranavj7.android.hellonote.provider.NotesContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class RescheduleIntentService extends IntentService {
    private static final String LOG_TAG = RescheduleIntentService.class.getSimpleName();
    public static final String EXTRA_NOTE_ID = "extra_habit_id";
    public static final String EXTRA_REMOVE_REMINDER = "extra_delete_reminder";
    public static final String EXTRA_TODO_ID = "extra_reminder_id";
    public static final String EXTRA_CLEAR_ONLY = "extra_clear_only";

    private int mNoteId = -1;
    private int mTodoId = -1;



    public RescheduleIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNoteId = intent.getIntExtra(EXTRA_NOTE_ID, -1);
        mTodoId = intent.getIntExtra(EXTRA_TODO_ID, -1);


        boolean removeReminder = intent.getBooleanExtra(EXTRA_REMOVE_REMINDER, false);
        boolean clearOnly = removeReminder || intent.getBooleanExtra(EXTRA_CLEAR_ONLY, false);
        String selection = null;
        String[] selectionArgs = null;

        if (intent.hasExtra(EXTRA_TODO_ID)) {
            selection = RemindersQuery.SELECT_BY_ID;
            selectionArgs = new String[]{String.valueOf(mTodoId)};
        } else if (intent.hasExtra(EXTRA_NOTE_ID)) {
            selection = RemindersQuery.SELECT_BY_NOTE;
            selectionArgs = new String[]{String.valueOf(mNoteId)};
        }

        Cursor cursor = getContentResolver().query(NotesContract.TODOC.CONTENT_URI,
                RemindersQuery.PROJECTION, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                reschedule(cursor, clearOnly);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        cursor.close();
        }

        if (mTodoId != -1 && removeReminder) {
            Uri reminderUri = ContentUris.withAppendedId(NotesContract.TODOC.CONTENT_URI, mTodoId);

            getContentResolver().delete(reminderUri, null, null);
        }
    }

    private String getNoteTitle(int noteId) {
        final int COLUMN_NAME = 0;

        Cursor cursor = getContentResolver().query(
                NotesContract.NOTESC.CONTENT_URI,
                new String[]{NotesContract.NOTESC.COLUMN_TITLE},
                NotesContract.NOTESC._ID + " = ?",
                new String[]{String.valueOf(noteId)},
                null
        );

        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        try {
            return cursor.getString(COLUMN_NAME);
        } finally {
            cursor.close();
        }
    }

    final int SDK_INT = Build.VERSION.SDK_INT;
    /** private void registerExactAlarm(PendingIntent sender, long delayInMillis) {
     final int SDK_INT = Build.VERSION.SDK_INT;
     AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
     long timeInMillis = (System.currentTimeMillis() + delayInMillis) / 1000 * 1000;     //> example

     if (SDK_INT < Build.VERSION_CODES.KITKAT) {
     am.set(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
     }
     else if (Build.VERSION_CODES.KITKAT <= SDK_INT  && SDK_INT < Build.VERSION_CODES.M) {
     am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
     }
     else if (SDK_INT >= Build.VERSION_CODES.M) {
     am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
     }
     } **/

    private String getTodoTitle(int todoId) {
        final int COLUMN_TASKS = 0;

        Cursor cursor = getContentResolver().query(
                NotesContract.TODOC.CONTENT_URI,
                new String[]{NotesContract.TODOC.COLUMN_TASKS},
                NotesContract.TODOC._ID + " = ?",
                new String[]{String.valueOf(todoId)},
                null
        );

        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        try {
            return cursor.getString(COLUMN_TASKS);
        } finally {
            cursor.close();
        }
    }

    private void reschedule(Cursor cursor, boolean clearOnly) throws ParseException {
        SparseArray<String> nameCache = new SparseArray<>();

        do {
            int noteId = cursor.getInt(RemindersQuery.COLUMN_NOTES_ID);
            int todoId= cursor.getInt(RemindersQuery.COLUMN_ID);
            String noteTitle = nameCache.get(noteId);
            String todoTitle = nameCache.get(todoId);
            if (noteTitle == null) {
                noteTitle = getNoteTitle(noteId);
                nameCache.put(noteId, noteTitle);
            }
            if(todoTitle==null)
            {
                todoTitle = getTodoTitle(todoId);
                nameCache.put(todoId,todoTitle);
            }

                rescheduleAlarm(clearOnly, cursor, noteTitle , todoTitle);
        } while (cursor.moveToNext());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void rescheduleAlarm(boolean clearOnly, Cursor cursor, String noteTitle , String todoTitle) throws ParseException {
        int todoId = cursor.getInt(RemindersQuery.COLUMN_ID);
        int noteId = cursor.getInt(RemindersQuery.COLUMN_NOTES_ID);
        int timeInMinutes = cursor.getInt(RemindersQuery.COLUMN_TIME);
        String task = cursor.getString(RemindersQuery.COLUMN_TASKS);
        int hour =  timeInMinutes/ 60;
        int minute = timeInMinutes % 60;
        String n =DateFormat.getDateInstance().format(cursor.getLong(RemindersQuery.COLUMN_DATE));
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

        Date theDate = format.parse(n);

        Calendar myCal = new GregorianCalendar();
        myCal.setTime(theDate);
        int day =  myCal.get(Calendar.DAY_OF_MONTH);
         int month = myCal.get(Calendar.MONTH);
        int year = myCal.get(Calendar.YEAR);
//Set time in milliseconds
//        String time1 = getString(R.string.time_format, hour, minute);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_TODO_ID, todoId);
        intent.putExtra(AlarmReceiver.EXTRA_NOTE_ID, noteId);
        intent.putExtra(AlarmReceiver.EXTRA_NOTE_TITLE, noteTitle);
        intent.putExtra(AlarmReceiver.EXTRA_TODO_TITLE, todoTitle);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, todoId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar c2 = Calendar.getInstance();
        c2.set(year,month,day,hour,minute,0);
        alarmManager.cancel(pendingIntent);
        if (!clearOnly) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
             {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(),
                        pendingIntent);
                        }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(),
                        pendingIntent);
            }
            else
            {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(),
                        pendingIntent);
            }
            }
        }

        private static final class RemindersQuery {
            private RemindersQuery() {
            }

            static final String SELECT_BY_ID =
                    NotesContract.TODOC._ID + " = ?";

            static final String SELECT_BY_NOTE=
                    NotesContract.TODOC.COLUMN_NOTES_ID+ " = ?";
            static final String SELECT_BY_TASK=
                    NotesContract.TODOC.COLUMN_TASKS+ " = ?";

            static final String[] PROJECTION = new String[]{
                    NotesContract.TODOC._ID,
                    NotesContract.TODOC.COLUMN_TIME,
                    NotesContract.TODOC.COLUMN_NOTES_ID,
                    NotesContract.TODOC.COLUMN_DATE,
                    NotesContract.TODOC.COLUMN_TASKS,
            };

            static final int COLUMN_ID = 0;
            static final int COLUMN_TIME = 1;
            static final int COLUMN_NOTES_ID = 2;
            static final int COLUMN_DATE=3;
            static final int COLUMN_TASKS=4;


        }

}
