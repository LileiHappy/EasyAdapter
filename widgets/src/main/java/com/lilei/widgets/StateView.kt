package com.lilei.widgets

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * 状态视图控件，用于展示正在加载数据、网络出错、没有网络或后端服务挂掉等状态对应的视图
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 * // todo 将ImageView换成LottieAnimationView，这样可以完成复杂的动画
 */
class StateView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
    LinearLayout(context, attrs, defStyleAttr) {
    /**日志tag*/
    private val TAG = "StateView"
    /**日志信息*/
    private val MSG = "context is null!"

    /**图片默认宽度*/
    private val DEFAULT_PIC_WIDTH = ViewGroup.LayoutParams.WRAP_CONTENT
    /**图片默认高度*/
    private val DEFAULT_PIC_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT
    /**提示文案文本默认大小*/
    private val DEFAULT_HINT_TEXT_SIZE = 18f
    /**状态或动作提示文案文本默认大小*/
    private val DEFAULT_STATE_TEXT_SIZE = 16f
    /**重加载文本默认大小*/
    private val DEFAULT_RELOAD_TEXT_SIZE = 15f
    /**默认文本颜色*/
    private val DEFAULT_TEXT_COLOR = Color.BLACK
    /**重加载按钮默认宽度*/
    private val DEFAULT_RELOAD_HINT_WIDTH = 160
    /**重加载按钮默认高度*/
    private val DEFAULT_RELOAD_HINT_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT
    /**重加载按钮文本与控件边界默认上下间距*/
    private val DEFAULT_RELOAD_HINT_PADDING_TB = 9
    /**展示默认值，默认都不展示*/
    private val DEFAULT_VISIBILITY= View.GONE

    /**上下文*/
    private var mContext: Context? = null
    /**提示图片*/
    private var mSrc: Drawable? = null
    private var mPicWidth = DEFAULT_PIC_WIDTH
    private var mPicHeight = DEFAULT_PIC_HEIGHT
    private var mPicVisible = DEFAULT_VISIBILITY

    /**进度条展示值*/
    private var mProgressBarVisibility = DEFAULT_VISIBILITY

    /**提示文本相关*/
    private var mHint: String? = null
    private var mHintTextSize = DEFAULT_HINT_TEXT_SIZE
    private var mHintTextColor = DEFAULT_TEXT_COLOR
    private var mHintVisibility = DEFAULT_VISIBILITY

    /**状态相关*/
    private var mStateHint: String? = null
    private var mStateHintTextSize = DEFAULT_STATE_TEXT_SIZE
    private var mStateHintTextColor = DEFAULT_TEXT_COLOR
    private var mStateHintVisibility = DEFAULT_VISIBILITY

    /**重加载相关*/
    private var mReloadHint: String? = null
    private var mReloadHintTextSize = DEFAULT_RELOAD_TEXT_SIZE
    private var mReloadHintTextColor = DEFAULT_TEXT_COLOR
    private var mReloadHintWidth = DEFAULT_RELOAD_HINT_WIDTH
    private var mReloadHintHeight = DEFAULT_RELOAD_HINT_HEIGHT
    private var mReloadHintPaddingTb = DEFAULT_RELOAD_HINT_PADDING_TB
    private var mReloadHintVisibility = DEFAULT_VISIBILITY

    /**提示图片控件*/
    private var iv_hint: ImageView? = null
    /**加载进度条*/
    private var pb_loading: ProgressBar? = null
    /**提示文本控件*/
    private var tv_hint: TextView? = null
    /**状态或动作文本*/
    private var tv_stateHint: TextView? = null
    /**重加载按钮*/
    private var tv_reload: TextView? = null

    init {
        mContext = context
        inits(context, attrs, defStyleAttr, defStyleRes)
    }

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)

    /**
     * 初始化
     */
    private fun inits(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (checkContext()) {
            obtainStyleAttribute(context, attrs, defStyleAttr, defStyleRes)
            findViews()
            showStyle()
        }
    }

    /**
     * 校验上下文
     */
    private fun checkContext(): Boolean {
        if (mContext == null) {
            handleException(IllegalArgumentException(MSG))
            return false
        }
        return true
    }

    /**
     * 异常处理
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
     * 校验可见值
     * @param visibility 可见值
     */
    private fun checkVisibility(visibility: Int): Boolean {
        return visibility == View.VISIBLE || visibility == View.GONE || visibility == View.INVISIBLE
    }

    /**
     * 加载属性
     */
    private fun obtainStyleAttribute(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val types: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.StateView, defStyleAttr, defStyleRes)
        // 图片
        mSrc = types.getDrawable(R.styleable.StateView_src)
        mPicWidth = types.getDimensionPixelSize(R.styleable.StateView_picWidth, DEFAULT_PIC_WIDTH)
        mPicHeight = types.getDimensionPixelSize(R.styleable.StateView_picHeight, DEFAULT_PIC_HEIGHT)
        mPicVisible = types.getInt(R.styleable.StateView_picVisible, DEFAULT_VISIBILITY)

        // 进度条
        mProgressBarVisibility = types.getInt(R.styleable.StateView_state_progressBarVisible, DEFAULT_VISIBILITY)

        // 提示文本
        mHint = types.getString(R.styleable.StateView_state_hint)
        mHintTextSize = types.getFloat(R.styleable.StateView_hintTextSize, DEFAULT_HINT_TEXT_SIZE)
        mHintTextColor = types.getColor(R.styleable.StateView_hintTextColor, DEFAULT_TEXT_COLOR)
        mHintVisibility = types.getInt(R.styleable.StateView_hintVisible, DEFAULT_VISIBILITY)

        // 状态或动作文本
        mStateHint = types.getString(R.styleable.StateView_loadState)
        mStateHintTextSize = types.getFloat(R.styleable.StateView_loadStateTextSize, DEFAULT_STATE_TEXT_SIZE)
        mStateHintTextColor = types.getColor(R.styleable.StateView_loadStateTextColor, DEFAULT_TEXT_COLOR)
        mStateHintVisibility = types.getInt(R.styleable.StateView_loadStateVisible, DEFAULT_VISIBILITY)

        // 重加载
        mReloadHintWidth = types.getDimensionPixelSize(R.styleable.StateView_reloadWidth, DEFAULT_RELOAD_HINT_WIDTH)
        mReloadHintHeight = types.getDimensionPixelSize(R.styleable.StateView_reloadHeight, DEFAULT_RELOAD_HINT_HEIGHT)
        mReloadHint = types.getString(R.styleable.StateView_reload)
        mReloadHintTextSize = types.getFloat(R.styleable.StateView_reloadTextSize, DEFAULT_RELOAD_TEXT_SIZE)
        mReloadHintTextColor = types.getColor(R.styleable.StateView_reloadTextColor, DEFAULT_TEXT_COLOR)
        mReloadHintPaddingTb = types.getDimensionPixelSize(R.styleable.StateView_reloadPaddingTB, DEFAULT_RELOAD_HINT_PADDING_TB)
        mReloadHintVisibility = types.getInt(R.styleable.StateView_reloadVisible, DEFAULT_VISIBILITY)
        types.recycle()
    }

    /**
     * 渲染视图
     */
    private fun findViews() {
        LayoutInflater.from(mContext).inflate(R.layout.widget_state_layout, this, true)
        iv_hint = findViewById(R.id.widget_hint_iv)
        pb_loading = findViewById(R.id.widget_loading_pb)
        tv_hint = findViewById(R.id.widget_hint_tv)
        tv_stateHint = findViewById(R.id.widget_state_hint_tv)
        tv_reload = findViewById(R.id.widget_reload_tv)
    }

    /**
     * 展示相应视图
     */
    private fun showStyle() {
        // 状态图片
        iv_hint?.layoutParams = LayoutParams(mPicWidth, mPicHeight)
        if (mSrc != null) {
            iv_hint?.setImageDrawable(mSrc)
        }
        iv_hint?.visibility = mPicVisible

        pb_loading?.visibility = mProgressBarVisibility

        // 提示文案
        if (!TextUtils.isEmpty(mHint)) {
            tv_hint?.text = mHint
        }
        tv_hint?.setTextSize(TypedValue.COMPLEX_UNIT_SP, mHintTextSize)
        tv_hint?.setTextColor(mHintTextColor)
        tv_hint?.visibility = mHintVisibility

        // 状态或动作文案
        if (!TextUtils.isEmpty(mStateHint)) {
            tv_stateHint?.text = mStateHint
        }
        tv_stateHint?.setTextSize(TypedValue.COMPLEX_UNIT_SP, mStateHintTextSize)
        tv_stateHint?.setTextColor(mStateHintTextColor)
        tv_stateHint?.visibility = mStateHintVisibility

        // 重加载
        if (!TextUtils.isEmpty(mReloadHint)) {
            tv_reload?.text = mReloadHint
            var params = tv_reload?.layoutParams
            params?.height = (if (mReloadHintHeight <= 0) params?.height else mReloadHintHeight)?.plus(mReloadHintPaddingTb)
            tv_reload?.layoutParams = params
        }
        tv_reload?.setTextSize(TypedValue.COMPLEX_UNIT_SP, mReloadHintTextSize)
        tv_reload?.setTextColor(mReloadHintTextColor)
        tv_reload?.gravity = Gravity.CENTER
        tv_reload?.visibility = mReloadHintVisibility
    }

    /**
     * 设置提示图片
     * @param 图片id
     */
    public fun setHintPicRes(@DrawableRes hintPic: Int) {
        if (checkContext()) {
            try {
                val drawable = mContext!!.resources!!.getDrawable(hintPic)
                if (drawable != mSrc) {
                    iv_hint?.setImageDrawable(drawable)
                    mSrc = drawable
                }
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置提示图片
     * @param 图片
     */
    public fun setHintPicDrawable(drawable: Drawable?) {
        if (drawable == null || drawable == mSrc) {
            return
        }
        iv_hint?.setImageDrawable(drawable)
        mSrc = drawable
    }

    /**
     * 设置提示图片宽度
     * @param width 宽度
     */
    public fun setHintPicWidth(width: Int) {
        if (mPicWidth != width) {
            val params = iv_hint?.layoutParams
            params?.width = width
            iv_hint?.layoutParams = params
            mPicWidth = width
        }
    }

    /**
     * 设置提示图片宽度
     * @param width 宽度id
     */
    public fun setHintPicWidthRes(@DimenRes widthRes: Int) {
        if (checkContext()) {
            try {
                val width = mContext!!.resources.getDimensionPixelSize(widthRes)
                setHintPicWidth(width)
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置提示图片高度
     * @param width 高度
     */
    public fun setHintPicHeight(height: Int) {
        if (mPicHeight != height) {
            val params = iv_hint?.layoutParams
            params?.height = height
            iv_hint?.layoutParams = params
            mPicHeight = height
        }
    }

    /**
     * 设置提示图片高度
     * @param width 高度id
     */
    public fun setHintPicHeightRes(@DimenRes heightRes: Int) {
        if (checkContext()) {
            try {
                val height = mContext!!.resources.getDimensionPixelSize(heightRes)
                setHintPicHeight(height)
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置图片展示值
     * @param visibility 可见值
     */
    public fun setHintPicVisibility(visibility: Int) {
        if (!checkVisibility(visibility) || visibility == mPicVisible) {
            return
        }
        iv_hint?.visibility = visibility
        mPicVisible = visibility
    }

    /**
     * 设置进度条展示值
     * @param visibility 展示值
     */
    public fun setProgressBarVisibility(visibility: Int) {
        if (!checkVisibility(visibility) || visibility == mProgressBarVisibility) {
            return
        }
        pb_loading?.visibility = visibility
        mProgressBarVisibility = visibility
    }

    /**
     * 设置提示文案
     * @param hint 提示文案
     */
    public fun setHint(hint: String?) {
        if (hint == null || TextUtils.equals(hint, mHint)) {
            return
        }
        tv_hint?.text = hint
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
                if (!TextUtils.equals(hint, mHint)) {
                    tv_hint?.text = hint
                    mHint = hint
                }
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置提示文本大小
     * @param textSize 文本大小
     */
    public fun setHintTextSize(textSize: Float) {
        if (mHintTextSize != textSize) {
            tv_hint?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            mHintTextSize = textSize
        }
    }

    /**
     * 设置文本颜色
     * @param color 颜色
     */
    public fun setHintTextColor(color: Int) {
        if (mHintTextColor != color) {
            tv_hint?.setTextColor(color)
            mHintTextColor = color
        }
    }

    /**
     * 设置文本颜色
     * @param colorRes 颜色id
     */
    public fun setHintTextColorRes(@ColorRes colorRes: Int) {
        if (checkContext()) {
            try {
                val color = mContext!!.resources.getColor(colorRes)
                if (mHintTextColor != color) {
                    tv_hint?.setTextColor(color)
                    mHintTextColor = color
                }
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置文本可见值
     * @param visibility 可见值
     */
    public fun setHintVisibility(visibility: Int) {
        if (!checkVisibility(visibility) || mHintVisibility == visibility) {
            return
        }
        tv_hint?.visibility = visibility
        mHintVisibility = visibility
    }

    /**
     * 设置状态提示文案
     * @param stateHint 状态提示文案
     */
    public fun setStateHint(stateHint: String?) {
        if (stateHint == null || TextUtils.equals(mStateHint, stateHint)) {
            return
        }
        tv_stateHint?.text = stateHint
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
                if (!TextUtils.equals(mStateHint, stateHint)) {
                    tv_stateHint?.text = stateHint
                    mStateHint = stateHint
                }
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置状态文本大小
     * @param textSize 文本大小
     */
    public fun setStateHintTextSize(textSize: Float) {
        if (mStateHintTextSize != textSize) {
            tv_stateHint?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            mStateHintTextSize = textSize
        }
    }

    /**
     * 设置文本颜色
     * @param textColor 颜色
     */
    public fun setStateHintTextColor(textColor: Int) {
        if (mStateHintTextColor != textColor) {
            tv_stateHint?.setTextColor(textColor)
            mStateHintTextColor = textColor
        }
    }

    /**
     * 设置文本颜色
     * @param textColorRes 颜色id
     */
    public fun setStateHintTextColorRes(@ColorRes textColorRes: Int) {
        if (checkContext()) {
            try {
                val textColor = mContext!!.resources.getColor(textColorRes)
                if (mStateHintTextColor != textColor) {
                    tv_stateHint?.setTextColor(textColor)
                    mStateHintTextColor = textColor
                }
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置状态文本可见值
     * @param visibility 可见值
     */
    public fun setStateHintVisibility(visibility: Int) {
        if (!checkVisibility(visibility) || visibility == mStateHintVisibility) {
            return
        }
        tv_stateHint?.visibility = visibility
        mStateHintVisibility = visibility
    }

    /**
     * 设置重加载文案
     * @param reloadHint 文案
     */
    public fun setReloadHint(reloadHint: String?) {
        if (reloadHint == null || TextUtils.equals(mReloadHint, reloadHint)) {
            return
        }
        tv_reload?.text = reloadHint
        mReloadHint = reloadHint
    }

    /**
     * 设置重加载文案
     * @param reloadHintRes 文案id
     */
    public fun setReloadHintRes(@StringRes reloadHintRes: Int) {
        if (checkContext()) {
            try {
                val reloadHint = mContext!!.getString(reloadHintRes)
                if (!TextUtils.equals(reloadHint, mReloadHint)) {
                    tv_reload?.text = reloadHint
                    mReloadHint = reloadHint
                }
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置重加载按钮宽度
     * @param width 宽度
     */
    public fun setReloadWidth(width: Int) {
        if (mReloadHintWidth != width) {
            tv_reload?.width = width
            mReloadHintWidth = width
        }
    }

    /**
     * 设置重加载按钮宽度
     * @param widthRes 宽度id
     */
    public fun setReloadWidthRes(@DimenRes widthRes: Int) {
        if (checkContext()) {
            try {
                val width = mContext!!.resources.getDimensionPixelSize(widthRes)
                if (mReloadHintWidth != width) {
                    tv_reload?.width = width
                    mReloadHintWidth = width
                }
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置重加载高度
     * @param height 高度
     */
    public fun setReloadHeight(height: Int) {
        if (mReloadHintHeight != height) {
            tv_reload?.height = height
            mReloadHintHeight = height
        }
    }

    /**
     * 设置重加载高度
     * @param heightRes 高度id
     */
    public fun setReloadHeightRes(@DimenRes heightRes: Int) {
        if (checkContext()) {
            try {
                val height = mContext!!.resources.getDimensionPixelSize(heightRes)
                if (mReloadHintHeight != height) {
                    tv_reload?.height = height
                    mReloadHintHeight = height
                }
            } catch (e: Resources.NotFoundException) {
                handleException(e)
            }
        }
    }

    /**
     * 设置重加载控件的内间距
     * @param paddingTb 内间距
     */
    public fun setReloadPaddingTb(paddingTb: Int) {
        if (mReloadHintPaddingTb == paddingTb || tv_reload == null) {
            return
        }
        tv_reload!!.setPadding(tv_reload!!.paddingLeft, paddingTb, tv_reload!!.paddingRight, paddingTb)
        mReloadHintPaddingTb = paddingTb
    }

    /**
     * 设置重加载控件的内间距
     * @param paddingTbRes 内间距id
     */
    public fun setReloadPaddingTbRes(@DimenRes paddingTbRes: Int) {
        if (!checkContext() || tv_reload == null) {
            return
        }
        try {
            val paddingTb = mContext!!.resources.getDimensionPixelSize(paddingTbRes)
            if (mReloadHintPaddingTb != paddingTb) {
                tv_reload!!.setPadding(tv_reload!!.paddingLeft, paddingTb, tv_reload!!.paddingRight, paddingTb)
                mReloadHintPaddingTb = paddingTb
            }
        } catch (e: Resources.NotFoundException) {
            handleException(e)
        }
    }

    /**
     * 设置重加载可见值
     */
    public fun setReloadVisibility(visibility: Int) {
        if (!checkVisibility(visibility) || mReloadHintVisibility == visibility) {
            return
        }
        tv_reload?.visibility = visibility
        mReloadHintVisibility = visibility
    }

    /**
     * 设置重加载点击监听
     * @param listener 监听对象
     */
    public fun setOnReloadClickListener(listener: OnClickListener) {
        if (listener != null) {
            tv_reload?.setOnClickListener(listener)
        }
    }
}