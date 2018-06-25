package com.lineclient.home.homelineclient.activity;

import com.lineclient.home.homelineclient.tools.AESHelper;

public class test {

	public static void main() {
		// TODO Auto-generated method stub
		try {
			
			String aeskey ="02A7C51501CB2370A2DA97067E8425A2";
			String data="MjMwMjM0QzI4OUVEQzM1RTM4MDhFNDYyM0VFNThGMUNBRUIzMTQxQ0NDMTYxQUEzRUYyNDMwRDM1OTAwMDkwQjI2OUEyMzY1RkIyRkUyN0IzQzkwQTE0NTEyMTg2ODA0QzA2REUzNjE5N0FBNzNDRjcxQTJBRUYxRjUzNzMwNzAwNDY5MUNDMUU1MjAzRkI3N0QyRkE0MEQ5RDI1NkJDMjBGREEwMDkwRDdCNzY5MzhFRTlFQkJDNDIyMkRBNkFE";
			
			//String aeskey = new String(AESHelper.toHex(AESHelper.initKey()));
			//String data="sjkdjfksjdkfjskfkskdfkdsdfkskdf";
			System.out.println("AESHelper:"+data);
			/*String en=AESHelper.encryptByBase64(data, aeskey);
			System.out.println("en:"+en);*/
			String de= AESHelper.decryptByBase64(data,aeskey);
			System.out.println("AESHelper:"+de);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
