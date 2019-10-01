package it.bz.idm.bdp.dcmeteorologybz;

import java.lang.reflect.Method;
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

    public static final Class<?>[] EMPTY_TYPES = new Class[]{};
    public static final Object[]   EMPTY_VALUES = new Object[]{};
    public static final Class<?>[] STRING_TYPES = new Class[]{String.class};
    public static final String SEPARATOR = "$";

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

    public static Double convertIntegerToDouble(Integer inval) {
        Double retval = null;
        try {
            if ( inval!=null ) {
                retval = Double.valueOf(inval.intValue());
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
        String retval = convertDateToString(inval, "yyyy-MM-dd_HH-mm-ss");
        return retval;
    }

    public static String convertDateToString(Date inval, String pattern) {
        String retval = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            retval = formatter.format(inval);
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Date convertStringToDate(String inval) {
        Date retval = convertStringToDate(inval, "yyyy-MM-dd_HH-mm-ss");
        return retval;
    }

    public static Date convertStringToDate(String inval, String pattern) {
        Date retval = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            retval = formatter.parse(inval);
        } catch (Exception ex) {
        }
        return retval;
    }

    public static Date convertStringTimezoneToDate(String inval) {
        Date retval = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            retval = sdf.parse(inval);
        } catch (Exception e1) {
            LOG.debug("Step1, unable to parse date "+inval+": "+e1+"  Go To Step2");
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                retval = sdf.parse(inval);
            } catch (Exception e2) {
                LOG.debug("Step2, unable to parse date "+inval+": "+e2+"  Go To Step3");
                try {
                    OffsetDateTime odt = OffsetDateTime.parse(inval);
                    retval = new Date(odt.toEpochSecond());
                } catch (Exception e3) {
                    LOG.debug("Step3, unable to parse date "+inval+": "+e3+"  Go To Step4");
                    try {
                        inval = inval.substring(0, inval.indexOf("+"));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        retval = sdf.parse(inval);
                    } catch (Exception e4) {
                        LOG.error("Step4, Exception parsing date "+inval+": "+e4);
                    }
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

    public static Object getProperty(String name, Object target) {
        Object retval = null;
        Method getterMethod = null;
        Object obj = target;
        name = vv(name);
        int countOccorrenze = occorrenze(name, SEPARATOR);
        for ( int i=1 ; obj!=null && i<countOccorrenze ; i++ ) {
            String nameToUpperCase = checkCase(getToken(name,i));
            getterMethod = findMethod(obj, "get" + nameToUpperCase, EMPTY_TYPES);
            if (getterMethod != null) {
                retval = invokeMethod(obj, getterMethod, EMPTY_VALUES);
                obj = retval;
            } else {
                retval = null;
                obj = retval;
            }
        }
        return retval;
    }

    public static Method findMethod(Object target, String methodName, Class<?>[] methodTypes) {
        try {
            if ( target!=null ) {
                Method method = target.getClass().getMethod(methodName, methodTypes);
                return method;
            }
        } catch (Exception ex) {
            LOG.debug("Method '"+methodName+"' not found in class '"+target+"': "+ex);
            LOG.debug("EXCEPTION: "+ex, ex);
        }
        return null;
    }

    public static Object invokeMethod(Object target, Method method, Object[] methodValues) {
        try {
            Object retval = method.invoke(target, methodValues);
            return retval;
        } catch (Exception ex) {
            LOG.error("EXCEPTION: "+ex, ex);
        }
        return null;
    }

    public static String vv(String s) {
        String r = allowNulls(s);
        if ("".equals(r)) {
            return r;
        } else {
            r = adjustsPropertyName(r);
            if (!r.substring(r.length()-1).equals(SEPARATOR)){
                r = r+SEPARATOR;
            }
            if (!r.substring(0,1).equals(SEPARATOR)){
                r = SEPARATOR+r;
            }
        }
        return r;
    }

    public static String adjustsPropertyName(String propName) {
        if (paramNotNull(propName)) {
            propName = propName.replace(".", SEPARATOR);
        }
        return propName;
    }

    public static int occorrenze(String s, String c) {
        if (s.length()==0) return 0;
        else if (s.charAt(0)==c.charAt(0)) return 1 + occorrenze(s.substring(1),c);
        else return occorrenze(s.substring(1),c);
    }

    public static String checkCase(String name) {
        char firstLetter = name.charAt(0);
        if(Character.isLowerCase(firstLetter)) {
            name = name.substring(1,name.length());
            name = Character.toString(firstLetter).toUpperCase() + name;
        }
        return name;
    }

    public static String getToken(String s, int token){
        int p;
        int q;
        String ss = vv (s);
        p = instr (ss, SEPARATOR, 0, token) + 1;
        q = instr (ss, SEPARATOR, p, 1) - p;
        return ss.substring(p, p+q);
    }

    public static int instr(String s, String v, int d, int p) {
        int e = d;
        for (int i=0;i<p;i++){
            d = s.indexOf(v, e);
            e = d+1;
        }
        return d;
    }

}
