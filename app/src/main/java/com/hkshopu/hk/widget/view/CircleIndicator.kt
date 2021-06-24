package com.HKSHOPU.hk.widget.view

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.annotation.AnimatorRes
import androidx.annotation.DrawableRes
import androidx.viewpager.widget.ViewPager
import com.HKSHOPU.hk.R

class CircleIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    val TAG = CircleIndicator::class.java.simpleName

    val DEFAULT_INDICATOR_WIDTH = 5
    var mViewpager: ViewPager? = null
    var mIndicatorMargin = -1
    var mIndicatorWidth = -1
    var mIndicatorHeight = -1
    var mAnimatorResId: Int = R.anim.scale_with_alpha
    var mAnimatorReverseResId = 0
    var mIndicatorBackgroundResId: Int = R.drawable.banner_radius
    var mIndicatorUnselectedBackgroundResId: Int = R.drawable.banner_radius_unselect
    var mAnimatorOut: Animator? = null
    var mAnimatorIn: Animator? = null
    var mImmediateAnimatorOut: Animator? = null
    var mImmediateAnimatorIn: Animator? = null

    var mLastPosition = -1

    var loop = false
    var length = 0

    open fun init(context: Context, attrs: AttributeSet?) {
        handleTypedArray(context, attrs)
        checkIndicatorConfig(context)
    }

    open fun handleTypedArray(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicator)
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_width, -1)
        mIndicatorHeight =
            typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_height, -1)
        mIndicatorMargin =
            typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_margin, -1)
        mAnimatorResId = typedArray.getResourceId(
            R.styleable.CircleIndicator_ci_animator,
            R.anim.scale_with_alpha
        )
        mAnimatorReverseResId =
            typedArray.getResourceId(R.styleable.CircleIndicator_ci_animator_reverse, 0)
        mIndicatorBackgroundResId = typedArray.getResourceId(
            R.styleable.CircleIndicator_ci_drawable,
            R.drawable.banner_radius
        )
        mIndicatorUnselectedBackgroundResId = typedArray.getResourceId(
            R.styleable.CircleIndicator_ci_drawable_unselected,
            mIndicatorBackgroundResId
        )
        val orientation = typedArray.getInt(R.styleable.CircleIndicator_ci_orientation, -1)
        setOrientation(if (orientation == LinearLayout.VERTICAL) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL)
        val gravity = typedArray.getInt(R.styleable.CircleIndicator_ci_gravity, -1)
        setGravity(if (gravity >= 0) gravity else Gravity.CENTER)
        typedArray.recycle()
    }

    /**
     * Create and configure Indicator in Java code.
     */
    @SuppressLint("ResourceType")
    fun configureIndicator(
        @DrawableRes indicatorBackgroundId: Int,
        @DrawableRes indicatorUnselectedBackgroundId: Int
    ) {
        configureIndicator(
            -1, -1, -1,
            R.anim.scale_with_alpha, 0, indicatorBackgroundId, indicatorUnselectedBackgroundId
        )
    }

    fun configureIndicator(
        indicatorWidth: Int, indicatorHeight: Int, indicatorMargin: Int,
        @AnimatorRes animatorId: Int, @AnimatorRes animatorReverseId: Int,
        @DrawableRes indicatorBackgroundId: Int,
        @DrawableRes indicatorUnselectedBackgroundId: Int
    ) {
        mIndicatorWidth = indicatorWidth
        mIndicatorHeight = indicatorHeight
        mIndicatorMargin = indicatorMargin
        mAnimatorResId = animatorId
        mAnimatorReverseResId = animatorReverseId
        mIndicatorBackgroundResId = indicatorBackgroundId
        mIndicatorUnselectedBackgroundResId = indicatorUnselectedBackgroundId
        checkIndicatorConfig(getContext())
    }

    open fun checkIndicatorConfig(context: Context) {
        mIndicatorWidth =
            if (mIndicatorWidth < 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else mIndicatorWidth
        mIndicatorHeight =
            if (mIndicatorHeight < 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else mIndicatorHeight
        mIndicatorMargin =
            if (mIndicatorMargin < 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else mIndicatorMargin
        mAnimatorResId = if (mAnimatorResId == 0) R.anim.scale_with_alpha else mAnimatorResId
        mAnimatorOut = createAnimatorOut(context)
        mImmediateAnimatorOut = createAnimatorOut(context)
        mImmediateAnimatorOut!!.duration = 0
        mAnimatorIn = createAnimatorIn(context)
        mImmediateAnimatorIn = createAnimatorIn(context)
        mImmediateAnimatorIn!!.duration = 0
        mIndicatorBackgroundResId =
            if (mIndicatorBackgroundResId == 0) R.drawable.banner_radius else mIndicatorBackgroundResId
        mIndicatorUnselectedBackgroundResId =
            if (mIndicatorUnselectedBackgroundResId == 0) mIndicatorBackgroundResId else mIndicatorUnselectedBackgroundResId
    }

    open fun createAnimatorOut(context: Context): Animator? {
        return AnimatorInflater.loadAnimator(context, mAnimatorResId)
    }

    open fun createAnimatorIn(context: Context): Animator? {
        val animatorIn: Animator
        if (mAnimatorReverseResId == 0) {
            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorResId)
            animatorIn.interpolator = ReverseInterpolator()
        } else {
            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorReverseResId)
        }
        return animatorIn
    }

    fun setViewPager(viewPager: ViewPager?, loop: Boolean, length: Int) {
        mViewpager = viewPager
        this.loop = loop
        this.length = length
        if (mViewpager != null && mViewpager!!.getAdapter() != null) {
            mLastPosition = -1
            createIndicators()
            mViewpager!!.removeOnPageChangeListener(mInternalPageChangeListener)
            mViewpager!!.addOnPageChangeListener(mInternalPageChangeListener)
            mInternalPageChangeListener.onPageSelected(if (loop) mViewpager!!.getCurrentItem() % length else mViewpager!!.getCurrentItem())
        }
    }

    val mInternalPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                var position = position
                if (mViewpager!!.getAdapter() == null || mViewpager!!.getAdapter()!!
                        .getCount() <= 0
                ) {
                    return
                }
                if (mAnimatorIn!!.isRunning) {
                    mAnimatorIn!!.end()
                    mAnimatorIn!!.cancel()
                }
                if (mAnimatorOut!!.isRunning) {
                    mAnimatorOut!!.end()
                    mAnimatorOut!!.cancel()
                }
                var currentIndicator: View? = null
                if (mLastPosition >= 0 && getChildAt(mLastPosition).also {
                        currentIndicator = it
                    } != null) {
                    currentIndicator!!.setBackgroundResource(mIndicatorUnselectedBackgroundResId)
                    mAnimatorIn!!.setTarget(currentIndicator)
                    mAnimatorIn!!.start()
                }
                position = if (loop) position % length else position
                val selectedIndicator: View = getChildAt(position)
                if (selectedIndicator != null) {
                    selectedIndicator.setBackgroundResource(mIndicatorBackgroundResId)
                    mAnimatorOut!!.setTarget(selectedIndicator)
                    mAnimatorOut!!.start()
                }
                mLastPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        }

    fun getDataSetObserver(): DataSetObserver? {
        return mInternalDataSetObserver
    }

    val mInternalDataSetObserver: DataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            if (mViewpager == null) {
                return
            }
            val newCount = if (loop) length else mViewpager!!.getAdapter()!!.getCount()
            val currentCount: Int = getChildCount()
            mLastPosition = if (newCount == currentCount) {  // No change
                return
            } else if (mLastPosition < newCount) {
                if (loop) mViewpager!!.getCurrentItem() % length else mViewpager!!.getCurrentItem()
            } else {
                -1
            }
            createIndicators()
        }
    }


    @Deprecated("User ViewPager addOnPageChangeListener")
    fun setOnPageChangeListener(onPageChangeListener: ViewPager.OnPageChangeListener?) {
        if (mViewpager == null) {
            throw NullPointerException("can not find Viewpager , setViewPager first")
        }
        onPageChangeListener?.let { mViewpager!!.removeOnPageChangeListener(it) }
        onPageChangeListener?.let { mViewpager!!.addOnPageChangeListener(it) }
    }

    open fun createIndicators() {
        removeAllViews()
        val count = if (loop) length else mViewpager!!.getAdapter()!!.getCount()
        if (count <= 0) {
            return
        }
        val currentItem: Int =
            if (loop) mViewpager!!.getCurrentItem() % length else mViewpager!!.getCurrentItem()
        val orientation: Int = getOrientation()
        for (i in 0 until count) {
            if (currentItem == i) {
                mImmediateAnimatorOut?.let {
                    addIndicator(
                        orientation, mIndicatorBackgroundResId,
                        it
                    )
                }
            } else {
                mImmediateAnimatorIn?.let {
                    addIndicator(
                        orientation, mIndicatorUnselectedBackgroundResId,
                        it
                    )
                }
            }
        }
    }

    open fun addIndicator(
        orientation: Int, @DrawableRes backgroundDrawableId: Int,
        animator: Animator
    ) {
        if (animator.isRunning) {
            animator.end()
            animator.cancel()
        }
        val Indicator = View(getContext())
        Indicator.setBackgroundResource(backgroundDrawableId)
        addView(Indicator, mIndicatorWidth, mIndicatorHeight)
        val lp = Indicator.layoutParams as LinearLayout.LayoutParams
        if (orientation == LinearLayout.HORIZONTAL) {
            lp.leftMargin = mIndicatorMargin
            lp.rightMargin = mIndicatorMargin
        } else {
            lp.topMargin = mIndicatorMargin
            lp.bottomMargin = mIndicatorMargin
        }
        Indicator.layoutParams = lp
        animator.setTarget(Indicator)
        animator.start()
    }

    class ReverseInterpolator : Interpolator {
        override fun getInterpolation(value: Float): Float {
            return Math.abs(1.0f - value)
        }
    }

    fun dip2px(dpValue: Float): Int {
        val scale: Float = getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }
}