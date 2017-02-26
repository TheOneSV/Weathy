package com.yonko.weathy.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yonko.weathy.app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    // Flag to determinate if we want want to use separate view for "today"
    private boolean useTodayLayout = true;

    private Cursor mCursor;
    private final Context mContext;
    private final ForecastAdapterOnClickHandler mClickHandler;
    private final View mEmptyView;


    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView mIconView;
        private final TextView mDateView;
        private final TextView mDescriptionView;
        private final TextView mHighTempView;
        private final TextView mLowTempView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            mIconView = (ImageView) itemView.findViewById(R.id.list_item_icon);
            mDateView = (TextView) itemView.findViewById(R.id.list_item_date_textview);
            mDescriptionView = (TextView) itemView.findViewById(R.id.list_item_forecast_textview);
            mHighTempView = (TextView) itemView.findViewById(R.id.list_item_high_textview);
            mLowTempView = (TextView) itemView.findViewById(R.id.list_item_low_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dateColumnIndex = mCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            mClickHandler.onClick(mCursor.getLong(dateColumnIndex), this);
        }
    }

    public ForecastAdapter(Context context, ForecastAdapterOnClickHandler dh, View emptyView) {
        mContext = context;
        mClickHandler = dh;
        mEmptyView = emptyView;
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_TODAY: {
                    layoutId = R.layout.list_item_forecast_today;
                } break;
                case VIEW_TYPE_FUTURE_DAY: {
                    layoutId = R.layout.list_item_forecast;
                }
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            view.setFocusable(true);
            return new ForecastAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecycleViewSelection");
        }
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        // ViewCompat.setTransitionName(holder.mIconView, "iconView" + position);

        int weatherId = mCursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int weatherConditionRes;
        if (getItemViewType(mCursor.getPosition()) == VIEW_TYPE_TODAY) {
            weatherConditionRes = Utility.getArtResourceForWeatherCondition(weatherId);
        } else {
            weatherConditionRes = Utility.getIconResourceForWeatherCondition(weatherId);
        }
        holder.mIconView.setImageResource(weatherConditionRes);

        long date = mCursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        holder.mDateView.setText(Utility.getFriendlyDayString(mContext, date));

        String forecast = mCursor.getString(ForecastFragment.COL_WEATHER_DESC);
        holder.mDescriptionView.setText(forecast);
        holder.mIconView.setContentDescription(forecast);
        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(mContext);

        // Read high temperature from cursor
        double high = mCursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        holder.mHighTempView.setText(Utility.formatTemperature(mContext, high, isMetric));

        double low = mCursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        holder.mLowTempView.setText(Utility.formatTemperature(mContext, low, isMetric));
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && useTodayLayout)? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        this.useTodayLayout = useTodayLayout;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public interface ForecastAdapterOnClickHandler {
          void onClick(Long date, ForecastAdapterViewHolder vh);
    }
}
