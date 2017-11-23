package ruge.train;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import model.struct.Matrix;
import model.struct.Rule;
import model.struct.RuleSet;
import model.struct.Triple;
import model.struct.TripleSet;
import model.util.MetricMonitor;
import model.util.NegativeTripleGeneration;



public class RUGEModel {
	public TripleSet TrainingTriples;
	public TripleSet ValidateTriples;
	public TripleSet TestingTriples;
	public TripleSet Triples;
	public RuleSet GroundRules;
	public Matrix Entity_Real_MatrixE;
	public Matrix Relation_Real_MatrixR;
	public Matrix Entity_Imag_MatrixE;
	public Matrix Relation_Imag_MatrixR;
	public Matrix Real_MatrixEGradient;
	public Matrix Real_MatrixRGradient;
	public Matrix Imag_MatrixEGradient;
	public Matrix Imag_MatrixRGradient;
	
	public int NumRelation;
	public int NumEntity;
	public String MatrixE_prefix = "";
	public String MatrixR_prefix = "";
	
	public int NumFactor = 20;
	public int NumMiniBatch = 100;
	public int NumNeg = 10;
	public double Lambda = 0.01;//L2 regularization parameter
	public double GammaE = 0.01;
	public double GammaR = 1.;
	public double Weight = 1.;
	public double C = 0.01;//slacking parameter
	public int NumIteration = 1000;
	public int OutputIterSkip = 10;
	
	
	
	java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#.######");
	
	public RUGEModel() {
	}
	
	public void Initialization(String strNumRelation, String strNumEntity,
			String fnTrainingTriples, String fnValidateTriples, String fnTestingTriples,
			String fnGroundRules) throws Exception {
		NumRelation = Integer.parseInt(strNumRelation);
		NumEntity = Integer.parseInt(strNumEntity);
		MatrixE_prefix = "MatrixE";	
		MatrixR_prefix = "MatrixR";
		
		System.out.println("\nLoading training and validate triples");
		TrainingTriples = new TripleSet(NumEntity, NumRelation);
		ValidateTriples = new TripleSet(NumEntity, NumRelation);
		Triples = new TripleSet();
		TrainingTriples.load(fnTrainingTriples);
		ValidateTriples.subload(fnValidateTriples);
		Triples.loadStr(fnTrainingTriples);
		Triples.loadStr(fnValidateTriples);
		Triples.loadStr(fnTestingTriples);
		System.out.println("Success.");
		
		System.out.println("\nLoading grounding rules");
		GroundRules = new RuleSet(NumEntity, NumRelation);
		GroundRules.load(fnGroundRules);
		System.out.println("Success.");
		
		System.out.println("\nRandom initialize matrix E and matrix R w.r.t real and imaginary part");
		Entity_Real_MatrixE = new Matrix(NumEntity, NumFactor);
		Entity_Real_MatrixE.setToGaussian();
		
		Relation_Real_MatrixR = new Matrix(NumRelation, NumFactor);
		Relation_Real_MatrixR.setToGaussian();
		
		Entity_Imag_MatrixE = new Matrix(NumEntity, NumFactor);
		Entity_Imag_MatrixE.setToGaussian();
		
		Relation_Imag_MatrixR = new Matrix(NumRelation, NumFactor);
		Relation_Imag_MatrixR.setToGaussian();

		System.out.println("Success.");
		
		System.out.println("\nInitializing gradients of matrix E and matrix R");
		Real_MatrixEGradient = new Matrix(NumEntity, NumFactor);
		Real_MatrixRGradient = new Matrix(NumRelation, NumFactor);
		Imag_MatrixEGradient = new Matrix(NumEntity, NumFactor);
		Imag_MatrixRGradient = new Matrix(NumRelation, NumFactor);
		System.out.println("Success.");
	}
	
