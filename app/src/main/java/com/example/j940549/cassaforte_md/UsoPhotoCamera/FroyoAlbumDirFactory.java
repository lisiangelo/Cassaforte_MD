package com.example.j940549.cassaforte_md.UsoPhotoCamera;

import android.os.Environment;

import java.io.File;

public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

	@Override
	public File getAlbumStorageDir(String albumName) {
		// TODO Auto-generated method stub
		return new File(
		 // Environment.getExternalStoragePublicDirectory(
		 //   Environment.DIRECTORY_PICTURES),
		  Environment.getDataDirectory(),
				albumName
		);
	}
}
