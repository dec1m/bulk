package com.unifun.db;

import com.unifun.model.DeliveryStatus;
import com.unifun.utils.PropertyReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DBlayer {
	private ConcurrentHashMap<String, StringBuffer> mapBuffer;
	private ConcurrentHashMap<String, AtomicInteger> mapCounter;
	private static final Logger logger = LogManager.getLogger(DBlayer.class);
	private HikariCPDataSource ds = new HikariCPDataSource();
	private RowSetFactory rowSetFactory;
	private static DBlayer instance;
	private ExecutorService threadPool;
	private PropertyReader reader = new PropertyReader();
	//List<String> deliveryUpdates = new ArrayList<>();
	List<DeliveryStatus> deliveryStatuses = new ArrayList<>();
	private int batchSize;
	private int dbThreadPool;
	private static String sqlUpdateDelivery = "UPDATE bulk_deliveries SET status = \"setStatus\" WHERE remoteId  = setRemote;";

	public DBlayer() {
		mapBuffer = new ConcurrentHashMap<>();
		mapBuffer.put("updateBulkRequestStatusWhenThenPart", new StringBuffer());
		mapBuffer.put("updateBulkRequestStatusWherePart", new StringBuffer());
		StringBuffer wherePart = mapBuffer.get("updateBulkRequestStatusWherePart");
		wherePart.append("WHERE id IN(");

		//counters
		mapCounter = new ConcurrentHashMap<>();
		mapCounter.put("updateBulkRequestStatus", new AtomicInteger());
		mapCounter.put("batchDeliveryUpdateCounter", new AtomicInteger());


		final Properties properties = reader.readParamFromFile();
		batchSize = Integer.parseInt(properties.getProperty("batchSize"));
		dbThreadPool = Integer.parseInt(properties.getProperty("dbThreadPool"));

		threadPool = Executors.newFixedThreadPool(dbThreadPool);
		try {
			rowSetFactory = RowSetProvider.newFactory();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}

	public CachedRowSet getBulkRequests(Integer limit) {
		CachedRowSet cachedRowSet = null;
		String sqlQuery = "SELECT r.id, r.compaign_id, r.msisdn, r.message FROM bulk_sending_requests r " +
				" USE INDEX (compaign_id_trans_id) JOIN bulk_sending_campaign c ON r.compaign_id = c.id WHERE c.is_active = 1 AND transaction_id = 0 " +
				" LIMIT ?";

		try (Connection connection = ds.getConnection();
		     PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
			preparedStatement.setInt(1, limit);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				synchronized (rowSetFactory) {
					cachedRowSet = rowSetFactory.createCachedRowSet();
				}
				cachedRowSet.populate(resultSet);
			}
		} catch (Exception e) {
			logger.error("Selecting bulk requests failed", e);
		}
		return cachedRowSet;
	}

	public static synchronized DBlayer getInstance() {
		if (instance == null) {
			instance = new DBlayer();
		}
		return instance;
	}

	public void executeUpdateBach() {
		StringBuffer whenThenPart = mapBuffer.get("updateBulkRequestStatusWhenThenPart");
		StringBuffer wherePart = mapBuffer.get("updateBulkRequestStatusWherePart");
		final String substring = wherePart.toString().replaceFirst(",", "");
		final String endWhenThenPart = " END)";
		final String sqlQuery = "UPDATE bulk_sending_requests SET transaction_id = (CASE id " + whenThenPart + endWhenThenPart + substring + ");";
		if (whenThenPart.length() > 0) {
			try (Connection connection = ds.getConnection();
			     Statement statement = connection.createStatement()) {
				statement.execute(sqlQuery);
				whenThenPart.delete(0, whenThenPart.length());
				wherePart.delete(0, wherePart.length());
				wherePart.append(" WHERE id IN(");
			} catch (Exception e) {
				logger.error("executeUpdateBach failed", e);
			}
		}

	}

	public void addToUpdateBatch(Integer transactionId, Integer requestId) {
		StringBuffer whenThenPart = mapBuffer.get("updateBulkRequestStatusWhenThenPart");
		StringBuffer wherePart = mapBuffer.get("updateBulkRequestStatusWherePart");
		whenThenPart.append(" WHEN ")
				.append(requestId)
				.append(" THEN ")
				.append(transactionId);
		wherePart.append(",");
		wherePart.append(requestId);

	}

	public void updateCampaignCounters(Integer campaignId, Integer processCounter) {
		threadPool.execute(() -> {
			String sqlQuery = "UPDATE bulk_sending_campaign SET process_count = process_count + ? WHERE id = ?";

			try (Connection connection = ds.getConnection();
			     PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
				preparedStatement.setInt(1, processCounter);
				preparedStatement.setInt(2, campaignId);
				preparedStatement.executeUpdate();
			} catch (Exception e) {
				logger.error("Updating campaing counters failed", e);
			}
		});
	}

	public void saveDeliveryStatus(DeliveryStatus deliveryStatus) {
		threadPool.execute(() -> {
			String sqlQuery = "INSERT INTO bulk_deliveries(transactionId,remoteId) VALUES (?,?)";

			try (Connection connection = ds.getConnection();
			     PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
				preparedStatement.setInt(1, deliveryStatus.getTransactionId());
				preparedStatement.setLong(2, deliveryStatus.getRemoteId());
				preparedStatement.executeUpdate();
			} catch (Exception e) {
				logger.error("saveDeliveryStatus failed", e);
			}
		});

	}

	public void updateDeliveryStatus(long remoteId, String status) {
		threadPool.execute(() -> {
			String sqlQuery = "UPDATE bulk_deliveries SET status = ? WHERE remoteId  = ?;";

			try (Connection connection = ds.getConnection();
			     PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
				preparedStatement.setString(1, status);
				preparedStatement.setLong(2, remoteId);
				preparedStatement.executeUpdate();
			} catch (Exception e) {
				logger.error("updateDeliveryStatus failed", e);
			}
		});

	}

//	private void batchUpdateDeliveryStatusExecute() {
//		List<String> tmp = deliveryUpdates;
//		deliveryUpdates = new ArrayList<>();
//		threadPool.execute(() -> {
//		logger.info(">> tmp " + tmp.size());
//		logger.info(">> deliveryUpdates " + deliveryUpdates.size());
//		try {
//			Connection connection = ds.getConnection();
//			final Statement statement = connection.createStatement();
//			connection.setAutoCommit(false);
//
//			for (String deliveryUpdate : tmp) {
//				statement.addBatch(deliveryUpdate);
//				logger.info("SQL >> " + deliveryUpdate );
//			}
//
//			statement.executeBatch();
//			connection.commit();
//		} catch (Exception e) {
//			logger.error("updateDeliveryStatus failed", e);
//		}
//
//		});
//	}
//
//	public void addUpdateBatch(long remoteId, String status) {
//		AtomicInteger counter = mapCounter.get("batchDeliveryUpdateCounter");
//		final String replace = sqlUpdateDelivery.replace("setStatus", status).replace("setRemote", remoteId + "");
//		deliveryUpdates.add(replace);
//		counter.incrementAndGet();
//		logger.info("COUNTER " + counter.get());
//		logger.info("batchSize " + batchSize);
//		if (counter.get() < batchSize) {
//			logger.info("RETURN COUNTER " + counter.get());
//			return;
//		}
//		counter.set(0);
//		batchUpdateDeliveryStatusExecute();
//
//	}


	public void addToUpdateBatch(DeliveryStatus deliveryStatus) {
		deliveryStatuses.add(deliveryStatus);
		if (deliveryStatuses.size() < batchSize) {
			return;
		}
		batchUpdateDeliveryStatusExecute();

	}

	private synchronized void batchUpdateDeliveryStatusExecute() {
		String sql = "UPDATE bulk_deliveries SET status = ? WHERE remoteId  = ?;";
		List<DeliveryStatus> tmp = deliveryStatuses;
		deliveryStatuses = new ArrayList<>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			connection.setAutoCommit(false);

			for (DeliveryStatus deliveryUpdate : tmp) {
				if (deliveryUpdate != null) {
					preparedStatement.setString(1, deliveryUpdate.getState());
					preparedStatement.setLong(2, deliveryUpdate.getRemoteId());

					preparedStatement.addBatch();
				}
			}

			preparedStatement.executeBatch();
			connection.commit();

		} catch (Exception e) {
			logger.error("updateDeliveryStatus failed", e);
		} finally {
			try {
				preparedStatement.close();
				connection.close();
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}

		}


	}

	public int getMaxTransactionID() {
		int maxTransactionID = 1;
		String sqlQuery = "SELECT max_transaction_id FROM transactionidstorage LIMIT 1";
		try (Connection connection = ds.getConnection();
		     Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(sqlQuery);

			if (resultSet.next()) {
				maxTransactionID = resultSet.getInt("max_transaction_id");
			}
			resultSet.close();
		} catch (Exception e) {
			logger.error("Selecting max transaction id failed", e);
		}
		return maxTransactionID;
	}

}
