/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui.utils;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.ide.core.LangCore;
import melnorme.utilbox.core.CommonException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Utility for handling exceptions during UI operations, by presenting an information dialog to the user.
 */
public class UIOperationExceptionHandler {
	
	public static void handleError(String message, Throwable exception) {
		handleError(true, message, exception);
	}
	
	public static void handleError(boolean logError, String message, Throwable exception) {
		assertNotNull(message);
		
		if(logError) {
			LangCore.logError(message, exception);
		}
		
		Shell shell = WorkbenchUtils.getActiveWorkbenchShell();
		
		if(exception == null) {
			MessageDialog.open(SWT.ERROR, shell, "Error: ", message, SWT.SHEET);
		} else {
			String exceptionText = getExceptionText(exception);
			MessageDialog.open(SWT.ERROR, shell, "Error: " + message, exceptionText, SWT.SHEET);
		}
	}
	
	protected static String getExceptionText(Throwable exception) {
		if(exception == null)
			return null;
		
		String exceptionText = exception.getClass().getName();
		if(exception.getMessage() != null) {
			exceptionText += ": " + exception.getMessage();
		}
		return exceptionText;
	}
	
	public static void handleWithErrorDialog(boolean logError, String title, String message, Throwable exception) {
		if(logError) {
			LangCore.logError(message, exception);
		}
		
		if(message == null) {
			message = "Error";
		}
		
		Shell shell = WorkbenchUtils.getActiveWorkbenchShell();
		
		if(exception == null) {
			// No point in using ErrorDialog, use simpler dialog
			MessageDialog.open(SWT.ERROR, shell, title, message, SWT.SHEET);
			return;
		}
		
		Status status = LangCore.createErrorStatus(getExceptionText(exception), exception);
		ErrorDialog.openError(shell, title, message, status);
	}
	
	/* -----------------  ----------------- */
	
	public static void handleOperationStatus(String dialogTitle, CoreException ce) {
		IStatus status = ce.getStatus();
		if(status.isOK() || status.matches(IStatus.CANCEL)) {
			return;
		}
		
		boolean logError = status.matches(IStatus.ERROR);
		
		handleWithErrorDialog(logError, dialogTitle, ce.getMessage(), ce.getCause());
	}
	
	public static void handleOperationStatus(String dialogTitle, CommonException ce) {
		handleWithErrorDialog(true, dialogTitle, ce.getMessage(), ce.getCause());
	}
	
}