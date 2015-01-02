package com.muchev.sort.test;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Counter {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(Utils.TEST_FOLDER+File.separator+"external_sort"+File.separator+"_sort_5")));

		Object o = null;
		int count = 0;
		try {
			while ((o = ois.readObject()) != null) {
				count++;
			}
		} catch (EOFException e) {

		}finally{
			ois.close();
		}

		System.out.println(count);
	}
}
