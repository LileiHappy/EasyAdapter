package com.lilei.easyAdapterLib.type

/**
 * 状态类型枚举
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
enum class StateTyper constructor(state: Int){

    /**默认状态，只展示加载进度条*/
    NORMAL(0),
    /**不可见*/
    INVISIBLE(1),
    /**正在加载，展示进度条和提示文案*/
    LOADING(2),
    /**本次加载完成*/
    FINISH(3),
    /**加载失败*/
    LOAD_FAIL(4),
    /**未登录*/
    NOT_LOGIN(5),
    /**连接超时*/
    CONNECT_TIMEOUT(6),
    /**网络不可用或没有网络*/
    NETWORK_UNAVAILABLE(7);

    /**当前状态类型对应的状态*/
    private var mState = 0

    init {
        mState = state
    }

    /**
     * 获取当前状态
     */
    public fun getState(): Int {
        return mState
    }

    /**
     * 获取状态对应的状态类型
     * @param state 状态
     */
    public fun getStateTyper(state: Int): StateTyper {
        var stateTyper: StateTyper? = null
        when (state) {
            0 -> stateTyper = NORMAL
            1 -> stateTyper = INVISIBLE
            2 -> stateTyper = LOADING
            3 -> stateTyper = FINISH
            4 -> stateTyper = LOAD_FAIL
            5 -> stateTyper = NOT_LOGIN
            6 -> stateTyper = CONNECT_TIMEOUT
            7 -> stateTyper = NETWORK_UNAVAILABLE
            else -> throw IllegalStateException("not support state!")
        }
        return stateTyper
    }

    /**
     * 获取状态类型对应的状态
     * @param stateTyper 状态类型
     */
    public fun getState(stateTyper: StateTyper): Int {
        var state = 0
        when (stateTyper) {
            NORMAL -> state = 0
            INVISIBLE -> state = 1
            LOADING -> state = 2
            FINISH -> state = 3
            LOAD_FAIL -> state = 4
            NOT_LOGIN -> state = 5
            CONNECT_TIMEOUT -> state = 6
            NETWORK_UNAVAILABLE -> state = 7
            else -> throw IllegalStateException("not support state!")
        }
        return state
    }
}