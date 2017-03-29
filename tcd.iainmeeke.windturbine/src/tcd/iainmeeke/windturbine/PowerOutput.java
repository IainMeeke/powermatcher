package tcd.iainmeeke.windturbine;

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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PowerOutput.class);
	

	public PowerOutput(double lat, double lng, double capacity) throws ClientProtocolException, IOException{
		getOutputFile(lat, lng, capacity);
		
	}

	/**
	 * Given the appropriate parameters this gets windturbine output data from https://www.renewables.ninja/# and writes it to a csv file. The file is stored in a time stamped folder
	 * @param lat latitude of the wind turbine location
	 * @param lng longitude of the wind turbine location
	 * @param capacity of the wind turbine
	 * @throws ClientProtocolException
	 * @throws IOException 
	 *   
	 */
	private void getOutputFile(double lat, double lng, double capacity) throws ClientProtocolException, IOException{
		//create a folder for the current configuration
		SimpleDateFormat dateFormatFolder = new SimpleDateFormat("HHmmss_dd-MM-yyyy");
		String filePath = System.getProperty("user.dir")+"/res/";
		String currentOutputFolder = filePath+"windpower-"+dateFormatFolder.format(new Date());
		String valueFilePathName = currentOutputFolder+"/value.csv";
		String paramsFilePathName = currentOutputFolder+"/params.txt";
		File dir = new File(currentOutputFolder);
		boolean successful = dir.mkdirs();
	    if(!successful) LOGGER.error("Failed to make directory for windTurbine power output");
	    
		//url params
		Date dateFrom = new Date(1388534400000l); //1st of Jan 2014
		Date dateTo = new Date(1420070399000l); //31st Dec 2014
		String turbineModel = "Vestas V27 225";
		String height = "100";
		String dataSet = "merra2"; 
		String format = "csv";
		SimpleDateFormat dateFormatURL = new SimpleDateFormat("yyyy-MM-dd");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>(); //list contains params for generating the url
	    qparams.add(new BasicNameValuePair("lat", Double.toString(lat)));
	    qparams.add(new BasicNameValuePair("lon", Double.toString(lng)));
	    qparams.add(new BasicNameValuePair("date_from", dateFormatURL.format(dateFrom)));
	    qparams.add(new BasicNameValuePair("date_to", dateFormatURL.format(dateTo)));
	    qparams.add(new BasicNameValuePair("capacity", Double.toString(capacity)));
	    qparams.add(new BasicNameValuePair("dataset", dataSet));
	    qparams.add(new BasicNameValuePair("format", format));
	    qparams.add(new BasicNameValuePair("turbine", turbineModel));
	    qparams.add(new BasicNameValuePair("height", height));
	    //write params to file
	    File paramsFile = new File(paramsFilePathName);
	    FileWriter fw = new FileWriter(paramsFile);
	    BufferedWriter bw = new BufferedWriter(fw);
	    for(NameValuePair pair: qparams){
	    	bw.write(pair.getName() + " : " + pair.getValue() + "\n");
	    }
	    bw.close();
	    
	    //build the url and make the request
	    URIBuilder builder = new URIBuilder().setScheme("http").setHost("www.renewables.ninja/api/v1/data/wind").setParameters(qparams);
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
