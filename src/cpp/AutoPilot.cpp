/**
 * AutoPilot.cpp 
 * 
 * Defines the exported functions for the AutoPilot native binding library.
 * Detailed documentation of the underlying Java functions can be found in AutoPilotC.java
 *
 * Author: Loic Royer (2014)
 *
 */

#include <AutoPilot.h>
#define WIN32_LEAN_AND_MEAN             // Exclude rarely-used stuff from Windows headers
#include <windows.h>
#include <jni.h>       					/* where everything is defined */
#include <iostream>
#include <string.h>

using namespace std;


#define cErrorNone "No Error"

////*******************************************************************************************************
//  Utils:

const char * getEnvWin(const char * name)
{
    const DWORD buffSize = 65535;
    static char buffer[buffSize];
    if (GetEnvironmentVariableA(name, buffer, buffSize))
    {
        return buffer;
    }
    else
    {
        return 0;
    }
}

////*******************************************************************************************************
//  JVM globals:

typedef jint (JNICALL *CreateJavaVM)(JavaVM **pvm, void **penv, void *args);
JavaVM *sJVM; /* denotes a Java VM */
JavaVMInitArgs sJVMArgs; /* JDK/JRE 6 VM initialization arguments */
char* sJavaLastError = cErrorNone;

////*******************************************************************************************************
//  Class and method globals:

jclass sAutoPilotClass;
jmethodID 	getLastExceptionMessageID, 
			setLoggingOptionsID, 
			dcts16bitID, 
			tenengrad16bitID,
			isores16bitID,
			argmaxID,
			setParameterID,
			newStackID,
			loadPlaneID,
			getResultID,
			l2solveIDSSP, 
			l2solveID, 
			qpsolveID, 
			extrasolveID;


////*******************************************************************************************************
//  Library initialization and termination:

