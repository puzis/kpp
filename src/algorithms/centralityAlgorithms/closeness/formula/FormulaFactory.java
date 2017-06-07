package algorithms.centralityAlgorithms.closeness.formula;

import common.Pair;

import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

public class FormulaFactory {
	
	public static IClosenessFormula createFormula(FormulaType type, double[][] dists, Object param)
	{
		switch (type) 
		{
		case FORMULA_TYPE_STANDARD: return new ClosenessStandard(dists);
		case FORMULA_TYPE_EXPONENTIAL: return new ClosenessExponential(dists, (Double)param);
		case FORMULA_TYPE_RECIPROCAL: return new ClosenessReciprocal(dists);
		case FORMULA_TYPE_WEIGHTED: return new ClosenessTM((AbsTrafficMatrix)param, dists);
		case FORMULA_TYPE_IV: 
			{
				double[] ivObj = (double[]) param;
				double[] iv = new double[ivObj.length];
				for (int i=0; i<iv.length; i++){
					iv[i] = ((Double)ivObj[i]).doubleValue();
				}
				return new ClosenessIVFrom(dists,iv);
			}
		case FORMULA_TYPE_FOOTPRINT: 
			{
				Pair<Boolean, Pair<AbsTrafficMatrix, AbsTrafficMatrix>> tmParam = (Pair<Boolean, Pair<AbsTrafficMatrix, AbsTrafficMatrix>>)param;
				Pair<AbsTrafficMatrix, AbsTrafficMatrix> tmsPair = tmParam.getValue2();
				return new Footprint(dists, tmsPair.getValue1(), tmsPair.getValue2(), tmParam.getValue1());
			}
		case FORMULA_TYPE_MONITOR_FOOTPRINT:
			{
				Pair<Boolean, Pair<AbsTrafficMatrix, AbsTrafficMatrix>> tmParam = (Pair<Boolean, Pair<AbsTrafficMatrix, AbsTrafficMatrix>>)param;
				Pair<AbsTrafficMatrix, AbsTrafficMatrix> tmsPair = tmParam.getValue2();
				return new MonitoringWithDiversionFootprint(dists, tmsPair.getValue1(), tmsPair.getValue2(), tmParam.getValue1());
			}
		}
		// esle the default is ClosenessReciprocal
		return new ClosenessReciprocal(dists);	
	}
}
