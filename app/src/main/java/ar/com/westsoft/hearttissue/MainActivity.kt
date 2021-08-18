package ar.com.westsoft.hearttissue

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ar.com.westsoft.joyschool.electrictissuesimulator.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

class MainActivity : AppCompatActivity() {
    companion object {
        private external fun nativeInit()
        init {
            System.loadLibrary("native-lib");
            nativeInit()
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
            launch(mainContext) {
                var cloneTimeAcc = 0
                var calcTimeAcc = 0
                var times = 0
                while (true) {

                    val startTime = System.currentTimeMillis()

                    tissueViewModel.calcAll()

                    val calcTime = System.currentTimeMillis()
                    calcTimeAcc += (startTime - calcTime).toInt()
                    times++
                    println("Clone time:${cloneTimeAcc / times} Calc Time:${calcTimeAcc / times}")

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
        when {
            buttonPanel.autoButton.isChecked -> tissueViewModel.setCell(Cell(CellType.AUTOCELL), x, y)
            buttonPanel.myoButton.isChecked -> tissueViewModel.setCell(Cell(CellType.MYOCELL), x, y)
            buttonPanel.fastButton.isChecked -> tissueViewModel.setCell(Cell(CellType.FASTCELL), x, y)
            else -> tissueViewModel.setCell(Cell(CellType.DEADCELL), x, y)
        }
        findViewById<TissueView>(R.id.tissueView).invalidate()
    }
}