__declspec(dllexport) unsigned long __cdecl begin(char* pJREFolderPath, char* AutoPilotJarPath)
{
	try
	{
		clearError();

		const char* JAVAHOME  = getEnvWin("JAVA_HOME");
		char JREFolderPath[1024];
		if(pJREFolderPath==NULL)
		{
			
			strcpy(JREFolderPath,JAVAHOME);
			strcat(JREFolderPath,"\\bin\\server\\jvm.dll");
		}
		else
		{
			strcpy(JREFolderPath,pJREFolderPath);
		}
		cout << "JREFolderPath=" << JREFolderPath << "\n";

		// First attempt assuming we are given a JRE path:
		HINSTANCE lDLLInstance = LoadLibraryA(JREFolderPath);

		// Seconds attempt assuming we are given a JDK path:
		if( lDLLInstance == 0)
		{
			strcpy(JREFolderPath,JAVAHOME);
			strcat(JREFolderPath,"\\jre\\bin\\server\\jvm.dll");
		 	lDLLInstance = LoadLibraryA(JREFolderPath);
		}

		if( lDLLInstance == 0)
		{
			sJavaLastError = "Cannot load Jvm.dll (wrong path given, should be jre folder inside of autopilot folder)";
			return 1;
		}
		CreateJavaVM lCreateJavaVM = (CreateJavaVM)GetProcAddress(lDLLInstance, "JNI_CreateJavaVM");
		if (lCreateJavaVM == NULL )
		{
			sJavaLastError = "Cannot load Jvm.dll (wrong path given)";
			return 2;
		}

		size_t lJREFolderPathLength= strlen(JREFolderPath);

		char lClassPathPrefix[] = "-Djava.class.path=";
		size_t lClassPathPrefixLength= strlen(lClassPathPrefix);
		
		char lClassPathString[1024];

		strcpy(lClassPathString,lClassPathPrefix);
		strcat(lClassPathString,AutoPilotJarPath);

		JavaVMOption options[3];
		options[0].optionString = "-Xmx4G";
		options[1].optionString = lClassPathString;
		options[2].optionString = "-verbose";
		sJVMArgs.version = JNI_VERSION_1_6;
		sJVMArgs.nOptions = 2;
		sJVMArgs.options = options;
		sJVMArgs.ignoreUnrecognized = false;

		JNIEnv *lJNIEnv;
		jint res = lCreateJavaVM(&sJVM, (void **)&lJNIEnv, &sJVMArgs);
		if (res < 0)
		{
			return 3;
		}

		sAutoPilotClass = lJNIEnv->FindClass("autopilot/interfaces/AutoPilotC");

		if (sAutoPilotClass == 0)
		{
			return 4;
		}

		// method Ids for error handling:
		getLastExceptionMessageID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "getLastExceptionMessage", "()Ljava/lang/String;");
		setLoggingOptionsID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "setLoggingOptions", "(ZZ)V");
		
		// method Ids for focus measures:
		dcts16bitID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "dcts16bit", "(Ljava/nio/ByteBuffer;IID)D");
		tenengrad16bitID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "tenengrad16bit", "(Ljava/nio/ByteBuffer;IID)D");
		isores16bitID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "isores16bit", "(Ljava/nio/ByteBuffer;IID)D");

		// method Ids for smart argmax:
		argmaxID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "argmax", "([D[D[D[D)I");

		// method Ids for stack analysis:
		setParameterID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "setParameter", "(Ljava/lang/String;D)I");
		newStackID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "newStack", "([D)I");
		loadPlaneID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "loadPlane", "(Ljava/nio/ByteBuffer;II)I");
		getResultID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "getResult", "(DLjava/lang/String;[D)I");
		
		// method Ids for solvers:
		l2solveIDSSP = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "l2solve", "(ZZIII[D[D[Z[D)I");
		l2solveID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "l2solve", "(ZZII[Z[D[D[Z[D)I");
		qpsolveID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "qpsolve", "(ZZII[Z[D[D[Z[D[D)I");
		extrasolveID = lJNIEnv->GetStaticMethodID(sAutoPilotClass, "extrasolve", "(III[D[D[Z[D[D)I");
		

		if (getLastExceptionMessageID == 0)
			return 101;
		if(setLoggingOptionsID == 0)
			return 102;

		if(dcts16bitID == 0)
			return 111;
		if(tenengrad16bitID == 0)
			return 112;
		if(isores16bitID == 0)
			return 113;

		if(argmaxID == 0)
			return 121;

		if(setParameterID == 0)
			return 131;
		if(newStackID == 0)
			return 132;
		if(loadPlaneID == 0)
			return 133;
		if(getResultID == 0)
			return 134;

		if(l2solveIDSSP == 0)
			return 141;
		if(l2solveID == 0)
			return 142;
		if(qpsolveID == 0)
			return 143;
		if(extrasolveID == 0)
			return 144;


		return 0;
	}
	catch(...)
	{
		sJavaLastError = "Error while creating Java JVM";
		return 100;
	}
}

__declspec(dllexport) unsigned long __cdecl end()
{
	try
	{
		clearError();
		// This hangs the system for no good reason:
		//sJVM->DetachCurrentThread();
		//sJVM->DestroyJavaVM();
		return 0;
	}
	catch(...)
	{
		sJavaLastError = "Error while destroying Java JVM";
		return 1;
	}
}

////*******************************************************************************************************
//  Error handling:

__declspec(dllexport)  void __cdecl setLoggingOptions(bool pStdOut, bool pLogFile)
{
	clearError();
	JNIEnv *lJNIEnv;
	sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);
	lJNIEnv->CallStaticIntMethod(sAutoPilotClass,setLoggingOptionsID, pStdOut, pLogFile);
}

__declspec(dllexport)  void __cdecl clearError()
{
	sJavaLastError=cErrorNone;
}



jstring sLastJavaExceptionMessageJString = NULL;
char* sLastJavaExceptionMessage = NULL;

__declspec(dllexport) char* __cdecl getLastJavaExceptionMessage()
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);

		if(sLastJavaExceptionMessageJString!=NULL && sLastJavaExceptionMessage != NULL)
		{
			lJNIEnv->ReleaseStringUTFChars(sLastJavaExceptionMessageJString, sLastJavaExceptionMessage);
		}

		sLastJavaExceptionMessageJString = (jstring)lJNIEnv->CallStaticObjectMethod(sAutoPilotClass,getLastExceptionMessageID);
		sLastJavaExceptionMessage = NULL;

		if(sLastJavaExceptionMessageJString!=NULL)
		{
			sLastJavaExceptionMessage = (char*)lJNIEnv->GetStringUTFChars(sLastJavaExceptionMessageJString, NULL);
		}
		return sLastJavaExceptionMessage;
	}
	catch (...)
	{
		return "Error while obtaining the Java exception string";
	}
}

