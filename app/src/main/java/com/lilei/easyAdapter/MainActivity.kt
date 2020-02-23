package com.lilei.easyAdapter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lilei.easyAdapterLib.adapter.SingleAdapter

/**
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
class MainActivity : AppCompatActivity() {
    /** 单一样式 */
    private var rv_single: RecyclerView? = null
    /** 多样式 */
    private var rv_multiple: RecyclerView? = null

    private var mSingleData: MutableList<String?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun findViews() {
        rv_single = findViewById(R.id.rv_single)
        rv_multiple = findViewById(R.id.rv_multiple)
    }

    private fun init() {
        mSingleData = ArrayList()
        rv_single?.layoutManager = LinearLayoutManager(this)
        rv_single?.adapter = SingleAdapter<String>(this, R.layout.adapter_single_text)
    }
}
