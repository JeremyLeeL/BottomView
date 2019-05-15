package com.ymlee.bottomview

import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View

/**
 * Author: liyimin
 * Time: 2019/4/24 0024 10:35
 * github：https://github.com/JeremyLeeL
 */


/**
 * 获取文字规格（width, height, left, top, right, bottom）
 */
fun View.getTextBounds(text: String, paint: Paint, rect: Rect){
    paint.getTextBounds(text, 0, text.length, rect)
}

/**
 * dp转px
 */
fun dp2px(dp: Float): Float{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
}

/**
 * sp转px
 */
fun sp2px(sp: Float): Float{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().displayMetrics)
}

