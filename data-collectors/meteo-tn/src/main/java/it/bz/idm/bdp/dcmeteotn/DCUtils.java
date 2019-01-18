package it.bz.idm.bdp.dcmeteotn;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DCUtils {

    private static final Logger LOG = LogManager.getLogger(DCUtils.class.getName());

    public static final String TRUE_VALUE = "1";
    public static final String FALSE_VALUE = "0";

    public static boolean isEmpty(String s) {
        return (s == null || s.trim().length() == 0);
    }

    public static boolean isNullAsString(Object o) {
        return (o == null || o.toString().trim().length() == 0 || o.toString().trim().equalsIgnoreCase("null"));
    }

    public static String allowNulls(Object o) {
        return o != null ? o.toString() : "";
    }

    public static String defaultNulls(Object o, String s) {
        return (paramNull(o) ? s : o.toString());
    }

    public static boolean paramNotNull(Object param) {
        boolean ok = true;
        if ( param == null ) {
            ok = false;
        } else if (param instanceof String) {
            ok = !param.toString().trim().equals("");
        } else if (param instanceof StringBuffer) {
            ok = !param.toString().trim().equals("");
        }
        return ok;
    }

    public static boolean paramNull(Object param) {
        boolean notNull = paramNotNull(param);
        return !notNull;
    }

    public static Object checkEmpty(Object obj, Object defaultValue) {
        return paramNotNull(obj) ? obj : defaultValue;
    }

    public static String mustEndWithSlash(String s) {
        if (s!=null && !s.endsWith("/")) {
            s += "/";
        }
        return s;
    }

    public static String mustNotEndWithSlash(String s) {
        if (s!=null && s.endsWith("/") ) {
            s = s.substring(0, s.length()-1);
        }
        return s;
    }

    public static Double convertLongToDouble(Long inval) {
        Double retval = null;
        try {
            if ( inval!=null ) {
                retval = Double.valueOf(inval.doubleValue());
            }
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Long convertDoubleToLong(Double inval) {
        Long retval = null;
        try {
            if ( inval!=null ) {
                retval = Long.valueOf(inval.longValue());
            }
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Integer convertDoubleToInteger(Double inval) {
        Integer retval = null;
        try {
            if ( inval!=null ) {
                retval = Integer.valueOf(inval.intValue());
            }
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Long convertStringToLong(String inval) {
        Long retval = null;
        try {
            if ( inval!=null ) {
                retval = Long.valueOf(inval);
            }
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Integer convertStringToInteger(String inval) {
        Integer retval = null;
        try {
            if ( inval!=null ) {
                retval = Integer.valueOf(inval);
            }
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Double convertStringToDouble(String inval) {
        Double retval = null;
        try {
            if ( inval!=null ) {
                retval = Double.valueOf(inval);
            }
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Boolean convertStringToBoolean(String inval) {
        Boolean retval = null;
        try {
            retval = 
                    (TRUE_VALUE.equals(inval)  || "true".equalsIgnoreCase(inval))  ? Boolean.TRUE :
                    (FALSE_VALUE.equals(inval) || "false".equalsIgnoreCase(inval)) ? Boolean.FALSE :
                    null;
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Boolean convertLongToBoolean(Long inval) {
        String strRetval = convertObjectToString(inval);
        Boolean retval = convertStringToBoolean(strRetval);
        return retval;
    }

    public static String convertBooleanToString(Boolean inval) {
        String retval = null;
        try {
            retval = Boolean.TRUE.equals(inval) ? TRUE_VALUE : FALSE_VALUE;
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Long convertBooleanToLong(Boolean inval) {
        String strRetval = convertBooleanToString(inval);
        Long retval = convertStringToLong(strRetval);
        return retval;
    }

    public static String convertDateToString(Date inval) {
        String retval = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            retval = formatter.format(inval);
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Date convertStringToDate(String inval) {
        Date retval = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            retval = formatter.parse(inval);
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Date convertStringTimezoneToDate(String inval) {
        Date retval = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
            retval = sdf.parse(inval);
        } catch (Exception e1) {
            LOG.debug("Exception parsing date "+inval+": "+e1);
            try {
                OffsetDateTime odt = OffsetDateTime.parse(inval);
                retval = new Date(odt.toEpochSecond());
            } catch (Exception e2) {
                LOG.debug("Exception parsing date "+inval+": "+e2);
                try {
                    inval = inval.substring(0, inval.indexOf("+"));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    retval = sdf.parse(inval);
                } catch (Exception e3) {
                    LOG.error("Exception parsing date "+inval+": "+e3);
                }
            }
        }
        return retval;
    }

    public static String convertObjectToString(Object inval) {
        String retval = null;
        try {
            if ( inval!=null ) {
                retval = String.valueOf(inval);
            }
        } catch (Exception ex) {
        }
        return retval;
    }

    public static String trunc(String inval, int maxlen) {
        String retval = inval;
        if ( inval!=null && inval.length()>maxlen ) {
            retval = inval.substring(0, maxlen);
        }
        return retval;
    }

    public static String extractStackTrace(Throwable tx, int maxLength) {
        if (tx==null) {
              return "";
        }
        try {
            StringBuffer sb = new StringBuffer();
            Object[] st = tx.getStackTrace();
            for ( int i=0 ; i<st.length ; i++ ) {
                sb.append(st[i]+"\n");
                if ( maxLength>=0 && sb.length()>maxLength ) {
                    return sb.substring(0, maxLength);
                }
            }
            return sb.toString();
        } catch (Exception ex) {
            return "" + tx + " EXCEPTION EXTRACTING STACK-TRACE: " + ex;
        }
    }

    public static String removeUnexpectedChars(String s, String allowedChars) {
        StringBuffer ret = new StringBuffer();
        for ( int i=0 ; s!=null && i<s.length() ; i++ ) {
            char c = s.charAt(i);
            if ( allowedChars.indexOf(c) >= 0 ) {
                ret.append(c);
            }
        }
        return ret.toString();
    }

    public static String toHexString(String s) {
        StringBuffer ret = new StringBuffer();
        for ( int i=0 ; s!=null && i<s.length() ; i++ ) {
            char c = s.charAt(i);
            String hex = (Long.toHexString( 0x100 | c).substring(1).toUpperCase());
            ret.append(hex);
        }
        return ret.toString();
    }

    public static String fromHexString(String s) {
        StringBuffer ret = new StringBuffer();
        for ( int i=0 ; s!=null && i<s.length() ; i++ ) {
            String hex = s.substring(i,i+1);
            i++;
            if ( i<s.length() ) {
                hex += s.substring(i,i+1);
            }
            long l = Long.parseLong(hex, 16);
            char c = (char) l;
            ret.append(c);
        }
        return ret.toString();
    }

    public static String getNodeAttributeValue(NamedNodeMap attributes, String attrName) {
        if ( attributes==null || attrName==null ) {
            return null;
        }
        String value = 
            attributes.getNamedItem(attrName) != null ?
            attributes.getNamedItem(attrName).getNodeValue() :
            null;
        return value;
    }

    public static Map<String, String> getNodeAttributes(NamedNodeMap attributes) {
        if ( attributes==null ) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        int length = attributes.getLength();
        for ( int i=0 ; i<length ; i++ ) {
            Node item = attributes.item(i);
            String nodeName = item.getNodeName();
            String nodeValue = item.getNodeValue();
            map.put(nodeName, nodeValue);
        }
        return map;
    }

    public static String getElementTagValue(Element elem, String elemName) {
        if ( elem==null || paramNull(elemName) ) {
            return null;
        }
        String value = null;
        NodeList nodeList = elem.getElementsByTagName(elemName);
        if ( nodeList!=null && nodeList.getLength()>0 ) {
            Node item = nodeList.item(0);
            value = item.getTextContent().trim();
        }
        return value;
    }

}
