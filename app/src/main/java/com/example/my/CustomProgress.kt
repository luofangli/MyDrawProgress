package com.example.my

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View

class CustomProgress : View {
    //运行起来的背景画笔
    private val paintFirst:Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLUE
        }
    }
    //移动的进度条的画笔
    private val paintSecond:Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = Color.RED
        }
    }
    //勾勾的画笔
    private val paintPath:Paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = Color.WHITE
            strokeWidth = 6f
        }
    }
    //勾勾的路径
    private val path:Path by lazy {
        Path().apply {
            moveTo(startpathX,startpathY)
            lineTo(centerpathX,centerpathY)
        }
    }
    //定义动画因子
    //路径移动的起点和尾点
    private var startpathX = 0f
    private var startpathY = 0f
    private var centerpathX = 0f
    private var centerpathY = 0f
    //两边向中间移动成一个圆的距离
    private var centerDistance = 0f
    //圆矩形圆的半径
    private var radius = 0f
    //进度条的总长度
    private var progressLength = 0f
    //进度条下载过程中的动画因子
   private var allDistance = 0f
    //外部下载的进度
    var loadProgress = 0f
    set(value) {
        field = value
        progressGo(field)
    }

    constructor(context: Context):super(context){}
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet){}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        progressLength = measuredWidth.toFloat()
//        startpathX = measuredWidth/2f-measuredHeight*3/10f
//        startpathY = measuredHeight/2f
//        centerpathX = measuredWidth/2f-measuredHeight*3/10f
//       centerpathY = measuredHeight/2f
        startpathX = measuredWidth/2f-radius/2
        startpathY = measuredHeight/2f
        centerpathX =measuredWidth/2f-radius/2
        centerpathY = measuredHeight/2f

    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //运行起来就有的背景
        canvas?.drawRoundRect(allDistance,0f,measuredWidth.toFloat(),measuredHeight.toFloat(),
        0f,0f,paintFirst)
        //移动的进度条
        canvas?.drawRoundRect(0f+centerDistance,0f,allDistance-centerDistance,measuredHeight.toFloat(),
            radius,radius,paintSecond)
        //画勾勾
        canvas?.drawPath(path,paintPath)
    }
    //进度条前进的动画
    private fun progressGo(load:Float){
        allDistance = progressLength*load
        //下载完之后执行
        if (allDistance == progressLength){
            afterProgress()
        }
        invalidate()
    }
    //进度条完了之后的动画
    private fun afterProgress(){
        //如果进度条完了则开启下面的动画
        //由长方形变成一个圆矩形的动画
        val radiusValueAnimator = ValueAnimator.ofFloat(0f,measuredHeight/2f).apply {
            duration = 2000
            addUpdateListener {
                Log.v("lfl","长方形变成圆矩形")
                val value = it.animatedValue as Float
                radius = value
                invalidate()
            }
        }
        //由两边向中间移动成一个圆的距离
        val centerValueAnimator = ValueAnimator.ofFloat(0f,(measuredWidth-measuredHeight)/2f).apply {
            duration = 2000
            addUpdateListener {
                Log.v("lfl","从两边缩成一个圆")
                val value = it.animatedValue as Float
                centerDistance = value
                invalidate()
            }
        }
        //画钩钩
        // measuredWidth/2f-measuredHeight*3/20f,
        val pathXValueAnimator = ValueAnimator.ofFloat(measuredWidth/2f-radius/2f,
           measuredWidth/2f+radius/2f).apply {
            duration = 2000
            addUpdateListener {
                val value = it.animatedValue as Float
                centerpathX = value
                Log.v("lfl","勾勾尾巴的位置X:$centerpathX")
                invalidate()
            }
        }

        val pathYValueAnimator = ValueAnimator.ofFloat(measuredHeight/2f,
           measuredHeight/2f+radius/2f,
            measuredHeight/2f-radius/2f).apply {
            duration = 2000
            addUpdateListener {
                val value = it.animatedValue as Float
                centerpathY = value
                invalidate()
            }
        }
        val path = AnimatorSet().apply {
            playTogether(pathXValueAnimator,pathYValueAnimator)
            Log.v("lfl","圆的区域为：左：${measuredWidth/2-radius},上：${measuredHeight/2-radius}," +
                    "右：${measuredWidth/2+radius},下：${measuredHeight/2+radius}")

        }
        val ValueAnimator = AnimatorSet().apply {
           playSequentially(radiusValueAnimator,centerValueAnimator)
        }
        val all = AnimatorSet().apply {
            playSequentially(ValueAnimator,path)
        }.start()
    }
}