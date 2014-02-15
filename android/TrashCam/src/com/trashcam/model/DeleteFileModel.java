package com.trashcam.model;

import java.io.File;

import com.trashcam.Utility;

public class DeleteFileModel {
	public String fileUrl;
	public int days;
	public String dateToBeDeleted;

	public DeleteFileModel(String fileUrl, int days) {
		super();
		this.fileUrl = fileUrl;
		this.dateToBeDeleted = Utility.getDateAfter(days);
	}

	public DeleteFileModel(File file, int days) {
		this(file.getAbsolutePath(),days);
	}
}
