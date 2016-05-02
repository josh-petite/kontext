package org.kontext.analyser;

import static  org.kontext.analyser.ContextAnalyserConstants.NAMED_ENTITY_TAG_O;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.cassandra_context_table;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.cassandra_keyspace;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.kontext.analyser.context.Context;
import org.kontext.analyser.dictionary.Dictionary;
import org.kontext.analyser.dictionary.DictionaryImpl;
import org.kontext.analyser.dictionary.exception.DictionaryException;
import org.kontext.analyser.exception.DocumentAnalyserException;
import org.kontext.cassandra.documents.DocumentRepositoryImpl;
import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.data.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidTypeException;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class DocumentAnalyserImpl implements DocumentAnalyser {

	private static final Logger LOG = LoggerFactory.getLogger(DocumentAnalyserImpl.class);
	private static final PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();

	private static DataSourceManager datasourceMgr;

	private final UUID docId;
	private final List<CoreMap> sentences;

	private static Session session;
	private Context docContext;
	
	private static String todaysDate;

	static {
		datasourceMgr = new CassandraManager(propsRepo);
		session = (Session) datasourceMgr.getConnection();
		todaysDate = DocumentRepositoryImpl.getTodaysDate();
		init();
	}

	public DocumentAnalyserImpl(UUID docId, List<CoreMap> sentences) {
		this.docId = docId;
		this.sentences = sentences;

		docContext = new Context.ContextBuilder()
							.id(this.docId)
							.associations(new HashSet<>())
							.nouns(new HashSet<>())
							.synonyms(new HashSet<>())
							.contents(new HashSet<>())
							.contexts(new HashSet<>())
							.build();
	}

	@Override
	public void analyse() throws DocumentAnalyserException {
		if (LOG.isDebugEnabled())
			LOG.debug("Id : " + docId + " | Length of the document : " + sentences.size());

		for (CoreMap sentence : sentences) {
			if (LOG.isDebugEnabled())
				LOG.debug(sentence.toString());

			analyseSentence(sentence);

			Tree tree = sentence.get(TreeAnnotation.class);
			tree.printLocalTree();

			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			dependencies.prettyPrint();
		}
		
		if(LOG.isDebugEnabled())
			LOG.debug(docContext.toString());
	}

	private void analyseSentence(CoreMap sentence) throws DocumentAnalyserException {
		List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
		Dictionary dictionary = DictionaryImpl.getInstance();
		
		for (CoreLabel token : tokens) {
			String word = token.get(TextAnnotation.class);
			String pos = token.get(PartOfSpeechAnnotation.class);
			String ne = token.get(NamedEntityTagAnnotation.class);
			
			if (LOG.isDebugEnabled())
				LOG.debug("Word : " + word + "; POS : " + pos + "; Named entity tag : " + ne);
			
			if (Noun.isNoun(pos) && NAMED_ENTITY_TAG_O.equals(ne)) {
				docContext.getNouns().add(word);
				docContext.getSynonyms().addAll(dictionary.getSynonymsForNoun(word));
			}
		}
		
		try {
			persistContext();
		} catch (DictionaryException e) {
			if (LOG.isErrorEnabled())
				LOG.error(e.getMessage());
			throw new DocumentAnalyserException(e);
		}
	}
	
	private static void init() {
		// initializing analyser shall involve the following
		// 1. Creating document context table if not created
		// 2. Primary key - id of the context
		// 3. Can be associated to multiple contents, multiple associations, Nouns and Synonyms etc.
		String cqlMask = "CREATE TABLE IF NOT EXISTS %s.%s "
				+ "(id UUID, "
				+ "nouns set<text>, "
				+ "synonyms set<text>, "
				+ "document2confidence map<uuid, float>, "
				+ "association2confidence map<uuid, float>, create_date timestamp, "
				+ "PRIMARY KEY (create_date, id));";
		String cql = String.format(cqlMask, propsRepo.read(cassandra_keyspace), propsRepo.read(cassandra_context_table));

		PreparedStatement statement = session.prepare(cql);
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement.bind());
	}
	
	private void persistContext() throws DictionaryException {
		String cqlMask = "INSERT INTO %s.%s "
				+ "(id, nouns, synonyms, create_date) VALUES "
				+ "(?, ?, ?, toTimestamp('" + todaysDate + "'));";
		String cql = String.format(cqlMask, propsRepo.read(cassandra_keyspace), propsRepo.read(cassandra_context_table));
		
		PreparedStatement statement = session.prepare(cql);
		BoundStatement boundStatement = new BoundStatement(statement);
		try {
			session.execute(boundStatement.bind(docContext.getId(), docContext.getNouns(), docContext.getSynonyms()));
		} catch (InvalidTypeException ex) {
			LOG.error("Context creation failed for document : " + docId.toString());
			throw new DictionaryException(DictionaryException.dictionary_response_interleaved_with_serializables);
		}
	}
	
	@Override
	public Context getContext() {
		return docContext;
	}

}
