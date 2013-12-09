package models.product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.avaje.ebean.Ebean;
import com.google.common.base.CharMatcher;

import play.Logger;
import util.ConfigParameter;

import models.CreateUpdateAuditData;
import models.account.AppUser;

@Entity
@Table(name="user_file")
@SuppressWarnings("serial")
public class UserFile extends CreateUpdateAuditData {
	@Id
	@SequenceGenerator(name = "user_file_seq", sequenceName = "user_file_seq", initialValue=1)
	@GeneratedValue(generator = "user_file_seq", strategy=GenerationType.TABLE)
	@Column(name="user_file_id")
	public Long userFileId;

	@Column(name="file_name")
	public String fileName;
	
	// @Lob
	// @Column(name="file_content")
	// public String fileContent;
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = AppUser.class)
	@JoinColumn(name="app_user_id")
	public AppUser appUser;

	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.REMOVE)
	@JoinTable(
		name="user_file_file_tag",
		joinColumns={@JoinColumn(name="user_file_id", referencedColumnName="user_file_id")},
		inverseJoinColumns={@JoinColumn(name="file_tag_id", referencedColumnName="file_tag_id")})
	public List<FileTag> tags;

	// used during semantic processing of the uploaded file; holds a set of unique words found during file processing.
	@Transient
	private Set<String> tagSet;

	// used during semantic processing of the uploaded file; holds a map of unique UserTags found during file processing.
	@Transient
	private Map<String, FileTag> tagMap;
	
	private static Finder<Long,UserFile> find = new Finder<Long,UserFile>(Long.class, UserFile.class);
	
	public static UserFile findById(Long userFileId) {
		return find.byId(userFileId);
	}
	
	public static List<UserFile> getListByUser(AppUser appUser) {
		return find.where()
			.eq("appUser", appUser)
			.orderBy("userFileId")
			.findList();
	}

	public boolean processNewFile(AppUser appUser, String fileName, File uploadedFile) {
		initNew();

		this.tags = new ArrayList<FileTag>();
		this.tagSet = new HashSet<String>();
		this.tagMap = new HashMap<String, FileTag>();
		
		this.appUser = appUser;
		this.fileName = fileName;
		return processFile(uploadedFile);
	}
	
	private boolean processFile(File uploadedFile) {
		
		StringBuilder fileContent = new StringBuilder((int)uploadedFile.length());

		Scanner scanner;
		try {
			// scanner = new Scanner(uploadedFile);
			scanner = new Scanner((Readable) new BufferedReader(new FileReader(uploadedFile)));
		} catch (FileNotFoundException e) {
			Logger.error("Uploaded file can not be found.");
			return false;
		}

		String lineSeparator = System.getProperty("line.separator");

		try {
			while(scanner.hasNextLine()) {
				Logger.info("processing line from file ...");
				String line = scanner.nextLine();
				fileContent.append(line + lineSeparator);
				findTags(line);
			}
			
			// this.fileContent = fileContent.toString();
			save();

			for(Entry<String, FileTag> e: tagMap.entrySet()) {
				Logger.debug("adding " + e.getKey());
				tags.add(e.getValue());
			}
    		
			Logger.debug("UserFile.processFile(): saving tags");
			this.saveManyToManyAssociations("tags");

			this.appUser.addTags(tags);

			Ebean.beginTransaction();
			try {
				for(FileTag fileTag: tagMap.values()) {
					int rankIncrement = fileTag.tagRankIncrement;
					fileTag.refresh();
					fileTag.tagRank += rankIncrement;
					fileTag.save();
				}
				Ebean.commitTransaction();
			} finally {
				Ebean.endTransaction();
			}
    		
            return true;
            
        } finally {
            scanner.close();
        }

    }
	
	private void findTags(String line) {
		String[] words = line.split(" ");
		for(String word: words) {
			
			// remove all characters but letters
			word = 	CharMatcher.JAVA_LETTER.retainFrom(word).toLowerCase();
			
			// filter out "useless" words
			if (isUseless(word))
				continue;
			
			if (tagSet.contains(word)) {
				// this word appeared more than once in the uploaded file, add it to the tagMap
				
				// check if tagMap already has this tag
				FileTag fileTag = tagMap.get(word);
				if (fileTag == null) {
					// new tag, add it to the map
					fileTag = FileTag.getByTagName(word);
					tagMap.put(word, fileTag);
				} else {
					// just increment rank count
					++fileTag.tagRankIncrement;
				}
			} else {
				// this is the first time this word appeared in the uploaded file, add it to the tagSet
				tagSet.add(word);
			}
		}
	}

	/**
	 * This method checks if word should not be used for tagging.
	 * @param word
	 * @return true if word should not be used as a tag
	 */
	private static boolean isUseless(String word) {
		if (word.length() < ConfigParameter.FILE_TAG_MIN_LENGTH)
			return true;
		
		for(String uselessWord: ConfigParameter.USELESS_WORDS)
			if (word.equals(uselessWord))
				return true;
		
		return false;
	}
}
