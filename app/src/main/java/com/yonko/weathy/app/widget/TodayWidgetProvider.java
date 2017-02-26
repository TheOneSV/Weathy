package com.yonko.weathy.app.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class TodayWidgetProvider extends AppWidgetProvider {
    private final static String LOG_TAG = TodayWidgetProvider.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive Called");
        super.onReceive(context, intent);
        startUpdateService(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "onUpdate Called");
        startUpdateService(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.d(LOG_TAG, "onAppWidgetOptionsChanged Called");
        startUpdateService(context);
    }

    public void startUpdateService(Context context) {
        Intent intent = new Intent(context, TodayWidgetService.class);
        context.startService(intent);
    }
}