	public void Complex_Learn() throws Exception {
		HashMap<Integer, ArrayList<Triple>> lstPosTriples = new HashMap<Integer, ArrayList<Triple>>();
		HashMap<Integer, ArrayList<Triple>> lstHeadNegTriples = new HashMap<Integer, ArrayList<Triple>>();
		HashMap<Integer, ArrayList<Triple>> lstTailNegTriples = new HashMap<Integer, ArrayList<Triple>>();
		
		HashMap<Integer, ArrayList<Rule>> lstRules = new HashMap<Integer, ArrayList<Rule>>();
		
		String PATHLOG = "result.log";				
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(PATHLOG), "UTF-8"));
		
		
		int iIter = 0;
		writer.write("Complete iteration #" + iIter + ":\n");
		MetricMonitor first_metrics = new MetricMonitor(
				ValidateTriples,
				Triples.tripleSet(),
				Entity_Real_MatrixE,
				Relation_Real_MatrixR,
				Entity_Imag_MatrixE,
				Relation_Imag_MatrixR
				);
		first_metrics.calculateMetrics();
		double dCurrentHits = first_metrics.dHits;
		double dCurrentMRR = first_metrics.dMRR;
		writer.write("------Current MRR:"+ dCurrentMRR + "\tCurrent Hits@10:" + dCurrentHits + "\n");
		System.out.print("\n");
		double dBestHits = first_metrics.dHits;
		double dBestMRR = first_metrics.dMRR;
		int iBestIter = 0;
		
		long startTime = System.currentTimeMillis();
		while (iIter < NumIteration) {
			
			TrainingTriples.randomShuffle();
			for (int iIndex = 0; iIndex < TrainingTriples.triples(); iIndex++) {
				Triple PosTriple = TrainingTriples.get(iIndex);
				NegativeTripleGeneration negTripGen = new NegativeTripleGeneration(
						PosTriple, NumEntity, NumRelation);
				HashSet<Triple> headNegTripleSet = negTripGen.generateHeadNegTriple(NumNeg/2);
				HashSet<Triple> tailNegTripleSet = negTripGen.generateTailNegTriple(NumNeg/2);
				
				int iID = iIndex % NumMiniBatch;
				if (!lstPosTriples.containsKey(iID)) {
					ArrayList<Triple> tmpPosLst = new ArrayList<Triple>();
					ArrayList<Triple> tmpHeadNegLst = new ArrayList<Triple>();
					ArrayList<Triple> tmpTailNegLst = new ArrayList<Triple>();
					tmpPosLst.add(PosTriple);
					tmpHeadNegLst.addAll(headNegTripleSet);
					tmpTailNegLst.addAll(tailNegTripleSet);
					lstPosTriples.put(iID, tmpPosLst);
					lstHeadNegTriples.put(iID, tmpHeadNegLst);
					lstTailNegTriples.put(iID, tmpTailNegLst);
				} else {
					lstPosTriples.get(iID).add(PosTriple);
					lstHeadNegTriples.get(iID).addAll(headNegTripleSet);
					lstTailNegTriples.get(iID).addAll(tailNegTripleSet);
				}
			}
			
			GroundRules.randomShuffle();
			for (int iIndex = 0; iIndex < GroundRules.rules(); iIndex++) {
				Rule rule = GroundRules.get(iIndex);				
				int iID = iIndex % NumMiniBatch;
				if (!lstRules.containsKey(iID)) {
					ArrayList<Rule> tmpLst = new ArrayList<Rule>();
					tmpLst.add(rule);
					lstRules.put(iID, tmpLst);
					
				} else {
					lstRules.get(iID).add(rule);
				}
			}
			
			for (int iID = 0; iID < NumMiniBatch; iID++) {
				StochasticUpdate stochasticUpdate = new StochasticUpdate(
						lstPosTriples.get(iID),
						lstHeadNegTriples.get(iID),
						lstTailNegTriples.get(iID),
						lstRules.get(iID),
						Entity_Real_MatrixE,
						Relation_Real_MatrixR,
						Entity_Imag_MatrixE,
						Relation_Imag_MatrixR,
						Real_MatrixEGradient,
						Real_MatrixRGradient,
						Imag_MatrixEGradient,
						Imag_MatrixRGradient,
						GammaE ,
						GammaR ,						
						Lambda,
						Weight,
						C
						);
				stochasticUpdate.stochasticIteration(iID);
			}
			lstPosTriples = new HashMap<Integer, ArrayList<Triple>>();
			lstHeadNegTriples = new HashMap<Integer, ArrayList<Triple>>();
			lstTailNegTriples = new HashMap<Integer, ArrayList<Triple>>();
			lstRules = new HashMap<Integer, ArrayList<Rule>>();
			
			iIter++;
			System.out.println("Complete iteration #" + iIter);
		
			if (iIter % OutputIterSkip == 0) {
				System.out.println("Complete iteration #" + iIter + ":");
				writer.write("Complete iteration #" + iIter + ":\n");
				MetricMonitor metric = new MetricMonitor(
						ValidateTriples,
						Triples.tripleSet(),
						Entity_Real_MatrixE,
						Relation_Real_MatrixR,
						Entity_Imag_MatrixE,
						Relation_Imag_MatrixR
						);
				metric.calculateMetrics();
				dCurrentHits = metric.dHits;
				dCurrentMRR = metric.dMRR;
				writer.write("------Current MRR:"+ dCurrentMRR + "\tCurrent Hits@10:" + dCurrentHits + "\n");
				 if (dCurrentMRR > dBestMRR) {
					Entity_Real_MatrixE.output(MatrixE_prefix + ".real.best");
					Relation_Real_MatrixR.output(MatrixR_prefix + ".real.best");
					Entity_Imag_MatrixE.output(MatrixE_prefix + ".imag.best");
					Relation_Imag_MatrixR.output(MatrixR_prefix + ".imag.best");
					
					dBestHits = dCurrentHits;
					dBestMRR = dCurrentMRR;
					iBestIter = iIter;
				}
				writer.write("------Best iteration #" + iBestIter + "\t" + "MRR:" + dBestMRR + "\t" + "Hits@10:" + dBestHits+"\n");
				writer.flush();
				System.out.println("------Best iteration #" + iBestIter + "\t" + "MRR:" + dBestMRR + "\t" + "Hits@10:" + dBestHits);

			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("All running time:" + (endTime-startTime)+"ms");
		writer.close();
	}
}
