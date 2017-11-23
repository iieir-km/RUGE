package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.struct.Matrix;


public class Eval_LinkPrediction {
	public int iNumberOfEntities;
	public int iNumberOfRelations;
	public int iNumberOfFactors;
	
	public Matrix Real_MatrixE;
	public Matrix Real_MatrixR;
	public Matrix Imag_MatrixE;
	public Matrix Imag_MatrixR;
	List<Double> iFiltList = new ArrayList<Double>();
	List<Double> iRawList = new ArrayList<Double>();
	public HashMap<String, Boolean> lstTriples = null;
	
	
	public Eval_LinkPrediction(int iEntities, int iRelations, int iFactors) {
		iNumberOfEntities = iEntities;
		iNumberOfRelations = iRelations;
		iNumberOfFactors = iFactors;
	}
	
	public void LPEvaluation(
			String fnRealMatrixE, 
			String fnRealMatrixR,
			String fnImagMatrixE, 
			String fnImagMatrixR,
			String fnTrainTriples, 
			String fnValidTriples, 
			String fnTestTriples) throws Exception {
		preprocess(fnTrainTriples,fnValidTriples,fnTestTriples, 
				fnRealMatrixE, fnRealMatrixR, fnImagMatrixE, fnImagMatrixR);
		evaluate( fnTestTriples);
	}
	
	public void preprocess(
			String fnTrainTriples, String fnValidTriples, String fnTestTriples, 
			String fnRealMatrixE, 
			String fnRealMatrixR,
			String fnImagMatrixE, 
			String fnImagMatrixR) throws Exception {
		System.out.println("\nLoading training and validate triples");
		Real_MatrixE = new Matrix(iNumberOfEntities, iNumberOfFactors);
		Real_MatrixE.load(fnRealMatrixE);
		Imag_MatrixE = new Matrix(iNumberOfEntities, iNumberOfFactors);
		Imag_MatrixE.load(fnImagMatrixE);
		
		Real_MatrixR = new Matrix(iNumberOfRelations, iNumberOfFactors);
		Real_MatrixR.load(fnRealMatrixR);
		Imag_MatrixR = new Matrix(iNumberOfRelations, iNumberOfFactors);
		Imag_MatrixR.load(fnImagMatrixR);
		
		BufferedReader train = new BufferedReader(new InputStreamReader(
				new FileInputStream(fnTrainTriples), "UTF-8"));	
		BufferedReader valid = new BufferedReader(new InputStreamReader(
				new FileInputStream(fnValidTriples), "UTF-8"));
		BufferedReader test = new BufferedReader(new InputStreamReader(
				new FileInputStream(fnTestTriples), "UTF-8"));
		lstTriples = new HashMap<String, Boolean> ();
		String line = "";
		while ((line = train.readLine()) != null) {
			if (!lstTriples.containsKey(line)) {
					lstTriples.put(line , true);
				} 
		}	
		line = "";
		while ((line = valid.readLine()) != null) {
			if (!lstTriples.containsKey(line)) {
				lstTriples.put(line , true);
			} 
		}
		line = "";
		while ((line = test.readLine()) != null) {
			if (!lstTriples.containsKey(line)) {
				lstTriples.put(line, true);
			} 

		}
		System.out.println("triples:"+lstTriples.size());
		valid.close();
		test.close();
		train.close();
		System.out.println("Success.");
	}
	
