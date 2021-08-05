package ar.com.westsoft.joyschool.electrictissuesimulator

import android.graphics.Color

enum class ChannelState {
    RESTING, OPEN, INACTIVE
}

abstract class Cell(open val tissueViewModel: TissueViewModel, open val colPos: Int, open val rowPos: Int) {
    var state = ChannelState.RESTING

    var vm = -70.0
    protected var charge = -70.0
    var step = 600

    fun upperCell() = if (rowPos > 0) tissueViewModel.getCell(colPos, rowPos - 1) else null
    fun lowerCell() = if (rowPos < tissueViewModel.ySize - 1) tissueViewModel.getCell(colPos, rowPos + 1) else null
    fun leftCell() = if (colPos > 0) tissueViewModel.getCell(colPos - 1, rowPos) else null
    fun rightCell() = if (colPos < tissueViewModel.xSize - 1) tissueViewModel.getCell(colPos + 1, rowPos) else null
    fun lowerLeftCell() =
        if (colPos > 0 && rowPos < tissueViewModel.ySize - 1)
            tissueViewModel.getCell(colPos - 1, rowPos + 1)
        else null
    fun lowerRightCell() =
        if (colPos < tissueViewModel.xSize - 1 && rowPos < tissueViewModel.ySize - 1)
            tissueViewModel.getCell(colPos + 1, rowPos + 1)
        else null
    fun upperLeftCell() =
        if (colPos > 0 && rowPos > 0)
            tissueViewModel.getCell(colPos - 1, rowPos - 1)
        else null
    fun upperRightCell() =
        if (colPos < tissueViewModel.xSize - 1 && rowPos > 0)
            tissueViewModel.getCell(colPos + 1, rowPos - 1)
        else null

    abstract fun calculateCharge()
    abstract fun updateState()
    abstract fun clone(): Cell
    abstract fun membranePotential()
    abstract fun stateColor(): Int

    companion object {
        fun buildAlphaVector(incomingVector: List<Pair<Double, Double>>): DoubleArray {
            var lastTime = incomingVector[0].first
            var lastVm = incomingVector[0].second
            val alphaList = MutableList(0) { 0.0 }
            incomingVector.forEach { (time, vm) ->
                for (idx in 0 until time.toInt()) {
                    lastTime += 1
                    alphaList += lastVm + (vm - lastVm) / time * idx
                }
                lastVm = vm
            }
            return alphaList.toDoubleArray()
        }
    }
}

data class MyoCell(override val tissueViewModel: TissueViewModel, override val colPos: Int, override val rowPos: Int)
    : Cell(tissueViewModel, colPos, rowPos) {
    companion object {
        val alphaVector = buildAlphaVector(arrayOf(
            Pair(0.0, -75.0),   Pair(5.0, -70.0),
            Pair(30.0, 25.0),   Pair(5.0, 10.0),   Pair(5.0, 7.0),    Pair(400.0, 5.0),
            Pair(10.0, 4.0),    Pair(10.0, 3.0),    Pair(10.0, 2.0),    Pair(10.0, 0.0),
            Pair(10.0, -2.0),   Pair(10.0, -4.0),   Pair(10.0, -7.0),   Pair(10.0, -11.0),
            Pair(10.0, -16.0),  Pair(10.0, -22.0),  Pair(10.0, -29.0),  Pair(10.0, -37.0),
            Pair(10.0, -45.0),  Pair(10.0, -54.0),  Pair(10.0, -62.0),  Pair(10.0, -67.0),
            Pair(10.0, -71.0),  Pair(10.0, -74.0),  Pair(10.0, -75.0),  Pair(10.0, -75.0),
            Pair(10.0, -75.0),  Pair(10.0, -75.0),  Pair(10.0, -75.0), Pair(400.0, -75.0))
            .map { value -> Pair(value.first, (value.second + 20.0) * 1.3) })

        val stateColors = hashMapOf(
            ChannelState.RESTING to Color.argb(0xFF, 0x03, 0x2B, 0x43),
            ChannelState.OPEN to Color.argb(0xFF,0xF9, 0xC8, 0x0E),
            ChannelState.INACTIVE to Color.argb(0xFF,0xEA, 0x35, 0x46))
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
        this.charge = (0.4 * vm) +
                    (0.075 * (upperCell()?:this).vm) +
                    (0.075 * (lowerCell()?:this).vm) +
                    (0.075 * (leftCell()?:this).vm) +
                    (0.075 * (rightCell()?:this).vm) +
                    (0.075 * (lowerRightCell()?:this).vm) +
                    (0.075 * (upperRightCell()?:this).vm) +
                    (0.075 * (upperLeftCell()?:this).vm) +
                    (0.075 * (lowerLeftCell()?:this).vm)
    }
    override fun membranePotential() {
        this.vm = alphaVector[step]
    }
    override fun stateColor(): Int = stateColors[state]!!
    override fun clone(): Cell = MyoCell(tissueViewModel, colPos, rowPos)
}

