package com.silvermaple.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DrawFreqSpectrum extends JPanel {
	/**
	 * 画出给定时刻的频谱图
	 */
	private float[] data = null;
	private long sampleRate = 44100; //取默认值
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
		
		//从n=1开始，n=0为直流分量
		//n=0时，幅度h=data/N
		//n>=1时，幅度h=data/(N/2)
		//频率Fn=(n-1)Fs/N
		//Fs为采样频率，N为采样点数
		float fn = 0;
		float h = 0;
		int n = 1;
		while(fn < 2000 && n < data.length){
			fn = (float)((n-1) * (sampleRate * 1.0 / FFT_N)); //第N个点的频率
			h = (float) (Math.abs(data[n]) / (FFT_N/2)); 
			int y = (int) (20*Math.log10(h)); //由公式计算出分贝大小
			//System.out.println("第" + n + "个频率：" + fn + " 幅度：" + h + " 分贝：" + y);
			y = hh - y;
			int x = (int)(fn/5.0) * step;
			// 每点都取出并绘制
			// 实际中应该按照采样率来设置间隔			
			
			g.drawLine(x, hh, x, y);
			n++;
		}
		System.out.println(FFT_N);
	}
}
