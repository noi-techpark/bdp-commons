select * 
  from a22.a22_station
  where exists (select timestamp from a22.a22_traffic where stationcode = code and timestamp > extract(epoch from (current_timestamp - '1 year'::interval)));
