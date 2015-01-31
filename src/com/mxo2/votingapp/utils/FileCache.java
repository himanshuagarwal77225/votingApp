package com.mxo2.votingapp.utils;

import java.io.File;
import android.content.Context;

/**
 * Associated with Lazy Loading of images, this class prepares the cache
 * directory to hold downloaded images and use them for displaying. As the
 * concept of Lazy Loading goes, the cache will be scanned for a particular file
 * and if found, that file will not be downloaded again, thus preventing
 * unnecessary memory usage with heavy networking operations.
 **/

public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {

		// Find the dir at SDCARD to save cached images
		if (AppUtils.isSdPresent()) {
			// if SDCARD is mounted (SDCARD is present on device and mounted)
			cacheDir = new File(AppConstants.APP_Images_directory, "LazyList");
		} else {
			// if checking on simulator the create cache dir in your application
			// context
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()) {
			// create cache dir in your application context
			cacheDir.mkdirs();
		}
	}

	public File getFile(String url) {
		// Identify images by hashcode or encode by URLEncoder.encode.
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		return f;
	}

	public void clear() {
		// list all files inside cache directory
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		// delete all cache directory files
		for (File f : files)
			f.delete();
	}

}
