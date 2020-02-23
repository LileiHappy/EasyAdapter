package com.lilei.easyAdapterLib.delegate

/**
 * 尾视图点击重加载监听对象，当加载更多数据失败后，可通过点击尾视图进行重加载监听处理
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
interface OnReloadClickListener {
    /**
     * 重加载
     */
    fun onReloadClick()
}