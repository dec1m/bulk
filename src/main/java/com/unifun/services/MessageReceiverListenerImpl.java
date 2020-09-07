package com.unifun.services;

import com.unifun.db.DBlayer;
import com.unifun.model.DeliveryStatus;
import org.jsmpp.bean.*;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;

public class MessageReceiverListenerImpl implements MessageReceiverListener {
	private DBlayer dBlayer = DBlayer.getInstance();

	@Override
	public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
		long remoteId;
		try {
			DeliveryReceipt deliveryReceipt = DefaultDeliveryReceiptStripper.getInstance().strip(deliverSm);
			remoteId = Long.parseLong(deliveryReceipt.getId());
			dBlayer.addToUpdateBatch(new DeliveryStatus(remoteId, deliveryReceipt.getFinalStatus().name()));
		} catch (InvalidDeliveryReceiptException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onAcceptAlertNotification(AlertNotification alertNotification) {

	}

	@Override
	public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
		return null;
	}
}
