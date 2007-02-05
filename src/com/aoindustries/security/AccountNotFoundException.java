package com.aoindustries.security;

/*
 * Copyright 2004-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public class AccountNotFoundException extends LoginException {

    public AccountNotFoundException() {
        super();
    }
    
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(Throwable cause) {
        super(cause);
    }
    
    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}