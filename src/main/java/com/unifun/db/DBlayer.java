package com.unifun.db;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
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

	public DBlayer() {
		mapBuffer = new ConcurrentHashMap<String, StringBuffer>();
		mapBuffer.put("updateBulkRequestStatusWhenThenPart", new StringBuffer());
		mapBuffer.put("updateBulkRequestStatusWherePart", new StringBuffer());
		StringBuffer wherePart = mapBuffer.get("updateBulkRequestStatusWherePart");
		wherePart.append("WHERE id IN(");

		//
		mapCounter = new ConcurrentHashMap<String, AtomicInteger>();
		mapCounter.put("updateBulkRequestStatus", new AtomicInteger());

		threadPool = Executors.newFixedThreadPool(20);
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
				synchronized(rowSetFactory) {
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
		if(instance == null) {
			instance = new DBlayer();
		}
		return instance;
	}

	public void executeUpdateBach(){
		StringBuffer whenThenPart = mapBuffer.get("updateBulkRequestStatusWhenThenPart");
		StringBuffer wherePart = mapBuffer.get("updateBulkRequestStatusWherePart");
		final String substring = wherePart.toString().replaceFirst(",","");
		final String endWhenThenPart = " END)";
		final String sqlQuery = "UPDATE bulk_sending_requests SET transaction_id = (CASE id " + whenThenPart + endWhenThenPart + substring + ");";
		if(whenThenPart.length() > 0) {
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

}
