package com.thedeanda.ajaxproxy.service;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

/**
 * TODO: move this to "model" package
 * @author mdeanda
 *
 */
@DatabaseTable(tableName = "resource")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoredResource {
	@Setter
	@DatabaseField(id = true, width = 64)
	private String id;

	@Setter
	@DatabaseField(width = 1024 * 4)
	private String url;

	@Setter
	@DatabaseField(width = 32)
	private String method;

	@Setter
	@DatabaseField(width = 1024 * 4)
	private String headers;

	@Setter
	@DatabaseField(dataType = DataType.SERIALIZABLE, width = 1024 * 1024)
	// 1MB max
	private byte[] input;

	@Setter
	@DatabaseField()
	private int status;

	@Setter
	@DatabaseField(width = 1024 * 2)
	private String reason;

	@Setter
	@DatabaseField(index = true)
	private long startTime;

	@Setter
	@DatabaseField()
	private long duration;

	@Setter
	@DatabaseField(width = 1024 * 4)
	private String responseHeaders;

	@Setter
	@DatabaseField(dataType = DataType.SERIALIZABLE, width = 1024 * 1024 * 4)
	// 4MB max
	private byte[] output;

	@Setter
	@DatabaseField(width = 1024 * 2)
	private String errorMessage;

	@Setter
	@DatabaseField(width = 64)
	private String contentEncoding;

	@Setter
	private byte[] outputDecompressed;

	@Builder.Default
	private boolean notSaved = true;
}