__declspec(dllexport) char* __cdecl getLastError()
{
	char* lLastJavaExceptionMessage = getLastJavaExceptionMessage();
	if(lLastJavaExceptionMessage!=NULL) return lLastJavaExceptionMessage;
	else return sJavaLastError;
}

////*******************************************************************************************************
//  Focus measures:

__declspec(dllexport) double __cdecl dcts16bit(short* pBuffer, int pWidth, int pHeight, double pPSFSupportDiameter)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);
		jobject lByteBuffer = lJNIEnv->NewDirectByteBuffer(pBuffer,pWidth*pHeight*2);
		double dcts = lJNIEnv->CallStaticDoubleMethod(sAutoPilotClass,dcts16bitID,lByteBuffer, pWidth, pHeight, pPSFSupportDiameter);
		lJNIEnv->DeleteLocalRef(lByteBuffer);
		return dcts;
	}
	catch (...)
	{
		sJavaLastError = "Error while computing dcts focus measure";
		return -1;
	}
}

__declspec(dllexport) double __cdecl tenengrad16bit(short* pBuffer, int pWidth, int pHeight, double pPSFSupportDiameter)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);
		jobject lByteBuffer = lJNIEnv->NewDirectByteBuffer(pBuffer,pWidth*pHeight*2);
		double tenengrad = lJNIEnv->CallStaticDoubleMethod(sAutoPilotClass,tenengrad16bitID, lByteBuffer, pWidth, pHeight, pPSFSupportDiameter);
		lJNIEnv->DeleteLocalRef(lByteBuffer);
		return tenengrad;
	}
	catch (...)
	{
		sJavaLastError = "Error while computing tenengrad focus measure";
		return -1;
	}
}

__declspec(dllexport) double __cdecl isores16bit(short* pBuffer, int pWidth, int pHeight, double pPSFSupportDiameter)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);
		jobject lByteBuffer = lJNIEnv->NewDirectByteBuffer(pBuffer,pWidth*pHeight*2);
		double isores = lJNIEnv->CallStaticDoubleMethod(sAutoPilotClass,isores16bitID, lByteBuffer, pWidth, pHeight, pPSFSupportDiameter);
		lJNIEnv->DeleteLocalRef(lByteBuffer);
		return isores;
	}
	catch (...)
	{
		sJavaLastError = "Error while computing tenengrad focus measure";
		return -1;
	}
}

////*******************************************************************************************************
//  smart argmax function:

_declspec(dllexport) int __cdecl argmax(
		int pNumberOfPoints,
		double* pX,
		double* pY,
		double* pFittedY,
		double* pResult)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);

		jdoubleArray lX = lJNIEnv->NewDoubleArray(pNumberOfPoints);
		lJNIEnv->SetDoubleArrayRegion(lX, 0, pNumberOfPoints, (jdouble*)pX);

		jdoubleArray lY = lJNIEnv->NewDoubleArray(pNumberOfPoints);
		lJNIEnv->SetDoubleArrayRegion(lY, 0, pNumberOfPoints, (jdouble*)pY);

		jdoubleArray lFittedY = lJNIEnv->NewDoubleArray(pNumberOfPoints);
		lJNIEnv->SetDoubleArrayRegion(lFittedY, 0, pNumberOfPoints, (jdouble*)pFittedY);

		pResult[0] = NAN;
		pResult[1] = NAN;		
		jdoubleArray lResult = lJNIEnv->NewDoubleArray(2);
		lJNIEnv->SetDoubleArrayRegion(lResult, 0, 2, (jdouble*)pResult);

		int lReturnValue = lJNIEnv->CallStaticIntMethod(sAutoPilotClass,
														argmaxID, 
														lX,
														lY,
														lFittedY,
														lResult);

		jdouble* lFittedYArray = lJNIEnv->GetDoubleArrayElements(lFittedY, NULL);
		for (int i=0; i<pNumberOfPoints; i++)
			pFittedY[i] = lFittedYArray[i];
		lJNIEnv->ReleaseDoubleArrayElements(lFittedY, lFittedYArray, NULL);

		jdouble* lResultArray = lJNIEnv->GetDoubleArrayElements(lResult, NULL);
		for (int i=0; i<2; i++)
			pResult[i] = lResultArray[i];
		lJNIEnv->ReleaseDoubleArrayElements(lResult, lResultArray, NULL);

		lJNIEnv->DeleteLocalRef(lX);
		lJNIEnv->DeleteLocalRef(lY);
		lJNIEnv->DeleteLocalRef(lFittedY);
		lJNIEnv->DeleteLocalRef(lResult);

		return lReturnValue;
	}
	catch (...)
	{
		sJavaLastError = "Error while running argmax";
		return -2;
	}
}

