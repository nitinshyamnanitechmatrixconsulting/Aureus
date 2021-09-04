package com.auresus.academy.view.teacherhome

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout

class SoftInputAssist(activity: Activity) {
    private var rootView: View? = null
    private var contentContainer: ViewGroup? = null
    private var viewTreeObserver: ViewTreeObserver? = null
    private val listener: () -> Unit
    private val contentAreaOfWindowBounds = Rect()
    private val rootViewLayout: FrameLayout.LayoutParams
    private var usableHeightPrevious = 0

    init {
        contentContainer = activity.findViewById(android.R.id.content) as ViewGroup
        rootView = contentContainer!!.getChildAt(0)
        rootViewLayout = rootView!!.layoutParams as FrameLayout.LayoutParams
        listener = { possiblyResizeChildOfContent() }
    }

    fun onResume() {
        if (viewTreeObserver == null || viewTreeObserver?.isAlive == false) {
            viewTreeObserver = rootView?.viewTreeObserver
        }

        viewTreeObserver?.addOnGlobalLayoutListener(listener)
    }

    fun onPause() {
        if (viewTreeObserver?.isAlive == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewTreeObserver?.removeOnGlobalLayoutListener(listener)
            } else {
                //noinspection deprecation
                viewTreeObserver?.removeGlobalOnLayoutListener(listener)
            }
        }
    }

    fun onDestroy() {
        rootView = null
        contentContainer = null
        viewTreeObserver = null
    }

    private fun possiblyResizeChildOfContent() {
        contentContainer?.getWindowVisibleDisplayFrame(contentAreaOfWindowBounds)
        val usableHeightNow = contentAreaOfWindowBounds.height()

        if (usableHeightNow != usableHeightPrevious) {
            rootViewLayout.height = usableHeightNow
            // Change the bounds of the root view to prevent gap between keyboard and content, and top of content positioned above top screen edge.
            rootView?.layout(contentAreaOfWindowBounds.left, contentAreaOfWindowBounds.top,
                contentAreaOfWindowBounds.right, contentAreaOfWindowBounds.bottom)
            rootView?.requestLayout()

            usableHeightPrevious = usableHeightNow
        }
    }
}