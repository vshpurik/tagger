package models;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import play.db.ebean.Model;
import play.mvc.Http;
import play.data.format.Formats;

@MappedSuperclass
@SuppressWarnings("serial")
public class CreateUpdateAuditData extends Model {
	
	protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/hh:mm:ss");
	
	@Column(name="create_date")
	@Formats.DateTime(pattern="yyyy-MM-dd hh:mm:ss")
	public Date createDate;
	public String createDateStr() { return dateFormat.format(createDate); }
	
	@Column(name="update_date")
	@Formats.DateTime(pattern="yyyy-MM-dd hh:mm:ss")
	public Date updateDate;
	public String updateDateStr() { return dateFormat.format(updateDate); }
	
	@Column(name="create_ip")
	public String createIp;
	
	@Column(name="update_ip")
	public String updateIp;
	
	@Transient
	protected void initNew() {
		createDate = new Date();
		updateDate = createDate;
		createIp = Http.Context.current().request().remoteAddress();
		updateIp = createIp;
	}

	@Transient
	protected void createAuditData() {
		initNew();
	}
	
	@Transient
	protected void updateAuditData() {
		updateDate = new Date();
		updateIp = Http.Context.current().request().remoteAddress();
	}
	
	@Transient
	protected void prepareForUpdate() {
		updateAuditData();
		
		createDate = null;
		createIp = null;
	}

}
