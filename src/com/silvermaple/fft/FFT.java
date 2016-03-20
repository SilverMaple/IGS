package com.silvermaple.fft;

public class FFT {
	public final int FFT_N_LOG; //FFT_N_LOG<=13
	public final int FFT_N;
	private final float MINY;
	private final float[] real, imag, sintable, costable;
	private final int[] bitReverse;
	
	public FFT(int FFT_N_LOG) {
		this.FFT_N_LOG = FFT_N_LOG;
		FFT_N = 1 << FFT_N_LOG;
		MINY = (float) ((FFT_N << 2) * Math.sqrt(2));
		real = new float[FFT_N];
		imag = new float[FFT_N];
		sintable = new float[FFT_N >> 1];
		costable = new float[FFT_N >> 1];
		bitReverse = new int[FFT_N];
		
		//���������������н��е�λ��任����������ʵ��,Ϊ�������ݵĽ����ṩ����
		int i, j, k, reve;
		for(i=0; i<FFT_N; i++) {
			k = i;
			for(j=0, reve=0; j!=FFT_N_LOG; j++) {
				reve <<= 1;
				reve |= (k&1);
				k >>>= 1;
			}
			bitReverse[i] = reve; //�������Ӧ��λ�������±�
			//System.out.println(bitReverse[i]);
		}
		
		//�����ÿ�����ȶ�Ӧ��sinֵ��cosֵ
		double theta, dt = 2 * Math.PI / FFT_N;
		for(i=0; i<(FFT_N >> 1); i++) {
			theta = i * dt;
			costable[i] = (float) Math.cos(theta);
			sintable[i] = (float) Math.sin(theta);
		}
	}
	
	/**
	 * ���ٸ���Ҷ�任
	 * 
	 * @param reallO
	 * 		����FFT_N��ʵ����Ҳ�����ݴ�fft���FFT_N/2�����ֵ������ģ��ƽ����,��һ��Գ�
	 */
	public void calculate(float[] reallO) {
		int i, j, k, ir, exchanges = 1, idx = FFT_N_LOG - 1;
		float cosv, sinv, tmpr, tmpi;
		//�����ݽ��е���
		for(i=0; i!=FFT_N; i++) {
			real[i] = reallO[bitReverse[i]];
			imag[i] = 0;
		}
		//i�׵���ͼ����
		for(i=FFT_N_LOG; i!=0; i--) {
			//j�׵�����
			for(j=0; j!=exchanges; j++) {
				//����Ӧ�����Ǻ���ֵ��ȡ����
				cosv = costable[j << idx];
				sinv = sintable[j << idx];
				//�������е�k������
				for(k=j; k<FFT_N; k+=exchanges << 1) {
					ir = k + exchanges;
					tmpr = cosv * real[ir] - sinv * imag[ir];
					tmpi = cosv * imag[ir] + sinv * real[ir];
					real[ir] = real[k] - tmpr;
					imag[ir] = imag[k] - tmpr;
					real[k] += tmpr;
					imag[k] += tmpi;
				}
			}
			exchanges <<= 1;
			idx--;
		}
		
		j = FFT_N >> 1;
			
		/**
		 * ���ģ��ƽ������FFT_N������
		 * for(i=1; i<=j; i++)
		 * reallO[i-1] = real[i] * real[i] + imag[i] * imag[i];
		 */
		sinv = MINY;
		cosv = -MINY;
		for(i=j; i!=0; i--) {
			tmpr = real[i];
			tmpi = imag[i];
			if(tmpr > cosv && tmpr <sinv && tmpi> cosv && tmpi < sinv) {
				reallO[i-1] = 0;
			}
			else {
				reallO[i-1] = tmpr * tmpr + tmpi * tmpi;
			}
		}
	
	}
	
	public long getFFT_N() {
		return FFT_N;
	}
		
//	public static void main(String[] args) {
//		FFT fft = new FFT();
//		float[] reallo = {185, 184, 183, 185, 188, 189, 186, 180, 173, 167, 165, 173, 194, 205, 209, 206, 202, 198, 195, 190, 225, 250, 236, 191, 166, 155, 153, 157, 162, 168, 170, 165};
//		fft.calculate(reallo);
//		for(int i=0; i<reallo.length; i++) 
//			System.out.println(reallo[i]);
//	}
}







