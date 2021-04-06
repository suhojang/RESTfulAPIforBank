package com.kwic.security.aes;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * Aes 128/192/256 암호화
 * 
 * security.js와 동일한 알고리즘을 사용
 * 
 * java <--> javascript간의 암복호화에 적용이가능하나...
 * 
 * javascript에 seed key값을 넘겨주고 복호화하는건 자살골 넣는거니 자제해야겟지??
 * 
 * 2014.03.10
 * 장정훈
 * */
public class JSecurity {
	public static final String CRYPT_KEY_ID	= "JSecurity.KEY";
	/**
	 * str : plain text
	 * key : seed key
	 * bits : 128/192/256
	 * */
	public static String encrypt(String str, String key,int bits) throws Exception{
		if(bits!=128 && bits!=192 && bits!=256){
			throw new Exception("Unsupported bits ["+bits+"]. Now select in 128/192/256.");
		}
		return JAes.encrypt(str, key, bits);
	}
	public static String encrypt(String str, String key) throws Exception{
		return JAes.encrypt(str, key, 256);
	}
	public static String decrypt(String str, String key,int bits) throws Exception{
		if(bits!=128 && bits!=192 && bits!=256){
			throw new Exception("Unsupported bits ["+bits+"]. Now select in 128/192/256.");
		}
		return JAes.decrypt(str, key, bits);
	}
	public static String decrypt(String str, String key) throws Exception{
		return JAes.decrypt(str, key, 256);
	}
}

class JAes{
	public static final int[] sBox =  {0x63,0x7c,0x77,0x7b,0xf2,0x6b,0x6f,0xc5,0x30,0x01,0x67,0x2b,0xfe,0xd7,0xab,0x76,
	             0xca,0x82,0xc9,0x7d,0xfa,0x59,0x47,0xf0,0xad,0xd4,0xa2,0xaf,0x9c,0xa4,0x72,0xc0,
	             0xb7,0xfd,0x93,0x26,0x36,0x3f,0xf7,0xcc,0x34,0xa5,0xe5,0xf1,0x71,0xd8,0x31,0x15,
	             0x04,0xc7,0x23,0xc3,0x18,0x96,0x05,0x9a,0x07,0x12,0x80,0xe2,0xeb,0x27,0xb2,0x75,
	             0x09,0x83,0x2c,0x1a,0x1b,0x6e,0x5a,0xa0,0x52,0x3b,0xd6,0xb3,0x29,0xe3,0x2f,0x84,
	             0x53,0xd1,0x00,0xed,0x20,0xfc,0xb1,0x5b,0x6a,0xcb,0xbe,0x39,0x4a,0x4c,0x58,0xcf,
	             0xd0,0xef,0xaa,0xfb,0x43,0x4d,0x33,0x85,0x45,0xf9,0x02,0x7f,0x50,0x3c,0x9f,0xa8,
	             0x51,0xa3,0x40,0x8f,0x92,0x9d,0x38,0xf5,0xbc,0xb6,0xda,0x21,0x10,0xff,0xf3,0xd2,
	             0xcd,0x0c,0x13,0xec,0x5f,0x97,0x44,0x17,0xc4,0xa7,0x7e,0x3d,0x64,0x5d,0x19,0x73,
	             0x60,0x81,0x4f,0xdc,0x22,0x2a,0x90,0x88,0x46,0xee,0xb8,0x14,0xde,0x5e,0x0b,0xdb,
	             0xe0,0x32,0x3a,0x0a,0x49,0x06,0x24,0x5c,0xc2,0xd3,0xac,0x62,0x91,0x95,0xe4,0x79,
	             0xe7,0xc8,0x37,0x6d,0x8d,0xd5,0x4e,0xa9,0x6c,0x56,0xf4,0xea,0x65,0x7a,0xae,0x08,
	             0xba,0x78,0x25,0x2e,0x1c,0xa6,0xb4,0xc6,0xe8,0xdd,0x74,0x1f,0x4b,0xbd,0x8b,0x8a,
	             0x70,0x3e,0xb5,0x66,0x48,0x03,0xf6,0x0e,0x61,0x35,0x57,0xb9,0x86,0xc1,0x1d,0x9e,
	             0xe1,0xf8,0x98,0x11,0x69,0xd9,0x8e,0x94,0x9b,0x1e,0x87,0xe9,0xce,0x55,0x28,0xdf,
	             0x8c,0xa1,0x89,0x0d,0xbf,0xe6,0x42,0x68,0x41,0x99,0x2d,0x0f,0xb0,0x54,0xbb,0x16};

