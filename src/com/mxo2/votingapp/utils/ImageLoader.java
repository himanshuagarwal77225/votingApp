package com.mxo2.votingapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.mxo2.votingapp.webservice.MySSLSocketFactory;
import com.mxo2.votingapp.R;

/**
 * As the name tells it all, it executes Lazy Loading of an array of image files
 * given their URL. This class handles the memory and file caches, uses separate
 * thread to download file from the server using HTTPS request, decodes the
 * received image, prepares an image file(.png) from that, and saves in the
 * memory cache. Before downloading the file from the given URL, this code will
 * verify whether the required file has already been downloaded and exists in
 * the file cache or not. If so, then it will pick up the image file from the
 * cache and use it. The final image file is set as resource to the ImageView
 * within the row of the Adapter View (here, GridView).
 **/
public class ImageLoader {

	// Initialize MemoryCache
	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	Context m_Context = null;

	// Create Map (collection) to store image and image url in key value pair
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;

	// handler to display images in UI thread
	Handler handler = new Handler();

	public ImageLoader(Context context) {
		m_Context = context;
		fileCache = new FileCache(context);
		// Creates a thread pool that reuses a fixed number of threads operating
		// off a shared unbounded queue.
		executorService = Executors.newFixedThreadPool(5);
	}

	// default image show in list (Before online image download)
	final int stub_id = R.drawable.ic_launcher;

	public void DisplayImage(String url, ImageView imageView) {
		// Store image and url in Map
		imageViews.put(imageView, url);

		// Check image is stored in MemoryCache Map or not (see
		// MemoryCache.java)
		Bitmap bitmap = memoryCache.get(url);

		if (bitmap != null) {
			// if image is stored in MemoryCache Map then Show image in listview
			// row
			imageView.setImageBitmap(bitmap);
		} else {
			// queue Photo to download from url
			queuePhoto(url, imageView);
			// Before downloading image show default image
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		// Store image and url in PhotoToLoad object
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		// pass PhotoToLoad object to PhotosLoader runnable class
		// and submit PhotosLoader runnable to executers to run runnable
		// Submits a PhotosLoader runnable task for execution
		executorService.submit(new PhotosLoader(p));
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				// Check if image already downloaded
				if (imageViewReused(photoToLoad))
					return;
				// download image from web url
				Bitmap bmp = getBitmap(photoToLoad.url);

				// set image data in Memory Cache
				memoryCache.put(photoToLoad.url, bmp);

				if (imageViewReused(photoToLoad))
					return;

				// Get bitmap to display
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);

				// Causes the Runnable bd (BitmapDisplayer) to be added to the
				// message queue.
				// The runnable will be run on the thread to which this handler
				// is attached.
				// BitmapDisplayer run method will call
				handler.post(bd);

			} catch (Throwable th) {
				Log.e("Exception 0 --> ", th.toString());
			}
		}
	}

	private Bitmap getBitmap(String url) {

		File f = fileCache.getFile(url);
		// from SD cache
		// CHECK : if trying to decode file which not exist in cache return null
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// Download image file from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) imageUrl
					.openConnection();
			httpConn.setConnectTimeout(30000);
			httpConn.setReadTimeout(30000);
			httpConn.setInstanceFollowRedirects(true);

			/*
			 * SSLContext sc = SSLContext.getInstance("TLS"); sc.init(null, new
			 * TrustManager[] { new MyTrustManager() }, new SecureRandom());
			 * HttpsURLConnection
			 * .setDefaultSSLSocketFactory(sc.getSocketFactory());
			 * HttpsURLConnection.setDefaultHostnameVerifier(new
			 * MyHostnameVerifier()); httpConn = (HttpsURLConnection)
			 * imageUrl.openConnection(); httpConn.setDoInput(true);
			 * httpConn.connect();
			 */

			int responseCode = httpConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String disposition = httpConn
						.getHeaderField("Content-Disposition");
				String contentType = httpConn.getContentType();
				int contentLength = httpConn.getContentLength();
				if (contentType != null) {
					Log.v("Content-Type = ", contentType);
				}
				if (disposition != null) {
					Log.v("Content-Disposition = ", disposition);
				}
				// opens input stream from the HTTP connection
				InputStream inputStream = httpConn.getInputStream();
				// opens an output stream to save into file
				OutputStream file_outputStream = new FileOutputStream(f);

				if (contentLength != -1) {
					int bytesRead = -1;
					byte[] buffer = new byte[contentLength];
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						file_outputStream.write(buffer, 0, bytesRead);
					}
				} else {
					AppUtils.CopyStream(inputStream, file_outputStream);
				}
				file_outputStream.close();
				inputStream.close();
				Log.v("File downloaded", responseCode + "");
			} else {
				Log.v("Failed to download file", responseCode + "");
			}
			httpConn.disconnect();

			// Now file created and going to resize file with defined height
			// Decodes image and scales it to reduce memory consumption
			bitmap = decodeFile(f);
			return bitmap;

		} catch (Throwable ex) {
			Log.e("Exception 1 --> ", ex.toString());
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}

	// Decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {

		try {
			Bitmap bitmap = null;
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			if (f.exists()) {
				FileInputStream stream1 = new FileInputStream(f);
				BitmapFactory.decodeStream(stream1, null, o);
				stream1.close();
				// Find the correct scale value. It should be the power of 2.
				// Set width/height of recreated image
				final int REQUIRED_SIZE = 85;
				int width_tmp = o.outWidth, height_tmp = o.outHeight;
				int scale = 1;
				/*
				 * while(true){ if(width_tmp / 2 < REQUIRED_SIZE || height_tmp /
				 * 2 < REQUIRED_SIZE) break; width_tmp /= 2; height_tmp /= 2;
				 * scale *= 2; }
				 */
				// scale *= 2;
				// decode with current scale values
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = scale;
				FileInputStream stream2 = new FileInputStream(f);
				bitmap = BitmapFactory.decodeStream(stream2, null, o2);
				stream2.close();
			}
			return bitmap;
		} catch (FileNotFoundException e) {
			Log.e("Exception 2 --> ", e.toString());
		} catch (IOException e) {
			Log.e("Exception 3--> ", e.toString());
		}
		return null;
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		// Check url is already exist in imageViews MAP
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			// Show bitmap on UI
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		// Clear cache directory downloaded images and stored data in maps
		memoryCache.clear();
		fileCache.clear();
	}

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

}
