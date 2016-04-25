package org.kontext.analyser.context;

import java.util.Set;
import java.util.UUID;

import org.kontext.data.Content;

public class Context {
	private final String name;
	private final UUID id;

	private final Set<String> nouns;
	private final Set<String> synonyms;

	private final Set<Association> associations;
	private final Set<Content> contents;

	public Context(ContextBuilder cBuilder) {
		this.name = cBuilder.name;
		this.id = cBuilder.id;
		this.nouns = cBuilder.nouns;
		this.synonyms = cBuilder.synonyms;
		this.associations = cBuilder.associations;
		this.contents = cBuilder.contents;
	}

	public String getName() {
		return name;
	}

	public UUID getId() {
		return id;
	}

	public Set<String> getNouns() {
		return nouns;
	}

	public Set<String> getSynonyms() {
		return synonyms;
	}

	public Set<Association> getAssociations() {
		return associations;
	}

	public Set<Content> getContents() {
		return contents;
	}

	public class ContextBuilder {
		private ContextBuilder cBuilder = new ContextBuilder();

		private String name;
		private UUID id;
		private Set<String> nouns;
		private Set<String> synonyms;
		private Set<Association> associations;
		private Set<Content> contents;

		public ContextBuilder getBuilder() {
			return cBuilder;
		}

		public ContextBuilder name(String name) {
			this.name = name;
			return this;
		}

		public ContextBuilder id(UUID id) {
			this.id = id;
			return this;
		}

		public ContextBuilder nouns(Set<String> nouns) {
			this.nouns = nouns;
			return this;
		}

		public ContextBuilder synonyms(Set<String> synonyms) {
			this.synonyms = synonyms;
			return this;
		}

		public ContextBuilder associations(Set<Association> associations) {
			this.associations = associations;
			return this;
		}

		public ContextBuilder contents(Set<Content> contents) {
			this.contents = contents;
			return this;
		}
	}

}
