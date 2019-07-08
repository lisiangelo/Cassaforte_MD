
package com.example.j940549.cassaforte_md.UsoPhotoCamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.MainActivity;
import com.example.j940549.cassaforte_md.PwFinanza.FragmentPwFinanziarie;
import com.example.j940549.cassaforte_md.PwLavoro.PwBancheDati.FragmentPwBancheDati;
import com.example.j940549.cassaforte_md.PwLavoro.PwGestori.FragmentPwGestori;
import com.example.j940549.cassaforte_md.PwLavoro.PwLavoroGen.FragmentPwLavoroGen;
import com.example.j940549.cassaforte_md.PwPersonale.FragmentPwPersonali;
import com.example.j940549.cassaforte_md.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class PhotoIntentActivity extends AppCompatActivity {

	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_TAKE_GALLERY = 2;
	private boolean permessiOK;
	private String user;
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	private String mCurrentPhotoPath;
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private String tipoPassword;
	private String nomeApplicazione;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_intent);
		tipoPassword = getIntent().getExtras().getString("tipoPassword");
		nomeApplicazione= getIntent().getExtras().getString("nomeApplicazione");
		user = getIntent().getExtras().getString("user");
		Log.d("nomeApp",nomeApplicazione);
		mImageView= (ImageView) findViewById(R.id.imageView1);

		ImageButton photoBtn = (ImageButton) findViewById(R.id.btnPhoto);
		ImageButton galleryBtn = (ImageButton) findViewById(R.id.btnGallery);
		permessiOK=false;
		galleryBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {

				controllaPermessi();
				if(permessiOK==true) {
					Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(in, ACTION_TAKE_GALLERY);
					//       finish();
				}else{
					Toast.makeText(PhotoIntentActivity.this, "L'applicazione non ha i permessi per proseguire", Toast.LENGTH_SHORT).show();
				}
			}
		});
		photoBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				controllaPermessi();
				if(permessiOK==true) {
				dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
				}else{
					Toast.makeText(PhotoIntentActivity.this, "L'applicazione non ha i permessi per proseguire", Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {// && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();

		}else{
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
	}

	/* Photo album for this application */
	/*private String getAlbumName() {
		return  getString(R.string.album_name)+"/"+tipoPassword;
	}*/


	/*private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

//			storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+getAlbumName());
			Log.d("file storagedir1",storageDir.getAbsolutePath());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d(" file directoriAlbum", "failed to create directory");
						Toast.makeText(this, "directoriAlbum fallita", Toast.LENGTH_SHORT).show();
						return null;
					}
				}
			}
			Log.d("file storagedir2",storageDir.getAbsolutePath());
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
			Toast.makeText(this, "External storage is not mounted READ/WRITE.", Toast.LENGTH_SHORT).show();

		}
		Log.d("file storagedir3",storageDir.getAbsolutePath());


		return storageDir;


	}
*/

	private File createImageFile() throws IOException {
		// Create an image file name

//		File albumF = getAlbumDir();
//      Log.d("image file album",albumF.getAbsolutePath());

		File imageF = new File( getBaseContext().getExternalFilesDir(null), nomeApplicazione+JPEG_FILE_SUFFIX);
		Log.d("image file patch",imageF.getAbsolutePath());

		return imageF;
	}


	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		switch (tipoPassword){
			case "FINANZA":{
				FragmentPwFinanziarie.imageAggiunte.add(mCurrentPhotoPath);
				break;
			}
			case "PERSONALE":{
				FragmentPwPersonali.imageAggiunte.add(mCurrentPhotoPath);
				break;
			}
			case "BANCHE_DATI":{
				FragmentPwBancheDati.imageAggiunte.add(mCurrentPhotoPath);
				break;
			}
			case "GESTORI":{
				FragmentPwGestori.imageAggiunte.add(mCurrentPhotoPath);
				break;
			}
			case "AZIENDALE":{
				FragmentPwLavoroGen.imageAggiunte.add(mCurrentPhotoPath);
				break;
			}
		}
		return f;
	}

	private void setPic() {
/*
		// There isn't enough memory to open up more than a couple camera photos
		// So pre-scale the target bitmap into which the file is decoded

		// Get the size of the ImageView
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		// Get the size of the image
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Figure out which way needs to be reduced less
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);
		}

		// Set bitmap options to scale the image decode target
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		// Decode the JPEG file into a Bitmap

*/		Bitmap bitmaporig=BitmapFactory.decodeFile(mCurrentPhotoPath);
        Bitmap bitmap = Bitmap.createScaledBitmap(bitmaporig,50,50,true);//BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		Toast.makeText(this, "pic", Toast.LENGTH_SHORT).show();
		// Associate the Bitmap to the ImageView
		mImageView.setImageBitmap(bitmap);
		//mImageView.setVisibility(View.VISIBLE);
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		Toast.makeText(this, "galleriaddpicc", Toast.LENGTH_SHORT).show();
		this.sendBroadcast(mediaScanIntent);
	}


	private void dispatchTakePictureIntent(int actionCode) {


		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
			case ACTION_TAKE_PHOTO_B:
				File f = null;

				try {
					f=setUpPhotoFile();
					 //f = new File(mCurrentPhotoPath);
					mCurrentPhotoPath = f.getAbsolutePath();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

						Uri photoURI = FileProvider.getUriForFile(getBaseContext(), getApplicationContext().getPackageName() + ".provider", f);
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);}
					else{
						takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
						Toast.makeText(this, mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
					}

				} catch (IOException e) {
					e.printStackTrace();
					f = null;
					mCurrentPhotoPath = null;
				}
				break;

			default:
				break;
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
		//finish();
	}

	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("azione", ""+requestCode);
		switch (requestCode) {

			case ACTION_TAKE_PHOTO_B: {
				Log.d("azione", ""+requestCode);
				if (resultCode == RESULT_OK) {
					handleBigCameraPhoto();
					ritornaActionChiamante();
				}
				break;
			} // ACTION_TAKE_PHOTO_B
			case ACTION_TAKE_GALLERY: {
				Log.d("azione", ""+ACTION_TAKE_GALLERY);
				//  Toast.makeText(this, "image rom gallery"+mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
				if (resultCode == RESULT_OK) {
					//           Toast.makeText(this, "image rom gallery"+mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
					Uri contentUri=data.getData();
					String [] proj={MediaStore.Images.Media.DATA};
					Cursor cursor=managedQuery(contentUri,proj,null,null,null);
					int colum_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					mCurrentPhotoPath=cursor.getString(colum_index);
					String pathorig=mCurrentPhotoPath;

					Log.d("image rom gallery", pathorig);
					File dest=null;
					if (pathorig!= null) {
						try {
							dest=setUpPhotoFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
						String pathchdest=dest.getAbsolutePath();

						Log.d("image rom gallery 2", pathchdest);
						try {
							copyFile(pathorig,pathchdest);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						mCurrentPhotoPath = null;

						finish();
						ritornaActionChiamante();
					}

				}
				break;
			} // ACTION_TAKE_PHOTO_B
		} // switch
	}
	private void copyFile(String patchOrig,String patchDest) throws IOException {
		File inputFile= new File(patchOrig);
		File outFile=new File(patchDest);
		Log.d("file dest", outFile.getAbsolutePath());
		try {

			FileChannel src = new FileInputStream(inputFile).getChannel();
			FileChannel dst = new FileOutputStream(outFile).getChannel();
			dst.transferFrom(src, 0, src.size());
			src.close();
			dst.close();

			Toast.makeText(getBaseContext(), "Import Successful!", Toast.LENGTH_SHORT).show();


		} catch (Exception e) {
			Log.d("image rom gallery ",e.toString());
			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}

	}

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);

	}



