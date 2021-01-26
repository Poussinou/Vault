package com.arsvechkarev.vault.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import com.arsvechkarev.vault.viewbuilding.Colors
import com.arsvechkarev.vault.viewdsl.AccelerateDecelerateInterpolator
import com.arsvechkarev.vault.viewdsl.DURATION_SHORT
import com.arsvechkarev.vault.viewdsl.cancelIfRunning
import com.arsvechkarev.vault.viewdsl.contains
import com.arsvechkarev.vault.viewdsl.gone
import com.arsvechkarev.vault.viewdsl.invisible
import com.arsvechkarev.vault.viewdsl.layoutGravity
import com.arsvechkarev.vault.viewdsl.visible
import kotlin.math.abs
import kotlin.math.hypot

open class SimpleDialog(context: Context) : FrameLayout(context) {
  
  private var latestX = 0f
  private var latestY = 0f
  private var currentShadowFraction = 0f
  private val dialogView get() = getChildAt(0)
  private val shadowAnimator = ValueAnimator().apply {
    interpolator = AccelerateDecelerateInterpolator
    duration = DURATION_SHORT
    addUpdateListener {
      currentShadowFraction = it.animatedValue as Float
      onShadowFractionChangedListener?.invoke(currentShadowFraction)
      val color = ColorUtils.blendARGB(Color.TRANSPARENT, Colors.Shadow, currentShadowFraction)
      setBackgroundColor(color)
    }
  }
  
  var onShadowFractionChangedListener: ((Float) -> Unit)? = null
  var onHide = {}
  
  var isOpened = false
    private set
  
  init {
    invisible()
  }
  
  override fun onViewAdded(child: View) {
    if (child === dialogView) {
      dialogView.layoutGravity(Gravity.CENTER)
      dialogView.scaleX = SCALE_FACTOR
      dialogView.scaleY = SCALE_FACTOR
    }
  }
  
  fun show() {
    if (isOpened) return
    isOpened = true
    visible()
    dialogView.alpha = 0f
    dialogView.visible()
    shadowAnimator.cancelIfRunning()
    shadowAnimator.setFloatValues(currentShadowFraction, 1f)
    shadowAnimator.start()
    dialogView.animate()
        .withLayer()
        .scaleX(1f)
        .scaleY(1f)
        .alpha(1f)
        .setDuration(DURATION_SHORT)
        .setInterpolator(AccelerateDecelerateInterpolator)
        .start()
  }
  
  fun hide() {
    if (!isOpened) return
    isOpened = false
    shadowAnimator.cancelIfRunning()
    shadowAnimator.setFloatValues(currentShadowFraction, 0f)
    shadowAnimator.start()
    dialogView.animate()
        .withLayer()
        .alpha(0f)
        .scaleX(SCALE_FACTOR)
        .scaleY(SCALE_FACTOR)
        .setDuration(DURATION_SHORT)
        .setInterpolator(AccelerateDecelerateInterpolator)
        .withEndAction { gone(); onHide() }
        .start()
  }
  
  override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.action) {
      ACTION_DOWN -> {
        if (event !in dialogView) {
          latestX = event.x
          latestY = event.y
          return true
        }
      }
      ACTION_UP -> {
        val dx = abs(event.x - latestX)
        val dy = abs(event.y - latestY)
        val scaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        if (hypot(dx, dy) < scaledTouchSlop) {
          hide()
        }
      }
    }
    return super.onTouchEvent(event)
  }
  
  override fun onDetachedFromWindow() {
    onHide = {}
    super.onDetachedFromWindow()
  }
  
  companion object {
    
    private const val SCALE_FACTOR = 0.8f
  }
}