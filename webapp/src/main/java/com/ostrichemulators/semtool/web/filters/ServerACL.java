package com.ostrichemulators.semtool.web.filters;

/**
 * 
 * @author Wayne Warren
 *
 */
public class ServerACL extends AbstractAccessControlList  {

	
	public void initialize(){
		
	}
	
	public boolean currentUserCanRead(){
		
		return true;
	}
	
	public boolean currentUserCanWrite(){
		return true;
	}
	
}