public void controllaPermessi(){
	if (ContextCompat.checkSelfPermission(PhotoIntentActivity.this,
			Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


		ActivityCompat.requestPermissions(PhotoIntentActivity.this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
				ACTION_TAKE_GALLERY);
	}
	else{
		permessiOK=true;
	}
	if (ContextCompat.checkSelfPermission(PhotoIntentActivity.this,
			Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

		ActivityCompat.requestPermissions(PhotoIntentActivity.this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
				ACTION_TAKE_PHOTO_B);

	}else{
		permessiOK=true;
	}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

		switch (requestCode) {

			case ACTION_TAKE_GALLERY: {

 // If request is cancelled, the result arrays are empty.

				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					Toast.makeText(this, "permesso accordato", Toast.LENGTH_SHORT).show();
					permessiOK=true;

				} else {
					Toast.makeText(this, "permesso negato", Toast.LENGTH_SHORT).show();
					permessiOK=false;
				}
				break;
			}

			case ACTION_TAKE_PHOTO_B: {
		 // If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
				 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					Toast.makeText(this, "permesso accordato", Toast.LENGTH_SHORT).show();
					permessiOK=true;
				} else {

			 Toast.makeText(this, "permesso negato", Toast.LENGTH_SHORT).show();
			 permessiOK=false;
				}
				break;
			}
		}
	}
	public void ritornaActionChiamante() {
		Intent vaiaActivityChiamante=null;
		switch (tipoPassword) {
			case "FINANZA": {
				vaiaActivityChiamante = new Intent(this, MainActivity.class);
				vaiaActivityChiamante.putExtra("qualeFragment","pwFinanziarie");

				break;
			}
			case "PERSONALE": {
				vaiaActivityChiamante = new Intent(this, MainActivity.class);
				vaiaActivityChiamante.putExtra("qualeFragment","pwPersonali");
				break;
			}
			case "BANCHE_DATI": {
				vaiaActivityChiamante = new Intent(this, MainActivity.class);
				vaiaActivityChiamante.putExtra("qualeFragment","pwLavoro");
				break;
			}
			case "GESTORI": {
				vaiaActivityChiamante = new Intent(this, MainActivity.class);
				vaiaActivityChiamante.putExtra("qualeFragment","pwLavoro");
				break;
			}
			case "AZIENDALE": {
				vaiaActivityChiamante = new Intent(this, MainActivity.class);
				vaiaActivityChiamante.putExtra("qualeFragment","pwLavoro");
				break;
			}
		}
		vaiaActivityChiamante.putExtra("user",user);
		vaiaActivityChiamante.putExtra("isffpp", MainActivity.isffpp);
		startActivity(vaiaActivityChiamante);
		finish();
		}

	}
