package com.muchev.sort.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Generator {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		File dir = new File(Utils.TEST_FOLDER);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		for (int i = 0; i < 9; i++) {
			
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(Utils.TEST_FOLDER + File.separator + i + ".txt"))));

			for (int j = 0; j < 6854; j++) {
				oos.writeObject(Double.valueOf(Math.random()));
			}

			oos.flush();
			oos.close();
		}
	}

}
