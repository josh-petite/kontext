package org.kontext.analyser;

public enum Noun {	
	NNP("NNP"), NN("NN");
	
	private String noun;

	Noun(String noun) {
		this.noun = noun;
	}
	
	public String getNoun() {
		return this.noun;
	}
	
	public static boolean isNoun(String _pos) {
		if (Noun.valueOf(_pos) != null)
			return true;
		return false;
	}
}
