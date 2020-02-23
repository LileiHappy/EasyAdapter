package com.lilei.easyAdapterLib.adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.contains
import androidx.core.util.isEmpty
import androidx.core.util.putAll
import androidx.recyclerview.widget.RecyclerView
import com.lilei.easyAdapterLib.R
import com.lilei.easyAdapterLib.delegate.MultipleViewTyper
import com.lilei.easyAdapterLib.type.ViewTyper
import com.lilei.easyAdapterLib.viewHolder.BaseItemViewHolder
import com.lilei.easyAdapterLib.viewHolder.HeaderViewHolder
import com.lilei.easyAdapterLib.viewHolder.OtherViewHolder
import com.lilei.easyAdapterLib.viewHolder.TitleViewHolder
import java.util.Arrays

/**
 * 多样式列表内容和样式适配器
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
open class MultipleAdapter<Any> constructor(context: Context, multipleViewTyper: MultipleViewTyper?)
    : BaseItemAdapter<Any>(context) {
    /**item对应的视图类型列表，具体类型见{@link ViewTyper}*/
    private var mViewTypes: MutableList<Int>? = null
    /**视图类型与对应布局id对应的映射集合，key：视图类型，value：布局id*/
    private var mViewTypeToViewLayouts: SparseArray<Int>? = null
    /**多视图类型提供器，获取对应数据的视图类型*/
    private var mMultipleViewTyper: MultipleViewTyper? = null
    /**尾视图布局id*/
    private var mFooterLayoutResId = -1

    init {
        mViewTypes = mutableListOf()
        mMultipleViewTyper = multipleViewTyper
    }

    constructor(context: Context, multipleViewTyper: MultipleViewTyper, viewTypeToViewLayouts: SparseArray<Int>?)
            : this(context, multipleViewTyper)

    constructor(context: Context, multipleViewTyper: MultipleViewTyper, viewTypeToViewLayouts: SparseArray<Int>?, isAttachParent: Boolean): this(context, multipleViewTyper) {
        if (!isEmpty(viewTypeToViewLayouts)) {
            mViewTypeToViewLayouts = viewTypeToViewLayouts
        }
        mViewTypeToViewLayouts = getMapIfNullCreate()
        this.isAttachParent = isAttachParent
    }

    /**
     * 创建视图句柄
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 获取该视图类型对应的布局id
        val layoutId = mViewTypeToViewLayouts!!.get(viewType)
        // 渲染该布局
        val view = LayoutInflater.from(mContext).inflate(layoutId, null)
        var viewHolder: RecyclerView.ViewHolder
        when (viewType) {
            // 头视图
            ViewTyper.HEADER.getViewType() -> viewHolder = HeaderViewHolder(view)
            // 标题视图
            ViewTyper.TITLE.getViewType() -> viewHolder = TitleViewHolder(view)
            // item
            ViewTyper.ITEM.getViewType() -> viewHolder = BaseItemViewHolder(view)
            // 尾视图
            ViewTyper.FOOTER.getViewType() -> viewHolder = createFooterViewHolder(parent, viewType)
            // 其他
            else -> viewHolder = OtherViewHolder(view)
        }
        return viewHolder
    }

    /**
     * 删除指定位置的视图类型
     * @param position 位置
     */
    override fun deleteItemByPositionAccess(position: Int) {
        super.deleteItemByPositionAccess(position)
        mViewTypes!!.removeAt(position)
    }

    /**
     * 删除指定位置的视图类型
     * @param position 位置
     */
    override fun deleteItemAccess(position: Int) {
        super.deleteItemAccess(position)
        mViewTypes!!.removeAt(position)
    }

    /**
     * 删除一系列位置的视图类型
     * @param positionList 位置列表
     */
    override fun deleteAllItemsAccess(positionList: MutableList<Int>?) {
        super.deleteAllItemsAccess(positionList)
        val iterator = mViewTypes!!.iterator()
        positionList?.forEach {
            while (iterator.hasNext()) {
                if (iterator.next() == it) {
                    iterator.remove()
                    break
                }
            }
        }
    }

    /**
     * 设置有尾视图
     */
    override fun setHasFooter(isHasFooter: Boolean) {
        super.setHasFooter(isHasFooter)
        mViewTypeToViewLayouts = getMapIfNullCreate()
        // 添加尾视图类型和对应的布局id
        mViewTypeToViewLayouts!!.put(ViewTyper.FOOTER.getViewType(),
            R.layout.widget_load_state_layout
        )
    }

    /**
     * 获取指定位置视图对应的视图类型
     * @param position 位置
     */
    override fun getItemViewType(position: Int): Int {
        if (mData == null) {
            if (isHasFooter()) {
                return ViewTyper.FOOTER.getViewType()
            }
            return 0
        } else if (position >= mData!!.size) {
            return ViewTyper.FOOTER.getViewType()
        } else {
            return mViewTypes!!.get(position)
        }
    }

    /**
     * 判断映射集合是否为空或无内容
     */
    private fun isEmpty(map: SparseArray<Int>?): Boolean {
        return map == null || map.isEmpty()
    }

    /**
     * 获取map集合，如果为空则创建
     */
    private fun getMapIfNullCreate(): SparseArray<Int> {
        if (mViewTypeToViewLayouts == null) {
            mViewTypeToViewLayouts = SparseArray()
        }
        return mViewTypeToViewLayouts!!
    }

    /**
     * 判断视图类型是否是目前支持的类型，如果要改该处的判断值，请同步在{@link ViewTyper}进行修改
     */
    private fun isNotSupportType(viewType: Int): Boolean {
        return viewType < 0 || viewType > 4
    }

    /**
     * 判断列表是否为空或没有内容
     */
    private fun isEmptyTypeList(list: MutableList<Int>?): Boolean {
        return list == null || list.size == 0
    }

    /**
     * 判断数组是否为空或没有内容
     */
    private fun isEmptyTypeArray(array: Array<Int>?): Boolean {
        return array == null || array.isEmpty()
    }

    /**
     * 清除之前保存的数据
     */
    private fun clear() {
        // 清除数据
        mViewTypes?.clear()
        mData?.clear()
        addFooterTypeLayout()
    }

    /**
     * 添加尾视图的视图类型和布局id
     */
    private fun addFooterTypeLayout() {
        // 如果有尾视图则需要添加尾视图布局id
        if (isHasFooter()) {
            val footerLayoutResId = if (isCustomFooter()) mFooterLayoutResId else R.layout.widget_load_state_layout
            if (!mViewTypeToViewLayouts!!.contains(footerLayoutResId)) {
                mViewTypeToViewLayouts?.put(
                    ViewTyper.FOOTER.getViewType(),
                    footerLayoutResId)
            }
        }
    }

    /**
     * 使用视图类型提供器设置数据
     * @param data 数据
     * @param multipleViewTyper 提供器
     */
    fun setData(data: MutableList<Any?>?, multipleViewTyper: MultipleViewTyper) {
        if (isEmptyList(data) || multipleViewTyper == null) {
            return
        }
        clear()
        mData = data
        // 获取每个数据对应的类型
        val iterator = data!!.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            // 添加到类型列表中
            mViewTypes!!.add(multipleViewTyper.getViewTyper(item))
        }
        notifyDataSetChanged()
    }

    /**
     * 使用视图类型提供器设置数据
     * @param data 数据
     * @param multipleViewTyper 提供器
     */
    fun setData(data: Array<Any?>?, multipleViewTyper: MultipleViewTyper) {
        if (isEmptyArray(data)) {
            return
        }
        setData(Arrays.asList(*data!!), multipleViewTyper)
    }

    /**
     * 使用视图类型提供器设置数据
     * @param data 数据
     * @param multipleViewTyper 提供器
     */
    fun setData(data: MutableList<Any?>?, viewTypes: MutableList<Int>?) {
        if (isEmptyList(data) || isEmptyTypeList(viewTypes)) {
            return
        }
        clear()
        mData = data
        mViewTypes = viewTypes
        notifyDataSetChanged()
    }

    /**
     * 设置数据和视图类型（注意一定要将数据与类型对应起来）
     * @param data 数据
     * @param viewTypes 类型集
     */
    fun setData(data: Array<Any?>?, viewTypes: MutableList<Int>?) {
        if (isEmptyArray(data)) {
            return
        }
        setData(Arrays.asList(*data!!), viewTypes)
    }

    /**
     * 设置数据和视图类型（注意一定要将数据与类型对应起来）
     * @param data 数据
     * @param viewTypes 类型集
     */
    fun setData(data: MutableList<Any?>?, viewTypes: Array<Int>?) {
        if (isEmptyTypeArray(viewTypes)) {
            return
        }
        setData(data, Arrays.asList(*viewTypes!!))
    }

    /**
     * 使用视图类型提供器设置数据
     * @param data 数据
     * @param multipleViewTyper 提供器
     */
    fun setData(data: Array<Any?>?, viewTypes: Array<Int>?) {
        if (isEmptyArray(data) || isEmptyTypeArray(viewTypes)) {
            return
        }
        setData(Arrays.asList(*data!!), Arrays.asList(*viewTypes!!))
    }

    /**
     * 设置所有的item视图类型与布局id
     * @param typeToLayouts 类型与布局映射集合
     */
    fun setTypeToLayouts(typeToLayouts: SparseArray<Int>?) {
        if (isEmpty(typeToLayouts)) {
            return
        }
        mViewTypeToViewLayouts = getMapIfNullCreate()
        // 先清空
        mViewTypeToViewLayouts!!.clear()
        // 再添加
        mViewTypeToViewLayouts!!.putAll(typeToLayouts!!)
        addFooterTypeLayout()
    }

    /**
     * 添加一个类型与布局id对应关系
     * @param viewType item视图类型
     * @param viewLayout 布局id
     */
    fun addTypeToLayout(viewType: Int, viewLayout: Int) {
        if (!isNotSupportType(viewType)) {
            mViewTypeToViewLayouts = getMapIfNullCreate()
            mViewTypeToViewLayouts!!.put(viewType, viewLayout)
        }
    }

    /**
     * 批量添加对应关系（不做支持类型校验了，所以有可能在渲染item视图的时候抛出异常，请使用之前自己确认类型）
     * @param typeToLayouts 对应关系集合
     */
    fun addTypeToLayouts(typeToLayouts: SparseArray<Int>?) {
        if (!isEmpty(typeToLayouts)) {
            mViewTypeToViewLayouts = getMapIfNullCreate()
            mViewTypeToViewLayouts!!.putAll(typeToLayouts!!)
        }
    }

    /**
     * 添加一个指定类型的数据
     * @param item 数据
     * @param viewType 视图类型
     */
    fun addItem(item: Any?,viewType: Int) {
        if (item == null || !isNotSupportType(viewType)) {
            return
        }
        mData = getListIfNullCreate()
        val position = mData!!.size
        mData!!.add(item)
        mViewTypes!!.add(viewType)
        notifyItemInserted(position)
    }

    /**
     * 在指定位置处添加数据
     * @param item 数据
     * @param viewType 类型
     * @param position 位置
     */
    fun addItemAssignPosition(item: Any?, viewType: Int, position: Int) {
        if (item == null || isNotSupportType(viewType)) {
            return
        }
        mData = getListIfNullCreate()
        var insertPosition = 0
        // 小于等于0，则添加在头部
        if (position <= 0) {
            mData!!.add(0, item)
            mViewTypes!!.add(0, viewType)
        } else if (position >= mData!!.size) { // 超出上限，则添加在尾部
            // 获取插入的位置，方便后面刷新使用
            insertPosition = mData!!.size
            mData!!.add(item)
            mViewTypes!!.add(viewType)
        } else {
            mData!!.add(position, item)
            mViewTypes!!.add(viewType)
            insertPosition = position
        }
        notifyItemInserted(insertPosition)
    }

    /**
     * 批量添加指定类型的数据
     * @param items 批量数据
     * @param viewType 类型
     */
    fun addAllItemsAssignType(items: MutableList<Any?>?, viewType: Int) {
        if (isEmptyList(items) || isNotSupportType(viewType)) {
            return
        }
        mData = getListIfNullCreate()
        val position = mData!!.size
        mData!!.addAll(items!!)
        var count = items.size
        while (count > 0) {
            mViewTypes!!.add(viewType)
            --count
        }
        notifyItemRangeInserted(position, items.size)
    }

    /**
     * 批量添加指定类型的数据
     * @param items 批量数据
     * @param viewType 类型
     */
    fun addAllItemsAssignType(items: Array<Any?>?, viewType: Int) {
        if (isEmptyArray(items) || isNotSupportType(viewType)) {
            return
        }
        addAllItemsAssignType(Arrays.asList(*items!!), viewType)
    }

    /**
     * 批量添加数据（注意添加的数据要与类型对应，需要自己注意）
     * @param items 数据集
     * @param viewTypes 对应的类型集
     */
    fun addAllItems(items: MutableList<Any?>?, viewTypes: MutableList<Int>?) {
        if (isEmptyList(items) || isEmptyTypeList(viewTypes) || items!!.size != viewTypes!!.size) {
            return
        }
        mData = getListIfNullCreate()
        var position = mData!!.size
        mData!!.addAll(items!!)
        mViewTypes!!.addAll(viewTypes!!)
        notifyItemRangeInserted(position, items.size)
    }

    /**
     * 批量添加数据
     * @param items 数据集
     * @param viewTypes 类型集
     */
    fun addAllItems(items: MutableList<Any?>?, viewTypes: Array<Int>?) {
        if (isEmptyList(items) || isEmptyTypeArray(viewTypes) || items!!.size != viewTypes!!.size) {
            return
        }
        addAllItems(items, Arrays.asList(*viewTypes!!))
    }

    /**
     * 批量添加数据
     * @param items 数据集
     * @param viewTypes 类型集
     */
    fun addAllItems(items: Array<Any?>?, viewTypes: MutableList<Int>?) {
        if (isEmptyArray(items) || isEmptyTypeList(viewTypes) || items!!.size != viewTypes!!.size) {
            return
        }
        addAllItems(Arrays.asList<Any?>(*items!!), viewTypes)
    }

    /**
     * 批量添加数据
     * @param items 数据集
     * @param viewTypes 类型集
     */
    fun addAllItems(items: Array<Any?>?, viewTypes: Array<Int>?) {
        if (isEmptyArray(items) || isEmptyTypeArray(viewTypes) || items!!.size != viewTypes!!.size) {
            return
        }
        addAllItems(Arrays.asList(*items!!), Arrays.asList(*viewTypes!!))
    }

    /**
     * 在指定位置处批量添加数据
     * @param items 批量数据
     * @param viewTypes 类型集
     * @param position 位置
     */
    fun addAllItemsAssignPosition(items: MutableList<Any?>?, viewTypes: MutableList<Int>?, position: Int) {
        if (isEmptyList(items) || isEmptyTypeList(viewTypes)) {
            return
        }
        mData = getListIfNullCreate()
        var insertPosition = mData!!.size
        if (position <= 0) {
            mData!!.addAll(0, items!!)
            mViewTypes!!.addAll(0, viewTypes!!)
            insertPosition = 0
        } else if (position >= insertPosition) {
            mData!!.addAll(items!!)
            mViewTypes!!.addAll(viewTypes!!)
        } else {
            mData!!.addAll(position, items!!)
            mViewTypes!!.addAll(position, viewTypes!!)
            insertPosition = position
        }
        notifyItemRangeInserted(insertPosition, items.size)
    }

    /**
     * 在指定位置处批量添加数据
     * @param items 批量数据
     * @param viewTypes 类型集
     * @param position 位置
     */
    fun addAllItemsAssignPosition(items: MutableList<Any?>?, viewTypes: Array<Int>?, position: Int) {
        if (isEmptyList(items) || isEmptyTypeArray(viewTypes)) {
            return
        }
        addAllItemsAssignPosition(items, Arrays.asList(*viewTypes!!), position)
    }

    /**
     * 在指定位置处批量添加数据
     * @param items 批量数据
     * @param viewTypes 类型集
     * @param position 位置
     */
    fun addAllItemsAssignPosition(items: Array<Any?>?, viewTypes: MutableList<Int>?, position: Int) {
        if (isEmptyArray(items) || isEmptyTypeList(viewTypes)) {
            return
        }
        addAllItemsAssignPosition(Arrays.asList(*items!!), viewTypes, position)
    }

    /**
     * 在指定位置处批量添加数据
     * @param items 批量数据
     * @param viewTypes 类型集
     * @param position 位置
     */
    fun addAllItemsAssignPosition(items: Array<Any?>?, viewTypes: Array<Int>?, position: Int) {
        if (isEmptyArray(items) || isEmptyTypeArray(viewTypes)) {
            return
        }
        addAllItemsAssignPosition(Arrays.asList(*items!!), Arrays.asList(*viewTypes!!), position)
    }

    /**
     * 使用类型提供器批量添加数据
     * @param items 批量数据
     * @param multipleViewTyper 类型提供器
     */
    fun addAllItemsByTyper(items: MutableList<Any?>?, multipleViewTyper: MultipleViewTyper) {
        if (isEmptyList(items) || multipleViewTyper == null) {
            return
        }
        mData = getListIfNullCreate()
        val position = mData!!.size
        val iterator = items!!.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            mData!!.add(item)
            mViewTypes!!.add(multipleViewTyper.getViewTyper(item))
        }
        notifyItemRangeInserted(position, items.size)
    }

    /**
     * 使用类型提供器批量添加数据
     * @param items 批量数据
     * @param multipleViewTyper 类型提供器
     */
    fun addAllItemsByTyper(items: Array<Any?>?, multipleViewTyper: MultipleViewTyper) {
        if (isEmptyArray(items) || multipleViewTyper == null) {
            return
        }
        addAllItemsByTyper(Arrays.asList(*items!!), multipleViewTyper)
    }

    /**
     * 在指定位置处使用提供器批量添加数据
     * @param items 批量数据
     * @param multipleViewTyper 提供器
     * @param position 位置
     */
    fun addAllItemsByTyperAssignPosition(items: MutableList<Any?>?, multipleViewTyper: MultipleViewTyper, position: Int) {
        if (isEmptyList(items) || multipleViewTyper == null) {
            return
        }
        mData = getListIfNullCreate()
        var insertPosition = mData!!.size
        val iterator = items!!.iterator()
        // 待添加数据对应的类型列表
        var viewTypes = mutableListOf<Int>()
        while (iterator.hasNext()) {
            val item = iterator.next()
            viewTypes.add(multipleViewTyper.getViewTyper(item))
        }
        if (position <= 0) {
            mData!!.addAll(0, items!!)
            mViewTypes!!.addAll(viewTypes)
            insertPosition = 0
        } else if (position >= insertPosition) {
            mData!!.addAll(items)
            mViewTypes!!.addAll(viewTypes)
        } else {
            mData!!.addAll(position, items)
            mViewTypes!!.addAll(position, viewTypes)
        }
        notifyItemRangeInserted(insertPosition, items.size)
    }

    /**
     * 在指定位置处使用提供器批量添加数据
     * @param items 批量数据
     * @param multipleViewTyper 提供器
     * @param position 位置
     */
    fun addAllItemsByTyperAssignPosition(items: Array<Any?>?, multipleViewTyper: MultipleViewTyper, position: Int) {
        if (isEmptyArray(items) || multipleViewTyper == null) {
            return
        }
        addAllItemsByTyperAssignPosition(Arrays.asList(*items!!), multipleViewTyper, position)
    }

    /**
     * 自定义尾部
     * @param isCustomFooter 自定义标志
     * @param footerLayoutResId 尾视图布局id
     */
    fun setCustomFooter(isCustomFooter: Boolean, footerLayoutResId: Int) {
        super.setCustomFooter(isCustomFooter)
        super.setHasFooter(true)
        mViewTypeToViewLayouts = getMapIfNullCreate()
        mViewTypeToViewLayouts!!.put(ViewTyper.FOOTER.getViewType(), footerLayoutResId)
        // 保存自定义的尾视图布局id
        mFooterLayoutResId = footerLayoutResId
    }
}