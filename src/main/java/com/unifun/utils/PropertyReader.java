package com.unifun.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
	public  Properties readParamFromFile(){
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream("bulkConfig.properties")) {
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop;
	}
}
