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
		
		//��n=1��ʼ��n=0Ϊֱ������
		//n=0ʱ������h=data/N
		//n>=1ʱ������h=data/(N/2)
		//Ƶ��Fn=(n-1)Fs/N
		//FsΪ����Ƶ�ʣ�NΪ��������
		float fn = 0;
		float h = 0;
		int n = 1;
		while(fn < 2000 && n < data.length){
			fn = (float)((n-1) * (sampleRate * 1.0 / FFT_N)); //��N�����Ƶ��
			h = (float) (Math.abs(data[n]) / (FFT_N/2)); 
			int y = (int) (20*Math.log10(h)); //�ɹ�ʽ������ֱ���С
			//System.out.println("��" + n + "��Ƶ�ʣ�" + fn + " ���ȣ�" + h + " �ֱ���" + y);
			y = hh - y;
			int x = (int)(fn/5.0) * step;
			// ÿ�㶼ȡ��������
			// ʵ����Ӧ�ð��ղ����������ü��			
			
			g.drawLine(x, hh, x, y);
			n++;
		}
		System.out.println(FFT_N);
	}
}
