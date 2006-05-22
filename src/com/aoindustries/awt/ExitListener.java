package com.aoindustries.awt;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * A class implementing the <code>ExitListener</code> interface is
 * able to respond to the closing of an application.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public interface ExitListener {
/**
 * Called when the application is exiting.
 *
 * @return <code>true</code> if successful, or <code>false</code> if unable to exit
 */
boolean exitApplication();
}
