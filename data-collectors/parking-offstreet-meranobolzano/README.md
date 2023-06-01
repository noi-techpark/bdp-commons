<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# Data Collector: Parking Merano/Bolzano

This data collector is a module of the [Open Data Hub](https://opendatahub.bz.it)
Mobility Project.

It harvests the following data:
1) Parking slot data from Merano
2) Parking slot data from FAMAS
3) Parking forecast data

[![CI/CD parking-offstreet-meranobolzano](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-parking-offstreet-meranobolzano.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-parking-offstreet-meranobolzano.yml)

## Setup

The Spring Component `JobScheduler.java` contains all scheduled methods configured by
`/src/main/resources/META-INF/spring/applicationContext.xml`.

Configure all settings inside `src/main/resources/META-INF/spring/app.properties`.


## Contributing

Please, read the general [documentation](https://opendatahub.readthedocs.io/en/latest/index.html)
for the Open Data Hub, and the [resources for developers](https://opendatahub.readthedocs.io/en/latest/guidelines.html).

Java entry points to for the three moduls:
1) `ParkingMeranoClient.java`: parking slot data from Merano
2) `ParkingClient.java`: parking slot data from FAMAS
3) `PredictionRetriever.java`: parking forecast data


## Testing

### Unit tests
Run unit tests within `src/test/java/it/bz/idm/bdp/`

### Database tests

*NB: This is only relevant for local testers, or Open Data Hub maintainers.*

Search for stations:
```sql
select *
from station s
where stationtype = 'ParkingStation'
and origin in ('FAMAS', 'Municipality Merano')
```

Search for measurements and predictions:
```sql
select m.created_on, m.timestamp, t.*, double_value, s.*
from station s
join measurement m on s.id = m.station_id
join type t on t.id = m.type_id
where stationtype = 'ParkingStation'
and origin in ('FAMAS', 'Municipality Merano')
and active = true
and m.timestamp > now() - interval '10 days'
order by m.timestamp
limit 30
```

Check `timestamps` and `created_on`, if inserted data has been created recently.
