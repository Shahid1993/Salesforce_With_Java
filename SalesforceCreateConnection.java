import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SalesforceCreateConnection {

    static final String USERNAME     = "shahid@codemantra.in";
    static final String PASSWORD     = "Sh@h2070PWQevwFQLy2EPRLPcziWlI676";
    static final String LOGINURL     = "https://login.salesforce.com";
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String CLIENTID     = "3MVG9pe2TCoA1Pf6_3gMYCWJvQaPSs7gN5fPcPrRZjy8Z3vpKPb8wDgOKYiwiSTQSyBGsq_7XMIEXqQDEVNWg";
    static final String CLIENTSECRET = "204460EDDB8A5D0D2CEE7A0E4E8D1F75F1AB66E63E34C46CEE12D781F1307D26";

    public static void main(String[] args) {

        HttpClient httpclient = HttpClientBuilder.create().build();

        // Assemble the login request URL
        /*String loginURL = LOGINURL +
                          GRANTSERVICE +
                          "&client_id=" + CLIENTID +
                          "&client_secret=" + CLIENTSECRET +
                          "&username=" + USERNAME +
                          "&password=" + PASSWORD;*/
        String loginURLencoded = null;
        
        try {
			loginURLencoded = LOGINURL + 
									GRANTSERVICE +
									"&client_id="+URLEncoder.encode(CLIENTID, "UTF-8")+
									"&client_secret="+ CLIENTSECRET +
									"&username=" + URLEncoder.encode(USERNAME, "UTF-8") +
									"&password=" +URLEncoder.encode(PASSWORD, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

        // Login requests must be POSTs
        HttpPost httpPost = new HttpPost(loginURLencoded);
        HttpResponse response = null;

        try {
            // Execute the login POST request
            response = httpclient.execute(httpPost);
        } catch (ClientProtocolException cpException) {
            cpException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // verify response is HTTP OK
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            System.out.println("Error authenticating to Force.com: "+statusCode);
            // Error is in EntityUtils.toString(response.getEntity())
            return;
        }

        String getResult = null;
        try {
            getResult = EntityUtils.toString(response.getEntity());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JSONObject jsonObject = null;
        String loginAccessToken = null;
        String loginInstanceUrl = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
            loginAccessToken = jsonObject.getString("access_token");
            loginInstanceUrl = jsonObject.getString("instance_url");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        System.out.println(response.getStatusLine());
        System.out.println("Successful login");
        System.out.println("  instance URL: "+loginInstanceUrl);
        System.out.println("  access token/session ID: "+loginAccessToken);

        // release connection
        httpPost.releaseConnection();
    }
}
