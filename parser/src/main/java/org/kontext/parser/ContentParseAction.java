package org.kontext.parser;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.cassandra_document_table;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.cassandra_keyspace;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.create_date;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.id;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.parsed_out;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.parser_threshold;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.raw_text;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.SerializationUtils;
import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class ContentParseAction extends RecursiveAction {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ContentParseAction.class);

	private static PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();
	private List<Row> documents;
	private int length;
	
	private static StanfordCoreNLP pipeline;
	private static Session session;
	
	private static String keySpace;
	private static String documentsTable;
	
	private final String parseString;
	
	private static int threadCount = 0;
	
	static {
		keySpace = propsRepo.read(cassandra_keyspace);
		documentsTable = propsRepo.read(cassandra_document_table);
		pipeline = new StanfordCoreNLP(propsRepo.getAllProperties());
		session = (Session) new CassandraManager(propsRepo).getConnection();
	}

	public ContentParseAction(List<Row> documents) {
		this.documents = documents;
		this.length = documents.size();
		this.parseString = null;
	}

	/*
	 * To support parsing a string directly rather than via a document.
	 */
	public ContentParseAction(String parseString) {
		this.parseString = parseString;
	}

	@Override
	protected void compute() {
		incrementThreadCount();
		
		if (LOG.isInfoEnabled())
			LOG.info("Number of documents : " + length + " | Thread count = " + threadCount);

		if (parseString != null) {
			parse(null, null, parseString);
			return;
		}

		if (length <= Integer.parseInt(propsRepo.read(parser_threshold))) {
			parse();
			return;
		}

		int split = length / 2;

		List<Row> firstSplit = documents.subList(0, split);
		List<Row> secondSplit = documents.subList(split, length);
		invokeAll(new ContentParseAction(firstSplit), new ContentParseAction(secondSplit));
	}

	private static synchronized void incrementThreadCount() {
		threadCount ++;
	}

	private void parse() {
		String _rawText = null;
		UUID _id = null;
		Date _createDate = null;
		
		for (Row document : documents) {
			_rawText = document.getString(raw_text);
			_id = document.getUUID(id);
			_createDate = document.getTimestamp(create_date);
			parse(_id, _createDate, _rawText);
		}
	}

	private void parse(UUID id, Date createDate, String _rawText) {
		Annotation docAnnotation = null;
		docAnnotation = new Annotation(_rawText);
		pipeline.annotate(docAnnotation);

		List<CoreMap> sentences = docAnnotation.get(SentencesAnnotation.class);
		if (id != null)
			persistParseOut(id, createDate, sentences);
		
		if (LOG.isDebugEnabled())
			parseSentences(sentences);
	}

	private void persistParseOut(UUID _id, Date createDate, List<CoreMap> sentences) {
		byte[] parsedOut = SerializationUtils.serialize((Serializable) sentences);
		
		Statement statement = QueryBuilder.update(keySpace, documentsTable)
											.with(QueryBuilder.set(parsed_out, ByteBuffer.wrap(parsedOut)))
											.where(QueryBuilder.eq(create_date, createDate))
											.and(QueryBuilder.eq(id, _id));
		
		session.execute(statement);
	}

	private void parseSentences(List<CoreMap> sentences) {
		for (CoreMap sentence : sentences) {
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			parseSentence(tokens);

			if (LOG.isDebugEnabled()) {
				Tree tree = sentence.get(TreeAnnotation.class);
				tree.printLocalTree();
	
				SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
				dependencies.prettyPrint();
			}
		}
	}

	private void parseSentence(List<CoreLabel> tokens) {
		for (CoreLabel token : tokens) {
			String word = token.get(TextAnnotation.class);
			String pos = token.get(PartOfSpeechAnnotation.class);
			String ne = token.get(NamedEntityTagAnnotation.class);

			if (LOG.isDebugEnabled())
				LOG.debug("Word : " + word + "; POS : " + pos + "; Named entity tag : " + ne);
		}
	}

}
