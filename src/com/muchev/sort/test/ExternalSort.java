package com.muchev.sort.test;

import com.muchev.sort.ExternalSortThread;


public class ExternalSort {

	public static void main(String[] args) {
		ExternalSortThread pt = new ExternalSortThread(Utils.TEST_FOLDER);
		pt.start();
	}

}
