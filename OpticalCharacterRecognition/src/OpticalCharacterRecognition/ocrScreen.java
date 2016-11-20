package OpticalCharacterRecognition;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.io.PrintStream;



public class ocrScreen {

	private JFrame frame;
	private JTextField textField;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ocrScreen window = new ocrScreen();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	
	public ocrScreen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 612, 398);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(40, 35, 412, 23);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblEnterThePath = new JLabel("Enter the path of Test Image");
		lblEnterThePath.setBounds(37, 11, 186, 23);
		lblEnterThePath.setFont(new Font("Tahoma", Font.BOLD, 11));
		frame.getContentPane().add(lblEnterThePath);
		
		JLabel lblRecognizedText = new JLabel("Recognized Text");
		lblRecognizedText.setBounds(44, 130, 105, 29);
		lblRecognizedText.setFont(new Font("Tahoma", Font.BOLD, 11));
		frame.getContentPane().add(lblRecognizedText);
		
		JButton btnExecute = new JButton("Train from starting");
		btnExecute.setBounds(62, 81, 155, 23);
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
			    String s;
			    s=textField.getText();
				NeuralNetwork nw = new NeuralNetwork(s,1);
				try {
					nw.NeuralFunction();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		frame.getContentPane().add(btnExecute);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(40, 168, 502, 147);
		textArea.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
		frame.getContentPane().add(textArea);
		
		JButton btnUseAlreadyTrained = new JButton("Use already trained fonts");
		btnUseAlreadyTrained.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String s;
			    s=textField.getText();
				NeuralNetwork nw = new NeuralNetwork(s,0);
				try {
					nw.NeuralFunction();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		btnUseAlreadyTrained.setBounds(233, 81, 191, 23);
		frame.getContentPane().add(btnUseAlreadyTrained);
		
		System.setOut(printStream);
        System.setErr(printStream);
 
		
	}
}


