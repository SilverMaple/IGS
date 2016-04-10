package com.silvermaple.igs;

import java.awt.GridLayout;

import javax.swing.JFrame;

import com.silvermaple.fft.FFT;
import com.silvermaple.noteIdentifier.NoteIdentifier;
import com.silvermaple.ui.DrawFreqSpectrum;
import com.silvermaple.ui.DrawTimeSpectrum;

public class IGS {
	
	public IGS() {
		
	}
	
	public static void main(String[] args) {
		
		String filename = "test7.wav";
		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout(2,1));
		WAVReader reader = new WAVReader(filename);
		if(reader.isSuccess()){
			float[] data = reader.getData()[0]; //获取第一声道
			for(int i=0; i<data.length; i++) {
				System.out.println(data[i]);
			}
			
			//画出时域图
			DrawTimeSpectrum drawPanel2 = new DrawTimeSpectrum(data); // 创建一个绘制波形的面板
			frame.add(drawPanel2);
			drawPanel2.setAutoscrolls(true);
			
			FFT fft = new FFT(12);
			fft.calculate(data);
			for(int i=reader.getDataLength()/2 + 1; i<reader.getDataLength(); i++) {
				data[i] = data[reader.getDataLength()-i];
			}
			
			//画出频域图
			DrawFreqSpectrum drawPanel = new DrawFreqSpectrum(data, reader.getSampleRate(), fft.getFFT_N()); // 创建一个绘制波形的面板
			frame.add(drawPanel);
			drawPanel.setAutoscrolls(true);
			
			frame.setTitle("时域频域图");
			frame.setSize(1000, 600);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			
			NoteIdentifier identifier = new NoteIdentifier(reader);
			identifier.identify();
			
		}
		else{
			System.err.println(filename + "不是一个正常的wav文件");
		}	
	}
}
