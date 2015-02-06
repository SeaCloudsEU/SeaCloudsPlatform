package eu.seaclouds.platform.planner.optimizer.heuristics;


import java.util.Map;

import test.interfaces.YAMLmanager;

public abstract class AbstractHeuristic implements Heuristic {

	private int MAX_ITER_NO_IMPROVE = 200;
	
	
	public AbstractHeuristic(int maxIter){
		MAX_ITER_NO_IMPROVE=maxIter;
		
	}
	
	public AbstractHeuristic(){	}
	
	/*@Override
	public abstract void computeOptimalSolution(SuitableOptions cloudOffers,
			Map<String, Object> applicationMap); 
	*/
	
	public void setMaxIterNoImprove(int value){
		MAX_ITER_NO_IMPROVE=value;
	}
	
	public int getMaxIterNoImprove(){
		return MAX_ITER_NO_IMPROVE;
	}

	public double fitness(Solution bestSol,Map<String, Object> applicationMap) {
		
		return 1.0;
		
		QualityReqirements requirements = YAMLmanager.getQualityRequirements(applicationMap);
		
		double perfGoodness= requirements.getPerfRequirement(applicationMap) / qualityAnalyzer.getPerformance(bestSol).getResponseTime() ;
		double availGoodness= (1.0 - requirements.getAvailRequirement(applicationMap)) / (1.0 - qualityAnalyzer.getAvailability(bestSol));
		double costGoodness= requirements.getCostRequirement(applicationMap) / qualityAnalyzer.getCost(bestSol);
		return perfGoodness + availGoodness + costGoodness;
	}

	
	public void addSolutionToAppMap(Solution currentSol,Map<String, Object> applicationMap) {
		
		for(String solkey :  currentSol){
			
			YAMLmanager.CleanSuitableOfferForModule(solkey, applicationMap);
			YAMLmanager.AddSuitableOfferForModule(solkey, currentSol.getItem(solkey),applicationMap);
		}
		
	}
	
	
	
}
