package com.abozaid.foursquareexplorer.singleton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import android.content.Context;

public class ReadWriteToFile {
	
	//method to cache data in file 
		public static void write(String fileName, String data, Context context) {
			try {
				FileOutputStream fos = context.openFileOutput(fileName,
						Context.MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(fos);
				os.writeObject(data);
				os.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//function to read cached data from file
		public static String read(String fileName,Context context) {
			String simpleClass = null;
			try {
				FileInputStream fis = context.openFileInput(fileName);
				ObjectInputStream is = new ObjectInputStream(fis);
				simpleClass = (String) is.readObject();
				is.close();
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return simpleClass;
		}
}
