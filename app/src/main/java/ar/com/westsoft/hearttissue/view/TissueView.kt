package ar.com.westsoft.hearttissue.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.util.Size
import android.view.MotionEvent
import android.view.View
import ar.com.westsoft.hearttissue.viewmodel.TissueViewModel

class TissueView: View {
    var viewModel: TissueViewModel? = null


    val paint = Paint()

    var bmp: Bitmap? = null

    var onTouchTissue: ((col: Int, row: Int) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    private external fun printBitmap(jBitmap: Bitmap, colors: IntArray)
    external fun getPosCell(x: Int, y: Int): Point
    external fun setCellSize(xSize: Int, ySize: Int)
    external fun getCellSize(): Size

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                val point = getPosCell(event.x.toInt(), event.y.toInt())
                onTouchTissue?.invoke(point.x, point.y)
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
            val cellSize = getCellSize()
            if (bmp == null) bmp = Bitmap.createBitmap(
                cellSize.width * vm.tissue.colCount,
                cellSize.height * vm.tissue.rowCount,
                Bitmap.Config.ARGB_8888
            )
            val colors = vm.getColors()
            printBitmap(bmp!!, colors)
            canvas?.drawBitmap(bmp!!, 0f, 0f, paint)
        }
    }
}