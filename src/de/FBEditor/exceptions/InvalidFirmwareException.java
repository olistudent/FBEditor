/*
 * $Id: InvalidFirmwareException.java,v 1.3 2009/01/24 11:01:13 robotniko Exp $
 * 
 * Created on 10.04.2005
 */

package de.FBEditor.exceptions;

/**
 * thrown when a firmware object is invalid
 * 
 * @author Arno Willig
 * 
 */
public class InvalidFirmwareException extends Exception {
	private static final long serialVersionUID = 1;

	public InvalidFirmwareException() {
		super();
	}

	public InvalidFirmwareException(final String param) {
		super(param);
	}
}
