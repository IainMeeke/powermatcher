package tcd.iainmeeke.pvpanel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

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

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerOutput {
	
	public static final int AZIMUTH_TRACK = 1;
	public static final int AZIMUTH_TILT_TRACK = 2;

	private static final Logger LOGGER = LoggerFactory.getLogger(PowerOutput.class);
	

	public PowerOutput(double lat, double lng, double sysLoss, int tracking, double capacity, double tilt, double azim) throws ClientProtocolException, IOException{
		getOutputFile(lat, lng, sysLoss, tracking, capacity, tilt, azim);
		
	}
	
	/**
	 * Given the appropriate parameters this gets pvpanel output data from https://www.renewables.ninja/# and writes it to a csv file. The file is stored in a time stamped folder
	 * and also contains a text file of the parameters.
	 * @param lat latitude of the location of the panel
	 * @param lng longitude of the location of the panel
	 * @param sysLoss the percentage system loss of the panel
	 * @param tracking whether the panel has azimuth(1), azimuth and tilt(2), or no(0) tracking
	 * @param capacity the capacity of the panel 
	 * @param tilt the angle the panel is at (0 is facing directly upwards, 90 is vertically installed)
	 * @param azim Compass direction of the panel. For latitude >= 0, 180 degrees is south facing
	 * @throws ClientProtocolException 
	 * @throws IOException
	 */
	private void getOutputFile(double lat, double lng, double sysLoss, int tracking, double capacity, double tilt, double azim) throws ClientProtocolException, IOException{
		//create a folder for the current configuration
		SimpleDateFormat dateFormatFolder = new SimpleDateFormat("HHmmss_dd-MM-yyyy");
		String filePath = System.getProperty("user.dir")+"/res/";
		String currentOutputFolder = filePath+"pvpower-"+dateFormatFolder.format(new Date());
		String valueFilePathName = currentOutputFolder+"/value.csv";
		String paramsFilePathName = currentOutputFolder+"/params.txt";
		File dir = new File(currentOutputFolder);
		boolean successful = dir.mkdirs();
	    if(!successful) LOGGER.error("Failed to make directory for pvPanel power output");
	    
		//url params
		Date dateFrom = new Date(1388534400000l); //1st of Jan 2014
		Date dateTo = new Date(1420070399000l); //31st Dec 2014
		String dataSet = "merra2"; 
		String format = "csv";
		SimpleDateFormat dateFormatURL = new SimpleDateFormat("yyyy-MM-dd");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>(); //list contains params for generating the url
	    qparams.add(new BasicNameValuePair("lat", Double.toString(lat)));
	    qparams.add(new BasicNameValuePair("lon", Double.toString(lng)));
	    qparams.add(new BasicNameValuePair("system_loss", Double.toString(sysLoss)));
	    qparams.add(new BasicNameValuePair("tracking", Integer.toString(tracking)));
	    qparams.add(new BasicNameValuePair("capacity", Double.toString(capacity)));
	    qparams.add(new BasicNameValuePair("tilt", Double.toString(tilt)));
	    qparams.add(new BasicNameValuePair("azim", Double.toString(azim)));
	    qparams.add(new BasicNameValuePair("date_from", dateFormatURL.format(dateFrom)));
	    qparams.add(new BasicNameValuePair("date_to", dateFormatURL.format(dateTo)));
	    qparams.add(new BasicNameValuePair("dataset", dataSet));
	    qparams.add(new BasicNameValuePair("format", format));
	    
	    //write params to file
	    File paramsFile = new File(paramsFilePathName);
	    FileWriter fw = new FileWriter(paramsFile);
	    BufferedWriter bw = new BufferedWriter(fw);
	    for(NameValuePair pair: qparams){
	    	bw.write(pair.getName() + " : " + pair.getValue() + "\n");
	    }
	    bw.close();
	    
	    //build the url and make the request
	    URIBuilder builder = new URIBuilder().setScheme("http").setHost("www.renewables.ninja/api/v1/data/pv").setParameters(qparams);
	    String uri = builder.toString();
	    System.out.println(uri);
	    HttpClient client = HttpClientBuilder.create().build();
	    HttpGet request = new HttpGet(uri);
	    HttpResponse response = client.execute(request);
	    //read the response and write it to a file    
	    InputStream is = response.getEntity().getContent();   
	    FileOutputStream vfos = new FileOutputStream(new File(valueFilePathName));
	    int inByte;
	    while((inByte = is.read()) != -1)
	         vfos.write(inByte);
	    is.close();
	    vfos.close();
	}
}
