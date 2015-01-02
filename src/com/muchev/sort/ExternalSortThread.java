package com.muchev.sort;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ExternalSortThread extends Thread{
	
	private static final long MAX_OBJECTS_IN_FILE = 10000;
	
	List<Comparable> objectsList = new ArrayList<Comparable>();
	List<File> filesList = new ArrayList<File>();
	
	String inputFolder;
	String outputFolder;
	
	int errorCount = 0;
	
	public ExternalSortThread(String inputFolder) {
		super();
		this.inputFolder = inputFolder;
	}
	
	private Comparable getObjectFromStream(ObjectInputStream ois){
		Comparable mmm = null;
		try{
			mmm = (Comparable)ois.readObject();
		}catch(Exception e){
			mmm = null;
		}
		return mmm;
	}

	@Override
	public void run() {
		
		try{		
			//validation
			if(inputFolder == null || inputFolder.trim().equals("")){
				log("Please specify input folder!", LogType.DEBUG);
				return;
			}
			
			File folder = new File(inputFolder);
			if(!folder.isDirectory()){
				log("Please specify input folder!", LogType.DEBUG);
				return;
			}
			
			outputFolder = inputFolder+File.separator+"external_sort";

			log("Started!", LogType.DEBUG);

			getFilesAndSubFolders(folder);
			
			//must save remaining objects
			saveList();
			
			long startTime = System.currentTimeMillis();
			log("Sorting objects", LogType.DEBUG);	
			
			//must sort files
			int y = 0;
			for(int i = 0; i < filesList.size()-1; ){
				
				File one = filesList.get(i);
				File two = filesList.get(i+1);
				
				File file = new File(outputFolder+File.separator+"_sort_"+y);				
				ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file, true)));
				
				ObjectInputStream ois1 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(one)));
				ObjectInputStream ois2 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(two)));
				
				Comparable mmm1 = getObjectFromStream(ois1);
				Comparable mmm2 = getObjectFromStream(ois2);
				
				while(mmm1!=null || mmm2!=null){
				
					if(mmm1==null && mmm2!=null){
						oos.writeObject(mmm2);
						mmm2 = getObjectFromStream(ois2);
					}else if(mmm1!=null && mmm2==null){
						oos.writeObject(mmm1);
						mmm1 = getObjectFromStream(ois1);
					}else{						
						if(mmm1.compareTo(mmm2) < 0){
							oos.writeObject(mmm1);
							mmm1 = getObjectFromStream(ois1);	
						}else{
							oos.writeObject(mmm2);
							mmm2 = getObjectFromStream(ois2);
						}						
					}
				
				};
				
				ois1.close();
				ois2.close();
				
				oos.flush();
				oos.close();
				filesList.add(file);
				y++;				
				
				while(!one.delete()){
					Thread.sleep(10);
				}
				filesList.remove(one);
				
				while(!two.delete()){
					Thread.sleep(10);
				}
				filesList.remove(two);
			}
			
			log("Sorting completed totalTime = "+(System.currentTimeMillis() - startTime)+" ms", LogType.DEBUG);		


		}catch(Exception ex){
			log(ex.getMessage(), LogType.ERROR);
		}
		
		log("Process end! Total errors: "+errorCount, LogType.DEBUG);
		log(errorCount==0?"SUCCESS!!!":"FINISHED WITH ERRORS!!!", LogType.DEBUG);
		
		log("Generated file(s):", LogType.DEBUG);
		for(File f:filesList){
			log(f.getPath(), LogType.DEBUG);
		}

	}
	
	private void getFilesAndSubFolders(File directory){
		
		// get all the files from a directory
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isFile()) {
				
				ObjectInputStream ois = null;
				
				try{
					
					ois = new ObjectInputStream(new FileInputStream(file));
				
					log("Started reading objects from file: "+file.getAbsolutePath(), LogType.DEBUG);
	
					Comparable o = null;
					
					try{
						while((o = (Comparable)ois.readObject())!=null){
							objectsList.add(o);
						
							if(objectsList.size() >= MAX_OBJECTS_IN_FILE){
								saveList();
							}
						}
					}catch(EOFException eof){
						log("Finished reading objects from file: "+file.getAbsolutePath(), LogType.DEBUG);
					}
					
					
				}catch(Exception ex){
					log(ex.getMessage(), LogType.ERROR);
				}finally{
					if(ois!=null){
						try {
							ois.close();
						} catch (IOException e) {
							
						}
					}
				}

			} else if (file.isDirectory()) {
				getFilesAndSubFolders(file);
			}
		}
		
	}
	
	private void saveList() throws Exception{
		
		Collections.sort(objectsList);
		
		File folder = new File(outputFolder);
		if(!folder.exists()){
			folder.mkdirs();
		}
		
		File file = new File(outputFolder+File.separator+"_"+filesList.size());
		
		ObjectOutputStream aoos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file, true)));
		
		for(Comparable mmm:objectsList){
			aoos.writeObject(mmm);
		}
		
		aoos.flush();
		aoos.close();
		
		filesList.add(file);
		
		objectsList.clear();
	}
	
	private void log(String text, LogType type){
		
		SimpleDateFormat logTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		logTime.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		System.out.println(logTime.format(new Date())+" "+text);
		
		if(type == LogType.ERROR){
			errorCount++;
		}
	}
	
	private enum LogType{
		DEBUG, ERROR;
	}


}
