package com.unifun.model;



import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;


public class SmsData implements Serializable {

	private static final long serialVersionUID = -5504964926459738529L;

	private short dcs;
	private String dlrResponseType;
	private byte esmClass = 0;
	private String fromAD;
	private String fromNP;
	private String fromTON;
	private Timestamp inserted;
	private String message;
	private short pid;
	private String priority;
	private short quantity;
	private byte ReplaceIfPresent;
	private String segmentLen;
	private Timestamp senduntil;
	private int serviceId;
	private Integer systemId;
	private Long toAD;
	private String toAN;
	private String toNP;
	private Integer transactionId;
	private Long validity;

	public SmsData() {
	}

	public SmsData(Integer transactionId, String fromAD, String fromTON, String fromNP, Long toAD, String toAN,
	               String toNP, String message, short quantity, short dcs, short pid, Timestamp inserted, Timestamp senduntil,
	               Integer systemId, String dlrResponseType, String priority, String segmentLen, int serviceId) {
		super();
		this.transactionId = transactionId;
		this.fromAD = fromAD;
		this.fromTON = fromTON;
		this.fromNP = fromNP;
		this.toAD = toAD;
		this.toAN = toAN;
		this.toNP = toNP;
		this.message = message;
		this.quantity = quantity;
		this.dcs = dcs;
		this.pid = pid;
		this.inserted = inserted;
		this.senduntil = senduntil;
		this.systemId = systemId;
		this.dlrResponseType = dlrResponseType;
		this.priority = priority;
		this.segmentLen = segmentLen;
		this.serviceId = serviceId;
	}


	public short getDcs() {
		return dcs;
	}

	public String getDlrResponseType() {
		return dlrResponseType;
	}

	public byte getEsmClass() {
		return esmClass;
	}

	public String getFromAD() {
		return fromAD;
	}

	public String getFromNP() {
		return fromNP;
	}

	public String getFromTON() {
		return fromTON;
	}

	public Timestamp getInserted() {
		return inserted;
	}

	public String getMessage() {
		return message;
	}

	public short getPid() {
		return pid;
	}

	public String getPriority() {
		return priority;
	}

	public short getQuantity() {
		return quantity;
	}

	public byte getReplaceIfPresent() {
		return ReplaceIfPresent;
	}

	public String getSegmentLen() {
		return segmentLen;
	}

	public Timestamp getSenduntil() {
		return senduntil;
	}

	public int getServiceId() {
		return serviceId;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public Long getToAD() {
		return toAD;
	}

	public String getToAN() {
		return toAN;
	}

	public String getToNP() {
		return toNP;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public Long getValidity() {
		return validity;
	}

	public void setDcs(short dcs) {
		this.dcs = dcs;
	}

	public void setDlrResponseType(String dlrResponseType) {
		this.dlrResponseType = dlrResponseType;
	}

	public void setEsmClass(byte esmClass) {
		this.esmClass = esmClass;
	}

	public void setFromAD(String fromAD) {
		this.fromAD = fromAD;
	}

	public void setFromNP(String fromNP) {
		this.fromNP = fromNP;
	}

	public void setFromTON(String fromTON) {
		this.fromTON = fromTON;
	}

	public void setInserted(Timestamp inserted) {
		this.inserted = inserted;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPid(short pid) {
		this.pid = pid;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setQuantity(short quantity) {
		this.quantity = quantity;
	}

	public void setReplaceIfPresent(byte replaceIfPresent) {
		ReplaceIfPresent = replaceIfPresent;
	}

	public void setSegmentLen(String segmentLen) {
		this.segmentLen = segmentLen;
	}

	public void setSenduntil(Timestamp senduntil) {
		this.senduntil = senduntil;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public void setToAD(Long toAD) {
		this.toAD = toAD;
	}

	public void setToAN(String toAN) {
		this.toAN = toAN;
	}

	public void setToNP(String toNP) {
		this.toNP = toNP;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public void setValidity(Long validity) {
		this.validity = validity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SmsData smsData = (SmsData) o;
		return dcs == smsData.dcs &&
				esmClass == smsData.esmClass &&
				pid == smsData.pid &&
				quantity == smsData.quantity &&
				ReplaceIfPresent == smsData.ReplaceIfPresent &&
				serviceId == smsData.serviceId &&
				Objects.equals(dlrResponseType, smsData.dlrResponseType) &&
				Objects.equals(fromAD, smsData.fromAD) &&
				Objects.equals(fromNP, smsData.fromNP) &&
				Objects.equals(fromTON, smsData.fromTON) &&
				Objects.equals(inserted, smsData.inserted) &&
				Objects.equals(message, smsData.message) &&
				Objects.equals(priority, smsData.priority) &&
				Objects.equals(segmentLen, smsData.segmentLen) &&
				Objects.equals(senduntil, smsData.senduntil) &&
				Objects.equals(systemId, smsData.systemId) &&
				Objects.equals(toAD, smsData.toAD) &&
				Objects.equals(toAN, smsData.toAN) &&
				Objects.equals(toNP, smsData.toNP) &&
				Objects.equals(transactionId, smsData.transactionId) &&
				Objects.equals(validity, smsData.validity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dcs, dlrResponseType, esmClass, fromAD, fromNP, fromTON, inserted, message, pid, priority, quantity, ReplaceIfPresent, segmentLen, senduntil, serviceId, systemId, toAD, toAN, toNP, transactionId, validity);
	}

	@Override
	public String toString() {
		return "SmsData{" +
				"dcs=" + dcs +
				", dlrResponseType='" + dlrResponseType + '\'' +
				", esmClass=" + esmClass +
				", fromAD='" + fromAD + '\'' +
				", fromNP='" + fromNP + '\'' +
				", fromTON='" + fromTON + '\'' +
				", inserted=" + inserted +
				", message='" + message + '\'' +
				", pid=" + pid +
				", priority='" + priority + '\'' +
				", quantity=" + quantity +
				", ReplaceIfPresent=" + ReplaceIfPresent +
				", segmentLen='" + segmentLen + '\'' +
				", senduntil=" + senduntil +
				", serviceId=" + serviceId +
				", systemId=" + systemId +
				", toAD=" + toAD +
				", toAN='" + toAN + '\'' +
				", toNP='" + toNP + '\'' +
				", transactionId=" + transactionId +
				", validity=" + validity +
				'}';
	}
}
