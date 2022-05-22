package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gson.stream.JsonReader;

public class RecordJsonParser {
	
	public static void parse (String inputPath) {
		
		FileReader frd; JsonReader jrd; FileWriter fwr;
		
		 Boolean sucsuccess_request = true;
		
		HashMap<Integer, HashMap<String, String>> records = new HashMap<Integer, HashMap<String, String>>();
		
		try {

			frd = new FileReader(new File(inputPath));
			jrd = new JsonReader(frd);
			jrd.setLenient(true);

			parseRecordJSON(jrd, records, sucsuccess_request);

			jrd.close();
			frd.close();
			
			if (records.size() != 0) {
				
				fwr = new FileWriter(new File(inputPath));

				printProcessedRecordJSON(fwr, records);

				fwr.close();
			}
			
			else new File(inputPath).delete();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	static private void parseRecordJSON (JsonReader jrd, HashMap<Integer, HashMap<String, String>> records, Boolean sucsuccess_request) throws IOException {

		String key = ""; Integer record_number = 0;

		// Objecto principal del fichero JSON
		jrd.beginObject();

		while(jrd.hasNext()) {
			key = jrd.nextName();
			
			if (key.equalsIgnoreCase("success"))  sucsuccess_request = jrd.nextBoolean();

			// Object "results"
			else if(key.equalsIgnoreCase("result") && sucsuccess_request) {
				
				// Begin of Object "results"
				jrd.beginObject();
				
				while(jrd.hasNext()) {
					key = jrd.nextName();
					
					if (key.equalsIgnoreCase("records")) {
						
						// Comienzo del array "records".
						jrd.beginArray(); 

						// Procesar todos los elementos del array "records".
						while(jrd.hasNext()) {
							parseArrayElement(jrd, records, record_number);
							++record_number;
						}

						// Final del array "records".
						jrd.endArray();
					}
					
					// Si no es un objeto "records" no analizamos el objeto.
					else jrd.skipValue();
				}
				
				// End of Object "results"
				jrd.endObject();
			}
			
			
			// Si no es un objeto "results" no analizamos el objeto.
			else jrd.skipValue();

		}

		// Final del objeto principal del fichero JSON 
		jrd.endObject();
	}
	
	static private void parseArrayElement(JsonReader jrd, HashMap<Integer, HashMap<String, String>> records, Integer record_number) throws IOException {

		String key;
		
		String  s_value;
		Date    date_value;
		Integer i_value;
		Double  d_value;
		
		HashMap<String, String> record = new HashMap<String, String>();
		
		try {

			// Objecto "record"
			jrd.beginObject();
			
			while(jrd.hasNext()) {
				key = jrd.nextName();
				
				if( key.equalsIgnoreCase("_id") ) {
					// {"type": "int", "id": "_id"}
					i_value = jrd.nextInt();
					
					record.put("_id", i_value.toString());
				}
				
				else if( key.equalsIgnoreCase("latitud") ) {
					// {"type":"numeric","id":"latitud"}
					d_value = jrd.nextDouble();
					
					record.put("latitude", d_value.toString());
				}
				
				else if( key.equalsIgnoreCase("longitud") ) {
					// {"type":"numeric","id":"longitud"}
					d_value = jrd.nextDouble();
					
					record.put("longitude", d_value.toString());
				}
				
				else if( key.equalsIgnoreCase("fecha") ) {
					// {"type":"timestamp","id":"fecha"}
					s_value = jrd.nextString();
					
					date_value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s_value.replace("T", " "));
					
					record.put("timestamp", Long.toString(date_value.getTime()));
				}
				
				else if( key.equalsIgnoreCase("descripcion") ) {
					// {"type":"text","id":"descripcion"}
					s_value = jrd.nextString();
					
					record.put("description", s_value);
				}
				
				else if( key.equalsIgnoreCase("nombre") ) {
					// {"type":"text","id":"nombre"}
					s_value = jrd.nextString();
					
					record.put("sensor_id", s_value);
				}
				
				else if( key.equalsIgnoreCase("ligeros") ) {
					// {"type":"int4","id":"ligeros"}
					i_value = jrd.nextInt();
					
					record.put("light_vehicles", i_value.toString());
				}
				
				else if( key.equalsIgnoreCase("pesados") ) {
					// {"type":"int4","id":"pesados"}
					i_value = jrd.nextInt();
					
					record.put("heavy_vehicles", i_value.toString());
				}
				
				else jrd.skipValue();
			}
			
			// Final de un elemento "record"
			jrd.endObject();
			
			record.put("record_hash", Integer.toString(record.hashCode()));
			
			records.put(record_number, record);
			
			System.err.println(record.toString());

		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static final String json_format = "{\r\n"
            + "\t\"fields\":  ["
            + "{\"type\":\"int\",\"id\":\"_id\"},"
            + "{\"type\":\"int\",\"id\":\"hash\"},"
            + "{\"type\":\"timestamp\",\"id\":\"fecha\"},"
            + "{\"type\":\"double\",\"id\":\"latitude\"},"
            + "{\"type\":\"double\",\"id\":\"longitude\"},"
            + "{\"type\":\"text\",\"id\":\"sensor_id\"},"
            + "{\"type\":\"text\",\"id\":\"description\"},"
            + "{\"type\":\"int\",\"id\":\"light_vehicles\"},"
            + "{\"type\":\"int\",\"id\":\"heavy_vehicles\"}"
            + "],\r\n" 
            + "\t\"records\": ["
            + "#RECORDS#"
            + "\r\n\t]"
            + "\r\n}";
	
	private static final String record_format = "\r\n\t\t{"
			+ "\"_id\":#RECORD_ID#,"
            + "\"hash\":#RECORD_HASH#,"
            +  "\"timestamp\":\"#RECORD_TIMESTAMP#\","
            + "\"latitude\":#RECORD_LAT#,"
            + "\"longitude\":#RECORD_LONG#,"
            + "\"description\":\"#RECORD_DESCRIPTION#\","
            + "\"sensor_id\":\"#RECORD_SENSOR_ID#\","
            + "\"light_vehicles\":#RECORD_LIGHT_VEH#,"
            + "\"heavy_vehicles\":#RECORD_HEAVY_VEH#"
            + "},";
	
	static private void printProcessedRecordJSON(FileWriter fwr,  HashMap<Integer, HashMap<String, String>> records) throws IOException {
		
		String s_records = "", s_record = ""; String s_timestamp = "";
		
		HashMap<String, String> record;
		
		List<Integer> shorted_list = new ArrayList<Integer>(records.keySet());
		
		Collections.sort(shorted_list);
		
		for ( Integer record_key :  shorted_list){
				
			record = records.get(record_key);
			
			s_record = record_format.replaceAll("#RECORD_ID#", record.get("_id"));
			
			s_record = s_record.replaceAll("#RECORD_HASH#", record.get("record_hash"));
			
			s_timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(record.get("timestamp"))));
			
			s_record = s_record.replaceAll("#RECORD_TIMESTAMP#", s_timestamp);
			
			s_record = s_record.replaceAll("#RECORD_LAT#", record.get("latitude"));
			
			s_record = s_record.replaceAll("#RECORD_LONG#", record.get("longitude"));
			
			s_record = s_record.replaceAll("#RECORD_DESCRIPTION#", record.get("description"));
			
			s_record = s_record.replaceAll("#RECORD_SENSOR_ID#",  record.get("sensor_id"));
			
			s_record = s_record.replaceAll("#RECORD_LIGHT_VEH#", record.get("light_vehicles"));
			
			s_record = s_record.replaceAll("#RECORD_HEAVY_VEH#", record.get("heavy_vehicles"));
			
			s_records += s_record;
		}
		
		if ( s_records.length() != 0 ) fwr.write(json_format.replace("#RECORDS#", s_records.substring(0, s_records.length() - 1)));
		else fwr.write(json_format.replace("#RECORDS#", ""));
	}
	
}
