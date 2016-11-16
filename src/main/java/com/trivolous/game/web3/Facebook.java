package com.trivolous.game.web3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Controller;

@Controller
public class Facebook {
	protected final Log logger = LogFactory.getLog(getClass());
    // get these from your FB Dev App
    private static final String secret = "<removed>";
    // this is the same as app id
    private static final String client_id = "<removed>";  

    // set this to your servlet URL for the authentication servlet/filter
    private static final String redirect_uri = "https://www.trivolous.com/signon.html";
    private static final String canvas_uri = "https://apps.facebook.com/trivolous/";
    // for testing
    private static final String my_id = "<removed>";
    public static String getClientId() {
		return client_id;
	}

	public static String getRedirectUri() {
		return redirect_uri;
	}

	public static String getCanvasUri() {
		return canvas_uri;
	}

	public static String getPerms() {
		return perms;
	}

	/// set this to the list of extended permissions you want
    private static final String perms = "email";  // comma separated (no spaces)

//    public static String getAPIKey() {
//        return api_key;
//    }

    public static String getSecret() {
        return secret;
    }

    public static String getLoginRedirectURL() {
        return "https://graph.facebook.com/oauth/authorize?client_id=" +
            client_id + "&display=page&redirect_uri=" +
            redirect_uri+"&scope="+perms;
    }

    public static String getAuthURL(String authCode) {
        return "https://graph.facebook.com/oauth/access_token?client_id=" +
            client_id+"&redirect_uri=" +
            redirect_uri+"&client_secret="+secret+"&code="+authCode;
    }

    public static String getAppAuthURL() {
        return "https://www.facebook.com/dialog/oauth/?"+
        	"client_id=" + client_id + 
        	"&redirect_uri=" + redirect_uri+
        	"&scope=" + perms;
    }

    static String app_access_token = "";

    public static String getAppAccessToken() {
    	
    	if (app_access_token.isEmpty()) {
	    	String url = "https://graph.facebook.com/oauth/access_token?" +
	           "client_id=" + client_id +
	           "&client_secret=" + secret +
	           "&grant_type=client_credentials";
	    	
	    	String tokenjson = "";
			try {
				tokenjson = Request.Get(url).execute().returnContent().toString();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if (!tokenjson.isEmpty()) {
	    		app_access_token = tokenjson.split("=")[1];
	    	}
    	}
    	return app_access_token;
    }
    
    // returns true if accepted by FB -- note this does not mean it was sent to user.  if
    // user has disabled notifications return will still be true.  
    public static boolean sendNotification(String userId, String message) {
    	String app_access_token = getAppAccessToken();
		//    	POST /{recipient_userid}/notifications?
		//    		     access_token= … & 
		//    		     href=/& 
		//    		     template=You have people waiting to play with you, play now!
    	
    	// example here: http://hc.apache.org/httpcomponents-client-ga/quickstart.html
    	DefaultHttpClient httpclient = new DefaultHttpClient();    	
    	HttpPost httpPost = new HttpPost("https://graph.facebook.com/"+userId+"/notifications");
    	List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    	nvps.add(new BasicNameValuePair("access_token", app_access_token));
    	nvps.add(new BasicNameValuePair("href", ""));   // TODO -- use this to send use to game?
    	nvps.add(new BasicNameValuePair("template", message));
    	String fbResp ="";
    	try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			HttpResponse response2 = httpclient.execute(httpPost);
    	    //System.out.println(response2.getStatusLine());
    	    HttpEntity entity2 = response2.getEntity();
    	    // do something useful with the response body
    	    // and ensure it is fully consumed
    	    //System.out.println(EntityUtils.toString(entity2));
    	    fbResp = EntityUtils.toString(entity2);
    	    EntityUtils.consume(entity2);
    	} catch (Exception e) {
			e.printStackTrace();
		} finally {
    	    httpPost.releaseConnection();
    	    httpclient.close();
    	}
    	
    	return fbResp.contains("success") && fbResp.contains("true"); 
    	
    }
    
	private String readURL(URL url) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = url.openStream();
		int r;
		while ((r = is.read()) != -1) {
			baos.write(r);
		}
		return new String(baos.toByteArray());
	}
	
	public class FbLoginInfo {
	      public String id;
	      public String firstName;
	      public String lastName;
	      public String email;
		}
		
	public FbLoginInfo authFacebookLogin(String accessToken) {
		FbLoginInfo fbLoginInfo = null;
        try {
        	JSONObject jsonmap = (JSONObject) JSONValue.parse(
            		readURL(new URL("https://graph.facebook.com/me?access_token=" + accessToken)));
        	fbLoginInfo = new FbLoginInfo(); 
        	fbLoginInfo.id = (String)jsonmap.get("id");
        	fbLoginInfo.firstName = (String)jsonmap.get("first_name");
        	fbLoginInfo.lastName = (String)jsonmap.get("last_name");
        	fbLoginInfo.email = (String)jsonmap.get("email");
        	logger.info("Facebook login : id=" + fbLoginInfo.id + " fname=" + fbLoginInfo.firstName + " lastname=" + 
        			fbLoginInfo.lastName + " email=" + fbLoginInfo.email);
        } catch (Throwable ex) {
            logger.warn("Facebook login failed for token " + accessToken);
        }
        return fbLoginInfo;
    }    
    
    public static void main(String[] args) {

    	System.out.println(getAppAccessToken());

    	sendNotification(my_id, "Hello from test stub!");
	}
}