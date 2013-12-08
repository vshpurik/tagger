package models.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import play.db.ebean.Model;

import com.feth.play.module.pa.user.AuthUser;

@Entity
@Table(name="linked_account")
@SuppressWarnings("serial")
public class LinkedAccount extends Model {

	@Id
	@SequenceGenerator(name = "linked_account_seq", sequenceName = "linked_account_seq", initialValue=1)
	@GeneratedValue(generator = "linked_account_seq", strategy=GenerationType.TABLE)
	@Column(name="linked_account_id")
	public Long linkedAccountId;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = AppUser.class)
	@JoinColumn(name="app_user_id")
	public AppUser appUser;

	public String providerUserId;
	public String providerKey;

	public static final Finder<Long, LinkedAccount> find = new Finder<Long, LinkedAccount>(
			Long.class, LinkedAccount.class);

	public static LinkedAccount findByProviderKey(final AppUser appUser, String key) {
		return find
			.where()
			.eq("appUser", appUser)
			.eq("providerKey", key)
			.findUnique();
	}

	public static LinkedAccount create(final AuthUser authUser) {
		final LinkedAccount ret = new LinkedAccount();
		ret.update(authUser);
		return ret;
	}
	
	public void update(final AuthUser authUser) {
		this.providerKey = authUser.getProvider();
		this.providerUserId = authUser.getId();
	}

	public static LinkedAccount create(final LinkedAccount acc) {
		final LinkedAccount ret = new LinkedAccount();
		ret.providerKey = acc.providerKey;
		ret.providerUserId = acc.providerUserId;

		return ret;
	}
}