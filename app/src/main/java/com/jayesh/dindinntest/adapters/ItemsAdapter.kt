package com.jayesh.dindinntest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jayesh.dindinntest.R
import com.jayesh.dindinntest.orderModels.Items

class ItemsAdapter(private var dataList : List<Items>) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = dataList[position]
        holder.tv_item_qty.text ="x"+ item.quantity.toString() +" "
        holder.tv_item_name.text = item.title
        var addons_str:String=""
        if(item.addon!=null){
            for(addons in item.addon){
                addons_str += addons.quantity.toString() +" "+addons.title+"\n"
            }
        }
        holder.tv_item_addons.text=addons_str
    }

    fun setData(orderlist:List<Items>){
        dataList = orderlist
        notifyDataSetChanged()
    }

    fun getData(): List<Items> {
        return this.dataList
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_item, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var tv_item_qty:TextView = view.findViewById(R.id.tv_item_qty)
        var tv_item_name:TextView = view.findViewById(R.id.tv_item_name)
        var tv_item_addons:TextView = view.findViewById(R.id.tv_item_addons)
    }
}