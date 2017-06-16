package cy;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import javax.swing.*;
public class ImageHiding extends JFrame implements ActionListener {

	BufferedImage hostImage;
	 BufferedImage secretImage;

	 JPanel controlPanel;
	 JPanel imagePanel;

	 JTextField encodeBitsText;
	 JButton encodeBitsPlus;
	 JButton encodeBitsMinus;

	 JTextField nBitsText;
	 JButton nBitsPlus;
	 JButton nBitsMinus;

	 ImageCanvas hostCanvas;
	 ImageCanvas secretCanvas;

	 Steganography s;

	 public BufferedImage getHostImage()
	 {
	  BufferedImage img = null;
	  try
	  {
	   img = ImageIO.read(new File("C:\\Users\\Vishal PC\\Documents\\sir.jpg"));
	  }
	  catch (IOException ioe) { ioe.printStackTrace(); }
	  return img;
	 }

	 public int getBits()
	 {
	  return Integer.parseInt(encodeBitsText.getText());
	 }

	 public void actionPerformed(ActionEvent event)
	 {
	  Object source = event.getSource();
	  if (source == encodeBitsPlus)
	  {
	   int bits = this.getBits() + 1;
	   if (bits > 8) { bits = 8; }
	   encodeBitsText.setText(Integer.toString(bits));
	   s = new Steganography(this.getHostImage());
	   s.encode( bits);
	   hostCanvas.setImage(s.getImage());
	   hostCanvas.repaint();
	  }
	  else if (source == encodeBitsMinus)
	  {
	   int bits = this.getBits() - 1;
	   if (bits < 0) { bits = 0; }
	   encodeBitsText.setText(Integer.toString(bits));
	   s = new Steganography(this.getHostImage());
	   s.encode( bits);
	   hostCanvas.setImage(s.getImage());
	   hostCanvas.repaint();
	  }
	 }

	 public ImageHiding()
	 {
	  GridBagLayout layout = new GridBagLayout();
	  GridBagConstraints gbc = new GridBagConstraints();
	  this.setTitle("Image Hiding Demo");
	  Container container = this.getContentPane();
	  this.setLayout(layout);
	  this.add(new JLabel("Bits to encode into host image:"));
	  encodeBitsText = new JTextField("0", 5);
	  encodeBitsText.setEditable(false);
	  gbc.weightx = -1.0;
	  layout.setConstraints(encodeBitsText, gbc);
	  this.add(encodeBitsText);
	  encodeBitsPlus = new JButton("+");
	  encodeBitsPlus.addActionListener(this);
	  encodeBitsMinus = new JButton("-");
	  encodeBitsMinus.addActionListener(this);
	  gbc.weightx = 1.0;
	  layout.setConstraints(encodeBitsPlus, gbc);
	  this.add(encodeBitsPlus);
	  gbc.gridwidth = GridBagConstraints.REMAINDER;
	  layout.setConstraints(encodeBitsMinus, gbc);
	  this.add(encodeBitsMinus);
	  GridBagLayout imageGridbag = new GridBagLayout();
	  GridBagConstraints imageGBC = new GridBagConstraints();
	  imagePanel = new JPanel();
	  imagePanel.setLayout(imageGridbag);
	  JLabel hostImageLabel = new JLabel("Host image:");
	  imagePanel.add(hostImageLabel);
	  hostCanvas = new ImageCanvas(this.getHostImage());  
	  imagePanel.add(hostCanvas);
	  gbc.gridwidth = GridBagConstraints.REMAINDER;
	  layout.setConstraints(imagePanel, gbc);
	  this.add(imagePanel);
	  Steganography host = new Steganography(this.getHostImage());
	  host.encode( this.getBits());
	  hostCanvas.setImage(host.getImage());
	  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  this.pack();
	  this.setVisible(true);
	 }
	 public static void main(String[] args)
	 {
	  ImageHiding frame = new ImageHiding();
	  frame.setVisible(true);
	 }
	 public class ImageCanvas extends JPanel
	 { 
	  Image img;
	  public void paintComponent(Graphics g)
	  {
	   g.drawImage(img, 0, 0, this);
	  }

	  public void setImage(Image img)
	  {
	   this.img = img;
	  }

	  public ImageCanvas(Image img)
	  {
	   this.img = img;

	   this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
	  }
	 }
	}

	class Steganography
	{
	 BufferedImage image;
	 
	 //define a byte array elements to hold the values.
	 byte[] elements = null;
//String elements=null;
	 public void getMaskedImage(int bits)
	 {
	  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));
	  int maskBits = (int)(Math.pow(2, bits)) - 1 << (8 - bits);
	  int mask = (maskBits << 24) | (maskBits << 16) | (maskBits << 8) | maskBits;
	  for (int i = 0; i < imageRGB.length; i++)
	  {
	   imageRGB[i] = imageRGB[i] & mask;
	  }

	  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
	 }

	 public void encode(int encodeBits)
	 {
	  //int[] encodeRGB = encodeImage.getRGB(0, 0, encodeImage.getWidth(null), encodeImage.getHeight(null), null, 0, encodeImage.getWidth(null));
	  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));
	 
	  int encodeByteMask = (int)(Math.pow(2, encodeBits)) - 1 << (8 - encodeBits);
	  int encodeMask = (encodeByteMask << 24) | (encodeByteMask << 16) | (encodeByteMask << 8) | encodeByteMask;

	  int decodeByteMask = ~(encodeByteMask >>> (8 - encodeBits)) & 0xFF;
	  int hostMask = (decodeByteMask << 24) | (decodeByteMask << 16) | (decodeByteMask << 8) | decodeByteMask;
	  int t =0;
	  for (int i = 0; i < imageRGB.length; i++)
	  { 
			  int x=0,y=0,z=0,w=0;
			  if(t<elements.length){
				  //pick first three bits and with 255 value.
				  		 x = elements[t] & 0xff;
				  		//make a left 16 bit shift
				  		x = x << 16;
	//value of t increases pick next 3 bits and with 255 value again and then make 8 bit left shift
					  	if(t+1<elements.length){
					  		 y = elements[t+1] & 0xff;
					  		y = y << 8;
					  	}
					  	else{
					  		y=0;
					  	}
					  	//now do t+2 increases pick next 3 bits and with 255 else d will be 0
					  		if(t+2<elements.length){
						  		 w = elements[t+2] & 0xff;
					  		}
					  		else
					  		{w=0;
					  		}
//now OR the values of x, y, w and increase the value of t each time 3 as we try to pick first 3 elements then next 3 each time. 
					  		
					  	  z = x|y|w;
		//doing OR  operation of x, y, z and store in z 
						  t=t+3;
						  // each time the value of t increases by 3.
						   int encodeData = (z & encodeMask) >>> (8 - encodeBits);
						   imageRGB[i] = (imageRGB[i] & hostMask) | (encodeData & ~hostMask);
						  	} 
	  }
	  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
	 }
	 public Image getImage()
	 {
	  return image;
	 }
	 
	// this method simply reading a text from a file which is demo.txt
	 public Steganography(BufferedImage image)
	 {
	  this.image = image;
	  try {
		elements =(Files.readAllBytes(Paths.get("C:\\Users\\Vishal PC\\Documents\\demo.txt")));
	} catch (IOException e) {
		e.printStackTrace();
	}
	 }
	}