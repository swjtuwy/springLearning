/**
 *******************************************************************************
 *                       Continental Confidential
 *                  Copyright (c) Continental, AG. 2017
 *
 *      This software is furnished under license and may be used or
 *      copied only in accordance with the terms of such license.
 *******************************************************************************
 * @file   FileUtil.java
 * @brief  Provide some file related interface
 *******************************************************************************
 */

package com.map.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.util.TextUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


public class FileUtil {

	//private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static boolean writeFile(String fileName, InputStream in, boolean saveEmptyFile) {

		FileOutputStream fileout = null;
		File file = new File(fileName);
		boolean bFlag = false;
		try {
			fileout = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int ch;
			while ((ch = in.read(buffer)) != -1) {
				bFlag = true;
				fileout.write(buffer, 0, ch);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			logger.debug("write file :" + fileName + " error:" + e.getMessage());
			return false;
		} finally {
			close(fileout);
		}

		if (!saveEmptyFile && !bFlag) {
			file.delete();
		}
		return true;
	}
	
	
	public static String moveFile(String srcFile, String destFolder) {

		if (FileUtil.createFolder(destFolder)) {
			File file = new File(srcFile);
			String newFilePath = destFolder + "/" + file.getName();

			file.renameTo(new File(newFilePath));

			if ((new File(newFilePath)).exists()) {
				return newFilePath;
			} else {
				return srcFile;
			}
		}

		return null;
	}

	public static void close(Closeable inputSteam) {
		if (inputSteam != null) {
			try {
				inputSteam.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				logger.error(e.getMessage());
			}
		}
	}
	
	public static boolean exists(String path) {
		File dir = new File(path);
		return dir.exists();
	}

	public static boolean createFolder(String path) {

		File dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
//			logger.debug("dir " + path + "is exist");
			return true;
		}

		if (dir.mkdirs() || dir.exists()) {
//			logger.debug("createFolder  " + path + "sucessful");
			return true;
		} else {
//			logger.debug("createFolder " + path + "fail");
			return false;
		}
	}
	
	public static String readFile(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
//			logger.error("readFile() error, filePath is empty");
			return null;
		}
		File file = new File(filePath);
		FileReader fileReader = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String result;
		try {
			fileReader = new FileReader(file);
			br = new BufferedReader(fileReader);
			String tempString = br.readLine();
			if (tempString != null) {
				sb.append(tempString);
			}
			while ((tempString = br.readLine()) != null) {
				sb.append(System.getProperty("line.separator"));
				sb.append(tempString);
			}
			result = sb.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			logger.error(e.getMessage());
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			logger.error(e.getMessage());
			return null;
		} finally {
			close(fileReader);
			close(br);
		}
		return result;
	}


	public static String unGzFile(String gzFile, String destDir) {
		if (!gzFile.endsWith(".gz")) {
//			logger.debug("ungz failed: " + gzFile + " is not a .gz file");
			return null;
		}

		String destFileName = null;
		File gz = new File(gzFile);
		String gzFileName = gz.getName();
		mkdirs(destDir);

		try (InputStream gis = new GZIPInputStream(new BufferedInputStream(
				new FileInputStream(gz)))) {
			String outFileName = destDir + File.separator
					+ gzFileName.substring(0, gzFileName.length() - 3);
			File outFile = new File(outFileName);
			try (OutputStream bos = new BufferedOutputStream(
					new FileOutputStream(outFile))) {
				int count;
				byte data[] = new byte[1024];
				while ((count = gis.read(data, 0, 1024)) != -1) {
					bos.write(data, 0, count);
				}
				bos.flush();
				destFileName = outFile.getAbsolutePath();
			}
		} catch (IOException e) {
//			logger.error("ungz failed: " + e.toString());
		}

		return destFileName;
	}

	private static void mkdirs(String dir) {
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdirs();
		}
	}
	
	private static void delFile(String filePath,boolean delDir){
		File file = new File(filePath);
		if(file.isDirectory()){
			File[] files = file.listFiles();  
			if(null!=files){
				for (int i = 0; i < files.length; i++) {  
					//files[i].delete();     
					if(files[i].isDirectory()){
						delFile(files[i].getAbsolutePath(), delDir);
					}else{
						files[i].delete();
					}
				} 
			}
			if(delDir){
				file.delete();
			}
		}else{
			file.delete();
//			logger.debug("delete file:" + filePath);
		}
		
	 }
	 
	 public static void delFile(String filePath) {
		 delFile(filePath,false);
	 }
	
	 public static void delDir(String filePath){
		delFile(filePath,true);
	}
	 
	
	public static boolean writeFile(String filePath, StringBuffer buffer) {
		PrintWriter p = null;
		try {
			File newFile = new File(filePath);
			if (newFile.exists()) {
				if (!newFile.delete()) {
//					logger.error("delete file" + newFile + "failed!");
					return false;
				}
			}
			if (newFile.createNewFile()) {
				p = new PrintWriter(new FileOutputStream(
						newFile.getAbsolutePath()));
				p.write(buffer.toString());

			} else {
//				logger.error("create file" + newFile + "failed!");
				return false;
			}
		} catch (Exception e) {
//			logger.error(e + "");
		} finally {
			close(p);
		}
		return true;
	}
	
	public static boolean move(String from, String to) {

		try {
			File fromfile = new File(from);
			File tofile = new File(to);

			if (!fromfile.exists())
				return false;

			if (tofile.exists())
				tofile.delete();

			if (fromfile.renameTo(tofile)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static String getFileMD5(File file){
		InputStream inputStream = null;
		String md5 = "";
		try {
			inputStream = new FileInputStream(file);
			md5 = DigestUtils.md5Hex(inputStream);
		}  catch (IOException e) {
//			logger.error(e.toString());
		}finally{
			FileUtil.close(inputStream);
		}
		return md5;
	}
	
	public static boolean copyFile(String srcFile,String destFile){
		String inputFile = srcFile;
		String outputFile = destFile;
		FileInputStream fis ;
		FileOutputStream fos ;
		BufferedInputStream bufis = null;
		BufferedOutputStream bufos = null;
		try {
			fis = new FileInputStream(inputFile);
			bufis = new BufferedInputStream(fis);  
			fos = new FileOutputStream(outputFile);
			bufos = new BufferedOutputStream(fos);  
			int len ; 
			byte[] buffer=new byte[1024];
	        while ((len = bufis.read(buffer)) != -1) {  
	        	bufos.write(buffer,0,len);  
	        }  
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			logger.error("load file:"+outputFile +"error:"+e.getMessage());
			return false;	
		}finally{
			close(bufis);
			close(bufos);
		}
		return true;
	}
	
	public static void delSometimeAgoFile(String filePath, long time) {

		long curTime = new Date().getTime();
		File file = new File(filePath);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (curTime - files[i].lastModified() > time) {
						files[i].delete();
					}
				}
			}
		}
	}
}

