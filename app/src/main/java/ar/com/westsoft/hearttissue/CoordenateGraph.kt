package ar.com.westsoft.hearttissue

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.ViewModel
import kotlin.math.max
import kotlin.math.min

class CoordenateGraph: View {

    var coordGraphModel: CoordGraphModel? = null
    var paint = Paint()

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
        paint.strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = Color.BLACK
        if (coordGraphModel != null) {
            val bound = coordGraphModel!!.bound
            if (bound.width() != 0f && bound.height() != 0f) {
                val xScale = width.toFloat() / bound.width()
                val yScale = height.toFloat() / bound.height()
                canvas!!.translate(
                    - bound.left * xScale,
                    - bound.bottom * yScale + height.toFloat())
                canvas.scale(xScale, yScale)

                coordGraphModel!!.points.forEach { point ->
                    canvas.drawPoint(point.first, point.second, paint)
                }
            }
        }
    }
}
class CoordGraphModel(val points: List<Pair<Float, Float>> = ArrayList()): ViewModel() {
    val bound: RectF

    init {
        bound = updateBound()
    }

    fun add(point: Pair<Float, Float>): CoordGraphModel =
        CoordGraphModel(points + point).purge()

    fun updateBound(): RectF {
        if (points.isEmpty()) return RectF()
        val tempBound = RectF(points[0].first, points[0].second, points[0].first, points[0].second)

        points.forEach { point ->
            tempBound.left = min(tempBound.left, point.first)
            tempBound.right = max(tempBound.right, point.first)
        }
        tempBound.top = 60f
        tempBound.bottom = -100f
        return tempBound
    }
    fun purge(): CoordGraphModel =
        CoordGraphModel(points.filterIndexed { index, _ -> index > points.size - 500 })
}