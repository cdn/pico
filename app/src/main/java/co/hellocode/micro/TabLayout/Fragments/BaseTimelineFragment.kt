package co.hellocode.micro.TabLayout.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.hellocode.micro.*
import co.hellocode.micro.NewPost.NewPostActivity
import co.hellocode.micro.Utils.NEW_POST_REQUEST_CODE
import co.hellocode.micro.Utils.PREFS_FILENAME
import co.hellocode.micro.Utils.TOKEN
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.baselayout_timeline.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

open class BaseTimelineFragment: Fragment() {
    open var url = "https://micro.blog/posts/all"
    open var title = "Timeline"
    open lateinit var linearLayoutManager: LinearLayoutManager
    open lateinit var adapter: TimelineRecyclerAdapter
    open var posts = ArrayList<Post>()
    open lateinit var refresh: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.baselayout_timeline, container, false)
        this.linearLayoutManager = LinearLayoutManager(context)
        view.recyclerView.layoutManager = this.linearLayoutManager
        this.adapter = TimelineRecyclerAdapter(this.posts)
        view.recyclerView.adapter = this.adapter
        this.refresh = view.refresher
        this.refresh.setOnRefreshListener { refresh() }
        view.fab.setOnClickListener {
            val intent = Intent(this.activity, NewPostActivity::class.java)
            startActivityForResult(intent, NEW_POST_REQUEST_CODE)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        Log.i("TimelineFragment", "onStart")
        if (this.posts.count() == 0){
            initialLoad()
        }
    }

    open fun initialLoad() {
        Log.i("BaseTimeline", "initialLoad")
        this.refresh.isRefreshing = true
        refresh()
    }

    open fun refresh() {
        Log.i("BaseTimeline", "refresh")
        getTimeline()
    }

    fun prefs() : SharedPreferences? {
        return this.activity?.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    }

    open fun getTimeline(url: String = this.url) {
        Log.i("BaseTimeline", "getTimeline url: $url")
        val rq = object : JsonObjectRequest(
                this.url,
                null,
                Response.Listener<JSONObject> { response ->
                    Log.i("BaseTimelineFrag", "resp: $response")
                    val items = response["items"] as JSONArray
                    this.posts.clear()
                    for (i in 0 until items.length()) {
                        val item = items[i] as JSONObject
                        this.posts.add(Post(item))
                    }
                    this.adapter.notifyDataSetChanged()
                    this.refresh.isRefreshing = false
                },
                Response.ErrorListener { error ->
                    Log.i("BaseTimelineFrag", "err: $error")
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        Log.i("BaseTimelineFrag", "error is: ${error.networkResponse} msg: ${error.networkResponse.data.toString()}")
                    }
                    this.refresh.isRefreshing = false

                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                Log.i("BaseTimelineFrag", "getHeaders")
                val headers = HashMap<String, String>()
                val prefs = prefs()
                val token: String? = prefs?.getString(TOKEN, null)
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        val queue = Volley.newRequestQueue(this.activity)
        queue.add(rq)
    }
}