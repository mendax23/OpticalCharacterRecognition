package OpticalCharacterRecognition;


public class Network {

	Layer inputLayer;
	
	Layer outputLayer;
	double learningRate; //Learning Rate , eta in original program
	double netError; //net error to terminate training , episilon in original program
	double thresholdError;
	
	//noOfUnitsInOutputLayer = Number of possible characters our network will detect (Excluding bias)
	//noOfUnitsInInputLayer = Number of features extracted from the input
	public Network(int noOfUnitsInInputLayer,int noOfUnitsInOutputLayer)
	{
		inputLayer=new Layer(noOfUnitsInInputLayer,0);
		outputLayer=new Layer(noOfUnitsInOutputLayer,noOfUnitsInInputLayer);
		//Check redefinition in Neural Network  : (original)Initiate Application
		
		
		
	}
}

