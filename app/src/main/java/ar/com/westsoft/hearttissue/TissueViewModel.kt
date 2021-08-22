package ar.com.westsoft.hearttissue

import androidx.lifecycle.ViewModel


class TissueViewModel(val colCount: Int, val rowCount: Int) : ViewModel() {
    init {
        setUp(colCount, rowCount)
    }

    private external fun setUp(cellRowCount: Int, cellColCount: Int)
    external fun setCell(cell: Cell, x:Int, y: Int)
    external fun getCell(x:Int, y: Int): Cell
    external fun calcAll()

}

