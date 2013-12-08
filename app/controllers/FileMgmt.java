package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

import models.account.AppUser;
import models.product.FileTag;
import models.product.UserFile;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class FileMgmt extends Controller {

	public static String FILE_UPLOAD_FORM_FIELD_NAME = "file";
	
    @Restrict(@Group(models.account.SecurityRole.SecurityRoleCustomer))
    public static Result uploadFile() {
    	return ok(views.html.uploadFile.render("File Upload"));
    }

    @Restrict(@Group(models.account.SecurityRole.SecurityRoleCustomer))
    public static Result doUploadFile() {
    	MultipartFormData body = request().body().asMultipartFormData();
    	FilePart uploadedFile = body.getFile(FILE_UPLOAD_FORM_FIELD_NAME);
    	
    	if (uploadedFile == null) {
    		Logger.error("file upload failed");
    		ctx().flash().put(util.ConfigParameter.FLASH_ERROR_KEY, "File upload failed!");
    		
    	} else {
    		String fileName = uploadedFile.getFilename();
    		if (fileName.isEmpty()) {
    			Logger.error("file upload failed; fileName is empty");
        		ctx().flash().put(util.ConfigParameter.FLASH_ERROR_KEY, "File upload failed!");
    		} else {
	    		final AppUser appUser = controllers.account.AccountMgmt.getLocalUser(session());
	    		if (appUser == null) {
	        		Logger.error("Something is messed up ... user is not in the database.");
	        		ctx().flash().put(util.ConfigParameter.FLASH_ERROR_KEY, "Please login first to upload files!");
	
	    		} else {
		    		UserFile userFile = new UserFile();
		    		if (userFile.processNewFile(appUser, fileName, uploadedFile.getFile())) {
		    			Logger.info("file has been uploaded");
		    			ctx().flash().put(util.ConfigParameter.FLASH_MESSAGE_KEY, "File " + fileName + " has been uploaded!");
		    		} else {
		    			Logger.error("file upload failed; error while executing processNewFile().");
		    			ctx().flash().put(util.ConfigParameter.FLASH_ERROR_KEY, "File " + fileName + " upload failed!");
		    		}
	    		}
    		}
    	}

		return ok(views.html.uploadFileResult.render("File Upload Result"));
    }
    
    @Restrict(@Group(models.account.SecurityRole.SecurityRoleCustomer))
    public static Result displayUserFiles() {
    	final AppUser appUser = controllers.account.AccountMgmt.getLocalUser(session());
    	List<UserFile> userFileList = UserFile.getListByUser(appUser);
    	return ok(views.html.displayUserFiles.render("User Files", userFileList));
    }

    @Restrict(@Group(models.account.SecurityRole.SecurityRoleCustomer))
    public static Result displayUserFileTags(Long userFileId) {
    	UserFile userFile = UserFile.findById(userFileId);
    	return ok(views.html.displayUserFileTags.render("File Tags", userFile));
    }
    
    @Restrict(@Group(models.account.SecurityRole.SecurityRoleCustomer))
    public static Result displayUserTags() {
    	final AppUser appUser = controllers.account.AccountMgmt.getLocalUser(session());
    	return ok(views.html.displayUserTags.render("User Tag List", appUser.userTags));
    }

    public static Result displayAllTags() {
    	List<FileTag> fileTagList = FileTag.getAll();
    	return ok(views.html.displayAllTags.render("Tag Cloud", fileTagList));
    }
    
}
