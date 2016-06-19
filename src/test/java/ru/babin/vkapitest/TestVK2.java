package ru.babin.vkapitest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.builder.api.XingApi;
import org.scribe.oauth.OAuthService;

import jdk.nashorn.tools.Shell;

public class TestVK2 {
	
	// foreign application
	
	private final String APP_ID = "3140623";
	
	private final String ACCESS_TOKEN = "296074cd97b3035222c9ef3c373c3647938b77bd6d7fc8912a1c24afd064d92e22a9305ddf40a67cdf7ce";
	
	
				
	@Test
	public void test() throws IOException{
		
		VkApi api = VkApi.with(APP_ID, ACCESS_TOKEN);
		
		String result = api.find("Mavrov", 100);
		
		JSONObject ob = new JSONObject(result);
		
		JSONObject response = ob.getJSONObject("response");
		
		int count = response.getInt("count");
		
		print(" COUNT : %s " , String.valueOf(count));
		
		JSONArray records = (JSONArray) response.get("items");
		
		Iterator <?> it = records.iterator();
		int index = 0;
		String ids  = "";
		while(it.hasNext()){
			JSONObject j = (JSONObject) it.next();
			print(" %s %s " , String.valueOf(index), j.toString());
			ids += (ids.isEmpty()) ? String.valueOf(j.getLong("id")) : "," + String.valueOf(j.getLong("id")); 
			index++;
		}
		
		print("==========================================");
		
		String fields = "bdate,career,city,country,education,photo_100,sex,universities";
		
		result = api.getInfoByUserIds(ids, fields);
		print(result);
		
		
		
		
	}
	
	/*
	private void printMap(Map map){
		print("{");
		for(Entry entry : map.entrySet()){
			if(entry.getValue() instanceof String){
				print(" %s = $s ; " , entry.getKey().toString() , entry.getValue().toString());
			}else{
				printMap(entry.getValue());
			}
			
		}
	}
	*/
	
	private void print( String str, String ... args){
		System.out.format(str + "\n", args);
		
	}
	
	
	
	
}