class AutoCell(tissueViewModel: TissueViewModel, colPos: Int, rowPos: Int)
    : Cell(tissueViewModel, colPos, rowPos) {
    companion object {
        val alphaVector = buildAlphaVector(arrayOf(
            Pair(0.0, -55.0),  Pair(15.0, -53.0), Pair(15.0, -50.0), Pair(15.0, -43.0),
            Pair(15.0, -35.0), Pair(15.0, -27.0), Pair(15.0, -17.0), Pair(15.0, -7.0),
            Pair(15.0, -1.0),  Pair(15.0, 5.0),   Pair(15.0, 7.0),   Pair(15.0, 8.0),
            Pair(15.0, 8.0),   Pair(15.0, 8.0),   Pair(15.0, 7.0),   Pair(15.0, 6.0),
            Pair(15.0, 4.0),   Pair(15.0, 1.0),   Pair(15.0, -2.0),  Pair(15.0, -6.0),
            Pair(15.0, -10.0), Pair(15.0, -14.0), Pair(15.0, -19.0), Pair(15.0, -24.0),
            Pair(15.0, -29.0), Pair(15.0, -34.0), Pair(15.0, -38.0), Pair(15.0, -42.0),
            Pair(15.0, -46.0), Pair(15.0, -50.0), Pair(15.0, -54.0),  Pair(15.0, -57.0),
            Pair(15.0, -60.0), Pair(15.0, -62.0), Pair(15.0, -64.0),
            Pair(15.0, -65.0), Pair(15.0, -65.0), Pair(10.0, -65.0), Pair(3000.0, -12.0))
            .map { value -> Pair(value.first, (value.second + 10.0) * 1.3) })

        val stateColors = hashMapOf(
            ChannelState.RESTING to Color.argb(0xFF, 0xFE, 0xB3, 0x8B),
            ChannelState.OPEN to Color.argb(0xFF, 0xF9, 0xC8, 0x0E),
            ChannelState.INACTIVE to Color.argb(0xFF, 0xEA, 0x35, 0x46))
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
        charge = (0.6 * vm) +
                    (0.05 * (upperCell()?:this).vm) +
                    (0.05 * (lowerCell()?:this).vm) +
                    (0.05 * (leftCell()?:this).vm) +
                    (0.05 * (rightCell()?:this).vm) +
                    (0.05 * (lowerRightCell()?:this).vm) +
                    (0.05 * (upperRightCell()?:this).vm) +
                    (0.05 * (upperLeftCell()?:this).vm) +
                    (0.05 * (lowerLeftCell()?:this).vm)
    }
    override fun membranePotential() {
        this.vm = alphaVector[step]
    }
    override fun stateColor(): Int = stateColors[state]!!
    override fun clone(): Cell = AutoCell(tissueViewModel, colPos, rowPos)
}

class DeadCell (tissueViewModel: TissueViewModel, colPos: Int, rowPos: Int)
    : Cell(tissueViewModel, colPos, rowPos) {
    companion object {
        val alphaVector = doubleArrayOf(0.0)
        val stateColors = hashMapOf (
            ChannelState.RESTING to Color.argb(0xFF, 0x00, 0x00, 0x00))
    }

    override fun calculateCharge() {
        TODO("Not yet implemented")
    }

    override fun updateState() {
        TODO("Not yet implemented")
    }
    override fun membranePotential() {

    }
    override fun stateColor(): Int = stateColors[state]!!
    override fun clone(): Cell = DeadCell(tissueViewModel, colPos, rowPos)
}

class FastCell (tissueViewModel: TissueViewModel, colPos: Int, rowPos: Int)
    : Cell(tissueViewModel, colPos, rowPos) {
    companion object {
        val alphaVector = buildAlphaVector(arrayOf(
                Pair(0.0, -75.0),
                Pair(10.0, 25.0),   Pair(5.0, 10.0),   Pair(5.0, 7.0),    Pair(400.0, 5.0),
                Pair(10.0, 4.0),    Pair(10.0, 3.0),    Pair(10.0, 2.0),    Pair(10.0, 0.0),
                Pair(10.0, -2.0),   Pair(10.0, -4.0),   Pair(10.0, -7.0),   Pair(10.0, -11.0),
                Pair(10.0, -16.0),  Pair(10.0, -22.0),  Pair(10.0, -29.0),  Pair(10.0, -37.0),
                Pair(10.0, -45.0),  Pair(10.0, -54.0),  Pair(10.0, -62.0),  Pair(10.0, -67.0),
                Pair(10.0, -71.0),  Pair(10.0, -74.0),  Pair(10.0, -75.0),  Pair(10.0, -75.0),
                Pair(10.0, -75.0),  Pair(10.0, -75.0),  Pair(10.0, -75.0), Pair(400.0, -75.0))
            .map { value -> Pair(value.first, (value.second + 20.0) * 1.3) })
        val stateColors = hashMapOf(
            ChannelState.RESTING to Color.argb(0xFF, 0x03, 0x2B, 0x43),
            ChannelState.OPEN to Color.argb(0xFF, 0xF9, 0xC8, 0x0E),
            ChannelState.INACTIVE to Color.argb(0xFF, 0xEA, 0x35, 0x46))
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
        charge = (0.4 * vm) +
                    (0.075 * (upperCell()?:this).vm) +
                    (0.075 * (lowerCell()?:this).vm) +
                    (0.075 * (leftCell()?:this).vm) +
                    (0.075 * (rightCell()?:this).vm) +
                    (0.075 * (lowerRightCell()?:this).vm) +
                    (0.075 * (upperRightCell()?:this).vm) +
                    (0.075 * (upperLeftCell()?:this).vm) +
                    (0.075 * (lowerLeftCell()?:this).vm)
    }
    override fun membranePotential() {
        this.vm = alphaVector[step]
    }
    override fun stateColor(): Int = stateColors[state]!!
    override fun clone(): Cell = FastCell(tissueViewModel, colPos, rowPos)
}

