package parser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DataParser {
	
	private static final String datosabiertos_cuenca_es_url = "https://datosabiertos.cuenca.es/api/3/action/datastore_search?resource_id=74a58122-630d-49ef-a160-af53a0add1bb&q=#DATE#";
	
	private static final String processed_data_path = "./processed_data/#DATE#-record.json";
	
	private static Calendar start_Date;
	
	public DataParser ( ) {
		
		start_Date = new GregorianCalendar();
		
		start_Date.set(Calendar.DAY_OF_MONTH, 1);
		start_Date.set(Calendar.MONTH, 7);
		start_Date.set(Calendar.YEAR, 2019);
		
		start_Date.set(Calendar.SECOND, 0);
		start_Date.set(Calendar.MINUTE, 0);
		start_Date.set(Calendar.HOUR_OF_DAY, 0);
	}
	
	public void downloadDataset ( ) {
		
		String s_day_record_json_path, downloand_path;
		
		while ( start_Date.getTime().compareTo(new Date(System.currentTimeMillis())) < 0) {
			
			s_day_record_json_path = String.format("%04d-%02d-%02d", start_Date.get(Calendar.YEAR),
                                                                    (start_Date.get(Calendar.MONTH) + 1),
                                                                     start_Date.get(Calendar.DAY_OF_MONTH));
			
			downloand_path = processed_data_path.replace("#DATE#", s_day_record_json_path);
			
			downloadRecord(downloand_path, s_day_record_json_path);
			
			RecordJsonParser.parse(downloand_path);
			
			start_Date.add(Calendar.DAY_OF_MONTH, 1);
		}
	}

	public void downloadRecord (String savedRecordpath, String recordDate) {
		
		URL url;
		
		try {
			
			url = new URL(datosabiertos_cuenca_es_url.replace("#DATE#", recordDate));
			
			InputStream in = new BufferedInputStream(url.openStream());
			
			OutputStream out = new BufferedOutputStream(new FileOutputStream(savedRecordpath));

			for ( int i; (i = in.read()) != -1; ) out.write(i);
			
			out.close(); in.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}
