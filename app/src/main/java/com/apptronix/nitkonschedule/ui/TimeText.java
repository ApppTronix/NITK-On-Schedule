package com.apptronix.nitkonschedule.ui;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptronix.nitkonschedule.R;

/**
 * Created by Maha Perriyava on 9/9/2017.
 */

public class TimeText extends LinearLayout {

    LinearLayout layout = null;
    TextView timeTextView, ampmTextView;
    Context mContext = null;

    public TimeText(Context context) {
        super(context);
        mContext = context;
    }

    public TimeText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeText);

        String dateText = a.getString(R.styleable.TimeText_time);
        String dayText = a.getString(R.styleable.TimeText_ampm);

        dateText = dateText == null ? "" : dateText;
        dayText = dayText == null ? "" : dayText;

        String service = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(service);

        layout = (LinearLayout) li.inflate(R.layout.timetext, this, true);

        timeTextView = (TextView) layout.findViewById(R.id.date_num);
        ampmTextView = (TextView) layout.findViewById(R.id.date_day);

        timeTextView.setText(dateText);
        ampmTextView.setText(dayText);

        a.recycle();
    }

    public TimeText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @SuppressWarnings("unused")
    public void setAmPM(String text) {
        ampmTextView.setText(text);
    }

    @SuppressWarnings("unused")
    public void setTime(String text) {
        timeTextView.setText(text);
    }

    @SuppressWarnings("unused")
    public String getAmPM() {
        return ampmTextView.getText().toString();
    }

    @SuppressWarnings("unused")
    public String getTime() {
        return timeTextView.getText().toString();
    }


}
