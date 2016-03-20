package com.silvermaple.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DrawFreqSpectrum extends JPanel {
	/**
	 * ��������ʱ�̵�Ƶ��ͼ
	 */
	private float[] data = null;
	private long sampleRate = 44100; //ȡĬ��ֵ
	private long FFT_N = 10;
	
	public DrawFreqSpectrum(float[] data, long sampleRate, long FFT_N) {
		this.data = data;
		this.sampleRate = sampleRate;
		this.FFT_N = FFT_N;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		int ww = getWidth();
		int hh = getHeight();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, ww, hh);
		
		int len = data.length;
		int step = len/ww;
		if(step==0)
			step = 1;
		
		g.setColor(Color.blue);
		double k = hh/500.0;
		
		//��n=1��ʼ��n=0Ϊֱ������
		//n=0ʱ������h=data/N
		//n>=1ʱ������h=data/(N/2)
		//Ƶ��Fn=(n-1)Fs/N
		//FsΪ����Ƶ�ʣ�NΪ��������
		float fn = 0;
		float h = 0;
		int n = 1;
		while(fn < 2000){
			fn = (n-1) * (sampleRate / FFT_N);
			h = (float) (data[n] / (FFT_N/2) * k);
			System.out.println("��" + n + "��Ƶ�ʣ�" + fn + " ���ȣ�" + h);
			int y = hh - (int) h;
			if(y < 0) y *= -1;
			y = (int)(y/100000.0);
			int x = (int)(fn/5.0) * step;
			// ÿ�㶼ȡ��������
			// ʵ����Ӧ�ð��ղ����������ü��			
			
			g.drawLine(x, hh, x, y);
			n++;
		}
	}
}
