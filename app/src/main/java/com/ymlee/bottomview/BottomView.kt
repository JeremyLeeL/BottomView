package com.ymlee.bottomview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import java.util.logging.Logger

/**
 * Author: liyimin
 * Time: 2019/4/25 0025 15:52
 * github：https://github.com/JeremyLeeL
 */
class BottomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0): View(context, attrs, defStyleAttr) {

    companion object{
        const val LEFT = 0
        const val MID = 1
        const val RIGHT = 2
        const val NONE = 3
        const val LEFT2MID = 10
        const val MID2RIGHT = 20
        const val RIGHT2MID = 30
        const val MID2LEFT = 40
        const val LEFT2RIGHT = 50
        const val RIGHT2LEFT = 60
    }

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var leftTitle = "首页"
    private var rightTitle = "设置"

    /**用于测量文字规格*/
    private val rect = Rect()
    /**文字下划线宽度（有多粗）*/
    private val bottomLineWidth = dp2px(5f)
    /**文字底部下划线长度*/
    private val bottomLineLength = dp2px(20f)
    private val bottomPadding = dp2px(15f)
    /**空心圆的半径*/
    private var hollowCircleRadius = dp2px(15f)
    private var hollowCenterX = 0f
    private var hollowCenterY = 0f

    /**偏移(0~1)*/
    private var textOffset = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var leftLineOffset = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var rightLineOffset = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var leftTextStartX = 0f
    private var leftTextStartY = 0f

    /**文字的偏移距离*/
    private var textTranslateF = 0f
    /**圆的偏移距离*/
    private var circleTranslateF = dp2px(80f)

    private var lineStartX = 0f
    private var lineStartY = 0f

    /**当前选中index*/
    var currentPosition = 0
    /**左点击区域*/
    private val leftRect = Rect()
    /**中间点击区域*/
    private val midRect = Rect()
    /**右点击区域*/
    private val rightRect = Rect()

