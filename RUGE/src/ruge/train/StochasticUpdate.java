package ruge.train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.struct.Matrix;
import model.struct.Rule;
import model.struct.Triple;


public class StochasticUpdate {
	public ArrayList<Triple> lstPosTriples;
	public ArrayList<Triple> lstHeadNegTriples;
	public ArrayList<Triple> lstTailNegTriples;
	public ArrayList<Rule> lstRules;
	public HashMap<String, Double> MapTriple2Rule;
	public Matrix Real_MatrixE;
	public Matrix Real_MatrixR;
	public Matrix Imag_MatrixE;
	public Matrix Imag_MatrixR;
	public Matrix Real_MatrixEGradient;
	public Matrix Real_MatrixRGradient;
	public Matrix Imag_MatrixEGradient;
	public Matrix Imag_MatrixRGradient;
	public double dGammaE;
	public double dGammaR;
	public double dTheta;
	public double dWeight;
	public double dC;
	
	
	public StochasticUpdate(
			ArrayList<Triple> inLstPosTriples,
			ArrayList<Triple> inLstHeadNegTriples,
			ArrayList<Triple> inLstTailNegTriples,
			ArrayList<Rule> inlstRules,
			Matrix in_Real_MatrixE,
			Matrix in_Real_MatrixR,
			Matrix in_Imag_MatrixE,
			Matrix in_Imag_MatrixR,
			Matrix in_Real_MatrixEGradient,
			Matrix in_Real_MatrixRGradient,
			Matrix in_Imag_MatrixEGradient,
			Matrix in_Imag_MatrixRGradient,
			double inGammaE,
			double inGammaR,
			double inTheta,
			double inWeight,
			double inC) {
		lstPosTriples = inLstPosTriples;
		lstHeadNegTriples = inLstHeadNegTriples;
		lstTailNegTriples = inLstTailNegTriples;
		lstRules = inlstRules;
		Real_MatrixE = in_Real_MatrixE;
		Real_MatrixR = in_Real_MatrixR;
		Imag_MatrixE = in_Imag_MatrixE;
		Imag_MatrixR = in_Imag_MatrixR;
		Real_MatrixEGradient = in_Real_MatrixEGradient;
		Real_MatrixRGradient = in_Real_MatrixRGradient;
		Imag_MatrixEGradient = in_Imag_MatrixEGradient;
		Imag_MatrixRGradient = in_Imag_MatrixRGradient;
		dGammaE = inGammaE;
		dGammaR = inGammaR;
		dTheta = inTheta;
		dWeight = inWeight;
		dC = inC;
	}
	
	public double scoring (Triple triple) throws Exception{
		int iNumberOfFactors = Real_MatrixE.columns();
		double dValue = 0.0;
		int iHead = triple.head();
		int iTail = triple.tail();
		int iRelation = triple.relation();
		for (int p = 0; p < iNumberOfFactors; p++) {
			dValue += Real_MatrixE.get(iHead, p) * Real_MatrixR.get(iRelation, p) * Real_MatrixE.get(iTail, p)
					+ Imag_MatrixE.get(iHead, p) * Real_MatrixR.get(iRelation, p) * Imag_MatrixE.get(iTail, p)
					+ Real_MatrixE.get(iHead, p) * Imag_MatrixR.get(iRelation, p) * Imag_MatrixE.get(iTail, p) 
					- Imag_MatrixE.get(iHead, p) * Imag_MatrixR.get(iRelation, p) * Real_MatrixE.get(iTail, p);
		}
		return dValue;
	}
	
	public double sigmoid(double fs){

		double nfs = 0.0;
		if(fs > 10.0){
			nfs = 1.0;
		}
		else if (fs < -10.0){
			nfs = 0.0;
		}
		else{
			nfs = 1.0 / (1.0 + Math.exp(-fs));
		}
		return nfs;	
	}
	
