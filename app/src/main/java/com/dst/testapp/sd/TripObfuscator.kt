package com.dst.testapp.sd

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import java.util.Random


private var mCalendarMapping = (0..365).shuffled()

private var mRandomSource: kotlin.random.Random = kotlin.random.Random.Default

object TripObfuscator {
    private const val TAG = "TripObfuscator"

    var randomSource: kotlin.random.Random
        get() = mRandomSource
        @VisibleForTesting
        set(random) {
            mRandomSource = random
            mCalendarMapping = (0..365).shuffled(random)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obfuscateDaystamp(input: Daystamp): Daystamp {
        var year = input.year
        var dayOfYear = input.dayOfYear
        if (dayOfYear < mCalendarMapping.size) {
            dayOfYear = mCalendarMapping[dayOfYear]
        } else {
            // Shouldn't happen...
            Log.w(TAG, "Oops, got out of range day-of-year ($dayOfYear)")
        }

        val today = TimestampFull.now().toDaystamp()

        val ret = Daystamp.fromDayOfYear(year, dayOfYear)

        // Adjust for the time of year
        if (ret > today) {
            year--
        }

        return Daystamp.fromDayOfYear(year, dayOfYear)
    }

    /**
     * Maybe obfuscates a timestamp
     *
     * @param input          Calendar representing the time to obfuscate
     * @param obfuscateDates true if dates should be obfuscated
     * @param obfuscateTimes true if times should be obfuscated
     * @return maybe obfuscated value
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun maybeObfuscateTSFull(input: TimestampFull, obfuscateDates: Boolean, obfuscateTimes: Boolean): TimestampFull {
        if (!obfuscateDates && !obfuscateTimes) {
            return input
        }

        // Clone the input before we start messing with it.
        val daystamp = maybeObfuscateTSDay(
            input.toDaystamp(), obfuscateDates)

        if (!obfuscateTimes)
            return daystamp.promote(input.tz, input.hour, input.minute, input.second)

        // Reduce resolution of timestamps to 5 minutes.
        val minute = (input.minute + 2) / 5 * 5

        // Add a deviation of up to 350 minutes (5.5 hours) earlier or later.
        val off = mRandomSource.nextInt(700) - 350

        return daystamp.promote(input.tz, input.hour, minute) + Duration.mins(off)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun maybeObfuscateTSDay(input: Daystamp, obfuscateDates: Boolean): Daystamp {
        if (!obfuscateDates) {
            return input
        }

        return obfuscateDaystamp(input)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun maybeObfuscateTS(input: TimestampFull): TimestampFull =
            maybeObfuscateTSFull(input, Preferences.obfuscateTripDates,
                Preferences.obfuscateTripTimes)

    @RequiresApi(Build.VERSION_CODES.O)
    fun maybeObfuscateTS(input: Daystamp): Daystamp =
            maybeObfuscateTSDay(input, Preferences.obfuscateTripDates)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obfuscateTrip(trip: Trip, obfuscateDates: Boolean, obfuscateTimes: Boolean, obfuscateFares: Boolean): ObfuscatedTrip {
        val start = trip.startTimestamp
        val timeDelta: Long = when (start) {
            null -> 0
            is TimestampFull -> maybeObfuscateTSFull(start, obfuscateDates, obfuscateTimes).timeInMillis - start.timeInMillis
            is Daystamp -> 86400L * 1000L * (maybeObfuscateTSDay(start, obfuscateDates).daysSinceEpoch - start.daysSinceEpoch).toLong()
        }

        return ObfuscatedTrip(trip, timeDelta, obfuscateFares)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obfuscateTrips(trips: List<Trip>, obfuscateDates: Boolean, obfuscateTimes: Boolean, obfuscateFares: Boolean): List<ObfuscatedTrip> =
            trips.map { obfuscateTrip(it, obfuscateDates, obfuscateTimes, obfuscateFares) }
}