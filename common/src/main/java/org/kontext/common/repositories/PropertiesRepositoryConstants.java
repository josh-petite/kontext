package org.kontext.common.repositories;

public class PropertiesRepositoryConstants {

	// Cassandra properties
	public static final String cassandra_url = "cassandra_url";
	public static final String cassandra_port = "cassandra_port";
	public static final String cassandra_keyspace = "cassandra_keyspace";
	public static final String cassandra_document_table = "cassandra_document_table";
	public static final String cassandra_context_table = "cassandra_context_table";

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

	// Dictionary properties
	public static final String dictionary_thesaurus = "dictionary_thesaurus";
	public static final String dictionary_uri = "dictionary_uri";
	public static final String dictionary_path = "dictionary_path";
	public static final String dictionary_key = "key";

	// Wiki properties
	public static final String wiki_uri = "wiki_uri";
	public static final String wiki_action = "wiki_action";

	public static final String wiki_search_action = "wiki_search_action";
	public static final String wiki_search_param_search = "wiki_search_param_search";

	public static final String wiki_query_action = "wiki_query_action";
	public static final String wiki_query_prop = "wiki_query_prop";
	public static final String wiki_query_prop_value = "wiki_query_prop_value";
	
	public static final String wiki_query_rvprop = "wiki_query_rvprop";
	public static final String wiki_query_rvprop_value = "wiki_query_rvprop_value";
	
	public static final String wiki_query_rvlimit = "wiki_query_rvlimit";
	public static final String wiki_query_rvlimit_value = "wiki_query_rvlimit_value";
	
	public static final String wiki_query_format = "wiki_query_format";
	public static final String wiki_query_format_value = "wiki_query_format_value";
	
	public static final String wiki_query_rvparse = "wiki_query_rvparse";
	public static final String wiki_query_rvparse_value = "wiki_query_rvparse_value";
	
	public static final String wiki_query_rvexpandtemplates = "wiki_query_rvexpandtemplates";
	public static final String wiki_query_rvexpandtemplates_value = "wiki_query_rvexpandtemplates_value";
	
	public static final String wiki_query_titles = "wiki_query_titles";
	
}
