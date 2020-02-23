package com.lilei.easyAdapterLib.viewHolder

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.iterator
import androidx.recyclerview.widget.RecyclerView

/**
 * item视图句柄基类
 * @author libai
 * @since 2020-2-22
 * @version 1.0
 */
open class BaseItemViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    /**item视图对应的根视图*/
    private var mItemView: View? = null
    /**视图控件id与视图控件对应的映射集合*/
    private var mViewIdToViews: SparseArray<View>? = null

    /**
     * 初始化
     */
    init {
        mItemView = itemView
        parserWithIdView()
    }

    /**
     * 获取根视图
     * @return 根视图
     */
    open fun getItemView(): View? {
        return mItemView
    }

    /**
     * 获取指定的视图控件id对应的视图控件，可能为空
     * @param id 视图控件id
     */
    open fun findViewById(@IdRes id: Int): View? {
        if (mViewIdToViews != null) {
            return mViewIdToViews!![id]
        }
        return null
    }

    /**
     * 获取该视图容器中所有指定了id的所有控件
     */
    private fun parserWithIdView() {
        if (mItemView != null) {
            // 实例化再使用
            if (mViewIdToViews == null) {
                mViewIdToViews = SparseArray()
            }
            // 获取所有指定了id的视图控件
            getViewFromView(mItemView!!)
        }
    }

    /**
     * 从指定的视图控件中获取所有指定id的视图控件
     * @param view 视图控件
     */
    private fun getViewFromView(view: View) {
        // 退出递归条件，为空时直接退出本次递归
        if (view == null) {
            return
        }
        // 容器视图
        if (view is ViewGroup) {
            // 转为容器
            val viewGroup = view as ViewGroup
            // 指定了id
            if (viewGroup!!.id != View.NO_ID) {
                // 将视图id与视图对应保存起来
                mViewIdToViews!!.put(viewGroup!!.id, view)
            }
            // 获取该容器中的所有子视图
            val iterator = viewGroup.iterator()
            // 依次判断每个子视图
            while (iterator.hasNext()) {
                // 获取本次迭代对应的子视图
                val v = iterator.next()
                // 判断该子视图，并从中获取指定了id的视图控件
                getViewFromView(v)
            }
        } else {
            // 指定了id
            if (view!!.id != View.NO_ID) {
                // 将视图id与视图对应保存起来
                mViewIdToViews!!.put(view!!.id, view)
            }
            return
        }
    }
}