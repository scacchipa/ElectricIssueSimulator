package ar.com.westsoft.hearttissue.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import ar.com.westsoft.joyschool.electrictissuesimulator.R

class PanelView: LinearLayout {

    lateinit var playPauseButton: ImageButton
    lateinit var resetButton: ImageButton
    lateinit var autoButton: RadioButton
    lateinit var myoButton: RadioButton
    lateinit var fastButton: RadioButton

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?)
            : super(context, attrs) {
        setUpView(context)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        setUpView(context)
    }
    private fun setUpView(context: Context?) {
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.button_panel, this)

        playPauseButton = findViewById(R.id.playButton)
        resetButton = findViewById(R.id.rewButton)
        autoButton = findViewById(R.id.autoButton)
        myoButton = findViewById(R.id.myoButton)
        fastButton = findViewById(R.id.fastButton)

        autoButton.isChecked = true
    }
}