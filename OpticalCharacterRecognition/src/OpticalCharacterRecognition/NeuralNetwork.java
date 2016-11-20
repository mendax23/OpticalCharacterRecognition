package OpticalCharacterRecognition;
/*
 * MINOR PROJECT 2016:2017
 */
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


public class NeuralNetwork {
	
	private static Random generator;
	static int noOfFeatures; 
	static int noOfdetectableCharcters;
	static int noOfCharactersWrittenOfTest;
	static int spacePositionOfTest[][];
	static int noOfSpacesInTest[];
	static int WordCountInIthLineOfTest[];
	static int currentlineNoOfTest;
	public static String path;
	public static int trainType;
	
	public NeuralNetwork(String p,int t)
	{
		noOfdetectableCharcters=62;
		noOfFeatures=120;
		generator = new Random(4711);
		path=p;
		trainType=t;
	}
	
	public void NeuralFunction() throws Exception
	{
		
		Network net=new Network(noOfFeatures,noOfdetectableCharcters);
		double maxError;
		boolean stop;
		
	
		
		if(trainType==1)
			initializeTheta(net);
		else
			retrieveTheta(net);
		
		//Initialisation Of learning Rate and netError 
		net.learningRate=0.001;
		net.thresholdError=0.0001;
		
		int targetVectorsForAllCharacters[][]=new int[noOfdetectableCharcters][noOfdetectableCharcters];
		for(int i=0;i<noOfdetectableCharcters;i++)
		for(int j=0;j<noOfdetectableCharcters;j++)
		if(i==j)
			targetVectorsForAllCharacters[i][j]=1;
		else
			targetVectorsForAllCharacters[i][j]=0;
	
		
		
		
		String str[]=new String[2];
		str[0]="/Users/YOURUSERNAME/Desktop/resources/trainimage1.PNG";//Change the path.
		str[1]="/Users/YOURUSERNAME/Desktop/resources/trainimage2.PNG"; //Change the path.
		
		for(int l=0;l<2;l++)
		{
			
			double featureVectorsForAllCharacters[][]=generateFeatureVectorsForAllCharacters(str[l]);
		
		if(trainType==1)
		{		
			//Training Part
				
			do
			{
				maxError=0;
				stop=true;
				for(int i=0;i<noOfdetectableCharcters;i++)
				{
					simulateNet(net,featureVectorsForAllCharacters[i],targetVectorsForAllCharacters[i],false,false,false);
					
					maxError = Math.max(maxError, net.netError);
					stop=stop&&(net.netError<net.thresholdError);
				}
				maxError = Math.max(maxError, net.thresholdError);
				
				if(!stop)
				{
					for(int i=0;i<noOfdetectableCharcters;i++)
					{
						int j;
						j=generateRandomNumberBw(0, noOfdetectableCharcters-1);
						simulateNet(net, featureVectorsForAllCharacters[j],targetVectorsForAllCharacters[j], true, false,false);
					}
				}
			}while(!stop);
		}
		else
			retrieveTheta(net);
		
		for(int i=0;i<noOfdetectableCharcters;i++)
		{
		
			simulateNet(net, featureVectorsForAllCharacters[i], targetVectorsForAllCharacters[i], false, true,false);
			
		}
		storeTheta(net);
		}
	
		
		//Testing Part
	
		testFunc(net);
		
		
		
		
	}
	
	private static void testFunc(Network net) throws Exception {
		
		CharExtractor ce=new CharExtractor(path);
		noOfCharactersWrittenOfTest=0;
		spacePositionOfTest=new int[ce.lines.length][];
		noOfSpacesInTest=new int[ce.lines.length];
		WordCountInIthLineOfTest=new int[ce.lines.length];
		currentlineNoOfTest=0;
		for(int i=0;i<noOfSpacesInTest.length;i++)
		{
			noOfSpacesInTest[i]=ce.noOfWords[i];
			WordCountInIthLineOfTest[i]=0;
		}
		for(int i=0;i<spacePositionOfTest.length;i++)
		{
			spacePositionOfTest[i]=new int[noOfSpacesInTest[i]];
			for(int j=0;j<spacePositionOfTest[i].length;j++)
				spacePositionOfTest[i][j]=ce.spacePosition[i][j];
		}
		
		
		for(int i=0;i<ce.characters.length;i++)
			for(int j=0;j<ce.characters[i].length;j++)
			{
				currentlineNoOfTest=i;
				double a[]=generateFeatureVectorForCharacter(ce.characters[i][j]);
				int t[]=new int[noOfdetectableCharcters];
				simulateNet(net,a,t,false,true,true);
			}
		
		
	}