	public static final int[][] rCon = { {0x00, 0x00, 0x00, 0x00},
	             {0x01, 0x00, 0x00, 0x00},
	             {0x02, 0x00, 0x00, 0x00},
	             {0x04, 0x00, 0x00, 0x00},
	             {0x08, 0x00, 0x00, 0x00},
	             {0x10, 0x00, 0x00, 0x00},
	             {0x20, 0x00, 0x00, 0x00},
	             {0x40, 0x00, 0x00, 0x00},
	             {0x80, 0x00, 0x00, 0x00},
	             {0x1b, 0x00, 0x00, 0x00},
	             {0x36, 0x00, 0x00, 0x00} }; 
	
	public static final int[] cipher(int[] input,int[][] w){
		  int Nb = 4;
		  int Nr = w.length/Nb - 1; 

		  int[][] state = new int[4][4*Nb/4];
		  for (int i=0; i<4*Nb; i++) {
			  state[i%4][i/4] = input[i];
		  }
		  state = addRoundKey(state, w, 0, Nb);

		  for (int round=1; round<Nr; round++) {
		    state = subBytes(state, Nb);
		    state = shiftRows(state, Nb);
		    state = mixColumns(state, Nb);
		    state = addRoundKey(state, w, round, Nb);
		  }

		  state = subBytes(state, Nb);
		  state = shiftRows(state, Nb);
		  state = addRoundKey(state, w, Nr, Nb);

		  int[] output = new int[4*Nb];
		  for (int i=0; i<4*Nb; i++) {
			  output[i] = state[i%4][(int)Math.floor(i/4)];
		  }
		  return output;
	}
	public static final int[][] keyExpansion(int[] key) {
		int Nb = 4;
		int Nk = key.length/4;
		int Nr = Nk + 6;

		int[][] w = new int[Nb*(Nr+1)][];
		int[] temp = new int[4];

		for (int i=0; i<Nk; i++) {
			int[] r = {key[4*i], key[4*i+1], key[4*i+2], key[4*i+3]};
			w[i] = r;
		}

		for (int i=Nk; i<(Nb*(Nr+1)); i++) {
			w[i] = new int[4];
			for (int t=0; t<4; t++) {
				temp[t] = w[i-1][t];
			}
			if (i % Nk == 0) {
				temp = subWord(rotWord(temp));
				for (int t=0; t<4; t++) {
					temp[t] ^= JAes.rCon[i/Nk][t];
				}
			} else if (Nk > 6 && i%Nk == 4) {
				temp = subWord(temp);
			}
			for (int t=0; t<4; t++) {
				w[i][t] = w[i-Nk][t] ^ temp[t];
			}
		}
		return w;
	}
	
	public static final int[][] addRoundKey (int[][]state, int[][] w, int rnd, int Nb) {
		for (int r=0; r<4; r++) {
			for (int c=0; c<Nb; c++) {
				state[r][c] ^= w[rnd*4+c][r];
			}
		}
		return state;
	}
	
	public static final int[][] subBytes(int[][] s, int Nb) {
		for (int r=0; r<4; r++) {
			for (int c=0; c<Nb; c++) {
				s[r][c] = JAes.sBox[s[r][c]];
			}
		}
		return s;
	}
		
