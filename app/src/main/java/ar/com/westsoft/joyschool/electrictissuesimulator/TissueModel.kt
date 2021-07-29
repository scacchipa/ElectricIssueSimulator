package ar.com.westsoft.joyschool.electrictissuesimulator

class TissueModel {
    val xSize: Int
    val ySize: Int
    private val tissue: Array<Array<Cell>>

    constructor(xSize: Int,ySize: Int) {
        this.xSize = xSize
        this.ySize = xSize
        this.tissue = Array(xSize)  { col -> Array(ySize) { row -> MyoCell(this, col, row) } }
    }
    constructor(xSize: Int, ySize: Int, tissue: Array<Array<Cell>>) {
        this.xSize = xSize
        this.ySize = ySize
        this.tissue = tissue
    }

    fun getCell(x:Int, y:Int): Cell  = tissue[x][y]

    fun setCell(x:Int, y:Int, cell:Cell) {
        this.tissue[x][y] = cell
    }

    private fun forAll(funcA: (Cell) -> Unit) {
        tissue.forEach { it.forEach { cell -> cell.apply(funcA) }
        }
    }
    fun clone() = TissueModel(xSize, ySize, tissue.clone())

    fun calcAll() {
        forAll { it.membranePotential() }
        forAll { it.calculateCharge() }
        forAll { it.updateState() }
    }

    //fun cloneTissue() = duplicateArray(tissue)
    //private fun duplicateArray(tissue: Array<Array<Cell>>) = Array(xSize) { duplicateSubArray(tissue[it]) }
    //private fun duplicateSubArray(subArray: Array<Cell>) = Array(subArray.size) { subArray[it].clone() }
}

