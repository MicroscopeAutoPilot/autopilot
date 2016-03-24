package autopilot.fmatrix.solvers.qp.test;

import org.junit.Test;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;

public class TestJOptimizerQPSolver
{

	@Test
	public void testWebsiteExample() throws Exception
	{
		// Objective function
		double[][] P = new double[][]
		{
		{ 1., 0.4 },
		{ 0.4, 1. } };
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P,
																																																		null,
																																																		0);

		// equalities
		double[][] A = new double[][]
		{
		{ 1, 1 } };
		double[] b = new double[]
		{ 1 };

		// inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
		inequalities[0] = new LinearMultivariateRealFunction(new double[]
		{ -1, 0 }, 0);
		inequalities[1] = new LinearMultivariateRealFunction(new double[]
		{ 0, -1 }, 0);

		// optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[]
		{ 0.1, 0.9 });
		// or.setFi(inequalities); //if you want x>0 and y>0
		or.setA(A);
		or.setB(b);
		or.setToleranceFeas(1.E-12);
		or.setTolerance(1.E-12);

		// optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		// System.out.println("returnCode=" + returnCode);

		double[] sol = opt.getOptimizationResponse().getSolution();

		// System.out.println("solution:" + Arrays.toString(sol));

	}

}
