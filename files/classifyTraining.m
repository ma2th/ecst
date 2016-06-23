% Example file for two class problem

% The file "instance.format" contains the format of the instances
fileHandle = fopen('instances.format');
format = fgetl(fileHandle);
fclose(fileHandle);

% No features (just class label)?
if length(format) <= 2
	exit
end

% The file "instances.csv" contains all training instances
% One header line with the name of the attributes is written
fileHandle = fopen('instances.csv');                                           
rawTrainingData = textscan(fileHandle, format, 'delimiter', ',', 'headerlines', 1);
fclose(fileHandle);

noFeatures = length(rawTrainingData);
noFeatures = noFeatures - 1;
trainingData = cell2mat(rawTrainingData(1:noFeatures));                                           
[class, err, post, log, coeff] = classify(trainingData, trainingData, rawTrainingData{noFeatures + 1});

% The file "classes.csv" contains the class names and class numbers
fileHandle = fopen('classes.csv');                                           
rawClasses = textscan(fileHandle, '%d %s', 'delimiter', ',');
fclose(fileHandle);

% Check if WEKA class 0 is Matlab class 0
% rawClasses.1 == WEKA.0 !
if strcmp(rawClasses{2}{1}, coeff(1,2).name1) == 1
	linear = coeff(1,2).linear;
	const = coeff(1,2).const;
else
	linear = coeff(2,1).linear;
	const = coeff(2,1).const;
end

% Save the parameters for classification
save('linear.mat', 'linear');
save('const.mat', 'const');

% Write algorithm specific multiplier
fileHandle = fopen('multiplier.csv','w');
fprintf(fileHandle,'constantExternExample, %d\n', 1);
fprintf(fileHandle,'attributesExternExample, %d\n', size(trainingData,2));
fclose(fileHandle);

exit

