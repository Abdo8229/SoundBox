package com.example.extensions.bottomsheet

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.ParcelableCompat
import androidx.core.os.ParcelableCompatCreatorCallbacks
import androidx.core.view.NestedScrollingChild
import androidx.core.view.ViewCompat
import androidx.customview.view.AbsSavedState
import androidx.customview.widget.ViewDragHelper
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.extensions.R
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.ref.WeakReference

class CustomBottomSheetBehavior<V : View> : CoordinatorLayout.Behavior<V> {
    // Important add your package here or don't delete package line from your created class.
    // Make sure the java file name & this class name, matches.
    val TAG = this.javaClass.simpleName

    /**
     * Callback for monitoring events about bottom sheets.
     */
    abstract class BottomSheetCallback {
        /**
         * Called when the bottom sheet changes its state.
         *
         * @param bottomSheet The bottom sheet view.
         * @param oldState    The old state. This will be one of [.STATE_DRAGGING],
         * [.STATE_SETTLING], [.STATE_EXPANDED],
         * [.STATE_COLLAPSED], [.STATE_ANCHORED] or
         * [.STATE_HIDDEN].
         * @param newState    The new state. This will be one of [.STATE_DRAGGING],
         * [.STATE_SETTLING], [.STATE_EXPANDED],
         * [.STATE_COLLAPSED], [.STATE_ANCHORED] or
         * [.STATE_HIDDEN].
         */
        abstract fun onStateChanged(bottomSheet: View, @State oldState: Int, @State newState: Int)

        /**
         * Called when the bottom sheet is being dragged.
         *
         * @param bottomSheet The bottom sheet view.
         * @param slideOffset The new offset of this bottom sheet within [-1,1] range. Offset
         * increases as this bottom sheet is moving upward. From 0 to 1 the sheet
         * is between collapsed and expanded states and from -1 to 0 it is
         * between hidden and collapsed states.
         */
        abstract fun onSlide(bottomSheet: View, slideOffset: Float)
    }

    /**
     * Stub/no-op implementations of all methods of [BottomSheetCallback].
     * Override this if you only care about a few of the available callback methods.
     */
    abstract class SimpleBottomSheetCallback : BottomSheetCallback() {
        override fun onStateChanged(
            bottomSheet: View,
            @State oldState: Int,
            @State newState: Int
        ) {
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.GROUP_ID)
    @IntDef(*[STATE_EXPANDED, STATE_COLLAPSED, STATE_DRAGGING, STATE_SETTLING, STATE_HIDDEN, STATE_ANCHORED])
    @Retention(
        RetentionPolicy.SOURCE
    )
    annotation class State

    private var mMinimumVelocity = 0f
    private var mMaximumVelocity = 0f
    private var mPeekHeight = 0
    private var mPeekHeightAuto = false

    @get:VisibleForTesting
    var peekHeightMin = 0
        private set
    private var mAnchorOffset = 0
    var allowUserDragging = true
    var mMinOffset = 0
    var mMaxOffset = 0
    /**
     * Gets whether this bottom sheet can hide when it is swiped down.
     *
     * @return `true` if this bottom sheet can hide.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_hideable
     */
    /**
     * Sets whether this bottom sheet can hide when it is swiped down.
     *
     * @param hideable `true` to make this bottom sheet hideable.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_hideable
     */
    var isHideable = false
    /**
     * Sets whether this bottom sheet should skip the collapsed state when it is being hidden
     * after it is expanded once.
     *
     * @return Whether the bottom sheet should skip the collapsed state.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_skipCollapsed
     */
    /**
     * Sets whether this bottom sheet should skip the collapsed state when it is being hidden
     * after it is expanded once. Setting this to true has no effect unless the sheet is hideable.
     *
     * @param skipCollapsed True if the bottom sheet should skip the collapsed state.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_skipCollapsed
     */
    var skipCollapsed = false
    /**
     * Sets whether this bottom sheet should skip the anchored state when it is being collapsed from
     * an expanded state or when it is being expanded from a collapsed state.
     *
     * @return Whether the bottom sheet should skip the anchored state.
     * @attr ref R.styleable#AnchorBottomSheetBehavior_Layout_behavior_skipAnchored
     */
    /**
     * Sets whether this bottom sheet should skip the anchored state when it is being collapsed from
     * an expanded state or when it is being expanded from a collapsed state.
     *
     * @param skipAnchored True if the bottom sheet should skip the anchored state.
     * @attr ref R.styleable#AnchorBottomSheetBehavior_Layout_behavior_skipAnchored
     */
    var skipAnchored = false
    var isDisableExpanded = false

