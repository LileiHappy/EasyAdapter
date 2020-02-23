package com.lilei.easyAdapterLib.delegate

import androidx.recyclerview.widget.RecyclerView
import com.lilei.easyAdapterLib.adapter.BaseItemAdapter

/**
 * RecyclerView的内容和样式适配器item点击监听：过滤掉onRecyclerItemLongClick监听，避免无用的实现
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
abstract class OnItemClickListener: BaseItemAdapter.OnRecyclerItemClickLitener {
    override fun onRecyclerItemLongClick(view: RecyclerView.ViewHolder, position: Int) {
    }
}