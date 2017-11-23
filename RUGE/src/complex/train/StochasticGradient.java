package complex.train;

import model.struct.Matrix;
import model.struct.Triple;

public class StochasticGradient {
	public Triple triple;
	public Matrix Real_MatrixE;
	public Matrix Real_MatrixR;
	public Matrix Imag_MatrixE;
	public Matrix Imag_MatrixR;
	public Matrix Real_MatrixEGradient;
	public Matrix Real_MatrixRGradient;
	public Matrix Imag_MatrixEGradient;
	public Matrix Imag_MatrixRGradient;
	public double dTheta;
	public double dLabel;
	public double dSize;
	
	public StochasticGradient(
			Triple inTriple,
			double inSize,
			Matrix in_Real_MatrixE,
			Matrix in_Real_MatrixR,
			Matrix in_Imag_MatrixE,
			Matrix in_Imag_MatrixR,
			Matrix in_Real_MatrixEGradient,
			Matrix in_Real_MatrixRGradient,
			Matrix in_Imag_MatrixEGradient,
			Matrix in_Imag_MatrixRGradient,
			double inTheta,
			double inLabel) {
		triple = inTriple;
		Real_MatrixE = in_Real_MatrixE;
		Real_MatrixR = in_Real_MatrixR;
		Imag_MatrixE = in_Imag_MatrixE;
		Imag_MatrixR = in_Imag_MatrixR;
		Real_MatrixEGradient = in_Real_MatrixEGradient;
		Real_MatrixRGradient = in_Real_MatrixRGradient;
		Imag_MatrixEGradient = in_Imag_MatrixEGradient;
		Imag_MatrixRGradient = in_Imag_MatrixRGradient;
		dTheta = inTheta;
		dLabel = inLabel;
		dSize = inSize;
	}
	
	public double calculateSigmoid(double fs){
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
//		nfs = 1.0 / (1.0 + Math.exp(-fs));
		return nfs;	
	}
		
	
	public void calculateGradient() throws Exception {
		int iNumberOfFactors = Real_MatrixE.columns();
		int iHead = triple.head();
		int iTail = triple.tail();
		int iRelation = triple.relation();		
		double dTriPi = 0.0;
		for (int p = 0; p < iNumberOfFactors; p++) {
			dTriPi += Real_MatrixE.get(iHead, p) * Real_MatrixR.get(iRelation, p) * Real_MatrixE.get(iTail, p)
					+ Imag_MatrixE.get(iHead, p) * Real_MatrixR.get(iRelation, p) * Imag_MatrixE.get(iTail, p)
					+ Real_MatrixE.get(iHead, p) * Imag_MatrixR.get(iRelation, p) * Imag_MatrixE.get(iTail, p) 
					- Imag_MatrixE.get(iHead, p) * Imag_MatrixR.get(iRelation, p) * Real_MatrixE.get(iTail, p);
		}
		dTriPi = calculateSigmoid(-dLabel * dTriPi);
		//calculate gradient
		for (int p = 0; p < iNumberOfFactors; p++) {
			double dRealHead = 0.0;			
			double dRealTail = 0.0;
			double dRealRel  = 0.0;
			double dImagHead = 0.0;			
			double dImagTail = 0.0;
			double dImagRel  = 0.0;
			
			dRealHead = Real_MatrixR.get(iRelation, p) * Real_MatrixE.get(iTail, p) + Imag_MatrixR.get(iRelation, p) * Imag_MatrixE.get(iTail, p);			
			dRealTail = Real_MatrixR.get(iRelation, p) * Real_MatrixE.get(iHead, p) - Imag_MatrixR.get(iRelation, p) * Imag_MatrixE.get(iHead, p);
			dRealRel  = Real_MatrixE.get(iHead, p) * Real_MatrixE.get(iTail, p) + Imag_MatrixE.get(iHead, p) * Imag_MatrixE.get(iTail, p);
			dImagHead = Real_MatrixR.get(iRelation, p) * Imag_MatrixE.get(iTail, p) - Imag_MatrixR.get(iRelation, p) * Real_MatrixE.get(iTail, p);			
			dImagTail = Real_MatrixR.get(iRelation, p) * Imag_MatrixE.get(iHead, p) + Imag_MatrixR.get(iRelation, p) * Real_MatrixE.get(iHead, p);
			dImagRel  = Real_MatrixE.get(iHead, p) * Imag_MatrixE.get(iTail, p) - Imag_MatrixE.get(iHead, p) * Real_MatrixE.get(iTail, p);
			
						
			Real_MatrixEGradient.add(iHead, p, (-dLabel * dTriPi * dRealHead  + 2.0 * (dTheta/iNumberOfFactors) * Real_MatrixE.get(iHead, p)) / dSize);
			Real_MatrixEGradient.add(iTail, p, (-dLabel * dTriPi * dRealTail + 2.0 * (dTheta/iNumberOfFactors) * Real_MatrixE.get(iTail, p))/ dSize);
			Real_MatrixRGradient.add(iRelation, p, (-dLabel * dTriPi * dRealRel + 2.0 * (dTheta/iNumberOfFactors) * Real_MatrixR.get(iRelation, p))/ dSize);
			Imag_MatrixEGradient.add(iHead, p, (-dLabel * dTriPi * dImagHead + 2.0 * (dTheta/iNumberOfFactors) * Imag_MatrixE.get(iHead, p))/ dSize);
			Imag_MatrixEGradient.add(iTail, p, (-dLabel * dTriPi * dImagTail + 2.0 * (dTheta/iNumberOfFactors) * Imag_MatrixE.get(iTail, p))/ dSize);
			Imag_MatrixRGradient.add(iRelation, p, (-dLabel * dTriPi * dImagRel + 2.0 * (dTheta/iNumberOfFactors) * Imag_MatrixR.get(iRelation, p))/ dSize);

			
		}
	}
}