    @State
    var mState = STATE_COLLAPSED

    @State
    var mPrevState = STATE_COLLAPSED
    var mViewDragHelper: ViewDragHelper? = null
    private var mIgnoreEvents = false
    private var mNestedScrolled = false
    var mParentHeight = 0
    var mViewRef: WeakReference<V>? = null
    var mNestedScrollingChildRef: WeakReference<View?>? = null
    private val mCallbacks: MutableList<BottomSheetCallback> = ArrayList(2)
    private var mVelocityTracker: VelocityTracker? = null
    var mActivePointerId = 0
    private var mInitialY = 0
    var mTouchingScrollingChild = false
    private var mMediaPlayerBarHeight = 0

    /**
     * Default constructor for instantiating AnchorBottomSheetBehaviors.
     */
    constructor() {}

    /**
     * Default constructor for inflating AnchorBottomSheetBehaviors from layout.
     *
     * @param context The [Context].
     * @param attrs   The [AttributeSet].
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        var a = context.obtainStyledAttributes(
            attrs,
            com.google.android.material.R.styleable.BottomSheetBehavior_Layout
        )
        val value =
            a.peekValue(com.google.android.material.R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight)
        if (value != null && value.data == PEEK_HEIGHT_AUTO) {
            peekHeight = value.data
        } else {
            peekHeight = a.getDimensionPixelSize(
                com.google.android.material.R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight,
                PEEK_HEIGHT_AUTO
            )
        }
        isHideable = a.getBoolean(
            com.google.android.material.R.styleable.BottomSheetBehavior_Layout_behavior_hideable,
            false
        )
        skipCollapsed = a.getBoolean(
            com.google.android.material.R.styleable.BottomSheetBehavior_Layout_behavior_skipCollapsed,
            false
        )
        skipAnchored =
            a.getBoolean(R.styleable.CustomBottomSheetBehavior_behavior_skipAnchored, false)
        a.recycle()
        a = context.obtainStyledAttributes(attrs, R.styleable.CustomBottomSheetBehavior)
        mAnchorOffset =
            a.getDimension(R.styleable.CustomBottomSheetBehavior_behavior_anchorOffset, 0f).toInt()
        mState = a.getInt(R.styleable.CustomBottomSheetBehavior_behavior_defaultState, mState)
        a.recycle()
        val configuration = ViewConfiguration.get(context)
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity.toFloat()
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
    }

    fun setMediaPlayerBarHeight(mediaPlayerBarHeight: Int) {
        mMediaPlayerBarHeight = mediaPlayerBarHeight
    }

    fun invalidateScrollingChild() {
        val scrollingChild = findScrollingChild(mViewRef!!.get())
        mNestedScrollingChildRef = WeakReference(scrollingChild)
    }

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: V): Parcelable? {
        return SavedState(super.onSaveInstanceState(parent, child), mState)
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: V, state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(parent, child, ss.superState!!)
        // Intermediate states are restored as collapsed state
        mState =
            if (ss.state == STATE_DRAGGING || ss.state == STATE_SETTLING) {
                STATE_COLLAPSED
            } else {
                ss.state
            }
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child!!)) {
            ViewCompat.setFitsSystemWindows(child, true)
        }
        val savedTop = child!!.top
        // First let the parent lay it out
        parent.onLayoutChild(child, layoutDirection)
        // Offset the bottom sheet
        mParentHeight = parent.height
        val peekHeight: Int
        if (mPeekHeightAuto) {
            if (peekHeightMin == 0) {
                peekHeightMin = parent.resources.getDimensionPixelSize(
                    com.google.android.material.R.dimen.design_bottom_sheet_peek_height_min
                )
            }
            peekHeight = Math.max(peekHeightMin, mParentHeight - parent.width * 9 / 16)
        } else {
            peekHeight = mPeekHeight
        }
        mMinOffset = Math.max(0, mParentHeight - child.height)
        if (isDisableExpanded) {
            mMinOffset = mAnchorOffset
        }
        mMaxOffset = Math.max(mParentHeight - peekHeight, mMinOffset)
        if (mState == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, mMinOffset)
        } else if (isHideable && mState == STATE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, mParentHeight)
        } else if (mState == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, mMaxOffset)
        } else if (mState == STATE_DRAGGING || mState == STATE_SETTLING) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.top)
        } else if (mState == STATE_ANCHORED) {
            if (mAnchorOffset > mMinOffset) {
                ViewCompat.offsetTopAndBottom(child, mAnchorOffset)
            } else {
                mState = STATE_EXPANDED
                ViewCompat.offsetTopAndBottom(child, mMinOffset)
            }
        }
        if (mViewDragHelper == null) {
            mViewDragHelper = ViewDragHelper.create(parent, mDragCallback)
        }
        mViewRef = WeakReference(child)
        mNestedScrollingChildRef = WeakReference(findScrollingChild(child))
        return true
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V,
        event: MotionEvent
    ): Boolean {
        if (!child!!.isShown || !allowUserDragging) {
            mIgnoreEvents = true
            return false
        }
        val action = event.actionMasked
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset()
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        when (action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mTouchingScrollingChild = false
                mActivePointerId = MotionEvent.INVALID_POINTER_ID
                // Reset the ignore flag
                if (mIgnoreEvents) {
                    mIgnoreEvents = false
                    return false
                }
            }

            MotionEvent.ACTION_DOWN -> {
                val initialX = event.x.toInt()
                mInitialY = event.y.toInt()
                val scroll = mNestedScrollingChildRef!!.get()
                if (scroll != null && parent.isPointInChildBounds(scroll, initialX, mInitialY)) {
                    mActivePointerId = event.getPointerId(event.actionIndex)
                    mTouchingScrollingChild = true
                }
                mIgnoreEvents = mActivePointerId == MotionEvent.INVALID_POINTER_ID &&
                        !parent.isPointInChildBounds(child, initialX, mInitialY)
            }
        }
        if (!mIgnoreEvents && mViewDragHelper!!.shouldInterceptTouchEvent(event)) {
            return true
        }
        // We have to handle cases that the ViewDragHelper does not capture the bottom sheet because
        // it is not the top most view of its parent. This is not necessary when the touch event is
        // happening over the scrolling content as nested scrolling logic handles that case.
        val scroll = mNestedScrollingChildRef!!.get()
        return action == MotionEvent.ACTION_MOVE && scroll != null &&
                !mIgnoreEvents && mState != STATE_DRAGGING &&
                !parent.isPointInChildBounds(scroll, event.x.toInt(), event.y.toInt()) && Math.abs(
            mInitialY - event.y
        ) > mViewDragHelper!!.touchSlop
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        if (!child!!.isShown || !allowUserDragging) {
            return false
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (state != STATE_DRAGGING) {
                mPrevState = state
                Log.i(TAG, "Set Prev State to : " + state)
            }
        }
        val action = event.actionMasked
        if (mState == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
            return true
        }
        if (mViewDragHelper != null) {
            mViewDragHelper!!.processTouchEvent(event)
        }
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset()
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        // The ViewDragHelper tries to capture only the top-most View. We have to explicitly tell it
        // to capture the bottom sheet in case it is not captured and the touch slop is passed.
        if (action == MotionEvent.ACTION_MOVE && !mIgnoreEvents) {
            if (Math.abs(mInitialY - event.y) > mViewDragHelper!!.touchSlop) {
                mViewDragHelper!!.captureChildView(child, event.getPointerId(event.actionIndex))
            }
        }
        return !mIgnoreEvents
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: V,
        directTargetChild: View, target: View, nestedScrollAxes: Int
    ): Boolean {
        if (!allowUserDragging) {
            return false
        }
        mNestedScrolled = false
        return nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int,
        dy: Int, consumed: IntArray
    ) {
        if (!allowUserDragging) {
            return
        }
        val scrollingChild = mNestedScrollingChildRef!!.get()
        if (target !== scrollingChild) {
            return
        }
        val currentTop = child!!.top
        val newTop = currentTop - dy
        if (dy > 0) { // Upward
            if (newTop < mMinOffset) {
                consumed[1] = currentTop - mMinOffset
                ViewCompat.offsetTopAndBottom(child, -consumed[1])
                setStateInternal(STATE_EXPANDED)
            } else {
                consumed[1] = dy
                ViewCompat.offsetTopAndBottom(child, -dy)
                setStateInternal(STATE_DRAGGING)
            }
        } else if (dy < 0) { // Downward
            if (!ViewCompat.canScrollVertically(target, -1)) {
                if (newTop <= mMaxOffset || isHideable) {
                    consumed[1] = dy
                    ViewCompat.offsetTopAndBottom(child, -dy)
                    setStateInternal(STATE_DRAGGING)
                } else {
                    consumed[1] = currentTop - mMaxOffset
                    ViewCompat.offsetTopAndBottom(child, -consumed[1])
                    setStateInternal(STATE_COLLAPSED)
                }
            }
        }
        dispatchOnSlide(child.top)
        mNestedScrolled = true
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View) {
        if (!allowUserDragging) {
            return
        }
        if (child!!.top == mMinOffset) {
            setStateInternal(STATE_EXPANDED)
            return
        }
        if (target !== mNestedScrollingChildRef!!.get() || !mNestedScrolled) {
            return
        }
        mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity)
        val xvel = mVelocityTracker!!.getXVelocity(mActivePointerId)
        val yvel = mVelocityTracker!!.getYVelocity(mActivePointerId)
        val out = IntArray(2)
        calculateTopAndTargetState(child, xvel, yvel, out)
        val top = out[0]
        val targetState = out[1]
        if (mViewDragHelper!!.smoothSlideViewTo(child, child.left, top)) {
            setStateInternal(STATE_SETTLING)
            ViewCompat.postOnAnimation(child, SettleRunnable(child, targetState))
        } else {
            setStateInternal(targetState)
        }
        mNestedScrolled = false
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout, child: V, target: View,
        velocityX: Float, velocityY: Float
    ): Boolean {
        return if (!allowUserDragging) {
            false
        } else target === mNestedScrollingChildRef!!.get() &&
                (mState != STATE_EXPANDED ||
                        super.onNestedPreFling(
                            coordinatorLayout, child, target,
                            velocityX, velocityY
                        ))
    }
    /**
     * Gets the height of the bottom sheet when it is collapsed.
     *
     * @return The height of the collapsed bottom sheet in pixels, or [.PEEK_HEIGHT_AUTO]
     * if the sheet is configured to peek automatically at 16:9 ratio keyline
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_peekHeight
     */
    /**
     * Sets the height of the bottom sheet when it is collapsed.
     *
     * @param peekHeight The height of the collapsed bottom sheet in pixels, or
     * [.PEEK_HEIGHT_AUTO] to configure the sheet to peek automatically
     * at 16:9 ratio keyline.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_peekHeight
     */
    var peekHeight: Int
        get() = if (mPeekHeightAuto) PEEK_HEIGHT_AUTO else mPeekHeight
        set(peekHeight) {
            var layout = false
            if (peekHeight == PEEK_HEIGHT_AUTO) {
                if (!mPeekHeightAuto) {
                    mPeekHeightAuto = true
                    layout = true
                }
            } else if (mPeekHeightAuto || mPeekHeight != peekHeight) {
                mPeekHeightAuto = false
                mPeekHeight = Math.max(0, peekHeight)
                mMaxOffset = mParentHeight - peekHeight
                layout = true
            }
            if (layout && mState == STATE_COLLAPSED && mViewRef != null) {
                val view = mViewRef!!.get()
                view?.requestLayout()
            }
        }
    /**
     * Gets the offset of the bottom sheet when it is anchored.
     *
     * @return The offset of the anchored bottom sheet in pixels.
     * @attr ref com.trafi.anchorbottomsheetbehavior.R.styleable#AnchorBottomSheetBehavior_Layout_behavior_anchorOffset
     */
    /**
     * Sets the offset of the bottom sheet when it is anchored.
     *
     * @param anchorOffset The offset of the anchored bottom sheet in pixels.
     * @attr ref com.trafi.anchorbottomsheetbehavior.R.styleable#AnchorBottomSheetBehavior_Layout_behavior_anchorOffset
     */
    var anchorOffset: Int
        get() = mAnchorOffset
        set(anchorOffset) {
            if (mAnchorOffset != anchorOffset) {
                mAnchorOffset = anchorOffset
                if (isDisableExpanded) {
                    mMinOffset = mAnchorOffset
                }
                if (mState == STATE_ANCHORED) {
                    setStateInternal(STATE_SETTLING)
                    state = STATE_ANCHORED
                }
            }
        }

