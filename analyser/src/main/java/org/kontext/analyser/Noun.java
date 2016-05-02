package org.kontext.analyser;

import java.util.HashSet;
import java.util.Set;

public enum Noun {
	NNP("NNP"), NN("NN");

	private String noun;
	private static Set<String> nouns;
	
	static {
		nouns = getEnums();
	}
	
	Noun(String noun) {
		this.noun = noun;
	}

	public String getNoun() {
		return this.noun;
	}

	public static Set<String> getEnums() {
		Set<String> nouns = new HashSet<String>();

		for (Noun noun : Noun.values()) {
			nouns.add(noun.name());
		}

		return nouns;
	}

	public static boolean isNoun(String _pos) {
		if (nouns.contains(_pos))
			return true;
		return false;
	}

}
