// (c) Catchoom Technologies S.L.
// Licensed under the MIT license.
// https://raw.github.com/catchoom/android-sdk/master/LICENSE
// All warranties and liabilities are disclaimed.
package com.catchoom.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import android.util.Pair;

class CatchoomImageUtil {
	
	private static final class Config {
		private static final int PICTURE_COMPRESSION_QUALITY = 75;
		private static final int PICTURE_MIN_SIZE = 240;
	}

	/**
	 * Processes the image to optimize it for server consumption.
	 * @param imagePath Path to the original file to use.
	 * @return The image {@link File} processed.
	 */
	static ByteArrayOutputStream processPicture(String imagePath) {
		// Just query the bitmap without allocating its space in memory
		Log.d("PROFILE_SDK","start process");
		Options options = new Options();
		options.inJustDecodeBounds = true;
		Bitmap picture = BitmapFactory.decodeFile(imagePath, options);

		int sampleSize = resolveSampleSize(options.outWidth, options.outHeight);

		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
		picture = BitmapFactory.decodeFile(imagePath, options);

		ByteArrayOutputStream result =  processPicture(picture);
		Log.d("PROFILE_SDK","end process");
		return result;
	}

	/**
	 * Processes the image to optimize it for server consumption.
	 * @param imagePath Path to the original file to use.
	 * @return The image {@link File} processed.
	 */
	static ByteArrayOutputStream processPicture(Bitmap image) {
		// Compress JPEG PICTURE_COMPRESSION_QUALITY % quality and PICTURE_MIN_SIZE min size		
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			Pair<Integer, Integer> desiredSize = resolveDesiredSize(image.getWidth(), image.getHeight());
			Bitmap resized = Bitmap.createScaledBitmap(image, desiredSize.first, desiredSize.second, false);
			resized.compress(CompressFormat.JPEG, Config.PICTURE_COMPRESSION_QUALITY, outputStream);
			outputStream.close();

			return outputStream;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Resolves the sample size nearest to the desired minimal size.
	 * @param width Original width.
	 * @param height Original height.
	 * @return Optimal sample size.
	 */
	private static int resolveSampleSize(int width, int height) {
		int shortestEdge = Math.min(width, height);
		int sampleSize = 0;
		
		do {
			// Powers of 2 are more efficient when decoding
			sampleSize += 2;
		} while ((shortestEdge / (sampleSize + 2)) > Config.PICTURE_MIN_SIZE);
		
		return sampleSize;
	}

	/**
	 * Resolves the desired size using minimal size and maintaining aspect
	 * ratio.
	 * @param width Original width.
	 * @param height Original height.
	 * @return Desired size.
	 */
	private static Pair<Integer, Integer> resolveDesiredSize(int width, int height) {
		int desiredWidth = 0;
		int desiredHeight = 0;
		float resizeFactor = 1;
		if (width < height) {
			desiredWidth = Config.PICTURE_MIN_SIZE;
			resizeFactor = (float) width / Config.PICTURE_MIN_SIZE;
			desiredHeight = Math.round(height / resizeFactor);
		} else {
			desiredHeight = Config.PICTURE_MIN_SIZE;
			resizeFactor = (float) height / Config.PICTURE_MIN_SIZE;
			desiredWidth = Math.round(width / resizeFactor);
		}
		
		return new Pair<Integer, Integer>(desiredWidth, desiredHeight);
	}
}
