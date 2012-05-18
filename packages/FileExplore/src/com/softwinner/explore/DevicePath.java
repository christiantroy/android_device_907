/**
 * you can find the path of sdcard,flash and usbhost in here
 * @author chenjd
 * @email chenjd@allwinnertech.com
 * @data 2011-8-10
 */
package com.softwinner.explore;

import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
/**
 * define the root path of flash,sdcard,usbhost
 * @author chenjd
 *
 */
public class DevicePath{
	private ArrayList<String> totalDevicesList;
	private String FLASH_ROOT_PATH;
	private String SDCARD_ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
	private String USBHOST_ROOT_PATH;
	public DevicePath(Context context)
	{
		totalDevicesList = new ArrayList<String>();
		StorageManager stmg = (StorageManager) context.getSystemService(context.STORAGE_SERVICE);
		String[] list = stmg.getVolumePaths();
		for(int i = 0; i < list.length; i++)
		{
			totalDevicesList.add(list[i]);
		}
	}
	
	public String getSdStoragePath()
	{
		String path = "none";
		for(int i = 0; i < totalDevicesList.size(); i++)
		{
			if(!totalDevicesList.get(i).equals(Environment.getExternalStorageDirectory().getPath()))
			{
				if(totalDevicesList.get(i).contains("sd"))
				{
					path = totalDevicesList.get(i);
					return path;
				}
			}
		}
		return path;
	}
	
	public String getInterStoragePath()
	{
		return Environment.getExternalStorageDirectory().getPath();
	}
	
	public String getUsbStoragePath()
	{
		String path = "none";
		for(int i = 0; i < totalDevicesList.size(); i++)
		{
			if(!totalDevicesList.get(i).equals(Environment.getExternalStorageDirectory().getPath()))
			{
				if(totalDevicesList.get(i).contains("usb"))
				{
					path = totalDevicesList.get(i);
					return path;
				}
			}
		}
		return path;
	}
}


