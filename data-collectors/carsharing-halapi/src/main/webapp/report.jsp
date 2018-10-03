<!DOCTYPE html>
<html>
<%
   java.util.HashMap<String, Object> data = (java.util.HashMap<String, Object>) request.getAttribute("data");
   it.bz.tis.integreen.carsharingbzit.ActivityLog[] logs = (it.bz.tis.integreen.carsharingbzit.ActivityLog[]) data.get("logs");
   int errors = 0;
   for (int i = 0; i < logs.length; i++)
   {
      it.bz.tis.integreen.carsharingbzit.ActivityLog log = logs[i];
      if (log.error != null)
         errors++;
   }
%>
<head>
<meta charset="UTF-8">
<title>Activity report!</title>
<style>
table {
	border-collapse: collapse;
	width: 100%;
}

td {
	border: 1px solid black;
	padding: 4px;
}

.ok {
	background-color: #AAFFAA;
}

.error {
	background-color: red;
}
</style>
</head>
<body>
	<h1>Activity report!</h1>
	<p>
		Last 1000 activities [<span class="<%=(errors == 0) ? "ok" : "error"%>"><%=errors%></span>
		errors] from service start at <%=data.get("from")%><br>
	<p>
	<table>
		<tr>
			<td>Timestamp</td>
			<td>Requested time</td>
			<td>Full</td>
			<td>Report</td>
			<td>Duration (seconds)</td>
			<td>Status</td>
		</tr>
		<%
		   for (int i = logs.length - 1; i >= 0; i--)
		   {
		      it.bz.tis.integreen.carsharingbzit.ActivityLog log = logs[i];
		%>
		<tr class="<%=(log.error == null) ? "ok" : "error"%>">
			<td><%=log.timestamp%></td>
			<td><%=log.requesttime%></td>
			<td><%=log.full%></td>
			<td style="white-space: pre"><%=log.report%></td>
			<td style="text-align: right"><%=(log.durationSec < 0) ? "In progress ..." : String.valueOf(log.durationSec)%></td>
			<td><%=(log.error == null) ? ((log.durationSec < 0) ? "" : "OK") : log.error%></td>
		</tr>
		<%
		   }
		%>

	</table>
</body>
</html>