package com.martin.ads.testopencv;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

	private static final int REQUEST_PERMISSION_CODE = 233;
	private static final String TAG = "HomeActivity";

	/**
	 * 需要进行检测的权限数组
	 */
	protected String[] requestPermissions = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA
	};

	private boolean isRequesting = false;
	private final List<String> mMissingPermissions = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String[] needPermissions = checkNeedPermission(requestPermissions);
		if (needPermissions == null || needPermissions.length <= 0) {
			isRequesting = false;
			init();
		} else {
			isRequesting = true;
			requestNeedPermission(needPermissions);
		}
       /* if(checkPermission(needPermissions,REQUEST_PERMISSION))
            init();*/
	}

	private void init() {
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	/**
	 * 请求必要的权限
	 *
	 * @param permissions 需要的权限
	 */
	private void requestNeedPermission(String[] permissions) {
		if (Build.VERSION.SDK_INT >= 23 && permissions != null && permissions.length > 0) {
			Log.d(TAG, "App is requesting for permissions...");
			ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
		}
	}

	/**
	 * 检查App是否拥有需要的权限，并返回需要请求的权限数据
	 *
	 * @param needPermissions 需要check的权限
	 * @return 权限数组（还没有权限的数组）可能为空
	 */
	public String[] checkNeedPermission(String[] needPermissions) {

		if (needPermissions == null || needPermissions.length <= 0) {
			return null;
		}

		final List<String> needRequestPermissionList = new ArrayList<>();

		for (String permission : needPermissions) {
			if (!hasPermission(this, permission)) {
				needRequestPermissionList.add(permission);
				Log.d(TAG, "App has no " + permission + ", add it to request permission list!");
			} else {
				Log.d(TAG, "App has " + permission + "!");
			}
		}

		String[] tmpNeedRequestPermissions = null;

		if (!needRequestPermissionList.isEmpty()) {
			tmpNeedRequestPermissions = new String[needRequestPermissionList.size()];
			int i = 0;
			for (String permission : needRequestPermissionList) {
				tmpNeedRequestPermissions[i++] = permission;
			}
		}

		return tmpNeedRequestPermissions;

	}

	/**
	 * 检查是否有权限
	 *
	 * @param activity   检查权限所在的Activity
	 * @param permission 某个需要请求的权限
	 * @return 是否有权限
	 */
	public static boolean hasPermission(@NonNull Activity activity, String permission) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_DENIED;
		}
		return true;
	}

    /*private boolean checkPermission(String[] permissions,int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    showHint("Camera and SDCard access is required, please grant the permission in settings.");
                    finish();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{
                                    permission,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            requestCode);
                }
                return false;
            }else return true;
        }
        return true;
    }*/

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (isRequesting) {
			Log.d(TAG, "onRequestPermissionsResult...");
			if (requestCode == REQUEST_PERMISSION_CODE && grantResults.length > 0) {
				int i = 0;
				for (String permission : permissions) {
					final int grantResult = grantResults[i++];
					if (!TextUtils.isEmpty(permission)) {
						if (grantResult == PackageManager.PERMISSION_GRANTED) {
							Log.i(TAG, permission + " is granted!");
							mMissingPermissions.remove(permission);
						} else {
							Log.w(TAG, permission + " is no grant!");
							mMissingPermissions.add(permission);
						}
					} else {
						Log.d(TAG, "onPermissionResult...permission--->null");
					}
				}
				isRequesting = false;
				if (mMissingPermissions.isEmpty()) {
					init();
				} else {
					showHint("Camera and SDCard access is required, please grant the permission in settings.");
					finish();
				}
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }else {
                    showHint("Camera and SDCard access is required, please grant the permission in settings.");
                    finish();
                }
                break;
            default:
                finish();
                break;
        }
    }*/

	private void showHint(String hint) {
		Toast.makeText(this, hint, Toast.LENGTH_LONG).show();
	}
}
