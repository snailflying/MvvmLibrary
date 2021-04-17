package com.theone.framework.widget.smarttablayout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @Author ZhiQiang
 * @Date 2020/11/15
 * @Description
 */
abstract class SimpleSmartTabAdapter(var layoutResId: Int, var tabViewTextViewId: Int = View.NO_ID) : SmartTabAdapter<SimpleSmartTabAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(layoutResId, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val customText: View =
                if (tabViewTextViewId != View.NO_ID) {
                    viewHolder.itemView.findViewById(tabViewTextViewId)
                } else {
                    viewHolder.itemView
                }

        val isSelect = currTabIndex == position
        viewHolder.itemView.isSelected = isSelect
        customText.isSelected = isSelect
        if (customText is TextView) {
            customText.text = getTabTitle(position)
            onBindViewHolderExt(customText, isSelect)
        }
    }

    open fun onBindViewHolderExt(textView: TextView, isSelect: Boolean) {}

    abstract fun getTabTitle(position: Int): String?

    class ViewHolder(item: View) : SmartTabAdapter.Holder(item)

}
