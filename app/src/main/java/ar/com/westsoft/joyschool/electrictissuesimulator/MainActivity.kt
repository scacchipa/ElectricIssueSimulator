package ar.com.westsoft.joyschool.electrictissuesimulator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    val tissue = Tissue(50, 50)
    var tissueView: TissueView? = null
    lateinit var buttonPanel: PanelView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tissueView = findViewById(R.id.tissueView)
        tissueView!!.tissue = tissue
        tissueView!!.callback = this

        buttonPanel = findViewById(R.id.buttonPanel)

        kotlin.concurrent.timer(null, false, 0.toLong(), 10) {
            println(System.currentTimeMillis())
            repeat(10) {
                tissue.forAll { it.membranePotential() }
                tissue.forAll { it.calculateCharge() }
                tissue.forAll { it.updateState() }
            }
            runOnUiThread { tissueView!!.invalidate() }
        }
    }
    fun onClickOnCell(x: Int, y: Int) {
        println("$x, $y")
        val newCell: Cell =when {
                    buttonPanel.autoButton.isChecked -> AutoCell(tissue, x, y)
                    buttonPanel.myoButton.isChecked -> MyoCell(tissue, x, y)
                    buttonPanel.fastButton.isChecked -> FastCell(tissue, x, y)
                    else -> DeadCell(tissue, x, y)
                }
        tissue.setCell(x, y, newCell)
        tissueView!!.invalidate()
    }
}

