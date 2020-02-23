package com.lilei.widgets

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes

/**
 * 加载视图
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
open class LoadStateView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
    : LinearLayout(context, attrs, defStyleAttr) {
    /**日志Tag*/
    private val TAG = "LoadStateView"
    /**调试版标志*/
    private val DEBUG = BuildConfig.DEBUG

    /**线条默认颜色*/
    private val DEFAULT_LINE_COLOR = Color.GRAY
    /**线条默认高度*/
    private val DEFAULT_LINE_HEIGHT = 1
    /**线条默认可见值*/
    private val DEFAULT_LINE_VISIBLE = View.GONE
    /**提示文案可见标志*/
    private val DEFAULT_HINT_SHOW = true
    /**文本颜色*/
    private val DEFAULT_TEXT_COLOR = Color.BLACK
    /**文本大小*/
    private val DEFAULT_TEXT_SIZE = 16f
    /**文本单行展示标志*/
    private val DEFAULT_SINGLE_LINE = true
    /**进度条大小：默认为内容填充*/
    private val DEFAULT_PROGRESS_BAR_SIZE = ViewGroup.LayoutParams.WRAP_CONTENT
    /**进度条可见值*/
    private val DEFAULT_PROGRESS_VISIBLE = View.GONE

    /**当前线条颜色*/
    private var mLineColor = 0
    /**当前线条高度*/
    private var mLineHeight = 0
    /**当前线条可见值*/
    private var mLineVisible = 0

    /**文本展示标志*/
    private var isHintShow = true
    /**提示文案*/
    private var mHint: String? = null
    /**文本颜色*/
    private var mTextColor = 0
    private var mTextSize = 0f
    /**单行展示标志*/
    private var isSingleLine = true

    /**进度条宽度*/
    private var mProgressBarWidth = 0
    private var mProgressBarHeight = 0
    /**可见值*/
    private var mProgressBarVisible = 0

    /**上下文*/
    private var mContext: Context? = null
    /**进度条*/
    private var pb_progress: ProgressBar? = null
    /**提示文案对应的文本*/
    private var tv_load_hint: TextView? = null
    /**左分割线*/
    private var line_left: View? = null
    /**右分割线*/
    private var line_right: View? = null

    init {
        inits(context, attrs, defStyleAttr, defStyleRes)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr,
        0)

    /**
     * 获取属性和样式
     */
    private fun obtainStyleAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val types: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadStateView,
            defStyleAttr, defStyleRes)
        mLineColor = types.getColor(R.styleable.LoadStateView_lineColor, DEFAULT_LINE_COLOR)
        mLineHeight =
            types.getDimensionPixelSize(R.styleable.LoadStateView_lineHeight, DEFAULT_LINE_HEIGHT)
        mLineVisible = types.getInt(R.styleable.LoadStateView_lineVisible, DEFAULT_LINE_VISIBLE)
        isHintShow = types.getBoolean(R.styleable.LoadStateView_hintShow, DEFAULT_HINT_SHOW)
        mHint = types.getString(R.styleable.LoadStateView_hint)
        mTextColor = types.getColor(R.styleable.LoadStateView_textColor, DEFAULT_TEXT_COLOR)
        mTextSize = types.getFloat(R.styleable.LoadStateView_textSize, DEFAULT_TEXT_SIZE)
        isSingleLine = types.getBoolean(R.styleable.LoadStateView_singleLine, DEFAULT_SINGLE_LINE)
        mProgressBarWidth = types.getDimensionPixelSize(R.styleable.LoadStateView_progressBarWidth,
            DEFAULT_PROGRESS_BAR_SIZE)
        mProgressBarHeight = types.getDimensionPixelSize(R.styleable.LoadStateView_progressBarHeight,
            DEFAULT_PROGRESS_BAR_SIZE)
        mProgressBarVisible = types.getInt(R.styleable.LoadStateView_progressBarVisible, DEFAULT_PROGRESS_VISIBLE)
        types.recycle()
    }

    /**
     * 初始化
     */
    private fun inits(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        mContext = context
        if (!checkContext()) {
            return
        }
        LayoutInflater.from(mContext).inflate(R.layout.widget_load_state_layout, this, true)
        obtainStyleAttributes(context, attrs, defStyleAttr, defStyleRes)
        initView()
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        findViews()
        showStyle()
    }

    /**
     * 获取视图控件
     */
    private fun findViews() {
        pb_progress = findViewById(R.id.widget_load_pb)
        tv_load_hint = findViewById(R.id.widget_load_hint_tv)
        line_left = findViewById(R.id.widget_line_left)
        line_right = findViewById(R.id.widget_line_right)
    }

    /**
     * 展示样式
     */
    private fun showStyle() {
        // 进度条
        if (mProgressBarWidth > 0 && mProgressBarHeight > 0) {
            var params = ViewGroup.LayoutParams(mProgressBarWidth, mProgressBarHeight)
            pb_progress?.layoutParams = params
        }
        pb_progress?.visibility = mProgressBarWidth

        // 左右分割线
        line_left?.setBackgroundColor(mLineColor)
        line_right?.setBackgroundColor(mLineColor)
        var params = line_left?.layoutParams
        params?.height = mLineHeight
        line_left?.layoutParams = params
        line_right?.layoutParams = params
        line_left?.visibility = mLineVisible
        line_right?.visibility = mLineVisible

        // 提示文案
        tv_load_hint?.setTextColor(mTextColor)
        tv_load_hint?.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
        tv_load_hint?.visibility = if (isHintShow) View.VISIBLE else View.GONE
        tv_load_hint?.text = if (TextUtils.isEmpty(mHint)) null else mHint
        tv_load_hint?.setSingleLine(isSingleLine)
//        tv_load_hint?.isSingleLine = isSingleLine
    }

    /**
     * 校验上下文
     * @return 上下文不为空标志，在调试阶段会抛出异常，方便快速发现问题并解决问题
     */
    private fun checkContext(): Boolean {
        if (mContext == null) {
            if (DEBUG) {
                throw IllegalStateException("context is null!")
            } else {
                // todo 后面换成日志工具，日志工具会有是否允许日志记录判断
                Log.e(TAG, "context is null!")
                return false
            }
        }
        return true
    }

    /**
     * 处理异常，策略是调试阶段直接抛出异常，非调试阶段则日志记录异常
     */
    private fun handleException(e: Exception) {
        if (DEBUG) {
            throw IllegalStateException(e.toString())
        } else {
            Log.e(TAG, e.toString())
        }
    }

    /**
     * 获取指定的可见值
     * @return 支持的可见值，对于不支持的使用异常处理
     */
    private fun getSupportVisible(visible: Int): Int {
        var vi = 0
        when (visible) {
            View.VISIBLE -> vi = View.VISIBLE
            View.INVISIBLE -> vi = View.INVISIBLE
            View.GONE -> vi = View.GONE
            else -> handleException(IllegalArgumentException("not support visible type!"))
        }
        return vi
    }

    //----》对外抛出的api
    /**
     * 设置线条颜色
     * @param color 颜色
     */
    public fun setLineColor(color: Int) {
        if (line_left == null || line_right == null || mLineColor == color) {
            return
        }
        line_left?.setBackgroundColor(color)
        line_right?.setBackgroundColor(color)
        mLineColor = color
    }

    /**
     * 设置线条颜色
     * @param colorRes 颜色id
     */
    public fun setLineColorRes(@ColorRes colorRes: Int) {
        if (!checkContext()) {
            return
        }
        try {
            val color = mContext!!.resources.getColor(colorRes)
            setLineColor(color)
        } catch (e: Resources.NotFoundException) {
            handleException(e)
        }
    }

    /**
     * 设置线条高度
     * @param height 高度
     */
    public fun setLineHeight(height: Int) {
        if (line_right == null || line_left == null || mLineHeight == height) {
            return
        }
        val params = line_left?.layoutParams
        params?.height = height
        line_left?.layoutParams = params
        line_right?.layoutParams = params
        mLineHeight = height
    }

    /**
     * 设置线条高度
     * @param heightRes 高度id
     */
    public fun setLineHeightRes(@DimenRes heightRes: Int) {
        if (!checkContext()) {
            return
        }
        try {
            val height = mContext!!.resources.getDimensionPixelSize(heightRes)
            setLineHeight(height)
        } catch (e: Resources.NotFoundException) {
            handleException(e)
        }
    }

    /**
     * 设置线条可见值
     * @param visible 可见值
     */
    public fun setLineVisible(visible: Int) {
        if (line_left == null || line_right == null || mLineVisible == visible) {
            return
        }
        val vi = getSupportVisible(visible)
        line_left?.visibility = vi
        line_right?.visibility = vi
        mLineVisible = vi
    }

    /**
     * 设置提示文本是否展示
     * @param isHintShow 展示标志
     */
    public fun setHintShow(isHintShow: Boolean) {
        if (tv_load_hint == null || this.isHintShow == isHintShow) {
            return
        }
        tv_load_hint?.visibility = if (isHintShow) View.VISIBLE else View.GONE
        this.isHintShow = isHintShow
    }

    /**
     * 设置提示文案
     * @param hint 提示文案
     */
    public fun setHint(hint: String?) {
        if (TextUtils.isEmpty(hint) || tv_load_hint == null) {
            return
        }
        tv_load_hint!!.text = hint
        mHint = hint
    }

    /**
     * 设置提示文案
     * @param hintRes 提示文案id
     */
    public fun setHintRes(@StringRes hintRes: Int) {
        if (!checkContext() || tv_load_hint == null) {
            return
        }
        try {
            val hint = mContext!!.getString(hintRes)
            tv_load_hint!!.text = hint
            mHint = hint
        } catch (e: Resources.NotFoundException) {
            handleException(e)
        }
    }

    /**
     * 设置文本大小
     * @param textSize 文本大小
     */
    public fun setTextSize(textSize: Float) {
        if (tv_load_hint == null || mTextSize == textSize) {
            return
        }
        tv_load_hint!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        mTextSize = textSize
    }

    /**
     * 设置文本颜色
     * @param color 颜色
     */
    public fun setTextColor(color: Int) {
        if (tv_load_hint == null || mTextColor == color) {
            return
        }
        tv_load_hint!!.setTextColor(color)
        mTextColor = color
    }

    /**
     * 设置文本颜色
     * @param colorRes 颜色id
     */
    public fun setTextColorRes(@ColorRes colorRes: Int) {
        if (!checkContext()) {
            return
        }
        try {
            val color = mContext!!.resources.getColor(colorRes)
            setTextColor(color)
        } catch (e: Resources.NotFoundException) {
            handleException(e)
        }
    }

    /**
     * 设置提示文案单行展示
     * @param isSingleLine 单行展示标志
     */
    public fun setSingleLine(isSingleLine: Boolean) {
        if (tv_load_hint == null || this.isSingleLine == isSingleLine) {
            return
        }
//        tv_load_hint!!.isSingleLine = isSingleLine
        tv_load_hint!!.setSingleLine(isSingleLine)
        this.isSingleLine = isSingleLine
    }

    /**
     * 设置进度条宽度
     * @param width 宽度
     */
    public fun setProgressBarWidth(width: Int) {
        if (pb_progress == null || mProgressBarWidth == width) {
            return
        }
        val params = pb_progress?.layoutParams
        params?.width = width
        pb_progress?.layoutParams = params
        mProgressBarWidth = width
    }

    /**
     * 设置进度条宽度
     * @param widthRes 宽度id
     */
    public fun setProgressBarWidthRes(@DimenRes widthRes: Int) {
        if (!checkContext()) {
            return
        }
        try {
            val width = mContext!!.resources.getDimensionPixelSize(widthRes)
            setProgressBarWidth(width)
        } catch (e: Resources.NotFoundException) {
            handleException(e)
        }
    }

    /**
     * 设置进度条高度
     * @param height 高度
     */
    public fun setProgressBarHeight(height: Int) {
        if (pb_progress == null || mProgressBarHeight == height) {
            return
        }
        val params = pb_progress?.layoutParams
        params?.height = height
        pb_progress?.layoutParams = params
        mProgressBarHeight = height
    }

    /**
     * 设置进度条高度
     * @param heightRes 高度id
     */
    public fun setProgressBarHeightRes(@DimenRes heightRes: Int) {
        if (!checkContext()) {
            return
        }
        try {
            val height = mContext!!.resources.getDimensionPixelSize(heightRes)
            setProgressBarHeight(height)
        } catch (e: Resources.NotFoundException) {
            handleException(e)
        }
    }

    /**
     * 设置进度条可见值
     * @param visible 可见值
     */
    public fun setProgressBarVisible(visible: Int) {
        if (pb_progress == null || visible == mProgressBarVisible) {
            return
        }
        val vi = getSupportVisible(visible)
        pb_progress!!.visibility = vi
        mProgressBarVisible = vi
    }
    //《----对外抛出的api
}