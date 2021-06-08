package ar.com.westsoft.joyschool.electrictissuesimulator

import android.graphics.Paint

enum class ChannelState {
    RESTING, OPEN, INACTIVE
}

abstract class Cell(val tissue: Tissue, val colPos: Int, val rowPos: Int) {
    lateinit var alphaVector: List<Double>
    protected var state = ChannelState.RESTING

    var vm = -70.0
    protected var charge = -70.0
    var step = 70

    var upperCell: Cell? = null
    var lowerCell: Cell? = null
    var leftCell: Cell? = null
    var rightCell: Cell? = null
    var lowerLeftCell: Cell? = null
    var lowerRightCell: Cell? = null
    var upperLeftCell: Cell? = null
    var upperRightCell: Cell? = null

    abstract fun calculateCharge()
    abstract fun updateState()

    val stateColors = HashMap<ChannelState, Paint>();
    fun createPaint(state: ChannelState, color: Int) {
        val p = Paint(0)
        p.color = color
        p.alpha = 255
        stateColors[state] = p
    }
    fun stateColor(): Paint = stateColors[state]!!

    fun refreshCellReference() {
        if (rowPos > 0) upperCell = tissue.getCell(colPos, rowPos - 1);
        if (rowPos < tissue.ySize - 1) lowerCell = tissue.getCell(colPos, rowPos + 1);
        if (colPos > 0) {
            this.leftCell = tissue.getCell(colPos - 1, rowPos);
            if (rowPos < tissue.ySize - 1) lowerLeftCell = tissue.getCell(colPos - 1, rowPos + 1);
            if (rowPos > 0) upperLeftCell = tissue.getCell(colPos - 1, rowPos - 1);
        }
        if (colPos < tissue.xSize - 1) {
            this.rightCell = tissue.getCell(colPos + 1, rowPos);
            if (rowPos > 0) upperRightCell = tissue.getCell(colPos + 1, rowPos - 1);
            if (rowPos < tissue.ySize - 1) lowerRightCell = tissue.getCell(colPos + 1, rowPos + 1);
        }
    }

    fun refreshNearReference() {
        upperCell?.refreshCellReference()
        lowerCell?.refreshCellReference()
        leftCell?.refreshCellReference()
        rightCell?.refreshCellReference()
        lowerLeftCell?.refreshCellReference()
        lowerRightCell?.refreshCellReference()
        upperLeftCell?.refreshCellReference()
        upperRightCell?.refreshCellReference()
    }

    fun membranePotential() {
        this.vm = alphaVector[step]
    }
}

class MyoCell(tissue: Tissue, colPos: Int, rowPos: Int)
    : Cell(tissue, colPos, rowPos) {
    init {
        alphaVector = doubleArrayOf(
            -75.0, -70.0, -65.0, -60.0, -55.0, -50.0,
            -45.0, -40.0, -35.0, -30.0, -25.0, -20.0, -15.0, -10.0, -5.0, 0.0, 5.0, 10.0, 15.0,
            20.0, 25.0, 10.0, 7.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0,
            5.0, 5.0, 5.0, 5.0, 4.0, 3.0, 2.0, 0.0, -2.0, -4.0, -7.0, -11.0, -16.0, -22.0, -29.0,
            -37.0, -45.0, -54.0, -62.0, -67.0, -71.0, -74.0, -75.0, -75.0, -75.0, -75.0, -75.0,
            -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0,
            -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0,
            -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0,
            -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0)
            .map { Vm -> (Vm + 20.0) * 1.3 }
        createPaint(ChannelState.RESTING, 0x032B43)//blue
        createPaint(ChannelState.OPEN, 0xF9C80E) //green
        createPaint(ChannelState.INACTIVE, 0xEA3546) //red
    }
    override fun updateState() {
        if (state == ChannelState.RESTING && charge > -55) {
            state = ChannelState.OPEN
            step = 0
        }
        else if (state == ChannelState.OPEN && charge > 0) {
            state = ChannelState.INACTIVE
        }
        else if (state == ChannelState.INACTIVE && charge < -55) {
            state = ChannelState.RESTING
        }
        if (step < alphaVector.size - 1) step++
    }
    override fun calculateCharge() {
        if (colPos < 1 || colPos >= (tissue.xSize - 1)
            || rowPos < 1 || rowPos >= (tissue.ySize - 1))
            return;

        this.charge = (0.4 * vm) +
                    (0.075 * upperCell!!.vm) +
                    (0.075 * lowerCell!!.vm) +
                    (0.075 * leftCell!!.vm) +
                    (0.075 * rightCell!!.vm) +
                    (0.075 * lowerRightCell!!.vm) +
                    (0.075 * upperRightCell!!.vm) +
                    (0.075 * upperLeftCell!!.vm) +
                    (0.075 * lowerLeftCell!!.vm)
    }
}

