package org.kontext.parser;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Row;

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
	private int start;

	private final String parseString;

	public ContentParseAction(List<Row> documents, int start) {
		this.documents = documents;
		this.length = documents.size();
		this.start = start;
		this.parseString = null;
	}

	public ContentParseAction(String parseString) {
		this.parseString = parseString;
	}

	@Override
	protected void compute() {
		if (LOG.isDebugEnabled())
			LOG.debug("Length : " + length + "; Start = " + start);

		if (parseString != null) {
			parse(parseString);
			return;
		}

		if (length <= Integer.parseInt(propsRepo.read(parser_threshold))) {
			parse();
			return;
		}

		int split = length / 2;

		List<Row> firstSplit = documents.subList(start, start + split);
		List<Row> secondSplit = documents.subList(start + split, start + 2 * split);
		invokeAll(new ContentParseAction(firstSplit, start), new ContentParseAction(secondSplit, start + split));
	}

	private void parse() {
		String _rawText = null;
		
		for (Row document : documents) {
			_rawText = document.getString(raw_text);
			parse(_rawText);
		}
	}

	private void parse(String _rawText) {
		StanfordCoreNLP pipeline = new StanfordCoreNLP(propsRepo.getAllProperties());
		Annotation docAnnotation = null;

		docAnnotation = new Annotation(_rawText);
		pipeline.annotate(docAnnotation);

		List<CoreMap> sentences = docAnnotation.get(SentencesAnnotation.class);
		parseSentences(sentences);
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
