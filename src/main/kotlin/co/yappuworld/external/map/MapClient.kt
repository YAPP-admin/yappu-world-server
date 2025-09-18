package co.yappuworld.external.map

import co.yappuworld.external.map.dto.AddressCoordinate

interface MapClient {

    fun convertAddressToCoordinates(address: String): AddressCoordinate
}
