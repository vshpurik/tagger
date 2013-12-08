package models.account;

import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.annotation.EnumValue;
import com.avaje.ebean.validation.Email;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

import models.CreateUpdateAuditData;
import models.account.SecurityPermission.SecurityPermissionType;
import models.account.SecurityRole.SecurityRoleType;

import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;

//----------------------------

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import models.account.TokenAction.Type;
import models.product.FileTag;
import models.product.UserFile;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;

@Entity
@Table(name="app_user")
@SuppressWarnings("serial")
public class AppUser extends CreateUpdateAuditData implements Subject {

	public static enum Language {
		@EnumValue("English")
		English,
		
		@EnumValue("Spanish")
		Spanish,
		
		@EnumValue("French")
		French,
		
		@EnumValue("German")
		German,
		
		@EnumValue("Italian")
		Italian,
		
		@EnumValue("Russian")
		Russian;
		
		public static Map<String, String> getAsSelectOpts() {
			Map<String, String> result = new TreeMap<String, String>();
			Language [] values = Language.values();
			//Arrays.sort(values);
			for(Language lang: values)
				result.put(lang.name(), lang.name());
			return result;
		}

	}

	public static enum UserType {
		@EnumValue("admin")
		admin,
		
		@EnumValue("customer")
		customer,
		
		@EnumValue("none")
		none;
		
		public static Map<String, String> getAsSelectOpts() {
			Map<String, String> result = new TreeMap<String, String>();
			UserType [] values = UserType.values();
			//Arrays.sort(values);
			for(UserType ut: values)
				if (!ut.equals(UserType.admin))
					result.put(ut.name(), ut.name());
			return result;
		}

	}

	@Id
	@SequenceGenerator(name = "app_user_seq", sequenceName = "app_user_seq", initialValue=1)
	@GeneratedValue(generator = "app_user_seq", strategy=GenerationType.TABLE)
	@Column(name="app_user_id")
	public Long appUserId;
	
	@Required
	@Column(name="name")
	public String name;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date lastLogin;

	@Email
	@Column(name="email")
	public String email;
	
	@Column(name="first_name")
	public String firstName;

	@Column(name="last_name")
	public String lastName;

	@Column(name="language")
	public Language language;
	
	@Column(name="type")
	public UserType type;

	@Column(name="email_validated")
	public boolean emailValidated;

	@Column(name="active")
	public boolean active;
	
	@Lob
	@Column(name="activation_token")
	public String activationToken;

	/**********************************************************************
	 * Relationships
	 **********************************************************************/

