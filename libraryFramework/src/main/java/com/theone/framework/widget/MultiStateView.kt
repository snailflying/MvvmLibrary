package com.theone.framework.widget
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import com.theone.framework.R
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @Author zhiqiang
 * @Date 2019-10-16
 * @Description 多状态View，补充MultiViewFragment或者MultiViewActivity
 *
 * 参考：https://github.com/Kennyc1012/MultiStateView
 */
class MultiStateView : FrameLayout {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(VIEW_STATE_UNKNOWN, VIEW_STATE_CONTENT, VIEW_STATE_ERROR, VIEW_STATE_EMPTY, VIEW_STATE_LOADING, VIEW_STATE_OTHER)
    annotation class ViewState

    private lateinit var mInflater: LayoutInflater
    private var mContentView: View? = null
    private var mLoadingView: View? = null
    private var mErrorView: View? = null
    private var mEmptyView: View? = null
    private var mOtherView: View? = null
    private var mAnimateViewChanges = false
    private var mListener: StateListener? = null
    @ViewState
    private var mViewState = VIEW_STATE_UNKNOWN

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        mInflater = LayoutInflater.from(context)
        val a = context.obtainStyledAttributes(attrs, R.styleable.MultiStateView)
        val loadingViewResId = a.getResourceId(R.styleable.MultiStateView_msv_loadingView, -1)
        if (loadingViewResId > -1) {
            mLoadingView = mInflater.inflate(loadingViewResId, this, false)
            addView(mLoadingView!!, mLoadingView!!.getLayoutParams())
        }
        val emptyViewResId = a.getResourceId(R.styleable.MultiStateView_msv_emptyView, -1)
        if (emptyViewResId > -1) {
            mEmptyView = mInflater.inflate(emptyViewResId, this, false)
            addView(mEmptyView!!, mEmptyView!!.getLayoutParams())
        }
        val ohterViewResId = a.getResourceId(R.styleable.MultiStateView_msv_otherView, -1)
        if (ohterViewResId > -1) {
            mOtherView = mInflater.inflate(ohterViewResId, this, false)
            addView(mOtherView!!, mOtherView!!.getLayoutParams())
        }
        val errorViewResId = a.getResourceId(R.styleable.MultiStateView_msv_errorView, -1)
        if (errorViewResId > -1) {
            mErrorView = mInflater.inflate(errorViewResId, this, false)
            addView(mErrorView!!, mErrorView!!.getLayoutParams())
        }
        val viewState = a.getInt(R.styleable.MultiStateView_msv_viewState, VIEW_STATE_CONTENT)
        mAnimateViewChanges = a.getBoolean(R.styleable.MultiStateView_msv_animateViewChanges, false)
        mViewState = when (viewState) {
            VIEW_STATE_CONTENT -> VIEW_STATE_CONTENT
            VIEW_STATE_ERROR -> VIEW_STATE_ERROR
            VIEW_STATE_EMPTY -> VIEW_STATE_EMPTY
            VIEW_STATE_LOADING -> VIEW_STATE_LOADING
            VIEW_STATE_OTHER -> VIEW_STATE_OTHER
            VIEW_STATE_UNKNOWN -> VIEW_STATE_UNKNOWN
            else -> VIEW_STATE_UNKNOWN
        }
        a.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        requireNotNull(mContentView) { "Content view is not defined" }
        setView(VIEW_STATE_UNKNOWN)
    }

    /* All of the addView methods have been overridden so that it can obtain the content view via XML
     It is NOT recommended to add views into MultiStateView via the addView methods, but rather use
     any of the setViewForState methods to set views for their given ViewState accordingly */
    override fun addView(child: View) {
        if (isValidContentView(child)) {
            mContentView = child
        }
        super.addView(child)
    }

    override fun addView(child: View, index: Int) {
        if (isValidContentView(child)) {
            mContentView = child
        }
        super.addView(child, index)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (isValidContentView(child)) {
            mContentView = child
        }
        super.addView(child, index, params)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (isValidContentView(child)) {
            mContentView = child
        }
        super.addView(child, params)
    }

    override fun addView(child: View, width: Int, height: Int) {
        if (isValidContentView(child)) {
            mContentView = child
        }
        super.addView(child, width, height)
    }

    override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams): Boolean {
        if (isValidContentView(child)) {
            mContentView = child
        }
        return super.addViewInLayout(child, index, params)
    }

    override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams, preventRequestLayout: Boolean): Boolean {
        if (isValidContentView(child)) {
            mContentView = child
        }
        return super.addViewInLayout(child, index, params, preventRequestLayout)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(superState!!, mViewState)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            val savedState = state
            super.onRestoreInstanceState(savedState.superState)
            viewState = savedState.state
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    /**
     * Returns the [View] associated with the [MultiStateView.ViewState]
     *
     * @param state The [MultiStateView.ViewState] with to return the view for
     * @return The [View] associated with the [MultiStateView.ViewState], null if no view is present
     */
    fun getView(@ViewState state: Int): View? {
        return when (state) {
            VIEW_STATE_LOADING -> mLoadingView
            VIEW_STATE_CONTENT -> mContentView
            VIEW_STATE_EMPTY -> mEmptyView
            VIEW_STATE_OTHER -> mOtherView
            VIEW_STATE_ERROR -> mErrorView
            else -> null
        }
    }

    /**
     * Returns the current [MultiStateView.ViewState]
     *
     * @return
     */
    /**
     * Sets the current [MultiStateView.ViewState]
     *
     * @param state The [MultiStateView.ViewState] to set [MultiStateView] to
     */
    @get:ViewState
    var viewState: Int
        get() = mViewState
        set(state) {
            if (state != mViewState) {
                val previous = mViewState
                mViewState = state
                setView(previous)
                if (mListener != null) {
                    mListener!!.onStateChanged(mViewState)
                }
            }
        }

    /**
     * Shows the [View] based on the [MultiStateView.ViewState]
     */
    private fun setView(@ViewState previousState: Int) {
        when (mViewState) {
            VIEW_STATE_LOADING -> {
                if (mLoadingView == null) {
                    return
                }
                if (mContentView != null) {
                    mContentView!!.visibility = View.GONE
                }
                if (mErrorView != null) {
                    mErrorView!!.visibility = View.GONE
                }
                if (mEmptyView != null) {
                    mEmptyView!!.visibility = View.GONE
                }
                if (mOtherView != null) {
                    mOtherView!!.visibility = View.GONE
                }
                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mLoadingView!!.visibility = View.VISIBLE
                }
            }
            VIEW_STATE_EMPTY -> {
                if (mEmptyView == null) {
                    return
                }
                if (mLoadingView != null) {
                    mLoadingView!!.visibility = View.GONE
                }
                if (mErrorView != null) {
                    mErrorView!!.visibility = View.GONE
                }
                if (mContentView != null) {
                    mContentView!!.visibility = View.GONE
                }
                if (mOtherView != null) {
                    mOtherView!!.visibility = View.GONE
                }
                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mEmptyView!!.visibility = View.VISIBLE
                }
            }
            VIEW_STATE_ERROR -> {
                if (mErrorView == null) {
                    return
                }
                if (mLoadingView != null) {
                    mLoadingView!!.visibility = View.GONE
                }
                if (mContentView != null) {
                    mContentView!!.visibility = View.GONE
                }
                if (mEmptyView != null) {
                    mEmptyView!!.visibility = View.GONE
                }
                if (mOtherView != null) {
                    mOtherView!!.visibility = View.GONE
                }
                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mErrorView!!.visibility = View.VISIBLE
                }
            }
            VIEW_STATE_OTHER -> {
                if (mOtherView == null) {
                    return
                }
                if (mLoadingView != null) {
                    mLoadingView!!.visibility = View.GONE
                }
                if (mContentView != null) {
                    mContentView!!.visibility = View.GONE
                }
                if (mEmptyView != null) {
                    mEmptyView!!.visibility = View.GONE
                }
                if (mErrorView != null) {
                    mErrorView!!.visibility = View.GONE
                }
                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mOtherView!!.visibility = View.VISIBLE
                }
            }
            VIEW_STATE_CONTENT -> {
                if (mContentView == null) {
                    return
                }
                if (mLoadingView != null) {
                    mLoadingView!!.visibility = View.GONE
                }
                if (mErrorView != null) {
                    mErrorView!!.visibility = View.GONE
                }
                if (mEmptyView != null) {
                    mEmptyView!!.visibility = View.GONE
                }
                if (mOtherView != null) {
                    mOtherView!!.visibility = View.GONE
                }
                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mContentView!!.visibility = View.VISIBLE
                }
            }
            else -> {
                if (mContentView == null) {
                    return
                }
                if (mLoadingView != null) {
                    mLoadingView!!.visibility = View.GONE
                }
                if (mErrorView != null) {
                    mErrorView!!.visibility = View.GONE
                }
                if (mEmptyView != null) {
                    mEmptyView!!.visibility = View.GONE
                }
                if (mOtherView != null) {
                    mOtherView!!.visibility = View.GONE
                }
                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mContentView!!.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Checks if the given [View] is valid for the Content View
     *
     * @param view The [View] to check
     * @return
     */
    private fun isValidContentView(view: View): Boolean {
        return if (mContentView != null && mContentView !== view) {
            false
        } else view !== mLoadingView && view !== mErrorView && view !== mEmptyView && view !== mOtherView
    }

    /**
     * Sets the view for the given view state
     *
     * @param view          The [View] to use
     * @param state         The [MultiStateView.ViewState]to set
     * @param switchToState If the [MultiStateView.ViewState] should be switched to
     */
    fun setViewForState(view: View?, @ViewState state: Int, switchToState: Boolean) {
        when (state) {
            VIEW_STATE_LOADING -> {
                if (mLoadingView != null) {
                    removeView(mLoadingView)
                }
                mLoadingView = view
                addView(mLoadingView!!)
            }
            VIEW_STATE_EMPTY -> {
                if (mEmptyView != null) {
                    removeView(mEmptyView)
                }
                mEmptyView = view
                addView(mEmptyView!!)
            }
            VIEW_STATE_ERROR -> {
                if (mErrorView != null) {
                    removeView(mErrorView)
                }
                mErrorView = view
                addView(mErrorView!!)
            }
            VIEW_STATE_OTHER -> {
                if (mOtherView != null) {
                    removeView(mOtherView)
                }
                mOtherView = view
                addView(mOtherView!!)
            }
            VIEW_STATE_CONTENT -> {
                if (mContentView != null) {
                    removeView(mContentView)
                }
                mContentView = view
                addView(mContentView!!)
            }
            else -> {
            }
        }
        setView(VIEW_STATE_UNKNOWN)
        if (switchToState) {
            viewState = state
        }
    }

    /**
     * Sets the [View] for the given [MultiStateView.ViewState]
     *
     * @param view  The [View] to use
     * @param state The [MultiStateView.ViewState] to set
     */
    fun setViewForState(view: View?, @ViewState state: Int) {
        setViewForState(view, state, false)
    }

    /**
     * Sets the [View] for the given [MultiStateView.ViewState]
     *
     * @param layoutRes     Layout resource id
     * @param state         The [MultiStateView.ViewState] to set
     * @param switchToState If the [MultiStateView.ViewState] should be switched to
     */
    fun setViewForState(@LayoutRes layoutRes: Int, @ViewState state: Int, switchToState: Boolean) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(context)
        }
        val view = mInflater!!.inflate(layoutRes, this, false)
        setViewForState(view, state, switchToState)
    }

    /**
     * Sets the [View] for the given [MultiStateView.ViewState]
     *
     * @param layoutRes Layout resource id
     * @param state     The [View] state to set
     */
    fun setViewForState(@LayoutRes layoutRes: Int, @ViewState state: Int) {
        setViewForState(layoutRes, state, false)
    }

    /**
     * Sets whether an animate will occur when changing between [ViewState]
     *
     * @param animate
     */
    fun setAnimateLayoutChanges(animate: Boolean) {
        mAnimateViewChanges = animate
    }

    /**
     * Sets the [StateListener] for the view
     *
     * @param listener The [StateListener] that will receive callbacks
     */
    fun setStateListener(listener: StateListener?) {
        mListener = listener
    }

    /**
     * Animates the layout changes between [ViewState]
     *
     * @param previousView The view that it was currently on
     */
    private fun animateLayoutChange(previousView: View?) {
        if (previousView == null) {
            getView(mViewState)!!.visibility = View.VISIBLE
            return
        }
        previousView.visibility = View.VISIBLE
        val anim = ObjectAnimator.ofFloat(previousView, "alpha", 1.0f, 0.0f).setDuration(250L)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                previousView.visibility = View.GONE
                getView(mViewState)!!.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(getView(mViewState), "alpha", 0.0f, 1.0f).setDuration(250L).start()
            }
        })
        anim.start()
    }

    interface StateListener {
        /**
         * Callback for when the [ViewState] has changed
         *
         * @param viewState The [ViewState] that was switched to
         */
        fun onStateChanged(@ViewState viewState: Int)
    }

    private class SavedState : BaseSavedState {
        val state: Int

        constructor(superState: Parcelable, state: Int) : super(superState) {
            this.state = state
        }

        private constructor(`in`: Parcel) : super(`in`) {
            state = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(state)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        const val VIEW_STATE_UNKNOWN = -1
        const val VIEW_STATE_CONTENT = 0
        const val VIEW_STATE_ERROR = 1
        const val VIEW_STATE_EMPTY = 2
        const val VIEW_STATE_LOADING = 3
        const val VIEW_STATE_OTHER = 4
    }
}