	public static final int[][] shiftRows(int[][]s, int Nb) {
		int[] t = new int[4];
		for (int r=1; r<4; r++) {
			for (int c=0; c<4; c++) {
				t[c] = s[r][(c+r)%Nb];
			}
			for (int c=0; c<4; c++) {
				s[r][c] = t[c];
			}
		}          
		return s;  
	}

	public static final int[][] mixColumns(int[][] s, int Nb) {  
		for (int c=0; c<4; c++) {
			int[] a = new int[4];  
			int[] b = new int[4]; 
			for (int i=0; i<4; i++) {
				a[i] = s[i][c];
				b[i] = (s[i][c] &0x80)!=0 ? s[i][c]<<1 ^ 0x011b : s[i][c]<<1;
			}
			s[0][c] = b[0] ^ a[1] ^ b[1] ^ a[2] ^ a[3];
			s[1][c] = a[0] ^ b[1] ^ a[2] ^ b[2] ^ a[3];
			s[2][c] = a[0] ^ a[1] ^ b[2] ^ a[3] ^ b[3];
			s[3][c] = a[0] ^ b[0] ^ a[1] ^ a[2] ^ b[3];
		}
		return s;
	}
	public static final int[] rotWord(int[] w) {   
		int tmp = w[0];
		for (int i=0; i<3; i++) {
			w[i] = w[i+1];
		}
		w[3] = tmp;
		return w;
	}
	public static final int[] subWord(int[] w) {   
		for (int i=0; i<4; i++) {
			w[i] = JAes.sBox[w[i]];
		}
		return w;
	}
	public static final boolean isNaN(String str) {
		Pattern pattern = Pattern.compile("[+-]?\\d+");
		return pattern.matcher(str).matches();
	}
	public static final char[] concat(char[] w1,char[] w2){
		char[] s	= new char[w1.length+w2.length];
		System.arraycopy(w1,0,s,0,w1.length);
		System.arraycopy(w2,0,s,w1.length,w2.length);
		return s;
	}
	public static final int[] concat(int[] w1,int[] w2){
		int[] s	= new int[w1.length+w2.length];
		System.arraycopy(w1,0,s,0,w1.length);
		System.arraycopy(w2,0,s,w1.length,w2.length);
		return s;
	}
	public static final char[] slice(char[] w,int sIdx,int eIdx){
		char[] s	= new char[eIdx-sIdx];
		System.arraycopy(w,sIdx,s,0,s.length);
		return s;
	}
	public static final int[] slice(int[] w,int sIdx,int eIdx){
		int[] s	= new int[eIdx-sIdx];
		System.arraycopy(w,sIdx,s,0,s.length);
		return s;
	}
	public static final String join(int[][] w){
		StringBuffer sb	= new StringBuffer();
		for(int i=0;i<w.length;i++){
			for(int j=0;j<w[i].length;j++){
				sb.append((char)w[i][j]);
			}
		}
		return sb.toString();
	}
	public static final String join(int[] w){
		StringBuffer sb	= new StringBuffer();
		for(int i=0;i<w.length;i++){
			sb.append((char)w[i]);
		}
		return sb.toString();
	}
	public static final String join(char[][] w){
		StringBuffer sb	= new StringBuffer();
		for(int i=0;i<w.length;i++){
			if(w[i]==null){
				continue;
			}
			for(int j=0;j<w[i].length;j++){
				sb.append(w[i][j]);
			}
		}
		return sb.toString();
	}
	public static final String join(char[] w){
		StringBuffer sb	= new StringBuffer();
		for(int i=0;i<w.length;i++){
			sb.append(w[i]);
		}
		return sb.toString();
	}
	public static final String join(String[] w){
		StringBuffer sb	= new StringBuffer();
		for(int i=0;i<w.length;i++){
			if(w[i]==null){
				continue;
			}
			sb.append(w[i]);
		}
		return sb.toString();
	}

