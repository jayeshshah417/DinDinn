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
import com.jayesh.dindinntest.orderModels.Items
import com.jayesh.dindinntest.orderModels.Orders
import com.jayesh.dindinntest.utils.DateTimeUtils
import com.jayesh.dindinntest.utils.OConstants
import kotlin.time.ExperimentalTime

class OrderAdapter(private var dataList: List<Orders>, private val listener: Listener?, private val mContext:Context?) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private var mDatalist:List<Orders>?=dataList
    constructor(dataList : List<Orders>) : this(dataList,null,null){
        this.mDatalist = dataList
    }

    private var map_countertimer:HashMap<Int,CountDownTimer>?=null
    private var map_accept_expired:HashMap<Int,Boolean>?=null
    interface Listener {

        fun onItemClick(android : Orders, map:HashMap<Int,CountDownTimer>?)
    }

    fun setMap(map_time:HashMap<Int,CountDownTimer>){
        this.map_countertimer = map_time
    }

    fun getMap(): HashMap<Int, CountDownTimer>? {
       return this.map_countertimer
    }

    fun setMapAcceptExpired(map_time:HashMap<Int,Boolean>){
        this.map_accept_expired = map_time
    }

    fun getMapAcceptExpired(): HashMap<Int, Boolean>? {
        return this.map_accept_expired
    }
    @ExperimentalTime
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(mContext!=null) {
            val order = dataList[position]
            holder.tv_order_id.text ="#"+ order.id.toString()
            holder.tv_order_time.text =DateTimeUtils.convertToDefault(order.created_at,DateTimeUtils.DEFAULT_FORMAT,DateTimeUtils.DEFAULT_TIME_FORMAT_AM_PM)

            if(is_in_alertZone_not_expired(order.alerted_at,order.expired_at)){
                holder.iv_notify.visibility=View.VISIBLE
            }else{
                holder.iv_notify.visibility=View.GONE
            }

            val linearLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(mContext)
            holder.rv_items!!.layoutManager = linearLayoutManager
            var mItems_list: List<Items> = ArrayList<Items>()
            if (order.items != null) {
                mItems_list = order.items
            }
            val mItemAdapter = ItemsAdapter(mItems_list)
            holder.rv_items!!.adapter = mItemAdapter
            mItemAdapter!!.notifyDataSetChanged()


            holder.bt_accept.setOnClickListener(View.OnClickListener {
                order_Accept_Expired(order)
            })
            holder.bt_expired.setOnClickListener(View.OnClickListener {
                order_Accept_Expired(order)
            })

            val diff_time = get_diff_curr_expired(order.expired_at)

            if (is_not_already_expired(diff_time!!)) {
                if (map_countertimer!!.get(order.id) == null && diff_time != null) {
                    val timer = object : CountDownTimer(diff_time, 1) {
                        override fun onTick(millisUntilFinished: Long) {
                            checkAlertExpired(holder, order, millisUntilFinished)
                        }

                        override fun onFinish() {
                            setExpiredButton(holder)
                        }
                    }
                    timer.start()
                    if (map_countertimer == null) {
                        map_countertimer = HashMap<Int, CountDownTimer>();
                        map_countertimer!!.put(order.id, timer)
                    } else {
                        map_countertimer!!.put(order.id, timer)
                    }
                }
            } else {

                if (map_accept_expired!!.get(order.id) == null) {
                    setExpiredButton(holder)
                }
            }


            holder.itemView.setOnClickListener {
                if (listener != null) {
                    listener!!.onItemClick(order, map_countertimer)
                }
            }
        }
    }



    fun setData(orderlist:List<Orders>){
        dataList = orderlist
        //from line 150 to 156 can be deleted if we get only orders which are active when refreshing the list
        var datalist1:MutableList<Orders> =ArrayList<Orders>()
        for(data in dataList){
            if(map_accept_expired!!.get(data.id)==null || map_accept_expired!!.get(data.id)!=true){
                datalist1.add(data)
            }
        }
        dataList = datalist1
        notifyDataSetChanged()
    }

    fun getData(): List<Orders> {
        return this.dataList
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_line, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var tv_order_id:TextView = view.findViewById(R.id.tv_order_id)
        var tv_order_expired:TextView = view.findViewById(R.id.tv_order_expired)
        var tv_order_time:TextView = view.findViewById(R.id.tv_order_time)
        var rv_items:RecyclerView=view.findViewById(R.id.rv_items)
        var bt_accept:Button =view.findViewById(R.id.bt_accept)
        var bt_expired:Button =view.findViewById(R.id.bt_expired)
        var iv_notify:ImageView =view.findViewById(R.id.iv_notify)
        var ll_5:LinearLayout =view.findViewById(R.id.ll_5)
        var ll_4:LinearLayout =view.findViewById(R.id.ll_4)
        var ll_3:LinearLayout =view.findViewById(R.id.ll_3)
        var ll_2:LinearLayout =view.findViewById(R.id.ll_2)
        var ll_1:LinearLayout =view.findViewById(R.id.ll_1)

    }


    fun resetMapTimer(){
        if (map_countertimer != null) {
            for ((key, value) in map_countertimer!!) {
                if(value!=null) {
                    value.cancel()
                }
            }
        }
        this.map_countertimer = HashMap<Int,CountDownTimer>()
    }

    @ExperimentalTime
    private fun checkAlertExpired(holder: OrderAdapter.ViewHolder, order: Orders, millisUntilFinished:Long) {

        if (is_in_alert_zone(order.alerted_at)) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                if(!holder.iv_notify.isVisible) {
                    holder.iv_notify.visibility = View.VISIBLE
                    OConstants.playTone()
                }


            }, 0)

        }

        val long_mins = OConstants.getMins(millisUntilFinished)
        val long_secs = OConstants.getSecs(millisUntilFinished)
        holder.tv_order_expired.text = "" + String.format("%d min, %d sec",long_mins,long_secs)
        showProgress(long_mins,long_secs,holder)
    }

    @ExperimentalTime
    fun get_diff_curr_expired(expired:String): Long? {
        if(expired!=null) {
            var diff_time = DateTimeUtils.getDateDiff(
                DateTimeUtils.getUTCDate(DateTimeUtils.DEFAULT_FORMAT),
                expired
            )
            return diff_time
        }else{
            return null
        }
    }

    fun is_not_already_expired(milliseconds:Long):Boolean{
        if(milliseconds!=null && milliseconds>0){
            return true
        }else{
            return false
        }
    }

    @ExperimentalTime
    fun is_in_alert_zone(alerted:String):Boolean{
        if(alerted!=null) {
            var diff_time = DateTimeUtils.getDateDiff(
                DateTimeUtils.getUTCDate(DateTimeUtils.DEFAULT_FORMAT),
                alerted
            )
            if(diff_time>0){
                return false
            }else {
                return true
            }
        }else{
            return true
        }
    }

    @ExperimentalTime
    fun is_in_alertZone_not_expired(alerted:String,expired:String):Boolean{
        return is_in_alertZone_not_expired(alerted,expired,DateTimeUtils.getUTCDate(DateTimeUtils.DEFAULT_FORMAT))
    }
    @ExperimentalTime
    fun is_in_alertZone_not_expired(alerted:String,expired:String,current:String):Boolean{
        if(alerted!=null && expired!=null) {
            var diff_time_alert = DateTimeUtils.getDateDiff(
                current,
                alerted
            )
            var diff_time_expired = DateTimeUtils.getDateDiff(
                current,
                expired
            )
            if(diff_time_alert>0){
                return false
            }else{
                if(diff_time_expired>0){
                    return true
                }else{
                    return false
                }
            }
        }else{
            return true
        }
    }

    private fun showProgress(mins:Long,secs:Long,holder:OrderAdapter.ViewHolder){
        if(mins>=4){
            holder.ll_5.visibility=View.VISIBLE
            holder.ll_4.visibility=View.GONE
            holder.ll_3.visibility=View.GONE
            holder.ll_2.visibility=View.GONE
            holder.ll_1.visibility=View.GONE
        }else if(mins.equals(3L)){
            holder.ll_5.visibility=View.GONE
            holder.ll_4.visibility=View.VISIBLE
            holder.ll_3.visibility=View.GONE
            holder.ll_2.visibility=View.GONE
            holder.ll_1.visibility=View.GONE
        }else if(mins.equals(2L)){
            holder.ll_5.visibility=View.GONE
            holder.ll_4.visibility=View.GONE
            holder.ll_3.visibility=View.VISIBLE
            holder.ll_2.visibility=View.GONE
            holder.ll_1.visibility=View.GONE
        }else if(mins.equals(1L)){
            holder.ll_5.visibility=View.GONE
            holder.ll_4.visibility=View.GONE
            holder.ll_3.visibility=View.GONE
            holder.ll_2.visibility=View.VISIBLE
            holder.ll_1.visibility=View.GONE
        }else if(mins.equals(0L) && secs>0){
            holder.ll_5.visibility=View.GONE
            holder.ll_4.visibility=View.GONE
            holder.ll_3.visibility=View.GONE
            holder.ll_2.visibility=View.GONE
            holder.ll_1.visibility=View.VISIBLE
        }else if(mins.equals(0L) && secs.equals(0L)){
            holder.ll_5.visibility=View.GONE
            holder.ll_4.visibility=View.GONE
            holder.ll_3.visibility=View.GONE
            holder.ll_2.visibility=View.GONE
            holder.ll_1.visibility=View.GONE
        }
    }

    private fun order_Accept_Expired(order:Orders){
        val timer_val = map_countertimer!!.get(order.id)
        if (timer_val != null) {
            timer_val!!.cancel()
        }
        resetMapTimer()
        map_accept_expired!!.put(order.id, true)
        val result_map_accept_expired = map_accept_expired!!.get(order.id)
        if (result_map_accept_expired != null && result_map_accept_expired) {
            var datalist1: MutableList<Orders> = ArrayList<Orders>()
            for (order_item in dataList) {
                if (order.id != order_item.id) {
                    datalist1!!.add(order_item)
                }
            }
            setData(datalist1)
        }
    }

    private fun setExpiredButton(holder: OrderAdapter.ViewHolder){
        if (!holder.bt_expired.isVisible) {
            holder.bt_expired.visibility = View.VISIBLE
            holder.bt_accept.visibility = View.GONE
            holder.iv_notify.visibility = View.GONE
            showProgress(0,0,holder)
            holder.tv_order_expired.text=""
        }
    }
}