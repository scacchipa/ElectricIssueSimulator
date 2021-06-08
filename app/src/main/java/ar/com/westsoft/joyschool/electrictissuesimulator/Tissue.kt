package ar.com.westsoft.joyschool.electrictissuesimulator

class Tissue(val xSize: Int, val ySize: Int) {

    var tissue: Array<Array<Cell>> =
        Array(xSize)  { col -> Array(ySize) { row -> MyoCell(this, col, row) } }


    init {
        forAll( Cell::refreshCellReference )
    }

    fun getCell(x:Int, y:Int): Cell  = tissue[x][y]

    fun setCell(x:Int, y:Int, cell:Cell) {
        this.tissue[x][y] = cell
        cell.refreshCellReference()
        cell.refreshNearReference()
    }

    fun forAll(funcA: (Cell) -> Unit) {
        tissue.forEach { it.forEach { cell -> cell.apply(funcA) }
        }
    }
}

