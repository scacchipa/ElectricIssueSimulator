package ar.com.westsoft.hearttissue.model

import android.graphics.RectF
import kotlin.math.max
import kotlin.math.min

data class CoordGraphModel(val points: List<Pair<Float, Float>> = ArrayList()) {
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
        CoordGraphModel(points.filterIndexed { index, _ -> index > points.size - 200 })
}