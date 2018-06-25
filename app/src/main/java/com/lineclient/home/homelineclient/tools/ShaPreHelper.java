package com.lineclient.home.homelineclient.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.lineclient.home.homelineclient.contants.XMLContants;

public class ShaPreHelper {

	public static void writeShaPre(String XMLName,String key,String msg,Context context){
		//实例化SharedPreferences对象（第一步）
		SharedPreferences mySharedPreferences=context.getSharedPreferences(XMLName,Activity.MODE_PRIVATE);
		//实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		//用putString的方法保存数据
		editor.putString(key,msg);
		//提交当前数据
		editor.commit(); 
	}

	public static String readShaPre(String XMLName,String key,Context context){
		//同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
		SharedPreferences sharedPreferences=context.getSharedPreferences(XMLName,Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		return sharedPreferences.getString(key, "");
	}

	//SharedPreferences加密写入
	public static void writeShaPreCrypt(String XMLName,String key,String msg,Context context,String aesKey){
		try {
			msg=AESHelper.encryptByBase64(msg, aesKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		writeShaPre(XMLName,key,msg,context);
	}

	//SharedPreferences加密读出
	public static String readShaPreCrypt(String XMLName,String key,Context context,String aesKey){
		String msg=readShaPre(XMLName,key,context);
		try {
			return AESHelper.decryptByBase64(msg, aesKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	public static void clearFileData(Context context,String fileName)
	{

		SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();

	}

	public static void deleteAllFiles(Context context)
	{

		String[] list= XMLContants.getAllFilesName();

		for(int i=0;i<list.length;i++){

			SharedPreferences preferences = context.getSharedPreferences(list[i], Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.clear();
			editor.commit();

		}

		/*File file=new File("/data/data/" + context.getApplicationContext().getPackageName() + "/shared_prefs");
		for (File item : file.listFiles()) {
			item.delete();
		}*/

	}


}
