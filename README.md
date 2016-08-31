
# AutoPilot - Open Source Software Library for Real-Time Adaptive Light-Sheet Microscopy.

Light-sheet microscopy is a powerful method for imaging the development and function of biological systems. In order to successfully produce high-resolution images, these microscopes must achieve perfect overlap between the thin sheet of light used to illuminate the sample and the focal plane of the objective used to form an image. Whenever this co-planarity is violated, spatial resolution and image contrast immediately suffer. 

Unfortunately, living specimens have complex optical properties that are not only heterogeneous in space but also dynamic in time, which typically leads to significant, spatiotemporally variable mismatches between light-sheet and detection planes. Achieving and maintaining high spatial resolution thus critically requires an automated framework capable of continuously analyzing and optimizing the spatial relationship between light-sheet and detection planes across the specimen volume and in real-time. 

<em>AutoPilot</em> is the open source project that hosts the general algorithm for fast and robust assessment of local image quality, an automated computational method for image-based mapping of the three-dimensional light-sheet geometry inside a fluorescently labeled biological specimen, and a general algorithm for data-driven optimization of the system state of light-sheet microscopes capable of multi-color imaging with multiple illumination and detection arms.

## Contents of repository:

- Libs          : contains 3rd party libraries not handled via Maven/Gradle dependencies.
- artwork       : contains source files for the AutoPilot logo
- gradle/wrapper: gradle wrapper files
- src           : java, cpp and matlab source code
- wiki          : wiki files

## Contents of source code folder:

- cpp           : C++ source code for C/C++ interface (also for interfacing with LabView)
- java          : Java source code (core algorithms)
- matlab        : Matlab source code (utilities for computing DCTS3D from matlab)

## In depth details:

For more information please consult the main [website](http://microscopeautopilot.github.io/) and the [wiki](http://github.com/MicroscopeAutoPilot/AutoPilot/wiki).







