//Golomb-Rice Coder
//
//Encoder saves code to FILENAME
//Decoder decodes from FILENAME to integer array
//
//Recommended integer range [-255, 255]
//
//
//Usage: "Usage: Coder  e|d FILENAME"
//////////////////////////////////////////////////////
package hr.fer.zari.midom.utils.decode;
import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;


public class GRCoder {
	//Coder parameters:
	int k;
	int m;
	boolean sedmi = false, sesti = false, peti = false, cetvrti = false, treci = false, drugi = false, prvi = false, nulti = false;
	static int MAX_K_FACTOR = 7;
	static int scaleLimit = 128;
	static int RESCALE_FACTOR = 8;
	int symbolCnt;
	int errorSum;

	static int fileHeight;
	static int fileWidth;
	static int fileMaxPixel;
	byte [] fileBytes;

	@SuppressLint("NewApi") public  void encode (int []fileIntegers, String NEW_FILENAME){
		//ENCODER MODE:	
		try {
			FileOutputStream fout = new FileOutputStream(NEW_FILENAME);

			fileWidth = fileIntegers[0];
			fileHeight = fileIntegers[1];
			fileMaxPixel = fileIntegers [2];

			//to encode:
			fileIntegers = Arrays.copyOfRange (fileIntegers, 3, fileIntegers.length);

			BitSet outSet = new BitSet();
			int outLength = 0;
			int outIndex = 0;
			symbolCnt = 0;
			errorSum = 0;
			k = 0;

			for (int focus : fileIntegers){
				k = getOptimalParamK();
				m = (int) Math.pow(2, k);
				BitSet focusCode = encode (focus);
				for (int i = 0; i< codeLength (focus); i++){
					if (focusCode.get(i)) outSet.set(outIndex);
					outIndex++;
				}
				update(Math.abs(focus));
				outLength += codeLength (focus);
			}

			//write header:
			byte[] integerData = ByteBuffer.allocate(4).putInt(fileWidth).array();
			fout.write(integerData);

			integerData = ByteBuffer.allocate(4).putInt(fileHeight).array();
			fout.write(integerData);

			integerData = ByteBuffer.allocate(4).putInt(fileMaxPixel).array();
			fout.write(integerData);

			//write code:
			byte [] outArray = new byte [(outLength+7)/8];
			System.arraycopy (outSet.toByteArray(), 0, outArray, 0, outSet.toByteArray().length);

			for(int i=0; i < outArray.length; i++){
				if (((outArray[i] >> 7) & 1) == 1) {sedmi = true;}else {sedmi = false;}
				if (((outArray[i] >> 6) & 1) == 1){ sesti = true;}else {sesti = false;}
				if (((outArray[i] >> 5) & 1) == 1) {peti = true; }else{ peti = false;}
				if (((outArray[i] >> 4) & 1) == 1) {cetvrti = true;}else {cetvrti = false;}
				if (((outArray[i] >> 3) & 1) == 1) {treci = true; }else {treci = false;}
				if (((outArray[i] >> 2) & 1) == 1) {drugi = true;}else {drugi = false;}
				if (((outArray[i] >> 1) & 1) == 1) {prvi = true;}else {prvi = false;}
				if (((outArray[i]) & 1) == 1) {nulti = true; }else {nulti = false;}

				if (sedmi) {outArray[i] |= 1;}else{outArray[i] &= ~(1);}
				if (sesti) {outArray[i] |= 1 << 1;}else{outArray[i] &= ~(1 << 1);}
				if (peti) {outArray[i] |= 1 << 2;}else{outArray[i] &= ~(1 << 2);}
				if (cetvrti){ outArray[i] |= 1 << 3;}else{outArray[i] &= ~(1 << 3);}
				if (treci) {outArray[i] |= 1 << 4;}else{outArray[i] &= ~(1 << 4);}
				if (drugi){ outArray[i] |= 1 << 5;}else{outArray[i] &= ~(1 << 5);}
				if (prvi) {outArray[i] |= 1 << 6;}else{outArray[i] &= ~(1 << 6);}
				if (nulti) {outArray[i] |= 1 << 7;}else{outArray[i] &= ~(1 << 7);}
			}
			fout.write(outArray);
			fout.close();
		}catch (Exception err) {
			System.out.println("Error: " + err);
			System.exit(-1);
		}}

