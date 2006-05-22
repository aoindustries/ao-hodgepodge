package com.aoindustries.swing;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;
import javax.swing.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class JBlank extends JComponent {

	private int width, height;

/**
 * Constructs an empty DefaultComboBoxModel object.
 */
public JBlank() {
	this(1, 1);
}
/**
 * Constructs an empty DefaultComboBoxModel object.
 */
public JBlank(int width, int height) {
	this.width=width;
	this.height=height;
}
public Dimension getPreferredSize() {
	return new Dimension(width,height);
}
}
