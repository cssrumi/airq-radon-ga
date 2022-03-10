package pl.airq.ga.radon.domain.model

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

data class Timestamp(val seconds: Long) : Comparable<Timestamp> {
    private val instant by lazy { Instant.ofEpochSecond(seconds) }

    fun toInstant(): Instant = instant
    fun toOffsetDateTime(): OffsetDateTime = OffsetDateTime.ofInstant(toInstant(), ZoneOffset.UTC)
    fun toMillis(): Long = seconds * 1000

    companion object Factory {
        fun from(offsetDateTime: OffsetDateTime): Timestamp {
            return Timestamp(offsetDateTime.toEpochSecond())
        }
        fun from(instant: Instant): Timestamp {
            return Timestamp(instant.epochSecond)
        }

        fun now(): Timestamp {
            return from(Instant.now())
        }
    }

    override fun compareTo(other: Timestamp): Int = seconds.compareTo(other.seconds)
}
