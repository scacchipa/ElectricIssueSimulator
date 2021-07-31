package ar.com.westsoft.joyschool.electrictissuesimulator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
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
                coordGraphModel!!.points.forEach { point ->
                    val x = (point.first - bound.left) / bound.width() * width
                    val y = height - (point.second - bound.top) / bound.height() * height
                    canvas!!.drawPoint(x, y, paint)
                }
            }
        }
    }
}
class CoordGraphModel(val points: List<Pair<Float, Float>> = ArrayList()) {
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
            tempBound.top = min(tempBound.top, point.second)
            tempBound.right = max(tempBound.right, point.first)
            tempBound.bottom = max(tempBound.bottom, point.second)
        }
        return tempBound
    }
    fun purge(): CoordGraphModel =
        CoordGraphModel(points.filterIndexed { index, _ -> index > points.size - 500 })
}