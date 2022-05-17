package com.example.sykkelapp.data.source

import com.example.sykkelapp.data.RepositoryInterface
import com.example.sykkelapp.data.Route
import com.example.sykkelapp.data.airquality.AirQualityItem
import com.example.sykkelapp.data.airqualityforecast.Pm10Concentration
import com.example.sykkelapp.data.bysykkel.Station
import com.example.sykkelapp.data.directions.*
import com.example.sykkelapp.data.locationForecast.Data

class FakeRepository (private val datasource: FakeDataSource) : RepositoryInterface {
    override suspend fun loadWeather(lat : String, lon : String, verbose: String) : Data {
        return datasource.loadWeather(lat,lon,verbose)
    }

    override suspend fun loadOsloRoutes() : String {
        return datasource.loadOsloRoutes()
    }

    override suspend fun loadNILUAirQ() : List<AirQualityItem> {
        return datasource.loadNILUAirQ()
    }

    override suspend fun loadAirQualityForecast(lat: String, lon: String) : Pm10Concentration {
        return datasource.loadAirQualityForecast(lat, lon)
    }

    override suspend fun loadBySykkel() : List<Station> {
        return datasource.loadBySykkel()
    }

    override suspend fun loadBySykkelRoutes() : List<Route> {
        return listOf(Route("ved trikkestoppen", end_station_id="425", end_station_latitude=59.932792, end_station_longitude=10.734457, end_station_name="Adamstuen", start_station_description="ved Ullevål Stadion T-bane", start_station_id="575", start_station_latitude=59.94743324225673, start_station_longitude=10.732569703704815, start_station_name="Sognsveien"
            , placeid="ChIJ2asOhPxtQUYRe5FanOcKXOY", air_quality=4.804357051849365,
            airq_unit="ug/m3",
            directions=Route(Bounds(northeast=Northeast(lat=59.94745539999999, lng=10.7354348), southwest=Southwest(lat=59.9322153, lng=10.7296183)),
                copyrights="Map data ©2022", legs=listOf(Leg(distance=Distance(text="1.9 km", value=1890),
                    duration=Duration(text="8 mins", value=504),
                    end_address="Sognsveien, 0854 Oslo, Norway",
                    end_location=EndLocation(lat=59.94745539999999, lng=10.7327349),
                    start_address="Ullevålsveien 82B, 0454 Oslo, Norway",
                    start_location=StartLocation(lat=59.93276919999999, lng=10.7346402),
                    steps=
                    listOf(Step(distance=DistanceX(text="63 m", value=63),
                        duration=DurationX(text="1 min", value=10),
                        end_location=EndLocationX(lat=59.9322153, lng=10.7344727),
                        html_instructions="Head <b>south</b> on <b>Sognsveien</b> toward <b>Ullevålsveien</b>"
                        , maneuver="null", polyline=Polyline(points="yrxlJoro`AJDZLRHTDL?LA"),
                        start_location=StartLocationX(lat=59.93276919999999, lng=10.7346402), travel_mode="BICYCLING"),
                        Step(distance=DistanceX(text="1.8 km", value=1827), duration=DurationX(text="8 mins", value=494),
                            end_location=EndLocationX(lat=59.94745539999999, lng=10.7327349), html_instructions="Make a <b>U-turn</b><div style=font-size:0.9em>Go through 1 roundabout</div><div style=font-size:0.9em>Destination will be on the left</div>",
                    maneuver="uturn-left",
                            polyline=Polyline(points="koxlJmqo`AEMO[aHuCM@KJQP_@h@SZYb@_@j@W`@U`@QZc@n@u@hAm@`Ay@jAGLMJUZMRg@x@U`@CBk@bA]h@SZg@p@QRa@\\a@X]PSHIDE@UH]FMBK@}@H{@JyANSB{@Hc@FWB?AAA?A?AAA?AAAA??AAAA??AA?A??AA?A?A?A@A?A@A??@A@A@?@A@A@?@?@AB?@ABq@DyAN[BE?E@QLe@Bg@DW@S?_@A]EKCYGUKMGOIWQECUQ][GEUSmAiAe@a@e@a@KKe@_@WSo@k@c@]g@[_@Wo@]e@UKEKEo@Ui@Oe@OKCu@Q}@MGCG?E?C?g@@MBG?SD"),
                                start_location=StartLocationX(lat=59.9322153, lng=10.7344727), travel_mode="BICYCLING")),
                        traffic_speed_entry= listOf(), via_waypoint=listOf())),
                overview_polyline=OverviewPolyline(points="yrxlJoro`Af@Rh@NZAUi@aHuCM@]\\mBtCcBnCeDdFc@f@u@lAcBrC{@lAs@p@_Aj@]N[Jk@J_Ff@kCXAGIIICIFEJADABq@DuBRK@QLe@B_AFs@Ai@Io@S]QqAcAqCeCoBaBsAiAgAs@mB_AkCy@cCc@_AD[D"),
            summary="Sognsveien",
            warnings=listOf("Bicycling directions are in beta. Use caution – This route may contain streets that aren't suited for bicycling."), waypoint_order= listOf()),
            popularity=10,
            difficulty=3.75,
            bookmarked=false))
    }

    override suspend fun averageAirQuality(
        latStart: Double,
        lonStart: Double,
        latEnd: Double,
        longEnd: Double
    ): Double {
        return 10.0
    }
}