////*******************************************************************************************************
//  Stack analysis:


__declspec(dllexport) int			__cdecl	setParameter(	char* pString,
															double pValue)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);

		jstring lParameterName = lJNIEnv->NewStringUTF(pString);

		int lReturnValue = lJNIEnv->CallStaticIntMethod(
			sAutoPilotClass,
			setParameterID,
			lParameterName, 
			pValue
			);

		lJNIEnv->DeleteLocalRef(lParameterName);

		return lReturnValue;
	}
	catch (...)
	{
		sJavaLastError = "Error while running setParameter";
		return -1;
	}
}

__declspec(dllexport) int			__cdecl	newStack(	 	double* pZArray,
															int pZArrayLength)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);

		jdoubleArray lZJArray = lJNIEnv->NewDoubleArray(pZArrayLength);
		lJNIEnv->SetDoubleArrayRegion(lZJArray, 0, pZArrayLength, (jdouble*)pZArray);

		int lNumberOfStripes = lJNIEnv->CallStaticIntMethod(
			sAutoPilotClass,
			newStackID,
			lZJArray
			);

		lJNIEnv->DeleteLocalRef(lZJArray);

		return lNumberOfStripes;
	}
	catch (...)
	{
		sJavaLastError = "Error while running newStack";
		return -1;
	}
}

__declspec(dllexport) double		__cdecl	loadPlane(		short* p16BitImageByteBuffer, 
															int pWidth, 
															int pHeight)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);

		jobject lByteBuffer = lJNIEnv->NewDirectByteBuffer(p16BitImageByteBuffer,pWidth*pHeight*2);

		int lReturnValue = lJNIEnv->CallStaticIntMethod(
			sAutoPilotClass,
			loadPlaneID,
			lByteBuffer, 
			pWidth, 
			pHeight);

		lJNIEnv->DeleteLocalRef(lByteBuffer);

		return lReturnValue;

	}
	catch (...)
	{
		sJavaLastError = "Error while running loadPlane";
		return -1;
	}
}

__declspec(dllexport) double		__cdecl	getResult(		double pMaxWaitTimeInSeconds,
															char* pString,
															double* pResultArray,
															int pResultArrayLength)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);

		jstring lParameterName = lJNIEnv->NewStringUTF(pString);

		jdoubleArray lResultJArray = lJNIEnv->NewDoubleArray(pResultArrayLength);

		int lNumberOfStripes = lJNIEnv->CallStaticIntMethod(
			sAutoPilotClass,
			getResultID,
			pMaxWaitTimeInSeconds,
			lParameterName,
			lResultJArray
			);

		jdouble* lArray = lJNIEnv->GetDoubleArrayElements(lResultJArray, NULL);
		for (int i=0; i<pResultArrayLength; i++)
			pResultArray[i] = lArray[i];
		lJNIEnv->ReleaseDoubleArrayElements(lResultJArray, lArray, NULL);

		lJNIEnv->DeleteLocalRef(lParameterName);
		lJNIEnv->DeleteLocalRef(lResultJArray);

		return lNumberOfStripes;
	}
	catch (...)
	{
		sJavaLastError = "Error while running getResult";
		return -1;
	}
}