    /**
     * Registers a callback to be notified of bottom sheet events.
     *
     * @param callback The callback to notify when bottom sheet events occur.
     */
    fun addBottomSheetCallback(callback: BottomSheetCallback) {
        mCallbacks.add(callback)
    }

    fun removeBottomSheetCallback(callback: BottomSheetCallback) {
        mCallbacks.remove(callback)
    }
    /**
     * Gets the current state of the bottom sheet.
     *
     * @return One of [.STATE_EXPANDED], [.STATE_ANCHORED], [.STATE_COLLAPSED],
     * [.STATE_DRAGGING], and [.STATE_SETTLING].
     */// The view is not laid out yet; modify mState and let onLayoutChild handle it later
    // Start the animation; wait until a pending layout if there is one.
    /**
     * Sets the state of the bottom sheet. The bottom sheet will transition to that state with
     * animation.
     *
     * @param state One of [.STATE_COLLAPSED], [.STATE_EXPANDED],
     * [.STATE_ANCHORED] or[.STATE_HIDDEN].
     */
    @get:State
    var state: Int
        get() = mState
        set(state) {
            if (state == mState) {
                return
            }
            if (mViewRef == null) {
                // The view is not laid out yet; modify mState and let onLayoutChild handle it later
                if (state == STATE_COLLAPSED || state == STATE_EXPANDED || state == STATE_ANCHORED || isHideable && state == STATE_HIDDEN) {
                    mState = state
                }
                return
            }
            val child = mViewRef!!.get() ?: return
            // Start the animation; wait until a pending layout if there is one.
            val parent = child.parent
            if (parent != null && parent.isLayoutRequested && ViewCompat.isAttachedToWindow(child)) {
                child.post { startSettlingAnimation(child, state) }
            } else {
                startSettlingAnimation(child, state)
            }
        }

