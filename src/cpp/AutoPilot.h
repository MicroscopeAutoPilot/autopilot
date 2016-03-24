
// AutoPilot.H
// Header file for the C/C++ interface to the main AutoPilot functions.
//
// Please check the java file AutoPilotC.java for documentation of the underlying Java functions. 
//
// Author: Loic Royer


////*******************************************************************************************************
//  Library initialization and termination:

extern "C" __declspec(dllexport) unsigned long 	__cdecl begin(char* pJREFolderPath, char* AutoPilotJarPath);
extern "C" __declspec(dllexport) unsigned long 	__cdecl end();

////*******************************************************************************************************
//  Error handling:

extern "C" __declspec(dllexport) void 			__cdecl setLoggingOptions(bool pStdOut, bool pLogFile);

extern "C" __declspec(dllexport) void 			__cdecl clearError();
extern "C" __declspec(dllexport) char* 			__cdecl getLastJavaExceptionMessage();
extern "C" __declspec(dllexport) char* 			__cdecl getLastError();

////*******************************************************************************************************
//  Other:

extern "C" __declspec(dllexport) void 			__cdecl freePointer(void* pPointer);


////*******************************************************************************************************
//  Focus measures:

extern "C" __declspec(dllexport) double 		__cdecl dcts16bit(		short* pBuffer, 
																		int pWidth, 
																		int pHeight, 
																		double pPSFSupportDiameter);

extern "C" __declspec(dllexport) double 		__cdecl	tenengrad16bit(	short* pBuffer, 
																		int pWidth, 
																		int pHeight, 
																		double pPSFSupportDiameter);

extern "C" __declspec(dllexport) double 		__cdecl	isores16bit(	short* pBuffer, 
																		int pWidth, 
																		int pHeight, 
																		double pPSFSupportDiameter);

////*******************************************************************************************************
//  smart argmax function:

extern "C" __declspec(dllexport) int		 	__cdecl argmax(			int pNumberOfPoints,
																		double* pX,
																		double* pY,
																		double* pFittedY,
																		double* pResult);

////*******************************************************************************************************
//  Stack analysis:

extern "C" __declspec(dllexport) int			__cdecl	setParameter(	char* pString,
																		double pValue);

extern "C" __declspec(dllexport) int			__cdecl	newStack(	 	double* pZArray,
																		int pZArrayLength);

extern "C" __declspec(dllexport) double			__cdecl	loadPlane(		short* pBuffer, 
																		int pWidth, 
																		int pHeight);

extern "C" __declspec(dllexport) double			__cdecl	getResult(		double pMaxWaitTimeInSeconds,
																		char* pString,
																		double* pResultArray,
																		int pResultArrayLength);


////*******************************************************************************************************
//  Microscope state solvers:

extern "C" __declspec(dllexport) int		 	__cdecl l2solveSSP(		bool pAnchorDetection,
																		bool pSymmetricAnchor,
																		int pNumberOfWavelengths,
																		int pNumberOfPlanes,
																		int pSyncPlaneIndex,
																		double* pCurrentStateVector,
																		double* pObservationsVector,
																		bool* pMissingObservations,
																		double* pNewStateVector);
																		
extern "C" __declspec(dllexport) int		 	__cdecl l2solve(		bool pAnchorDetection,
																		bool pSymmetricAnchor,
																		int pNumberOfWavelengths,
																		int pNumberOfPlanes,
																		bool* pSyncPlaneIndices,
																		double* pCurrentStateVector,
																		double* pObservationsVector,
																		bool* pMissingObservations,
																		double* pNewStateVector);

extern "C" __declspec(dllexport) int		 	__cdecl qpsolve(		bool pAnchorDetection,
																		bool pSymmetricAnchor,
																		int pNumberOfWavelengths,
																		int pNumberOfPlanes,
																		bool* pSyncPlaneIndices,
																		double* pCurrentStateVector,
																		double* pObservationsVector,
																		bool* pMissingObservations,
																		double* pMaxCorrections,
																		double* pNewStateVector);

extern "C" __declspec(dllexport) int		 	__cdecl extrasolve(		int pNumberOfWavelengths,
																		int pNumberOfPlanes,
																		int pNumberOfDOFs,
																		double* pCurrentStateVector,
																		double* pObservationsVector,
																		bool* pMissingObservations,
																		double* pMaxCorrections,
																		double* pNewStateVector);



