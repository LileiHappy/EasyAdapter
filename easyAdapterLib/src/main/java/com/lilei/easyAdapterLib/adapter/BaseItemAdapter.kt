package com.lilei.easyAdapterLib.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lilei.easyAdapterLib.delegate.DecorateItemViewHolder
import com.lilei.easyAdapterLib.delegate.FooterViewHolderCreator
import com.lilei.easyAdapterLib.delegate.OnReloadClickListener
import com.lilei.easyAdapterLib.type.LoadStateTyper
import com.lilei.easyAdapterLib.type.ViewTyper
import com.lilei.easyAdapterLib.viewHolder.FooterViewHolder
import com.lilei.easyAdapterLib.viewModel.LoadStateViewModel
import com.lilei.widgets.LoadStateView
import java.util.Arrays
import kotlin.collections.HashMap
import kotlin.collections.set

/**
 * 列表内容和样式适配器基类
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
open abstract class BaseItemAdapter<Any> constructor(context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**日志tag*/
    private val TAG = "BaseItemAdapter"

    /**上下文*/
    var mContext: Context? = null
    /**添加到父容器标志*/
    var isAttachParent = true
    /**数据源*/
    var mData: MutableList<Any?>? = null
    /**加载状态*/
    private var mLoadState = LoadStateTyper.NORMAL.getLoadState()
    /**加载视图和状态对应的封装*/
    var mLoadStateViewModel : LoadStateViewModel? = null

    /**有尾视图（加载更多等）标志*/
    private var isHasFooter = false
    /**自定义尾视图标志*/
    private var isCustomFooter = false
    /**尾视图创建器，为创建自定义尾视图提供能力*/
    var mFooterViewHolderCreator: FooterViewHolderCreator? = null
    /**修改item样式的对象，提供修改item的能力*/
    private var mDecorateItemViewHolder: DecorateItemViewHolder? = null

    /**item监听对象*/
    private var mClickLitener: OnRecyclerItemClickLitener? = null
    /**加载更多重加载监听对象*/
    private var mReloadClickListener: OnReloadClickListener? = null

    init {
        if (context == null) {
            throw IllegalArgumentException("context is null!")
        }
        mContext = context
    }

    /**
     * 获取总数量
     */
    override fun getItemCount(): Int {
        var dataSize = if (mData == null) 0 else mData!!.size
        return if (isHasFooter) 1 + dataSize else dataSize
    }

    /**
     * 将视图句柄和数据进行绑定
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewType = getItemViewType(position)
        // 尾视图
        if (ViewTyper.FOOTER.getViewType() == viewType) {
            // 自定义尾视图且有修饰器，则可以对尾视图进行修改
            if (isCustomFooter && mDecorateItemViewHolder != null) {
                mDecorateItemViewHolder?.onDecorateItemViewHolder(holder, position, viewType)
            }
            if (mReloadClickListener != null) {
                // 尾视图设置点击监听
                mLoadStateViewModel?.getLoadView()?.setOnClickListener {
                    // 加载失败状态下允许重新加载
                    if (mLoadState == LoadStateTyper.LOAD_FAIL.getLoadState()) {
                        mReloadClickListener?.onReloadClick()
                    }
                }
            }
        } else {
            if (mClickLitener != null) {
                holder.itemView.setOnClickListener {
                    mClickLitener?.onRecyclerItemClick(holder, position)
                }
                holder.itemView.setOnLongClickListener {
                    mClickLitener?.onRecyclerItemLongClick(holder, position)
                    return@setOnLongClickListener false
                }
            }
            // 回调修饰
            mDecorateItemViewHolder?.onDecorateItemViewHolder(holder, position, viewType)
        }
    }

    /**判断共同元素是否在原数据中是连续的并获取第一个共同元素在原数据中的位置索引
     *
     * 判断共同元素是否是在原数据中是连续的，思路是找出共同元素在原数据中的位置索引，然后将其排序，最后判断相邻的位置是否差值为1
     * 如原数据中的数为：{1,2,3,4,5,6,7,8}     待比较的数据为：
     * {4,3,2,5} 则可知在原数据中是连续的
     * {3,2,5,4} 也是连续的
     * {5,3,2,4} 也是连续的
     * {3,5,4,2} 也是的
     * {3,4,5,7} 不是连续的
     *
     * return说明 -1：共同数据在原数据中不是连续的，大于-1：第一个元素在原数据中的位置索引
     * @param items 待比较元素列表
     * @param positionList 用于获取共同元素索引的列表，为多样式适配器删除共同元素对应的视图类型提供服务
     * @return 第一个共同元素在原数据中的位置所以，-1：共同元素在原数据不是连续的，大于-1：第一个在原数据中的位置索引
     */
    private fun getFirstIndexInData(items: MutableList<Any?>, positionList: MutableList<Int>): Int {
        // 将元素和序号对应的映射集合，保存待比较的所有元素
        // 因为使用映射表判断是否包含指定的键和通过键获取值效率都是很高的
        // （在数组中则计算哈希值时间然后对桶数取余即可得到，不在数组中则会在链表或红黑树中查找效率都还可以）
        // 列表按索引查找很快，常数级
        var map: HashMap<Any?, Int> = HashMap()
        // 将每个元素与位置索引对应起来
        for (index in items.indices) {
            // 将元素与索引对应
            map[items[index]] = index
        }
        // 保存待比较列表和原数据列表中共同元素的位置索引的列表
        var bothItems: MutableList<Int> = mutableListOf()
        // 查找共同元素，将找到的共同元素位置索引保存在列表中
        for (index in mData!!.indices) {
            // 待比较列表含有当前操作元素
            if (map.containsKey(mData!![index])) {
                // 将共同元素在原数据列表中的位置索引保存在列表中
                bothItems.add(index)
                positionList.add(index)
            }
        }
//        // 保存位置的数组，用于对位置进行排序
//        var array = IntArray(bothItems.size)
//        // 复制每个值
//        for (index in bothItems.indices) {
//            array[index] = bothItems[index]
//        }
//        // 升序排序
//        Arrays.sort(array)
//        val length = array.size
        // 第一个共同元素在原数据中的位置，默认值为不在原列表中
        var firstItemIndex = Integer.MAX_VALUE
//        // 比较相邻的两个数之差是否为1，为1表示所有的数据是连续的
//        for (index in array.indices) {
//            // 不连续
//            if (index + 1 < length && array[index] != array[index] + 1) {
//                firstItemIndex  = -1
//                break
//            }
//        }
        // 上面的代码注释掉的原因是，在比较的时候就是按照原数据的位置索引递增的方式进行的，所以bothItems保存的内容也是递增的（即排好序的）
        for (index in bothItems.indices) {
            if (index + 1< bothItems.size && bothItems[index] + 1!= bothItems[index + 1]) {
                firstItemIndex = -1
                break
            }
        }
        return if (firstItemIndex == -1) -1 else bothItems[0]
    }

    /**
     * 更新加载状态
     * @param loadState 加载状态
     */
    public fun setLoadState(loadState: Int) {
        if (isHasFooter) {
            mLoadStateViewModel?.setLoadState(loadState)
            notifyItemChanged(mData!!.size)
        }
    }

    /**
     * 获取当前加载状态
     */
    public fun getLoadState(): Int {
        return if (mLoadStateViewModel == null) 0 else mLoadStateViewModel!!.getLoadState()
    }

    /**
     * 设置加载视图状态viewmodel
     */
    open fun setLoadStateViewLoad(loadStateViewModel: LoadStateViewModel) {
        mLoadStateViewModel = loadStateViewModel
    }

    /**
     * 设置列表有尾视图
     * @param 有尾视图标志
     */
    open fun setHasFooter(isHasFooter: Boolean) {
        this.isHasFooter = isHasFooter
    }

    /**
     * 判断是否有尾视图
     */
    public fun isHasFooter(): Boolean {
        return isHasFooter
    }

    /**
     * 自定义尾视图
     * @param isCustomFooter 自定义尾视图标志
     */
    open fun setCustomFooter(isCustomFooter: Boolean) {
        this.isCustomFooter = isCustomFooter
    }

    /**
     * 判断是否自定义尾视图
     */
    public fun isCustomFooter(): Boolean {
        return isCustomFooter
    }

    /**
     * 设置尾视图句柄创建者，自定义尾视图需要设置该创建者
     * @param footerViewHolderCreator 尾视图句柄创建者
     */
    public fun setFooterViewHolderCreator(footerViewHolderCreator: FooterViewHolderCreator) {
        mFooterViewHolderCreator = footerViewHolderCreator
    }

    /**
     * 设置item样式修饰器，用于更新item对应的视图，之所以使用接口的方式，是因为框架中做了实例化item视图句柄的处理
     * 所以只提供修改item样式的功能，对于不需要修改的则不用实现，所以使用接口适合
     * @param decorateItemViewHolder item视图修饰器
     */
    public fun setDecorateItemViewHolder(decorateItemViewHolder: DecorateItemViewHolder) {
        mDecorateItemViewHolder = decorateItemViewHolder
    }

    /**
     * 设置监听
     * @param 监听对象
     */
    public fun setOnRecyclerItemClickLitener(listener: OnRecyclerItemClickLitener) {
        mClickLitener = listener
    }

    /**
     * 设置重加载监听
     * @param 监听对象
     */
    public fun setOnReloadClickListener(listener: OnReloadClickListener) {
        mReloadClickListener = listener
    }

    /**
     * 列表无内容或为空判断
     * @param list 列表
     */
    public fun isEmptyList(list: MutableList<Any?>?): Boolean {
        return list == null || list.size == 0
    }

    /**
     * 数组无内容或为空判断
     * @param array 数据
     */
    public fun isEmptyArray(array: Array<Any?>?): Boolean {
        return array == null || array.isEmpty()
    }

    /**
     * 越界判断
     */
    public fun isOutRange(index: Int): Boolean {
        return index < 0 || index > mData!!.size -1
    }

    /**
     * 获取列表，如果为空就创建列表
     */
    public fun getListIfNullCreate(): MutableList<Any?> {
        if (mData == null) {
            mData = mutableListOf()
        }
        return mData!!
    }

    //----》删除数据
    /**
     * 删除指定位置的数据
     * @param position 位置
     */
    public fun deleteItemByPosition(position: Int) {
        if (isEmptyList(mData) || isOutRange(position)) {
            return
        }
        // 为多样式适配器提供删除访问入口，多样式适配器可通过覆写该方法进行自己需要的处理
        deleteItemByPositionAccess(position)
        mData!!.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * 删除一条数据
     * @param item 待删除数据
     */
    public fun deleteItem(item: Any?) {
        if (item == null || isEmptyList(mData)) {
            return
        }
        // 获取待删除记录的位置
        var position = mData!!.indexOf(item)
        // 存在待删除数据，则进行删除操作
        if (position > -1) {
            // 为多样式适配器提供删除访问入口，多样式适配器可通过覆写该方法进行自己需要的处理
            deleteItemAccess(position)
            mData!!.remove(item)
            notifyItemRemoved(position)
        }
    }

    /**
     * 批量删除数据
     * @param items 批量数据
     */
    public fun deleteAllItems(items: MutableList<Any?>?) {
        if (isEmptyList(mData) || isEmptyList(items)) {
            return
        }
        var positionList = mutableListOf<Int>()
        var position = getFirstIndexInData(items!!, positionList)
        // 为多样式适配器提供删除访问入口，多样式适配器可通过覆写该方法进行自己需要的处理
        deleteAllItemsAccess(positionList)
        mData!!.removeAll(items)
        // 被删除的数据在原数据中不是连续的
        if (position == -1) {
            // 因为是不连续的，所以只能全部刷新了
            notifyDataSetChanged()
        } else {
            notifyItemRangeRemoved(position, items.size)
        }
    }

    /**
     * 批量删除数据
     * @param items 批量数据
     */
    public fun deleteAllItems(items: Array<Any?>?) {
        if (isEmptyArray(items)) {
            return
        }
        deleteAllItems(Arrays.asList(*items!!))
    }

    /**
     * 为多样式获知删除指定位置数据提供入口
     * @param position 位置索引
     */
    open fun deleteItemByPositionAccess(position: Int) {}

    /**
     * 为多样式获知删除指定位置数据提供入口
     * @param position 位置索引
     */
    open fun deleteItemAccess(position: Int) {}

    /**
     * 为多样式获知删除指定位置数据提供入口
     * @param positionList 位置索引列表
     */
    open fun deleteAllItemsAccess(positionList: MutableList<Int>?) {}

    /**
     * 更新指定位置处的数据，使用先删除再添加方式
     * @param item 更新后数据
     * @param position 位置
     */
    public fun updateItemAssignPosition(item: Any?, position: Int) {
        if (item == null || isEmptyList(mData) || isOutRange(position)) {
            return
        }
        mData!!.removeAt(position)
        mData!!.add(position, item)
        notifyItemChanged(position)
    }

    /**
     * 更新一条数据，使用比较器查找要更新的数据
     * @param item 数据
     * @param comparator 比较器
     */
    public fun updateItem(item: Any?, comparator: Comparator<Any>?) {
        if (item == null || isEmptyList(mData) || comparator == null) {
            return
        }
        // 对于按条件删除的操作最好用迭代的方式，使用for可能产生删除的数据位置错乱，使用forEach则会抛并发修改异常
        var iterator: MutableIterator<Any?> = mData!!.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            // 找到了要更新的数据
            if (comparator.compare(it, item) == 0) {
                // 获取对应的位置
                val position = mData!!.indexOf(it)
                // 使用先删除再添加的方式
                iterator.remove()
                mData!!.add(position, item)
                notifyItemRemoved(position)
                break
            }
        }
    }

    /**
     * 获取指定位置处数据
     * @param position 位置
     */
    public fun getItemByPosition(position: Int): Any? {
        if (isEmptyList(mData) || isOutRange(position)) {
            return null
        }
        return mData!![position]
    }

    /**
     * 从指定位置起获取指定大小的数据
     * @param position 起始位置
     * @param count 总量 超过当前列表的总长度后只会返回从起始后的所有数据
     */
    open fun getRangeItems(position: Int, count: Int): MutableList<Any?>? {
        if (isEmptyList(mData) || isOutRange(position) || count <= 0) {
            return null
        }
        var resultList = mutableListOf<Any?>()
        var remandCount = count
        for (index in mData!!.indices) {
            if (index >= position && remandCount > 0) {
                resultList.add(mData!![index])
                --remandCount
            }
        }
        return resultList
    }

    /**
     * 创建尾视图
     * @param parent 父容器
     * @param viewType 视图类型
     */
    public fun createFooterViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (isCustomFooter()) {
            if (mFooterViewHolderCreator == null) {
                throw RuntimeException("footer ViewHolder creator is null!")
            }
            // 返回自定义尾视图句柄
            return mFooterViewHolderCreator!!.onCreateFooterViewHolder(parent, viewType)
        }
        // 渲染加载视图
        val loadStateView = LoadStateView(mContext!!)
        // 实例化尾视图句柄
        val footerViewHolder = FooterViewHolder(loadStateView)
        // 未实例化加载视图viewmodel则实例化
        setLoadStateViewLoad(LoadStateViewModel(mContext, footerViewHolder))
        // 返回默认实现的尾视图句柄
        return footerViewHolder
    }

    /**
     * item点击监听
     */
    open interface OnRecyclerItemClickLitener {
        /**
         * 点击监听回调处理
         * @param view 视图句柄
         * @param position 位置索引
         */
        fun onRecyclerItemClick(view: RecyclerView.ViewHolder, position: Int)

        /**
         * 长按监听回调处理
         * @param view 视图句柄
         * @param position 位置索引
         */
        fun onRecyclerItemLongClick(view: RecyclerView.ViewHolder, position: Int)
    }
}