    fun setStateInternal(@State state: Int) {
        if (mState == state) {
            return
        }
        val oldState = mState
        mState = state
        val bottomSheet: View? = mViewRef!!.get()
        if (bottomSheet != null) {
            for (i in mCallbacks.indices) {
                mCallbacks[i].onStateChanged(bottomSheet, oldState, state)
            }
        }
    }

    private fun reset() {
        mActivePointerId = ViewDragHelper.INVALID_POINTER
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    private fun calculateTopAndTargetState(child: View, xvel: Float, yvel: Float, out: IntArray) {
        val top: Int
        @State val targetState: Int
        if (yvel < 0 && Math.abs(yvel) > mMinimumVelocity && Math.abs(yvel) > Math.abs(xvel)) {
            // scrolling up, i.e. expanding
            if (shouldExpand(child, yvel)) {
                if (mPrevState == STATE_COLLAPSED) {
                    top = mAnchorOffset
                    targetState = STATE_ANCHORED
                } else {
                    top = mMinOffset
                    targetState = STATE_EXPANDED
                }
            } else {
                top = mAnchorOffset
                targetState = STATE_ANCHORED
            }
        } else if (isHideable && shouldHide(child, yvel)) {
            top = mParentHeight
            targetState = STATE_HIDDEN
        } else if (yvel > 0 && Math.abs(yvel) > mMinimumVelocity && Math.abs(yvel) > Math.abs(xvel)) {
            // scrolling down, i.e. collapsing
            if (shouldCollapse(child, yvel)) {
                if (mPrevState == STATE_EXPANDED) {
                    top = mAnchorOffset
                    targetState = STATE_ANCHORED
                } else {
                    top = mMaxOffset
                    targetState = STATE_COLLAPSED
                }
            } else {
                top = mAnchorOffset
                targetState = STATE_ANCHORED
            }
        } else {
            // not scrolling much, i.e. stationary
            val currentTop = child.top
            val distanceToExpanded = Math.abs(currentTop - mMinOffset)
            val distanceToCollapsed = Math.abs(currentTop - mMaxOffset)
            val distanceToAnchor = Math.abs(currentTop - mAnchorOffset)
            if (mAnchorOffset > mMinOffset && distanceToAnchor < distanceToExpanded && distanceToAnchor < distanceToCollapsed) {
                top = mAnchorOffset
                targetState = STATE_ANCHORED
            } else if (distanceToExpanded < distanceToCollapsed) {
                top = mMinOffset
                targetState = STATE_EXPANDED
            } else {
                top = mMaxOffset
                targetState = STATE_COLLAPSED
            }
        }
        out[0] = top
        out[1] = targetState
    }

    fun shouldHide(child: View, yvel: Float): Boolean {
        if (skipCollapsed) {
            return true
        }
        if (child.top < mMaxOffset) {
            // It should not hide, but collapse.
            return false
        }
        val newTop = child.top + yvel * HIDE_FRICTION
        return Math.abs(newTop - mMaxOffset) / mPeekHeight.toFloat() > HIDE_THRESHOLD
    }

    fun shouldExpand(child: View, yvel: Float): Boolean {
        if (skipAnchored || mMinOffset >= mAnchorOffset) {
            return true
        }
        val currentTop = child.top
        if (currentTop < mAnchorOffset) {
            return true
        }
        val newTop = currentTop + yvel * EXPAND_FRICTION
        return newTop < mAnchorOffset
    }

    fun shouldCollapse(child: View, yvel: Float): Boolean {
        if (skipAnchored || mMinOffset >= mAnchorOffset) {
            return true
        }
        val currentTop = child.top
        if (currentTop > mAnchorOffset) {
            return true
        }
        val newTop = currentTop + yvel * COLLAPSE_FRICTION
        return newTop > mAnchorOffset
    }

    private fun findScrollingChild(view: View?): View? {
        if (view is NestedScrollingChild) {
            return view
        }
        //ToDo
        if (view is ViewPager2) {
            val viewPager = view
            /*View currentViewPagerChild = ViewPager2Utils.getCurrentView(viewPager);
            View scrollingChild = findScrollingChild(currentViewPagerChild);
            if (scrollingChild != null) {
                return scrollingChild;
            }*/
        } else if (view is ViewPager) {
            val viewPager = view
            /*View currentViewPagerChild = ViewPagerUtils.getCurrentView(viewPager);
            View scrollingChild = findScrollingChild(currentViewPagerChild);
            if (scrollingChild != null) {
                return scrollingChild;
            }*/
        } else if (view is ViewGroup) {
            val group = view
            var i = 0
            val count = group.childCount
            while (i < count) {
                val scrollingChild = findScrollingChild(group.getChildAt(i))
                if (scrollingChild != null) {
                    return scrollingChild
                }
                i++
            }
        }
        return null
    }

    fun startSettlingAnimation(child: View, state: Int) {
        var state = state
        val top: Int
        if (state == STATE_COLLAPSED) {
            top = mMaxOffset
        } else if (state == STATE_EXPANDED) {
            top = mMinOffset
        } else if (state == STATE_ANCHORED) {
            if (mAnchorOffset > mMinOffset) {
                top = mAnchorOffset
            } else {
                state = STATE_EXPANDED
                top = mMinOffset
            }
        } else if (isHideable && state == STATE_HIDDEN) {
            top = mParentHeight
        } else {
            throw IllegalArgumentException("Illegal state argument: $state")
        }
        setStateInternal(STATE_SETTLING)
        if (mViewDragHelper!!.smoothSlideViewTo(child, child.left, top)) {
            ViewCompat.postOnAnimation(child, SettleRunnable(child, state))
        }
    }

    private val mDragCallback: ViewDragHelper.Callback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (mState == STATE_DRAGGING) {
                return false
            }
            if (mTouchingScrollingChild) {
                return false
            }
            if (mState == STATE_EXPANDED && mActivePointerId == pointerId) {
                val scroll = mNestedScrollingChildRef!!.get()
                if (scroll != null && ViewCompat.canScrollVertically(scroll, -1)) {
                    // Let the content scroll up
                    return false
                }
            }
            return mViewRef != null && mViewRef!!.get() === child
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            dispatchOnSlide(top)
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == ViewDragHelper.STATE_DRAGGING) {
                setStateInternal(STATE_DRAGGING)
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val out = IntArray(2)
            calculateTopAndTargetState(releasedChild, xvel, yvel, out)
            val top = out[0]
            @State val targetState = out[1]
            if (mViewDragHelper!!.settleCapturedViewAt(releasedChild.left, top)) {
                setStateInternal(STATE_SETTLING)
                ViewCompat.postOnAnimation(
                    releasedChild,
                    SettleRunnable(releasedChild, targetState)
                )
            } else {
                setStateInternal(targetState)
            }
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            if (mPrevState == STATE_COLLAPSED) {
                //Log.i(TAG, "Prev State is Collapsed, Top = " + (mParentHeight - (getAnchorOffset()+getPeekHeight())));
                val maxTop: Int = anchorOffset - mMediaPlayerBarHeight
                //                    mMediaPlayerBarHeight = 64
                return Math.max(top, maxTop)
            }
            return if (mPrevState == STATE_EXPANDED) {
                //Log.i(TAG, "Prev State is Collapsed, Top = " + (mParentHeight - (getAnchorOffset()+getPeekHeight())));
                Log.i(TAG, "Dy : $dy")
                Log.i(TAG, "Top : $top")
                Log.i(TAG, "Min Offset : $mMinOffset")
                val maxTop: Int = anchorOffset + 64
                Log.i(TAG, "Max offset : $maxTop")
                Math.min(Math.max(top, mMinOffset), maxTop)
            } else {
                constrain(top, mMinOffset, if (isHideable) mParentHeight else mMaxOffset)
            }
        }

