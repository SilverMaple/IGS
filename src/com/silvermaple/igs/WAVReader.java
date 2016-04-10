package com.silvermaple.igs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class WAVReader {

	//��wav�ļ������и�����������ռλ�����ж���
	//begin
	private String chunkDescriptor = null; //"RIFF"��ʶ��
	static private int lenChunkDescriptor = 4;
	
	private long chunkSize = 0; //��ȥ"RIFF"��ʶ�����ļ��Ĵ�С
	static private int lenChunkSize = 4;
	
	private String waveFlag = null; //"WAVE"��ʶ��
	static private int lenWaveFlag = 4;
	
	private String fmtSubChunk = null; //"fmt"��ʶ��
	static private int lenFmtSubChunk = 4;
	
	private long subChunk1Size = 0; //��ȥ"fmt"��ʶ�����chunk�Ĵ�С��ȡֵ16��18����2λ������Ϣ��
	static private int lenSubChunk1Size = 4;
	
	private int audioFormat = 0; //���뷽ʽ��һ��Ϊ0X0001
	static private int lenAudioFormat = 2;
	
	private int numChannels = 0; //������Ŀ
	static private int lenNumChannels = 2;
	
	private long sampleRate = 0; //����Ƶ��
	static private int lenSampleRate = 2;
	
	private long byteRate = 0; //ÿ�������ֽ���
	static private int lenByteRate = 4;
	
	private int blockAlign = 0; //���ݿ���뵥λ(ÿ��������Ҫ���ֽ���)
	static private int lenBlockAlign = 2;
	
	private int bitsPerSample = 0; //ÿ�����������bit��
	static private int lenBitsPerSample = 2;
	
	private String dataSubChunk = null; //data chunk��ʶ��
	static private int lenDataSubChunk = 4;
	
	private long subChunk2Size = 0; //����"data"��ʶ�����chunk�Ĵ�С
	static private int lenSubChunk2Size = 4;
	//end
	//��wav�ļ������и�����������ռλ�����ж���
	
	
	private String filename = null;
	private float[][] data = null;
	private int length = 0;
	
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private boolean issuccess = false; //��Ϊ��������ı�ʾ
	
	public WAVReader(String filename) {
		this.read(filename);
	}
	
	// �ж��Ƿ��ȡWAV�ļ��ɹ�
	public boolean isSuccess() {
		return issuccess;
	}
	
	// ��ȡÿ�������ı��볤�ȣ�8bit����16bit
	public int getBitPerSample() {
		return this.bitsPerSample;
	}
	
	// ��ȡ������
	public long getSampleRate() {
		return this.sampleRate;
	}
	
	// ��ȡ����������1��ʾ��������2��ʾ˫����
	public int getNumChannels() {
		return this.numChannels;
	}
	
	// ��ȡ���ݳ��ȣ�����������
	public int getDataLength() {
		return this.length;
	}
	
	// ��ȡ����
	// ������һ����ά���飬 [n][m]�����n�������ĵ�m������ֵ
	public float[][] getData() {
		return this.data;
	}

	/**
	 * 
	 * @param filename
	 * 		ʶ��������ļ� 
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
				System.out.println("�������ļ���dataSubChunk����data��β��");
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
			// �������ֵ���൱��buf[1]*2^4 + buf[0]
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
			// �������ֵ����readInt
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
			 * 32�������ĸ�ʽΪ
			 * 1λ������λ��8λ����λ��23λβ��λ
			 * SEEE EEEE EMMM MMMM MMMM MMMM MMMM MMMM
			 * buf[3]    buf[2]    buf[1]    buf[0]
			 */
			long M = buf[0] | (buf[1] << 8) | ((buf[2] & 0x0000007F) << 16); //�����β��
			int E = ((buf[2] & 0x00000080) >> 7) | ((buf[3] & 0x0000007F) << 1); //���������
			int S = (buf[3] & 0x00000080) >> 7; //���������
			res = (-1) * S * 2^(E - 127) * (2^24 + M); //�ָ�float��ֵ
		} catch (IOException e) {
			
		}
		return res;
	}
}
