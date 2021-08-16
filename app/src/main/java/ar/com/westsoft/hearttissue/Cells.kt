package ar.com.westsoft.hearttissue

enum class ChannelState(val value: Int) {
    RESTING(0), OPEN(1), INACTIVE(2)
}
enum class CellType(val value: Int) {
    MYOCELL(0), AUTOCELL(1), FASTCELL(2), DEADCELL(3)
}

data class Cell (var cellType: CellType = CellType.MYOCELL,
                 var state: ChannelState = ChannelState.RESTING,
                 var vm: Double = -70.0,
                 var charge: Double = -70.0,
                 var step: Int = 700)


