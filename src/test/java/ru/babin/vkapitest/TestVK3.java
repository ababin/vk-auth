package ru.babin.vkapitest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class TestVK3 {
	
	private final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.63 Safari/537.36";
	
	private final String APP_ID = "5511114";
	
	private static final String AUTH_URL = "https://oauth.vk.com/authorize?client_id={APP_ID}&scope=photos,messages&redirect_uri=https://oauth.vk.com/blank.html&display=page&v=5.34&response_type=token";
	
	private String cookies;
	private String referer;
	
	private String ip_h , lg_h, _origin, to , expire;
	
	private String url;
	
	@Test
	public void test() throws Exception{
		
		getHomeAuth();
		parse();
		
		postAuth("+79779026691", "fhntvbqqwe2015");
		
		
	}
	
		
	
	private void getHomeAuth() throws Exception {
		
		url = AUTH_URL.replace("{APP_ID}", APP_ID);
		System.out.println("URL AUTH: " + url);
 
        URL oauth = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) oauth.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " + responseCode);
 
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
 
        while((inputLine = reader.readLine()) != null)
            response.append(inputLine + "\n");
        reader.close();
        PrintWriter writer = new PrintWriter("auth.html");
        writer.print(response);
        writer.close();
        Map<String, List<String>> headers = connection.getHeaderFields();
        parse();
        cookies = prepareCookiesString(connection.getHeaderField("Set-Cookie"));
        System.out.println("COOKIES : " + cookies);
        referer = connection.getURL().toString();
    }
	
	
	private void parse() throws Exception {
        System.out.println("Started parsing");
        File f = new File("auth.html");
        Document document = Jsoup.parse(f, "UTF-8");
        Elements inputs = document.select("input");
        ip_h = findValue(inputs, "ip_h");
        lg_h = findValue(inputs, "lg_h");
        _origin = findValue(inputs, "_origin");
        to = findValue(inputs, "to");
        expire = findValue(inputs, "expire");
 
    }
 
    private String findValue(Elements elements, String name) {
        for(Element el : elements) {
            if (containsAttribute(el, name)) {
                Attributes attrs = el.attributes();
                for(org.jsoup.nodes.Attribute attribute : attrs) {
                    if(attribute.getKey().equals("value"))
                        return attribute.getValue();
                }
            }
        }
        return "";
    }
 
    private boolean containsAttribute(Element element, String value) {
        for(org.jsoup.nodes.Attribute attribute : element.attributes()) {
            if(attribute.getKey().equals("name") && attribute.getValue().equals(value))
                return true;
        }
        return false;
    }
    
    
    private void postAuth(String e, String p) throws Exception {
        String email = e;
        String password = p;
        _origin = URLEncoder.encode(_origin, "UTF-8");
        String url = "https://login.vk.com/?act=login&soft=1";
        URL post = new URL(url);
 
        HttpsURLConnection postConnection = (HttpsURLConnection) post.openConnection();
 
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        postConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        postConnection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
        postConnection.setRequestProperty("Cache-Control", "max-age=0");
        postConnection.setRequestProperty("Connection", "keep-alive");
        postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        postConnection.setRequestProperty("Host", "login.vk.com");
        postConnection.setRequestProperty("Origin", "https://oauth.vk.com");
        
        postConnection.setRequestProperty("Referer", referer);
        postConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        postConnection.setRequestProperty("User-Agent", USER_AGENT);
        
                
        //cookies += ";t=72a24e1ee3b4d8ce9575bcef";
        postConnection.setRequestProperty("Cookie", cookies);
        
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
        postConnection.setInstanceFollowRedirects(false);

 
 
        String urlParameters = "ip_h=" + ip_h +
        		"&lg_h=" + lg_h +
        		"&_origin=" + _origin + 
        		"&to=" + to + 
        		"&expire=" + expire + 
        		"&email=" + email + 
        		"&pass=" + password;
        postConnection.setDoOutput(true);
        DataOutputStream stream = new DataOutputStream(postConnection.getOutputStream());
        stream.writeBytes(urlParameters);
        stream.flush();
        stream.close();
        int responseCode = postConnection.getResponseCode();
        System.out.println("Sent post. Response code: " + responseCode);
        
        System.out.println("Response message : " + postConnection.getResponseMessage());
        
        String location = "";
        
        Map<String, List<String>> headers  =postConnection.getHeaderFields();
        for(Entry<String, List <String>> entry : headers.entrySet()){
        	if("Location".equals(entry.getKey())){
        		List<String> l = entry.getValue(); 
        		location = l.get(0);
        	}
        	System.out.println(entry.getKey() + " == " +  entry.getValue());
        }
        
        String newCookies = prepareCookiesString(postConnection.getHeaderField("Set-Cookie")); 
        if(!newCookies.isEmpty()){
        	cookies += newCookies;
        }
                
        System.out.println("==================== NEXT REQUEST ===================================");
        System.out.println("URL: " + location);
        URL redirect = new URL(location);
        
        HttpsURLConnection con = (HttpsURLConnection) redirect.openConnection();
        con.setRequestMethod("GET");
        con.setInstanceFollowRedirects(false);
        con.setRequestProperty("Cookie", cookies);
        
        con.setDoOutput(true);
        stream = new DataOutputStream(con.getOutputStream());
        stream.writeBytes(urlParameters);
        stream.flush();
        stream.close();
        responseCode = con.getResponseCode();
        System.out.println("Sent post. Response code: " + responseCode);
        
        headers  =con.getHeaderFields();
        for(Entry<String, List <String>> entry : headers.entrySet()){
        	if("Location".equals(entry.getKey())){
        		List<String> l = entry.getValue(); 
        		location = l.get(0);
        	}
        	System.out.println(entry.getKey() + " == " +  entry.getValue());
        }
        newCookies = prepareCookiesString(postConnection.getHeaderField("Set-Cookie")); 
        if(!newCookies.isEmpty()){
        	cookies += newCookies;
        }
        
        System.out.println("==================== LAST REQUEST ===================================");
        System.out.println("URL: " + location);
        redirect = new URL(location);
        
        con = (HttpsURLConnection) redirect.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Cookie", cookies);
        con.setInstanceFollowRedirects(false);
        
        con.setDoOutput(true);
        stream = new DataOutputStream(con.getOutputStream());
        stream.writeBytes(urlParameters);
        stream.flush();
        stream.close();
        responseCode = con.getResponseCode();
        System.out.println("Sent post. Response code: " + responseCode);
        
        headers  =con.getHeaderFields();
        for(Entry<String, List <String>> entry : headers.entrySet()){
        	if("Location".equals(entry.getKey())){
        		List<String> l = entry.getValue(); 
        		location = l.get(0);
        	}
        	System.out.println(entry.getKey() + " == " +  entry.getValue());
        }
        
        
        
    }
    
    
    private String prepareCookiesString(String cookiesIn){
    	List<HttpCookie> httpCookies  = HttpCookie.parse("Set-cookie: " + cookiesIn);
    	    	
    	String res = "";
    	for(HttpCookie c : httpCookies){
    		res += res.isEmpty() ? (c.getName() + "=" + c.getValue()) : ("; " + c.getName() + "=" + c.getValue()); 
    	}
    	return res;
    }
	
	
}
