package com.lilei.easyAdapterLib.type

/**
 * 加载状态类型枚举
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
enum class LoadStateTyper constructor(loadStateType: Int) {

    /**正常状态，对应文案为提示上拉加载更多*/
    NORMAL(0),
    /**不可见*/
    INVISIBLE(1),
    /**正在加载*/
    LOADING(2),
    /**本次加载完成*/
    FINISH(3),
    /**加载失败*/
    LOAD_FAIL(4),
    /**没有更多内容*/
    NO_MORE(5);

    /**加载状态类型*/
    private var mLoadStateType = 0

    init {
        mLoadStateType = loadStateType
    }

    /**获取该加载类型对应的加载状态*/
    public fun getLoadState(): Int {
        return mLoadStateType
    }

    /**
     * 获取加载状态对应的加载类型
     * @param loadStateType 加载状态
     * @return 加载状态类型
     */
    public fun getLoadStateTyper(loadStateType: Int): LoadStateTyper {
        var loadStateTyper: LoadStateTyper? = null
        when (loadStateType) {
            0 -> loadStateTyper = NORMAL
            1 -> loadStateTyper = INVISIBLE
            2 -> loadStateTyper = LOAD_FAIL
            3 -> loadStateTyper = FINISH
            4 -> loadStateTyper = LOAD_FAIL
            5 -> loadStateTyper = NO_MORE
            else -> throw IllegalArgumentException("not support load state!")
        }
        return loadStateTyper
    }

    /**
     * 获取指定加载类型对应的加载状态
     * @param loadStateTyper 加载类型
     * @return 加载类型对应的加载状态
     */
    public fun getLoadState(loadStateTyper: LoadStateTyper): Int {
        var loadStateType = 0
        when (loadStateTyper) {
            NORMAL -> loadStateType = 0
            INVISIBLE -> loadStateType =1
            LOADING -> loadStateType = 2
            FINISH -> loadStateType = 3
            LOAD_FAIL -> loadStateType = 4
            NO_MORE -> loadStateType = 5
            else -> throw IllegalArgumentException("not support load state!")
        }
        return loadStateType
    }
}