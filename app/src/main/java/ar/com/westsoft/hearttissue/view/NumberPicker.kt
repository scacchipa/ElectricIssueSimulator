package ar.com.westsoft.hearttissue.view

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi

class NumberPicker: LinearLayout {

    lateinit var lessButton: Button
    lateinit var plusButton: Button
    lateinit var numberView: TextView
    var value = 10
    set(value) {
        field = when {
            value < minValue -> minValue
            value > maxValue -> maxValue
            else -> value
        }
        numberView.text = "$value"
        postInvalidate()
    }
    var minValue = 0
    set(value) {
        if (value < this.maxValue) {
            field = value
            if (value > this.value) {
                this.value = value
                numberView.text = "$value"
            }
            postInvalidate()
        }
    }
    var maxValue = 99
    set(value) {
        if (value > this.minValue) {
            field = value
            if (value < this.value) {
                this.value = value
                numberView.text = "$value"
            }
            postInvalidate()
        }
    }
    var onChanged: ((value:Int) ->Unit)? = null
    constructor(context: Context?) : super(context) {
        if (context != null) setUp(context)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        if (context != null) setUp(context)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        if (context != null) setUp(context)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        if (context != null) setUp(context)
    }
    private fun setUp(context: Context) {
        lessButton = Button(context)
        lessButton.text = "-"
        plusButton = Button(context)
        plusButton.text = "+"
        numberView = TextView(context)
        numberView.setTextColor(Color.BLACK)

        numberView.text = "$value"

        addView(lessButton)
        addView(numberView)
        addView(plusButton)

        lessButton.setOnClickListener {
            if (value > minValue) {
                value--
                numberView.text = "$value"
                onChanged?.invoke(value)
                this.postInvalidate()
            }
        }
        plusButton.setOnClickListener {
            if (value < maxValue) {
                value++
                numberView.text = "$value"
                onChanged?.invoke(value)
                this.postInvalidate()
            }
        }
    }
}