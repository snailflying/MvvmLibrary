package com.themone.core.base.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

/**
 * @Author ZhiQiang
 * @Date 2021/9/8
 * @Description 带懒加载
 */
open class CoreFragment : Fragment() {
    /**
     * 跟[getContext]区分，尽量避免为空
     */
    protected var mContext: Context? = null

    private var isFirstVisible = true
    private var isViewCreated = false
    private var isSupportVisible = false


    private val isParentInvisible: Boolean
        get() {
            val fragment = this.parentFragment as CoreFragment?
            return fragment != null && !fragment.isSupportVisible
        }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (this.isViewCreated) {
            if (isVisibleToUser && !this.isSupportVisible) {
                this.dispatchUserVisibleHint(true)
            } else if (!isVisibleToUser && this.isSupportVisible) {
                this.dispatchUserVisibleHint(false)
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.isViewCreated = true
        if (!this.isHidden && this.userVisibleHint) {
            this.dispatchUserVisibleHint(true)
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            this.dispatchUserVisibleHint(false)
        } else {
            this.dispatchUserVisibleHint(true)
        }

    }

    override fun onResume() {
        super.onResume()
        if (!this.isFirstVisible && !this.isHidden && !this.isSupportVisible && this.userVisibleHint) {
            this.dispatchUserVisibleHint(true)
        }

    }

    override fun onPause() {
        super.onPause()
        if (this.isSupportVisible && this.userVisibleHint) {
            this.dispatchUserVisibleHint(false)
        }

    }

    private fun isFragmentVisible(fragment: Fragment): Boolean {
        return !fragment.isHidden && fragment.userVisibleHint
    }

    private fun dispatchUserVisibleHint(visible: Boolean) {
        if (!visible || !this.isParentInvisible) {
            if (this.isSupportVisible != visible) {
                this.isSupportVisible = visible
                if (visible) {
                    if (this.isFirstVisible) {
                        this.isFirstVisible = false
                        this.onFragmentFirstVisible()
                    }

                    this.onFragmentResume()
                    this.dispatchChildVisibleState(true)
                } else {
                    this.dispatchChildVisibleState(false)
                    this.onFragmentPause()
                }

            }
        }
    }

    private fun dispatchChildVisibleState(visible: Boolean) {
        val childFragmentManager = this.childFragmentManager
        val fragments = childFragmentManager.fragments
        if (fragments.isNotEmpty()) {
            val var4 = fragments.iterator()

            while (var4.hasNext()) {
                val child = var4.next() as Fragment
                if (child is CoreFragment && !child.isHidden() && child.getUserVisibleHint()) {
                    child.dispatchUserVisibleHint(visible)
                }
            }
        }

    }

    open fun onFragmentFirstVisible() {
        Log.i(TAG, this.javaClass.simpleName + "  对用户第一次可见")
    }

    open fun onFragmentResume() {
        Log.i(TAG, this.javaClass.simpleName + "  对用户可见")
    }

    open fun onFragmentPause() {
        Log.i(TAG, this.javaClass.simpleName + "  对用户不可见")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.isViewCreated = false
        this.isFirstVisible = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    /**
     * Fragment返回键处理
     * 见[CoreActivity.onBackPressed]
     */
    protected fun onBackPressed() {
        activity?.onBackPressed()
    }

    companion object {

        private val TAG = "CoreFragment"
    }
}
