/*
 * $Id: WrongPasswordException.java,v 1.4 2009/02/23 13:57:40 robotniko Exp $
 * 
 * Created on 10.04.2005
 */

package de.FBEditor.exceptions;

import javax.swing.JOptionPane;

import de.FBEditor.FBEdit;

/**
 * thrown when the web admin password of the fritz box is invalid
 * @author Arno Willig
 *
 */

public class WrongPasswordException extends Exception {
	private static final long serialVersionUID = 1;
	
	private int waitSeconds = 0;
	
	private String affectedBox = "";
	
    public WrongPasswordException() {
        super();
        passwordWrongMessage();
    }
    
    public WrongPasswordException(final String affectedBox, final String param, final int waitSeconds) {
        super(param);
        passwordWrongMessage();
        this.waitSeconds = waitSeconds;
        this.affectedBox = affectedBox;
    }
    
    private void passwordWrongMessage() {
    	JOptionPane.showMessageDialog(FBEdit.getInstance().getframe(), "Password falsch!", "Fehler", 0);
	}

	public int getRetryTime()
    {
    	return waitSeconds;
    }
    
    public String getAffectedBox()
    {
    	return affectedBox;
    }
}
