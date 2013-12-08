package models.product;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.Logger;

import models.CreateUpdateAuditData;
import models.account.AppUser;

@Entity
@Table(name="file_tag")
@SuppressWarnings("serial")
public class FileTag extends CreateUpdateAuditData {

	@Id
	@SequenceGenerator(name = "file_tag_seq", sequenceName = "file_tag_seq", initialValue=1)
	@GeneratedValue(generator = "file_tag_seq", strategy=GenerationType.TABLE)
	@Column(name="file_tag_id")
	public Long fileTagId;

	@Column(name="tag_name")
	public String tagName;

	@Column(name="tag_rank")
	public Integer tagRank;

	@Transient
	public int tagRankIncrement = 1;

	@Transient
	public int tagTextSize;

	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.REMOVE)
	@JoinTable(
		name="user_file_file_tag",
		joinColumns={@JoinColumn(name="file_tag_id", referencedColumnName="file_tag_id")},
		inverseJoinColumns={@JoinColumn(name="user_file_id", referencedColumnName="user_file_id")})
	public List<UserFile> userFiles;

	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.REMOVE)
	@JoinTable(
		name="app_user_file_tag",
		joinColumns={@JoinColumn(name="file_tag_id", referencedColumnName="file_tag_id")},
		inverseJoinColumns={@JoinColumn(name="app_user_id", referencedColumnName="app_user_id")})
	public List<AppUser> appUsers;

	private static Finder<Long,FileTag> find = new Finder<Long,FileTag>(Long.class, FileTag.class);
	
	public static FileTag findById(Long fileTagId) {
		return find.byId(fileTagId);
	}

	public static List<FileTag> getAll() {
		return find.all();
	}

	public static List<FileTag> getMostRanked(int maxRows) {
		return find
			.orderBy("tagRank")
			.setMaxRows(maxRows)
			.findList();
	}

	public static FileTag getByTagName(String tagName) {
		
		Logger.debug("getByTagName start");
		
		FileTag fileTag = find
			.where()
			.eq("tagName", tagName)
			.findUnique();
		
		if (fileTag == null) {
			fileTag = new FileTag();
			fileTag.initNew(tagName);
			fileTag.save();
		}
		
		Logger.debug("getByTagName finish");
		
		return fileTag;
	}
	
	public boolean initNew(String tagName) {
		initNew();
		this.tagName = tagName;
		this.tagRank = 0;
		this.tagRankIncrement = 1;
		return true;
	}
}
