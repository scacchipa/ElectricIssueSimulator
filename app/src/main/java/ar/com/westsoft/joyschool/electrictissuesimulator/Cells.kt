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
            var lastTime = 0.0
            var lastVm = incomingVector[0].second
            val alphaList = MutableList(0) { 0.0 }
            incomingVector.forEach { (time, vm) ->
                for (idx in 0 until (time - lastTime).toInt()) {
                    lastVm += (vm - lastVm) / (time - lastTime)
                    lastTime += 1
                    alphaList += lastVm
                }
            }
            return alphaList.toDoubleArray()
        }
    }
}

data class MyoCell(override val tissueViewModel: TissueViewModel, override val colPos: Int, override val rowPos: Int)
    : Cell(tissueViewModel, colPos, rowPos) {
    companion object {
        val alphaVector = buildAlphaVector(arrayOf(
            Pair(0.0, -75.0),   Pair(10.0, -70.0),  Pair(20.0, -65.0),  Pair(30.0, -60.0),
            Pair(40.0, -55.0),  Pair(50.0, -50.0),  Pair(60.0, -45.0),  Pair(70.0, -40.0),
            Pair(80.0, -35.0),  Pair(90.0, -30.0),  Pair(100.0, -25.0), Pair(110.0, -20.0),
            Pair(120.0, -15.0), Pair(130.0, -10.0), Pair(140.0, -5.0),  Pair(150.0, 0.0),
            Pair(160.0, 5.0),   Pair(170.0, 10.0),  Pair(180.0, 15.0),  Pair(190.0, 20.0),
            Pair(200.0, 25.0),  Pair(210.0, 10.0),  Pair(220.0, 7.0),   Pair(230.0, 5.0),
            Pair(240.0, 5.0),   Pair(250.0, 5.0),   Pair(260.0, 5.0),   Pair(270.0, 5.0),
            Pair(280.0, 5.0),   Pair(290.0, 5.0),   Pair(300.0, 5.0),   Pair(310.0, 5.0),
            Pair(320.0, 5.0),   Pair(330.0, 5.0),   Pair(340.0, 5.0),   Pair(350.0, 5.0),
            Pair(360.0, 5.0),   Pair(370.0, 5.0),   Pair(380.0, 5.0),   Pair(390.0, 5.0),
            Pair(400.0, 4.0),   Pair(410.0, 3.0),   Pair(420.0, 2.0),   Pair(430.0, 0.0),
            Pair(440.0, -2.0),  Pair(450.0, -4.0),  Pair(460.0, -7.0),  Pair(470.0, -11.0),
            Pair(480.0, -16.0), Pair(490.0, -22.0), Pair(500.0, -29.0), Pair(510.0, -37.0),
            Pair(520.0, -45.0), Pair(530.0, -54.0), Pair(540.0, -62.0), Pair(550.0, -67.0),
            Pair(560.0, -71.0), Pair(570.0, -74.0), Pair(580.0, -75.0), Pair(590.0, -75.0),
            Pair(600.0, -75.0), Pair(610.0, -75.0), Pair(620.0, -75.0), Pair(1000.0, -75.0))
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
        if (colPos < 1 || colPos >= (tissueViewModel.xSize - 1)
            || rowPos < 1 || rowPos >= (tissueViewModel.ySize - 1))
            return

        this.charge = (0.4 * vm) +
                    (0.075 * upperCell()!!.vm) +
                    (0.075 * lowerCell()!!.vm) +
                    (0.075 * leftCell()!!.vm) +
                    (0.075 * rightCell()!!.vm) +
                    (0.075 * lowerRightCell()!!.vm) +
                    (0.075 * upperRightCell()!!.vm) +
                    (0.075 * upperLeftCell()!!.vm) +
                    (0.075 * lowerLeftCell()!!.vm)
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
            Pair(0.0, -55.0), Pair(10.0, -53.0), Pair(20.0, -50.0), Pair(30.0, -43.0),
            Pair(40.0, -35.0), Pair(50.0, -27.0), Pair(60.0, -17.0), Pair(70.0, -7.0),
            Pair(80.0, -1.0), Pair(90.0, 5.0), Pair(100.0, 7.0), Pair(110.0, 8.0),
            Pair(120.0, 8.0), Pair(130.0, 8.0), Pair(140.0, 7.0), Pair(150.0, 6.0),
            Pair(160.0, 4.0), Pair(170.0, 1.0), Pair(180.0, -2.0), Pair(190.0, -6.0),
            Pair(200.0, -10.0), Pair(210.0, -14.0), Pair(220.0, -19.0), Pair(230.0, -24.0),
            Pair(240.0, -29.0), Pair(250.0, -34.0), Pair(260.0, -38.0), Pair(270.0, -42.0),
            Pair(280.0, -46.0), Pair(290.0, -50.0), Pair(300.0,-54.0), Pair(310.0, -57.0),
            Pair(320.0, -60.0), Pair(330.0, -62.0), Pair(340.0, -64.0), Pair(350.0, 0.0),
            Pair(360.0, -65.0), Pair(370.0, -65.0), Pair(380.0, -65.0), Pair(390.0, -64.0),
            Pair(400.0, -64.0), Pair(410.0, -63.0), Pair(420.0, -63.0), Pair(430.0, -62.0),
            Pair(440.0, -62.0), Pair(450.0, -61.0), Pair(460.0, -61.0), Pair(470.0, -60.0),
            Pair(480.0, -60.0), Pair(490.0, -59.0), Pair(500.0, -59.0), Pair(510.0, -58.0),
            Pair(520.0, -58.0), Pair(530.0, -57.0), Pair(540.0, -57.0), Pair(550.0, -56.0),
            Pair(560.0, -56.0), Pair(570.0, -55.0), Pair(580.0, -55.0), Pair(590.0, -54.0),
            Pair(600.0, -54.0), Pair(610.0, -53.0), Pair(620.0, -53.0), Pair(630.0, 52.0),
            Pair(640.0, -52.0), Pair(650.0, -51.0), Pair(660.0, -50.0), Pair(670.0, -50.0),
            Pair(680.0, -49.0), Pair(690.0, -49.0), Pair(700.0, -48.0), Pair(710.0, -48.0),
            Pair(720.0, -47.0), Pair(730.0, -47.0), Pair(740.0, -46.0), Pair(750.0, -46.0),
            Pair(760.0, -45.0), Pair(770.0, -45.0), Pair(780.0, -44.0), Pair(790.0, -44.0),
            Pair(800.0, -43.0), Pair(810.0, -43.0), Pair(820.0, -42.0), Pair(830.0, -42.0),
            Pair(840.0, -41.0), Pair(850.0, -40.0), Pair(860.0, -40.0), Pair(870.0, -39.0),
            Pair(880.0, -39.0), Pair(890.0, -38.0), Pair(900.0, -38.0), Pair(910.0, -37.0),
            Pair(920.0, -37.0), Pair(930.0, -36.0), Pair(940.0, -36.0), Pair(950.0, -35.0),
            Pair(960.0, -35.0), Pair(970.0, -34.0), Pair(980.0, -34.0), Pair(990.0, -33.0),
            Pair(1000.0, -33.0), Pair(1010.0, -32.0), Pair(1020.0, -32.0), Pair(1030.0, -31.0),
            Pair(1040.0, -31.0), Pair(1050.0, -30.0), Pair(1060.0, -30.0), Pair(1070.0, -29.0),
            Pair(1080.0, -29.0), Pair(1090.0, -28.0), Pair(1100.0, -28.0), Pair(1110.0, -27.0),
            Pair(1120.0, -27.0), Pair(1130.0, -26.0), Pair(1140.0, -26.0), Pair(1150.0, -25.0),
            Pair(1160.0, -25.0), Pair(1170.0, -24.0), Pair(1180.0, -24.0), Pair(1190.0, -23.0),
            Pair(1200.0, -23.0), Pair(1210.0, -22.0), Pair(1220.0, -22.0), Pair(1230.0, -21.0),
            Pair(1240.0, -21.0), Pair(1250.0, -20.0), Pair(1260.0, -20.0), Pair(1270.0, -19.0),
            Pair(1280.0, -19.0), Pair(1290.0, -18.0), Pair(1300.0, -18.0), Pair(1310.0, -17.0),
            Pair(1320.0, -17.0), Pair(1330.0, -16.0), Pair(1340.0, -16.0), Pair(1350.0, -15.0),
            Pair(1360.0, -15.0), Pair(1370.0, -14.0), Pair(1380.0, -14.0), Pair(1390.0, -13.0),
            Pair(1400.0, -13.0), Pair(1410.0, -12.0), Pair(1420.0, -12.0))
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
        if (colPos < 0 || colPos >= (tissueViewModel.xSize - 1) || rowPos < 0
            || rowPos >= (tissueViewModel.ySize - 1))
            return

        charge = (0.6 * vm) +
                    (0.05 * upperCell()!!.vm) +
                    (0.05 * lowerCell()!!.vm) +
                    (0.05 * leftCell()!!.vm) +
                    (0.05 * rightCell()!!.vm) +
                    (0.05 * lowerRightCell()!!.vm) +
                    (0.05 * upperRightCell()!!.vm) +
                    (0.05 * upperLeftCell()!!.vm) +
                    (0.05 * lowerLeftCell()!!.vm)
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
            Pair(0.0, -75.0), Pair(10.0, -20.0), Pair(20.0, 25.0), Pair(30.0, 10.0),
            Pair(40.0, 7.0), Pair(50.0, 5.0), Pair(60.0, 5.0), Pair(70.0, 5.0),
            Pair(80.0, 5.0), Pair(90.0, 5.0), Pair(100.0, 5.0), Pair(110.0, 5.0),
            Pair(120.0, 5.0), Pair(130.0, 5.0), Pair(140.0, 5.0), Pair(150.0, 5.0),
            Pair(160.0, 5.0), Pair(170.0, 5.0), Pair(180.0, 5.0), Pair(190.0, 5.0),
            Pair(200.0, 5.0), Pair(210.0, 5.0), Pair(220.0, 4.0), Pair(230.0, 3.0),
            Pair(240.0, 2.0), Pair(250.0, 0.0), Pair(260.0, -2.0), Pair(270.0, -4.0),
            Pair(280.0, -7.0), Pair(290.0, -11.0), Pair(300.0, -16.0), Pair(310.0, -22.0),
            Pair(320.0, -29.0), Pair(330.0, -37.0), Pair(340.0, -45.0), Pair(350.0, -54.0),
            Pair(360.0, -62.0), Pair(370.0, -67.0), Pair(380.0, -71.0), Pair(390.0, -74.0),
            Pair(400.0, -75.0), Pair(1000.0, -75.0))
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
        if (colPos < 1 || colPos >= (tissueViewModel.xSize - 1) || rowPos < 1
            || rowPos >= (tissueViewModel.ySize - 1)) {
            return
        }

        charge = (0.4 * vm) +
                    (0.075 * upperCell()!!.vm) +
                    (0.075 * lowerCell()!!.vm) +
                    (0.075 * leftCell()!!.vm) +
                    (0.075 * rightCell()!!.vm) +
                    (0.075 * lowerRightCell()!!.vm) +
                    (0.075 * upperRightCell()!!.vm) +
                    (0.075 * upperLeftCell()!!.vm) +
                    (0.075 * lowerLeftCell()!!.vm)
    }
    override fun membranePotential() {
        this.vm = alphaVector[step]
    }
    override fun stateColor(): Int = stateColors[state]!!
    override fun clone(): Cell = FastCell(tissueViewModel, colPos, rowPos)
}

