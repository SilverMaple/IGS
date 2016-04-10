package com.silvermaple.igs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class WAVReader {

	//对wav文件内容中各个参数及其占位数进行定义
	//begin
	private String chunkDescriptor = null; //"RIFF"标识符
	static private int lenChunkDescriptor = 4;
	
	private long chunkSize = 0; //除去"RIFF"标识符后文件的大小
	static private int lenChunkSize = 4;
	
	private String waveFlag = null; //"WAVE"标识符
	static private int lenWaveFlag = 4;
	
	private String fmtSubChunk = null; //"fmt"标识符
	static private int lenFmtSubChunk = 4;
	
	private long subChunk1Size = 0; //除去"fmt"标识符后此chunk的大小，取值16或18（加2位附加信息）
	static private int lenSubChunk1Size = 4;
	
	private int audioFormat = 0; //编码方式，一般为0X0001
	static private int lenAudioFormat = 2;
	
	private int numChannels = 0; //声道数目
	static private int lenNumChannels = 2;
	
	private long sampleRate = 0; //采样频率
	static private int lenSampleRate = 2;
	
	private long byteRate = 0; //每秒所需字节数
	static private int lenByteRate = 4;
	
	private int blockAlign = 0; //数据块对齐单位(每个采样需要的字节数)
	static private int lenBlockAlign = 2;
	
	private int bitsPerSample = 0; //每个采样所需的bit数
	static private int lenBitsPerSample = 2;
	
	private String dataSubChunk = null; //data chunk标识符
	static private int lenDataSubChunk = 4;
	
	private long subChunk2Size = 0; //除了"data"标识符后此chunk的大小
	static private int lenSubChunk2Size = 4;
	//end
	//对wav文件内容中各个参数及其占位数进行定义
	
	
	private String filename = null;
	private float[][] data = null;
	private int length = 0;
	
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private boolean issuccess = false; //作为操作结果的表示
	
	public WAVReader(String filename) {
		this.read(filename);
	}
	
	// 判断是否读取WAV文件成功
	public boolean isSuccess() {
		return issuccess;
	}
	
	// 获取每个采样的编码长度，8bit或者16bit
	public int getBitPerSample() {
		return this.bitsPerSample;
	}
	
	// 获取采样率
	public long getSampleRate() {
		return this.sampleRate;
	}
	
	// 获取声道个数，1表示单声道，2表示双声道
	public int getNumChannels() {
		return this.numChannels;
	}
	
	// 获取数据长度，即采样点数
	public int getDataLength() {
		return this.length;
	}
	
	// 获取数据
	// 数据是一个二维数组， [n][m]代表第n个声道的第m个采样值
	public float[][] getData() {
		return this.data;
	}

	/**
	 * 
	 * @param filename
	 * 		识别的音乐文件 
	 */
	private void read(String filename) {
		this.filename = filename;
		
		try {
			fis = new FileInputStream(this.filename);
			bis = new BufferedInputStream(fis);
			
			this.chunkDescriptor = readString(lenChunkDescriptor);
			if(!chunkDescriptor.endsWith("RIFF"))
				throw new IllegalArgumentException("RIFF miss, " + filename + "is not a wave file.");
			
			this.chunkSize = readLong();
			this.waveFlag = readString(lenWaveFlag);
			if(!waveFlag.endsWith("WAVE"))
				throw new IllegalArgumentException("WAVE miss, " + filename + "is not a wave file.");
			
			this.fmtSubChunk = readString(lenFmtSubChunk);
			if(!fmtSubChunk.endsWith("fmt "))
				throw new IllegalArgumentException("fmt miss, " + filename + "is not a wave file.");

			this.subChunk1Size = readLong();
			this.audioFormat = readInt();
			this.numChannels = readInt();
			this.sampleRate = readLong();
			this.byteRate = readLong();
			this.blockAlign = readInt();
			this.bitsPerSample = readInt();
			
			this.dataSubChunk = readString(lenDataSubChunk);
			if(!dataSubChunk.endsWith("data")) {
				//throw new IllegalArgumentException("data miss, " + filename + " is not a wave file.");
				System.out.println("此音乐文件的dataSubChunk不以data结尾！");
			}

			this.subChunk2Size = readLong();
			this.length = (int)(this.subChunk2Size/(this.bitsPerSample/8)/this.numChannels);
			this.data = new float [this.numChannels][this.length];
			
			if(this.bitsPerSample == 8) {
				for(int i=0; i<this.length; i++) {
					for(int n=0; n<this.numChannels; n++) {
						this.data[n][i] = bis.read();
					}
				}
			} else if(this.bitsPerSample == 16) {
				for(int i=0; i<this.length; i++) {
					for(int n=0; n<this.numChannels; n++) {
						try {
							this.data[n][i] = this.readInt(); 
						} catch (Exception e) {
							break;
						}
					}
				}
			} 
			else if(this.bitsPerSample == 32) {
				for(int i=0; i<this.length; i++) {
					for(int n=0; n<this.numChannels; n++) {
						this.data[n][i] = this.readFloat();
					}
				}
			}
			
			issuccess = true;
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(bis != null) bis.close();
				if(fis != null) fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String readString(int len) {
		byte[] buf = new byte[len];
		try {
			if(bis.read(buf) != len) {
				throw new IOException("no more data for string!");
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new String(buf);
	}
	
	private int readInt() {
		byte[] buf = new byte[2];
		int res = 0;
		try {
			if(bis.read(buf) != 2) {
				//System.out.println(getBitPerSample() + " " + getDataLength() + " " + getNumChannels());
				throw new IOException("no more data for int!");
			}
			// 计算出数值，相当于buf[1]*2^4 + buf[0]
			res = (buf[0] & 0x000000FF) | (((int)buf[1])<<8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private long readLong() {
		long res = 0;
		try {
			long[] l = new long[4];
			for(int i=0; i<4; i++) {
				l[i] = bis.read();
				if(l[i] == -1)
					throw new IOException("no more data!");
			}
			// 计算出数值，如readInt
			res = l[0] | (l[1]<<8) | (l[2]<<16) | (l[3]<<24);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private float readFloat() {
		byte[] buf = new byte[4];
		float res = 0;
		try {
			if(bis.read(buf) != 4) {
				throw new IOException("no more data for float!");
			}
			/**
			 * 32浮点数的格式为
			 * 1位符号数位，8位阶数位，23位尾数位
			 * SEEE EEEE EMMM MMMM MMMM MMMM MMMM MMMM
			 * buf[3]    buf[2]    buf[1]    buf[0]
			 */
			long M = buf[0] | (buf[1] << 8) | ((buf[2] & 0x0000007F) << 16); //计算出尾数
			int E = ((buf[2] & 0x00000080) >> 7) | ((buf[3] & 0x0000007F) << 1); //计算出阶数
			int S = (buf[3] & 0x00000080) >> 7; //计算出符号
			res = (-1) * S * 2^(E - 127) * (2^24 + M); //恢复float数值
		} catch (IOException e) {
			
		}
		return res;
	}
}