	@OneToMany(fetch = FetchType.LAZY, targetEntity = UserFile.class, mappedBy = "appUser", cascade = CascadeType.ALL)
	public List<UserFile> userFiles;

	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.REMOVE)
	@JoinTable(
		name="app_user_file_tag",
		joinColumns={@JoinColumn(name="app_user_id", referencedColumnName="app_user_id")},
		inverseJoinColumns={@JoinColumn(name="file_tag_id", referencedColumnName="file_tag_id")})
	public List<FileTag> userTags;

	@OneToMany(targetEntity = LinkedAccount.class, mappedBy = "appUser", cascade = CascadeType.ALL)
	public List<LinkedAccount> linkedAccounts;

	// Deadbolt RoleHandler interface implementations
	@ManyToMany(cascade=CascadeType.REMOVE)
	@JoinTable(
		name="app_user_security_role",
		joinColumns={@JoinColumn(name="app_user_id", referencedColumnName="app_user_id")},
		inverseJoinColumns={@JoinColumn(name="security_role_id", referencedColumnName="security_role_id")})
	public List<SecurityRole> roles;
	
	@ManyToMany(cascade=CascadeType.REMOVE)
	@JoinTable(
			name="app_user_security_permission",
			joinColumns={@JoinColumn(name="app_user_id", referencedColumnName="app_user_id")},
			inverseJoinColumns={@JoinColumn(name="security_permission_id", referencedColumnName="security_permission_id")})
	public List<SecurityPermission> permissions;

	public List<? extends Role> getRoles() {
		return roles;
	}

	public List<? extends Permission> getPermissions() {
		return permissions;
	}

	public String getIdentifier() {
		return this.appUserId.toString();
	}
	
	
	private static Finder<Long,AppUser> find = new Finder<Long,AppUser>(Long.class, AppUser.class);

	public static AppUser findById(Long appUserId) {
		return find.byId(appUserId);
	}

	public static List<AppUser> getListByUserType(UserType userType) {
		return find.where()
			.eq("type", userType)
			.orderBy("createDate")
			.findList();
	}
	
	/*
	 * Returns total number of rows in database table
	 */
	public static int getRowCount() {
		return find.findRowCount();
	}
	
	/*
	 * Returns instance of APpUser by activationToken value. If not found - returns null.
	 */
	public static AppUser getByActivationToken(String activationToken) {
		return find.where()
				.eq("activationToken", activationToken)
				.findUnique();
	}
	
	/*
	 * find by unique user name
	 */
	public static AppUser findByName(String name) {
		return find.where()
			.eq("name", name)
			.findUnique();
	}

	/*
	 * find by unique domain name
	 */
	public static AppUser findByDomainName(String domainName) {
		return find.where()
			.eq("domainName", domainName)
			.findUnique();
	}

	/*********************************************************************************
	 * 
	 *  Utility methods
	 *  
	 *********************************************************************************/

	public void addTags(List<FileTag> tagList) {
		for(FileTag fileTag: tagList) {
			if (!userTags.contains(fileTag))
				userTags.add(fileTag);
		}

		Logger.debug("AppUser.addTags(): saving tags");
		this.saveManyToManyAssociations("userTags");
	}
	
	private boolean newUserSetup() {
		/*****************************************************************
		 *  create roles and permissions
		 *****************************************************************/
		// For now we will let users have all features available except for the site administrator.
		roles = new ArrayList<SecurityRole>();
		for (SecurityRoleType securityRoleType : SecurityRoleType.values()) {

			// VERY IMPORTANT!!! skip admin role
			if (securityRoleType.equals(SecurityRoleType.admin))
				continue;
			
			roles.add(SecurityRole.findByRoleName(securityRoleType.name()));
		}

		permissions = new ArrayList<SecurityPermission>();
		for (SecurityPermissionType securityPermissionType : SecurityPermissionType.values()) {
			permissions.add(SecurityPermission.findByValue(securityPermissionType.name()));
		}
		
        saveManyToManyAssociations("roles");
        saveManyToManyAssociations("permissions");

        type = UserType.customer;
        
        save();

        return true;
	}

	public static boolean existsByAuthUserIdentity(final AuthUserIdentity identity) {
		final ExpressionList<AppUser> exp;
		if (identity instanceof UsernamePasswordAuthUser) {
			exp = getUsernamePasswordAuthUserFind((UsernamePasswordAuthUser) identity);
		} else {
			exp = getAuthUserFind(identity);
		}
		return exp.findRowCount() > 0;
	}

	private static ExpressionList<AppUser> getAuthUserFind(final AuthUserIdentity identity) {
		return find.where().eq("active", true)
				.eq("linkedAccounts.providerUserId", identity.getId())
				.eq("linkedAccounts.providerKey", identity.getProvider());
	}

	public static AppUser findByAuthUserIdentity(final AuthUserIdentity identity) {
		if (identity == null) {
			return null;
		}
		if (identity instanceof UsernamePasswordAuthUser) {
			return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
		} else {
			return getAuthUserFind(identity).findUnique();
		}
	}

	public static AppUser findByUsernamePasswordIdentity(final UsernamePasswordAuthUser identity) {
		return getUsernamePasswordAuthUserFind(identity).findUnique();
	}

	private static ExpressionList<AppUser> getUsernamePasswordAuthUserFind(final UsernamePasswordAuthUser identity) {
		return getEmailUserFind(identity.getEmail())
			.eq("linkedAccounts.providerKey", identity.getProvider());
	}

	public void merge(final AppUser otherUser) {
		for (final LinkedAccount acc : otherUser.linkedAccounts) {
			this.linkedAccounts.add(LinkedAccount.create(acc));
		}
		// do all other merging stuff here - like resources, etc.

		// deactivate the merged user that got added to this one
		otherUser.active = false;
		Ebean.save(Arrays.asList(new AppUser[] { otherUser, this }));
	}

	public static AppUser create(final AuthUser authUser) {
		final AppUser appUser = new AppUser();
		appUser.initNew();
		
		appUser.language = AppUser.Language.English;
		appUser.active = true;
		appUser.lastLogin = new Date();
		appUser.linkedAccounts = Collections.singletonList(LinkedAccount.create(authUser));

		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			appUser.email = identity.getEmail();
			appUser.emailValidated = false;
		}

		if (authUser instanceof NameIdentity) {
			final NameIdentity identity = (NameIdentity) authUser;
			final String name = identity.getName();
			if (name != null) {
				appUser.name = name;
			}
		}

		appUser.save();
		
		appUser.newUserSetup();
		
		return appUser;
	}

	public static void merge(final AuthUser oldUser, final AuthUser newUser) {
		AppUser.findByAuthUserIdentity(oldUser).merge(
				AppUser.findByAuthUserIdentity(newUser));
	}

	public Set<String> getProviders() {
		final Set<String> providerKeys = new HashSet<String>(
				linkedAccounts.size());
		for (final LinkedAccount acc : linkedAccounts) {
			providerKeys.add(acc.providerKey);
		}
		return providerKeys;
	}

	public static void addLinkedAccount(final AuthUser oldUser,
			final AuthUser newUser) {
		final AppUser u = AppUser.findByAuthUserIdentity(oldUser);
		u.linkedAccounts.add(LinkedAccount.create(newUser));
		u.save();
	}

	public static void setLastLoginDate(final AuthUser knownUser) {
		final AppUser u = AppUser.findByAuthUserIdentity(knownUser);
		u.lastLogin = new Date();
		u.save();
	}

	public static AppUser findByEmail(final String email) {
		return getEmailUserFind(email).findUnique();
	}

	private static ExpressionList<AppUser> getEmailUserFind(final String email) {
		return find.where().eq("active", true).eq("email", email);
	}

	public LinkedAccount getAccountByProvider(final String providerKey) {
		return LinkedAccount.findByProviderKey(this, providerKey);
	}

	public static void verify(final AppUser unverified) {
		unverified.emailValidated = true;
		unverified.save();
		TokenAction.deleteByUser(unverified, Type.EMAIL_VERIFICATION);
	}

	public void changePassword(final UsernamePasswordAuthUser authUser, final boolean create) {
		LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
		
		if (a == null) {
			if (create) {
				a = LinkedAccount.create(authUser);
				a.appUser = this;
			} else {
				throw new RuntimeException("Account not enabled for password usage");
			}
		}
		
		a.providerUserId = authUser.getHashedPassword();
		a.save();
	}

	public void resetPassword(final UsernamePasswordAuthUser authUser, final boolean create) {
		// You might want to wrap this into a transaction
		this.changePassword(authUser, create);
		TokenAction.deleteByUser(this, Type.PASSWORD_RESET);
	}
	
	public boolean isAdmin() {
		return type.equals(UserType.admin);
	}
}
