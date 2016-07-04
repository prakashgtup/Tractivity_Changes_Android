/**
 * 
 */
package com.trac.android.tractivity.configuration;

/**
 * Helper class to hold network details
 *
 */
public class NetworkMemoryDetails {

	public String network_name,network_address,modified_date,host_ip,port,local_path;
	public String getHost_ip() {
		return host_ip;
	}

	public void setHost_ip(String host_ip) {
		this.host_ip = host_ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getLocal_path() {
		return local_path;
	}

	public void setLocal_path(String local_path) {
		this.local_path = local_path;
	}

	public int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNetwork_name() {
		return network_name;
	}

	public void setNetwork_name(String network_name) {
		this.network_name = network_name;
	}

	public String getNetwork_address() {
		return network_address;
	}

	public void setNetwork_address(String network_address) {
		this.network_address = network_address;
	}

	public String getModified_date() {
		return modified_date;
	}

	public void setModified_date(String modified_date) {
		this.modified_date = modified_date;
	}
	
	
	
}
