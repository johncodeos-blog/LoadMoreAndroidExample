package com.example.loadmoreexample.GridRecyclerView

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loadmoreexample.Constant.VIEW_TYPE_ITEM
import com.example.loadmoreexample.Constant.VIEW_TYPE_LOADING
import com.example.loadmoreexample.OnLoadMoreListener
import com.example.loadmoreexample.R
import com.example.loadmoreexample.RecyclerViewLoadMoreScroll
import kotlinx.android.synthetic.main.activity_grid_recycler_view.*
import java.util.*
import kotlin.collections.ArrayList


class GridRecyclerViewActivity : AppCompatActivity() {

    lateinit var itemsCells: ArrayList<Int?>
    lateinit var loadMoreItemsCells: ArrayList<Int?>
    lateinit var adapterGrid: Items_GridRVAdapter
    lateinit var scrollListener: RecyclerViewLoadMoreScroll
    lateinit var mLayoutManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_recycler_view)

        //** Set the data for our ArrayList
        setItemsData()

        //** Set the adapterLinear of the RecyclerView
        setAdapter()

        //** Set the Layout Manager of the RecyclerView
        setRVLayoutManager()

        //** Set the scrollListerner of the RecyclerView
        setRVScrollListener()
    }


    private fun setItemsData() {
        itemsCells = ArrayList()
        for (i in 0..41) {
            val rnd = Random()
            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            itemsCells.add(color)
        }
    }

    private fun setAdapter() {
        adapterGrid = Items_GridRVAdapter(itemsCells)
        adapterGrid.notifyDataSetChanged()
        items_grid_rv.adapter = adapterGrid
    }

    private fun setRVLayoutManager() {
        mLayoutManager = GridLayoutManager(this, 3)
        items_grid_rv.layoutManager = mLayoutManager
        items_grid_rv.setHasFixedSize(true)
        items_grid_rv.adapter = adapterGrid
        (mLayoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapterGrid.getItemViewType(position)) {
                    VIEW_TYPE_ITEM -> 1
                    VIEW_TYPE_LOADING -> 3 //number of columns of the grid
                    else -> -1
                }
            }
        }
    }

    private fun setRVScrollListener() {
        scrollListener = RecyclerViewLoadMoreScroll(mLayoutManager as GridLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                LoadMoreData()
            }
        })

        items_grid_rv.addOnScrollListener(scrollListener)
    }

    private fun LoadMoreData() {
        //Add the Loading View
        adapterGrid.addLoadingView()
        //Create the loadMoreItemsCells Arraylist
        loadMoreItemsCells = ArrayList()
        //Get the number of the current Items of the main Arraylist
        val start = adapterGrid.itemCount
        //Load 16 more items
        val end = start + 16
        //Use Handler if the items are loading too fast.
        //If you remove it, the data will load so fast that you can't even see the LoadingView
        Handler().postDelayed({
            for (i in start..end) {
                val rnd = Random()
                val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                //Get data and add them to loadMoreItemsCells ArrayList
                loadMoreItemsCells.add(color)
            }
            //Remove the Loading View
            adapterGrid.removeLoadingView()
            //We adding the data to our main ArrayList
            adapterGrid.addData(loadMoreItemsCells)
            //Change the boolean isLoading to false
            scrollListener.setLoaded()
            //Update the recyclerView in the main thread
            items_grid_rv.post {
                adapterGrid.notifyDataSetChanged()
            }
        }, 3000)

    }
}
