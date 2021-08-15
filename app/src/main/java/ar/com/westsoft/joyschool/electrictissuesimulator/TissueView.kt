package ar.com.westsoft.joyschool.electrictissuesimulator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.floor

class TissueView: View {
    var tissueViewModel: TissueViewModel? = null
    val xSize = 10f
    val ySize = 10f
    var callback: MainActivity? = null
    val paint = Paint()

    val w = 100
    val h = 100
    val conf = Bitmap.Config.ARGB_8888 // see other conf types
    val bmp = Bitmap.createBitmap(w, h, conf) // this creates a MUTABLE bitmap

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    private external fun printBitmap(jBitmap: Bitmap)
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> return onClickOrMove(event)
            MotionEvent.ACTION_MOVE -> return onClickOrMove(event)
        }
        return false
    }
    private fun onClickOrMove(event: MotionEvent?):Boolean {
        callback!!.onClickOnCell(
                floor(event!!.x / xSize).toInt(),
                floor(event.y / ySize).toInt())
        return true
    }
    override fun performClick(): Boolean {
        super.performClick()

        println("Screen was cl")
        System.gc()
        return true
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (x in 0 until tissueViewModel!!.xSize) {
            for (y in 0 until tissueViewModel!!.ySize) {
                paint.color = tissueViewModel!!.getCell(x, y).stateColor()
                canvas!!.drawRect(x * xSize + 1, y * ySize + 1,
                        x * xSize + xSize - 1, y * ySize + ySize - 1,
                        paint)
            }
        }
        printBitmap(bmp)
        canvas?.drawBitmap(bmp, 20f, 20f, paint)
    }
}