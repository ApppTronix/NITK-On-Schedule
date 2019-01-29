package com.apptronix.nitkonschedule.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.teacher.ui.ClickListener;

import java.lang.ref.WeakReference;

import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

/**
 * Created by Maha Perriyava on 12/26/2017.
 */

public class ViewMarkedAdapter extends RecyclerView.Adapter<ViewMarkedAdapter.ViewHolder> {

    Context context;
    String[] data;
    Boolean def;
    private static LayoutInflater inflater = null;

    public ViewMarkedAdapter(Context context, String[] data, Boolean def) {
        this.context = context;
        this.data = data;
        this.def=def;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.i("view holder created");
        return new ViewHolder(
                inflater.inflate(R.layout.mark_attendance_submission_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        vh.checkBox.setChecked(true);
        vh.checkBox.setEnabled(false);
        vh.textView.setText(data[position]);
    }


    public class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView textView;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.rollNumber);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

    }

}
