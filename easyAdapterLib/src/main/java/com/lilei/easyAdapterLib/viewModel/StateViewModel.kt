package com.lilei.easyAdapterLib.viewModel

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.lilei.easyAdapterLib.BuildConfig
import com.lilei.easyAdapterLib.R
import com.lilei.easyAdapterLib.type.StateTyper
import com.lilei.widgets.StateView
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 每个页面的状态视图和状态关联的viewModel，主要是根据状态管理状态视图
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
class StateViewModel constructor(context: Context?){
    private val TAG = "StateViewModel"
    /**上下文*/
    private var mContext: Context? = null
    /**状态视图控件*/
    private var mStateView: StateView? = null

    /**当前状态*/
    private var mState = StateTyper.NORMAL.getState()
    /**提示图片*/
    private var mHintPicDrawable: Drawable? = null
    /**提示文本（相当于标题）*/
    private var mHint: String? = null
    /**状态或动作提示文本*/
    private var mStateHint: String? = null
    /**重加载提示文本*/
    private var mReloadHint:String? = null


    init {
        mContext = context
        checkContext()
    }

    constructor(context: Context?, stateView: StateView?): this(context) {
        mStateView = stateView
    }

    /**
     * 校验上下文是否为空
     */
    private fun checkContext(): Boolean {
        if (mContext == null) {
            handleException(IllegalArgumentException("context is null!"))
            return false
        }
        return true
    }

    /**
     * 处理异常
     * @param e 异常
     */
    private fun handleException(e: Exception) {
        if (BuildConfig.DEBUG) {
            throw e
        } else {
            Log.e(TAG, e.toString())
        }
    }

    /**
     * 校验状态是否是允许的
     * @param state 状态值
     */
    private fun checkSupportState(state: Int): Boolean {
        return state in 0..7
    }

    /**
     * 显示改变后的视图
     */
    private fun showChangeView() {
        // 提示图片
        mStateView?.setHintPicDrawable(getPic())
        mStateView?.setHintPicVisibility(if (isHintPicShow()) View.VISIBLE else View.GONE)

        // 进度条
        mStateView?.setProgressBarVisibility(if (isProgressBarShow()) View.VISIBLE else View.GONE)

        // 提示文本
        mStateView?.setHint(getHint())
        mStateView?.setHintVisibility(if (isHintShow()) View.VISIBLE else View.GONE)

        // 状态或动作提示文本
        mStateView?.setStateHint(getStateHint())
        mStateView?.setStateHintVisibility(if (isStateHintShow()) View.VISIBLE else View.GONE)

        // 重加载按钮
        mStateView?.setReloadHint(getReloadHint())
        mStateView?.setReloadVisibility(if (isReloadBtnShow()) View.VISIBLE else View.GONE)
    }

    /**
     * 判断是否展示状态视图
     */
    private fun isStateViewShow(): Boolean {
        return mState != 1 || mState != 3
    }

    /**
     * 判断是否展示提示图片
     */
    private fun isHintPicShow(): Boolean {
        return mState in 4..7
    }

    /**
     * 判断是否展示进度条
     */
    private fun isProgressBarShow(): Boolean {
        return mState == 0 || mState == 2
    }

    /**
     * 判断是否展示提示文本
     */
    private fun isHintShow(): Boolean {
        return isReloadBtnShow()
    }

    /**
     * 判断是否展示状态或动作文本
     */
    private fun isStateHintShow(): Boolean {
        return mState == 2 || mState in 4..7
    }

    /**
     * 判断是否展示重加载按钮
     */
    private fun isReloadBtnShow(): Boolean {
        return mState in 4..7
    }

    /**
     * 获取提示图片
     */
    private fun getPic(): Drawable? {
        var drawable = 0
        // 处理默认的情况
        when (mState) {
            // 加载失败对应的展示图片 todo 可按照项目替换掉
            4 -> drawable = R.drawable.ic_launcher_foreground
            // 未登录
            5 -> drawable = R.drawable.ic_launcher_foreground
            // 连接超时
            6 -> drawable = R.drawable.ic_launcher_foreground
            // 网络不可用或未连接
            7 -> drawable = R.drawable.ic_launcher_foreground
        }
        // 对于非默认的情况，可能会由调用方自由定制，所以使用调用方定制的图片
        return if (drawable == 0) mHintPicDrawable else ContextCompat.getDrawable(mContext!!, drawable)
    }

    /**
     * 获取提示文本
     */
    private fun getHint(): String? {
        var hintRes = 0
        when (mState) {
            // 加载失败
            4 -> hintRes = R.string.easy_adapter_state_load_fail
            // 未登录
            5 -> hintRes = R.string.easy_adapter_state_not_login
            // 连接超时
            6 -> hintRes = R.string.easy_adapter_state_connect_timeout
            // 网络不可用或未连接
            7 -> hintRes = R.string.easy_adapter_state_network_unavailable
        }
        return if (hintRes == 0) mHint else mContext!!.getString(hintRes)
    }

    /**
     * 获取状态文本
     */
    private fun getStateHint(): String? {
        var stateHintRes = 0
        when (mState) {
            2 -> stateHintRes = R.string.easy_adapter_state_hint_loading
            4 -> stateHintRes = R.string.easy_adapter_state_hint_load_fail
            5 -> stateHintRes = R.string.easy_adapter_state_hint_not_login
            6 -> stateHintRes = R.string.easy_adapter_state_connect_timeout
            7 -> stateHintRes = R.string.easy_adapter_state_hint_network_unavailable
        }
        return if (stateHintRes == 0) mStateHint else mContext!!.getString(stateHintRes)
    }

