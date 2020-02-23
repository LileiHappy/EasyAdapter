package com.lilei.easyAdapterLib.delegate

import androidx.recyclerview.widget.RecyclerView

/**
 * 列表的item视图样式修饰器接口，用于根据数据展示或修改视觉
 * 之所以使用接口的方式，是考虑可能存在着不需要修改item视觉的场景，对于该场景，则只给出item对应的布局即可，不用实现该接口
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
interface DecorateItemViewHolder {
    /**修饰item视觉
     * @param viewHolder 视图句柄
     * @param position 位置索引
     * @param viewType 视图类型   目前支持的视图类型请参见{@link #ViewTyper}
     *
     */
    fun onDecorateItemViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int, viewType: Int)
}