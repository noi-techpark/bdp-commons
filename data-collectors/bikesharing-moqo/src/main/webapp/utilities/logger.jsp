<%@page import="org.apache.logging.log4j.core.config.Configurator"%>
<%@ page language="java" import="java.sql.*"  %>
<%@ page import="org.apache.logging.log4j.*"%>

<%!
public Level getCurrentLevel(String loggerName) {
    Logger logger = "ROOT".equals(loggerName) ? LoggerFactory.getRootLogger() : LoggerFactory.getLogger(loggerName);
    Level loggerLevel = null;
    if ( logger!=null ) {
        loggerLevel = logger.getLevel();
    }
    return loggerLevel;
}
%>

<%
StringBuffer msg = new StringBuffer();
try {
    boolean isChangeLog = "CHANGE LOG LEVEL".equals(request.getParameter("CNG_LOG"));
    if ( isChangeLog ) {
        String loggerName = request.getParameter("LOGGER_NAME");
        String levelName  = request.getParameter("LOGGER_LEVEL");
        Level level = Level.toLevel(levelName);
        if ( loggerName==null || loggerName.trim().equals("") ) {
            loggerName = request.getParameter("LOGGER_NAME_INSERTED");
        }
        Logger logger = "ROOT".equals(loggerName) ? LoggerFactory.getRootLogger() : LoggerFactory.getLogger(loggerName);
        Configurator.setRootLevel(level);
        msg.append("Logger '"+loggerName+"' set to '"+level+"'");
    }
}
catch (Exception e) {
    msg.append("<p>Errore:<br>" + e.getMessage());
}
%>

<HTML>
  <HEAD>
    <TITLE>CHANGE LOGGER LEVEL</TITLE>
  </HEAD>
  <BODY>

<form action="" method="post">
<table border="0" cellpadding="2" cellspacing="0">
<tr>
  <td>Logger:</td>
  <td>
    <select name="LOGGER_NAME">
      <option value=""></option>
<%
String[] names = new String[] {
         "it.bz.idm.bdp.bikesharingmoqo"
        ,"it.bz.idm.bdp.reader"
        ,"it.bz.idm.bdp.writer"
        ,"it.bz.idm.bdp"
        ,"org.springframework.http.client"
        ,"org.springframework"
        ,"ROOT"
};
for ( int i=0 ; i<names.length ; i++ ) {
    out.println("<option value='"+names[i]+"'>"+names[i]+" ("+getCurrentLevel(names[i])+")</option>");
}
%>
    </select>
    <input type="text" name="LOGGER_NAME_INSERTED" size="50">
  </td>
</tr>
<tr>
  <td>Level:</td>
  <td>
    <select name="LOGGER_LEVEL">
      <option value=""></option>
      <option value="<%=Level.OFF  %>">Level.OFF  </option>
      <option value="<%=Level.FATAL%>">Level.FATAL</option>
      <option value="<%=Level.ERROR%>">Level.ERROR</option>
      <option value="<%=Level.WARN %>">Level.WARN </option>
      <option value="<%=Level.INFO %>">Level.INFO </option>
      <option value="<%=Level.DEBUG%>">Level.DEBUG</option>
      <option value="<%=Level.TRACE%>">Level.TRACE</option>
      <option value="<%=Level.ALL  %>">Level.ALL  </option>
    </select>
  </td>
</tr>
<tr>
  <td>&nbsp;</td>
  <td><input type="submit" name="CNG_LOG" value="CHANGE LOG LEVEL"></td>
</tr>
</table>

</form>

<p><%=msg%></p>

</BODY>
</HTML>
