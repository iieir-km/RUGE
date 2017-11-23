package ruge.train;

import model.struct.Matrix;
import model.struct.Rule;

public class PosteriorRegular {
	public Rule Rule;
	public Matrix Real_MatrixE;
	public Matrix Real_MatrixR;
	public Matrix Imag_MatrixE;
	public Matrix Imag_MatrixR;
	double dC;
	double dFstPi;
	double dSndPi;
	double dTrdPi;
	double dConfidence;

	
	public PosteriorRegular(
			Rule inRule,
			Matrix in_Real_MatrixE,
			Matrix in_Real_MatrixR,
			Matrix in_Imag_MatrixE,
			Matrix in_Imag_MatrixR,
			double inC) {
		Rule = inRule;
		Real_MatrixE = in_Real_MatrixE;
		Real_MatrixR = in_Real_MatrixR;
		Imag_MatrixE = in_Imag_MatrixE;
		Imag_MatrixR = in_Imag_MatrixR;
		dC = inC;
		
	}
	
	public double sigmoid(double fs){
		//compute gradient for sigmoid
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
	
	public double calculatePosterior() throws Exception {
		int iNumberOfFactors = Real_MatrixE.columns();
		int iFstHead = Rule.fstTriple().head();
		int iFstTail = Rule.fstTriple().tail();
		int iFstRelation = Rule.fstTriple().relation();
		int iSndHead = Rule.sndTriple().head();
		int iSndTail = Rule.sndTriple().tail();
		int iSndRelation = Rule.sndTriple().relation();
		
		if(Rule.unseen() == 1){
			dSndPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dSndPi += Real_MatrixE.get(iSndHead, p) * Real_MatrixR.get(iSndRelation, p) * Real_MatrixE.get(iSndTail, p)
						+ Imag_MatrixE.get(iSndHead, p) * Real_MatrixR.get(iSndRelation, p) * Imag_MatrixE.get(iSndTail, p)
						+ Real_MatrixE.get(iSndHead, p) * Imag_MatrixR.get(iSndRelation, p) * Imag_MatrixE.get(iSndTail, p) 
						- Imag_MatrixE.get(iSndHead, p) * Imag_MatrixR.get(iSndRelation, p) * Real_MatrixE.get(iSndTail, p);
			}
			
			return dC * Rule.confi() * (sigmoid(dSndPi) - 1.0);
		}
		else if(Rule.unseen() == 2){
			dFstPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dFstPi  += Real_MatrixE.get(iFstHead, p) * Real_MatrixR.get(iFstRelation, p) * Real_MatrixE.get(iFstTail, p)
						+ Imag_MatrixE.get(iFstHead, p) * Real_MatrixR.get(iFstRelation, p) * Imag_MatrixE.get(iFstTail, p)
						+ Real_MatrixE.get(iFstHead, p) * Imag_MatrixR.get(iFstRelation, p) * Imag_MatrixE.get(iFstTail, p) 
						- Imag_MatrixE.get(iFstHead, p) * Imag_MatrixR.get(iFstRelation, p) * Real_MatrixE.get(iFstTail, p);
			}
			
			return dC * Rule.confi() * sigmoid(dFstPi);
		}
		else if(Rule.unseen() == 3)
		{
			dSndPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dSndPi += Real_MatrixE.get(iSndHead, p) * Real_MatrixR.get(iSndRelation, p) * Real_MatrixE.get(iSndTail, p)
						+ Imag_MatrixE.get(iSndHead, p) * Real_MatrixR.get(iSndRelation, p) * Imag_MatrixE.get(iSndTail, p)
						+ Real_MatrixE.get(iSndHead, p) * Imag_MatrixR.get(iSndRelation, p) * Imag_MatrixE.get(iSndTail, p) 
						- Imag_MatrixE.get(iSndHead, p) * Imag_MatrixR.get(iSndRelation, p) * Real_MatrixE.get(iSndTail, p);
			}
			
			dFstPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dFstPi  += Real_MatrixE.get(iFstHead, p) * Real_MatrixR.get(iFstRelation, p) * Real_MatrixE.get(iFstTail, p)
						+ Imag_MatrixE.get(iFstHead, p) * Real_MatrixR.get(iFstRelation, p) * Imag_MatrixE.get(iFstTail, p)
						+ Real_MatrixE.get(iFstHead, p) * Imag_MatrixR.get(iFstRelation, p) * Imag_MatrixE.get(iFstTail, p) 
						- Imag_MatrixE.get(iFstHead, p) * Imag_MatrixR.get(iFstRelation, p) * Real_MatrixE.get(iFstTail, p);
			}
			
			return dC * Rule.confi() * sigmoid(dFstPi) * sigmoid(dSndPi);		
		}		
		else{
			int iTrdHead = Rule.trdTriple().head();
			int iTrdTail = Rule.trdTriple().tail();
			int iTrdRelation = Rule.trdTriple().relation();
			
			dSndPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dSndPi += Real_MatrixE.get(iSndHead, p) * Real_MatrixR.get(iSndRelation, p) * Real_MatrixE.get(iSndTail, p)
						+ Imag_MatrixE.get(iSndHead, p) * Real_MatrixR.get(iSndRelation, p) * Imag_MatrixE.get(iSndTail, p)
						+ Real_MatrixE.get(iSndHead, p) * Imag_MatrixR.get(iSndRelation, p) * Imag_MatrixE.get(iSndTail, p)  
						- Imag_MatrixE.get(iSndHead, p) * Imag_MatrixR.get(iSndRelation, p) * Real_MatrixE.get(iSndTail, p);
			}
			
			dFstPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dFstPi  += Real_MatrixE.get(iFstHead, p) * Real_MatrixR.get(iFstRelation, p) * Real_MatrixE.get(iFstTail, p)
						+ Imag_MatrixE.get(iFstHead, p) * Real_MatrixR.get(iFstRelation, p) * Imag_MatrixE.get(iFstTail, p)
						+ Real_MatrixE.get(iFstHead, p) * Imag_MatrixR.get(iFstRelation, p) * Imag_MatrixE.get(iFstTail, p) 
						- Imag_MatrixE.get(iFstHead, p) * Imag_MatrixR.get(iFstRelation, p) * Real_MatrixE.get(iFstTail, p);
			}
			
			dTrdPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dTrdPi  += Real_MatrixE.get(iTrdHead, p) * Real_MatrixR.get(iTrdRelation, p) * Real_MatrixE.get(iTrdTail, p)
						+ Imag_MatrixE.get(iTrdHead, p) * Real_MatrixR.get(iTrdRelation, p) * Imag_MatrixE.get(iTrdTail, p)
						+ Real_MatrixE.get(iTrdHead, p) * Imag_MatrixR.get(iTrdRelation, p) * Imag_MatrixE.get(iTrdTail, p) 
						- Imag_MatrixE.get(iTrdHead, p) * Imag_MatrixR.get(iTrdRelation, p) * Real_MatrixE.get(iTrdTail, p);
			}
			return dC * Rule.confi() * sigmoid(dFstPi) * sigmoid(dSndPi) * sigmoid(dTrdPi);
		}
	}	
}
