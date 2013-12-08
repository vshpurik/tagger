package models.account;

import java.util.List;

import be.objectify.deadbolt.core.models.Permission;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="security_permission")
@SuppressWarnings("serial")
public class SecurityPermission extends Model implements Permission
{
	public static enum SecurityPermissionType {
		create,
		read,
		update,
		delete
	}

	@Id
	@SequenceGenerator(name = "security_permission_seq", sequenceName = "security_permission_seq", initialValue=1)
	@GeneratedValue(generator = "security_permission_seq", strategy=GenerationType.TABLE)
	@Column(name="security_permission_id")
	public Long securityPermissionid;

	@Column(name="value")
    public String value;

	public String getValue() {
		return value;
	}

	@ManyToMany
	@JoinTable(
			name="app_user_security_permission",
			joinColumns={@JoinColumn(name="security_permission_id", referencedColumnName="security_permission_id")},
			inverseJoinColumns={@JoinColumn(name="app_user_id", referencedColumnName="app_user_id")})
	public List<AppUser> appUsers;
	
	public static final Model.Finder<Long, SecurityPermission> find =
		new Model.Finder<Long, SecurityPermission>(Long.class, SecurityPermission.class);
    
	public static SecurityPermission findByValue(String value) {
	return find
		.where()
		.eq("value", value)
		.findUnique();
	}
}