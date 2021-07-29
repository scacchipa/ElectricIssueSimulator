package ar.com.westsoft.joyschool.electrictissuesimulator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val tissue = TissueModel(50, 50)
    private var tissueView: TissueView? = null
    lateinit var buttonPanel: PanelView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tissueView = findViewById(R.id.tissueView)
        tissueView!!.tissueModel = tissue
        tissueView!!.callback = this

        buttonPanel = findViewById(R.id.buttonPanel)

        //kotlin.concurrent.timer(null, false, 0.toLong(), 300) {
        Thread {
            while(true) {
                val startTime = System.currentTimeMillis()
                repeat (100) {
                    val tempTissue = tissueView!!.tissueModel!!.clone()
                    tempTissue.calcAll()
                    tissueView!!.tissueModel = tempTissue
                }
                println(System.currentTimeMillis() - startTime)
                runOnUiThread { tissueView!!.invalidate() }
            }
        }.start()
    }
    fun onClickOnCell(x: Int, y: Int) {
        println("$x, $y")
        val newCell: Cell = when {
                    buttonPanel.autoButton.isChecked -> AutoCell(tissue, x, y)
                    buttonPanel.myoButton.isChecked -> MyoCell(tissue, x, y)
                    buttonPanel.fastButton.isChecked -> FastCell(tissue, x, y)
                    else -> DeadCell(tissue, x, y)
                }
        tissue.setCell(x, y, newCell)
        tissueView!!.invalidate()
    }
}