        private fun constrain(amount: Int, low: Int, high: Int): Int {
            return if (amount < low) low else if (amount > high) high else amount
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return child.left
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return if (isHideable) {
                mParentHeight - mMinOffset
            } else {
                mMaxOffset - mMinOffset
            }
        }
    }

    fun dispatchOnSlide(top: Int) {
        val bottomSheet: View? = mViewRef!!.get()
        if (bottomSheet != null) {
            val slideOffset: Float
            slideOffset = if (top > mMaxOffset) {
                (mMaxOffset - top).toFloat() / (mParentHeight - mMaxOffset)
            } else {
                (mMaxOffset - top).toFloat() / (mMaxOffset - mMinOffset)
            }
            for (i in mCallbacks.indices) {
                mCallbacks[i].onSlide(bottomSheet, slideOffset)
            }
        }
    }

    private inner class SettleRunnable internal constructor(
        private val mView: View,
        @field:State @param:State private val mTargetState: Int
    ) : Runnable {
        override fun run() {
            if (mViewDragHelper != null && mViewDragHelper!!.continueSettling(true)) {
                ViewCompat.postOnAnimation(mView, this)
            } else {
                setStateInternal(mTargetState)
            }
        }
    }

    protected class SavedState : AbsSavedState {
        @State
        val state: Int

