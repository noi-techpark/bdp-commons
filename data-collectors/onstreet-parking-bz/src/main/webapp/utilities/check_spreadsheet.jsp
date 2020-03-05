<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="it.bz.idm.bdp.dto.StationDto"%>
<%@page import="it.bz.idm.bdp.dconstreetparkingbz.DCUtils"%>
<%@page import="it.bz.idm.bdp.dto.StationList"%>
<%@page import="it.bz.idm.bdp.dconstreetparkingbz.OnstreetParkingBzDataRetriever"%>
<%@ page language="java" import="java.sql.*"  %>
<%@ page import="org.apache.log4j.*"%>
<%@ page import="org.apache.log4j.spi.LoggerFactory"%>

<HTML>
<HEAD>
  <TITLE>CHECK SPREADSHEET</TITLE>
  <META HTTP-EQUIV="Expires" CONTENT="Mon, 06 Jan 1990 00:00:01 GMT">
<%
response.addHeader("Pragma", "No-cache");
response.addHeader("Cache-Control", "no-cache, no-store"); 
response.addDateHeader("Expires", 1);
%>
<style>
table {
  font-family: Monospace, "Courier new", sans-serif;
  font-size: 10pt;
  border-collapse: collapse;
  width: 100%;
}

td, th {
  border: 1px solid #ddd;
  padding: 2px;
}

tr:nth-child(even){background-color: #f2f2f2;}

tr:hover {background-color: #ddd;}

th {
  padding-top: 4px;
  padding-bottom: 4px;
  text-align: left;
  background-color: navy;
  color: white;
}
</style>
</HEAD>
<BODY>

<table>

<%
try {
    //Use Spring WebApplicationContext to create a bean annotated with @Component
    //This does the same as @Inject or @Autowired, Spring instantiates all needed dependencies
    WebApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(application);
    OnstreetParkingBzDataRetriever reader = ac.getBean(OnstreetParkingBzDataRetriever.class);

    StationList stationList = reader.fetchStations();

    //Check there is at least one item in the list
    if ( stationList == null || stationList.size() <= 0 ) {
        out.println("StationList IS "+stationList+"!!!");
    } else {
        int i = 1;
        out.println("<tr><th>row</th>"+"<th>id</th>"+"<th>stationType</th>"+"<th>name</th>"+"<th>Latitude</th>"+"<th>Longitude</th>"+"<th>elevation</th>"+"<th>origin</th>"+"<th>parentStation</th></tr>");
        for (StationDto s : stationList) {
            out.println("<tr><td>"+i+"</td><td>"+s.getId()+"</td><td>"+s.getStationType()+"</td><td>"+s.getName()+"</td><td>"+s.getLatitude()+"</td><td>"+s.getLongitude()+"</td><td>"+s.getElevation()+"</td><td>"+s.getOrigin()+"</td><td>"+s.getParentStation()+"</td></tr>");
            i++;
        }
    }

} catch (Exception e) {
    String msg = "Exception in testFetchData: " + e;
    out.println(msg);
}

%>

</table>

</BODY>
</HTML>

