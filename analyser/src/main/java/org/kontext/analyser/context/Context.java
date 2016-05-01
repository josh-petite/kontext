package org.kontext.analyser.context;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import org.kontext.data.Content;

public class Context {
	private final String name;
	private final UUID id;

	private final Set<String> nouns;
	private final Set<Serializable> synonyms;

	private final Set<Association> associations;
	private final Set<Context> contexts;
	
	private final Set<Content> contents;

	public Context(ContextBuilder cBuilder) {
		this.name = cBuilder.name;
		this.id = cBuilder.id;
		this.nouns = cBuilder.nouns;
		this.synonyms = cBuilder.synonyms;
		this.associations = cBuilder.associations;
		this.contents = cBuilder.contents;
		this.contexts = cBuilder.contexts;
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

	public Set<Serializable> getSynonyms() {
		return synonyms;
	}

	public Set<Association> getAssociations() {
		return associations;
	}

	public Set<Content> getContents() {
		return contents;
	}
	
	public Set<Context> getContexts() {
		return contexts;
	}

	public static class ContextBuilder {

		private String name;
		private UUID id;
		private Set<String> nouns;
		private Set<Serializable> synonyms;
		private Set<Association> associations;
		private Set<Content> contents;
		private Set<Context> contexts;

		public  ContextBuilder() {}
		
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

		public ContextBuilder synonyms(Set<Serializable> synonyms) {
			this.synonyms = synonyms;
			return this;
		}

		public ContextBuilder associations(Set<Association> associations) {
			this.associations = associations;
			return this;
		}

		public ContextBuilder contexts(Set<Context> contexts) {
			this.contexts = contexts;
			return this;
		}
		
		public ContextBuilder contents(Set<Content> contents) {
			this.contents = contents;
			return this;
		}

		public Context build() {
			return new Context(this);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder context = new StringBuilder();
		context.append("Nouns: " + nouns.toString());
		context.append("; Synonyms: " + synonyms.toString());
		context.append("; Associations: " + associations.toString());
		context.append("; Contents: " + contents.toString());
		return context.toString();
	}

}
