/*
    Open Manager, an open source file manager for the Android system
    Copyright (C) 2009, 2010, 2011  Joe Berria <nexesdevelopment@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.softwinner.explore;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main activity. The activity that is presented to the user
 * as the application launches. This class is, and expected not to be, instantiated.
 * <br>
 * <p>
 * This class handles creating the buttons and
 * text views. This class relies on the class EventHandler to handle all button
 * press logic and to control the data displayed on its ListView. This class
 * also relies on the FileManager class to handle all file operations such as
 * copy/paste zip/unzip etc. However most interaction with the FileManager class
 * is done via the EventHandler class. Also the SettingsMangager class to load
 * and save user settings. 
 * <br>
 * <p>
 * The design objective with this class is to control only the look of the
 * GUI (option menu, context menu, ListView, buttons and so on) and rely on other
 * supporting classes to do the heavy lifting. 
 *
 * @author Joe Berria
 *
 */
public final class Main extends ListActivity {
	private static final String PREFS_NAME = "ManagerPrefsFile";	//user preference file name
	private static final String PREFS_HIDDEN = "hidden";
	private static final String PREFS_COLOR = "color";
	private static final String PREFS_THUMBNAIL = "thumbnail";
	private static final String PREFS_SORT = "sort";
	private static final String PREFS_STORAGE = "sdcard space";
	
	private static final int MENU_MKDIR =   0x00;			//option menu id
	private static final int MENU_SETTING = 0x01;			//option menu id
	private static final int MENU_SEARCH =  0x02;			//option menu id
	private static final int MENU_SPACE =   0x03;			//option menu id
	private static final int MENU_QUIT = 	0x04;			//option menu id
	private static final int SEARCH_B = 	0x09;
	
	private static final int D_MENU_DELETE = 0x05;			//context menu id
	private static final int D_MENU_RENAME = 0x06;			//context menu id
	private static final int D_MENU_COPY =   0x07;			//context menu id
	private static final int D_MENU_PASTE =  0x08;			//context menu id
	private static final int D_MENU_ZIP = 	 0x0e;			//context menu id
	private static final int D_MENU_UNZIP =  0x0f;			//context menu id
	private static final int D_MENU_MOVE = 	 0x30;			//context menu id
	private static final int F_MENU_MOVE = 	 0x20;			//context menu id
	private static final int F_MENU_DELETE = 0x0a;			//context menu id
	private static final int F_MENU_RENAME = 0x0b;			//context menu id
	private static final int F_MENU_ATTACH = 0x0c;			//context menu id
	private static final int F_MENU_COPY =   0x0d;			//context menu id
	private static final int SETTING_REQ = 	 0x10;			//request code for intent

	private FileManager mFileMag;
	private EventHandler mHandler;
	private EventHandler.TableRow mTable;
	private CatalogList mCataList;
	private DevicePath  mDevicePath;
	
	private SharedPreferences mSettings;
	private boolean mReturnIntent = false;
	private boolean mHoldingFile = false;
	private boolean mHoldingZip = false;
	private boolean mUseBackKey = true;
	private String mCopiedTarget;
	private String mZippedTarget;
	private String mSelectedListItem;				//item from context menu
	private TextView  mPathLabel, mDetailLabel;
		
	private BroadcastReceiver mReceiver;

	private String TAG = "FileManager.Main";
	
	private String openType;
	private File openFile;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(android.os.Build.VERSION.SDK_INT != 11)
        	requestWindowFeature(Window.FEATURE_NO_TITLE);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  

        setContentView(R.layout.main);
        
        /*read settings*/
        mSettings = getSharedPreferences(PREFS_NAME, 0);
        boolean hide = mSettings.getBoolean(PREFS_HIDDEN, false);
        boolean thumb = mSettings.getBoolean(PREFS_THUMBNAIL, true);
        int space = mSettings.getInt(PREFS_STORAGE, View.VISIBLE);
        int color = mSettings.getInt(PREFS_COLOR, -1);
        int sort = mSettings.getInt(PREFS_SORT, 1);

		mFileMag = new FileManager(this);
        mFileMag.setShowHiddenFiles(hide);
        mFileMag.setSortType(sort);
        
        mCataList = new CatalogList(this);
        mDevicePath = new DevicePath(this);
        
        mHandler = new EventHandler(Main.this, mFileMag,mCataList);
        mHandler.setTextColor(color);
        mHandler.setShowThumbnails(thumb);
        mTable = mHandler.new TableRow();
        
        /*sets the ListAdapter for our ListActivity and
         *gives our EventHandler class the same adapter
         */
        mHandler.setListAdapter(mTable);
        setListAdapter(mTable);
        
        /* register context menu for our list view */
        registerForContextMenu(getListView());
                
        mDetailLabel = (TextView)findViewById(R.id.detail_label);
        mPathLabel = (TextView)findViewById(R.id.path_label);
        mHandler.setUpdateLabels(mPathLabel, mDetailLabel);
		