class AutoCell(tissue: Tissue, colPos: Int, rowPos: Int)
    : Cell(tissue, colPos, rowPos) {
    init {
        alphaVector = doubleArrayOf(-55.0, -53.0, -50.0, -43.0, -35.0, -27.0, -17.0, -7.0, -1.0,
            5.0, 7.0, 8.0, 8.0, 8.0, 7.0, 6.0, 4.0, 1.0, -2.0, -6.0, -10.0, -14.0, -19.0, -24.0, -29.0,
            -34.0, -38.0, -42.0, -46.0, -50.0, -54.0, -57.0, -60.0, -62.0, -64.0, -65.0, -65.0,
            -65.0, -64.0, -64.0, -63.0, -63.0,  -62.0, -62.0, -61.0, -61.0, -60.0, -60.0, -59.0,
            -59.0, -58.0, -58.0, -57.0, -57.0, -56.0, -56.0, -55.0, -55.0, -54.0, -54.0, -53.0,
            -53.0, -52.0, -52.0, -51.0, -50.0, -50.0, -49.0, -49.0, -48.0, -48.0, -47.0, -47.0,
            -46.0, -46.0, -45.0, -45.0, -44.0, -44.0, -43.0, -43.0, -42.0, -42.0, -41.0, -40.0,
            -40.0, -39.0, -39.0, -38.0, -38.0, -37.0, -37.0, -36.0, -36.0, -35.0, -35.0, -34.0,
            -34.0, -33.0, -33.0, -32.0, -32.0, -31.0, -31.0, -30.0, -30.0, -29.0, -29.0, -28.0,
            -28.0, -27.0, -27.0, -26.0, -26.0, -25.0, -25.0, -24.0, -24.0, -23.0, -23.0, -22.0,
            -22.0, -21.0, -21.0, -20.0, -20.0, -19.0, -19.0, -18.0, -18.0, -17.0, -17.0, -16.0,
            -16.0, -15.0, -15.0, -14.0, -14.0, -13.0, -13.0, -12.0, -12.0)
                .flatMap {Vm -> listOf(Vm, Vm, Vm) }
                .map{ Vm -> (Vm + 10) * 1.3 }
        createPaint(ChannelState.RESTING, 0xfeb38b) //blue
        createPaint(ChannelState.OPEN, 0xF9C80E) //green
        createPaint(ChannelState.INACTIVE, 0xEA3546) //red
    }
    override fun updateState() {
        if (state == ChannelState.RESTING && charge > -45) {
            state = ChannelState.OPEN
            step = 0
        }
        else if (state == ChannelState.OPEN && charge > 0) {
            state = ChannelState.INACTIVE
        }
        else if (state == ChannelState.INACTIVE && charge < -45) {
            state = ChannelState.RESTING
        }
        if (step < alphaVector.size - 1) step++
    }
    override fun calculateCharge() {
        if (colPos < 1 || colPos >= (tissue.xSize - 1) || rowPos < 1
            || rowPos >= (tissue.ySize - 1))
            return;

        charge = (0.6 * vm) +
                    (0.05 * upperCell!!.vm) +
                    (0.05 * lowerCell!!.vm) +
                    (0.05 * leftCell!!.vm) +
                    (0.05 * rightCell!!.vm) +
                    (0.05 * lowerRightCell!!.vm) +
                    (0.05 * upperRightCell!!.vm) +
                    (0.05 * upperLeftCell!!.vm) +
                    (0.05 * lowerLeftCell!!.vm);
    }
}

class DeadCell (tissue: Tissue, colPos: Int, rowPos: Int)
    : Cell(tissue, colPos, rowPos) {
    init {
        createPaint(ChannelState.RESTING, 0x000000)
    }
    override fun calculateCharge() {
        TODO("Not yet implemented")
    }

    override fun updateState() {
        TODO("Not yet implemented")
    }
}

class FastCell (tissue: Tissue, colPos: Int, rowPos: Int)
    : Cell(tissue, colPos, rowPos) {
    init {
        alphaVector = doubleArrayOf( -75.0, -20.0, 25.0, 10.0, 7.0, 5.0, 5.0, 5.0, 5.0, 5.0,
            5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 4.0, 3.0, 2.0,
            0.0, -2.0, -4.0, -7.0, -11.0, -16.0, -22.0, -29.0, -37.0, -45.0, -54.0, -62.0,
            -67.0, -71.0, -74.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0,
            -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0,
            -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0,
            -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0, -75.0,
            -75.0, -75.0, -75.0, -75.0, -75.0)
            .map {Vm -> (Vm + 20.0) * 1.3 }
        createPaint(ChannelState.RESTING, 0xFFFFF0) //blue
        createPaint(ChannelState.OPEN, 0xF9C80E) //yellow
        createPaint(ChannelState.INACTIVE, 0xEA3546) //red
    }

    override fun updateState() {
        if (state == ChannelState.RESTING && charge > -55) {
            state = ChannelState.OPEN
            step = 0
        }
        else if (state == ChannelState.OPEN && charge > 0) {
            state = ChannelState.INACTIVE
        }
        else if (state == ChannelState.INACTIVE && charge < -55) {
            state = ChannelState.RESTING
        }
        if (step < alphaVector.size - 1) step++
    }
    override fun calculateCharge() {
        if (colPos < 1 || colPos >= (tissue.xSize - 1) || rowPos < 1
            || rowPos >= (tissue.ySize - 1)) {
            return;
        }

        charge = (0.4 * vm) +
                    (0.075 * upperCell!!.vm) +
                    (0.075 * lowerCell!!.vm) +
                    (0.075 * leftCell!!.vm) +
                    (0.075 * rightCell!!.vm) +
                    (0.075 * lowerRightCell!!.vm) +
                    (0.075 * upperRightCell!!.vm) +
                    (0.075 * upperLeftCell!!.vm) +
                    (0.075 * lowerLeftCell!!.vm)
    }
}

