package ar.com.westsoft.hearttissue.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import ar.com.westsoft.hearttissue.viewmodel.TissueViewModel

class CoordenateGraph: View {
    private var paint = Paint()
    var viewModel: TissueViewModel? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        paint.color = Color.BLACK
        paint.alpha = 0xFF
        paint.strokeWidth = 2f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        viewModel?.coordGraphModel?.value?.let { coordModel ->
            paint.color = Color.BLACK
            if (coordModel.bound.width() != 0f && coordModel.bound.height() != 0f) {
                val xScale = width.toFloat() / coordModel.bound.width() / 2
                val yScale = height.toFloat() / coordModel.bound.height()
                canvas!!.translate(
                    -coordModel.bound.left * xScale,
                    -coordModel.bound.bottom * yScale + height.toFloat()
                )
                canvas.scale(xScale, yScale)

                coordModel.points.forEach { point ->
                    canvas.drawPoint(point.first, point.second, paint)
                }
            }
        }
    }
}