    private lateinit var viewPager: ViewPager
    /**是否展示中间圆的动画*/
    private var showMidAnim = true
    /**是否是点击*/
    private var isClick = false
    init {
        paint.color = Color.WHITE
        paint.textSize = sp2px(18f)
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = bottomLineWidth
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        lineStartX = width / 4f - bottomLineLength / 2f
        lineStartY = height - bottomPadding

        getTextBounds(leftTitle, paint, rect)
        val leftTextWidth = rect.width()
        leftTextStartX = lineStartX + bottomLineLength / 2f - leftTextWidth / 2f
        leftTextStartY = lineStartY - bottomLineWidth - bottomPadding / 2f

        textTranslateF = leftTextWidth * 1.5f

        hollowCenterX = width / 2f
        hollowCenterY = height - bottomPadding - bottomLineWidth - hollowCircleRadius

        leftRect.set(//left:42，top:301，right:214，bottom:160
            (leftTextStartX - textTranslateF).toInt(),
            (leftTextStartY - rect.height() - dp2px(5f)).toInt(),
            (leftTextStartX + rect.width()).toInt(),
            lineStartY.toInt())

        rightRect.set(
            (leftTextStartX + width / 2).toInt(),
            (leftTextStartY - rect.height() - dp2px(5f)).toInt(),
            (leftTextStartX + width / 2 + rect.width() + textTranslateF).toInt(),
            lineStartY.toInt())

        midRect.set(
            (hollowCenterX - hollowCircleRadius - dp2px(5f)).toInt(),
            (hollowCenterY - hollowCircleRadius - dp2px(5f)).toInt(),
            (hollowCenterX + hollowCircleRadius + dp2px(5f)).toInt(),
            (hollowCenterY + hollowCircleRadius + dp2px(5f)).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //左边的文字
        canvas.drawText(leftTitle, leftTextStartX - textTranslateF * textOffset, leftTextStartY, paint)

        //左边的下划线
        paint.alpha = (0xff * leftLineOffset).toInt()
        canvas.drawLine(
            lineStartX, lineStartY,
            lineStartX + bottomLineLength + (1 - leftLineOffset) * textTranslateF, lineStartY, paint
        )
        //右边的下划线
        val rightStartX = lineStartX + width / 2
        paint.alpha = (0xff * rightLineOffset).toInt()
        canvas.drawLine(
            rightStartX - (1 - rightLineOffset) * textTranslateF, lineStartY,
            rightStartX + bottomLineLength, lineStartY, paint
        )

        if (showMidAnim) {
            //空心圆环
            paint.alpha = 0xff
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(
                hollowCenterX,
                hollowCenterY - circleTranslateF * textOffset,
                hollowCircleRadius + dp2px(15f) * textOffset,
                paint
            )
            paint.style = Paint.Style.FILL

            //实心圆
            paint.alpha = (0xff * textOffset).toInt()
            canvas.drawCircle(
                width / 2f,
                height - bottomPadding - bottomLineWidth - hollowCircleRadius - circleTranslateF * textOffset,
                hollowCircleRadius + dp2px(15f) * textOffset - bottomLineWidth,
                paint
            )
        }else{
            //空心圆环
            paint.alpha = 0xff
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(
                hollowCenterX,
                hollowCenterY,
                hollowCircleRadius,
                paint
            )
            paint.style = Paint.Style.FILL
        }

        paint.alpha = 0xff
        //右边的文字
        canvas.drawText(rightTitle, leftTextStartX + width / 2 + textTranslateF * textOffset, leftTextStartY, paint)
    }

    /**
     * @param isLine position传入 position + 1 isLine = false
     */
    private fun setOffset(position: Int, positionOffset: Float, isLine: Boolean = true){
        when(position){
            0 ->{
                if (isLine) {
                    leftLineOffset = 1 - positionOffset
                }
                textOffset = positionOffset
            }
            1 ->{
                if (isLine)
                    rightLineOffset = positionOffset
            }
            2 ->{
                textOffset = positionOffset
            }
        }
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setUpWithViewPager(viewPager: ViewPager){
        this.viewPager = viewPager
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (!isClick) {
                    setOffset(position, positionOffset)
                    setOffset(position + 1, 1 - positionOffset, false)
                }
            }

            override fun onPageSelected(position: Int) {
                currentPosition = position
            }

        })
        viewPager.setOnTouchListener { v, event ->
            showMidAnim = true
            isClick = false
            false
        }
    }

    /**点击区域*/
    private var area = NONE
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                when(getTouchArea(x.toInt(), y.toInt())){
                    LEFT ->{
                        area = LEFT
                    }
                    MID ->{
                        area = MID
                    }
                    RIGHT ->{
                        area = RIGHT
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{
            }
            MotionEvent.ACTION_UP ->{
                when(getTouchArea(x.toInt(), y.toInt())){
                    LEFT ->{
                        if (area == LEFT){
                            setCurrentPage(LEFT)
                        }

                        currentPosition = LEFT
                    }
                    MID ->{
                        if (area == MID){
                            setCurrentPage(MID)
                        }

                        currentPosition = MID
                    }
                    RIGHT ->{
                        if (area == RIGHT){
                            setCurrentPage(RIGHT)
                        }

                        currentPosition = RIGHT
                    }
                }
            }
        }
        return true
    }
    /**动画时长 ms*/
    private val animDuration = 300L
    /**文字往两边走、圆往上走*/
    private val textAnimator by lazy {
        ObjectAnimator.ofFloat(this, "textOffset", 1f)
    }
    /**文字往内走、圆往下走*/
    private val textAnimatorReverse by lazy {
        ObjectAnimator.ofFloat(this, "textOffset", 1f, 0f)
    }
    /**显示左边下划线*/
    private val leftLineAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(this, "leftLineOffset", 1f)
    }
    /**隐藏左边下划线*/
    private val leftLineAnimatorReverse by lazy {
        ObjectAnimator.ofFloat(this, "leftLineOffset", 1f, 0f)
    }
    /**显示右边下划线*/
    private val rightLineAnimator by lazy {
        ObjectAnimator.ofFloat(this, "rightLineOffset", 1f)
    }
    /**显示右边下划线*/
    private val rightLineAnimatorReverse by lazy {
        ObjectAnimator.ofFloat(this, "rightLineOffset", 1f, 0f)
    }
    /**显示左边下划线，隐藏右边下划线*/
    private val leftLineAnimatorSet by lazy {
        val anim = AnimatorSet()
        anim.playTogether(leftLineAnimator, rightLineAnimatorReverse)
        anim.duration = animDuration
        anim
    }
    /**显示右边下划线，隐藏左边下划线*/
    private val rightLineAnimatorSet by lazy {
        val anim = AnimatorSet()
        anim.playTogether(rightLineAnimator, leftLineAnimatorReverse)
        anim.duration = animDuration
        anim
    }
    /**文字往内走、圆往下走、显示左边下划线*/
    private val leftAnimatorSet by lazy {
        val anim = AnimatorSet()
        anim.playTogether(textAnimatorReverse, leftLineAnimator)
        anim.duration = animDuration
        anim
    }
    /**文字往内走、圆往下走、显示右边下划线*/
    private val rightAnimatorSet by lazy {
        val anim = AnimatorSet()
        anim.playTogether(textAnimatorReverse, rightLineAnimator)
        anim.duration = animDuration
        anim
    }
    /**文字往两边走、圆往上走、隐藏左边下划线*/
    private val midLeftAnimatorSet by lazy {
        val anim = AnimatorSet()
        anim.playTogether(textAnimator, leftLineAnimatorReverse)
        anim.duration = animDuration
        anim
    }
    /**文字往两边走、圆往上走、隐藏右边下划线*/
    private val midRightAnimatorSet by lazy {
        val anim = AnimatorSet()
        anim.playTogether(textAnimator, rightLineAnimatorReverse)
        anim.duration = animDuration
        anim
    }
    private fun setCurrentPage(page: Int){
        isClick = true
        showMidAnim = true
        val direction = getDirection(page)
        when(direction){
            LEFT2MID ->{
                midLeftAnimatorSet.start()
            }
            LEFT2RIGHT ->{
                showMidAnim = false
                rightLineAnimatorSet.start()
            }
            MID2RIGHT ->{
                rightAnimatorSet.start()
            }
            MID2LEFT ->{
                leftAnimatorSet.start()
            }
            RIGHT2MID ->{
                midRightAnimatorSet.start()
            }
            RIGHT2LEFT ->{
                showMidAnim = false
                leftLineAnimatorSet.start()
            }
        }
        if (direction != NONE)
            viewPager.currentItem = page
    }

    /**
     * 获取点击区域
     */
    private fun getTouchArea(x: Int, y: Int): Int{
        return if (x > leftRect.left && x < leftRect.right && y < leftRect.bottom && y > leftRect.top)
            LEFT
        else if (x > midRect.left && x < midRect.right && y < midRect.bottom && y > midRect.top)
            MID
        else if (x > rightRect.left && x < rightRect.right && y < rightRect.bottom && y > rightRect.top)
            RIGHT
        else
            NONE
    }

    /**
     * 获得ViewPager的滚动方向
     */
    private fun getDirection(position: Int): Int{
        if (position == currentPosition)
            return NONE
        when(currentPosition) {
            LEFT -> {
                if (position == MID)
                    return LEFT2MID
                if (position == RIGHT)
                    return LEFT2RIGHT
            }
            MID -> {
                return if (position == RIGHT)
                    MID2RIGHT
                else
                    MID2LEFT
            }
            RIGHT -> {
                return if (position == MID)
                    RIGHT2MID
                else
                    RIGHT2LEFT
            }
        }
        return NONE
    }
}
