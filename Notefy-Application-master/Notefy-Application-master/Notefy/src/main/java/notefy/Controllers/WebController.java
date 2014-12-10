package notefy.Controllers;
import notefy.DatabaseSource;
import core.UserRegister;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.validation.Valid;
import javax.ws.rs.*;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.dropbox.core.*;

//import notefy.Authentication;
import notefy.core.*;

import org.json.simple.*;
import org.springframework.jdbc.core.JdbcTemplate;
@Controller
public class WebController extends WebMvcConfigurerAdapter {
	
    static HttpServletRequest request;
	static HttpServletResponse response;
	static final String APP_KEY = "plquakrf1h9nx7t";
    static final String APP_SECRET = "a0ohup3pzt86gxg";
    static DbxClient client;
    static DbxAuthFinish authFinish;
    static String accessToken;
	static HttpSession session;
	static String sessionKey;
	static DbxSessionStore csrfTokenStore;
	static DbxWebAuth webAuth;
	static String authorizeUrl;
	static DbxRequestConfig config = new DbxRequestConfig("Notefy/1.0", Locale.getDefault().toString());;
	static DataSource dataSource = DatabaseSource.dataSource();
	 JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	private String userName ;
	private DbxAppInfo appInfo;
	
	
	
	@RequestMapping("/skeleton")
	  public String templates(Person person, Model model) {
	    return "skeleton";
	  }
	
	// This is how you return dynamically generated content from WS. 
	// This page is present under src/main/resources/templates.
	// This content cannot be accessed directly from browser.
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	  public String loginPerson( Model model) {
		
	    return "login";
	  }
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	  public String loginValidate(@Valid Person person, BindingResult bindingResult, Model model) throws IOException, DbxException {
	
		
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    userName = user.getUsername();
		
		System.out.println("USerEmailId:"+userName);
		
	    String sql = "select accesstoken from users where username ='" +userName +"'";
	    accessToken =  (String) jdbcTemplate.queryForObject(sql, String.class); 
	       
	    System.out.println("savedAccesstoken:"+accessToken);
		
	    if(accessToken!=null)
	    {
	    	
	    
	    	
	    	try
	    	{
	    	   client = new DbxClient(config, accessToken);
	    	   client.createFolder("/Notes");
	    	}
	    	catch(Exception e)
	    	{
	    		System.out.println("Token expired. Need to reauthenticate.");
	    		
	    		try{ 
	    			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	    			request = attr.getRequest() ;
	    	        session = request.getSession(true);
	    	        sessionKey = session.getId();
	    	        System.out.println("SessionKey1:"+sessionKey);
	    	        csrfTokenStore = new DbxStandardSessionStore(session, sessionKey);
	    	        // Get your app key and secret from the Dropbox developers website.
	    			 appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
	    	        webAuth = new DbxWebAuth(config, appInfo, "http://localhost:8080/userauthorizationcallback",csrfTokenStore);
	    	        // Have the user sign in and authorize your app.
	    	        authorizeUrl = webAuth.start();
	    			}
	    		
	    		catch(Exception ex)
		        {
		        	
		        	System.out.println("Exception"+ex);
		        }
	    		
	    		
	    		return "redirect:"+authorizeUrl;
	    	}
	    	
	    	return "home";
	    }
		
	    else
	    {
	    	
			try{ 
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			request = attr.getRequest() ;
	        session = request.getSession(true);
	        sessionKey = session.getId();
	        System.out.println("SessionKey1:"+sessionKey);
	        csrfTokenStore = new DbxStandardSessionStore(session, sessionKey);
	        // Get your app key and secret from the Dropbox developers website.
			 appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
	        webAuth = new DbxWebAuth(config, appInfo, "http://localhost:8080/userauthorizationcallback",csrfTokenStore);
	        // Have the user sign in and authorize your app.
	        authorizeUrl = webAuth.start();
			}
			
			catch(Exception e)
	        {
	        	
	        	System.out.println("Exception"+e);
	        }
	        System.out.println("authorizeUrl:" +authorizeUrl);
	        return "redirect:"+authorizeUrl;
	        
	        
	    }
       
	  }
	
