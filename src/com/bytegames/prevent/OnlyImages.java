package com.bytegames.prevent;

import java.io.*; 

/**
 * @author byte
 *
 * Filter for images.
 */
public class OnlyImages implements FilenameFilter { 

	public boolean accept(File dir, String name) {
		
		if(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith(".bmp"))
			return true;
		
		return false; 
	} 
}