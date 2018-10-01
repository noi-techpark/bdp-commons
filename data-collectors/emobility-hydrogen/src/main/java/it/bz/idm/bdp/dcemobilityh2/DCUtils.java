package it.bz.idm.bdp.dcemobilityh2;

public class DCUtils {

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

}
