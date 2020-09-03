package com.unifun.services;

import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;

public class MessageReceiverListenerImpl implements MessageReceiverListener {
	@Override
	public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {

	}

	@Override
	public void onAcceptAlertNotification(AlertNotification alertNotification) {

	}

	@Override
	public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
		return null;
	}
}
