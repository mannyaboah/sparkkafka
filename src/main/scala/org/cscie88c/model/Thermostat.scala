package org.cscie88c.model

final case class Thermostat(
    indoorTemp: Int,
    outdoorTemp: Int,
    indoorHumid: Int,
    outdoorHumid: Int,
    datetime: String
)
