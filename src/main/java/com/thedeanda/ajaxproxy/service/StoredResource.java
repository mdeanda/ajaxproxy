package com.thedeanda.ajaxproxy.service;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;

/**
 * TODO: move this to "model" package
 * @author mdeanda
 *
 */
@DatabaseTable(tableName = "resource")
@Data
public class StoredResource {
	@DatabaseField(id = true, width = 64)
	private String id;

	@DatabaseField(width = 1024 * 4)
	private String url;

	@DatabaseField(width = 32)
	private String method;

	@DatabaseField(width = 1024 * 4)
	private String headers;

	@DatabaseField(dataType = DataType.SERIALIZABLE, width = 1024 * 1024)
	// 1MB max
	private byte[] input;

	@DatabaseField()
	private int status;

	@DatabaseField(width = 1024 * 2)
	private String reason;

	@DatabaseField()
	private long startTime;

	@DatabaseField()
	private long duration;

	@DatabaseField(width = 1024 * 4)
	private String responseHeaders;

	@DatabaseField(dataType = DataType.SERIALIZABLE, width = 1024 * 1024 * 4)
	// 4MB max
	private byte[] output;

	@DatabaseField(width = 1024 * 2)
	private String errorMessage;

	@DatabaseField(width = 64)
	private String contentEncoding;
	
	private byte[] outputDecompressed;

}