////*******************************************************************************************************
//  Microscope state solvers:

 
__declspec(dllexport) int __cdecl l2solveSSP(
		bool pAnchorDetection,
		bool pSymmetricAnchor,
		int pNumberOfWavelengths,
		int pNumberOfPlanes,
		int pSyncPlaneIndex,
		double* pOldStateVector,
		double* pObservationsVector,
		bool* pMissingObservations,
		double* pNewStateVector)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);

		bool lAddExtraDOF = true;
		int lStateVectorLength = pNumberOfWavelengths*pNumberOfPlanes*2*(1+(lAddExtraDOF?4:1));
		int lObservationVectorLength = pNumberOfWavelengths*pNumberOfPlanes*(2*2+2*(lAddExtraDOF?3:0));

		jdoubleArray lOldStateJArray = lJNIEnv->NewDoubleArray(lStateVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lOldStateJArray, 0, lStateVectorLength, (jdouble*)pOldStateVector);

		jdoubleArray lObservationJArray = lJNIEnv->NewDoubleArray(lObservationVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lObservationJArray, 0, lObservationVectorLength, (jdouble*)pObservationsVector);

		jbooleanArray lMissingJArray = lJNIEnv->NewBooleanArray(lObservationVectorLength);
		lJNIEnv->SetBooleanArrayRegion(lMissingJArray, 0, lObservationVectorLength, (jboolean*)pMissingObservations);

		jdoubleArray lNewStateJArray = lJNIEnv->NewDoubleArray(lStateVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lNewStateJArray, 0, lStateVectorLength, (jdouble*)pNewStateVector);

		int lReturnValue = lJNIEnv->CallStaticIntMethod(sAutoPilotClass,l2solveIDSSP, pAnchorDetection, pSymmetricAnchor, pNumberOfWavelengths,pNumberOfPlanes,pSyncPlaneIndex,lOldStateJArray,lObservationJArray,lMissingJArray,lNewStateJArray);

		jdouble* lArray = lJNIEnv->GetDoubleArrayElements(lNewStateJArray, NULL);
		for (int i=0; i<lStateVectorLength; i++)
			pNewStateVector[i] = lArray[i];
		lJNIEnv->ReleaseDoubleArrayElements(lNewStateJArray, lArray, NULL);

		lJNIEnv->DeleteLocalRef(lOldStateJArray);
		lJNIEnv->DeleteLocalRef(lObservationJArray);
		lJNIEnv->DeleteLocalRef(lMissingJArray);
		lJNIEnv->DeleteLocalRef(lNewStateJArray);

		return lReturnValue;
	}
	catch (...)
	{
		sJavaLastError = "Error while running L2 solver";
		return -2;
	}
}


__declspec(dllexport) int __cdecl l2solve(
		bool pAnchorDetection,
		bool pSymmetricAnchor,
		int pNumberOfWavelengths,
		int pNumberOfPlanes,
		bool* pSyncPlaneIndices,
		double* pOldStateVector,
		double* pObservationsVector,
		bool* pMissingObservations,
		double* pNewStateVector)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);

		bool lAddExtraDOF = true;
		int lStateVectorLength = pNumberOfWavelengths*pNumberOfPlanes*2*(1+(lAddExtraDOF?4:1));
		int lObservationVectorLength = pNumberOfWavelengths*pNumberOfPlanes*(2*2+2*(lAddExtraDOF?3:0));
		
		jbooleanArray lSyncPlanesIndicesJArray = lJNIEnv->NewBooleanArray(pNumberOfWavelengths*pNumberOfPlanes);
		lJNIEnv->SetBooleanArrayRegion(lSyncPlanesIndicesJArray, 0, pNumberOfWavelengths*pNumberOfPlanes, (jboolean*)pSyncPlaneIndices);

		jdoubleArray lOldStateJArray = lJNIEnv->NewDoubleArray(lStateVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lOldStateJArray, 0, lStateVectorLength, (jdouble*)pOldStateVector);

		jdoubleArray lObservationJArray = lJNIEnv->NewDoubleArray(lObservationVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lObservationJArray, 0, lObservationVectorLength, (jdouble*)pObservationsVector);

		jbooleanArray lMissingJArray = lJNIEnv->NewBooleanArray(lObservationVectorLength);
		lJNIEnv->SetBooleanArrayRegion(lMissingJArray, 0, lObservationVectorLength, (jboolean*)pMissingObservations);

		jdoubleArray lNewStateJArray = lJNIEnv->NewDoubleArray(lStateVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lNewStateJArray, 0, lStateVectorLength, (jdouble*)pNewStateVector);

		int lReturnValue = lJNIEnv->CallStaticIntMethod(sAutoPilotClass,l2solveID, pAnchorDetection, pSymmetricAnchor, pNumberOfWavelengths,pNumberOfPlanes,lSyncPlanesIndicesJArray,lOldStateJArray,lObservationJArray,lMissingJArray,lNewStateJArray);

		jdouble* lArray = lJNIEnv->GetDoubleArrayElements(lNewStateJArray, NULL);
		for (int i=0; i<lStateVectorLength; i++)
			pNewStateVector[i] = lArray[i];
		lJNIEnv->ReleaseDoubleArrayElements(lNewStateJArray, lArray, NULL);

		lJNIEnv->DeleteLocalRef(lSyncPlanesIndicesJArray);
		lJNIEnv->DeleteLocalRef(lOldStateJArray);
		lJNIEnv->DeleteLocalRef(lObservationJArray);
		lJNIEnv->DeleteLocalRef(lMissingJArray);
		lJNIEnv->DeleteLocalRef(lNewStateJArray);

		return lReturnValue;
	}
	catch (...)
	{
		sJavaLastError = "Error while running L2 solver";
		return -2;
	}
}


