package ar.com.westsoft.hearttissue

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ar.com.westsoft.hearttissue.dominio.Tissue
import ar.com.westsoft.hearttissue.viewmodel.TissueViewModel
import ar.com.westsoft.joyschool.electrictissuesimulator.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    companion object {
        private external fun nativeInit()
        val tissue: Tissue
        init {
            System.loadLibrary("native-lib")
            nativeInit()
            tissue = Tissue(20, 20)
        }
    }

    val tissueVM: TissueViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.coordenateGraph.viewModel = tissueVM
        binding.tissueView.viewModel = tissueVM
        binding.tissueView.setCellSize(40,40 )
        binding.tissueView.onTouchTissue = { col: Int, row: Int ->
            when {
                binding.buttonPanel.autoButton.isChecked ->
                    tissueVM.setCell(Cell(CellType.AUTOCELL), col, row)
                binding.buttonPanel.myoButton.isChecked ->
                    tissueVM.setCell(Cell(CellType.MYOCELL), col, row)
                binding.buttonPanel.fastButton.isChecked ->
                    tissueVM.setCell(Cell(CellType.FASTCELL), col, row)
                else -> tissueVM.setCell(Cell(CellType.DEADCELL), col, row)
            }
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
                    delay(100)
                    tissueVM.calcAll()
                }
            }
        }
    }
}

