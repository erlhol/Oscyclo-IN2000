package com.example.sykkelapp


class DataSource {

    suspend fun getPaths() {
        val url = "https://geoserver.data.oslo.systems/geoserver/bym/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=bym%3Abyruter&outputFormat=application/json"
    }
}