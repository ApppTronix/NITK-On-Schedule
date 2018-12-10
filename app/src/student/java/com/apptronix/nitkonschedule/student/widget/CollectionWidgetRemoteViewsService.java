package com.apptronix.nitkonschedule.student.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by DevOpsTrends on 5/10/2017.
 */

public class CollectionWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TimeTableCollectionRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
