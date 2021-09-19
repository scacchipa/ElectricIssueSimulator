package ar.com.westsoft.hearttissue

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ar.com.westsoft.hearttissue.dominio.Tissue
import ar.com.westsoft.hearttissue.viewmodel.TissueViewModel
import ar.com.westsoft.joyschool.electrictissuesimulator.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

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

        binding.tissueView.viewModel = tissueVM
        binding.coordenateGraph.viewModel = tissueVM
        binding.tissueView.onTouchTissue = { col: Int, row: Int ->
            tissueVM.setCell(Cell(cellType = CellType.AUTOCELL), col, row)
            binding.tissueView.invalidate()
        }
        binding.ctrlPanel.onChanged = { col: Int, row: Int ->
            tissueVM.selCoordModel.postValue(Point(col, row))
        }
        tissueVM.coordGraphModel.observe(this) {
            binding.coordenateGraph.postInvalidate()
        }

        tissueVM.tissueModel.observe(this) {
            binding.tissueView.postInvalidate()
        }

        val mainContext = newSingleThreadContext("CounterContext")

        CoroutineScope(Dispatchers.Main).launch {
            launch(mainContext) {
                Log.d("Coroutine","Launch Eternal loop.")
                while (true) {
                    //delay(100)
                    tissueVM.calcAll()
                }
            }
        }
    }

    fun onClickOnCell(x: Int, y: Int) {
        binding.tissueView.getPosCell(x, y).let { point ->
            when {
                binding.buttonPanel.autoButton.isChecked -> tissueVM.setCell(Cell(CellType.AUTOCELL), point.x, point.y)
                binding.buttonPanel.myoButton.isChecked -> tissueVM.setCell(Cell(CellType.MYOCELL), point.x, point.y)
                binding.buttonPanel.fastButton.isChecked -> tissueVM.setCell(Cell(CellType.FASTCELL), point.x, point.y)
                else -> tissueVM.setCell(Cell(CellType.DEADCELL), point.x, point.y)
            }
        }
    }
}