__declspec(dllexport) int __cdecl qpsolve(
		bool pAnchorDetection,
		bool pSymmetricAnchor,
		int pNumberOfWavelengths,
		int pNumberOfPlanes,
		bool* pSyncPlaneIndices,
		double* pOldStateVector,
		double* pObservationsVector,
		bool* pMissingObservations,
		double* pMaxCorrections,
		double* pNewStateVector)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);

		bool lAddExtraDOF = true;
		int lStateVectorLength = pNumberOfWavelengths*pNumberOfPlanes*2*(1+(lAddExtraDOF?4:1));
		int lObservationVectorLength = pNumberOfWavelengths*pNumberOfPlanes*(2*2+2*(lAddExtraDOF?3:0));
		
		jbooleanArray lSyncPlanesIndicesJArray = lJNIEnv->NewBooleanArray(pNumberOfWavelengths*pNumberOfPlanes);
		lJNIEnv->SetBooleanArrayRegion(lSyncPlanesIndicesJArray, 0, pNumberOfWavelengths*pNumberOfPlanes, (jboolean*)pSyncPlaneIndices);

		jdoubleArray lOldStateJArray = lJNIEnv->NewDoubleArray(lStateVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lOldStateJArray, 0, lStateVectorLength, (jdouble*)pOldStateVector);

		jdoubleArray lObservationJArray = lJNIEnv->NewDoubleArray(lObservationVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lObservationJArray, 0, lObservationVectorLength, (jdouble*)pObservationsVector);

		jbooleanArray lMissingJArray = lJNIEnv->NewBooleanArray(lObservationVectorLength);
		lJNIEnv->SetBooleanArrayRegion(lMissingJArray, 0, lObservationVectorLength, (jboolean*)pMissingObservations);

		jdoubleArray lMaxCorrectionsJArray = lJNIEnv->NewDoubleArray(lStateVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lMaxCorrectionsJArray, 0, lStateVectorLength, (jdouble*)pMaxCorrections);

		jdoubleArray lNewStateJArray = lJNIEnv->NewDoubleArray(lStateVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lNewStateJArray, 0, lStateVectorLength, (jdouble*)pNewStateVector);

		int lReturnValue = lJNIEnv->CallStaticIntMethod(sAutoPilotClass,qpsolveID, pAnchorDetection, pSymmetricAnchor, pNumberOfWavelengths,pNumberOfPlanes,lSyncPlanesIndicesJArray,lOldStateJArray,lObservationJArray,lMissingJArray,lMaxCorrectionsJArray,lNewStateJArray);

		jdouble* lArray = lJNIEnv->GetDoubleArrayElements(lNewStateJArray, NULL);
		for (int i=0; i<lStateVectorLength; i++)
			pNewStateVector[i] = lArray[i];
		lJNIEnv->ReleaseDoubleArrayElements(lNewStateJArray, lArray, NULL);


        lJNIEnv->DeleteLocalRef(lSyncPlanesIndicesJArray);
		lJNIEnv->DeleteLocalRef(lOldStateJArray);
		lJNIEnv->DeleteLocalRef(lObservationJArray);
		lJNIEnv->DeleteLocalRef(lMissingJArray);
		lJNIEnv->DeleteLocalRef(lMaxCorrectionsJArray);
		lJNIEnv->DeleteLocalRef(lNewStateJArray);

		return lReturnValue;
	}
	catch (...)
	{
		sJavaLastError = "Error while running QP solver";
		return -2;
	}
}

