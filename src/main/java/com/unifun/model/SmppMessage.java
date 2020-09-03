package com.unifun.model;


import java.util.Objects;

public class SmppMessage  {

	private static final long serialVersionUID = 6470376628364004368L;

	private Long absoluteValidityPeriod;
	private String channel;
	private Integer languageId;
	private Integer notificationServiceId;
	private Long relativeValidityPeriod;
	private SmsData smsData;
	private Integer sourceId;

	public SmppMessage(SmsData smsData) {
		this.smsData = smsData;
		this.languageId = 0;
		this.sourceId = smsData.getSystemId();
		setChannel("SMPP");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SmppMessage that = (SmppMessage) o;
		return Objects.equals(channel, that.channel) && Objects.equals(smsData, that.smsData)
				&& Objects.equals(sourceId, that.sourceId)
				&& Objects.equals(notificationServiceId, that.notificationServiceId)
				&& Objects.equals(languageId, that.languageId)
				&& Objects.equals(relativeValidityPeriod, that.relativeValidityPeriod);
	}


	public Long getAbsoluteValidityPeriod() {
		return absoluteValidityPeriod;
	}


	public String getChannel() {
		// TODO Auto-generated method stub
		return channel;
	}


	public Long getDestAddress() {
		// TODO Auto-generated method stub
		return smsData.getToAD();
	}


	public void setChannel(String channel) {
		this.channel = channel;
	}


	public String getMessageText() {
		return smsData.getMessage();
	}


	public Integer getLanguageId() {
		return languageId;
	}


	public Object getObject() {
		return smsData;
	}

	public Long getRelativeValidityPeriod() {
		return relativeValidityPeriod;
	}

	public SmsData getSmsData() {
		return smsData;
	}


	public String getSourceAddress() {
		return smsData.getFromAD();
	}


	public void setLanguageId(Integer langId) {
		this.languageId = langId;
	}


	public Integer getTransactionId() {
		return this.smsData.getTransactionId();
	}


	public Integer getNotificationServiceId() {
		return this.notificationServiceId;
	}


	public void setAbsoluteValidityPeriod(Long absoluteValidityPeriod) {
		this.absoluteValidityPeriod = absoluteValidityPeriod;
	}


	public void setNotificationServiceId(Integer serviceId) {
		this.notificationServiceId = serviceId;
	}


	public Integer getSourceId() {
		return sourceId;
	}


	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}


	public void setMessageText(String messageText) {
		smsData.setMessage(messageText);
	}

	@Override
	public int hashCode() {
		return Objects.hash(channel, smsData, sourceId, notificationServiceId, languageId, relativeValidityPeriod);
	}


	public void setObject(Object messageObject) {
		this.smsData = (SmsData) messageObject;
	}


	public void setRelativeValidityPeriod(Long relativeValidityPeriod) {
		this.relativeValidityPeriod = relativeValidityPeriod;
	}

	public void setSmsData(SmsData smsData) {
		this.smsData = smsData;
	}


	public void setSourceAddress(String sourceAddress) {
		smsData.setFromAD(sourceAddress);
	}


	public void setDestAddress(Long destAddress) {
		smsData.setToAD(destAddress);
	}


	public void setTransactionId(Integer transactionId) {
		this.smsData.setTransactionId(transactionId);
	}
}