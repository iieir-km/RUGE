package complex.train;

import java.util.ArrayList;

import model.struct.Matrix;
import model.struct.Triple;


public class StochasticUpdate {
	public ArrayList<Triple> lstPosTriples;
	public ArrayList<Triple> lstHeadNegTriples;
	public ArrayList<Triple> lstTailNegTriples;
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
	
	public StochasticUpdate(
			ArrayList<Triple> inLstPosTriples,
			ArrayList<Triple> inLstHeadNegTriples,
			ArrayList<Triple> inLstTailNegTriples,
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
			double inTheta) {
		lstPosTriples = inLstPosTriples;
		lstHeadNegTriples = inLstHeadNegTriples;
		lstTailNegTriples = inLstTailNegTriples;
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
	}
	
	public void stochasticIteration(int iter) throws Exception {
		Real_MatrixEGradient.setToValue(0.0);
		Real_MatrixRGradient.setToValue(0.0);
		Imag_MatrixEGradient.setToValue(0.0);
		Imag_MatrixRGradient.setToValue(0.0);
		
		int iPos = lstPosTriples.size();
		int iNeg = lstHeadNegTriples.size();
//		System.out.println("Training with positive triples! " + iPos);
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
					1.0);
			posGradient.calculateGradient();
		}
//		System.out.println("Training with negtive triples!");
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
					-1.0);
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
					-1.0);
			tailGradient.calculateGradient();
		}
		
		Real_MatrixEGradient.rescaleByRow();
		Real_MatrixRGradient.rescaleByRow();
		Imag_MatrixEGradient.rescaleByRow();
		Imag_MatrixRGradient.rescaleByRow();

		
		// AdaGrad with embeddings
//		System.out.println("AdaGrad with embeddings!");
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
		
//		Real_MatrixE.normalizeByRow();
//		Imag_MatrixE.normalizeByRow();
//		Real_MatrixR.normalizeByRow();
//		Imag_MatrixR.normalizeByRow();

	}
}
