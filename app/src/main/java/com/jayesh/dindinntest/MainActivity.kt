package com.jayesh.dindinntest

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson
import com.jayesh.dindinntest.ApiInterface.ApiInterface
import com.jayesh.dindinntest.adapters.OrderAdapter
import com.jayesh.dindinntest.orderModels.OrderWrapper
import com.jayesh.dindinntest.orderModels.Orders
import com.jayesh.dindinntest.utils.OConstants
import io.reactivex.disposables.CompositeDisposable
import com.google.gson.reflect.TypeToken
import com.jayesh.dindinntest.incredientsModels.Incredients
import com.jayesh.dindinntest.incredientsModels.IncredientsWrapper
import java.lang.reflect.Type
import android.util.DisplayMetrics
import android.view.Display
import com.jayesh.dindinntest.adapters.IncredientsMenuAdapter
import com.jayesh.dindinntest.incredientsModels.IncredientsMenu
import com.jayesh.dindinntest.incredientsModels.IncredientsMenuWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity(),OrderAdapter.Listener,IncredientsMenuAdapter.Listener {
    private var tabLayout:TabLayout?=null
    private var tabIncredientsLayout:TabLayout?=null
    private var mCompositeDisposable: CompositeDisposable? = null
    private var mOrderWrapper: OrderWrapper? = null
    private var mIncredientsWrapper: IncredientsWrapper? = null
    private var mIncredientsMenuWrapper: IncredientsMenuWrapper? = null
    private var mOrderArrayList: List<Orders>? = null
    private var mIncredientsArrayList: List<Incredients>? = null
    private var mIncredientsMenuList:List<IncredientsMenu>? =null
    private var mRecyclerViewOrders:RecyclerView?=null
    private var mRecyclerViewIncredients:RecyclerView?=null
    private var mOrderAdapter:OrderAdapter?=null
    private var mIncredientsMenuAdapter:IncredientsMenuAdapter?=null
    private var mSwipeRefreshlayout:SwipeRefreshLayout?=null
    private var mSwipeRefreshIncredientslayout:SwipeRefreshLayout?=null
    private var mRetrofit:ApiInterface?=null
    private var gson:Gson?=null
    private var mgridLayoutManager:RecyclerView.LayoutManager ?=null
    private var mgridLayoutIncredientsManager:RecyclerView.LayoutManager ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val display: Display = this.getWindowManager().getDefaultDisplay()
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = resources.displayMetrics.density
        val dpWidth = outMetrics.widthPixels / density
        val columns = Math.round(dpWidth / 300)

        mgridLayoutManager= GridLayoutManager(this,columns)
        mgridLayoutIncredientsManager= GridLayoutManager(this,columns)

        if(mOrderArrayList==null){
            mOrderArrayList=ArrayList<Orders>()
        }
        if(mIncredientsArrayList==null){
            mIncredientsArrayList=ArrayList<Incredients>()
        }

        initOrderSwipe()
        initIncredientsSwipe()
        initOrderTabLayouts()
        initIncredientsTabLayouts()
        initOrderRecycler()
        initIncredientsRecycler()

        mCompositeDisposable = CompositeDisposable()

        mRetrofit = ApiInterface.create()
        loadOrders(mRetrofit!!)
    }

    private fun initOrderSwipe() {
        mSwipeRefreshlayout = findViewById(R.id.swipe_layout)
        mSwipeRefreshlayout!!.setOnRefreshListener{
            Log.i("swipe refresh","order")
            loadOrders(mRetrofit!!)
        }
    }

    private fun initIncredientsSwipe() {
        mSwipeRefreshIncredientslayout = findViewById(R.id.swipe_incredients_layout)
        mSwipeRefreshIncredientslayout!!.setOnRefreshListener{
            Log.i("swipe refresh","incredients")
            loadIncredients(mRetrofit!!)
        }
    }

    private fun initIncredientsRecycler() {
        mIncredientsMenuList =  ArrayList<IncredientsMenu>()
        mRecyclerViewIncredients = findViewById(R.id.rv_incredients)
        mRecyclerViewIncredients!!.layoutManager = mgridLayoutIncredientsManager
        mIncredientsMenuAdapter = IncredientsMenuAdapter(mIncredientsMenuList!!,this,this)
        mRecyclerViewIncredients!!.adapter=mIncredientsMenuAdapter
        mIncredientsMenuAdapter!!.notifyDataSetChanged()
    }

    private fun initOrderRecycler() {
        mRecyclerViewOrders = findViewById(R.id.rv_orders)
        mRecyclerViewOrders!!.layoutManager = mgridLayoutManager
        mOrderAdapter = OrderAdapter(mOrderArrayList!!,this,this)
        mOrderAdapter!!.setMap(HashMap<Int,CountDownTimer>())
        mOrderAdapter!!.setMapAcceptExpired(HashMap<Int,Boolean>())
        mRecyclerViewOrders!!.adapter=mOrderAdapter
        mOrderAdapter!!.notifyDataSetChanged()
    }

    private fun initOrderTabLayouts() {
        tabLayout = findViewById(R.id.tab_layout)
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0-> {
                        Log.i("banane", "ORdr")
                        mSwipeRefreshIncredientslayout!!.visibility=View.GONE
                        mSwipeRefreshlayout!!.visibility=View.VISIBLE
                        loadOrders(mRetrofit!!)
                    }
                    1->{
                        loadIncredients(mRetrofit!!)
                        Log.i("banane", "Incred")
                        mSwipeRefreshIncredientslayout!!.visibility=View.VISIBLE
                        mSwipeRefreshlayout!!.visibility=View.GONE}
                    else->{
                        Log.i("banane", "Coucou")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    private fun initIncredientsTabLayouts() {

        if(tabIncredientsLayout!=null){
            tabIncredientsLayout!!.removeAllTabs()
        }else{
            tabIncredientsLayout=findViewById(R.id.tablayout_incredients)
        }
        for(incred in mIncredientsArrayList!!){
            tabIncredientsLayout!!.addTab(tabIncredientsLayout!!.newTab().setId(incred.category_id).setText(incred.title))
        }
        tabIncredientsLayout!!.addOnTabSelectedListener(object:OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                loadIncredientsMenu(tab!!.id)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    private fun loadOrders(mRetrofit:ApiInterface){
        mOrderAdapter!!.resetMapTimer()
        mSwipeRefreshlayout!!.setRefreshing(true)
        //mCompositeDisposable?.add(mRetrofit.getOrders().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(this::handleResponse,this::handleError))
        val jsonFileString: String? = OConstants.getJsonFromAssets(applicationContext, "BaseResponse.json")
        gson = Gson()
        val listUserType: Type = object : TypeToken<OrderWrapper>() {}.type

        val baseResponse: OrderWrapper = gson!!.fromJson<OrderWrapper>(jsonFileString, listUserType)
        handleResponse(baseResponse)
    }
    private fun loadIncredients(mRetrofit:ApiInterface){

        mSwipeRefreshIncredientslayout!!.setRefreshing(true)
        //mCompositeDisposable?.add(mRetrofit.getOrders().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(this::handleResponse,this::handleError))
        val jsonFileString: String? = OConstants.getJsonFromAssets(applicationContext, "IncredientsBaseReponse.json")
        val gson1 = Gson()
        val listUserType: Type = object : TypeToken<IncredientsWrapper>() {}.type

        val baseResponse: IncredientsWrapper = gson1!!.fromJson<IncredientsWrapper>(jsonFileString, listUserType)
        handleIncredientsResponse(baseResponse)
    }

    private fun loadIncredientsMenu(id: Int) {
        mSwipeRefreshIncredientslayout!!.setRefreshing(true)
        mCompositeDisposable?.add(mRetrofit!!.getIncredients(id.toString()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(
            Schedulers.io()).subscribe(this::handleIncredientsMenuResponse,this::handleError))
        Log.i("Clicked on ",id.toString())
        /*for(menu_item in mIncredientsArrayList!!){
            if(menu_item.category_id==id) {
                mIncredientsMenuList = menu_item.items
                mIncredientsMenuAdapter!!.setData(mIncredientsMenuList!!)
            }
        }*/
    }

    private fun handleIncredientsResponse(androidList: IncredientsWrapper) {
        mIncredientsWrapper = androidList
        if(mIncredientsWrapper!!.status!=null && mIncredientsWrapper!!.status.statusCode==200 && mIncredientsWrapper!!.status.success==true) {
            mIncredientsArrayList = mIncredientsWrapper!!.data
            initIncredientsTabLayouts()
        }else{
            Toast.makeText(this, "Error ${mOrderWrapper!!.status.message}", Toast.LENGTH_SHORT).show()
        }
       // mSwipeRefreshIncredientslayout!!.setRefreshing(false)

    }
    private fun handleIncredientsMenuResponse(androidList: IncredientsMenuWrapper) {
        mIncredientsMenuWrapper = androidList
        if(mIncredientsMenuWrapper!!.status!=null && mIncredientsMenuWrapper!!.status.statusCode==200 && mIncredientsMenuWrapper!!.status.success==true) {
            val mIncredientsMenu = mIncredientsMenuWrapper!!.data
            mIncredientsMenuAdapter!!.setData(mIncredientsMenu.items)
        }else{
            Toast.makeText(this, "Error ${mOrderWrapper!!.status.message}", Toast.LENGTH_SHORT).show()
        }
        mSwipeRefreshIncredientslayout!!.setRefreshing(false)

    }

    private fun handleResponse(androidList: OrderWrapper) {
        mOrderWrapper = androidList
        if(mOrderWrapper!!.status!=null && mOrderWrapper!!.status.statusCode==200 && mOrderWrapper!!.status.success==true) {
            mOrderArrayList = mOrderWrapper!!.data
            mOrderAdapter!!.setData(mOrderArrayList!!)
        }else{
            Toast.makeText(this, "Error ${mOrderWrapper!!.status.message}", Toast.LENGTH_SHORT).show()
        }
        mSwipeRefreshlayout!!.setRefreshing(false)

    }

    private fun handleError(error: Throwable) {
        mSwipeRefreshlayout!!.setRefreshing(false)
        Log.d(TAG, error.localizedMessage)
        Toast.makeText(this, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(order: Orders, map: HashMap<Int, CountDownTimer>?) {
        Toast.makeText(this,order.id,Toast.LENGTH_SHORT)
    }

    override fun onIncredientItemClick(android: IncredientsMenu, map: HashMap<Int, CountDownTimer>?) {
        Toast.makeText(this,android.title,Toast.LENGTH_SHORT)
    }


}