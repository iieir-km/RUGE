package model.struct;

public class Rule {
	private Triple FstTriple;
	private Triple SndTriple;
	private Triple TrdTriple;
	private Triple FouTriple;
	private int unseen;
	private double confidence;
	
	public Rule(Triple inFstTriple, Triple inSndTriple, int inUnseen, double inConfidence) {
		FstTriple = inFstTriple;
		SndTriple = inSndTriple;
		unseen = inUnseen;
		confidence = inConfidence;
	}
	public Rule(Triple inFstTriple, Triple inSndTriple,Triple inTrdTriple, int inUnseen, double inConfidence) {
		FstTriple = inFstTriple;
		SndTriple = inSndTriple;
		TrdTriple = inTrdTriple;
		unseen = inUnseen;
		confidence = inConfidence;
	}
	public Rule(Triple inFstTriple, Triple inSndTriple,Triple inTrdTriple, Triple inFouTriple, int inUnseen, double inConfidence) {
		FstTriple = inFstTriple;
		SndTriple = inSndTriple;
		TrdTriple = inTrdTriple;
		FouTriple = inFouTriple;
		unseen = inUnseen;
		confidence = inConfidence;
	}
	public Triple fstTriple() {
		return FstTriple;
	}
	
	public Triple sndTriple() {
		return SndTriple;
	}
	
	public Triple trdTriple() {
		return TrdTriple;
	}
	
	public Triple fouTriple() {
		return FouTriple;
	}
	
	public int unseen() {
		return unseen;
	}
	
	public double confi() {
		return confidence;
	}
}
