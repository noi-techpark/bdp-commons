-- SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
--
-- SPDX-License-Identifier: AGPL-3.0-or-later

INSERT INTO a22.ANOMALIE (stationcode, timestamp, distance, headway, length, axles, against_traffic, class, speed, direction)
            values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) 
             ON CONFLICT (stationcode, timestamp, distance, headway, length, axles, against_traffic, class, speed, direction)
             DO NOTHING;
