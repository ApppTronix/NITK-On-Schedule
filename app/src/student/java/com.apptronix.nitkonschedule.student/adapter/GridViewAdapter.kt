package com.apptronix.nitkonschedule.student.adapter

import android.app.Activity
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import com.apptronix.nitkonschedule.R
import timber.log.Timber

import java.util.ArrayList


class GridViewAdapter internal constructor(context: Context, private val layoutResourceId: Int, private var itemList: ArrayList<ImageItem>?) : ArrayAdapter<GridViewAdapter.ViewHolder>(context, layoutResourceId) {

    override fun getCount(): Int {
        return if (this.itemList != null) this.itemList!!.size else 0
    }

    fun setData(itemList:ArrayList<ImageItem>){
        this.itemList=itemList
        notifyDataSetInvalidated()
        notifyDataSetChanged()

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        var holder: ViewHolder? = null


        Timber.i("get view called GridVie adapter");
        if (row == null) {
            val inflater = (context as Activity).layoutInflater
            row = inflater.inflate(layoutResourceId, parent, false)
            holder = ViewHolder()
            holder.imageTitle = row!!.findViewById<View>(R.id.text) as TextView
            holder.image = row.findViewById<View>(R.id.image) as ImageView
            row.tag = holder
        } else {
            holder = row.tag as ViewHolder
        }

        val item = itemList!!.get(position) as ImageItem
        holder.imageTitle!!.text = item.title
        holder.image!!.setImageBitmap(item.image)
        return row
    }

     class ViewHolder {
        var imageTitle: TextView? = null
        var image: ImageView? = null
    }
}