	private static double[] generateFeatureVectorForCharacter(BufferedImage img) 
	{
		double a[]=new double[img.getHeight()*img.getWidth()];
		for(int i=0;i<img.getHeight();i++)
			for(int j=0;j<img.getWidth();j++)
				if(img.getRGB(j,i)<Color.LIGHT_GRAY.getRGB()) //Assuming bottom right pixel is white
					a[j+i*img.getWidth()]=0;
					else
					a[j+i*img.getWidth()]=1;
		
		return a;
	}
	private static void storeTheta(Network net)
	{
		
		String fileName = "/Users/username/Desktop/minor/new2.txt";//change the path
		try
		{
			FileOutputStream fos = new FileOutputStream(fileName);
			DataOutputStream dos= new DataOutputStream(fos);
            for(int i=1;i<net.outputLayer.noOfUnits;i++)
    		for(int j=0;j<net.inputLayer.noOfUnits;j++)
    			dos.writeDouble(net.outputLayer.unit[i].thetaj[j].theta);
			dos.close();
		} 
		catch(IOException e)
		{
			System.out.println("Error in Storing Theta");
			System.out.println("IOException:"+e);
		}
			
      }
	
	private static void retrieveTheta(Network net){
		String fileName = "/Users/username/Desktop/minor/new2.txt";//change the path
		try
		{	
			FileInputStream fin = new FileInputStream(fileName);
			DataInputStream din = new DataInputStream(fin);

			for(int i=1;i<net.outputLayer.noOfUnits;i++)
    			for(int j=0;j<net.inputLayer.noOfUnits;j++)
    				net.outputLayer.unit[i].thetaj[j].theta= din.readDouble();
			din.close();
		}
		
		catch(FileNotFoundException fe)
		{
			System.out.println("Error in Retriving Theta");
			System.out.println("FileNotFoundException : " + fe);
		}
		catch(IOException ioe)
		{
			System.out.println("Error in Retrieving Theta");
			System.out.println("IOException : " + ioe);
		}
	}


	private static double[][] generateFeatureVectorsForAllCharacters(String str) 
	{
		CharExtractor ce=new CharExtractor(str);
		double a[][];
		
		int size=0;
		for(int i=0;i<ce.characters.length;i++)
		size+=ce.characters[i].length;	
		
		a=new double[size][];
		
		int in=0;
		for(int i=0;i<ce.characters.length;i++)
		{	
			
			for(int j=0;j<ce.characters[i].length;j++)	
			{
				a[in]=new double[ce.characters[i][j].getHeight()*ce.characters[i][j].getWidth()];
				for(int k=0;k<ce.characters[i][j].getHeight();k++)
					for(int l=0;l<ce.characters[i][j].getWidth();l++)
					{
						if(ce.characters[i][j].getRGB(l,k)<Color.LIGHT_GRAY.getRGB()) 
						a[i*26+j][l+k*ce.characters[i][j].getWidth()]=0;
						else
						a[i*26+j][l+k*ce.characters[i][j].getWidth()]=1;
					}
				in++;
			}
		}
		return a;
	}

