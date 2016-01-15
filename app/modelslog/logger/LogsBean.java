package modelslog.logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.databind.JsonNode;

import play.db.ebean.Model;

@Entity
public class LogsBean extends Model{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	@Column(name="user_id")
	public String userId;				//user_id
	@Column(name="operation_time")
	public String operationTime;		//operation_time
	@Column(name="request", length=2000)			
	public String request;				//request
	@Column(name="response")		
	public String response;			    //response
	@Column(name="method")
	public String method;				//method
	@Column(name="url")
	public String url;					//url
	@Column(name="ip")
	public String ip;					//ip_address	
}