	public static final String encrypt(String plaintext, String password, int nBits) throws Exception {
		int blockSize = 16;
		if (!(nBits==128 || nBits==192 || nBits==256)) {
			return "";
		}
		plaintext = new String(plaintext.getBytes("UTF-8"),"ISO-8859-1");
		
		password = new String(password.getBytes("UTF-8"),"ISO-8859-1");
		int nBytes = nBits/8;
		int[] pwBytes = new int[nBytes];
		for (int i=0; i<nBytes; i++) {
			pwBytes[i] = password.length()<=i ? 0 : (int)password.charAt(i);
		}
		
		int[] key = cipher(pwBytes, keyExpansion(pwBytes));
		key = JAes.concat(key,JAes.slice(key,0, nBytes-16));
		
		int[] counterBlock = new int[blockSize];
		long nonce = new java.util.Date().getTime();
		long nonceSec = nonce/1000;
		long nonceMs = nonce%1000;
		for (int i=0; i<4; i++) {
			counterBlock[i] = (int) ((nonceSec >>> i*8) & 0xff);
		}
		for (int i=0; i<4; i++) {
			counterBlock[i+4] = (int) (nonceMs & 0xff);
		}

		String ctrTxt = "";
		for (int i=0; i<8; i++) {
			ctrTxt	= ctrTxt.concat(String.valueOf((char)counterBlock[i]));
		}
		int[][] keySchedule = keyExpansion(key);
		
		int blockCount = (int) Math.ceil((double)plaintext.length()/(double)blockSize);
		String[] ciphertxt = new String[blockCount];

		for (int b=0; b<blockCount; b++) {
			for (int c=0; c<4; c++) {
				counterBlock[15-c] = (b >>> c*8) & 0xff;
			}
			for (int c=0; c<4; c++) {
				counterBlock[15-c-4] = (int) (b/0x100000000L >>> c*8);
			}

			int[] cipherCntr = cipher(counterBlock, keySchedule);
	
			int blockLength = b<blockCount-1 ? blockSize : (plaintext.length()-1)%blockSize+1;
			char[] cipherChar = new char[blockLength];
	
			for (int i=0; i<blockLength; i++) {
				cipherChar[i] = (char) (cipherCntr[i] ^ ((int)plaintext.charAt(b*blockSize+i)));
			}
			ciphertxt[b] = join(cipherChar);
		}

		String ciphertext = ctrTxt + join(ciphertxt);
		return JBase64.encode(ciphertext,false);
	}
	
	public static final String decrypt(String ciphertextstr, String password, int nBits) throws Exception{
		int blockSize = 16;
		if (!(nBits==128 || nBits==192 || nBits==256)) {
			return ""; 
		}
		
		String ciphertext = JBase64.decode(ciphertextstr,false);
		password = new String(password.getBytes("UTF-8"),"ISO-8859-1");
		
		int nBytes = nBits/8; 
		int[] pwBytes = new int[nBytes];
		for (int i=0; i<nBytes; i++) {
			pwBytes[i] = password.length()<=i ? 0 : (int)password.charAt(i);
		}
		
		int[] key = cipher(pwBytes, keyExpansion(pwBytes));
		key = JAes.concat(key,JAes.slice(key,0, nBytes-16));
		
		int[] counterBlock = new int[16];
		String ctrTxt = ciphertext.substring(0,8);
		
		for (int i=0; i<8; i++) {
			counterBlock[i] = (int)ctrTxt.charAt(i);
		}
		  
		int[][] keySchedule = keyExpansion(key);

		int nBlocks = (int) Math.ceil((double)(ciphertext.length()-8) / (double)blockSize);
		char[][] ct = new char[nBlocks][];
		for (int b=0; b<nBlocks; b++) {
			if(ciphertext.length()>8+b*blockSize+blockSize){
				ct[b] = ciphertext.substring(8+b*blockSize, 8+b*blockSize+blockSize).toCharArray();
			}else{
				ct[b] = ciphertext.substring(8+b*blockSize).toCharArray();
			}
		}
		
		String[] plaintxt = new String[ct.length];

		for (int b=0; b<nBlocks; b++) {
			for (int c=0; c<4; c++) {
				counterBlock[15-c] = ((b) >>> c*8) & 0xff;
			}
			for (int c=0; c<4; c++) {
				counterBlock[15-c-4] = (int) (
							(
								(
									(long)(((double)(b+1))/0x100000000L-1D)
								) >>> c*8
							) & 0xff
						);
			}
			int[] cipherCntr = cipher(counterBlock, keySchedule); 
		    char[] plaintxtByte = new char[ct[b].length];
		    for (int i=0; i<ct[b].length; i++) {
		    	plaintxtByte[i] = (char) (cipherCntr[i] ^ (int)ct[b][i]);
		    }
		    plaintxt[b] = JAes.join(plaintxtByte);
		  }

		  String plaintext = JAes.join(plaintxt);
		  plaintext = new String(plaintext.getBytes("ISO-8859-1"),"UTF-8");

		  return plaintext;
	}
}


