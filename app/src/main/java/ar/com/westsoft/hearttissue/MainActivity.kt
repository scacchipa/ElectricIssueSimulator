package ar.com.westsoft.hearttissue

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ar.com.westsoft.hearttissue.dominio.Tissue
import ar.com.westsoft.hearttissue.viewmodel.TissueViewModel
import ar.com.westsoft.joyschool.electrictissuesimulator.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
import kotlin.time.Duration

class MainActivity : AppCompatActivity() {
    companion object {
        private external fun nativeInit()
        val tissue: Tissue
        init {
            System.loadLibrary("native-lib")
            nativeInit()
            tissue = Tissue(100, 100)
        }
    }

    val tissueVM: TissueViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tissueVM.tissueModel.observe(this) { println("Tissue Model Changed.") }
        tissueVM.coordGraphModel.observe(this) {
            print("Coordenate Graph Model.")
            binding.coordenateGraph.coordModel = tissueVM.coordGraphModel.value
                ?:binding.coordenateGraph.coordModel
            binding.coordenateGraph.postInvalidate()
        }
        binding.tissueView.viewModel = tissueVM;
        binding.tissueView.onTouchTissue = { col: Int, row: Int ->
            tissueVM.setCell(Cell(cellType = CellType.AUTOCELL), col, row)
            binding.tissueView.invalidate()
        }

        val mainContext = newSingleThreadContext("CounterContext")


        CoroutineScope(Dispatchers.Main).launch {
            launch(mainContext) {
                Log.d("Coroutine","Launch Eternal loop.")
                while (true) {
                    delay(1000)
                    Log.d("Coroutine","CalcAll")
                    tissueVM.calcAll()
                }
            }
        }
    }

    fun onClickOnCell(x: Int, y: Int) {
        tissueVM.getPosCell(x, y)?.let { point ->
            when {
                binding.buttonPanel.autoButton.isChecked -> tissueVM.setCell(Cell(CellType.AUTOCELL), point.x, point.y)
                binding.buttonPanel.myoButton.isChecked -> tissueVM.setCell(Cell(CellType.MYOCELL), point.x, point.y)
                binding.buttonPanel.fastButton.isChecked -> tissueVM.setCell(Cell(CellType.FASTCELL), point.x, point.y)
                else -> tissueVM.setCell(Cell(CellType.DEADCELL), point.x, point.y)
            }
        }
    }
}

