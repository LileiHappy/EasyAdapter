package com.lilei.easyAdapterLib.delegate

import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView

/**
 * 尾视图句柄创建器，用于自定义列表的尾视图，如果想自定义尾视图，请设置并实现该接口
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
interface FooterViewHolderCreator {
    /**
     * 创建尾视图句柄
     * @param parent 父容器
     * @param viewType 视图类型
     */
    fun onCreateFooterViewHolder(parent: ViewParent, viewType: Int): RecyclerView.ViewHolder
}