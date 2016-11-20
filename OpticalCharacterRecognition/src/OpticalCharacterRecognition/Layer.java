package OpticalCharacterRecognition;

public class Layer {
	
	int noOfUnits;
	Units unit[];
	
	public Layer(int n,int noOfUnitsInPrevLayer)
	{
		//In original program noOfUnits was n and array size was n+1
		noOfUnits=n+1;
		unit = new Units[noOfUnits];
		for(int i=0;i<noOfUnits;i++)
			unit[i]=new Units(noOfUnitsInPrevLayer);
		//System.out.println(noOfUnitsInPrevLayer);
		unit[0].activation=1; //BIAS
	}

}

