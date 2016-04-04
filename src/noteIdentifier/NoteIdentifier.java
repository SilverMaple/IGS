package noteIdentifier;

import com.silvermaple.fft.FFT;
import com.silvermaple.igs.WAVReader;

public class NoteIdentifier {
	
	private WAVReader reader;
	private FFT fft;
	private int N;
	
	public NoteIdentifier(WAVReader reader) {
		this.reader = reader;
		fft = new FFT(12);
		N = 4096;
	}
	
	public void identify() {
		float[] data = reader.getData()[0]; //获得第一声道数据
		int index=0; //记录汉明窗的初始位置
		float[] temp = new float[4096];
		System.out.println(data.length);
		while(index + 4096 < data.length) {
			System.arraycopy(data, index, temp, 0, 4096);
			index += 4096;
			temp = hamming(temp); //加汉明窗处理
			fft.calculate(temp); //fft处理
			for(int i=temp.length/2 + 1; i<temp.length; i++) { //还原另一半数组
				temp[i] = temp[temp.length-1-i];
			}
			String tone = basicTone(temp);
			System.out.print(tone + " ");
		}
		
	}
	
	/**
	 * 
	 * @param data 
	 *		一小段声音的信号数组
	 * @return 
	 * 		返回汉明窗处理后的信号数组
	 */
	public float[] hamming(float[] data) {
		for(int n = 0; n<N; n++) {
			data[n] *= (float)(0.54 - 0.46*Math.cos(2*Math.PI*n/(N-1)));
		}
		return data;
	}
	
	/**
	 * 
	 * @param range 
	 * 		经过分帧，加汉明窗处理，再FFT处理得到的短时频率数组
	 * @return 
	 * 		返回识别出的音符表示
	 */
	public String basicTone(float[] range) {
		String tone = " ";
		int index = 0;
		for(int n=0; n<range.length; n++) {
			if(range[index]<range[n]) 
				index = n;
		}
		float freq = (float)((index-1) * (reader.getSampleRate() * 1.0 / fft.getFFT_N()));;
		if(freq < 0 || freq > 4000) {
			tone = "none";
			return tone;
		}
		int low = 0;   
        int high = Tone.freq.length-1;   
        int middle = 0;
        while(low <= high) {   
            middle = (low + high)/2;   
            if(freq == Tone.freq[middle]) {   
                break; 
            }else if(freq < Tone.freq[middle]) {   
                high = middle - 1;   
            }else {   
                low = middle + 1;   
            }  
        }  
        if(middle > 0 && middle < Tone.freq.length-1) {
	        if(Math.abs(Tone.freq[middle-1]-freq) < Math.abs(Tone.freq[middle]-freq)) {
	        	middle -= 1;
	        } else if(Math.abs(Tone.freq[middle+1]-freq) < Math.abs(Tone.freq[middle]-freq)) {
	        	middle += 1;
	        }
        }
        System.out.println(index + " " + freq);
		tone = Tone.tone[middle];
		return tone;
	}
}
