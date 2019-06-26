package com.themone.core.base.impl

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.themone.core.base.IMultiStatusProvider
import com.themone.core.base.IViewModel
import com.themone.core.base.MultiViewStatus
import com.themone.core.base.MultiViewStatus.*
import com.themone.theone.library.R

/**
 * @author zhiqiang
 * @date 2019-06-06
 * @desc 提供默认的 loading、error、empty 等状态 UI
 * 需要更改状态的 view Id 强制规定命名 为 id=@+id/multiStatusView
 */
abstract class CoreMultiViewActivity<VM : IViewModel> : CoreMvvmActivity<VM>(), IMultiStatusProvider {

    protected var viewError: View? = null
    protected var viewLoading: View? = null
    protected var mMultiStatusView: View? = null
    var mParent: ViewGroup? = null

    private var mViewErrorRes: Int = 0

    protected var mViewState = STATUS_MAIN
    var isErrorViewAdded = false
    var mMultiStatusLP: ViewGroup.LayoutParams? = null

    val httpErrorRes: Int
        @LayoutRes
        get() = R.layout.view_error

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        initUiStatus()
    }

    protected fun initUiStatus() {
        mMultiStatusView = findViewById(R.id.multiStatusView)
        if (mMultiStatusView == null) {
            throw IllegalStateException(
                "The subclass of RootActivity must contain a View named 'view_main'."
            )
        }
        if (mMultiStatusView!!.parent !is ViewGroup) {
            throw IllegalStateException(
                "view_main's ParentView should be a ViewGroup."
            )
        }
        mMultiStatusLP = mMultiStatusView!!.layoutParams
        mParent = mMultiStatusView!!.parent as ViewGroup
        mViewErrorRes = httpErrorRes
    }


    override fun onStatusLoading() {
        if (mViewState === STATUS_LOADING) {
            return
        }
        if (null == viewLoading) {
            viewLoading = View.inflate(this, R.layout.view_progresss, null)
            if (null == viewLoading) {
                throw IllegalStateException(
                    "A View should be named 'view_progresss' in viewLoadingResource."
                )
            }
            //设置同一个layoutParams
            mParent!!.addView(viewLoading, mMultiStatusLP)
            viewLoading = mParent!!.findViewById(R.id.loadingView)
        }
        hideCurrentView()
        mViewState = STATUS_LOADING
        viewLoading!!.visibility = View.VISIBLE
        //        ivLoading.start();
    }

    override fun onStatusHttpError() {
        if (mViewState === STATUS_HTTP_ERROR) {
            return
        }
        if (!isErrorViewAdded) {
            isErrorViewAdded = true
            viewError = View.inflate(this, mViewErrorRes, null)
            if (null == viewError) {
                throw IllegalStateException(
                    "A View should be named 'view_error' in ErrorLayoutResource."
                )
            }
            //设置同一个layoutParams
            mParent!!.addView(viewError, mMultiStatusLP)
            viewError = mParent!!.findViewById(R.id.errorView)
            viewError!!.setOnClickListener { event -> retryNetWork() }
        }
        hideCurrentView()
        mViewState = STATUS_HTTP_ERROR
        viewError!!.visibility = View.VISIBLE
    }

    override fun onStatusEmpty() {
        onStatusMain()
    }

    override fun onStatusMain() {
        if (mViewState === STATUS_MAIN) {
            return
        }
        hideCurrentView()
        mViewState = STATUS_MAIN
        mMultiStatusView!!.visibility = View.VISIBLE

    }

    override fun onStatusServiceEx() {
        onStatusHttpError()
    }

    /**
     * 点击重试
     */
    open fun retryNetWork() {}

    fun hideCurrentView() {
        when (mViewState) {
            MultiViewStatus.STATUS_EMPTY, STATUS_MAIN -> mMultiStatusView!!.visibility = View.GONE
            STATUS_LOADING ->
                //                ivLoading.stop();
                viewLoading!!.visibility = View.GONE
            STATUS_HTTP_ERROR, MultiViewStatus.STATUS_NETWORK_FAILED -> if (null != viewError) {
                viewError!!.visibility = View.GONE
            }
        }
    }

}
