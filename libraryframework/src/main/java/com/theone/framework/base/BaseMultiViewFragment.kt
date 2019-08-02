package com.theone.framework.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import com.themone.core.base.IViewModel
import com.themone.core.base.impl.BaseMvvmFragment
import com.theone.framework.R
import com.theone.framework.base.multi.IMultiStateProvider
import com.theone.framework.base.multi.MultiViewState
import com.theone.framework.base.multi.MultiViewState.*

/**
 * @author zhiqiang
 * @date 2019-06-06
 * @desc 提供默认的 loading、error、empty 等状态 UI
 * 需要更改状态的 view Id 强制规定命名 为 id=@+id/multiStateView
 * 参考：https://github.com/Kennyc1012/MultiStateView
 */
abstract class BaseMultiViewFragment<VM : IViewModel> : BaseMvvmFragment<VM>(),
    IMultiStateProvider {

    private var errorView: View? = null
    private var emptyView: View? = null
    private var loadingView: View? = null
    private var contentView: View? = null
    private var parentViewGroup: ViewGroup? = null
    protected var viewState = STATE_MAIN
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
    protected open val loadingLayout: Int = R.layout.view_loading

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUiStatus(view, savedInstanceState)
    }

    private fun initUiStatus(view: View, savedInstanceState: Bundle?) {
        contentView = view.findViewById(R.id.multiStateView)
        if (contentView == null) {
            throw IllegalStateException(
                "The subclass of RootActivity must contain a View named 'view_main'."
            )
        }
        if (contentView!!.parent !is ViewGroup) {
            throw IllegalStateException(
                "view_main's ParentView should be a ViewGroup."
            )
        }
        contentLayoutParams = contentView!!.layoutParams
        parentViewGroup = contentView!!.parent as ViewGroup
    }

    override fun showStateView(state: MultiViewState) {
        when (state) {
            STATE_MAIN -> showStateMain()
            STATE_EMPTY -> showStateEmpty()
            STATE_ERROR -> showStateError()
            STATE_LOADING -> showStateLoading()
        }
    }

    override fun showStateLoading(view: View?) {
        if (viewState === STATE_LOADING) {
            return
        }
        if (view != null) {
            loadingView = view
            //设置同一个layoutParams
            parentViewGroup!!.addView(loadingView, contentLayoutParams)
        } else if (null == loadingView) {
            loadingView = View.inflate(mContext, loadingLayout, null)
            if (null == loadingView) {
                throw IllegalStateException(
                    "A View should be named 'view_progresss' in viewLoadingResource."
                )
            }
            //设置同一个layoutParams
            parentViewGroup!!.addView(loadingView, contentLayoutParams)
        }
        viewState = STATE_LOADING
    }

    override fun showStateError(view: View?) {
        if (viewState === STATE_ERROR) {
            return
        }
        if (view != null) {
            errorView = view
            //设置同一个layoutParams
            parentViewGroup!!.addView(errorView, contentLayoutParams)
        } else if (errorView == null) {
            errorView = View.inflate(mContext, errorLayout, null)
            if (errorView == null) {
                throw IllegalStateException(
                    "A View should be named 'view_error' in ErrorLayoutResource."
                )
            }
            //设置同一个layoutParams
            parentViewGroup!!.addView(errorView, contentLayoutParams)
            errorView = parentViewGroup!!.findViewById(R.id.errorView)
        }
        viewState = STATE_ERROR
    }

    override fun showStateEmpty(view: View?) {
        if (viewState === STATE_EMPTY) {
            return
        }
        if (view != null) {
            emptyView = view
            //设置同一个layoutParams
            parentViewGroup!!.addView(emptyView, contentLayoutParams)
        } else if (emptyView == null) {
            emptyView = View.inflate(mContext, emptyLayout, null)
            if (emptyView == null) {
                throw IllegalStateException(
                    "A View should be named 'view_error' in ErrorLayoutResource."
                )
            }
            //设置同一个layoutParams
            parentViewGroup!!.addView(emptyView, contentLayoutParams)
        }
        viewState = STATE_EMPTY
    }

    override fun showStateMain() {
        if (viewState === STATE_MAIN) {
            return
        }
        viewState = STATE_MAIN

    }

    /**
     * Returns the [View] associated with the [com.kennyc.view.MultiStateView.ViewState]
     *
     * @param state The [com.kennyc.view.MultiStateView.ViewState] with to return the view for
     * @return The [View] associated with the [com.kennyc.view.MultiStateView.ViewState], null if no view is present
     */
    @Nullable
    fun getView(state: MultiViewState): View? {
        return when (state) {
            STATE_LOADING -> loadingView

            STATE_MAIN -> contentView

            STATE_EMPTY -> emptyView

            STATE_ERROR -> errorView

            else -> throw IllegalArgumentException("Unknown ViewState $state")
        }
    }

    /**
     * Shows the [View] based on the [com.kennyc.view.MultiStateView.ViewState]
     */
    private fun setView(previousState: MultiViewState) {
        when (viewState) {
            STATE_LOADING -> {
                requireNotNull(loadingView).apply {
                    contentView?.visibility = View.GONE
                    errorView?.visibility = View.GONE
                    emptyView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }

            STATE_EMPTY -> {
                requireNotNull(emptyView).apply {
                    contentView?.visibility = View.GONE
                    errorView?.visibility = View.GONE
                    loadingView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }

            STATE_ERROR -> {
                requireNotNull(errorView).apply {
                    contentView?.visibility = View.GONE
                    loadingView?.visibility = View.GONE
                    emptyView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }

            STATE_MAIN -> {
                requireNotNull(contentView).apply {
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
        fun onStateChanged(viewState: MultiViewState)
    }
}
