package org.kontext.analyser.context;

public class Association {
	private final Context context1;
	private final Context context2;
	
	private final float probability;
	
	public Association(Context c1, Context c2, float p) {
		context1 = c1;
		context2 = c2;
		probability = p;
	}
	
	public Context getContext1() {
		return context1;
	}
	
	public Context getContext2() {
		return context2;
	}
	
	public float getProbability() {
		return probability;
	}
}