        @JvmOverloads
        constructor(source: Parcel, loader: ClassLoader? = null) : super(source, loader) {
            state = source.readInt()
        }

        constructor(superState: Parcelable?, @State state: Int) : super(
            superState!!
        ) {
            this.state = state
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(state)
        }

        companion object {
            @JvmField
            val CREATOR = ParcelableCompat.newCreator(
                object : ParcelableCompatCreatorCallbacks<SavedState?> {
                    override fun createFromParcel(`in`: Parcel, loader: ClassLoader): SavedState? {
                        return SavedState(`in`, loader)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                })
        }
    }

    companion object {
        /**
         * The bottom sheet is dragging.
         */
        const val STATE_DRAGGING = 1

        /**
         * The bottom sheet is settling.
         */
        const val STATE_SETTLING = 2

        /**
         * The bottom sheet is expanded.
         */
        const val STATE_EXPANDED = 3

        /**
         * The bottom sheet is collapsed.
         */
        const val STATE_COLLAPSED = 4

        /**
         * The bottom sheet is hidden.
         */
        const val STATE_HIDDEN = 5

        /**
         * The bottom sheet is anchored.
         */
        const val STATE_ANCHORED = 6

        /**
         * Peek at the 16:9 ratio keyline of its parent.
         *
         *
         *
         * This can be used as a parameter for [.setPeekHeight].
         * [.getPeekHeight] will return this when the value is set.
         */
        const val PEEK_HEIGHT_AUTO = -1
        private const val HIDE_THRESHOLD = 0.5f
        private const val HIDE_FRICTION = 0.1f
        private const val EXPAND_FRICTION = 0.2f
        private const val COLLAPSE_FRICTION = 0.2f

        /**
         * A utility function to get the [CustomBottomSheetBehavior] associated with the `view`.
         *
         * @param view The [View] with [CustomBottomSheetBehavior].
         * @return The [CustomBottomSheetBehavior] associated with the `view`.
         */
        fun <V : View?> from(view: V): CustomBottomSheetBehavior<View> {
            val params = view!!.layoutParams
            require(params is CoordinatorLayout.LayoutParams) { "The view is not a child of CoordinatorLayout" }
            val behavior = params
                .behavior
            require(behavior is CustomBottomSheetBehavior<*>) { "The view is not associated with AnchorBottomSheetBehavior" }
            return behavior as CustomBottomSheetBehavior<View>
        }
    }
}