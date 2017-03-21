package tcd.iainmeeke.pvpanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class PowerOutput {
	
	public static final int AZIMUTH_TRACK = 1;
	public static final int AZIMUTH_TILT_TRACK = 2;
	
	//lat=45.432123&lon=22&date_from=2014-01-01&date_to=2014-01-31&dataset=merra2&capacity=1.1234&system_loss=10.543&tracking=2&tilt=35&azim=180.0123&format=json
	public PowerOutput(double lat, double lng, double sysLoss, int tracking, double capacity, double tilt, double azim) throws ClientProtocolException, IOException{
		
		Date dateFrom = new Date(1388534400000l); //1st of Jan 2014
		Date dateTo = new Date(1420070399000l); //31st Dec 2014
		String dataSet = "merra2";
		String format = "csv";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
	    qparams.add(new BasicNameValuePair("lat", Double.toString(lat)));
	    qparams.add(new BasicNameValuePair("lon", Double.toString(lat)));
	    qparams.add(new BasicNameValuePair("system_loss", Double.toString(sysLoss)));
	    qparams.add(new BasicNameValuePair("system_loss", Double.toString(sysLoss)));
	    qparams.add(new BasicNameValuePair("tracking", Integer.toString(tracking)));
	    qparams.add(new BasicNameValuePair("capacity", Double.toString(capacity)));
	    qparams.add(new BasicNameValuePair("tilt", Double.toString(tilt)));
	    qparams.add(new BasicNameValuePair("azim", Double.toString(azim)));
	    qparams.add(new BasicNameValuePair("system_loss", Double.toString(sysLoss)));
	    qparams.add(new BasicNameValuePair("date_from", dateFormat.format(dateFrom)));
	    qparams.add(new BasicNameValuePair("date_to", dateFormat.format(dateTo)));
	    qparams.add(new BasicNameValuePair("dataset", dataSet));
	    qparams.add(new BasicNameValuePair("format", format));
	    
	    URIBuilder builder = new URIBuilder().setScheme("http").setHost("www.renewables.ninja/api/v1/data/pv").setParameters(qparams);
	    String uri = builder.toString();
	    System.out.println(uri);
	    HttpClient client = HttpClientBuilder.create().build();
	    HttpGet request = new HttpGet(uri);
	    HttpResponse response = client.execute(request);

	    System.out.println("Response Code : "
	                    + response.getStatusLine().getStatusCode());

	    BufferedReader rd = new BufferedReader(
	    	new InputStreamReader(response.getEntity().getContent()));

	    StringBuffer result = new StringBuffer();
	    String line = "";
	    while ((line = rd.readLine()) != null) {
	    	result.append(line);
	    }
	    System.out.println(result);
	}
}
