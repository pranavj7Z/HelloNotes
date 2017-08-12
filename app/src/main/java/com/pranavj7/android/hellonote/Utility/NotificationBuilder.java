package com.pranavj7.android.hellonote.Utility;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.ui.NotesAndTodoContainerActivity;

public class NotificationBuilder {

    public static void showReminderNotification(Context context, int todoId,
                                                int noteId, String noteTitle ,String todoTitle) {

        Intent habitIntent = new Intent(context, NotesAndTodoContainerActivity.class);
        habitIntent.putExtra(NotesAndTodoContainerActivity.EXTRA_NOTE_ID, noteId);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, habitIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_directions_run77)
                .setContentTitle(noteTitle)
                .setContentText(context.getString(R.string.task))
                .setContentText(todoTitle)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(todoId, notification);
    }
}
