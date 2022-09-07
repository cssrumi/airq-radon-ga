package pl.airq.ga.radon.domain.model

import java.util.stream.Stream

class TrainingData constructor(
    val sensorId: SensorId,
    val fields: List<String>,
    val predictionConfig: PredictionConfig
) {
    private val rows: MutableList<TrainingDataRow>
    val rowSize: Int = fields.size

    fun addData(row: TrainingDataRow) = rows.add(row)

    fun rows(): List<TrainingDataRow> = rows.toList()

    fun stream(): Stream<TrainingDataRow> = rows.stream()

    fun size(): Long = rows.size.toLong()

    init {
        rows = ArrayList()
    }
}

class TrainingDataRow(val timestamp: Timestamp, val values: FloatArray, val expectedValue: Float)
