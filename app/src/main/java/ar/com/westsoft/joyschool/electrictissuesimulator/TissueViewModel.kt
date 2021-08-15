package ar.com.westsoft.joyschool.electrictissuesimulator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.sync.Mutex

class TissueViewModel: ViewModel {
    val xSize: Int
    val ySize: Int
    val tissue: Array<Array<Cell>>
    val mutex = Mutex()

    constructor(xSize: Int, ySize: Int): super() {
        this.xSize = xSize
        this.ySize = xSize
        this.tissue = Array(xSize)  { col -> Array(ySize) { row -> MyoCell(this, col, row) } }
    }
    constructor(xSize: Int, ySize: Int, tissue: Array<Array<Cell>>): super() {
        this.xSize = xSize
        this.ySize = ySize
        this.tissue = tissue
    }
    fun getCell(x:Int, y:Int): Cell = tissue[x][y]

    fun setCell(x:Int, y:Int, cell:Cell) {
        this.tissue[x][y] = cell
    }

    private fun forAll(funcA: (Cell) -> Unit) {
        tissue.forEach { it.forEach { cell -> cell.apply(funcA) }
        }
    }
    fun clone() = TissueViewModel(xSize, ySize, tissue.clone())

    external fun calcAll()
//    {
//        forAll { it.membranePotential() }
//        forAll { it.calculateCharge() }
//        forAll { it.updateState() }
//    }
}

