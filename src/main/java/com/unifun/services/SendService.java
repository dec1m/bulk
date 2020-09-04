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

public class SendService implements Runnable {
	private static final Logger logger = LogManager.getLogger(SendService.class);
	private ClientService clientService = ClientService.getInstance();
	private SmsData smsData;


	public SendService(SmsData smsData) {
		this.smsData = smsData;

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
			int segments = smsData.getQuantity();
			if (clientService.getSession().getSessionState().isBound()) {
				if (segments > 1) {
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
									, new RegisteredDelivery(Integer.parseInt(smsData.getDlrResponseType())),
									smsData.getReplaceIfPresent(), DataCodings.newInstance((byte) smsData.getDcs()),
									(byte) 0,
									new byte[0], OptionalParameters.deserialize(OptionalParameter.Tag.MESSAGE_PAYLOAD.code(),
											smsData.getMessage().getBytes(cs)));
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
	}
}
