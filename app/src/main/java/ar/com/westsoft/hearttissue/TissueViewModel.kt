package ar.com.westsoft.hearttissue

import androidx.lifecycle.ViewModel


class TissueViewModel(val xSize: Int, val ySize: Int) : ViewModel() {
    init {
        setUp(xSize, ySize)
    }

    external fun setUp(cellRowCount: Int, cellColCount: Int)
    external fun setCell(cell: Cell, x:Int, y: Int)
    external fun getCell(x:Int, y: Int): Cell
    external fun calcAll()
//    {
//        forAll { it.membranePotential() }
//        forAll { it.calculateCharge() }
//        forAll { it.updateState() }
//    }
}