        /*
         * change to the specific file path if this component is started by other applications
         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {        	
			Log.i(TAG, "intent action ="+getIntent().getAction());
        	String path = bundle.getString("Path");
			Log.i(TAG, "path = "+path);
			if (path != null)
        	{
        		if(path.equals( mDevicePath.getUsbStoragePath() ) ) {
        			mPathLabel.setText(path);
        			mHandler.updateDirectory(mFileMag.getHomeDir(mFileMag.ROOT_USBHOST));
        			getFocusForButton(R.id.home_usbhost_button);
        		}
        		else if (path.equals(mDevicePath.getInterStoragePath())) {
        			mPathLabel.setText(path);
        			mHandler.updateDirectory(mFileMag.getHomeDir(mFileMag.ROOT_SDCARD));
        			getFocusForButton(R.id.home_sdcard_button);
        		}
        		else if (path.equals(mDevicePath.getSdStoragePath()) ) {
        			mPathLabel.setText(path);
        			mHandler.updateDirectory(mFileMag.getHomeDir(mFileMag.ROOT_FLASH));
        			getFocusForButton(R.id.home_flash_button);
        		}
        	}
        }
        else { 
        	//default path        	
        	mPathLabel.setText(mDevicePath.getInterStoragePath());
        	mHandler.updateDirectory(mFileMag.getHomeDir(mFileMag.ROOT_SDCARD));
        	getFocusForButton(R.id.home_sdcard_button);
        }

		/* setup buttons */
        int[] img_button_id = {R.id.home_flash_button,
        					   R.id.home_sdcard_button,R.id.home_usbhost_button,
        					   R.id.back_button,
        					   R.id.manage_button, R.id.multiselect_button,
        					   R.id.image_button,R.id.movie_button};
        
        int[] button_id = {R.id.hidden_copy, R.id.hidden_attach,
        				   R.id.hidden_delete, R.id.hidden_move};
        
        ImageButton[] bimg = new ImageButton[img_button_id.length];
        Button[] bt = new Button[button_id.length];
		
        for(int i = 0; i < img_button_id.length; i++) {
        	bimg[i] = (ImageButton)findViewById(img_button_id[i]);
        	bimg[i].setOnClickListener(mHandler);

        	if(i < 4) {
        		bt[i] = (Button)findViewById(button_id[i]);
        		bt[i].setOnClickListener(mHandler);
        	}
        }

		if( getIntent().getAction() != null ){
			if(getIntent().getAction().equals(Intent.ACTION_GET_CONTENT)) {
        	bimg[5].setVisibility(View.GONE);
			
        	mReturnIntent = true;
			
			}
		}
        	
