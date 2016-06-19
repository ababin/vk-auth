package ru.babin.vkapitest;

import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class TestVK {
	
	
	private final String APP_ID = "5511114";
	
	private final String ACCESS_TOKEN = "4c7a18e15ce5d3476ac55174057cf7805e18d4cafc077562ad7cd68d35107bc02bc887338ca49cc3d0470";
	
		
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
		
		//for(records.)
		
	
			
								
		//System.out.println(ob);
		//System.out.println(ob);
		
		
		
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
