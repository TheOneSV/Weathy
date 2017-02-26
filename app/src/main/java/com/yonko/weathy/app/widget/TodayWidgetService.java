package com.yonko.weathy.app.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.Time;
import android.widget.RemoteViews;

import com.yonko.weathy.app.MainActivity;
import com.yonko.weathy.app.R;
import com.yonko.weathy.app.Utility;
import com.yonko.weathy.app.data.WeatherContract;

public class TodayWidgetService extends IntentService {

    public TodayWidgetService() {
        super("TodayWidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = this.getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final ComponentName cn = new ComponentName(context, TodayWidgetProvider.class);
        int []allWidgetIds = appWidgetManager.getAppWidgetIds(cn);

        handleActionUpdate(context, appWidgetManager, allWidgetIds);
    }

    private void handleActionUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        boolean isMetric = Utility.isMetric(context);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_today);

            Cursor cursor = queryWeather(context);
            if (cursor.moveToFirst()) {
                int highIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
                double high = cursor.getDouble(highIndex);
                views.setTextViewText(R.id.list_item_high_textview, Utility.formatTemperature(context, high, isMetric));

                int lowIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
                double low = cursor.getDouble(lowIndex);
                views.setTextViewText(R.id.list_item_low_textview, Utility.formatTemperature(context, low, isMetric));

                int weatherIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
                int weatherId = cursor.getInt(weatherIndex);

                views.setImageViewResource(R.id.imageView, Utility.getArtResourceForWeatherCondition(weatherId));
            }
            cursor.close();

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private Cursor queryWeather(Context context) {
//        Cursor cursor = getContentResolver().query(
//                WeatherContract.WeatherEntry.CONTENT_URI,
//                new String[]{WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
//                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
//                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID},
//                null,
//                null,
//                WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
        Time t = new Time();
        t.setToNow();
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        long dateTime = t.setJulianDay(currentJulianDay);
        Cursor cursor = getContentResolver().query(
                WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        Utility.getPreferredLocation(context), dateTime),
                new String[]{WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID},
                null,
                null,
                WeatherContract.WeatherEntry.COLUMN_DATE + " ASC"

        );
        return cursor;
    }
}
