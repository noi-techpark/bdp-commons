select m.* from "measurement" m
join "station" s on s.id = m.station_id
 join "provenance" p on p.id = m.provenance_id
where s.stationtype = 'TrafficSensor'
order by m.created_on desc
limit 10;

