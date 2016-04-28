package org.kontext.common.repositories;

public class PropertiesRepositoryConstants {

	// Cassandra properties
	public static final String cassandra_url = "cassandra_url";
	public static final String cassandra_port = "cassandra_port";
	public static final String cassandra_keyspace = "cassandra_keyspace";
	public static final String cassandra_document_table = "cassandra_document_table";

	// Crawler properties
	public static final String crawler_storage_folder = "crawler_storage_folder";
	public static final String crawler_request_delay = "crawler_request_delay";
	public static final String crawler_thread_count = "crawler_thread_count";
	
	// Properties repository
	public static final String properties_version = "properties_version";
	public static final String properties_module = "properties";
	public static final String properties_config_file = "config/config.properties";
	public static final String properties_editable = "_editable";
	public static final String properties_table = "properties_table";
	
	// Parser properties
	public static final String parser_threshold = "parser_threshold";
	
	// Analyser properties
	public static final String analyser_partition_threshold = "analyser_partition_threshold";
	public static final String analyser_document_threshold = "analyser_document_threshold";
	
	// Document attributes
	public static final String id = "id";
	public static final String raw_text = "raw_text"; 
	public static final String create_date = "create_date";
	public static final String parsed_out = "parsed_out";
	
}
