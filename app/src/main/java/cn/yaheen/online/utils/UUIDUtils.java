package cn.yaheen.online.utils;

import java.util.UUID;


public class UUIDUtils {

	protected UUIDUtils(){
		
	}
	
	/**
	 * 获取uuid
	 * @return
	 */
	public  static String getUuid(){
		
		UUID result= UUID.randomUUID();
		return result.toString().replace("-", "");
	}
}
