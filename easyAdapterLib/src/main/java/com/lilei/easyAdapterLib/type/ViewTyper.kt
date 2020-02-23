package com.lilei.easyAdapterLib.type


/** 列表中item对应的视图类型枚举，如果未来觉得还有一些类型目前没定义到需要增加，则可以在这里增加
 *  目前定义的五种类型中Other是预留的，一般场景使用Other就可以解决多样式问题
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
enum class ViewTyper constructor(viewType: Int) {

    /**头部*/
    HEADER(0),
    /**标题*/
    TITLE(1),
    /**item*/
    ITEM(2),
    /**尾部*/
    FOOTER(3),
    /**预留类型*/
    OTHER(4);


    /**item对应的视图类型*/
    private var mViewType = -1

    init {
        mViewType = viewType
    }

    /**
     * 获取当前枚举对应的视图类型
     */
    public fun getViewType(): Int {
        return mViewType
    }

    /**
     * 根据视图类型获取对应的视图类型枚举
     * @param viewType 视图类型
     * @return 视图类型枚举
     */
    public fun getViewTyper(viewType: Int): ViewTyper {
        var viewTyper: ViewTyper? = null
        when (viewType) {
            0 -> viewTyper = HEADER
            1 -> viewTyper = TITLE
            2 -> viewTyper = ITEM
            3 -> viewTyper = FOOTER
            4 -> viewTyper = OTHER
            else -> throw IllegalArgumentException("not support view type!")
        }
        return viewTyper
    }

    /**
     * 获取视图类型枚举对应的视图类型
     * @param viewTyper 视图类型枚举
     * @return 视图类型
     */
    public fun getViewType(viewTyper: ViewTyper): Int {
        var viewType = -1
        when (viewTyper) {
            HEADER -> viewType = 0
            TITLE -> viewType = 1
            ITEM -> viewType = 2
            FOOTER -> viewType = 3
            OTHER -> viewType = 4
            else -> throw IllegalArgumentException("not support view type!")
        }
        return viewType
    }
}