	private static void simulateNet(Network net, double[] input, int[] target,boolean training, boolean protocoling,boolean testing) throws Exception 
	{	
		double output[]=new double[noOfdetectableCharcters];
		setInput(net,input,protocoling);
		propagateNet(net);
		getOutput(net,output,protocoling,testing);
		computeOutputError(net,target);
		if(training)
		adjustWeights(net);
	}
	private static void adjustWeights(Network net)
	{
		double error,Out=0.0;
		for(int i=1;i<net.outputLayer.noOfUnits;i++)
		{
			for(int j=0;j<net.inputLayer.noOfUnits;j++)
			{
				 Out=net.inputLayer.unit[j].output;
				error=net.outputLayer.unit[i].error;
				net.outputLayer.unit[i].thetaj[j].theta+=net.learningRate*error*Out;
			}
		}
	}

	private static void computeOutputError(Network net, int[] target) 
	{
		double error;
		net.netError=0;
		for(int i=1;i<net.outputLayer.noOfUnits;i++)
		{
			error=target[i-1]-net.outputLayer.unit[i].activation;
			net.outputLayer.unit[i].error=error;
			net.netError += 0.5*Math.abs(error)*Math.abs(error);	
		}
	}

	private static void getOutput(Network net, double[] output, boolean protocoling,boolean testing) throws Exception 
	{
		for(int i=1;i<net.outputLayer.noOfUnits;i++)
			output[i-1]=net.outputLayer.unit[i].output;
		if(protocoling)
		{
			if(testing)
			writeOuput(net,output,true);
			else
			writeOuput(net,output,false);	
		}
	}
	
	private static void writeOuput(Network net, double[] output,boolean testing) throws Exception 
	{
		int count,index=-1;
		count=0;
		for(int i=0;i<noOfdetectableCharcters;i++)
		if(output[i]==1)
		{
			count++;
			index=i;
		}
	    if(testing){
		if(count>0)
		{
			if(index<26)
			System.out.print((char)(index+'A'));
			else if(index<52)
			System.out.print((char)(index-26+'a'));
			else
			System.out.print((char)(index-52+'0'));
		}	
		else
		{
			System.out.print("$");
		}}
		if(testing)
		{
				noOfCharactersWrittenOfTest++;
		
				if(noOfCharactersWrittenOfTest==spacePositionOfTest[currentlineNoOfTest][WordCountInIthLineOfTest[currentlineNoOfTest]])
				{
				System.out.print(" ");
					WordCountInIthLineOfTest[currentlineNoOfTest]++;
					if((WordCountInIthLineOfTest[currentlineNoOfTest]%6)==0)
					System.out.println();
						noOfCharactersWrittenOfTest=0;
			
				}
		}
			
	}

	private static void propagateNet(Network net) 
	{
		double sum;
		int in=-1;
		double max=Double.MIN_VALUE;
		for(int i=1;i<net.outputLayer.noOfUnits;i++)
		{
			sum=0;
			for(int j=0;j<net.inputLayer.noOfUnits;j++)
			{	
				sum +=(net.outputLayer.unit[i].thetaj[j].theta)*(net.inputLayer.unit[j].output);
			}
			if(sum>max)
			{
				max=sum;
				in=i;
			}
			net.outputLayer.unit[i].activation=sigmoid(sum);
		}
		for(int i=1;i<net.outputLayer.noOfUnits;i++)
		if(i!=in)
			net.outputLayer.unit[i].output=0;
		else
			net.outputLayer.unit[i].output=1;
		
	}

	private static double sigmoid(double sum) 
	{
		return 1.0/(1+Math.exp(-sum));
	}

	private static void setInput(Network net, double[] input, boolean protocoling) 
	{
		for(int i=1;i<net.inputLayer.noOfUnits;i++)
		{
			net.inputLayer.unit[i].output=input[i-1];
		}
	}

	private static void initializeTheta(Network net) 
	{
		for(int i=1;i<net.outputLayer.noOfUnits;i++)
			for(int j=0;j<net.inputLayer.noOfUnits;j++)
				net.outputLayer.unit[i].thetaj[j].theta=generateRandomNumberBw(-0.5,0.5);
	}

	private static double generateRandomNumberBw(double st, double end) 
	{		
		return (generator.nextDouble()*(end-st+1)+st);
	}
	private static int generateRandomNumberBw(int st, int end) 
	{		
		return (int)(generator.nextDouble()*(end-st+1)+st);
	}

}




