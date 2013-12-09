package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

import models.account.AppUser;
import models.product.FileTag;
import models.product.UserFile;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import play.Logger;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import util.ConfigParameter;

public class FileMgmt extends Controller {

	public static String FILE_UPLOAD_FORM_FIELD_NAME = "file";
	
    @Restrict(@Group(models.account.SecurityRole.SecurityRoleCustomer))
    public static Result uploadFile() {
    	return ok(views.html.uploadFile.render("File Upload"));
    }

    @Restrict(@Group(models.account.SecurityRole.SecurityRoleCustomer))
    public static Result doUploadFile() {
    	// Execute this method in a separate thread pool (Akka thread pool)
	    Promise<Boolean> promiseOfBoolean = play.libs.Akka.future(
	    	new Callable<Boolean>() {
	        	public Boolean call() {
	        		return doUploadFileImpl();
	        	}
	        }
        );

	    return async(
	    	promiseOfBoolean.map(
	    		new Function<Boolean,Result>() {
	    	        public Result apply(Boolean processFileResult) {
	    	        	String uploadResultMessage;
	    	        	
	    	    		if (processFileResult) {
	    	    			Logger.info("file has been uploaded");
	    	    			uploadResultMessage = "File has been uploaded!";
	    	    		} else {
	    	    			Logger.error("file upload failed; error while executing processNewFile().");
	    	    			uploadResultMessage = "File upload failed!";
	    	    		}
	    	    		return ok(views.html.uploadFileResult.render("File Upload Result", uploadResultMessage));
	    	        }
	    		}
	    	)
	    );
    	
    }
    
    private static boolean doUploadFileImpl() {
    	MultipartFormData body = request().body().asMultipartFormData();
    	final FilePart uploadedFile = body.getFile(FILE_UPLOAD_FORM_FIELD_NAME);
    	
    	if (uploadedFile == null) {
    		Logger.error("file upload failed");
    		ctx().flash().put(util.ConfigParameter.FLASH_ERROR_KEY, "File upload failed!");
    		return false;
    	}

    	final String fileName = uploadedFile.getFilename();
    	if (fileName.isEmpty()) {
    		Logger.error("file upload failed; fileName is empty");
        	ctx().flash().put(util.ConfigParameter.FLASH_ERROR_KEY, "File upload failed!");
    		return false;
    	}

    	final AppUser appUser = controllers.account.AccountMgmt.getLocalUser(session());
	    if (appUser == null) {
	    	Logger.error("Something is messed up ... user is not in the database.");
	    	ctx().flash().put(util.ConfigParameter.FLASH_ERROR_KEY, "Please login first to upload files!");
	    	return false;
	    }
	    
	    UserFile userFile = new UserFile();
	    return userFile.processNewFile(appUser, fileName, uploadedFile.getFile());
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

    public static Result displayTopRankedTags() {
    	List<FileTag> fileTagList = FileTag.getMostRanked(ConfigParameter.MAX_TAG_DISPLAY_COUNT);
    	
    	// populate tagTextSize
    	
    	// step 1: find max rank value
    	Integer maxTagRankTmp = Integer.MIN_VALUE;
    	for(FileTag fileTag: fileTagList) {
    		if (fileTag.tagRank > maxTagRankTmp)
    			maxTagRankTmp = fileTag.tagRank;
    	}
    	
    	float maxTagRank = maxTagRankTmp;
    	
    	// step 2: calculate size adjustment coefficient.
    	float k = ConfigParameter.MAX_TEXT_SIZE_TOP / maxTagRank;
    	
    	for(FileTag fileTag: fileTagList) {
    		fileTag.tagTextSize = Math.round(fileTag.tagRank * k);
    	}

    	Collections.shuffle(fileTagList);
    	
    	return ok(views.html.displayTopRankedTags.render("Tag Cloud", fileTagList));
    }

    public static Result displayAllTags() {
    	List<FileTag> fileTagList = FileTag.getAll();
    	
    	// populate tagTextSize
    	
    	// step 1: find max rank value
    	Integer maxTagRankTmp = Integer.MIN_VALUE;
    	for(FileTag fileTag: fileTagList) {
    		if (fileTag.tagRank > maxTagRankTmp)
    			maxTagRankTmp = fileTag.tagRank;
    	}
    	
    	float maxTagRank = maxTagRankTmp;
    	
    	// step 2: calculate size adjustment coefficient.
    	float k = ConfigParameter.MAX_TEXT_SIZE / maxTagRank;
    	for(FileTag fileTag: fileTagList) {
    		fileTag.tagTextSize = Math.round(fileTag.tagRank * k);
    	}
    	
    	return ok(views.html.displayAllTags.render("Tag Cloud", fileTagList));
    }

    public static Result displayTagInfo(Long fileTagId) {
    	FileTag fileTag = FileTag.findById(fileTagId);
    	return ok(views.html.displayTagInfo.render("Tag Info", fileTag));
    }
}
