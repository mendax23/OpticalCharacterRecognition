package OpticalCharacterRecognition;

public class Units {

	double activation;
	double output;
	double error;
	Theta thetaj[];
	
	public Units(int noOfUnitsInPrevLayer)
	{
		thetaj=new Theta[noOfUnitsInPrevLayer+1];
		for(int i=0;i<noOfUnitsInPrevLayer+1;i++)
			thetaj[i]=new Theta();
	}
	
}
