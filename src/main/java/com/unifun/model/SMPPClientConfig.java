package com.unifun.model;

import com.unifun.utils.SMPPLinkType;

import java.util.Objects;

public class SMPPClientConfig {
	private String bindType;
	private Integer clientPriority;
	private String concatenateType;
	private String dlrIdType;
	private Integer groupID;
	private String host;
	private int id;
	private SMPPLinkType linkType;
	private String np;
	private String password;
	private int pduProcessorDegree;
	private int port;
	private int reconnectTries;
	private int reconnectTriesTime;
	private String remoteIdType = "LONG";
	private String serviceType;
	private int speedLimit;
	private String systemId;
	private String systemType;
	private int timeOut;
	private String ton;

	public SMPPClientConfig() {
	}

	public SMPPClientConfig(int id, SMPPLinkType linkType, String systemId, String password, Integer groupID,
	                        Integer clientPriority, String concatenateType, String systemType, String serviceType, String ton,
	                        String np, String host, int port, int timeOut, int pduProcessorDegree, String bindType, int reconnectTries,
	                        int reconnectTriesTime, int speedLimit, String remoteIdType, String dlrIdType) {
		this.id = id;
		this.linkType = linkType;
		this.systemId = systemId;
		this.password = password;
		this.groupID = groupID;
		this.clientPriority = clientPriority;
		this.concatenateType = concatenateType;
		this.systemType = systemType;
		this.serviceType = serviceType;
		this.ton = ton;
		this.np = np;
		this.host = host;
		this.port = port;
		this.timeOut = timeOut;
		this.pduProcessorDegree = pduProcessorDegree;
		this.bindType = bindType;
		this.reconnectTries = reconnectTries;
		this.reconnectTriesTime = reconnectTriesTime;
		this.speedLimit = speedLimit;
		this.remoteIdType = remoteIdType;
		this.dlrIdType = dlrIdType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SMPPClientConfig that = (SMPPClientConfig) o;
		return id == that.id && port == that.port && timeOut == that.timeOut
				&& pduProcessorDegree == that.pduProcessorDegree && reconnectTries == that.reconnectTries
				&& reconnectTriesTime == that.reconnectTriesTime && speedLimit == that.speedLimit
				&& linkType == that.linkType && Objects.equals(systemId, that.systemId)
				&& Objects.equals(password, that.password) && Objects.equals(groupID, that.groupID)
				&& Objects.equals(clientPriority, that.clientPriority)
				&& Objects.equals(concatenateType, that.concatenateType) && Objects.equals(systemType, that.systemType)
				&& Objects.equals(serviceType, that.serviceType) && Objects.equals(ton, that.ton)
				&& Objects.equals(np, that.np) && Objects.equals(host, that.host)
				&& Objects.equals(bindType, that.bindType) && Objects.equals(remoteIdType, that.remoteIdType)
				&& Objects.equals(dlrIdType, that.dlrIdType);
	}

	public String getBindType() {
		return bindType;
	}

	public Integer getClientPriority() {
		return clientPriority;
	}

	public String getConcatenateType() {
		return concatenateType;
	}

	public String getDlrIdType() {
		return dlrIdType;
	}

	public Integer getGroupID() {
		return groupID;
	}

	public String getHost() {
		return host;
	}

	public int getId() {
		return id;
	}

	public SMPPLinkType getLinkType() {
		return linkType;
	}

	public String getNp() {
		return np;
	}

	public String getPassword() {
		return password;
	}

	public int getPduProcessorDegree() {
		return pduProcessorDegree;
	}

	public int getPort() {
		return port;
	}

	public int getReconnectTries() {
		return reconnectTries;
	}

	public int getReconnectTriesTime() {
		return reconnectTriesTime;
	}

	public String getRemoteIdType() {
		return remoteIdType;
	}

	public String getServiceType() {
		return serviceType;
	}

	public int getSpeedLimit() {
		return speedLimit;
	}

	public String getSystemId() {
		return systemId;
	}

	public String getSystemType() {
		return systemType;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public String getTon() {
		return ton;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, linkType, systemId, password, groupID, clientPriority, concatenateType, systemType,
				serviceType, ton, np, host, port, timeOut, pduProcessorDegree, bindType, reconnectTries,
				reconnectTriesTime, speedLimit, remoteIdType, dlrIdType);
	}

	public void setBindType(String bindType) {
		this.bindType = bindType;
	}

	public void setClientPriority(Integer clientPriority) {
		this.clientPriority = clientPriority;
	}

	public void setConcatenateType(String concatenateType) {
		this.concatenateType = concatenateType;
	}

	public void setDlrIdType(String dlrIdType) {
		this.dlrIdType = dlrIdType;
	}

	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLinkType(SMPPLinkType linkType) {
		this.linkType = linkType;
	}

	public void setNp(String np) {
		this.np = np;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPduProcessorDegree(int pduProcessorDegree) {
		this.pduProcessorDegree = pduProcessorDegree;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setReconnectTries(int reconnectTries) {
		this.reconnectTries = reconnectTries;
	}

	public void setReconnectTriesTime(int reconnectTriesTime) {
		this.reconnectTriesTime = reconnectTriesTime;
	}

	public void setRemoteIdType(String remoteIdType) {
		this.remoteIdType = remoteIdType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public void setSpeedLimit(int speedLimit) {
		this.speedLimit = speedLimit;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public void setTon(String ton) {
		this.ton = ton;
	}
}
