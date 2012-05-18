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
import java.lang.ref.SoftReference;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import android.media.ExifInterface;

public class ThumbnailCreator {
	private int mWidth;
	private int mHeight;
	private ArrayList<Bitmap> mCacheBitmap;
	private SoftReference<Bitmap> mThumb;

	public ThumbnailCreator(int width, int height) {
		mWidth = width;
		mHeight = height;
		mCacheBitmap = new ArrayList<Bitmap>(100);
	}
	
	public Bitmap hasBitmapCached(int index) {
		if(mCacheBitmap.isEmpty())
			return null;
		
		try {
			return mCacheBitmap.get(index);
			
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public void clearBitmapCache() {
		mCacheBitmap.clear();
	}
	
	public void setBitmapToImageView(final String imageSrc, 
									 final Handler handle, 
									 final ImageView icon,
									 final boolean isJPG,
									 final int index) {

		Thread thread = new Thread() {
			public void run() {
				synchronized (this) {
					if(isJPG) {
						try{
							ExifInterface mExif = new ExifInterface(imageSrc);
							if(mExif != null) {
								byte[] thumbData = mExif.getThumbnail();
								if(thumbData == null)
								{
									BitmapFactory.Options options = new BitmapFactory.Options();
							        options.inJustDecodeBounds = true;
							        Bitmap mBitmap;
							        mBitmap = BitmapFactory.decodeFile(imageSrc, options); 

							        int be = (int)( Math.max(options.outWidth,options.outHeight) / 64);
							        if (be <= 0)
							            be = 1;
							        options.inSampleSize = be;
							        
							        options.inJustDecodeBounds = false;
							        
							        mBitmap=BitmapFactory.decodeFile(imageSrc,options);
							        mThumb = new SoftReference<Bitmap>(Bitmap.createScaledBitmap(
					 						  mBitmap,
					 						  mWidth,
					 						  mHeight,
					 						  false));
								}
								else
								{
								mThumb = new SoftReference<Bitmap>(Bitmap.createScaledBitmap(
				 						  BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length),
				 						  mWidth,
				 						  mHeight,
				 						  false));
								}
							} 
						}
						catch (IOException e){
							Log.d(imageSrc,"can't get exif");
						}
					}
					else {
						BitmapFactory.Options options = new BitmapFactory.Options();
				        options.inJustDecodeBounds = true;
				        Bitmap mBitmap;
				        mBitmap = BitmapFactory.decodeFile(imageSrc, options); 

				        Log.d(imageSrc, String.valueOf(options.outWidth) + ":" + String.valueOf(options.outHeight) + "is not a jpg file");
				        int be = (int)( Math.max(options.outWidth,options.outHeight) / 64);
				        if (be <= 0)
				            be = 1;
				        options.inSampleSize = be;
				        
				        options.inJustDecodeBounds = false;
				        
				        mBitmap=BitmapFactory.decodeFile(imageSrc,options);
				        mThumb = new SoftReference<Bitmap>(Bitmap.createScaledBitmap(
		 						  mBitmap,
		 						  mWidth,
		 						  mHeight,
		 						  false));
					}
					//mCacheBitmap.add(index,mThumb.get());
					int size = mCacheBitmap.size();
					int i;
					if(size <= index){
						for (i=size;i<=index;i++) {
							mCacheBitmap.add(null);
						}
					}
					mCacheBitmap.set(index,mThumb.get());
					Log.d("Thumbnail",String.valueOf(index) + ":set index success");
					final Bitmap src = mThumb.get();
					
					handle.post(new Runnable() {
						public void run() {
							icon.setImageBitmap(src);
						}
					});
				}
			}
		};
		
		thread.start();
	}
}