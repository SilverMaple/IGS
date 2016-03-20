package com.silvermaple.igs;

import java.awt.GridLayout;

import javax.swing.JFrame;

import com.silvermaple.fft.FFT;
import com.silvermaple.ui.DrawFreqSpectrum;
import com.silvermaple.ui.DrawTimeSpectrum;

public class IGS {
	
	public IGS() {
		
	}
	
	public static void main(String[] args) {
		
		String filename = "file.wav";
		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout(2,1));
		WAVReader reader = new WAVReader(filename);
		if(reader.isSuccess()){
			float[] data = reader.getData()[0]; //��ȡ��һ����
			FFT fft = new FFT(9);
			fft.calculate(data);
			for(int i=reader.getDataLength()/2 + 1; i<reader.getDataLength(); i++) {
				data[i] = data[reader.getDataLength()-i];
			}
			
			//����Ƶ��ͼ
			DrawTimeSpectrum drawPanel2 = new DrawTimeSpectrum(data); // ����һ�����Ʋ��ε����
			frame.add(drawPanel2);
			drawPanel2.setAutoscrolls(true);
			
			//����ʱ��ͼ
			DrawFreqSpectrum drawPanel = new DrawFreqSpectrum(data, reader.getSampleRate(), fft.getFFT_N()); // ����һ�����Ʋ��ε����
			frame.add(drawPanel);
			drawPanel.setAutoscrolls(true);
			
			frame.setTitle("ʱ��Ƶ��ͼ");
			frame.setSize(1000, 600);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			
		}
		else{
			System.err.println(filename + "����һ��������wav�ļ�");
		}	
		
	}
}