package notefy;

//Include the Dropbox SDK.
import com.dropbox.core.*;

import java.io.*;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class Authentication {
/*	static final String APP_KEY = "b4rrsv9qn3zgbj2";
    static final String APP_SECRET = "524a39aq6d194y6";*/
    static final String APP_KEY = "plquakrf1h9nx7t";
    static final String APP_SECRET = "a0ohup3pzt86gxg";
    static DbxClient client;
    static DbxAuthFinish authFinish;
    static String accessToken;
    static HttpServletRequest request;
	static HttpServletResponse response;
	static HttpSession session;
	static String sessionKey;
	static DbxSessionStore csrfTokenStore;
	static DbxWebAuth webAuth;
	static String authorizeUrl;
	static DbxRequestConfig config;
	
    public static void printInformation(DbxClient client) throws IOException, DbxException {
		System.out.println("Linked account: " + client.getAccountInfo().displayName);
		System.out.println("APP_KEY: "+APP_KEY);
		System.out.println("APP_SECRET: "+APP_SECRET);
		System.out.println("Ahutorization code: "+authFinish);
		System.out.println("Access Token: "+accessToken);
		System.out.println("Request : " +request);
		System.out.println("webAuth:" +webAuth);
		System.out.println("authorizeUrl:" +authorizeUrl);
	}
    
 /** Original Authenticate Method
  *    public static DbxClient authorizeUser() throws IOException, DbxException {
        // Get your app key and secret from the Dropbox developers website.
		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
            Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        // Have the user sign in and authorize your app.
        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

        // This will fail if the user enters an invalid authorization code.
        authFinish = webAuth.finish(code);
        accessToken = authFinish.accessToken;
        client = new DbxClient(config, accessToken);
		return client;
	}
*/
     
    public static String authorizeUser(DbxStandardSessionStore csrfTokenStore) throws IOException, DbxException {
        try {
    	/*ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        
    	request = attr.getRequest() ;
        session = request.getSession(true);
        sessionKey = "dropbox-auth-csrf-token";
        csrfTokenStore = new DbxStandardSessionStore(session, sessionKey);
    	*/
    	// Get your app key and secret from the Dropbox developers website.
		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        config = new DbxRequestConfig("Notefy/1.0", Locale.getDefault().toString());
        webAuth = new DbxWebAuth(config, appInfo, "http://localhost:8080/userauthorizationcallback",csrfTokenStore);

        // Have the user sign in and authorize your app.
        String authorizeUrl = webAuth.start();
        }
        catch(Exception e)
        {
        	
        	System.out.println("Exception"+e);
        }
        
       System.out.println("authorizeUrl:" +authorizeUrl);
       return "redirect:"+authorizeUrl;
	} 
    
    public static String finishUserAuthorization() throws IOException, DbxException {
    	try {
   		 authFinish = webAuth.finish(request.getParameterMap());
   		 accessToken =  authFinish.accessToken;
   	 }
   	catch(DbxWebAuth.BadRequestException ex)
   	 {
   		System.out.println(ex);
   		
   	 }
   	 catch (DbxWebAuth.BadStateException ex) {

   		 System.out.println(ex);
   	 }
   	 catch (DbxWebAuth.CsrfException ex) {

   		 System.out.println(ex);
   	 }
   	 catch (DbxWebAuth.NotApprovedException ex) {

   		 System.out.println(ex);

   	 }
   	 catch (DbxWebAuth.ProviderException ex) {

   		 System.out.println(ex);
   	 }
   	 catch (DbxException ex) {

   		 System.out.println(ex);
   	 }  	 
   	
    DbxClient client = new DbxClient(config, accessToken);
    printInformation(client); 
    
    return "redirect:home";
    }
}
