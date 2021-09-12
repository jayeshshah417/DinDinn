package com.jayesh.dindinntest.adapters

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jayesh.dindinntest.R
import com.jayesh.dindinntest.incredientsModels.IncredientsMenu
import com.jayesh.dindinntest.orderModels.Items
import com.jayesh.dindinntest.utils.DateTimeUtils
import com.jayesh.dindinntest.utils.OConstants
import kotlin.time.ExperimentalTime

class IncredientsMenuAdapter(private var dataList : List<IncredientsMenu>, private val listener : Listener, private val mContext:Context) : RecyclerView.Adapter<IncredientsMenuAdapter.ViewHolder>() {


    interface Listener {

        fun onIncredientItemClick(android : IncredientsMenu, map:HashMap<Int,CountDownTimer>?)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val incredientsMenu = dataList[position]
        holder.tv_title.text = incredientsMenu.title
        holder.tv_title1.text = incredientsMenu.title
        holder.tv_qty.text = incredientsMenu.qty_available.toString()
        holder.tv_qty1.text = incredientsMenu.qty_available.toString()
        if(incredientsMenu.qty_available<incredientsMenu.min_qty){
            holder.ll_below_qty.visibility=View.VISIBLE
            holder.ll_qty.visibility=View.GONE
        }else{
            holder.ll_below_qty.visibility=View.GONE
            holder.ll_qty.visibility=View.VISIBLE
        }


    }

    fun setData(orderlist:List<IncredientsMenu>){
        dataList = orderlist
        notifyDataSetChanged()
    }

    fun getData(): List<IncredientsMenu> {
        return this.dataList
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_incredients_item, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var tv_title:TextView = view.findViewById(R.id.tv_title)
        var tv_title1:TextView = view.findViewById(R.id.tv_title1)
        var ll_below_qty:LinearLayout = view.findViewById(R.id.ll_below_qty)
        var ll_qty:LinearLayout = view.findViewById(R.id.ll_qty)
        var tv_qty:TextView = view.findViewById(R.id.tv_qty)
        var tv_qty1:TextView = view.findViewById(R.id.tv_qty1)
    }





}