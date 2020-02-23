package com.lilei.easyAdapterLib.delegate

/**
 * 多样式视图类型接口，获取指定对象对应的视图类型
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
interface MultipleViewTyper {
    /**
     * 获取指定对象对应的视图类型
     * @param item 指定的对象
     * @return 对应视图的类型  目前支持的视图类型请参见{@link #ViewTyper}，可以自行增删视图类型
     */
    fun getViewTyper(item: Any?): Int
}