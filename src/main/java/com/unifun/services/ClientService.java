package com.unifun.services;


import com.unifun.model.SMPPClientConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ClientService {
	private static final Logger logger = LogManager.getLogger(ClientService.class);
	private static ScheduledExecutorService bulkSendingService = Executors.newScheduledThreadPool(1);
	private SMPPClientConfig config = new SMPPClientConfig();//todo, add param
	private static ClientService instance;
	private static SMPPSession session = new SMPPSession();


	private static final String smppIp = "127.0.0.1";

	private static int port = 33308;

	private static final String username = "sns1";

	private static final String password = "sns1";


	public static synchronized ClientService getInstance() {
		if (instance == null) {
			instance = new ClientService();
		}
		return instance;
	}

	public ClientService() {
		initSession();
	}

	private static SMPPSession initSession() {
			logger.info("Process bind:");
		try {
			session.setMessageReceiverListener(new MessageReceiverListenerImpl());
			String systemId = session.connectAndBind(
					smppIp,
					port,
					new BindParameter(
							BindType.BIND_TRX,
							username,
							password,
							"sns",
							TypeOfNumber.UNKNOWN,
							NumberingPlanIndicator.UNKNOWN,
							null));
			logger.info("Connected with SMPP with system id {} " + systemId);
		} catch (IOException e) {
			logger.error("NO CONNECTION TO SERVER");
		}
		return session;
	}


	public SMPPSession getSession() {
		return session;
	}

	public Charset getCharset(int dcs) {
		if (dcs > -1 && dcs < 4 || dcs > 15 && dcs < 20 || dcs > 31 && dcs < 36 || dcs > 47 && dcs < 52
				|| dcs > 63 && dcs < 68 || dcs > 79 && dcs < 84 // ) {
				// Compressed
				|| dcs > 95 && dcs < 100 || dcs > 111 && dcs < 116 || dcs > 207 && dcs < 224 || dcs > 239 && dcs < 244
				|| dcs > 111 && dcs < 252) {
			if (dcs == 3) {
				return StandardCharsets.ISO_8859_1;
			}
			return StandardCharsets.US_ASCII;
		}
		if (dcs > 7 && dcs < 12 || dcs > 23 && dcs < 28 || dcs > 39 && dcs < 44 || dcs > 55 && dcs < 60
				|| dcs > 71 && dcs < 76 || dcs > 87 && dcs < 92 // ) {
				// Compressed
				|| dcs > 103 && dcs < 108 || dcs > 119 && dcs < 124 || dcs > 224 && dcs < 240) {
			return StandardCharsets.UTF_16BE;
		}
		return StandardCharsets.UTF_8;
	}

	public SMPPClientConfig getConfig() {
		return config;
	}

	public void setConfig(SMPPClientConfig config) {
		this.config = config;
	}

	public String getAbsoluteExpiredTime(Long timel) throws ProcessRequestException {
		if (timel == null) {
			return null;
		}
		try {
			Date date = new Date(timel);
			SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
			return String.format("%s000+", format.format(date));
		} catch (Exception e) {
			return null;
		}
	}


	static Runnable reconnectProcess = () -> {
		logger.info("reconnectProcess START");
		if (!session.getSessionState().isBound()) {
			logger.info("TRUE!!!");
			initSession();
		}else {
			logger.info("FALSE!!!");
		}
	};
	public void start() {
		logger.info("SHED START");
		bulkSendingService.scheduleAtFixedRate(reconnectProcess, 0, 2, TimeUnit.SECONDS);
			}

}