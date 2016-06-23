% Example file for two class problem

% Load the parameters from training
load const
load linear

% The file "instance.format" contains the format of the instances
fileHandle = fopen('instance.format');
format = fgetl(fileHandle);
fclose(fileHandle);

% No features (just class label)?
if length(format) <= 2
	fprintf(1, '\n0\n');
	exit
end

% The file "instance.csv" contains the instance for classification
fileHandle = fopen('instance.csv');                                           
rawInstance = textscan(fileHandle, format, 'delimiter', ',', 'headerlines', 1);
fclose(fileHandle);

noFeatures = length(rawInstance);
noFeatures = noFeatures - 1;
instance = cell2mat(rawInstance(1:noFeatures));

% if > 0 then WEKA class 0 -> return 0
% if <= 0 then WEKA class 1 -> return 1
class = instance * linear + const <=  0;

% The ECST expects the class number as the last line of the matlab standard output
fprintf(1, '\n%d\n', class);

exit
               