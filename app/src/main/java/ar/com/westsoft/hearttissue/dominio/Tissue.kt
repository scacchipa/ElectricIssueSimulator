package ar.com.westsoft.hearttissue.dominio

import ar.com.westsoft.hearttissue.Cell

class Tissue(val colCount: Int, val rowCount: Int) {
    init {
        setUp(colCount, rowCount)
    }

    private external fun setUp(cellColCount: Int, cellRowCount: Int)
    external fun setCell(cell: Cell, x:Int, y: Int)
    external fun getCell(x:Int, y: Int): Cell
    external fun calcAll()
    external fun getColorArray(): IntArray
}


