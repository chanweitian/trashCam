package com.trashcam.model;

import java.io.File;
import java.util.HashMap;

public class DeleteFileModelManager {
	private static DeleteFileModelManager instance;

	public HashMap<String, DeleteFileModel> toBeDeleted;

	private DeleteFileModelManager() {
		toBeDeleted = new HashMap<String, DeleteFileModel>();
		instance = this;
	}

	public static DeleteFileModelManager getInstance() {
		if (instance == null) {
			new DeleteFileModelManager();
			/*
			 * LOAD FROM DB
			 */
		}
		return instance;
	}

	public void addFile(File new_picture, int days) {
		if(new_picture==null){
			throw new IllegalStateException("new_picture NOT INITED");
		}
		if(toBeDeleted==null){
			throw new IllegalStateException("TOBEDELETED NOT INITED");
		}
		removeIfExist(new_picture);
		toBeDeleted.put(new_picture.getAbsolutePath(), new DeleteFileModel(
				new_picture, days));
		/*
		 * TODO: save to database also
		 */
	}

	private void removeIfExist(File new_picture) {
		if (toBeDeleted.containsKey(new_picture.getAbsolutePath())) {
			toBeDeleted.remove(new_picture.getAbsolutePath());
			/*
			 * TODO: delete from database
			 */
		}
	}

	public void deleteFileNow(String path) {
		deleteFileNow(new File(path));
	}

	public void deleteFileNow(File file_to_be_deleted) {
		removeIfExist(file_to_be_deleted);
		boolean successful = file_to_be_deleted.delete();
	}
}
