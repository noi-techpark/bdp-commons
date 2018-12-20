<%@ page contentType="text/html; charset=iso-8859-1" language="java"%>


<html>
<head>
  <title>Show session variables</title>
  <META HTTP-EQUIV="Expires" CONTENT="Mon, 06 Jan 1990 00:00:01 GMT">
<%
response.addHeader("Pragma", "No-cache");
response.addHeader("Cache-Control", "no-cache, no-store"); 
response.addDateHeader("Expires", 1);
%>
</head>
<body>

<tt>

<form name="f" method="post">
  <input type="submit" name="BTN_CLEARSESS" value="Clear all session attributes">
</form>

<%!

    public void printRequestInfo(HttpServletRequest request) {
        try {
            System.out.println("Visualizza le variabili del form della request");
            java.util.Enumeration paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement().toString();
                String value = request.getParameter(name).toString();
                System.out.println(name + " = " + value);
            }
        }
        catch (Exception e) {
            System.out.println("EXCEPTION scrivendo debug info in jsp: "+e);
        }
    }
    
    public void printSessionInfo(HttpSession session) {
        try {
            System.out.println("Visualizza le variabili di sessione");
            System.out.println("getCreationTime()     = " + session.getCreationTime());
            System.out.println("getId()               = " + session.getId());
            System.out.println("getLastAccessedTime() = " + session.getLastAccessedTime());
            System.out.println("getServletContext()   = " + session.getServletContext());
            System.out.println("getAttributeNames()   = " + session.getAttributeNames());
            System.out.println("isNew()               = " + session.isNew());
            java.util.Enumeration attrNames = session.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String name  = attrNames.nextElement().toString();
                String value = session.getAttribute(name).toString();
                System.out.println(name + " = " + value);
            }
        }
        catch (Exception e) {
            System.out.println("EXCEPTION scrivendo debug info in jsp: "+e);
        }
    }

%>
<%
    java.util.Date now = new java.util.Date();
    out.println("<p><b>NOW: </b>"+now+"</p>");
    System.out.println("NOW: "+now);

    System.out.println("clearsess = "+request.getParameter("BTN_CLEARSESS"));
    boolean clearsess = request.getParameter("BTN_CLEARSESS")!=null;
    if (clearsess) {
        out.println("<p><b>Clear delle variabili di sessione</b></p>");
        java.util.Enumeration attrNames = session.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String name = attrNames.nextElement().toString();
            session.removeAttribute(name);
            out.println("<b>removed:</b> " + name + "<br>");
        }
    }

    out.println("<p><b>Visualizza le variabili di sessione</b></p>");
    out.println("<b>getCreationTime() = </b>" + session.getCreationTime() + "<br>");
    out.println("<b>getId() = </b>" + session.getId() + "<br>");
    out.println("<b>getLastAccessedTime() = </b>" + session.getLastAccessedTime() + "<br>");
    out.println("<b>getServletContext() = </b>" + session.getServletContext() + "<br>");
    out.println("<b>getAttributeNames() = </b>" + session.getAttributeNames() + "<br>");
    out.println("<b>isNew() = </b>" + session.isNew() + "<br>");

    java.util.Enumeration paramNames = session.getAttributeNames();
    while (paramNames.hasMoreElements()) {
        String name = paramNames.nextElement().toString();
        Object value = session.getAttribute(name);
        out.println("<b>" + name + " = </b>" + value + "<br>");
    }

    out.println("<p><b>Visualizza le variabili di request</b></p>");
    out.println("<b>request.getRequestURL() = </b>" + request.getRequestURL() + "<br>");
    out.println("<b>InetAddress.getLocalHost() = </b>" + java.net.InetAddress.getLocalHost() + "<br>");  
    out.println("<b>INDIRIZZO-IP = </b>"+request.getRemoteAddr() + "<br>");  
    out.println("<b>Method = </b>"      +request.getMethod() + "<br>");  
    out.println("<b>Request URI = </b>" +request.getRequestURI() + "<br>");  
    out.println("<b>Protocol = </b>"    +request.getProtocol() + "<br>");  
    out.println("<b>ContextPath = </b>" +request.getContextPath() + "<br>");  
    out.println("<b>PathInfo = </b>"    +request.getPathInfo() + "<br>");  
    out.println("<b>PathTranslated = </b>" +request.getPathTranslated() + "<br>");  
    out.println("<b>user-agent = </b>"  +request.getHeader("user-agent") + "<br>");  

    out.println("<p><b>Visualizza le variabili del form della request</b></p>");
    paramNames = request.getParameterNames();
    while (paramNames.hasMoreElements()) {
        String name = paramNames.nextElement().toString();
        String value = request.getParameter(name);
        out.println("<b>" + name + " = </b>" + value + "<br>");
    }

    out.println("<p><b>Visualizza le properties del context</b></p>");
    paramNames = application.getAttributeNames();
    while (paramNames.hasMoreElements()) {
        String name = paramNames.nextElement().toString();
        Object value = application.getAttribute(name);
        out.println("<b>" + name + " = </b>" + value + "<br>");
    }

    out.println("<p><b>Visualizza le properties della JVM</b></p>");
    paramNames = System.getProperties().propertyNames();
    while (paramNames.hasMoreElements()) {
        String name = paramNames.nextElement().toString();
        String value = System.getProperty(name);
        out.println("<b>" + name + " = </b>" + value + "<br>");
    }

%>
</tt>

</body>
</html>