        //register reciver to process sdcard out message
        mReceiver = new BroadcastReceiver() {   
        	@Override  
        	public void onReceive(Context context, Intent intent) {   
        		String tmpstring = intent.getData().toString();
        		
        		String dataOfUsb = mDevicePath.getUsbStoragePath();
        		String dataOfSd = mDevicePath.getInterStoragePath();
        		String dataOfFlash = mDevicePath.getSdStoragePath();
        		
        		if(intent.getAction().equals(intent.ACTION_MEDIA_UNMOUNTED) ||
        				intent.getAction().equals(intent.ACTION_MEDIA_REMOVED) ||
        				intent.getAction().equals(intent.ACTION_MEDIA_BAD_REMOVAL))
        		{
        			Log.d(TAG, tmpstring);
        			/*
        			 * add by chenjd,chenjd@allwinnertech.com 2011-09-15
        			 * waiting for unmounted really 
        			 */
        			try
					{
						Thread.currentThread().sleep(1000);
					}
					catch(Exception e) {};
        			switch(mHandler.getMode())
        			{
        			case EventHandler.TREEVIEW_MODE:
        				
        				if(tmpstring.contains(dataOfSd))
        				{
        					DisplayToast(getResources().getString(R.string.sdcard_out));
        					if(mFileMag.getCurrentDir().startsWith(dataOfSd))
        					{
        						mHandler.updateDirectory(mFileMag.getHomeDir(mFileMag.ROOT_SDCARD));
        					}
        				}
        				else if(tmpstring.contains(dataOfUsb))
        				{ 
        					DisplayToast(getResources().getString(R.string.usb_out));	
        					if(mFileMag.getCurrentDir().startsWith(dataOfUsb))
        					{
        						mHandler.updateDirectory(mFileMag.getHomeDir(mFileMag.ROOT_USBHOST));
        					}
        				}
        				else if(tmpstring.contains(dataOfFlash))
        				{
        					DisplayToast(getResources().getString(R.string.flash_out));
        					if(mFileMag.getCurrentDir().startsWith(dataOfFlash))
        					{
        						mHandler.updateDirectory(mFileMag.getHomeDir(mFileMag.ROOT_FLASH));
        					}
        				}
        				else
        				{
        					return;
        				}
        				if(mHandler.isMultiSelected()) 
        				{
        					mTable.killMultiSelect(true);
        					DisplayToast(getResources().getString(R.string.Multi_select_off));
        				}
        				if(mPathLabel != null)
        					mPathLabel.setText(mFileMag.getCurrentDir());
        				break;
        				//anyway,remove the list in media storage
        			case EventHandler.CATALOG_MODE:
        				ArrayList<String> content = null;
        				if(tmpstring.contains(dataOfSd))
        				{
        					DisplayToast(getResources().getString(R.string.sdcard_out));
        					content = mCataList.DisAttachMediaStorage(CatalogList.STORAGE_SDCARD);
        				}
        				else if(tmpstring.contains(dataOfUsb))
        				{ 
        					DisplayToast(getResources().getString(R.string.usb_out));
        					content = mCataList.DisAttachMediaStorage(CatalogList.STORAGE_USBHOST);			
        				}
        				else if(tmpstring.contains(dataOfFlash))
        				{
        					DisplayToast(getResources().getString(R.string.flash_out));
        					content = mCataList.DisAttachMediaStorage(CatalogList.STORAGE_FLASH);
        				}
        				else
        				{
        					return;
        				}
        				if(content != null)
        				{
        					mHandler.setFileList(content);
        				}
        				break;
        			}
        		}
        		else if (intent.getAction().equals(intent.ACTION_MEDIA_MOUNTED))
        		{
        			switch(mHandler.getMode())
        			{
        			case EventHandler.TREEVIEW_MODE:
        				if(tmpstring.contains(dataOfSd))
        				{
        					DisplayToast(getResources().getString(R.string.sdcard_in));
        					if(mFileMag.getCurrentDir().startsWith(dataOfSd))
        					{
        						mHandler.updateDirectory(mFileMag.getHomeDir(mFileMag.ROOT_SDCARD));
        					}
        				}
        				else if(tmpstring.contains(dataOfUsb))
        				{
        					DisplayToast(getResources().getString(R.string.usb_in));	
        					if(mFileMag.getCurrentDir().startsWith(dataOfUsb))
        					{
        						mHandler.updateDirectory(mFileMag.getHomeDir(mFileMag.ROOT_USBHOST));
        					}
        				}
        				else if(tmpstring.contains(dataOfFlash))
        				{
        					DisplayToast(getResources().getString(R.string.flash_in));
        					if(mFileMag.getCurrentDir().startsWith(dataOfFlash))
        					{
        						mHandler.updateDirectory(mFileMag.getHomeDir(mFileMag.ROOT_FLASH));
        					}
        				}
        				else 
        				{
        					return;
        				}
        				break;
        			case EventHandler.CATALOG_MODE:
        				if(tmpstring.contains(dataOfSd))
        				{
        					DisplayToast(getResources().getString(R.string.sdcard_in));
        					mCataList.AttachMediaStorage(CatalogList.STORAGE_SDCARD);
        				}
        				else if(tmpstring.contains(dataOfUsb))
        				{ 
        					DisplayToast(getResources().getString(R.string.usb_in));
        					mCataList.AttachMediaStorage(CatalogList.STORAGE_USBHOST);
        				}
        				else if(tmpstring.contains(dataOfFlash))
        				{
        					DisplayToast(getResources().getString(R.string.flash_in));
        					mCataList.AttachMediaStorage(CatalogList.STORAGE_FLASH);
        				}
        				else
        				{
        					return;
        				}
        				
        				mHandler.setFileList(mCataList.listSort());
        				
        				break;
        			}
        		}
        	}   
        };
		
