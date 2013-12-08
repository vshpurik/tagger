package models.account;

import java.util.List;

import be.objectify.deadbolt.core.models.Role;
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
@Table(name="security_role")
@SuppressWarnings("serial")
public class SecurityRole extends Model implements Role
{
	/*
	 * IMPORTANT: the following string constant values have to match enum names
	 *            in the SecurityRoleType enum.
	 */
	public static final String SecurityRoleAdmin       = "admin";
	public static final String SecurityRoleCustomer    = "customer";
	
	public static enum SecurityRoleType {
		admin,
		customer
	}
	
	@Id
	@SequenceGenerator(name = "security_role_seq", sequenceName = "security_role_seq", initialValue=1)
	@GeneratedValue(generator = "security_role_seq", strategy=GenerationType.TABLE)
	@Column(name="security_role_id")
	public Long securityRoleId;

	@Column(name="role_name")
	public String roleName;

	@ManyToMany
	@JoinTable(
		name="app_user_security_role",
		joinColumns={@JoinColumn(name="security_role_id", referencedColumnName="security_role_id")},
		inverseJoinColumns={@JoinColumn(name="app_user_id", referencedColumnName="app_user_id")})
	public List<AppUser> appUsers;

	public static final Finder<Long, SecurityRole> find = 
		new Finder<Long, SecurityRole>(Long.class, SecurityRole.class);

	public String getName() {
    	return roleName;
	}

	public static SecurityRole findByRoleName(String roleName) {
		return find
			.where()
			.eq("roleName", roleName)
			.findUnique();
	}
}
