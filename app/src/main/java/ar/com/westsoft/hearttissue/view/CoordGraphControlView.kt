package ar.com.westsoft.hearttissue.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import android.widget.GridLayout
import androidx.annotation.RequiresApi

class CoordGraphControlView : GridLayout {
    lateinit var xPosView: NumberPicker
    lateinit var yPosView: NumberPicker

    var x = 10
    var y = 10

    var onChanged: ((xPos: Int, yPos:Int) -> Unit)? = null

    constructor(context: Context?) : super(context) {
        if (context != null) setUp(context)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        if (context != null) setUp(context)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr) {
        if (context != null) setUp(context)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        if (context != null) setUp(context)
    }

    private fun setUp(context: Context?)  {
        rowCount = 2
        columnCount = 2

        xPosView = NumberPicker(context)
        xPosView.value = x
        xPosView.onChanged = { value: Int ->
            this.x = value
            onChanged?.invoke(this.x, this.y)
        }

        yPosView = NumberPicker(context)
        yPosView.value = y
        yPosView.onChanged = { value: Int ->
            this.y = value
            onChanged?.invoke(this.x, this.y)
        }

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(xPosView, 0)
        addView(yPosView, 1)
    }
}