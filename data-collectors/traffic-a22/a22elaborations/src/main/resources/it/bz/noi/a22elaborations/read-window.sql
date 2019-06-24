WITH t AS (
     SELECT a22_traffic.*,
            ( WITH t2 AS (
                         SELECT c2.speed
                           FROM a22.a22_traffic c2
                          WHERE c2."timestamp" <= a22_traffic."timestamp" AND c2."timestamp" >= (a22_traffic."timestamp" - 3600) AND c2.stationcode = a22_traffic.stationcode
                          ORDER BY c2."timestamp" DESC
                          LIMIT 50
                        )
                 SELECT avg(t2.speed) AS avg
                   FROM t2
            ) AS vmed_50
           FROM a22.a22_traffic as a22_traffic
          WHERE "timestamp" >= ? AND "timestamp" < ?
            and stationcode = ?
)
, 
t2 AS (
         SELECT t.*,
            t.class = 1 AND t.length >= 100::double precision AND t.length < 300::double precision AND t.speed < 190::double precision AND (t.speed > 70::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) AS classe_1_avg,
            t.length < 300::double precision AND t.speed < 190::double precision AND t.speed >= 0::double precision AND t.class = 1 AS classe_1_count,
            t.class = 2 AND t.length >= 300::double precision AND t.length < 700::double precision AND t.speed < 190::double precision AND (t.speed > 70::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) OR t.class = 1 AND t.length >= 300::double precision AND t.length < 700::double precision AND t.speed < 190::double precision AND (t.speed > 70::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) OR t.class = 3 AND t.length >= 700::double precision AND t.length < 1200::double precision AND t.speed > 140::double precision AND (t.speed > 60::double precision AND t.vmed_50 >= 40::double precision OR t.vmed_50 IS NULL) AS classe_2_avg,
            t.length < 700::double precision AND t.speed < 190::double precision AND t.speed >= 0::double precision AND t.class = 2 OR t.class = 1 AND t.length >= 300::double precision AND t.length < 700::double precision AND t.speed < 190::double precision AND t.speed >= 0::double precision OR t.length < 1200::double precision AND t.speed >= 140::double precision AND t.speed >= 0::double precision AND t.class = 3 AS classe_2_count,
            t.class = 3 AND t.length >= 700::double precision AND t.length < 1200::double precision AND t.speed < 140::double precision AND (t.speed > 60::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) OR t.class = 2 AND t.length >= 700::double precision AND t.length < 1200::double precision AND t.speed < 140::double precision AND (t.speed > 60::double precision AND t.vmed_50 >= 40::double precision OR t.vmed_50 IS NULL) AS classe_3_avg,
            t.length < 1200::double precision AND t.speed < 140::double precision AND t.speed >= 0::double precision AND t.class = 3 OR t.class = 2 AND t.length >= 700::double precision AND t.length < 1200::double precision AND t.speed < 140::double precision AND t.speed >= 0::double precision AS classe_3_count,
            t.class = 4 AND t.length >= 400::double precision AND t.length < 900::double precision AND t.speed < 160::double precision AND (t.speed > 70::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) OR t.class = 5 AND t.speed >= 140::double precision AND t.speed < 160::double precision AND t.length >= 500::double precision AND t.length < 900::double precision AND (t.speed > 70::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) AS classe_4_avg,
            t.speed < 160::double precision AND t.speed >= 0::double precision AND t.class = 4 OR t.length >= 500::double precision AND t.length < 900::double precision AND t.speed >= 140::double precision AND t.speed < 160::double precision AND t.class = 5 AS classe_4_count,
            t.class = 5 AND t.length >= 500::double precision AND t.length < 900::double precision AND t.speed < 140::double precision AND (t.speed > 60::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) OR t.class = 6 AND t.length >= 800::double precision AND t.length < 1800::double precision AND t.speed >= 120::double precision AND t.speed < 140::double precision AND (t.speed > 60::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) OR t.class = 4 AND t.length >= 900::double precision AND t.speed < 140::double precision AND (t.speed > 60::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) AS classe_5_avg,
            t.length < 900::double precision AND t.speed < 140::double precision AND t.speed >= 0::double precision AND t.class = 5 OR t.length >= 800::double precision AND t.length < 1800::double precision AND t.speed >= 120::double precision AND t.speed < 140::double precision AND t.class = 6 OR t.length >= 900::double precision AND t.speed < 140::double precision AND t.speed >= 0::double precision AND t.class = 4 AS classe_5_count,
            t.class = 6 AND t.length >= 800::double precision AND t.length < 1800::double precision AND t.speed < 120::double precision AND (t.speed > 50::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) OR t.class = 5 AND t.length >= 900::double precision AND t.length < 1800::double precision AND t.speed < 120::double precision AND (t.speed > 50::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) AS classe_6_avg,
            t.length < 1800::double precision AND t.speed < 120::double precision AND t.speed >= 0::double precision AND t.class = 6 OR t.length >= 900::double precision AND t.length < 1800::double precision AND t.speed < 140::double precision AND t.speed >= 0::double precision AND t.class = 5 AS classe_6_count,
            t.class = 7 AND t.length >= 1000::double precision AND t.length < 2500::double precision AND t.speed < 120::double precision AND (t.speed > 50::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) OR t.class = 6 AND t.length >= 1800::double precision AND t.length < 2500::double precision AND t.speed < 120::double precision AND (t.speed > 50::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) AS classe_7_avg,
            t.length < 2500::double precision AND t.speed < 120::double precision AND t.speed >= 0::double precision AND t.class = 7 OR t.length >= 1800::double precision AND t.length < 2500::double precision AND t.speed < 120::double precision AND t.speed >= 0::double precision AND t.class = 6 OR t.length >= 1800::double precision AND t.length < 2500::double precision AND t.speed < 150::double precision AND t.speed >= 0::double precision AND t.class = 9 AS classe_7_count,
            t.class = 8 AND t.length >= 1000::double precision AND t.length < 2500::double precision AND t.speed < 110::double precision AND (t.speed > 50::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) AS classe_8_avg,
            t.length < 2500::double precision AND t.speed < 110::double precision AND t.speed >= 0::double precision AND t.class = 8 AS classe_8_count,
            t.class = 9 AND t.length >= 900::double precision AND t.length < 1800::double precision AND t.speed < 150::double precision AND (t.speed > 60::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) OR t.class = 7 AND t.length >= 900::double precision AND t.length < 1800::double precision AND t.speed >= 120::double precision AND t.speed < 150::double precision AND (t.speed > 50::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) OR t.class = 8 AND t.speed >= 110::double precision AND t.speed < 150::double precision AND t.length >= 900::double precision AND t.length < 1800::double precision AND t.speed < 150::double precision AND (t.speed > 60::double precision AND t.vmed_50 >= 40::double precision OR t.speed > 0::double precision AND t.vmed_50 < 40::double precision OR t.vmed_50 IS NULL) AS classe_9_avg,
            t.length < 1800::double precision AND t.speed < 150::double precision AND t.speed >= 0::double precision AND t.class = 9 OR t.length < 1800::double precision AND t.speed >= 120::double precision AND t.speed < 150::double precision AND t.class = 7 OR t.length < 2500::double precision AND t.speed >= 110::double precision AND t.speed < 150::double precision AND t.class = 8 AS classe_9_count
          FROM t
)
,
t3 AS (
         SELECT t2.*,
            ( SELECT count(*) AS count
                FROM unnest(ARRAY[t2.classe_1_count, t2.classe_2_count, t2.classe_3_count, t2.classe_4_count, t2.classe_5_count, t2.classe_6_count, t2.classe_7_count, t2.classe_8_count, t2.classe_9_count]) classe(classe)
               WHERE classe.classe = true) AS nr_classes_count,
            ( SELECT count(*) AS count
                FROM unnest(ARRAY[t2.classe_1_avg, t2.classe_2_avg, t2.classe_3_avg, t2.classe_4_avg, t2.classe_5_avg, t2.classe_6_avg, t2.classe_7_avg, t2.classe_8_avg, t2.classe_9_avg]) classe_avg(classe_avg)
               WHERE classe_avg.classe_avg = true) AS nr_classes_avg
           FROM t2
        )
 SELECT t3.*