    /**
     * 获取重加载文本
     */
    private fun getReloadHint(): String? {
        var reloadHintRes = 0
        when (mState) {
            4 , 6-> reloadHintRes = R.string.easy_adapter_state_reload_reload
            5 -> reloadHintRes = R.string.easy_adapter_state_reload_login
            7 -> reloadHintRes = R.string.easy_adapter_state_reload_network_setting
        }
        return if (reloadHintRes == 0) mReloadHint else mContext!!.getString(reloadHintRes)
    }

    /**
     * 展示网络异常情况视图
     * @param e 异常
     * @param isShowToast 展示toast提示标志
     */
    private fun _showNetworkExceptionView(e: Throwable?, isShowToast: Boolean) {
        // 网络连接超时
        if (e is SocketTimeoutException) {
            // 设置为网络连接超时状态
            setState(StateTyper.CONNECT_TIMEOUT)
            // 展示toast提示
            if (isShowToast) {
                Toast.makeText(mContext?.applicationContext, "", Toast.LENGTH_SHORT).show()
            }
        } else if (e is ConnectException || e is UnknownHostException) { // 网络未连接
            // 设置为网络不可用状态
            setState(StateTyper.NETWORK_UNAVAILABLE)
            if (isShowToast) {
                Toast.makeText(mContext?.applicationContext, "", Toast.LENGTH_SHORT).duration
            }
        }
    }

    /**
     * 设置重新加载点击监听
     * @param listener 重加载点击监听
     */
    public fun setOnReloadClickListener(listener: View.OnClickListener) {
        mStateView?.setOnReloadClickListener(listener)
    }

    /**
     * 只展示网络异常视图，不展示toast提示
     * @param e 异常
     */
    public fun justShowNetworkExceptionView(e: Throwable?) {
        _showNetworkExceptionView(e, false)
    }

    /**
     * 网络异常视图和toast提示都展示
     * @param e 异常
     */
    public fun showNetworkExcetionViewAndToast(e: Throwable?) {
        _showNetworkExceptionView(e, true)
    }

    /**
     * 设置状态
     * @param state 状态
     */
    public fun setState(state: Int) {
        // 过滤掉状态没有改变时没有必要的刷新视图
        if (!checkSupportState(state) || mState == state) {
            return
        }
        mState = state
        // 需要展示状态视图
        if (isStateViewShow()) {
            // 展示状态改变后的视图
            showChangeView()
        }
    }

    /**
     * 设置状态
     * @param stateTyper 状态类型
     */
    public fun setState(stateTyper: StateTyper) {
        if (stateTyper == null) {
            return
        }
        setState(stateTyper.getState())
    }

    /**
     * 将状态视图于该状态视图和状态关联的viewModel进行绑定
     * @param stateView 关联的状态视图
     */
    public fun setStateView(stateView: StateView) {
        mStateView = stateView
    }

    /**
     * 设置提示图片
     * @param hintPicDrawable 提示图片
     */
    public fun setHintPic(hintPicDrawable: Drawable?) {
        if (hintPicDrawable == null || mHintPicDrawable == hintPicDrawable) {
            return
        }
        mHintPicDrawable = hintPicDrawable
        mStateView?.setHintPicDrawable(mHintPicDrawable)
    }

    /**
     * 设置提示图片
     * @param hintPicRes 图片id
     */
    public fun setHintPicRes(@DrawableRes hintPicRes: Int) {
        if (checkContext()) {
            try {
                val hintPic = ContextCompat.getDrawable(mContext!!, hintPicRes)
                setHintPic(hintPic)
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置提示文案
     * @param hint 提示文案
     */
    public fun setHint(hint: String) {
        if (hint == null || TextUtils.equals(hint, mHint)) {
            return
        }
        mStateView?.setHint(hint)
        mHint = hint
    }

    /**
     * 设置提示文案
     * @param hintRes 提示文案id
     */
    public fun setHintRes(@StringRes hintRes: Int) {
        if (checkContext()) {
            try {
                val hint = mContext!!.getString(hintRes)
                setHint(hint)
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置状态提示文案
     * @param stateHint 状态提示文案
     */
    public fun setStateHint(stateHint: String) {
        if (stateHint == null || TextUtils.equals(mStateHint, stateHint)) {
            return
        }
        mStateView?.setStateHint(stateHint)
        mStateHint = stateHint
    }

    /**
     * 设置状态提示文案
     * @param stateHintRes 状态提示文案id
     */
    public fun setStateHintRes(@StringRes stateHintRes: Int) {
        if (checkContext()) {
            try {
                val stateHint = mContext!!.getString(stateHintRes)
                setStateHint(stateHint)
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置重加载文案
     * @param reloadHint 重加载文案
     */
    public fun setReloadHint(reloadHint: String) {
        if (reloadHint == null || TextUtils.equals(mReloadHint, reloadHint)) {
            return
        }
        mStateView?.setReloadHint(reloadHint)
        mReloadHint = reloadHint
    }

    /**
     * 设置重加载文案
     * @param reloadHint 重加载文案id
     */
    public fun setReloadHintRes(@StringRes reloadHintRes: Int) {
        if (checkContext()) {
            try {
                val reloadHint = mContext!!.getString(reloadHintRes)
                setReloadHint(reloadHint)
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }
 }