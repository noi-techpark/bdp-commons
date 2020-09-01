// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package it.bz.odh.spreadsheets.utils;

import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.models.extensions.WorkbookRange;
import com.microsoft.graph.requests.extensions.GraphServiceClient;

/**
 * Graph
 */
public class Graph {

    private static String id = "YOUR_WORKBOOK_ID";

    private static IGraphServiceClient graphClient = null;
    private static SimpleAuthProvider authProvider = null;

    private static void ensureGraphClient(String accessToken) {
        if (graphClient == null) {
            // Create the auth provider
            authProvider = new SimpleAuthProvider(accessToken);

            // Create default logger to only log errors
            DefaultLogger logger = new DefaultLogger();
            logger.setLoggingLevel(LoggerLevel.ERROR);

            // Build a Graph client
            graphClient = GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
        }
    }

    public static User getUser(String accessToken) {
        ensureGraphClient(accessToken);

        // GET /me to get authenticated user
        User me = graphClient
                .me()
                .buildRequest()
                .get();

        return me;
    }

    public static WorkbookRange getWorksheetUsedRange(String accessToken) {
        ensureGraphClient(accessToken);

        WorkbookRange workbookRange = graphClient.me().drive().items(id).workbook().worksheets("Book")
                .usedRange()
                .buildRequest()
                .get();
        return workbookRange;
    }

    public static Drive getDrive(String accessToken) {
        ensureGraphClient(accessToken);

        Drive drive = graphClient.me().drive()
                .buildRequest()
                .get();

        return drive;
    }
}