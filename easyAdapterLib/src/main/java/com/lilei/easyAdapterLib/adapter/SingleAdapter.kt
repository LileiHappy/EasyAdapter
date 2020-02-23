package com.lilei.easyAdapterLib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lilei.easyAdapterLib.type.ViewTyper
import com.lilei.easyAdapterLib.viewHolder.BaseItemViewHolder
import java.util.Arrays

/**
 * 单一样式内容和样式适配器
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
open class SingleAdapter<Any> constructor(context: Context, itemLayoutResId: Int)
    : BaseItemAdapter<Any>(context) {
    /**item布局id*/
    private var mItemLayoutResId = -1

    init {
        mItemLayoutResId = itemLayoutResId
    }

    constructor(context: Context, itemLayoutResId: Int, isAttachParent: Boolean)
            : this(context, itemLayoutResId) {
        this.isAttachParent = isAttachParent
    }

    constructor(context: Context, itemLayoutResId: Int, data: MutableList<Any?>?)
            : this(context, itemLayoutResId) {
        mData = data
    }

    constructor(context: Context, itemLayoutResId: Int, isAttachParent: Boolean,
                data: MutableList<Any?>?): this(context, itemLayoutResId, isAttachParent) {
        mData = data
    }

    /**
     * 获取item视图类型
     */
    override fun getItemViewType(position: Int): Int {
        if (mData == null || position >= mData!!.size) {
            if (isHasFooter()) {
                return ViewTyper.FOOTER.getViewType()
            }
            return 0
        }
        return ViewTyper.ITEM.getViewType()
    }

    /**
     * 创建item视图句柄
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 尾视图
        if (viewType == ViewTyper.FOOTER.getViewType()) {
            return createFooterViewHolder(parent, viewType)
        } else {
            return BaseItemViewHolder(
                LayoutInflater.from(mContext).inflate(
                    mItemLayoutResId,
                    null
                )
            )
        }
    }

    //----》对外抛出的api
    /**
     * 设置数据
     * @param data 数据
     */
    public fun setData(data: MutableList<Any?>?) {
        if (isEmptyList(data)) {
            return
        }
        mData = data
        notifyDataSetChanged()
    }

    /**
     * 设置数据
     * @param data 数据
     */
    public fun setData(data: Array<Any?>) {
        if (isEmptyArray(data)) {
            return
        }
        mData = Arrays.asList<Any?>(*data)
        notifyDataSetChanged()
    }

    /**
     * 添加一条数据
     * @param item 一条数据
     */
    public fun addItem(item: Any?) {
        if (item == null) {
            return
        }
        // 列表未实例化则实例化后使用
        mData = getListIfNullCreate()
        mData!!.add(item)
        // 通知尾部插入数据并刷新尾部视图，能使用局部刷新的就使用局部刷新，避免性能消耗
        notifyItemInserted(mData!!.size - 1)
    }

    /**
     * 在指定位置处添加一条数据
     * @param item 数据
     * @param position 位置
     */
    public fun addItemAssignPosition(item: Any?, position: Int) {
        if (item == null) {
            return
        }
        mData = getListIfNullCreate()
        var insertPosition = 0
        // 小于0则添加到头部
        if (position <= 0) {
            mData!!.add(position, item)
        } else if (position >= mData!!.size) { // 超出列表的最大上限，则添加到尾部
            // 记录添加的位置，方便后面刷新时指定刷新位置
            insertPosition = mData!!.size
            mData!!.add(item)
        } else {
            // 添加到指定的位置
            mData!!.add(position, item)
            insertPosition = position
        }
        // 通知在指定位置插入数据并刷新该位置视图
        notifyItemInserted(insertPosition)
    }

    /**
     * 批量添加数据
     * @param items 批量数据
     */
    public fun addAllItems(items: MutableList<Any?>?) {
        if (isEmptyList(items)) {
            return
        }
        mData = getListIfNullCreate()
        val position = mData!!.size
        mData!!.addAll(items!!)
        notifyItemRangeInserted(position, items!!.size)
    }

    /**
     * 批量添加数据
     * @param items 批量数据
     */
    public fun addAllItems(items: Array<Any?>?) {
        if (isEmptyArray(items)) {
            return
        }
//        addAllItems(Arrays.asList<Any?>(*items!!))
        mData = getListIfNullCreate()
        val position = mData!!.size
        mData!!.addAll(Arrays.asList<Any?>(*items!!))
        notifyItemRangeInserted(position, items!!.size)
    }

    /**
     * 在指定位置处批量添加数据
     * @param items 批量数据
     * @param position 指定位置
     */
    public fun addAllItemsAssignPosition(items: MutableList<Any?>?, position: Int) {
        if (isEmptyList(items)) {
            return
        }
        var insertPosition = 0;
        // 对于小于等于0的，则添加在头部
        if (position <= 0) {
            mData!!.addAll(items!!)
        } else if (position >= mData!!.size) { // 超出列表大小上限的，则添加在尾部
            // 记录添加的位置，方便后面刷新时指定起始刷新位置
            insertPosition = mData!!.size
            mData!!.addAll(items!!)
        } else {
            mData!!.addAll(position, items!!)
            insertPosition = position
        }
        notifyItemRangeInserted(insertPosition, items.size)
    }

    /**
     * 在指定位置处批量添加数据
     * @param items 批量数据
     * @param position 位置
     */
    public fun addAllItemsAssignPosition(items: Array<Any?>, position: Int) {
        if (isEmptyArray(items)) {
            return
        }
        addAllItemsAssignPosition(Arrays.asList(*items), position)
    }
    //《----对外抛出的api
}