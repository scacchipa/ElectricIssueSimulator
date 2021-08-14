package ar.com.westsoft.joyschool.electrictissuesimulator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

class MainActivity : AppCompatActivity() {
    companion object {
        init {
            System.loadLibrary("native-lib");
        }
    }
    var tissueViewModel = TissueViewModel(100,100)
    var coordGraphModel = CoordGraphModel()
    lateinit var buttonPanel: PanelView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tissueView: TissueView = findViewById(R.id.tissueView)
        tissueView.tissueViewModel = tissueViewModel
        tissueView.callback = this

        val coordenateGraph: CoordenateGraph = findViewById(R.id.coordenateGraph)
        coordenateGraph.coordGraphModel = coordGraphModel

        buttonPanel = findViewById(R.id.buttonPanel)
        val mainContext = newSingleThreadContext("CounterContext")

        CoroutineScope(Dispatchers.Main).launch {
            var tempo = 0f
            val mainJob = launch(mainContext) {
                var cloneTimeAcc = 0
                var calcTimeAcc = 0
                var times = 0
                while (true) {

                    val startTime = System.currentTimeMillis()
                    val tempTissue = tissueView.tissueViewModel!!.clone()
                    val cloneTime = System.currentTimeMillis()
                    tempTissue.calcAll()
                    val calcTime = System.currentTimeMillis()
                    cloneTimeAcc += (cloneTime - startTime).toInt()
                    calcTimeAcc += (calcTime - cloneTime).toInt()
                    times++
                    println("Clone time:${cloneTimeAcc / times} Calc Time:${calcTimeAcc / times}")

                    tissueView.tissueViewModel = tempTissue

                    val cell = tissueView.tissueViewModel!!.getCell(10, 10)
                    coordGraphModel = coordGraphModel.add(Pair(tempo, cell.vm.toFloat()))
                    tempo++
                    coordenateGraph.coordGraphModel = coordGraphModel

                    launch(Dispatchers.Main) {
                        tissueView.invalidate()
                        coordenateGraph.invalidate()
                    }
                }
            }
        }
    }
    fun onClickOnCell(x: Int, y: Int) {
        println("$x, $y")
        val newCell: Cell = when {
                    buttonPanel.autoButton.isChecked -> AutoCell(tissueViewModel, x, y)
                    buttonPanel.myoButton.isChecked -> MyoCell(tissueViewModel, x, y)
                    buttonPanel.fastButton.isChecked -> FastCell(tissueViewModel, x, y)
                    else -> DeadCell(tissueViewModel, x, y)
                }
        tissueViewModel.setCell(x, y, newCell)
        findViewById<TissueView>(R.id.tissueView).invalidate()
    }
}

