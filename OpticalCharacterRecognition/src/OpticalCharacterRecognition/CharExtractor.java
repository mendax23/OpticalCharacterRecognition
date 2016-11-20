package OpticalCharacterRecognition;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;


public class CharExtractor {
	
	BufferedImage img,edgeImg;
	int std_width,std_height;
	BufferedImage lines[];
	BufferedImage characters[][];
	int spacePosition[][];
	int noOfWords[];
	
	public CharExtractor(String s)
	{
		//Set std_width and std_height of a character after resizing
		std_width=10;
		std_height=12;

		inputPage(s);
		generateEdgeImage();
		detectAndSegmentIntoLines();
		outputPage(edgeImg,"Edge");
		segmentIntoCharacters();		
	}
	
	private void segmentIntoCharacters() 
	{
		//Taking Vertical Projection
		int a[][]=verticalProjection();
		characters=new BufferedImage[lines.length][];
		spacePosition=new int[lines.length][];
		noOfWords=new int[lines.length];
		
		//Determine X Coordinate
		for(int i=0;i<a.length;i++)
			segment1LineIntoCharacters(a[i],i);
		
		
	}

	private void segment1LineIntoCharacters(int a[],int in) 
	{	
		Vector<Integer> textboxXi=new Vector<Integer>();
		Vector<Integer> textboxXf=new Vector<Integer>();
		
		int minSpace=1;
		int minSpaceToSegmentWord=lines[in].getHeight()/3;
		int charCount=0;
		int spaceCount=0;
		int lastFinal=0;
		textboxXi.add(0);
		
		noOfWords[in]=0;		
		spacePosition[in]=new int[lines[in].getWidth()];
		for(int i=0;i<spacePosition[in].length;i++)
			spacePosition[in][i]=-1;
		
		
		for(int j=1;j<a.length;j++)
		if(a[j]==0)
		{
			spaceCount++;
			if(a[j-1]!=0)
				lastFinal=(j-1);
		}
		else
		{
			if(spaceCount>=minSpace)
			{
				textboxXf.add(lastFinal);
				charCount++;
				textboxXi.add(j);
				if(spaceCount>=minSpaceToSegmentWord)
				{
					spacePosition[in][noOfWords[in]]=charCount;
					noOfWords[in]++;
					charCount=0;
				}
	
			}
			spaceCount=0;
			
		}
			
		if(a[a.length-1]!=0)
			textboxXf.add(a.length-1);
		else
			textboxXf.add(lastFinal);
		
		charCount++;
		spacePosition[in][noOfWords[in]]=charCount;
		noOfWords[in]++;
		
		characters[in]=new BufferedImage[textboxXf.size()];
		for(int i=0;i<textboxXf.size();i++)
		{
			characters[in][i]=new BufferedImage(textboxXf.get(i)-textboxXi.get(i)+1,lines[in].getHeight(),BufferedImage.TYPE_BYTE_GRAY);
			for(int j=textboxXi.get(i);j<=textboxXf.get(i);j++)
			for(int k=0;k<lines[in].getHeight();k++)
				characters[in][i].setRGB(j-textboxXi.get(i),k,lines[in].getRGB(j,k));
		
			characters[in][i]=resize(characters[in][i],std_width,std_height,i,in);
			outputPage(characters[in][i],"Line"+in+"Char"+i);
		}
				
				
	}

	private BufferedImage resize(BufferedImage inputImage, int scaledWidth, int scaledHeight,int i,int in)
    { 
        // Creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());
 
        // Scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        
        outputImage = sharpenImg(outputImage);
        return outputImage;

    }

	private int[][] verticalProjection()
	{

		int a[][]=new int[lines.length][];
		for(int i=0;i<lines.length;i++)
		{
			a[i]=new int[lines[i].getWidth()];
			for(int j=0;j<lines[i].getWidth();j++)
			{
				a[i][j]=0;
				for(int k=0;k<lines[i].getHeight();k++)
					if(lines[i].getRGB(j,k)<img.getRGB(0,0))
						a[i][j]++;
			}
		}
		return a;
	}