	public void stochasticIteration(int iter) throws Exception {
		Real_MatrixEGradient.setToValue(0.0);
		Real_MatrixRGradient.setToValue(0.0);
		Imag_MatrixEGradient.setToValue(0.0);
		Imag_MatrixRGradient.setToValue(0.0);
		
		int iPos = lstPosTriples.size();
		int iNeg = lstHeadNegTriples.size();
		int iRule = lstRules.size();

		for (int iID = 0; iID < iPos; iID++) {
			Triple PosTriple = lstPosTriples.get(iID);	
			StochasticGradient posGradient = new StochasticGradient(
					PosTriple,
					iPos + 2.0 * iNeg,
					Real_MatrixE,
					Real_MatrixR,
					Imag_MatrixE,
					Imag_MatrixR,
					Real_MatrixEGradient,
					Real_MatrixRGradient,
					Imag_MatrixEGradient,
					Imag_MatrixRGradient,
					dTheta,
					1.0,
					1.0);
			posGradient.calculateGradient();
		}

		for (int iID = 0; iID < iNeg; iID++) {
			Triple HeadNegTriple = lstHeadNegTriples.get(iID);
			Triple TailNegTriple = lstTailNegTriples.get(iID);
			
			StochasticGradient headGradient = new StochasticGradient(
					HeadNegTriple,
					iPos + 2.0 * iNeg,
					Real_MatrixE,
					Real_MatrixR,
					Imag_MatrixE,
					Imag_MatrixR,
					Real_MatrixEGradient,
					Real_MatrixRGradient,
					Imag_MatrixEGradient,
					Imag_MatrixRGradient,
					dTheta,
					0.0,
					1.0);
			headGradient.calculateGradient();
			
			StochasticGradient tailGradient = new StochasticGradient(
					TailNegTriple,
					iPos + 2.0 * iNeg,
					Real_MatrixE,
					Real_MatrixR,
					Imag_MatrixE,
					Imag_MatrixR,
					Real_MatrixEGradient,
					Real_MatrixRGradient,
					Imag_MatrixEGradient,
					Imag_MatrixRGradient,
					dTheta,
					0.0,
					1.0);
			tailGradient.calculateGradient();
		}
		
		MapTriple2Rule = new HashMap<String, Double>();
		for (int iID = 0; iID < iRule; iID++) {
			Rule rule = lstRules.get(iID);	
			double dPoster = 0.0;
			int m,s,n;
			PosteriorRegular triplePoster = new PosteriorRegular(
					rule,
					Real_MatrixE,
					Real_MatrixR,
					Imag_MatrixE,
					Imag_MatrixR,
					dC
					);
			dPoster = triplePoster.calculatePosterior();	
			
			int unseen = rule.unseen();
			if (unseen==1) {
				m = rule.fstTriple().head();
				n = rule.fstTriple().tail();
				s = rule.fstTriple().relation();
			}
			else if (unseen==2){
				m = rule.sndTriple().head();
				n = rule.sndTriple().tail();
				s = rule.sndTriple().relation();
			}
			else if (unseen==3){
				m = rule.trdTriple().head();
				n = rule.trdTriple().tail();
				s = rule.trdTriple().relation();
			}
			else{
				m = rule.fouTriple().head();
				n = rule.fouTriple().tail();
				s = rule.fouTriple().relation();
			}
			
			
			if (!MapTriple2Rule.containsKey(m+"_"+s+"_"+n)){
				MapTriple2Rule.put(m+"_"+s+"_"+n, dPoster);			
			}
			else{
				double value = MapTriple2Rule.get(m+"_"+s+"_"+n);
				MapTriple2Rule.put(m+"_"+s+"_"+n, dPoster+value);
			}
		}
		
		Iterator<String> lstTriple = MapTriple2Rule.keySet().iterator();
		while (lstTriple.hasNext()) {
			String strTriple = lstTriple.next();
			int m = Integer.parseInt(strTriple.split("_")[0]);
			int n = Integer.parseInt(strTriple.split("_")[2]);
			int s = Integer.parseInt(strTriple.split("_")[1]);
			Triple UnlabelTriple = new Triple(m, n, s);
			double dLabel = sigmoid(scoring(UnlabelTriple)) + MapTriple2Rule.get(strTriple);
			dLabel = Math.max(0.0, dLabel);
			dLabel = Math.min(1.0, dLabel);	
			StochasticGradient posGradient = new StochasticGradient(
					UnlabelTriple,
					MapTriple2Rule.size(),
					Real_MatrixE,
					Real_MatrixR,
					Imag_MatrixE,
					Imag_MatrixR,
					Real_MatrixEGradient,
					Real_MatrixRGradient,
					Imag_MatrixEGradient,
					Imag_MatrixRGradient,
					dTheta,
					dLabel,
					1.0);
			posGradient.calculateGradient();	
		}
		
		
		Real_MatrixEGradient.rescaleByRow();
		Real_MatrixRGradient.rescaleByRow();
		Imag_MatrixEGradient.rescaleByRow();
		Imag_MatrixRGradient.rescaleByRow();

		
		// AdaGrad
		for (int i = 0; i < Real_MatrixE.rows(); i++) {
			for (int j = 0; j < Real_MatrixE.columns(); j++) {
				double dValue = Real_MatrixEGradient.get(i, j);
				Real_MatrixEGradient.accumulatedByGrad(i, j);
				double dLrate = Math.sqrt(Real_MatrixEGradient.getSum(i, j)) + 1e-8;
				Real_MatrixE.add(i, j, -1.0 * dGammaE * dValue / dLrate);	
			}
		}

		for (int i = 0; i < Real_MatrixR.rows(); i++) {
			for (int j = 0; j < Real_MatrixR.columns(); j++) {
				double dValue = Real_MatrixRGradient.get(i, j);
				Real_MatrixRGradient.accumulatedByGrad(i, j);
				double dLrate = Math.sqrt(Real_MatrixRGradient.getSum(i, j)) + 1e-8;
				Real_MatrixR.add(i, j, -1.0 * dGammaR * dValue  / dLrate);
			}
		}
		
		for (int i = 0; i < Imag_MatrixE.rows(); i++) {
			for (int j = 0; j < Imag_MatrixE.columns(); j++) {
				double dValue = Imag_MatrixEGradient.get(i, j);
				Imag_MatrixEGradient.accumulatedByGrad(i, j);
				double dLrate = Math.sqrt(Imag_MatrixEGradient.getSum(i, j)) + 1e-8;
				Imag_MatrixE.add(i, j, -1.0 * dGammaE * dValue  / dLrate);
				
			}
		}
		
		for (int i = 0; i < Imag_MatrixR.rows(); i++) {
			for (int j = 0; j < Imag_MatrixR.columns(); j++) {
				double dValue = Imag_MatrixRGradient.get(i, j);
				Imag_MatrixRGradient.accumulatedByGrad(i, j);
				double dLrate = Math.sqrt(Imag_MatrixRGradient.getSum(i, j)) + 1e-8;
				Imag_MatrixR.add(i, j, -1.0 * dGammaR * dValue  / dLrate);
			}
		}
	}
}
