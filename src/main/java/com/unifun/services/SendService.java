package com.unifun.services;


import com.unifun.model.SmsData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.*;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class SendService implements Runnable {
	private static final Logger logger = LogManager.getLogger(SendService.class);
	private ClientService clientService = ClientService.getInstance();
	private SmsData smsData;
	private AtomicInteger tps;

	public SendService(SmsData smsData, AtomicInteger tps) {
		this.smsData = smsData;
		this.tps = tps;
	}
	@Override
	public void run() {

		final StringBuffer buffer = new StringBuffer();
		if (smsData == null) {
			logger.warn(buffer.delete(0, buffer.length()).append("sms data is null"));
			return;
		}
		if (smsData.getValidity() != null && smsData.getValidity() == 0) {
			smsData.setValidity(null);
		}

		String remoteMessageId = null;
		String errorMessage = null;

		Charset cs = clientService.getCharset(smsData.getDcs());
		long remoteId = -1L;
		try {
			short segments = smsData.getQuantity();
			if (clientService.getSession().getSessionState().isBound()) {
				if (segments > 1) {
					short refNum = (short) (smsData.getTransactionId() % 256);
					int segmetnLength = Integer.parseInt(smsData.getSegmentLen());
					int segmentMax;
					int messageLen = smsData.getMessage().length();
					switch (clientService.getConfig().getConcatenateType()) {
						case "1": // UDH
							byte[] udh = new byte[6];
							udh[0] = 0x05;
							udh[1] = 0x00;
							udh[2] = 0x03;
							udh[3] = (byte) refNum;
							udh[4] = (byte) smsData.getQuantity();

							byte[] moreMessageToSend = new byte[1];
							moreMessageToSend[0] = 1;

							for (int i = 0; i < smsData.getQuantity(); i++) {
								short currentStep = (short) (i + 1);
								segmentMax = Math.min(segmetnLength * currentStep, messageLen);
								byte[] segment = smsData.getMessage().substring(segmetnLength * i, segmentMax).getBytes(cs);
								udh[5] = (byte) currentStep;
								byte[] message = Arrays.copyOf(udh, udh.length + segment.length);
								System.arraycopy(segment, 0, message, udh.length, segment.length);
								remoteMessageId = clientService.getSession().submitShortMessage(
										clientService.getConfig().getServiceType(),
										TypeOfNumber.valueOf(Integer.valueOf(smsData.getFromTON()).byteValue()),
										NumberingPlanIndicator.valueOf(Integer.valueOf(smsData.getFromNP()).byteValue()),
										smsData.getFromAD(),
										TypeOfNumber.valueOf(Integer.valueOf(smsData.getToAN()).byteValue()),
										NumberingPlanIndicator.valueOf(Integer.valueOf(smsData.getToNP()).byteValue()),
										String.valueOf(smsData.getToAD()),
										new ESMClass(MessageMode.STORE_AND_FORWARD, MessageType.DEFAULT,
												GSMSpecificFeature.UDHI),
										Integer.valueOf(smsData.getPid()).byteValue(),
										Integer.valueOf(smsData.getPriority()).byteValue(), null,
										clientService.getAbsoluteExpiredTime(smsData.getValidity())
										// ,clientController.getExpiredIn(smsData.getSmsLifeTimeInMinutes())
										,
										new RegisteredDelivery(currentStep == smsData.getQuantity()
												? Integer.parseInt(smsData.getDlrResponseType())
												: 0),
										smsData.getReplaceIfPresent(), DataCodings.newInstance((byte) smsData.getDcs()),
										(byte) 0// refNum//(smsData.getTransactionId() % 256)
										, message, OptionalParameters.deserialize(OptionalParameter.Tag.MORE_MESSAGES_TO_SEND.code(),
												moreMessageToSend)/* new OptionalParameter[0] */);

							}
							break;
						case "2": // SAR
							OptionalParameter sarMsgRefNum = OptionalParameters.newSarMsgRefNum(refNum);
							OptionalParameter sarTotalSegments = OptionalParameters
									.newSarTotalSegments(smsData.getQuantity());
							for (int i = 0; i < smsData.getQuantity(); i++) {
								short currentStep = (short) (i + 1);
								segmentMax = Math.min(segmetnLength * currentStep, messageLen);
								remoteMessageId = clientService.getSession().submitShortMessage(
										clientService.getConfig().getServiceType(),
										TypeOfNumber.valueOf(Integer.valueOf(smsData.getFromTON()).byteValue()),
										NumberingPlanIndicator.valueOf(Integer.valueOf(smsData.getFromNP()).byteValue()),
										smsData.getFromAD(),
										TypeOfNumber.valueOf(Integer.valueOf(smsData.getToAN()).byteValue()),
										NumberingPlanIndicator.valueOf(Integer.valueOf(smsData.getToNP()).byteValue()),
										String.valueOf(smsData.getToAD()), new ESMClass(smsData.getEsmClass()),
										Integer.valueOf(smsData.getPid()).byteValue(),
										Integer.valueOf(smsData.getPriority()).byteValue(), null,
										clientService.getAbsoluteExpiredTime(smsData.getValidity())
										// ,clientController.getExpiredIn(smsData.getSmsLifeTimeInMinutes())
										,
										new RegisteredDelivery(currentStep == smsData.getQuantity()
												? Integer.parseInt(smsData.getDlrResponseType())
												: 0),
										smsData.getReplaceIfPresent(), DataCodings.newInstance((byte) smsData.getDcs()),
										(byte) 0// refNum//(smsData.getTransactionId() % 256)
										, smsData.getMessage().substring(segmetnLength * i, segmentMax).getBytes(cs),
										sarMsgRefNum, OptionalParameters.newSarSegmentSeqnum(currentStep),
										sarTotalSegments);

							}
							break;
						case "3": // PayLoad
							remoteMessageId = clientService.getSession()
									.submitShortMessage(clientService.getConfig().getServiceType(),
											TypeOfNumber.valueOf(Integer.valueOf(smsData.getFromTON()).byteValue()),
											NumberingPlanIndicator.valueOf(
													Integer.valueOf(smsData.getFromNP()).byteValue()),
											smsData.getFromAD(),
											TypeOfNumber.valueOf(Integer.valueOf(smsData.getToAN()).byteValue()),
											NumberingPlanIndicator.valueOf(Integer.valueOf(smsData.getToNP()).byteValue()),
											String.valueOf(smsData.getToAD()), new ESMClass(smsData.getEsmClass()),
											Integer.valueOf(smsData.getPid()).byteValue(),
											Integer.valueOf(smsData.getPriority()).byteValue(), null,
											clientService.getAbsoluteExpiredTime(smsData.getValidity())
											// ,clientController.getExpiredIn(smsData.getSmsLifeTimeInMinutes())
											, new RegisteredDelivery(Integer.parseInt(smsData.getDlrResponseType())),
											smsData.getReplaceIfPresent(), DataCodings.newInstance((byte) smsData.getDcs()),
											(byte) 0// refNum//(smsData.getTransactionId() % 256)
											// , smsData.getMessage().substring(segmetnLength * (i), segmentMax).getBytes()
											, new byte[0], OptionalParameters.deserialize(OptionalParameter.Tag.MESSAGE_PAYLOAD.code(),
													smsData.getMessage().getBytes(cs)));
							break;
					}
				} else {
					remoteMessageId = clientService.getSession().submitShortMessage(
							clientService.getConfig().getServiceType(),
							TypeOfNumber.valueOf(Integer.valueOf(smsData.getFromTON()).byteValue()),
							NumberingPlanIndicator.valueOf(Integer.valueOf(smsData.getFromNP()).byteValue()),
							smsData.getFromAD(), TypeOfNumber.valueOf(Integer.valueOf(smsData.getToAN()).byteValue()),
							NumberingPlanIndicator.valueOf(Integer.valueOf(smsData.getToNP()).byteValue()),
							String.valueOf(smsData.getToAD()), new ESMClass(smsData.getEsmClass()),
							Integer.valueOf(smsData.getPid()).byteValue(),
							Integer.valueOf(smsData.getPriority()).byteValue(), null,
							clientService.getAbsoluteExpiredTime(smsData.getValidity())
							// ,clientController.getExpiredIn(smsData.getSmsLifeTimeInMinutes())
							, new RegisteredDelivery(Integer.parseInt(smsData.getDlrResponseType())),
							smsData.getReplaceIfPresent(), DataCodings.newInstance((byte) smsData.getDcs()), (byte) 0,
							smsData.getMessage().getBytes(cs.name()));
				}
			}
			if (remoteMessageId != null) {

				switch (clientService.getConfig().getRemoteIdType()) {
					case "HEX":
						remoteId = new BigInteger(remoteMessageId, 16).longValueExact();
						break;
					case "LONG":
						remoteId = Long.parseLong(remoteMessageId);
						break;
					default:
						errorMessage = buffer.delete(0, buffer.length()).append("Unsupported RemoteIdType - ").append(clientService.getConfig().getRemoteIdType()).toString();
						logger.error(errorMessage);
						break;
				}
			}

		} catch (IllegalArgumentException e) {
			remoteId = -2L;
			logger.warn(buffer.delete(0, buffer.length()).append(e.getMessage()).append(Arrays.toString(e.getStackTrace())));
			errorMessage = buffer.delete(0, buffer.length()).append("IllegalArgumentException. ErrorMessage: ").append(e.getMessage()).toString();
		} catch (PDUException e) {
			remoteId = -3L;
			logger.warn(buffer.delete(0, buffer.length()).append(e.getMessage()).append(Arrays.toString(e.getStackTrace())));
			errorMessage = buffer.delete(0, buffer.length()).append("PDUException. ErrorMessage: ").append(e.getMessage()).toString();
		} catch (ResponseTimeoutException e) {
			remoteId = -4L;
			logger.warn(buffer.delete(0, buffer.length()).append(e.getMessage()).append(Arrays.toString(e.getStackTrace())));
			errorMessage = buffer.delete(0, buffer.length()).append("ResponseTimeoutException. ErrorMessage: ").append(e.getMessage()).toString();
		} catch (InvalidResponseException e) {
			remoteId = -5L;
			logger.warn(buffer.delete(0, buffer.length()).append(e.getMessage()).append(Arrays.toString(e.getStackTrace())));
			errorMessage = buffer.delete(0, buffer.length()).append("InvalidResponseException. ErrorMessage: ").append(e.getMessage()).toString();
		} catch (NegativeResponseException e) {
			remoteId = -6L;
			logger.warn(buffer.delete(0, buffer.length()).append(e.getMessage()).append(Arrays.toString(e.getStackTrace())));
			errorMessage = buffer.delete(0, buffer.length()).append("NegativeResponseException. ErrorMessage: ").append(e.getMessage()).toString();
		} catch (IOException e) {
			remoteId = -7L;
			logger.warn(buffer.delete(0, buffer.length()).append(e.getMessage()).append(Arrays.toString(e.getStackTrace())));
			errorMessage = buffer.delete(0, buffer.length()).append("IOException. ErrorMessage: ").append(e.getMessage()).toString();
		} catch (Exception e) {
			logger.error(buffer.delete(0, buffer.length()).append(e.getMessage()).append(Arrays.toString(e.getStackTrace())));
			errorMessage = buffer.delete(0, buffer.length()).append("Exception. ErrorMessage: ").append(e.getMessage()).toString();
		}
		tps.incrementAndGet();
	}
}