--          CASE
--            WHEN t3.classe_1_count = true AND t3.nr_classes_count = 1 THEN 1
--            WHEN t3.classe_2_count = true AND t3.nr_classes_count = 1 THEN 2
--            WHEN t3.classe_3_count = true AND t3.nr_classes_count = 1 THEN 3
--            WHEN t3.classe_4_count = true AND t3.nr_classes_count = 1 THEN 4
--            WHEN t3.classe_5_count = true AND t3.nr_classes_count = 1 THEN 5
--            WHEN t3.classe_6_count = true AND t3.nr_classes_count = 1 THEN 6
--            WHEN t3.classe_7_count = true AND t3.nr_classes_count = 1 THEN 7
--            WHEN t3.classe_8_count = true AND t3.nr_classes_count = 1 THEN 8
--            WHEN t3.classe_9_count = true AND t3.nr_classes_count = 1 THEN 9
--            ELSE NULL::integer
--        END AS class_count,
--        CASE
--            WHEN t3.classe_1_avg = true AND t3.nr_classes_avg = 1 THEN 1
--            WHEN t3.classe_2_avg = true AND t3.nr_classes_avg = 1 THEN 2
--            WHEN t3.classe_3_avg = true AND t3.nr_classes_avg = 1 THEN 3
--            WHEN t3.classe_4_avg = true AND t3.nr_classes_avg = 1 THEN 4
--            WHEN t3.classe_5_avg = true AND t3.nr_classes_avg = 1 THEN 5
--            WHEN t3.classe_6_avg = true AND t3.nr_classes_avg = 1 THEN 6
--            WHEN t3.classe_7_avg = true AND t3.nr_classes_avg = 1 THEN 7
--            WHEN t3.classe_8_avg = true AND t3.nr_classes_avg = 1 THEN 8
--            WHEN t3.classe_9_avg = true AND t3.nr_classes_avg = 1 THEN 9
--            ELSE NULL::integer
--        END AS class_avg
   FROM t3;
