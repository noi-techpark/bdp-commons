bdp-api
=================

This is the *codebase for each internal developed webservice* exposing the big data platform API. This module contains the API of the bdp, means it exposes data as json which it get's from the reader (https://github.com/idm-suedtirol/bdp-core/tree/master/reader). Which also means that alone this module gives you nothing.
For **each typology **of data we create **a specific module/webservice** which exposes the data which is meant to be shown.
e.g. for all data regarding Parking slots we deploy a module which only exposes data relative to the parking domain like capacity or current occupancy of a parking slots.

Each typology of data is **identified by the stationtype**, which can also have child stations. It only goes down **one level**, which means a child station can not have childs on it's own. Like for example a carsharingstation(the place where you take the car) has multiple children which are the single cars(CarsharingCar)

## Configuration
For it to work you just need to fill out the configuration file you find unser `src/main/resources/META-INF/spring/application.properties`
There you basically do 2 things:
1. Point the module to the reader which was setup previously

	[Default]
		host=localhost
		port=8080
		ssl=false
		endpoint=/reader
2. Define your stationtype and childstationtype and define under which path your children-station will be provided
		bdp.stationtype=EChargingStation
		bdp.childstationtype=EChargingPlug
		bdp.childrenpath=hello
	If no  children exist for specific station just leave the 2 child fields empty

Each module(and also each stationtype) will have the same api-structure. If you go to the root site of your app you will find swagger documentation about the API. Generally all api calls will be available under

		[app]/rest/

 and each all api calls for the child stations(which are the same api calls) will be available under

 		[app]/rest/${bdp.childrenpath}/

The available stationtypes and childstationtypes are:

| Stationtype   	|      Childstationtype | Childstationpath
|----------	|:-------------:
| BikesharingStation | Bicycle | bikes
| Bluetoothstation | / | /
| Carpoolinghub | / | /
| Carpoolingservice | / | /
| CarpoolingUser | / | /
| Carsharingstation | Carsharingcar | cars
| EChargingStation | EChargingPlug | plugs
| Environmentstation | / | /
| Linkstation | / | /
| Meteostation | / | /
| Mobilestation | / | /
| ParkingStation | / | /
| RWISstation | / | /
| Streetstation | / | /
| TrafficSensor | / | /
| Trafficstation | / | /

TODO: Further development will provide this mapping through the reader API which should make it dynamic and maintainable

Building & Deployment
==========================

Once you finished the configuration steps the building should be handled by maven easily. Just go to the root directory and:

		mvn clean test package

You will find your war artifact in the target director and you can deploy it to an application server of your choice.

In case you run into trouble or you need further support, please contact info@opendatahub.bz.it
