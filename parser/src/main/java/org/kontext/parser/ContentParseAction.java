package org.kontext.parser;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

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

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

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
	
	private static final StanfordCoreNLP pipeline = new StanfordCoreNLP(propsRepo.getAllProperties());;
	private static Session session;
	
	private final String parseString;

	public ContentParseAction(List<Row> documents) {
		this.documents = documents;
		this.length = documents.size();
		
		this.parseString = null;
		session = (Session) new CassandraManager(propsRepo).getConnection();
	}

	/*
	 * To support parsing a string directly rather than via a document.
	 */
	public ContentParseAction(String parseString) {
		this.parseString = parseString;
	}

	@Override
	protected void compute() {
		if (LOG.isDebugEnabled())
			LOG.debug("Number of documents : " + length);

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
		
		parseSentences(sentences);
	}

	private void persistParseOut(UUID id, Date createDate, List<CoreMap> sentences) {
		byte[] parsedOut = SerializationUtils.serialize((Serializable) sentences);
		
		String cqlMask = "UPDATE %s.%s SET %s = ? where create_date = ? and id = ?; ";
		String cql = String.format(cqlMask, propsRepo.read(cassandra_keyspace), propsRepo.read(cassandra_document_table), parsed_out);

		PreparedStatement statement = session.prepare(cql);
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement.bind(ByteBuffer.wrap(parsedOut), createDate, id));
	}

	private void parseSentences(List<CoreMap> sentences) {
		for (CoreMap sentence : sentences) {
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			parseSentence(tokens);

			Tree tree = sentence.get(TreeAnnotation.class);
			tree.printLocalTree();

			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			dependencies.prettyPrint();
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