        IntentFilter filter = new IntentFilter();   
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);   
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);  
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addDataScheme("file"); 
        registerReceiver(mReceiver, filter);   		
    }
	
	private void getFocusForButton(int id)
	{
		View v = findViewById(id);
		mHandler.getInitView(v);
		v.setSelected(true);
	}
	
	private void DisplayToast(String str){
		Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
	}
	
	@Override  
    protected void onDestroy() {   
        // TODO Auto-generated method stub   
        super.onDestroy();   
        unregisterReceiver(mReceiver);   
    }   
	/*(non Java-Doc)
	 * Returns the file that was selected to the intent that
	 * called this activity. usually from the caller is another application.
	 */
	private void returnIntentResults(File data) {
		mReturnIntent = false;
		
		Intent ret = new Intent();
		ret.setData(Uri.fromFile(data));
		setResult(RESULT_OK, ret);
		
		finish();
	}
		
	private String getCurrentFileName(int position){
		final String item = mHandler.getData(position);
		
    	if(mHandler.getMode() == EventHandler.TREEVIEW_MODE)
    	{
    		return (mFileMag.getCurrentDir() + "/" + item);
    	}
    	
    	return item;
	}
	/**
	 *  To add more functionality and let the user interact with more
	 *  file types, this is the function to add the ability. 
	 *  
	 *  (note): this method can be done more efficiently 
	 */
    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
    	final String item = getCurrentFileName(position);
    	File file = new File(item);
    	boolean multiSelect = mHandler.isMultiSelected();
    	
    	String item_ext = null;
    	
    	try {
    		item_ext = item.substring(item.lastIndexOf(".") + 1, item.length());
    		
    	} catch(IndexOutOfBoundsException e) {	
    		item_ext = ""; 
    	}
    	
    	/*
    	 * If the user has multi-select on, we just need to record the file
    	 * not make an intent for it.
    	 */
    	if(multiSelect) {
    		mTable.addMultiPosition(position, file.getPath());
    		 
    	} else {
	    	if (file.isDirectory()) {
				if(file.canRead()) {
		    		mHandler.updateDirectory(mFileMag.getNextDir(item, true));
		    		mPathLabel.setText(mFileMag.getCurrentDir());
		    		
		    		/*set back button switch to true 
		    		 * (this will be better implemented later)
		    		 */
		    		if(!mUseBackKey)
		    			mUseBackKey = true;
		    		
	    		} else {
	    			Toast.makeText(this, "Can't read folder due to permissions", 
	    							Toast.LENGTH_SHORT).show();
	    		}
	    	}
	    	
	    	/*music file selected--add more audio formats*/
	    	else if (TypeFilter.getInstance().isMusicFile(item_ext)) {
	    		
	    		if(mReturnIntent) {
	    			returnIntentResults(file);
	    		} else { 
                                        Intent picIntent = new Intent();
                                        picIntent.setAction(android.content.Intent.ACTION_VIEW);
                                        picIntent.setDataAndType(Uri.fromFile(file), "audio/*");
                                        startActivity(picIntent);
	    		}
	    	}
	    	
	    	/*photo file selected*/
	    	else if(TypeFilter.getInstance().isPictureFile(item_ext)) {
	 			    		
	    		if (file.exists()) {
	    			if(mReturnIntent) {
	    				returnIntentResults(file);
	    				
	    			} else {
			    		Intent picIntent = new Intent();
			    		picIntent.setAction(android.content.Intent.ACTION_VIEW);
			    		picIntent.setDataAndType(Uri.fromFile(file), "image/*");
			    		startActivity(picIntent);
	    			}
	    		}
	    	}
	    	
	    	/*video file selected--add more video formats*/
	    	else if(TypeFilter.getInstance().isMovieFile(item_ext)) {
	    		
	    		if (file.exists()) {
	    			if(mReturnIntent) {
	    				returnIntentResults(file);
	    				
	    			} else {
			    		Intent movieIntent = new Intent();
			    		movieIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
			    		movieIntent.setAction(android.content.Intent.ACTION_VIEW);
			    		movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
			    		startActivity(movieIntent);
	    			}
	    		}
	    	}
	    	
	    	/*zip file */
	    	else if(TypeFilter.getInstance().isZipFile(item_ext)) {
	    		
	    		if(mReturnIntent) {
	    			returnIntentResults(file);
	    			
	    		} else {
		    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    		AlertDialog alert;
		    		mZippedTarget = mFileMag.getCurrentDir() + "/" + item;
		    		CharSequence[] option = {"Extract here", "Extract to..."};
		    		
		    		builder.setTitle("Extract");
		    		builder.setItems(option, new DialogInterface.OnClickListener() {
		
						public void onClick(DialogInterface dialog, int which) {
							switch(which) {
								case 0:
									String dir = mFileMag.getCurrentDir();
									mHandler.unZipFile(item, dir + "/");
									break;
									
								case 1:
									mDetailLabel.setText("Holding " + item + 
														 " to extract");
									mHoldingZip = true;
									break;
							}
						}
		    		});
		    		
		    		alert = builder.create();
		    		alert.show();
	    		}
	    	}
	    	
	    	/* gzip files, this will be implemented later */
	    	else if(TypeFilter.getInstance().isGZipFile(item_ext)) {
	    		
	    		if(mReturnIntent) {
	    			returnIntentResults(file);
	    			
	    		} else {
	    			//TODO:
	    		}
	    	}
	    	
	    	/*pdf file selected*/
	    	else if(TypeFilter.getInstance().isPdfFile(item_ext)) {
	    		
	    		if(file.exists()) {
	    			if(mReturnIntent) {
	    				returnIntentResults(file);
	    				
	    			} else {
			    		Intent pdfIntent = new Intent();
			    		pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
			    		pdfIntent.setDataAndType(Uri.fromFile(file), 
			    								 "application/pdf");
			    		
			    		try {
			    			startActivity(pdfIntent);
			    		} catch (ActivityNotFoundException e) {
			    			Toast.makeText(this, "Sorry, couldn't find a pdf viewer", 
									Toast.LENGTH_SHORT).show();
			    		}
		    		}
	    		}
	    	}
	    	
	    	/*Android application file*/
	    	else if(TypeFilter.getInstance().isApkFile(item_ext)){
	    		
	    		if(file.exists()) {
	    			if(mReturnIntent) {
	    				returnIntentResults(file);
	    				
	    			} else {
		    			Intent apkIntent = new Intent();
		    			apkIntent.setAction(android.content.Intent.ACTION_VIEW);
		    			apkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		    			startActivity(apkIntent);
	    			}
	    		}
	    	}
	    	
	    	/* HTML file */
	    	else if(TypeFilter.getInstance().isHtml32File(item_ext)) {
	    		
	    		if(file.exists()) {
	    			if(mReturnIntent) {
	    				returnIntentResults(file);
	    				
	    			} else {
		    			Intent htmlIntent = new Intent();
		    			htmlIntent.setAction(android.content.Intent.ACTION_VIEW);
		    			htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");
		    			
		    			try {
		    				startActivity(htmlIntent);
		    			} catch(ActivityNotFoundException e) {
		    				Toast.makeText(this, "Sorry, couldn't find a HTML viewer", 
		    									Toast.LENGTH_SHORT).show();
		    			}
	    			}
	    		}
	    	}
	    	
	    	/* text file*/
	    	else if(TypeFilter.getInstance().isTxtFile(item_ext)) {
	    		
	    		if(file.exists()) {
	    			if(mReturnIntent) {
	    				returnIntentResults(file);
	    				
	    			} else {
		    			Intent txtIntent = new Intent();
		    			txtIntent.setAction(android.content.Intent.ACTION_VIEW);
		    			txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");
		    			
		    			try {
		    				startActivity(txtIntent);
		    			} catch(ActivityNotFoundException e) {
		    				txtIntent.setType("text/*");
		    				startActivity(txtIntent);
		    			}
	    			}
	    		}
	    	}
	    	
	    	/* generic intent */
	    	else {
	    		if(file.exists()) {
	    			if(mReturnIntent) {
	    				returnIntentResults(file);
	    				
	    			} else {
	    				openFile = file;
			    		selectFileType_dialog();
	    			}
	    		}
	    	}
    	}
	}
    
    private void selectFileType_dialog() {
    	String mFile = Main.this.getResources().getString(R.string.open_file);
		String mText = Main.this.getResources().getString(R.string.text);
		String mAudio = Main.this.getResources().getString(R.string.audio);
		String mVideo = Main.this.getResources().getString(R.string.video);
		String mImage = Main.this.getResources().getString(R.string.image);
		CharSequence[] FileType = {mText,mAudio,mVideo,mImage};
		AlertDialog.Builder builder;
    	AlertDialog dialog;
		builder = new AlertDialog.Builder(Main.this);
		builder.setTitle(mFile);
		builder.setIcon(R.drawable.help);
		builder.setItems(FileType, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent();
				switch(which) {
				case 0:
					openType = "text/*";
					break;
				case 1:
					openType = "audio/*";
					break;
				case 2:
					openType = "video/*";
					break;
				case 3:
					openType = "image/*";
					break;
				}
				mIntent.setAction(android.content.Intent.ACTION_VIEW);
				mIntent.setDataAndType(Uri.fromFile(openFile), openType);
				try {
	    			startActivity(mIntent);
	    		} catch(ActivityNotFoundException e) {
	    			Toast.makeText(Main.this, "Sorry, couldn't find anything " +
	    						   "to open " + openFile.getName(), 
	    						   Toast.LENGTH_SHORT).show();
	    		}
			}
		});	
		dialog = builder.create();
    	dialog.show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	SharedPreferences.Editor editor = mSettings.edit();
    	boolean check;
    	boolean thumbnail;
    	int color, sort, space;
    	
    	/* resultCode must equal RESULT_CANCELED because the only way
    	 * out of that activity is pressing the back button on the phone
    	 * this publishes a canceled result code not an ok result code
    	 */
    	if(requestCode == SETTING_REQ && resultCode == RESULT_CANCELED) {
    		//save the information we get from settings activity
    		check = data.getBooleanExtra("HIDDEN", false);
    		thumbnail = data.getBooleanExtra("THUMBNAIL", true);
    		color = data.getIntExtra("COLOR", -1);
    		sort = data.getIntExtra("SORT", 0);
    		space = data.getIntExtra("SPACE", View.VISIBLE);
    		
    		editor.putBoolean(PREFS_HIDDEN, check);
    		editor.putBoolean(PREFS_THUMBNAIL, thumbnail);
    		editor.putInt(PREFS_COLOR, color);
    		editor.putInt(PREFS_SORT, sort);
    		editor.putInt(PREFS_STORAGE, space);
    		editor.commit();
    		  		
    		mFileMag.setShowHiddenFiles(check);
    		mFileMag.setSortType(sort);
    		mHandler.setTextColor(color);
    		mHandler.setShowThumbnails(thumbnail);
    		mHandler.updateDirectory(mFileMag.getNextDir(mFileMag.getCurrentDir(), true));
    	}
    }
    
    /* ================Menus, options menu and context menu start here=================*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_MKDIR, 0, getResources().getString(R.string.New_Directory)).setIcon(R.drawable.newfolder);
    	menu.add(0, MENU_SEARCH, 0, getResources().getString(R.string.Search)).setIcon(R.drawable.search);
    	
    		/* free space will be implemented at a later time */
