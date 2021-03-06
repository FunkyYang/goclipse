/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package com.googlecode.goclipse.core.operations;

import melnorme.lang.ide.core.utils.CoreOperationAdapter;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.MiscUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.googlecode.goclipse.core.GoProjectEnvironment;
import com.googlecode.goclipse.tooling.env.GoEnvironment;
import com.googlecode.goclipse.tooling.env.GoPath;

public class GetAndInstallGocodeOperation extends CoreOperationAdapter {
	
	protected Location workingDir;
	
	@Override
	public void doRun(IProgressMonitor monitor) throws CommonException, CoreException {
		GoEnvironment goEnv = GoProjectEnvironment.getGoEnvironment(null);
		workingDir = getFirstGoPathEntry(goEnv);
		
		ArrayList2<String> cmdLine = getCmdLine();
		
		GoToolManager.getDefault().runBuildTool(goEnv, monitor, workingDir, cmdLine);
	}
	
	protected Location getFirstGoPathEntry(GoEnvironment goEnv) throws CommonException {
		GoPath goPath = goEnv.getGoPath();
		
		if(goPath.isEmpty()) {
			throw new CommonException("GOPATH is empty, can't install.");
		}
		String workingDirStr = goPath.getGoPathEntries().get(0);
		Location workingDir = Location.createValidLocation(workingDirStr, "Invalid GOPATH: ");
		return workingDir;
	}
	
	public ArrayList2<String> getCmdLine() {
		ArrayList2<String> cmdLine = CollectionUtil.createArrayList("go", "get", "-u");
		if(MiscUtil.OS_IS_WINDOWS) {
			cmdLine.addElements("-ldflags", "-H=windowsgui");
		}
		cmdLine.addElements("github.com/nsf/gocode");
		return cmdLine;
	}
	
	public Location getGocodeExeLocation() {
		return workingDir.resolve_fromValid("bin/gocode" + (MiscUtil.OS_IS_WINDOWS ? ".exe" : ""));
	}
	
}