	@RequestMapping(value = "/userauthorizationcallback", method = RequestMethod.GET)
    public String callback() throws IOException, DbxException {
		
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		request = attr.getRequest() ;
        session = request.getSession(true);
        sessionKey = session.getId();
        System.out.println("SessionKey2:"+sessionKey);
		
		
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
	   		return "redirect:login";

	   	 }
	   	 catch (DbxWebAuth.ProviderException ex) {

	   		 System.out.println(ex);
	   	 }
	   	 catch (DbxException ex) {

	   		 System.out.println(ex);
	   	 }  	 
	   	
	    client = new DbxClient(config, accessToken);
	   // printInformation(client);
	 
	    
	    try
	 		{	
	 		
	 		System.out.println("ACCESSTOKEN:"+accessToken+"USERNAME:"+userName);
	 		jdbcTemplate.update("UPDATE users set accesstoken = ? where username =?", accessToken,userName);
	 		}
	 		
	 		catch(Exception e)
	 		
	 		{
	 			//jdbcTemplate.rollback();
	 			System.out.println(e);
	 			return "register";
	 		}
	    client.createFolder("/Archive");
	    client.createFolder("/Trash");
	    client.createFolder("/Notes");
	    
	    return "redirect:sticky";
		//return Authentication.finishUserAuthorization();   	 
    }
	
	
	@RequestMapping(value = "/sticky", method = RequestMethod.GET)
	  public String redirect(Model model) {
	    return "redirect:home";
	  }
	@RequestMapping(value="/register", method=RequestMethod.GET)
	  public String userRegister(UserRegister userregister,Model model) {
		model.addAttribute("userregister", new UserRegister()); 
		
		System.out.println("REGISTERget");
		return "register";
	  }
      
      @RequestMapping(value="/register", method=RequestMethod.POST)
	  public String registerUserValidate(@Valid UserRegister userregister, BindingResult bindingResult, Model model) throws IOException, DbxException {
	//	System.out.println("REGISTERpost");
		//System.out.println("email:"+userregister.getEmail());
		
	/*	if (bindingResult.hasErrors()) {
			System.out.println("email:"+userregister.getEmail());
			System.out.println("Found errors");
			model.addAttribute("userregister", userregister); 
			return "register";
		}*/
		
		//System.out.println("password:"+userregister.getPassword());
		
		
		try
		{	
		
		jdbcTemplate.update("INSERT INTO users(username,password,fullname,accesstoken,enabled) VALUES (?,?,?,?,?)",userregister.getEmail(), userregister.getPassword(),userregister.getName(),null,true);
		jdbcTemplate.update("INSERT INTO user_roles(username, role) VALUES (?,?)",userregister.getEmail(), "ROLE_USER");
		
		
		}
		
		catch(Exception e)
		
		{
			//jdbcTemplate.rollback();
			System.out.println(e);
			return "register";
		}
		
		
		return "redirect:login";
	}
	@Consumes({"application/xml", "application/json","text/html"})
	@Produces({"text/html"})
	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(value="/save", method = RequestMethod.POST)
	public @ResponseBody JSONObject uploadFile(@RequestBody JSONObject o) throws Exception {
	  
	  FileInputStream inputStream = null;
      Date d = null;
      String filename = null;
      
      String title_new = o.get("title").toString();
      String content_new = o.get("content").toString();
	  String color_new = o.get("color").toString();
	  String folder = o.get("folder").toString();
	  int note_id = (int)o.get("note_id");
	  String sql = "select max(note_id) from notes";
	  
	  
	  
	  if(note_id == 0)
	  {
		 if((jdbcTemplate.queryForObject(sql, Integer.class)) ==null)
			 note_id= 1;
		 else 
		 { 
			note_id =  (int) jdbcTemplate.queryForObject(sql, Integer.class); 
		   System.out.println("noteid:"+note_id); 
			note_id+=1;
		 }
		 
		 filename = note_id+"-"+color_new+"-"+title_new+".txt";
		 jdbcTemplate.update("INSERT INTO notes(user_name,color,filename,title) VALUES (?,?,?,?)",userName,color_new,filename,title_new);
	  }
	  else{
		  filename = note_id+"-"+color_new+"-"+title_new+".txt";
		  sql = "select filename from notes where note_id = "+note_id ;
		  String old_filename = jdbcTemplate.queryForObject(sql, String.class);
		  client.delete("/"+folder+"/"+old_filename+".txt");
		  jdbcTemplate.update("update notes  set color =? ,  filename = ?,  title = ?  where note_id = ?",color_new,filename,title_new,note_id);

	}
	
      System.out.println("hurray it worked!!!");
      
      try {
    	File inputFile = new File(filename);
    	FileWriter filenw = new FileWriter(inputFile.getAbsoluteFile(),false);
    	filenw.write(content_new);
    	filenw.flush();
    	filenw.close();
    	inputStream = new FileInputStream(inputFile);
    	DbxEntry.File uploadedFile = client.uploadFile("/"+folder+"/"+filename, DbxWriteMode.add(), inputFile.length(), inputStream);
    	d = uploadedFile.clientMtime;
        System.out.println("Uploaded: " + uploadedFile.toString());
      } catch(Exception e){
      	System.out.println("File upload failed");
      }
      finally {
          inputStream.close();
      }
      JSONObject obj = new JSONObject();
      obj.put("date", d.toString());
      obj.put("note_id", note_id);
      return obj;
      }
	

	@Consumes({"application/xml", "application/json","text/html"})
	@Produces({"text/html"})
	@RequestMapping(value="/deleteforever", method = RequestMethod.DELETE)
	public @ResponseBody JSONObject deleteForeverFile(@RequestBody JSONObject o) throws Exception {
	  
	  
      Date d = new Date();
      
      String note_id = o.get("note_id").toString();
      String title_new = o.get("title").toString();
      String color_new = o.get("color").toString();
      String filename = note_id+"-"+color_new+"-"+title_new+".txt";
      
      try {
    	client.delete("/Trash/"+filename);
    	System.out.println("File: "+filename+"deleted");
      } catch(Exception e){
      	System.out.println("Excception occurred: " + e.getMessage());
      }
      JSONObject obj = new JSONObject();
      obj.put("date", d.toString());
      return obj;
	}
	
	
	
	@Consumes({"application/xml", "application/json","text/html"})
	@Produces({"text/html"})
	@RequestMapping(value="/share", method = RequestMethod.POST)
	public @ResponseBody JSONObject shareFile(@RequestBody JSONObject o) throws Exception {
	  
	
      Date d = new Date();
      String path = null;
      
      String note_id = o.get("note_id").toString();
      String title_new = o.get("title").toString();
      String color_new = o.get("color").toString();
      String folder = o.get("folder").toString();
      try {
    	String filename = note_id+"-"+color_new+"-"+title_new+".txt";
    	path = client.createShareableUrl("/"+folder+"/"+filename);
    	System.out.println("File: "+filename+" shared");
      } catch(Exception e){
      	System.out.println("Excception occurred: " + e.getMessage());
      }
      JSONObject obj = new JSONObject();
      obj.put("url", path);
      return obj;
	}
	
	
	@Consumes({"application/xml", "application/json","text/html"})
	@Produces({"text/html"})
	@RequestMapping(value="/fetchnotes", method = RequestMethod.POST)
	public @ResponseBody JSONArray fetchFile(@RequestBody JSONObject o) throws Exception {
	  
	  Date d = new Date();
      DbxEntry md;
      JSONArray objs = new JSONArray();
      
      
      String folder = o.get("folder").toString();
      System.out.println(folder);
      try {
    	DbxEntry.WithChildren listing = client.getMetadataWithChildren("/"+folder);
    	System.out.println("Files in the path");
    	for(DbxEntry child : listing.children)
    	{
    		System.out.println(" "+child.name+" ");
    		File target = new File("temp.txt");
    		OutputStream out = new FileOutputStream(target);
    		InputStream in = new FileInputStream(target);
    		try{
    			md = client.getFile("/"+folder+"/"+child.name,null,out);
    			byte[] data = new byte[(int) target.length()];
    			in.read(data);
    			String content_new = new String(data, "UTF-8");
    			System.out.println(content_new);
    			JSONObject obj = new JSONObject();
    			String[] parts = child.name.split("-");
    			String note_id = parts[0];
    			String color = parts[1];
    			String note_title="";
    			for(int i=2; i< parts.length;i++)
    			{
    				 note_title +=parts[i];
    			}
    			
    			obj.put("note_id", note_id);
    			obj.put("note_color", color);
    			obj.put("note_title", note_title);
    			obj.put("note_content", content_new);
    		    objs.add(obj);
    		   
    		}finally{
    			out.close();
    			in.close();
    			target.delete();
    		}
    	}
      } catch(Exception e){
      	System.out.println("Excception occurred: " + e.getMessage());
      }
      System.out.println(objs);
      return objs;
	}
	
	@Consumes({"application/xml", "application/json","text/html"})
	@Produces({"text/html"})
	@RequestMapping(value="/move", method = RequestMethod.POST)
	public @ResponseBody JSONObject moveFile(@RequestBody JSONObject o) throws Exception {
	  
	  
      Date d = new Date();
    
      String note_id = o.get("note_id").toString();
      String title_new = o.get("title").toString();
      String color_new = o.get("color").toString();
      String source = o.get("source").toString();
      String dest = o.get("dest").toString();
      
      try {
    	String filename = note_id+"-"+color_new+"-"+title_new+".txt";
    	client.move("/"+source+"/"+filename, "/"+dest+"/"+filename);
    	System.out.println("File: "+filename+"moved from "+source+" to "+dest);
      } catch(Exception e){
      	System.out.println("Excception occurred: " + e.getMessage());
      }
      JSONObject obj = new JSONObject();
      obj.put("date", d.toString());
      return obj;
	}
	
	
	@Consumes({"application/xml", "application/json","text/html"})
	@Produces({"text/html"})
	@RequestMapping(value="/search", method = RequestMethod.POST)
	public @ResponseBody JSONArray searchFile(@RequestBody JSONObject o) throws Exception {
	  
	  
      Date d = new Date();
      JSONArray objs = new JSONArray();
      DbxEntry md;
      
      String search_str = o.get("title").toString();
      String folder = o.get("folder").toString();
      
      try {
    	DbxEntry.WithChildren listing = client.getMetadataWithChildren("/"+folder);
      	System.out.println("Files in the path");
      	for(DbxEntry child : listing.children)
      	{
      		String[] parts = child.name.split("-");
      		if(parts[2].contains(search_str))
      			System.out.println("filename contains  "+ child.name+" contains "+ search_str);
      		else
      			System.out.println("filename contains  "+ child.name+" does not contain "+ search_str);
      	}
      	List<DbxEntry> list = client.searchFileAndFolderNames("/"+folder, search_str);
    	for(int i =0; i< list.size(); i++ )
    		{ 
    		System.out.println("Name: " + list.get(i).name);
       		File target = new File("temp.txt");
    		OutputStream out = new FileOutputStream(target);
    		InputStream in = new FileInputStream(target);
    		try{
    			md = client.getFile("/"+folder+"/"+list.get(i).name,null,out);
    			byte[] data = new byte[(int) target.length()];
    			in.read(data);
    			String content_new = new String(data, "UTF-8");
    			System.out.println(content_new);
    			JSONObject obj = new JSONObject();
    			obj.put("note_title", list.get(i).name);
    			obj.put("note_content", content_new);
    		    objs.add(obj);
    		   
    		}finally{
    			out.close();
    			in.close();
    			target.delete();
    		}
    		}
    	
      } catch(Exception e){
      	System.out.println("Excception occurred: " + e.getMessage());
      }
      System.out.println(objs);
      
      return objs;
	}
	
	@Consumes({"application/xml", "application/json","text/html"})
	@Produces({"text/html"})
	@RequestMapping(value="/info", method = RequestMethod.POST)
	public @ResponseBody JSONObject info(@RequestBody JSONObject o) throws Exception {
	  
	  Date d = new Date();
      String title_new = o.get("title").toString();
      String folder = o.get("folder").toString();
      try {
    	String filename = title_new+".txt";
    	DbxEntry md =client.getMetadata("/"+folder+"/"+filename);
    	System.out.println("Metadata of file: "+filename+" "+ md.toString());
    	} catch(Exception e){
      	System.out.println("Excception occurred: " + e.getMessage());
      }
      JSONObject obj = new JSONObject();
      obj.put("date", d.toString());
      return obj;
      }
	
	
	// This is how you return static content from WS.
	// This page is present under public/*
	// This content can also be accessed directly from browser.
	@RequestMapping("/About")
	  public String about(Model model) {
	    return "redirect:/notefy/about.html";
	  }
	
	// This is also the same
	@RequestMapping("/Contact")
	  public String contact(Model model) {
	    return "redirect:notefy/about.html";
	  }
	
	@RequestMapping(value="/logout", method=RequestMethod.POST)
	  public String logout(Person person, BindingResult bindingResult,Model model) throws IOException, DbxException {
		
          return "login";
      }
	
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
}