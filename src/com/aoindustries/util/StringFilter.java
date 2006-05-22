package com.aoindustries.util;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public class StringFilter extends DataFilter {

    public StringFilter(String expression) {
        super(expression);
    }
    
    public boolean matches(Object O) {
        if(O==null) return false;
        String S=(String)O;
        switch(function) {
            case LIKE: return S.toLowerCase().indexOf(value.toLowerCase())!=-1;
            case NOT_LIKE: return S.toLowerCase().indexOf(value.toLowerCase())==-1;
            case EQUALS: return S.equalsIgnoreCase(value);
            case NOT_EQUALS: return !S.equalsIgnoreCase(value);
            case GREATER_THAN: return S.compareToIgnoreCase(value)>0;
            case GREATER_THAN_OR_EQUAL: return S.compareToIgnoreCase(value)>=0;
            case LESS_THAN: return S.compareToIgnoreCase(value)<0;
            case LESS_THAN_OR_EQUAL: return S.compareToIgnoreCase(value)<=0;
            default: throw new RuntimeException("Unexpected function: "+function);
        }
    }
}