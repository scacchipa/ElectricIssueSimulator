package ar.com.westsoft.joyschool.electrictissuesimulator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    var tissueModel = TissueModel(50,50)
    var coordGraphModel = CoordGraphModel()
    lateinit var buttonPanel: PanelView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tissueView: TissueView = findViewById(R.id.tissueView)
         tissueView.tissueModel = tissueModel
        tissueView.callback = this

        val coordenateGraph: CoordenateGraph = findViewById(R.id.coordenateGraph)
        coordenateGraph.coordGraphModel = coordGraphModel

        buttonPanel = findViewById(R.id.buttonPanel)

        Thread {
            var tempo = 0f
            while(true) {
                Thread.sleep(50)
                val tempTissue = tissueView.tissueModel!!.clone()
                tempTissue.calcAll()
                tissueView.tissueModel = tempTissue

                val cell = tissueView.tissueModel!!.getCell(10,10)
                coordGraphModel = coordGraphModel.add( Pair(tempo, cell.vm.toFloat()) )
                tempo++
                coordenateGraph.coordGraphModel = coordGraphModel
                runOnUiThread {
                    tissueView.invalidate()
                    coordenateGraph.invalidate()
                }
            }
        }.start()
    }
    fun onClickOnCell(x: Int, y: Int) {
        println("$x, $y")
        val newCell: Cell = when {
                    buttonPanel.autoButton.isChecked -> AutoCell(tissueModel!!, x, y)
                    buttonPanel.myoButton.isChecked -> MyoCell(tissueModel!!, x, y)
                    buttonPanel.fastButton.isChecked -> FastCell(tissueModel!!, x, y)
                    else -> DeadCell(tissueModel!!, x, y)
                }
        tissueModel!!.setCell(x, y, newCell)
        findViewById<TissueView>(R.id.tissueView).invalidate()
    }
}

