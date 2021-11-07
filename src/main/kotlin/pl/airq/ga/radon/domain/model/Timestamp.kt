package pl.airq.ga.radon.domain.model

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

data class Timestamp(val value: Long) : Comparable<Timestamp> {
    private val instant by lazy { Instant.ofEpochSecond(value) }

    fun toInstant(): Instant = instant
    fun toOffsetDateTime(): OffsetDateTime = OffsetDateTime.ofInstant(toInstant(), ZoneOffset.UTC)

    companion object Factory {
        fun from(offsetDateTime: OffsetDateTime): Timestamp {
            return Timestamp(offsetDateTime.toEpochSecond())
        }
        fun from(instant: Instant): Timestamp {
            return Timestamp(instant.epochSecond)
        }
    }

    override fun compareTo(other: Timestamp): Int = value.compareTo(other.value)
}
