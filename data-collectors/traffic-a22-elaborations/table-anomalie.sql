-- SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
--
-- SPDX-License-Identifier: AGPL-3.0-or-later

CREATE TABLE a22.anomalie
(
    stationcode text,
    "timestamp" integer,
    distance double precision,
    headway double precision,
    length double precision,
    axles integer,
    against_traffic boolean,
    class integer,
    speed double precision,
    direction integer,
    UNIQUE (stationcode, "timestamp", distance, headway, length, axles, against_traffic, class, speed, direction)
);
