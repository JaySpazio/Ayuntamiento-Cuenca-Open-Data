package main;

import parser.*;

public class main {

	public static void main(String[] args) {
		
		DataParser parser = new DataParser();
		
		parser.downloadDataset();
		
//		json_parser.parse("./raw-data/74a58122-630d-49ef-a160-af53a0add1bb.json");
		
		System.exit(0);
	}

}
