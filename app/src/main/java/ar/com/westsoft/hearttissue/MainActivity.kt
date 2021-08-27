package ar.com.westsoft.hearttissue

import android.os.Bundle
import android.util.Log
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

                while (true) {

                    tissueViewModel.calcAll()

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
        Log.d("OnClick", "Pos: $x, $y")
        val pointPos = tissueViewModel.getPosCell(x, y)
        when {
            buttonPanel.autoButton.isChecked -> tissueViewModel.setCell(Cell(CellType.AUTOCELL), pointPos.x, pointPos.y)
            buttonPanel.myoButton.isChecked -> tissueViewModel.setCell(Cell(CellType.MYOCELL), pointPos.x, pointPos.y)
            buttonPanel.fastButton.isChecked -> tissueViewModel.setCell(Cell(CellType.FASTCELL), pointPos.x, pointPos.y)
            else -> tissueViewModel.setCell(Cell(CellType.DEADCELL), pointPos.x, pointPos.y)
        }
        findViewById<TissueView>(R.id.tissueView).invalidate()
    }
}

