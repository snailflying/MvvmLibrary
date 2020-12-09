package com.theone.framework.widget.smarttablayout

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup

/**
 * @Author ZhiQiang
 * @Date 2020/11/15
 * @Description
 */
abstract class SmartTabAdapter<VH : SmartTabAdapter.Holder?> {
    abstract fun onCreateViewHolder(parent: ViewGroup): VH
    abstract fun onBindViewHolder(viewHolder: VH, position: Int)

    private var mObservable: DataSetObservable? = null
    var currTabIndex = 0
        set(position) {
            if (position < 0) {
                field = 0
                return
            }
            if (position >= getCount()) {
                field = getCount() - 1
                return
            }
            field = position
        }

    /**
     * 清空之前数据，并刷新最新数据
     */
    fun notifyDataSetChanged() {
        synchronized(this) { mObservable?.notifyChanged() }
    }

    /**
     * 数据不刷新，只刷新 ui
     */
    fun notifyInvalidated() {
        synchronized(this) { mObservable?.notifyInvalidated() }
    }

    fun registerDataSetObserver(observer: DataSetObserver) {
        mObservable = DataSetObservable()
        mObservable!!.registerObserver(observer)
    }

    fun unregisterDataSetObserver(observer: DataSetObserver) {
        mObservable?.unregisterObserver(observer)
    }

    abstract fun getCount(): Int

    open fun onSelectStateChange(select: Boolean) {}

    abstract class Holder(var itemView: View)
}