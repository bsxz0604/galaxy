package models.users;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class AccountAppId implements Serializable {
	
	@Column(name="account_id", nullable=false)
	public String accountId;
	@Column(name="app_id", nullable=false)
	public String appId;
	
	public AccountAppId(String accountId, String appId) {
		this.accountId = accountId;
		this.appId = appId;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final AccountAppId other = (AccountAppId)obj;
        
        if (this.accountId.compareTo(other.accountId) != 0) {
        	return false;
        }
        
        if (this.appId.compareTo(other.appId) != 0) {
        	return false;
        }
        
        return true;
	}
	
	@Override
	public int hashCode() {
		int hash = this.accountId.hashCode() + this.appId.hashCode();
		
		return hash;
	}
}
