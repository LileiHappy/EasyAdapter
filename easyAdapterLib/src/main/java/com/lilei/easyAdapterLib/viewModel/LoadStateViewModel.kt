package com.lilei.easyAdapterLib.viewModel

import android.content.Context
import android.util.Log
import android.view.View
import com.lilei.easyAdapterLib.BuildConfig
import com.lilei.easyAdapterLib.R
import com.lilei.easyAdapterLib.type.LoadStateTyper
import com.lilei.easyAdapterLib.viewHolder.FooterViewHolder
import com.lilei.widgets.LoadStateView

/**
 * 加载视图和状态关联的viewmodel，主要是根据加载状态管理加载视图
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
open class LoadStateViewModel constructor(context: Context?, footerViewHolder: FooterViewHolder){

    /**日志tag*/
    private val TAG = "LoadStateViewModel"

    /**上下文*/
    private var mContext: Context? = null
    /**加载视图*/
    private var mLoadStateView: LoadStateView? = null
    /**当前加载状态值*/
    private var mLoadState = 0

    init {
        if (context == null) {
            if (BuildConfig.DEBUG) {
                throw IllegalStateException("context is null!")
            } else {
                Log.e(TAG, "context is null!")
            }
        }
        mContext = context
        val loadStateView = footerViewHolder.itemView
        // 实例化加载视图
        if (loadStateView is LoadStateView) {
            mLoadStateView = loadStateView as LoadStateView
        }
    }

    /**
     * 展示状态改变后视图
     * @param hint 提示文案
     */
    private fun showChangeView(hint: String?) {
        mLoadStateView?.setHint(hint)
        mLoadStateView?.setProgressBarVisible(
            if (isProgressShow()) View.VISIBLE else View.GONE)
        mLoadStateView?.setLineVisible(
            if (isLineShow()) View.VISIBLE else View.GONE
        )
    }

    /**
     * 获取提示文案
     */
    private fun getLoadHint(): String? {
        var loadHint : String? = null
        when (mLoadState) {
            0 -> loadHint = mContext?.getString(R.string.easy_adapter_load_normal)
            1 -> loadHint = mContext?.getString(R.string.easy_adapter_load_loading)
            2 -> loadHint = mContext?.getString(R.string.easy_adapter_load_load_fail)
            3 -> loadHint = mContext?.getString(R.string.easy_adapter_load_no_more)
        }
        return loadHint
    }

    /**
     * 判断进度条是否展示
     */
    private fun isProgressShow(): Boolean {
        return mLoadState == 1
    }

    /**
     * 判断分割线是否展示
     */
    private fun isLineShow(): Boolean {
        return mLoadState < 3
    }

    /**
     * 设置加载状态
     * @param loadState 加载状态
     */
    public fun setLoadState(loadState: Int) {
        mLoadState = loadState
        showChangeView(getLoadHint())
    }

    /**
     * 设置加载状态
     * @param loadStateType 加载状态类型
     */
    public fun setLoadState(loadStateType: LoadStateTyper) {
        mLoadState = loadStateType.getLoadState()
        showChangeView(getLoadHint())
    }

    /**
     * 获取当前加载状态值
     */
    public fun getLoadState(): Int {
        return mLoadState
    }

    /**
     * 获取加载视图
     */
    public fun getLoadView(): View? {
        return mLoadStateView
    }
 }