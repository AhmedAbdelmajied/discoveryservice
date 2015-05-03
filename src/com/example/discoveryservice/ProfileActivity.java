package com.example.discoveryservice;

import com.example.discoveryservice.BroadcastingService.MyBinder;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ProfileActivity extends Activity {

	private Button submit;
	private EditText name, email;
	private ImageView profilePic;
	private Bitmap pic;

	private BroadcastingService bService;

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder binder = (MyBinder) service;
			bService = binder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

	};

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.submit:
				ProfileManger profile = new ProfileManger();
				profile.setName(name.getText().toString());
				profile.setEmail(email.getText().toString());
				profile.setProfilePic(pic);
				bService.setProfile(profile);
				break;
			case R.id.profilepic:
				Intent pickPhoto = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(pickPhoto, 0);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		bindService(new Intent(getBaseContext(), BroadcastReceiver.class),
				connection, Service.BIND_AUTO_CREATE);

		submit = (Button) findViewById(R.id.submit);

		name = (EditText) findViewById(R.id.name);
		email = (EditText) findViewById(R.id.email);

		profilePic = (ImageView) findViewById(R.id.profilepic);

		submit.setOnClickListener(listener);
		profilePic.setOnClickListener(listener);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				String path = getRealPathFromURI(getBaseContext(),
						selectedImage);
				Log.d("path", path);
				// pic = resizeBitmap(path);
				// pic =
				// Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path),
				// 229, 229, true);
				pic = getResizedBitmap(BitmapFactory.decodeFile(path), 229, 229);
				profilePic.setImageBitmap(pic);
				// profilePic.setImageURI(selectedImage);
			}

			break;

		}
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;

	}

}
