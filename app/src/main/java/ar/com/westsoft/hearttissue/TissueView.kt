package ar.com.westsoft.hearttissue

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.floor

class TissueView: View {
    var tissueViewModel: TissueViewModel? = null
        set(value) {
            field = value
            bmp = if (value != null) {
                val conf = Bitmap.Config.ARGB_8888
                Bitmap.createBitmap(xCellSize * value.colCount,
                    yCellSize * value.rowCount, conf)
            } else null
        }
    private val xCellSize = 10
    private val yCellSize = 10
    var callback: MainActivity? = null
    val paint = Paint()

    val w = 100
    val h = 100
    val conf = Bitmap.Config.ARGB_8888 // see other conf types
    private var bmp: Bitmap? = null;

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private external fun printBitmap(jBitmap: Bitmap)
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                callback!!.onClickOnCell(event.x.toInt(), event.y.toInt())
                return true
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()

        println("Screen was cl")
        System.gc()
        return true
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (bmp != null) {
            printBitmap(bmp!!)
            canvas?.drawBitmap(bmp!!, 0f, 0f, paint)
        }
    }
}