	private void detectAndSegmentIntoLines() 
	{
		int a[]=horizontalProjection();
				
		//Determine Y Coordinate
		Vector<Integer> textboxYi = new Vector<Integer>();
		Vector<Integer> textboxYf = new Vector<Integer>();
		
		int minEdges=1;
		boolean insideTextbox=false;
		int upper=0,lower=0;
		int minLinesInChar=3;
		
		//Assuming 1st line of the image will NOT contain any part of the character
		for(int i=1;i<a.length;i++)
		{			
			if(insideTextbox==false&&(a[i]>minEdges))
			{
				insideTextbox=true;
				upper=i;
			}
			if(insideTextbox==true&&a[i]<=minEdges)
			{
				insideTextbox=false;
				lower=i-1;
			
				if(lower-upper+1>=minLinesInChar)
				{
					textboxYi.add(upper);
					textboxYf.add(lower);
				}
			}
			
			
		}
		
		//Determine X Coordinate
		Vector<Integer> textboxXi=new Vector<Integer>();
		Vector<Integer> textboxXf=new Vector<Integer>();
		for(int i=0;i<textboxYf.size();i++)
		{
			int left=Integer.MAX_VALUE,rt=-1;
			int width=edgeImg.getWidth();
			
			for(int j=textboxYi.get(i);j<=textboxYf.get(i);j++)
			for(int k=0;k<width;k++)
			if(edgeImg.getRGB(k,j)>edgeImg.getRGB(0,0))
			{
				if(left>k)
					left=k;
				if(rt<k)
					rt=k;
			}
			
			textboxXi.add(left);
			textboxXf.add(rt);
		}
		
		segmentIntoLines(textboxXi,textboxYi,textboxXf,textboxYf);
	}
	


	private void segmentIntoLines(Vector<Integer> textboxXi,Vector<Integer> textboxYi, Vector<Integer> textboxXf,Vector<Integer> textboxYf) {
		
		lines = new BufferedImage[textboxYf.size()];
		
		for(int i=0;i<textboxYf.size();i++)
		{
			lines[i]=new BufferedImage(textboxXf.get(i)-textboxXi.get(i)+1,textboxYf.get(i)-textboxYi.get(i)+1,BufferedImage.TYPE_BYTE_GRAY);
			for(int j=textboxXi.get(i);j<=textboxXf.get(i);j++)
				for(int k=textboxYi.get(i);k<=textboxYf.get(i);k++)
					lines[i].setRGB(j-textboxXi.get(i),k-textboxYi.get(i),img.getRGB(j,k));
			
			outputPage(lines[i],"Line"+i+"");
		}
		
	}

	private int[] horizontalProjection() 
	{
		int height=edgeImg.getHeight(),width=edgeImg.getWidth();
		int a[]=new int[height];
		for(int i=0;i<height;i++)
		{
			a[i]=0;
			for(int j=0;j<width;j++)
				if(edgeImg.getRGB(j,i)>edgeImg.getRGB(0,0))     //Assuming top left pixel is black in edgeImg
					a[i]++;
		}
		return a;
	}

	private void generateEdgeImage() 
	{		
		convertImgToGrayscale();
		
		int width,height;
		width=img.getWidth();
		height=img.getHeight();
		
		edgeImg = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
		int left=0,upper=0,rightUpper=0;
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
			{
				if(i>0 && i<width-1 && j>0 && j<height)
				{
					left=Math.abs(img.getRGB(i,j)-img.getRGB(i-1,j));
					upper=Math.abs(img.getRGB(i,j)-img.getRGB(i,j-1));
					rightUpper=Math.abs(img.getRGB(i,j)-img.getRGB(i+1,j-1));
					edgeImg.setRGB(i,j,Math.max(left,Math.max(upper,rightUpper)));
				}
				else
				edgeImg.setRGB(i, j, 0);
			}
		
		edgeImg=sharpenImg(edgeImg);
	}
	private void convertImgToGrayscale() 
	{
		int width,height;
		width=img.getWidth();
		height=img.getHeight();
	
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
			{
				Color c = new Color(img.getRGB(i,j));
				double y=0.2126*c.getRed()+0.7152*c.getGreen()+0.0722*c.getBlue(); //luminosity average
				img.setRGB(i,j,new Color((int)y,(int)y,(int)y).getRGB());
			}
		
	}
	private BufferedImage sharpenImg(BufferedImage img) 
	{
		Kernel kernel = new Kernel(3, 3, new float[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 });
	    BufferedImageOp op = new ConvolveOp(kernel);
	    return op.filter(img, null);
	}

	private void inputPage(String s) 
	{		
		try
		{
			img = ImageIO.read(new File(s));
		}
		catch(IOException e)
		{
			System.out.println("Image Not Found!");
		}
	}
	private void outputPage(BufferedImage im,String imgName) 
	{		
		try
		{
			File ed=new File("/Users/YOURUSERNAME/Desktop/resources/images/"+imgName+".jpeg");
			ImageIO.write(im, "jpeg",ed);
		}
		catch(IOException e)
		{
			System.out.println("Image File Not Created !");
		}
		
	}
}



