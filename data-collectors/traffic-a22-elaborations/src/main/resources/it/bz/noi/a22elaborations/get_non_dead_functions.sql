-- SPDX-FileCopyrightText: 2025 NOI Techpark <digital@noi.bz.it>
--
-- SPDX-License-Identifier: AGPL-3.0-or-later

-- DROP FUNCTION a22.get_non_dead_stations();

CREATE OR REPLACE FUNCTION a22.get_non_dead_stations()
 RETURNS SETOF a22_station
 LANGUAGE plpgsql
AS $function$
declare
        cd a22_station;
        mts int;
        cutoff int;
    begin
        cutoff := extract(epoch from (current_timestamp - '1 week'::interval));
        for cd in select * from a22.a22_station loop
            select max(timestamp) into mts from a22.a22_traffic where stationcode = cd.code and timestamp > cutoff;
            if mts is not null and mts > cutoff then
                return next cd;
            end if;
        end loop;
    end;
$function$
;
