package com.apptronix.nitkonschedule.teacher.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.teacher.data.DBContract;

import timber.log.Timber;



public class ResourcesAdapter extends CursorAdapter {

    public ResourcesAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.resources_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(cursor.getCount()>0){
            ResourcesAdapter.ResourceViewHolder holder = new ResourcesAdapter.ResourceViewHolder(view);
            holder.resourceName.setText(cursor.getString(cursor.getColumnIndex(DBContract.RepositoryEntry.COLUMN_FILE_NAME)));
            String loc = cursor.getString(cursor.getColumnIndex(DBContract.RepositoryEntry.COLUMN_FILE_LOCATION));
            if(loc!=null && !loc.equals("")){
                holder.resourceAvailable.setImageResource(R.drawable.ic_provider);
                holder.resourceName.setTag(loc);
            }else{
                holder.resourceAvailable.setImageResource(R.drawable.ic_action_name);
                holder.resourceName.setTag("cloud");
            }
        } else {
            Timber.i("courses cursor  null");
        }
    }

    private class ResourceViewHolder {

        TextView resourceName;
        ImageView resourceAvailable;
        public ResourceViewHolder(View itemView) {
            resourceName=(TextView)itemView.findViewById(R.id.resourceName);
            resourceAvailable=(ImageView)itemView.findViewById(R.id.resourceAvailable);
        }
    }
}