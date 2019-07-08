package com.example.j940549.cassaforte_md.UsoPhotoCamera;

import android.os.Environment;

import java.io.File;

public final class BaseAlbumDirFactory extends AlbumStorageDirFactory {

	// Standard storage location for digital camera files
	private static final String CAMERA_DIR = "/dcim/";

	@Override
	public File getAlbumStorageDir(String albumName) {
		return new File (
				Environment.getDataDirectory()//getExternalStorageDirectory()
				+ CAMERA_DIR
				//Environment.getDataDirectory()
				+ albumName
		);
	}
}
