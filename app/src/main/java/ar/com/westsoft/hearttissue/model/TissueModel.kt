package ar.com.westsoft.hearttissue.model

data class TissueModel (
    val colCount: Int,
    val rowCount: Int,
    val colors: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TissueModel) return false

        if (colCount != other.colCount) return false
        if (rowCount != other.rowCount) return false
        if (!colors.contentEquals(other.colors)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colCount
        result = 31 * result + rowCount
        result = 31 * result + colors.contentHashCode()
        return result
    }
}