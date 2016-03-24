%% How to use the MatLab interface to the AutoPilot library
% how to call dcts2 (dcts 3D), dcts3 (dcts 3D), and tenengrad2 (2D)


% First make sure to load the jar:
javaaddpath('AutoPilotM.jar');

% You can also add all jars in the curent folder which should include the 
% AutoPilot.jar file too...
javaaddpath('./');

% you can import the interface class:
import autopilot.interfaces.AutoPilotM;

% Query the functions available:
%methods AutoPilotM -full;

% Test it on a 3x3 matrix:
A = [1 2 0; 2 5 -1; 4 10 -1];

% the second parameter is the PSF diameter support in pixels:
a = AutoPilotM.dcts2(A,3);
disp(a)

% Or test it on a 100x100x100 matrix:
% the second parameter is the PSF diameter support in pixels for X,Y,
% the thrid parameter is the PSF diameter support in pixels for Z:
b = AutoPilotM.dcts3(rand(100,100,100),3,3);
disp(b)
