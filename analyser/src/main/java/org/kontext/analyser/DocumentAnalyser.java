package org.kontext.analyser;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.analyser_partition_threshold;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.kontext.analyser.context.Association;
import org.kontext.analyser.context.Context;
import org.kontext.analyser.context.Context.ContextBuilder;
import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.cassandra.documents.DocumentRepositoryImpl;
import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.data.Content;
import org.kontext.data.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class DocumentAnalyser implements ContextAnalyser {

	private static final Logger LOG = LoggerFactory.getLogger(DocumentAnalyser.class);
	private static final PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();

	private static DocumentRepository docsRepo;
	private static DataSourceManager datasourceMgr;

	private final UUID docId;
	private final List<CoreMap> sentences;

	private Context docContext;

	static {
		datasourceMgr = new CassandraManager(propsRepo);
		docsRepo = new DocumentRepositoryImpl(propsRepo, datasourceMgr);
	}

	public DocumentAnalyser(UUID docId, List<CoreMap> sentences) {
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
	public void analyse() {
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
	}

	private void analyseSentence(CoreMap sentence) {
		List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
		for (CoreLabel token : tokens) {
			String word = token.get(TextAnnotation.class);
			String pos = token.get(PartOfSpeechAnnotation.class);
			String ne = token.get(NamedEntityTagAnnotation.class);
			
			if (LOG.isDebugEnabled())
				LOG.debug("Word : " + word + "; POS : " + pos + "; Named entity tag : " + ne);
		}
	}

}
