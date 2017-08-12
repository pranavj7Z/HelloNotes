package com.pranavj7.android.hellonote.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.ui.NotesAndTodoContainerActivity;

public class HelloNoteWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, HelloNoteWidgetProvider.class);
            int[] appWidgetIds = widgetManager.getAppWidgetIds(componentName);

            widgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.notes_list);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, HelloNoteRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            rv.setRemoteAdapter(R.id.notes_list, intent);
            rv.setEmptyView(R.id.notes_list, R.id.empty_view);

            Intent clickIntent = new Intent(context, NotesAndTodoContainerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
            rv.setPendingIntentTemplate(R.id.notes_list, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }
}
