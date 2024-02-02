package com.dst.testapp

data class EventData(
    val eventDate: Int,
    val eventTimeLocal: Int,
    val eventCode: Int,
    val eventServiceProvider: Int,
    val eventRouteNumber: Int,
    val eventVehicleId: Int,
    val eventVehicleClass: Int,
    val b: Int,
    val eventContractPointer: Int,
    val c: Int,
    val eventAuthenticator: Int,
    val eventFirstStampDate: Int,
    val eventFirstStampTimeLocal: Int,
    val eventDataSimulation: Int,
    val eventDataRouteDirection: Int
)

fun parseEventData(input: String): EventData {
    val values = input
        .replace("0x", "")
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.split("=") }
        .associate { (key, value) -> key.trim() to value.trim().toInt(16) }

    return EventData(
        values["EventDate"] ?: 0,
        values["EventTimeLocal"] ?: 0,
        values["EventCode"] ?: 0,
        values["EventServiceProvider"] ?: 0,
        values["EventRouteNumber"] ?: 0,
        values["EventVehicleId"] ?: 0,
        values["EventVehiculeClass"] ?: 0,
        values["B"] ?: 0,
        values["EventContractPointer"] ?: 0,
        values["C"] ?: 0,
        values["EventAuthenticator"] ?: 0,
        values["EventFirstStampDate"] ?: 0,
        values["EventFirstStampTimeLocal"] ?: 0,
        values["EventDataSimulation"] ?: 0,
        values["EventDataRouteDirection"] ?: 0
    )
}
