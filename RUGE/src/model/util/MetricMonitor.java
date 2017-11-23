package model.util;

import java.util.HashMap;

import model.struct.Matrix;
import model.struct.TripleSet;

public class MetricMonitor {
	public TripleSet lstValidateTriples;
	public HashMap<String, Boolean> lstTriples;
	public Matrix Real_MatrixE;
	public Matrix Real_MatrixR;
	public Matrix Imag_MatrixE;
	public Matrix Imag_MatrixR;
	public double dMeanRank;
	public double dMRR;
	public double dHits;
	
	public MetricMonitor(TripleSet inLstValidateTriples,
			HashMap<String, Boolean> inlstTriples,
			Matrix in_Real_MatrixE,
			Matrix in_Real_MatrixR,
			Matrix in_Imag_MatrixE,
			Matrix in_Imag_MatrixR) {
		lstValidateTriples = inLstValidateTriples;
		lstTriples = inlstTriples;
		Real_MatrixE = in_Real_MatrixE;
		Real_MatrixR = in_Real_MatrixR;
		Imag_MatrixE = in_Imag_MatrixE;
		Imag_MatrixR = in_Imag_MatrixR;
	}
	
	public void calculateMetrics() throws Exception {
		int iNumberOfEntities = Real_MatrixE.rows();
		int iNumberOfFactors = Real_MatrixE.columns();
		
		int iCnt = 0;
		double avgMeanRank = 0.0;
		double avgMRR = 0.0;
		int avgHits = 0;
		for (int iID = 0; iID < lstValidateTriples.triples(); iID++) {
			int iRelationID = lstValidateTriples.get(iID).relation();
			int iSubjectID = lstValidateTriples.get(iID).head();
			int iObjectID = lstValidateTriples.get(iID).tail();
			double dTargetValue = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dTargetValue += Real_MatrixE.get(iSubjectID, p) * Real_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iObjectID, p)
						+ Imag_MatrixE.get(iSubjectID, p) * Real_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iObjectID, p)
						+ Real_MatrixE.get(iSubjectID, p) * Imag_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iObjectID, p) 
						- Imag_MatrixE.get(iSubjectID, p) * Imag_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iObjectID, p);
			}
			
			int iLeftRank = 1;
			int iLeftIdentical = 0;
			for (int iLeftID = 0; iLeftID < iNumberOfEntities; iLeftID++) {
				double dValue = 0.0;
				String negTiple = iLeftID + "\t" + iRelationID + "\t" +iObjectID;
				if(!lstTriples.containsKey(negTiple)){
					
					for (int p = 0; p < iNumberOfFactors; p++) {
						dValue += Real_MatrixE.get(iLeftID, p) * Real_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iObjectID, p)
								+ Imag_MatrixE.get(iLeftID, p) * Real_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iObjectID, p)
								+ Real_MatrixE.get(iLeftID, p) * Imag_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iObjectID, p) 
								- Imag_MatrixE.get(iLeftID, p) * Imag_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iObjectID, p);
					}
//					System.out.println("left"+ negTiple +" " +dValue +" "+dTargetValue);
					if (dValue > dTargetValue) {
						iLeftRank++;
					}
					if (dValue == dTargetValue) {
						iLeftIdentical++;
					}
				}
			}
			double dLeftRank = (double)iLeftRank;
			int iLeftHitsAt10 = 0;
			if (dLeftRank <= 10.0) {
				iLeftHitsAt10 = 1;
			}
			avgMeanRank += dLeftRank;
			avgMRR += 1.0/(double)dLeftRank;
			avgHits += iLeftHitsAt10;
			iCnt++;
			
			int iRightRank = 1;
			int iRightIdentical = 0;
			for (int iRightID = 0; iRightID < iNumberOfEntities; iRightID++) {
				double dValue = 0.0;
				String negTiple = iSubjectID + "\t" + iRelationID + "\t" +iRightID;
				if(!lstTriples.containsKey(negTiple)){
					for (int p = 0; p < iNumberOfFactors; p++) {
						dValue += Real_MatrixE.get(iSubjectID, p) * Real_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iRightID, p)
								+ Imag_MatrixE.get(iSubjectID, p) * Real_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iRightID, p)
								+ Real_MatrixE.get(iSubjectID, p) * Imag_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iRightID, p) 
								- Imag_MatrixE.get(iSubjectID, p) * Imag_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iRightID, p);
					}
//					System.out.println("right"+ negTiple +" " + dValue +" "+dTargetValue);
					if (dValue > dTargetValue) {
						iRightRank++;
					}
					if (dValue == dTargetValue) {
						iRightIdentical++;
					}
				}
			}
			double dRightRank = (double) iRightRank;
			int iRightHitsAt10 = 0;
			if (dRightRank <= 10.0) {
				iRightHitsAt10 = 1;
			}
			avgMeanRank += dRightRank;
			avgMRR += 1.0/(double)dRightRank;
			avgHits += iRightHitsAt10;
			iCnt++;	
			
			
//			System.out.println("dLeftRank:" + dLeftRank + "\t" + "dTargetValue:" + dTargetValue);
		}
		dMRR = avgMRR / (double)(iCnt);
		dHits = (double)avgHits / (double)(iCnt);
		System.out.println("------Current MRR:" + dMRR + "\t" + "Current Hits@10:" + dHits);

	}
}