	@SuppressLint("NewApi") public  int[] decode (String FILENAME) {
		try {
			File file = new File(FILENAME);
			FileInputStream fin = new FileInputStream(file);
			//fileBytes = IOUtils.toByteArray(fin);

			fileBytes = new byte[(int) file.length()];
			fin.read(fileBytes);

			//Get header:
			byte [] integerData = Arrays.copyOfRange(fileBytes, 0, 4);
			fileWidth = ByteBuffer.wrap(integerData).getInt();

			integerData = Arrays.copyOfRange(fileBytes, 4, 8);
			fileHeight = ByteBuffer.wrap(integerData).getInt();

			integerData = Arrays.copyOfRange(fileBytes, 8, 12);
			fileMaxPixel = ByteBuffer.wrap(integerData).getInt();


			//To be decoded:
			fin.close();
		}catch (Exception err) {
			System.out.println("Error: " + err);
			System.exit(-1);
		}
		int[] output = new int [fileWidth*fileHeight + 3];
		output[0] = fileWidth;
		output[1] = fileHeight;
		output[2] = fileMaxPixel;
		fileBytes = Arrays.copyOfRange (fileBytes,12, fileBytes.length);

		byte [] lookup = new byte[256];
		for (int i=0; i< lookup.length; i++){
			lookup[i] = calculateReverse((byte)(i-128));
		}

		Log.e("TEST", "Krecemo");

		for(int i=0; i < fileBytes.length; i++){
//			if (((fileBytes[i] >> 7) & 1) == 1) {sedmi = true;}else {sedmi = false;}
//			if (((fileBytes[i] >> 6) & 1) == 1){ sesti = true;}else {sesti = false;}
//			if (((fileBytes[i] >> 5) & 1) == 1) {peti = true; }else{ peti = false;}
//			if (((fileBytes[i] >> 4) & 1) == 1) {cetvrti = true;}else {cetvrti = false;}
//			if (((fileBytes[i] >> 3) & 1) == 1) {treci = true; }else {treci = false;}
//			if (((fileBytes[i] >> 2) & 1) == 1) {drugi = true;}else {drugi = false;}
//			if (((fileBytes[i] >> 1) & 1) == 1) {prvi = true;}else {prvi = false;}
//			if (((fileBytes[i] >> 0) & 1) == 1) {nulti = true; }else {nulti = false;}
//
//			if (sedmi) {fileBytes[i] |= 1;}else{fileBytes[i] &= ~(1 << 0);}
//			if (sesti) {fileBytes[i] |= 1 << 1;}else{fileBytes[i] &= ~(1 << 1);}
//			if (peti) {fileBytes[i] |= 1 << 2;}else{fileBytes[i] &= ~(1 << 2);}
//			if (cetvrti){ fileBytes[i] |= 1 << 3;}else{fileBytes[i] &= ~(1 << 3);}
//			if (treci) {fileBytes[i] |= 1 << 4;}else{fileBytes[i] &= ~(1 << 4);}
//			if (drugi){ fileBytes[i] |= 1 << 5;}else{fileBytes[i] &= ~(1 << 5);}
//			if (prvi) {fileBytes[i] |= 1 << 6;}else{fileBytes[i] &= ~(1 << 6);}
//			if (nulti) {fileBytes[i] |= 1 << 7;}else{fileBytes[i] &= ~(1 << 7);}

			try {
				fileBytes[i] = lookup[fileBytes[i]+128];
			}catch (Exception e){
				Log.e ("REVERSE", e.getMessage());
			}
		}

		Log.e("TEST", "GOTOVI");

		BitSet inSet = BitSet.valueOf (fileBytes);
		int unary =0;
		int binary = 0;
		symbolCnt = 0;
		errorSum = 0;
		k = 0;

		boolean binaryPart = false;
		int i= 0;
		for (int count= 0; count < fileWidth*fileHeight; count++){
			if (inSet.get(i) && !binaryPart){
				i++;
				count--;
				unary++;
				continue;
			}
			k = getOptimalParamK();
			m = (int) Math.pow(2, k);
			binaryPart = true;
			i++; //skip delimiter
			for (int j=0; j<k; j++){
				if (inSet.get(i+j))
					binary += (int) Math.pow (2,k-1-j);
			}
			i += k;
			int value = unary*m + binary;
			value = demap (value);
			output[3+count] = value;
			update(Math.abs(value));
			unary = 0;
			binary = 0;
			binaryPart = false;
		}
		return output;
	}

	private byte calculateReverse(byte bits) {
		byte ret = bits;
		ret = (byte)((ret & 0b11110000) >> 4 | (ret & 0b00001111) << 4);
		ret = (byte)((ret & 0b11001100) >> 2 | (ret & 0b00110011) << 2);
		ret = (byte)((ret & 0b10101010) >> 1 | (ret & 0b01010101) << 1);
		return ret;
	}

	static int map (int value) {
		int mappedValue;
		if (value < 0)
			mappedValue = (value * 2 + 1) * (-1);
		else
			mappedValue = value *2;
		return mappedValue;
	}

	static int demap (int value){
		int demmapedValue;
		if (value % 2 == 0)
			demmapedValue = value /2;
		else
			demmapedValue = (value + 1) / (-2);
		return demmapedValue;
	}

	BitSet encode (int value){
		int mappedValue = map (value);
		int unary = mappedValue/m;
		int binary = mappedValue % m;
		BitSet code = new BitSet(unary + 1 + k);
		for (int i=0; i<unary; i++) code.set(i);
		String binaryPart = Integer.toBinaryString(binary);
		for (int j=0; j < binaryPart.length(); j++)
			if (binaryPart.charAt(j) == '1') code.set (unary+1+j+(k - binaryPart.length()));
		return code;
	}

	int codeLength (int value){
		int mappedValue = map (value);
		int length = mappedValue/m + 1 + k;
		return length;
	}

	int getOptimalParamK (){
		return 2;
		/*
		int i = 0;

		for (i = 0; (symbolCnt << i) < errorSum; i++){
			if (i > MAX_K_FACTOR){
				throw new Error("Cooky Monster " + i);
				//return 9;
			}
		}
		return i;
		*/
	}

	void update (int absError){
		errorSum += absError;
		symbolCnt++;
		if( symbolCnt >= scaleLimit){
			rescale();
		}
	}

	void rescale(){
		errorSum /= RESCALE_FACTOR;
		symbolCnt /= RESCALE_FACTOR;
		if ( symbolCnt == 0 ){
			symbolCnt = 1;
		}
	}


}
