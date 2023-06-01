// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

/*
 *  A22 Parking Connector - Demo Application
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2021-05-17  1.0 - chris@1006.org
 */

package it.bz.noi.a22.parking;

import java.io.IOException;
import java.util.ArrayList;

/**
 * command line application to demo the usage of the A22ParkingConnector class
 */
public class Main {

    /**
     * @param args required three command line arguments: A22 web service base URL, username, password
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 3 || isEmpty(args[0]) || isEmpty(args[1]) || isEmpty(args[2])) {
            System.err.println("expected arguments: <A22 web service URL> <username> <password>");
            System.exit(1);
        }

        // step 1
        // create a Connector instance: this will perform authentication and store the session
        //
        // the session will last 24 hours unless de-authenticated before - however, if a user
        // deauthenticates one session, all sessions of the same user will be de-authenticated;
        // this means each running application neeeds their own username

        A22ParkingConnector A22Service = new A22ParkingConnector(args[0], args[1], args[2]);

        // step 2
        // fetch and print info about all car parks ("/parcheggi/anagrafica")

        try {
            ArrayList<A22CarParkInfo> parks = A22Service.getInfo();
            System.out.println("got info about " + parks.size() + " car parks:");
            for (A22CarParkInfo e : parks) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println("step 2 failed, continuing anyway to de-auth...");
        }

        // step 3
        // fetch and print capacity for car parks ("/parcheggi/stato")

        try {
            ArrayList<A22CarParkCapacity> caps = A22Service.getCapacity();
            System.out.println("got capacity for " + caps.size() + " parks:");
            for (A22CarParkCapacity e : caps) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println("step 3 failed, continuing anyway to de-auth...");
        }

        // step 4
        // de-authentication

        A22Service.close();

        System.out.println("'k thx bye");

    }

    private static Boolean isEmpty(String a) {
        return (a == null || a.trim().equals(""));
    }

}
