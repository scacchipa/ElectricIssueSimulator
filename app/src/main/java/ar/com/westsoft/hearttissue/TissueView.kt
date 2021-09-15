package ar.com.westsoft.hearttissue

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ar.com.westsoft.hearttissue.viewmodel.TissueViewModel

class TissueView: View {
    var viewModel: TissueViewModel? = null

    private val xCellSize = 10
    private val yCellSize = 10
    var callback: MainActivity? = null
    val paint = Paint()

    var bmp: Bitmap? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private external fun printBitmap(jBitmap: Bitmap, colors: IntArray)
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                // TODO  Correct next line
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
        viewModel?.let { vm ->
            if (bmp != null) bmp = Bitmap.createBitmap(
                xCellSize * vm.tissue.colCount,
                yCellSize * vm.tissue.colCount,
                Bitmap.Config.ARGB_8888
            )
            printBitmap(bmp!!, vm.getColors())
            canvas?.drawBitmap(bmp!!, 0f, 0f, paint)
        }
    }
}