__declspec(dllexport) int __cdecl extrasolve(
		int pNumberOfWavelengths,
		int pNumberOfPlanes,
		int pNumberOfDOFs,
		double* pOldStateVector,
		double* pObservationsVector,
		bool* pMissingObservations,
		double* pMaxCorrections,
		double* pNewStateVector)
{
	try
	{
		clearError();
		JNIEnv *lJNIEnv;
		sJVM->AttachCurrentThread((void**)&lJNIEnv, NULL);

		int lStateVectorLength = pNumberOfWavelengths*pNumberOfPlanes;
		int lObservationVectorLength = lStateVectorLength;
		
		jdoubleArray lOldStateJArray = lJNIEnv->NewDoubleArray(lStateVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lOldStateJArray, 0, lStateVectorLength, (jdouble*)pOldStateVector);

		jdoubleArray lObservationJArray = lJNIEnv->NewDoubleArray(lObservationVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lObservationJArray, 0, lObservationVectorLength, (jdouble*)pObservationsVector);

		jbooleanArray lMissingJArray = lJNIEnv->NewBooleanArray(lObservationVectorLength);
		lJNIEnv->SetBooleanArrayRegion(lMissingJArray, 0, lObservationVectorLength, (jboolean*)pMissingObservations);

		jdoubleArray lMaxCorrectionsJArray = lJNIEnv->NewDoubleArray(lStateVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lMaxCorrectionsJArray, 0, lStateVectorLength, (jdouble*)pMaxCorrections);

		jdoubleArray lNewStateJArray = lJNIEnv->NewDoubleArray(lStateVectorLength);
		lJNIEnv->SetDoubleArrayRegion(lNewStateJArray, 0, lStateVectorLength, (jdouble*)pNewStateVector);

		int lReturnValue = lJNIEnv->CallStaticIntMethod(sAutoPilotClass,extrasolveID, pNumberOfWavelengths,pNumberOfPlanes,pNumberOfDOFs,lOldStateJArray,lObservationJArray,lMissingJArray,lMaxCorrectionsJArray,lNewStateJArray);

		jdouble* lArray = lJNIEnv->GetDoubleArrayElements(lNewStateJArray, NULL);
		for (int i=0; i<lStateVectorLength; i++)
			pNewStateVector[i] = lArray[i];
		lJNIEnv->ReleaseDoubleArrayElements(lNewStateJArray, lArray, NULL);

		lJNIEnv->DeleteLocalRef(lOldStateJArray);
		lJNIEnv->DeleteLocalRef(lObservationJArray);
		lJNIEnv->DeleteLocalRef(lMissingJArray);
		lJNIEnv->DeleteLocalRef(lMaxCorrectionsJArray);
		lJNIEnv->DeleteLocalRef(lNewStateJArray);

		return lReturnValue;
	}
	catch (...)
	{
		sJavaLastError = "Error while running L2 solver";
		return -2;
	}
}





__declspec(dllexport) void __cdecl freePointer(void* pPointer)
{
	free(pPointer);
}


/*
 B = byte
 C = char
 D = double
 F = float
 I = int
 J = long
 S = short
 V = void
 Z = boolean
 Lfully-qualified-class = fully qualified class
 [type = array of type
 (argument types)return type = method type. If no arguments, use empty argument types: (). 
 If return type is void (or constructor) use (argument types)V.
 Observe that the ; is needed after the class name in all situations. 
 This won't work "(Ljava/lang/String)V" but this will "(Ljava/lang/String;)V". 
 */