//    	menu.add(0, MENU_SPACE, 0, "Free space").setIcon(R.drawable.space);
    	menu.add(0, MENU_SETTING, 0, getResources().getString(R.string.Settings)).setIcon(R.drawable.setting);
    	menu.add(0, MENU_QUIT, 0, getResources().getString(R.string.Quit)).setIcon(R.drawable.logout);
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case MENU_MKDIR:
    			showDialog(MENU_MKDIR);
    			return true;
    			
    		case MENU_SEARCH:
    			showDialog(MENU_SEARCH);
    			return true;
    			
    		case MENU_SPACE: /* not yet implemented */
    			return true;
    			
    		case MENU_SETTING:
    			Intent settings_int = new Intent(this, Settings.class);
    			settings_int.putExtra("HIDDEN", mSettings.getBoolean(PREFS_HIDDEN, false));
    			settings_int.putExtra("THUMBNAIL", mSettings.getBoolean(PREFS_THUMBNAIL, true));
    			settings_int.putExtra("COLOR", mSettings.getInt(PREFS_COLOR, -1));
    			settings_int.putExtra("SORT", mSettings.getInt(PREFS_SORT, 1));
    			settings_int.putExtra("SPACE", mSettings.getInt(PREFS_STORAGE, View.VISIBLE));
    			
    			startActivityForResult(settings_int, SETTING_REQ);
    			return true;
    			
    		case MENU_QUIT:
    			finish();
    			return true;
    	}
    	return false;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {
    	super.onCreateContextMenu(menu, v, info);
    	
    	boolean multi_data = mHandler.hasMultiSelectData();
    	AdapterContextMenuInfo _info = (AdapterContextMenuInfo)info;
    	mSelectedListItem = mHandler.getData(_info.position);
    	
    	if(mHandler.getMode() != EventHandler.TREEVIEW_MODE)
    	{
    		return;
    	}
    	/* is it a directory and is multi-select turned off */
    	if(mFileMag.isDirectory(mSelectedListItem) && !mHandler.isMultiSelected()) {
    		menu.setHeaderTitle(getResources().getString(R.string.Folder_operations));
        	menu.add(0, D_MENU_DELETE, 0, getResources().getString(R.string.Delete_Folder));
        	menu.add(0, D_MENU_RENAME, 0, getResources().getString(R.string.Rename_Folder));
        	menu.add(0, D_MENU_COPY, 0, getResources().getString(R.string.Copy_Folder));
        	menu.add(0, D_MENU_MOVE, 0, getResources().getString(R.string.Move_Folder));
        	menu.add(0, D_MENU_ZIP, 0, getResources().getString(R.string.Zip_Folder));
        	menu.add(0, D_MENU_PASTE, 0, getResources().getString(R.string.Paste_into_folder)).setEnabled(mHoldingFile || 
        																 multi_data);
        	menu.add(0, D_MENU_UNZIP, 0, getResources().getString(R.string.Extract_here)).setEnabled(mHoldingZip);
    		
        /* is it a file and is multi-select turned off */
    	} else if(!mFileMag.isDirectory(mSelectedListItem) && !mHandler.isMultiSelected()) {
        	menu.setHeaderTitle(getResources().getString(R.string.File_Operations));
    		menu.add(0, F_MENU_DELETE, 0, getResources().getString(R.string.Delete_File));
    		menu.add(0, F_MENU_RENAME, 0, getResources().getString(R.string.Rename_File));
    		menu.add(0, F_MENU_COPY, 0, getResources().getString(R.string.Copy_File));
    		menu.add(0, F_MENU_MOVE, 0, getResources().getString(R.string.Move_File));
    		menu.add(0, F_MENU_ATTACH, 0, getResources().getString(R.string.Email_File));
    	}	
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {

    	switch(item.getItemId()) {
    		case D_MENU_DELETE:
    		case F_MENU_DELETE:
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle(getResources().getString(R.string.Warning));
    			builder.setIcon(R.drawable.warning);
    			builder.setMessage(getResources().getString(R.string.Deleting) + mSelectedListItem +
    					getResources().getString(R.string.cannot_be_undone));
    			builder.setCancelable(false);
    			
    			builder.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
    			});
    			builder.setPositiveButton(getResources().getString(R.string.Delete), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mHandler.deleteFile(mFileMag.getCurrentDir() + "/" + mSelectedListItem);
					}
    			});
    			AlertDialog alert_d = builder.create();
    			alert_d.show();
    			return true;
    			
    		case D_MENU_RENAME:
    			showDialog(D_MENU_RENAME);
    			return true;
    			
    		case F_MENU_RENAME:
    			showDialog(F_MENU_RENAME);
    			return true;
    			
    		case F_MENU_ATTACH:
    			File file = new File(mFileMag.getCurrentDir() +"/"+ mSelectedListItem);
    			Intent mail_int = new Intent();
    			
    			mail_int.setAction(android.content.Intent.ACTION_SEND);
    			mail_int.setType("application/mail");
    			mail_int.putExtra(Intent.EXTRA_BCC, "");
    			mail_int.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
    			startActivity(mail_int);
    			return true;
    		
    		case F_MENU_MOVE:
    		case D_MENU_MOVE:
    		case F_MENU_COPY:
    		case D_MENU_COPY:
    			if(item.getItemId() == F_MENU_MOVE || item.getItemId() == D_MENU_MOVE)
    				mHandler.setDeleteAfterCopy(true);
    			
    			mHoldingFile = true;
    			
    			mCopiedTarget = mFileMag.getCurrentDir() +"/"+ mSelectedListItem;
    			mDetailLabel.setText(getResources().getString(R.string.Holding) + mSelectedListItem);
    			return true;
    			
    		
    		case D_MENU_PASTE:
    			boolean multi_select = mHandler.hasMultiSelectData();
    			
    			if(multi_select) {
    				mHandler.copyFileMultiSelect(mFileMag.getCurrentDir() +"/"+ mSelectedListItem);
    				
    			} else if(mHoldingFile && mCopiedTarget.length() > 1) {
    				
    				mHandler.copyFile(mCopiedTarget, mFileMag.getCurrentDir() +"/"+ mSelectedListItem);
    				mDetailLabel.setText("");
    			}
    			    			   			
    			mHoldingFile = false;
    			return true;
    			
    		case D_MENU_ZIP:
    			String dir = mFileMag.getCurrentDir();
    			
    			mHandler.zipFile(dir + "/" + mSelectedListItem);
    			return true;
    			
    		case D_MENU_UNZIP:
    			if(mHoldingZip && mZippedTarget.length() > 1) {
    				String current_dir = mFileMag.getCurrentDir() + "/" + mSelectedListItem + "/";
    				String old_dir = mZippedTarget.substring(0, mZippedTarget.lastIndexOf("/"));
    				String name = mZippedTarget.substring(mZippedTarget.lastIndexOf("/") + 1, mZippedTarget.length());
    				
    				if(new File(mZippedTarget).canRead() && new File(current_dir).canWrite()) {
	    				mHandler.unZipFileToDir(name, current_dir, old_dir);				
	    				mPathLabel.setText(current_dir);
	    				
    				} else {
    					Toast.makeText(this, getResources().getString(R.string.no_permission) + name, 
    							Toast.LENGTH_SHORT).show();
    				}
    			}
    			
    			mHoldingZip = false;
    			mDetailLabel.setText("");
    			mZippedTarget = "";
    			return true;
    	}
    	return false;
    }
    
    /* ================Menus, options menu and context menu end here=================*/
    @Override
    protected void  onPrepareDialog(int id, Dialog dialog) 
    {
    	switch(id) {
    	case MENU_MKDIR:
			TextView label = (TextView)dialog.findViewById(R.id.input_label);
			label.setText(mFileMag.getCurrentDir());
		break; 
    	}
    }
    @Override
    protected Dialog onCreateDialog(int id) {
    	final Dialog dialog = new Dialog(Main.this);
    	
    	switch(id) {
    		case MENU_MKDIR:
    			dialog.setContentView(R.layout.input_layout);
    			dialog.setTitle(getResources().getString(R.string.Create_Directory));
    			dialog.setCancelable(false);
    			
    			ImageView icon = (ImageView)dialog.findViewById(R.id.input_icon);
    			icon.setImageResource(R.drawable.newfolder);
    			
    			TextView label = (TextView)dialog.findViewById(R.id.input_label);
    			label.setText(mFileMag.getCurrentDir());
    			final EditText input = (EditText)dialog.findViewById(R.id.input_inputText);
    			
    			Button cancel = (Button)dialog.findViewById(R.id.input_cancel_b);
    			Button create = (Button)dialog.findViewById(R.id.input_create_b);
    			
    			create.setOnClickListener(new OnClickListener() {
    				public void onClick (View v) {
    					if (input.getText().length() >= 1) {
    						if (mFileMag.createDir(mFileMag.getCurrentDir() + "/", input.getText().toString()) == 0)
    							Toast.makeText(Main.this, 
    										   "Folder " + input.getText().toString() + " created", 
    										   Toast.LENGTH_LONG).show();
    						else
    							Toast.makeText(Main.this, getResources().getString(R.string.not_created), Toast.LENGTH_SHORT).show();
    					}
    					
    					dialog.dismiss();
    					String temp = mFileMag.getCurrentDir();
    					mHandler.updateDirectory(mFileMag.getNextDir(temp, true));
    				}
    			});
    			cancel.setOnClickListener(new OnClickListener() {
    				public void onClick (View v) {	dialog.dismiss(); }
    			});
    		break; 
    		case D_MENU_RENAME:
    		case F_MENU_RENAME:
    			dialog.setContentView(R.layout.input_layout);
    			dialog.setTitle(getResources().getString(R.string.Rename) + mSelectedListItem);
    			dialog.setCancelable(false);
    			
    			ImageView rename_icon = (ImageView)dialog.findViewById(R.id.input_icon);
    			rename_icon.setImageResource(R.drawable.rename);
    			
    			TextView rename_label = (TextView)dialog.findViewById(R.id.input_label);
    			rename_label.setText(mFileMag.getCurrentDir());
    			final EditText rename_input = (EditText)dialog.findViewById(R.id.input_inputText);
    			
    			Button rename_cancel = (Button)dialog.findViewById(R.id.input_cancel_b);
    			Button rename_create = (Button)dialog.findViewById(R.id.input_create_b);
    			rename_create.setText(getResources().getString(R.string.Rename));
    			
    			rename_create.setOnClickListener(new OnClickListener() {
    				public void onClick (View v) {
    					if(rename_input.getText().length() < 1)
    						dialog.dismiss();
    					
    					if(mFileMag.renameTarget(mFileMag.getCurrentDir() +"/"+ mSelectedListItem, rename_input.getText().toString()) == 0) {
    						Toast.makeText(Main.this, mSelectedListItem + getResources().getString(R.string.be_renamed) +rename_input.getText().toString(),
    								Toast.LENGTH_LONG).show();
    					}else
    						Toast.makeText(Main.this, mSelectedListItem + getResources().getString(R.string.not_renamed), Toast.LENGTH_LONG).show();
    						
    					dialog.dismiss();
    					String temp = mFileMag.getCurrentDir();
    					mHandler.updateDirectory(mFileMag.getNextDir(temp, true));
    				}
    			});
    			rename_cancel.setOnClickListener(new OnClickListener() {
    				public void onClick (View v) {	dialog.dismiss(); }
    			});
    		break;
    		
    		case SEARCH_B:
    		case MENU_SEARCH:
    			dialog.setContentView(R.layout.input_layout);
    			dialog.setTitle(getResources().getString(R.string.Search));
    			dialog.setCancelable(false);
    			
    			ImageView searchIcon = (ImageView)dialog.findViewById(R.id.input_icon);
    			searchIcon.setImageResource(R.drawable.search);
    			
    			TextView search_label = (TextView)dialog.findViewById(R.id.input_label);
    			search_label.setText(getResources().getString(R.string.Search_file));
    			final EditText search_input = (EditText)dialog.findViewById(R.id.input_inputText);
    			
    			Button search_button = (Button)dialog.findViewById(R.id.input_create_b);
    			Button cancel_button = (Button)dialog.findViewById(R.id.input_cancel_b);
    			search_button.setText(getResources().getString(R.string.Search));
    			
    			search_button.setOnClickListener(new OnClickListener() {
    				public void onClick(View v) {
    					String temp = search_input.getText().toString();
    					
    					if (temp.length() > 0)
    						mHandler.searchForFile(temp);
    					dialog.dismiss();
    				}
    			});
    			
    			cancel_button.setOnClickListener(new OnClickListener() {
    				public void onClick(View v) { dialog.dismiss(); }
    			});

    		break;
    	}
    	return dialog;
    }
    
    /*
     * (non-Javadoc)
     * This will check if the user is at root directory. If so, if they press back
     * again, it will close the application. 
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
   public boolean onKeyDown(int keycode, KeyEvent event) { 
    	String current = mFileMag.getCurrentDir();
    	
    	if(keycode == KeyEvent.KEYCODE_SEARCH) {
    		showDialog(SEARCH_B);
    		
    		return true;
    		
    	} else if(keycode == KeyEvent.KEYCODE_BACK && 
    			mHandler.getMode() == EventHandler.CATALOG_MODE) {
    		finish();
    		
    		return false;
    	} else if(keycode == KeyEvent.KEYCODE_BACK && mUseBackKey &&
    			!(mFileMag.isRoot()) ) {
    		if(mHandler.isMultiSelected()) {
    			mTable.killMultiSelect(true);
    			Toast.makeText(Main.this, getResources().getString(R.string.Multi_select_off), Toast.LENGTH_SHORT).show();
    		}
    		
    		mHandler.updateDirectory(mFileMag.getPreviousDir());
    		mPathLabel.setText(mFileMag.getCurrentDir());
    		
    		return true;
    		
    	} else if(keycode == KeyEvent.KEYCODE_BACK && mUseBackKey && 
    			mFileMag.isRoot() ) 
    	{
    		Toast.makeText(Main.this, getResources().getString(R.string.Press_back), Toast.LENGTH_SHORT).show();
    		mUseBackKey = false;
    		mPathLabel.setText(mFileMag.getCurrentDir());
    		
    		return false;
    		
    	} else if(keycode == KeyEvent.KEYCODE_BACK && !mUseBackKey && 
    			mFileMag.isRoot() ) 
    	{
    		finish();
    		
    		return false;
    	}
    	return false;
    }
}
