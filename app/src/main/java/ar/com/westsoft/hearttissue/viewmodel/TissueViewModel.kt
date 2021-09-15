package ar.com.westsoft.hearttissue.viewmodel

import android.graphics.Point
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.com.westsoft.hearttissue.Cell
import ar.com.westsoft.hearttissue.MainActivity
import ar.com.westsoft.hearttissue.dominio.Tissue
import ar.com.westsoft.hearttissue.model.CoordGraphModel
import ar.com.westsoft.hearttissue.model.TissueModel

class TissueViewModel: ViewModel() {
    val tissue = MainActivity.tissue
    var time: Float = 0f
    val tissueModel = MutableLiveData(
        TissueModel(tissue.colCount, tissue.rowCount, tissue.getColorArray())
    ) // model => IntArray( 100 * 100)
    val coordGraphModel = MutableLiveData(CoordGraphModel()) // model => CoordGraphModel()

    fun setCell(cell: Cell, x: Int, y: Int) {
        tissue.setCell(cell, x, y)
    }

    fun getCell(x: Int, y: Int): Cell = tissue.getCell(x, y)
    fun calcAll() {
        tissue.calcAll()

        val cell = tissue.getCell(10, 10)
        val newCoord = coordGraphModel.value?.add(Pair(time, cell.vm.toFloat()))
            ?:CoordGraphModel()
        time++

        tissueModel.postValue(tissueModel.value?.copy(colors = tissue.getColorArray()))
        coordGraphModel.postValue(newCoord)
    }

    fun getPosCell(xPixels: Int, yPixels: Int): Point =
        tissue.getPosCell(xPixels, yPixels)

    fun createNewModel() = TissueModel(tissue.colCount, tissue.rowCount, tissue.getColorArray())

    fun getColors(): IntArray = tissueModel.value?.colors
        ?: IntArray(tissue.colCount * tissue.rowCount) {
            (Math.random() * 0xFFFFFF).toInt() + 0xFF000000.toInt()
        }
}