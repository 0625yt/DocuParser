package com.parse.document;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.parse.document.DataExtractContext;
import com.parse.document.common.Const;

public abstract class FileDataExtractor {

	public static File download(DataExtractContext context, String title, String[] paths) {
		return context.getFile(title, paths);
	}

	public static File[] downloads(DataExtractContext context, String title, String documentType) {
	    String directoryPath = Const.DEFAULT_PATH;

	    File directory = new File(directoryPath);

	    if (directory.exists() && directory.isDirectory()) {
	        File subfolder = new File(directory, title);

	        if (subfolder.exists() && subfolder.isDirectory()) {
	            File[] filesInSubfolder = subfolder.listFiles();

	            if (filesInSubfolder != null) {
	                // 확장자 필터링
	                return filterFilesByType(filesInSubfolder, documentType);
	            } else {
	                System.out.println("No files found in the folder: " + subfolder.getAbsolutePath());
	                return new File[0];
	            }
	        } else {
	            System.out.println("Folder with the name '" + title + "' does not exist or is not a valid directory.");
	            return new File[0];
	        }
	    } else {
	        System.out.println("Directory does not exist or is not a valid directory.");
	        return new File[0];
	    }
	}

	private static File[] filterFilesByType(File[] files, String documentType) {
	    List<File> filteredFiles = new ArrayList<>();

	    for (File file : files) {
	        if (!file.isFile()) continue;

	        String name = file.getName().toLowerCase();

	        if ("HWP".equalsIgnoreCase(documentType) && Arrays.stream(Const.HWP_TYPE).anyMatch(name::endsWith)) {
	            filteredFiles.add(file);
	        } else if ("WORD".equalsIgnoreCase(documentType) && Arrays.stream(Const.DOC_TYPE).anyMatch(name::endsWith)) {
	            filteredFiles.add(file);
	        }
	    }

	    return filteredFiles.toArray(new File[0]);
	}

}