class JBase64{
//	public static final String code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	public static final String code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	
	public static final String encode(String str, boolean utf8encode) throws Exception{ 
		int o1, o2, o3, c, bits, h1, h2, h3, h4	= 0;
		String[] e = null;
		String pad = "", coded	= "";
		String b64 = JBase64.code;
		String plain = utf8encode ? URLEncoder.encode(str,"UTF-8") : str;
		  
		c = plain.length() % 3; 
		if (c > 0) { 
			while (c++ < 3) { 
				pad += "="; 
				plain += "\0"; 
		  	}
		}
		e	= new String[plain.length()/3];
		for (c=0; c<plain.length(); c+=3) { 
			o1 = (int)plain.charAt(c);
		    o2 = (int)plain.charAt(c+1);
		    o3 = (int)plain.charAt(c+2);
		      
		    bits = o1<<16 | o2<<8 | o3;
		      
		    h1 = bits>>18 & 0x3f;
		    h2 = bits>>12 & 0x3f;
		    h3 = bits>>6 & 0x3f;
		    h4 = bits & 0x3f;

		    e[c/3] = String.valueOf(b64.charAt(h1)) + String.valueOf(b64.charAt(h2)) + String.valueOf(b64.charAt(h3)) + String.valueOf(b64.charAt(h4));
		}
		coded = JAes.join(e); 

		coded = coded.substring(0, coded.length()-pad.length()) + pad;
		   
		return coded;
	}

	public static final String decode(String str, boolean utf8decode) throws Exception {
		int o1, o2, o3, bits, h1, h2, h3, h4	= 0;
		String[] d = null;
		String coded	= "", plain = "";

		String b64 = JBase64.code;
		coded = utf8decode ? URLDecoder.decode(str,"UTF-8") : str;
		  
		d	= new String[coded.length()];  
		
		for (int c=0; c<coded.length(); c+=4) { 
			h1 = b64.indexOf(coded.charAt(c));
			h2 = b64.indexOf(coded.charAt(c+1));
			h3 = b64.indexOf(coded.charAt(c+2));
			h4 = b64.indexOf(coded.charAt(c+3));
		      
			bits = h1<<18 | h2<<12 | h3<<6 | h4;
		      
			o1 = bits>>>16 & 0xff;
			o2 = bits>>>8 & 0xff;
			o3 = bits & 0xff;
		    
			d[c/4] = JAes.join(new int[]{o1, o2, o3});

			if (h4 == 0x40) {
				d[c/4] = JAes.join(new int[]{o1, o2});
			}
			if (h3 == 0x40) {
				d[c/4] = JAes.join(new int[]{o1});
			}
		}
		plain = JAes.join(d); 
		   
		return utf8decode ? URLDecoder.decode(plain,"UTF-8") : plain; 
	}
}
