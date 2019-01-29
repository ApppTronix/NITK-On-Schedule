package com.apptronix.nitkonschedule.teacher.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.model.SingleMarkedStudent;
import com.apptronix.nitkonschedule.teacher.ui.ClickListener;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.annotation.Nullable;

import timber.log.Timber;

/**
 * Created by Maha Perriyava on 12/26/2017.
 */

public class MarkAttendanceAdapter extends RecyclerView.Adapter<MarkAttendanceAdapter.ViewHolder> {

    Context context;
    Boolean def;
    List<SingleMarkedStudent> markedStudents;
    List<String> data;
    public final ClickListener listener;
    private static LayoutInflater inflater = null;

    public MarkAttendanceAdapter(Context context,List<String> data, Boolean def, ClickListener listener) {
        this.listener = listener;
        this.context = context;
        this.data = data;
        this.def=def;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public MarkAttendanceAdapter(Context context, List<SingleMarkedStudent> markedStudentList, ClickListener listener) {
        this.listener = listener;
        this.context = context;
        this.markedStudents = markedStudentList;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.i("view holder created");
        return new ViewHolder(
                inflater.inflate(R.layout.mark_attendance_submission_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        Timber.i("Binding %d",position);
        if(markedStudents==null){
            Timber.i("Binding %s",data.get(position));
            vh.checkBox.setChecked(def);
            vh.textView.setText(data.get(position));
        } else {
            vh.textView.setText(markedStudents.get(position).getCollId());
            vh.checkBox.setChecked(markedStudents.get(position).getPresent());
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        public TextView textView;
        public CheckBox checkBox;

        private WeakReference<ClickListener> listenerRef;
        public ViewHolder(View itemView) {
            super(itemView);

            listenerRef = new WeakReference<>(listener);
            textView = itemView.findViewById(R.id.rollNumber);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        @Override
        public void onClick(View view) {
            if(view.getId()==checkBox.getId()){
                listenerRef.get().onPositionClicked(getAdapterPosition(),checkBox.isChecked());
            }

        }
    }

}
