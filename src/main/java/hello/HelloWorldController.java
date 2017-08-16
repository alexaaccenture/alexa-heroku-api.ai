package hello;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/webhook")
public class HelloWorldController {
	static HttpsURLConnection con = null;
	static String clientIdApigee = "Q9GcIqNEW8utALl6EbR5gvAZQFAYKvDl";
    static String clientSecretApigee = "1UzBZpNDgi5kcHWH";
	static String voiceOutProduct="";
    static String prodName,prodPrice,prodValidity,prodLimit;
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody WebhookResponse webhook(@RequestBody String obj){

        System.out.println(obj);
        
        //add on
        
       String acc=getAccessTokenApigee();
       String addOn=getAddon(acc, "4152818284");

       // return new WebhookResponse("Hello! " + obj, "Text " + obj);
       return new WebhookResponse("Hello! " + addOn, "Text " + addOn);
    }
    
    

    public static String getAddon(String acc, String phno){


  		 voiceOutProduct="";
  		 //sfdc
  		 // String url = "https://eaglefly--EagleIDC.cs83.my.salesforce.com/services/apexrest/ProductCatalogue/product?phone="+phno;
  		 //apigee
  		// String url = "http://tiwaripooja-trial-test.apigee.net/sfdc_availableaddonplandetails_3?phone="+phno;
  		 String url = "https://tiwaripooja-trial-test.apigee.net/availableaddonplandetails_oauth_sfdc?phone="+phno;

  		 String[] x={"application/json"};
            //webResource.accept(x);
  	        Client restClient = Client.create();
            WebResource webResource = restClient.resource(url);
           ClientResponse resp = (ClientResponse) webResource.accept(x)
                    .header("Authorization", "Bearer " + acc)
                    .get(ClientResponse.class);
           String responseAddon = resp.getEntity(String.class);
           
           JSONArray response = null;
   		try {
   			response = new JSONArray(responseAddon);
   			if (response.length() != 0) {
   			
                                   for (int i = 0; i < response.length(); i++) {
                                       JSONObject jsonobject = response.getJSONObject(i);
                                       String x1=jsonobject.getString("Name");
                                       if(x.equals("Linkedin")){
                                           x1="Linked in";
                                           prodName=x1;
                                       }
                                       else{
                                           prodName = x1;
                                       }
                                       prodPrice = jsonobject.getString("Price");
                                       prodValidity=jsonobject.getString("Validity");
                                       prodLimit=jsonobject.getString("Grant");
                                       voiceOutProduct=voiceOutProduct+prodName+", with "+prodLimit+" data "+", with "+prodValidity+" validity, for "+prodPrice+" Euro cost. ";
                                   }
                                 


                                  





                               
   			}
   			else{
   				
   			}
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
   return voiceOutProduct;
  	    
       	
    }
    static String getAccessTokenApigee(){




		  String accessToken = null;

String body = null;
try {
    body = "grant_type=" + URLEncoder.encode("client_credentials", "UTF-8") + "&" +

            "client_id=" + URLEncoder.encode(clientIdApigee, "UTF-8") + "&" +

            "client_secret=" + URLEncoder.encode(clientSecretApigee, "UTF-8");
    // Create a new URL object with the base URL for the access token request.
    URL authUrl = null;
    authUrl = new URL("https://tiwaripooja-trial-prod.apigee.net/oauth/client_credential/accesstoken?grant_type=client_credentials&client_id=Q9GcIqNEW8utALl6EbR5gvAZQFAYKvDl&client_secret=1UzBZpNDgi5kcHWH");
    con = (HttpsURLConnection) authUrl.openConnection();
    con.setDoOutput(true);
    System.out.println("test 1");
    con.setRequestMethod("POST");
//   con.setRequestProperty("Charset", "UTF-8");
  // con.setRequestProperty("Charset", "x-www-form-urlencoded");
  //  con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    //con.setRequestProperty("Connection", "keep-alive");
    OutputStream os = null;
    System.out.println("test 2");
    os = con.getOutputStream();
    os.write(body.getBytes("UTF-8"));
    os.flush();
    con.connect();
    System.out.println("test 3");
    String responseContent = null;
    System.out.println("test 4");
    System.out.println(os);
    System.out.println("connn=="+con.getInputStream());
    responseContent = parseResponse(con.getInputStream());
    System.out.println("test 5");
    JSONObject parsedObject = null;
    parsedObject = new JSONObject(responseContent);
    accessToken = parsedObject.getString("access_token");
   // mInstanceUrl = parsedObject.getString("instance_url");
} catch (UnsupportedEncodingException e) {
    e.printStackTrace();
} catch (MalformedURLException e) {
    e.printStackTrace();
} catch (IOException e) {
    e.printStackTrace();
   // System.out.println(e.getMessage());
} catch (Exception e) {
    e.printStackTrace();
} finally {
    if (con != null)
        con.disconnect();
} 
return accessToken;
	



	
    }
    private static String parseResponse(InputStream in) throws Exception{



        InputStreamReader inputStream = new InputStreamReader(in, "UTF-8");
        BufferedReader buff = new BufferedReader(inputStream);
        StringBuilder sb = new StringBuilder();
        String line = buff.readLine();
        while (line != null) {
            sb.append(line);
            line = buff.readLine();
        }
        return sb.toString();
    
    
    }
}
