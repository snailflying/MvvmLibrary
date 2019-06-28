package com.themone.core.base.impl

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import com.themone.core.base.IMultiStateProvider
import com.themone.core.base.IViewModel
import com.themone.core.base.MultiViewStatus
import com.themone.core.base.MultiViewStatus.*
import com.themone.theone.library.R

/**
 * @author zhiqiang
 * @date 2019-06-06
 * @desc 提供默认的 loading、error、empty 等状态 UI
 * 需要更改状态的 view Id 强制规定命名 为 id=@+id/multiStatusView
 * 参考：https://github.com/Kennyc1012/MultiStateView
 */
abstract class CoreMultiViewActivity<VM : IViewModel> : CoreMvvmActivity<VM>(), IMultiStateProvider {

    private var errorView: View? = null
    private var emptyView: View? = null
    private var loadingView: View? = null
    private var mainView: View? = null
    private var parentViewGroup: ViewGroup? = null
    protected var viewState = STATUS_MAIN
        set(value) {
            val previousField = field

            if (value != previousField) {
                field = value
                setView(previousField)
                multiStatusListener?.onStateChanged(value)
            }
        }
    var multiStatusListener: StatusListener? = null

    var animateLayoutChanges: Boolean = false

    var contentLayoutParams: ViewGroup.LayoutParams? = null

    @LayoutRes
    protected open val errorLayout: Int = R.layout.view_error

    @LayoutRes
    protected open val emptyLayout: Int = R.layout.view_error
    @LayoutRes
    protected open val loadingLayout: Int = R.layout.view_progresss

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUiStatus(savedInstanceState)
    }

    private fun initUiStatus(savedInstanceState: Bundle?) {
        mainView = findViewById(R.id.multiStatusView)
        if (mainView == null) {
            throw IllegalStateException(
                "The subclass of RootActivity must contain a View named 'view_main'."
            )
        }
        if (mainView!!.parent !is ViewGroup) {
            throw IllegalStateException(
                "view_main's ParentView should be a ViewGroup."
            )
        }
        contentLayoutParams = mainView!!.layoutParams
        parentViewGroup = mainView!!.parent as ViewGroup
    }

    override fun showStateLoading(view: View?) {
        if (viewState === STATUS_LOADING) {
            return
        }
        if (view != null) {
            loadingView = view
            //设置同一个layoutParams
            parentViewGroup!!.addView(loadingView, contentLayoutParams)
        } else if (null == loadingView) {
            loadingView = View.inflate(this, loadingLayout, null)
            if (null == loadingView) {
                throw IllegalStateException(
                    "A View should be named 'view_progresss' in viewLoadingResource."
                )
            }
            //设置同一个layoutParams
            parentViewGroup!!.addView(loadingView, contentLayoutParams)
        }
        viewState = STATUS_LOADING
    }

    override fun showErrorState(view: View?) {
        if (viewState === STATUS_ERROR) {
            return
        }
        if (view != null) {
            errorView = view
            //设置同一个layoutParams
            parentViewGroup!!.addView(errorView, contentLayoutParams)
        } else if (errorView == null) {
            errorView = View.inflate(this, errorLayout, null)
            if (errorView == null) {
                throw IllegalStateException(
                    "A View should be named 'view_error' in ErrorLayoutResource."
                )
            }
            //设置同一个layoutParams
            parentViewGroup!!.addView(errorView, contentLayoutParams)
            errorView = parentViewGroup!!.findViewById(R.id.errorView)
        }
        viewState = STATUS_ERROR
    }

    override fun showStateEmpty(view: View?) {
        if (viewState === STATUS_EMPTY) {
            return
        }
        if (view != null) {
            emptyView = view
            //设置同一个layoutParams
            parentViewGroup!!.addView(emptyView, contentLayoutParams)
        } else if (emptyView == null) {
            emptyView = View.inflate(this, emptyLayout, null)
            if (emptyView == null) {
                throw IllegalStateException(
                    "A View should be named 'view_error' in ErrorLayoutResource."
                )
            }
            //设置同一个layoutParams
            parentViewGroup!!.addView(emptyView, contentLayoutParams)
        }
        viewState = STATUS_EMPTY
    }

    override fun showStateMain() {
        if (viewState === STATUS_MAIN) {
            return
        }
        viewState = STATUS_MAIN

    }

    /**
     * Returns the [View] associated with the [com.kennyc.view.MultiStateView.ViewState]
     *
     * @param state The [com.kennyc.view.MultiStateView.ViewState] with to return the view for
     * @return The [View] associated with the [com.kennyc.view.MultiStateView.ViewState], null if no view is present
     */
    @Nullable
    fun getView(state: MultiViewStatus): View? {
        return when (state) {
            STATUS_LOADING -> loadingView

            STATUS_MAIN -> mainView

            STATUS_EMPTY -> emptyView

            STATUS_ERROR -> errorView

            else -> throw IllegalArgumentException("Unknown ViewState $state")
        }
    }

    /**
     * Shows the [View] based on the [com.kennyc.view.MultiStateView.ViewState]
     */
    private fun setView(previousState: MultiViewStatus) {
        when (viewState) {
            STATUS_LOADING -> {
                requireNotNull(loadingView).apply {
                    mainView?.visibility = View.GONE
                    errorView?.visibility = View.GONE
                    emptyView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }

            STATUS_EMPTY -> {
                requireNotNull(emptyView).apply {
                    mainView?.visibility = View.GONE
                    errorView?.visibility = View.GONE
                    loadingView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }

            STATUS_ERROR -> {
                requireNotNull(errorView).apply {
                    mainView?.visibility = View.GONE
                    loadingView?.visibility = View.GONE
                    emptyView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }

            STATUS_MAIN -> {
                requireNotNull(mainView).apply {
                    loadingView?.visibility = View.GONE
                    errorView?.visibility = View.GONE
                    emptyView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }

            else -> {
                throw IllegalArgumentException("Unable to set state for value $viewState")
            }
        }
    }

    /**
     * Animates the layout changes between [ViewState]
     *
     * @param previousView The view that it was currently on
     */
    private fun animateLayoutChange(@Nullable previousView: View?) {
        if (previousView == null) {
            requireNotNull(getView(viewState)).visibility = View.VISIBLE
            return
        }

        ObjectAnimator.ofFloat(previousView, "alpha", 1.0f, 0.0f).apply {
            duration = 250L
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    previousView.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    previousView.visibility = View.GONE
                    val currentView = requireNotNull(getView(viewState))
                    currentView.visibility = View.VISIBLE
                    ObjectAnimator.ofFloat(currentView, "alpha", 0.0f, 1.0f).setDuration(250L).start()
                }
            })
        }.start()
    }

    interface StatusListener {
        /**
         * Callback for when the [ViewState] has changed
         *
         * @param viewState The [ViewState] that was switched to
         */
        fun onStateChanged(viewState: MultiViewStatus)
    }

}