	public void evaluate(String fnTestTriples) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fnTestTriples), "UTF-8"));

		String line = "";
		int iCnt = 0;
		double dTotalMeanRank_filt = 0.0;
		double dTotalMRR_filt = 0.0;
		int iTotalHits1_filt = 0;
		int iTotalHits3_filt = 0;
		int iTotalHits5_filt = 0;
		int iTotalHits10_filt = 0;
		double dMedian_filt = 0.0;
		
		double dTotalMeanRank_raw = 0.0;
		double dTotalMRR_raw = 0.0;
		int iTotalHits1_raw = 0;
		int iTotalHits3_raw = 0;
		int iTotalHits5_raw = 0;
		int iTotalHits10_raw = 0;
		double dMedian_raw = 0.0;
		
		while ((line = reader.readLine()) != null) {
			System.out.println("triple:" + iCnt/2);
			
			String[] tokens = line.split("\t");
			int iRelationID = Integer.parseInt(tokens[1]);
			int iSubjectID = Integer.parseInt(tokens[0]);
			int iObjectID = Integer.parseInt(tokens[2]);
			double dTargetValue = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dTargetValue += Real_MatrixE.get(iSubjectID, p) * Real_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iObjectID, p)
						+ Imag_MatrixE.get(iSubjectID, p) * Real_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iObjectID, p)
						+ Real_MatrixE.get(iSubjectID, p) * Imag_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iObjectID, p) 
						- Imag_MatrixE.get(iSubjectID, p) * Imag_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iObjectID, p);
			}
			
			int iLeftRank_filt = 1;
			int iLeftIdentical_filt = 0;
			int iLeftRank_raw = 1;
			int iLeftIdentical_raw = 0;
			for (int iLeftID = 0; iLeftID < iNumberOfEntities; iLeftID++) {
				double dValue = 0.0;
				for (int p = 0; p < iNumberOfFactors; p++) {
					dValue += Real_MatrixE.get(iLeftID, p) * Real_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iObjectID, p)
							+ Imag_MatrixE.get(iLeftID, p) * Real_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iObjectID, p)
							+ Real_MatrixE.get(iLeftID, p) * Imag_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iObjectID, p) 
							- Imag_MatrixE.get(iLeftID, p) * Imag_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iObjectID, p);
				}
				String negTiple = iLeftID + "\t" + iRelationID + "\t" +iObjectID;
				if(!lstTriples.containsKey(negTiple)){
					if (dValue > dTargetValue) {
						iLeftRank_filt++;
					}
					if (dValue == dTargetValue) {
						iLeftIdentical_filt++;
					}
				}	
				if (dValue > dTargetValue) {
					iLeftRank_raw++;
				}
				if (dValue == dTargetValue) {
					iLeftIdentical_raw++;
				}
			}

			double dLeftRank_filt = (double)(2.0 * iLeftRank_filt + iLeftIdentical_filt) / 2.0;
			double dLeftRank_raw = (double)(2.0 * iLeftRank_raw + iLeftIdentical_raw - 1.0) / 2.0;
			int iLeftHitsAt1_filt = 0,iLeftHitsAt3_filt = 0,iLeftHitsAt5_filt = 0,iLeftHitsAt10_filt = 0;
			int iLeftHitsAt1_raw = 0,iLeftHitsAt3_raw = 0,iLeftHitsAt5_raw = 0,iLeftHitsAt10_raw = 0;
			if (dLeftRank_filt <= 1.0) {
				iLeftHitsAt1_filt = 1;
			}
			if (dLeftRank_filt <= 3.0) {
				iLeftHitsAt3_filt = 1;
			}
			if (dLeftRank_filt <= 5.0) {
				iLeftHitsAt5_filt = 1;
			}
			if (dLeftRank_filt <= 10.0) {
				iLeftHitsAt10_filt = 1;
			}
			
			if (dLeftRank_raw <= 1.0) {
				iLeftHitsAt1_raw = 1;
			}
			if (dLeftRank_raw <= 3.0) {
				iLeftHitsAt3_raw = 1;
			}
			if (dLeftRank_raw <= 5.0) {
				iLeftHitsAt5_raw = 1;
			}
			if (dLeftRank_raw <= 10.0) {
				iLeftHitsAt10_raw = 1;
			}
			
			dTotalMeanRank_filt += dLeftRank_filt;
			dTotalMRR_filt += 1.0/(double)dLeftRank_filt;
			iTotalHits1_filt += iLeftHitsAt1_filt;
			iTotalHits3_filt += iLeftHitsAt3_filt;
			iTotalHits5_filt += iLeftHitsAt5_filt;
			iTotalHits10_filt += iLeftHitsAt10_filt;
			iFiltList.add(dLeftRank_filt);
			
			dTotalMeanRank_raw += dLeftRank_raw;
			dTotalMRR_raw += 1.0/(double)dLeftRank_raw;
			iTotalHits1_raw += iLeftHitsAt1_raw;
			iTotalHits3_raw += iLeftHitsAt3_raw;
			iTotalHits5_raw += iLeftHitsAt5_raw;
			iTotalHits10_raw += iLeftHitsAt10_raw;
			iRawList.add(dLeftRank_raw);
			iCnt++;
			
			int iRightRank_filt = 1;
			int iRightIdentical_filt = 0;
			int iRightRank_raw = 1;
			int iRightIdentical_raw = 0;
			for (int iRightID = 0; iRightID < iNumberOfEntities; iRightID++) {
				double dValue = 0.0;
				for (int p = 0; p < iNumberOfFactors; p++) {
					dValue += Real_MatrixE.get(iSubjectID, p) * Real_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iRightID, p)
							+ Imag_MatrixE.get(iSubjectID, p) * Real_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iRightID, p)
							+ Real_MatrixE.get(iSubjectID, p) * Imag_MatrixR.get(iRelationID, p) * Imag_MatrixE.get(iRightID, p) 
							- Imag_MatrixE.get(iSubjectID, p) * Imag_MatrixR.get(iRelationID, p) * Real_MatrixE.get(iRightID, p);
				}
				String negTiple = iSubjectID + "\t" + iRelationID + "\t" +iRightID;
				if(!lstTriples.containsKey(negTiple)){
					if (dValue > dTargetValue) {
						iRightRank_filt++;						
					}
					if (dValue == dTargetValue) {
						iRightIdentical_filt++;
					}					
				}
				if (dValue > dTargetValue) {
					iRightRank_raw++;						
				}
				if (dValue == dTargetValue) {
					iRightIdentical_raw++;
				}	
			}
			
			double dRightRank_filt = (double)(2.0 * iRightRank_filt + iRightIdentical_filt) / 2.0;
			double dRightRank_raw = (double)(2.0 * iRightRank_raw + iRightIdentical_raw - 1.0) / 2.0;
			int iRightHitsAt1_filt = 0,iRightHitsAt3_filt = 0,iRightHitsAt5_filt = 0,iRightHitsAt10_filt = 0;
			int iRightHitsAt1_raw = 0,iRightHitsAt3_raw = 0,iRightHitsAt5_raw= 0,iRightHitsAt10_raw = 0;
			if (dRightRank_filt <= 1.0) {
				iRightHitsAt1_filt = 1;
			}
			if (dRightRank_filt <= 3.0) {
				iRightHitsAt3_filt = 1;
			}
			if (dRightRank_filt <= 5.0) {
				iRightHitsAt5_filt = 1;
			}
			if (dRightRank_filt <= 10.0) {
				iRightHitsAt10_filt = 1;
			}
			
			if (dRightRank_raw <= 1.0) {
				iRightHitsAt1_raw = 1;
			}
			if (dRightRank_raw <= 3.0) {
				iRightHitsAt3_raw = 1;
			}
			if (dRightRank_raw <= 5.0) {
				iRightHitsAt5_raw = 1;
			}
			if (dRightRank_raw <= 10.0) {
				iRightHitsAt10_raw = 1;
			}
			
			dTotalMeanRank_filt += dRightRank_filt;
			dTotalMRR_filt += 1.0/(double)dRightRank_filt;
			iTotalHits1_filt += iRightHitsAt1_filt;
			iTotalHits3_filt += iRightHitsAt3_filt;
			iTotalHits5_filt += iRightHitsAt5_filt;
			iTotalHits10_filt += iRightHitsAt10_filt;
			iFiltList.add(dRightRank_filt);
			
			dTotalMeanRank_raw += dRightRank_raw;
			dTotalMRR_raw += 1.0/(double)dRightRank_raw;
			iTotalHits1_raw += iRightHitsAt1_raw;
			iTotalHits3_raw += iRightHitsAt3_raw;
			iTotalHits5_raw += iRightHitsAt5_raw;
			iTotalHits10_raw += iRightHitsAt10_raw;
			iRawList.add(dRightRank_raw);
			iCnt++;		
		}
		
		Collections.sort(iFiltList);
		int indx=iFiltList.size()/2;
		if (iFiltList.size()%2==0) {
			dMedian_filt = (iFiltList.get(indx-1)+iFiltList.get(indx))/2.0;
		}
		else {
			dMedian_filt = iFiltList.get(indx);
		}
		
		Collections.sort(iRawList);
		indx=iRawList.size()/2;
		if (iRawList.size()%2==0) {
			dMedian_raw = (iRawList.get(indx-1)+iRawList.get(indx))/2.0;
		}
		else {
			dMedian_raw = iRawList.get(indx);
		}
		
		System.out.println("Filt setting:");
		System.out.println("MeanRank: "+(dTotalMeanRank_filt / (double)iCnt) + "\n"  
				+ "MRR: "+(dTotalMRR_filt / (double)iCnt) + "\n" 
				+ "Median: " + dMedian_filt + "\n" 
				+ "Hit@1: "+((double)iTotalHits1_filt / (double)iCnt) + "\n" 
				+ "Hit@3: " + ((double)iTotalHits3_filt / (double)iCnt) + "\n" 
				+ "Hit@5: " +((double)iTotalHits5_filt / (double)iCnt)+ "\n"
				+ "Hit@10: " +((double)iTotalHits10_filt / (double)iCnt)+ "\n");
		
		System.out.println("Raw setting:");
		System.out.println("MeanRank: "+(dTotalMeanRank_raw / (double)iCnt) + "\n"  
				+ "MRR: "+(dTotalMRR_raw / (double)iCnt) + "\n" 
				+ "Median: " + dMedian_raw + "\n" 
				+ "Hit@1: "+((double)iTotalHits1_raw / (double)iCnt) + "\n" 
				+ "Hit@3: " + ((double)iTotalHits3_raw / (double)iCnt) + "\n" 
				+ "Hit@5: " +((double)iTotalHits5_raw / (double)iCnt)+ "\n"
				+ "Hit@10: " +((double)iTotalHits10_raw / (double)iCnt)+ "\n");
		reader.close();
	}
	
	public static void main(String[] args) throws Exception {
		int iEntities = 14951;// number of entities
		int iRelations = 1345;//number of relations
		int iFactors = 10; //embedding dimensionality
		String fnRealMatrixE = "MatrixE.real.best";
		String fnRealMatrixR = "MatrixR.real.best";
		String fnImagMatrixE = "MatrixE.imag.best";
		String fnImagMatrixR = "MatrixR.imag.best";
		String fnTrainTriples = "datasets\\FB15K\\train.txt";
		String fnValidTriples = "datasets\\FB15K\\valid.txt";
		String fnTestTriples = "datasets\\FB15K\\test.txt";
		
		Eval_LinkPrediction eval = new Eval_LinkPrediction(iEntities, iRelations, iFactors);
		eval.LPEvaluation(fnRealMatrixE, fnRealMatrixR, 
				fnImagMatrixE, fnImagMatrixR,
				fnTrainTriples, fnValidTriples, fnTestTriples);
	}
}
