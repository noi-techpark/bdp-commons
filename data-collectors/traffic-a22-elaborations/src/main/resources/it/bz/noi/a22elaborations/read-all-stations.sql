-- SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
--
-- SPDX-License-Identifier: AGPL-3.0-or-later

select code, name, geo,
       (select data
          from a22.a22_station_detail
         where a22_station_detail.code = t.code
       ) as metadata
  from a22.get_non_dead_stations() as t;
