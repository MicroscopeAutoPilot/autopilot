// Test.cpp : Defines the entry point for the console application.
//
#include <windows.h>
#include <tchar.h>
#include <iostream>
#include <AutoPilot.h>

using namespace std;


int _tmain(int argc, _TCHAR* argv[])
{
	int lReturnValue = 0;

	cout << "TEST BEGIN\n";
	long lReturnCode = begin(NULL, ".\\AutoPilot.jar");
	if(lReturnCode!=0) 
	{
		cout << "Begin failed, return code=" << lReturnCode;
		return 1;
	}

	int width = 1024;
	int height = 512;
	short* buffer = new short[width*height];
	
	for(int y=0; y<height; y++)
		for(int x=0; x<width; x++)
			buffer[x+width*y] = x+y;
	

	 // repeat everything a few times
	for(int r=0; r<1; r++)
	{
		double dctsvalue = dcts16bit(buffer,width,height,2);
		cout << "dcts=" << dctsvalue << "\n";
		cout << "error=" << getLastError() << "\n";

		double tenengradvalue = tenengrad16bit(buffer,width,height,2);
		cout << "tenengrad=" << tenengradvalue << "\n";
		cout << "error=" << getLastError() << "\n";

		double isoresvalue = isores16bit(buffer,width,height,2);
		cout << "isores=" << isoresvalue << "\n";
		cout << "error=" << getLastError() << "\n";


		// This block shows how to use the stack analysis features to find dx, alpha and beta:
		{

			// Below are the available parameters,
			// Most don't need to be set since they have good default values. 
			// Boolean parameters (flags) use the 0=false 1=true standard.
			// If flags/parameters need to be set only once but can be changed 
			// at any point in time. 

			// (a) Which calculation to perform:
			setParameter("computedz",1); //this is already the default
			setParameter("computealpha",1); //this is already the default
			setParameter("computebeta",1); //this is already the default

			// Which focus measure to use: default=dcts fast=tenengrad 
			setParameter("fastmeasure",0); //this is already the default

			// Stack orientation - IMPORTANT TO ADJUST IF IMAGE IS NOT IN STD ORIENTATION!
			// 0->std, 1->rotates image cw by 90deg, 2->rotates image ccw by 90
			setParameter("orientation",0);  //this is already the default

			// Argmax fit probability threshold:
			setParameter("fitprob",0.98);  //this is already the default

			// Angle determination parameters:
			setParameter("mintilestride[px]",8); //this is already the default
			setParameter("maxtiles",500); //this is already the default
			setParameter("psfsupportdiameter[px]",3); //this is already the default
			setParameter("minratioinliers",3); //this is already the default
			setParameter("mindatapoints",30); //this is already the default
			setParameter("minextent[um]",80); //this is already the default

			// Debug options:
			// opens up a 3D visualization of the plane
			setParameter("visualize",1); //this is already the default
			// Logs are sent to the std output
			setParameter("logconsole",0); //this is already the default
			// Logs are sent to a log window
			setParameter("logwindow",0); //this is already the default

			// This array defines the Z steping in the focus stack:
			double lDefocusZInMicrons[7] = { -3 * 1.33, -2 * 1.33, -1.33, 0, 1.33, 2 * 1.33, 3 * 1.33 };
			// length of array above...
			int lDefocusZLength = 7;

			// (Re)Initializes the stack analyser for a new stack.
			newStack(lDefocusZInMicrons,lDefocusZLength);

			// Load each plane in the same order as defined in the Z values array given previously.
			// Calculations are started asynchronously and in a multithreaded fashion as soon as the
			// first plane is loaded. The more intense computational load is started once all planes are
			// loaded.  
			for(int i=0; i<lDefocusZLength; i++)
				loadPlane(buffer,width,height);

			double lMaxWaitTimeInSeconds = 2.1; // example
			double* lResult = new double[3];

			// Non requested calculation - see (a) - will have a value of NaN.
			// NaN values are also returned if dz or angles cannot be computed.
			// This call will wait for at most lMaxWaitTimeInSeconds for the computation to finish.
			getResult(lMaxWaitTimeInSeconds,"{dz,alpha,beta}",lResult,3); 
			
			// Focus or angles don't make much sense for a a stack of identical planes...
			cout << "dz=" 		<< lResult[0] << "\n";
			cout << "alpha=" 	<< lResult[1] << "\n";
			cout << "beta=" 	<< lResult[2] << "\n";

		}


		{	
			int lStateVectorLength = 2+4*2;
			int lObservationVectorLength = 4+2*3;

			double* oldstate = new double[lStateVectorLength];
			double* observations = new double[lObservationVectorLength];
			bool* missing = new bool[lObservationVectorLength];
			double* newstate = new double[lStateVectorLength];
			
			for(int i=0; i<lStateVectorLength; i++) 
				oldstate[i]=0.0;
			oldstate[lStateVectorLength-1]=13;

			cout << "Old State Vector:\n";
			for(int i=0; i<lStateVectorLength; i++) 
				cout << oldstate[i] << "\n";
			cout << "Old State Vector (end)\n";

			for(int i=0; i<lObservationVectorLength; i++) 
				observations[i]=0.0;
			for(int i=0; i<lObservationVectorLength; i++) 
				missing[i]=false;

			observations[0] = 1;
			observations[1] = 1;	

			cout << "Observations Vector:\n";
			for(int i=0; i<lObservationVectorLength; i++) 
				cout << observations[i] << "\n";
			cout << "Observations Vector (end)\n";

			cout << "\ncalling l2solve(...)\n";
			lReturnValue = l2solveSSP(false,true,1,1,0,oldstate,observations,missing,newstate);
		    cout << "lReturnValue=" << lReturnValue << "\n";
			cout << "error=" << getLastError() << "\n";

			cout << "New State Vector:\n";
			for(int i=0; i<lStateVectorLength; i++) 
				cout << newstate[i] << "\n";
			cout << "New State Vector (end)\n";
			
			
			cout << "\ncalling l2solve(...) with sync plane indices array\n";
			bool* sync = new bool[1*1];
			lReturnValue = l2solve(false,true,1,1,sync,oldstate,observations,missing,newstate);
		    cout << "lReturnValue=" << lReturnValue << "\n";
			cout << "error=" << getLastError() << "\n";

			cout << "New State Vector:\n";
			for(int i=0; i<lStateVectorLength; i++) 
				cout << newstate[i] << "\n";
			cout << "New State Vector (end)\n";


			double* maxcorrections = new double[lObservationVectorLength];
		    maxcorrections[0]=0.25;
			maxcorrections[0]=0.25;
			maxcorrections[0]=0.5;
			maxcorrections[0]=0.5;

			cout << "\ncalling qpsolve(...) \n";
			lReturnValue = qpsolve(false,true,1,1,sync,oldstate,observations,missing,maxcorrections,newstate);
		    cout << "lReturnValue=" << lReturnValue << "\n";
			cout << "error=" << getLastError() << "\n";

			cout << "New State Vector:\n";
			for(int i=0; i<lStateVectorLength; i++) 
				cout << newstate[i] << "\n";
			cout << "New State Vector (end)\n";
		}

		{
			int lNumberOfWavelengths = 3;
			int lNumberOfPlanes = 2;
			int lNumberOfDOfs = 1;
			int lNumberOfVariables = lNumberOfWavelengths * lNumberOfPlanes * lNumberOfDOfs;

			double* lCurrentStateVector = new double[lNumberOfVariables];
			double* lObservationsVector = new double[lNumberOfVariables];
			bool* lMissingObservations = new bool[lNumberOfVariables];
			double* lMaximalCorrections = new double[lNumberOfVariables];
			double* lNewStateVector = new double[lNumberOfVariables];

			for (int i = 0; i < lNumberOfVariables; i++)
			{
				lCurrentStateVector[i] 	= 0;
				lObservationsVector[i] 	= 0;
				lMissingObservations[i] = false;
				lMaximalCorrections[i] 	= 0;
				lNewStateVector[i] 		= 0;
			}

			lObservationsVector[0] = +1.1;
			lObservationsVector[1] = +2.5;
			lObservationsVector[lNumberOfPlanes] = +0;
			lObservationsVector[2 * lNumberOfPlanes] = 0.5;
			for (int i = 0; i < lNumberOfPlanes; i++)
				lMissingObservations[lNumberOfPlanes + i] = true;/**/
			lMissingObservations[lNumberOfVariables - 1] = true;
			for (int i = 0; i < lNumberOfVariables; i++)
				lMaximalCorrections[i] = 2;

			cout << "Observations Vector:\n";
			for(int i=0; i<lNumberOfVariables; i++) 
				cout << lObservationsVector[i] << "\n";
			cout << "Observations Vector (end)\n";

			cout << "Missing observations Vector:\n";
			for(int i=0; i<lNumberOfVariables; i++) 
				if(lMissingObservations[i])
					cout << "true" << "\n";
				else
					cout << "false" << "\n";
			cout << "Missing observations Vector (end)\n";

			cout << "Maximal corrections Vector:\n";
			for(int i=0; i<lNumberOfVariables; i++) 
				cout << lMaximalCorrections[i] << "\n";
			cout << "Maximal corrections (end)\n";

			

			cout << "\ncalling extrasolve(...)\n";
			lReturnValue = extrasolve(		lNumberOfWavelengths,
											lNumberOfPlanes,
											lNumberOfDOfs,
											lCurrentStateVector,
											lObservationsVector,
											lMissingObservations,
											lMaximalCorrections,
											lNewStateVector);
			cout << "lReturnValue=" << lReturnValue << "\n";

			cout << "New State Vector:\n";
			for(int i=0; i<lNumberOfVariables; i++) 
				cout << lNewStateVector[i] << "\n";
			cout << "New State Vector (end)\n";


		}



		{
			double* lResult = new double[2];
			double lX[7] = { -3, -2, -1, 0, 1, 2, 3 };
			double lY[7] = { 0.21, 0.31, 0.41, 0.43, 0.39, 0.29, 0.19 };

			double* lFittedY = new double[7];

			cout << "\ncalling argmax(...)\n";
			int lReturnCode = argmax(7,
									lX,
									lY,
									lFittedY,
									lResult);
			cout << "lReturnValue=" << lReturnValue << "\n";
			cout << "error=" << getLastError() << "\n";

			double lArgMax = lResult[0];
			double lFitprobability = lResult[1];

			cout << "lArgMax=" << lArgMax << "\n";
			cout << "lFitprobability=" <<  lFitprobability << "\n";

			cout << "lX:\n";
			for (int i = 0; i < 7; i++)
				cout << lX[i] << "\n";
			cout << "lX: (end)\n";

			cout << "lY:\n";
			for (int i = 0; i < 7; i++)
				cout << lY[i] << "\n";
			cout << "lY: (end)\n";

			cout << "lFittedY:\n";
			for (int i = 0; i < 7; i++)
				cout << lFittedY[i] << "\n";
			cout << "lFittedY: (end)\n";
		}

		{
			double* lResult = new double[2];
			double lX[7] = { -3, -2, -1, 0, 1, 2, 3 };
			double lY[7] = { 0.1, 0.4, 0.45, 0.3, 0.1, 0.4, 0.3 };

			double* lFittedY = new double[7];

			cout << "\ncalling argmax(...)\n";
			int lReturnCode = argmax(7,
									lX,
									lY,
									lFittedY,
									lResult);
			cout << "lReturnValue=" << lReturnValue << "\n";
			cout << "error=" << getLastError() << "\n";

			double lArgMax = lResult[0];
			double lFitprobability = lResult[1];

			cout << "lArgMax=" << lArgMax << "\n";
			cout << "lFitprobability=" <<  lFitprobability << "\n";

			cout << "lX:\n";
			for (int i = 0; i < 7; i++)
				cout << lX[i] << "\n";
			cout << "lX: (end)\n";

			cout << "lY:\n";
			for (int i = 0; i < 7; i++)
				cout << lY[i] << "\n";
			cout << "lY: (end)\n";

			cout << "lFittedY:\n";
			for (int i = 0; i < 7; i++)
				cout << lFittedY[i] << "\n";
			cout << "lFittedY: (end)\n";
		}

	
	}

	end();
	cout << "TEST END\n";
	return 0;
}


