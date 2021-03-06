package hr.fer.zari.midom.utils.decode;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PGMImage {
	private String FilePath;

	// pgm imageheader
	private String Type;
	private String Comment;
	private int Columns, Rows, MaxGray;

	// pgm imagedata
	//private int[][] Pixels;
	private int[] PixelsP;

	// constructors
	public PGMImage() {
		FilePath = "";
		Type = "";
		Comment = "";
		Columns = 0;
		Rows = 0;
		MaxGray = 0;
		PixelsP = null;
	}

	public PGMImage(String tpath) {
		FilePath = tpath;
		readImage();
	}

	public PGMImage(int tColumns, int tRows) {
		FilePath = "";
		Type = "P5";
		Comment = "";
		MaxGray = 255;
		setDimension(tColumns, tRows);
	}

	// get functions

	//public String getFilePath() {
	//	return (FilePath);
	//}

	public String getType() {
		return (Type);
	}

	//public String getComment() {return (Comment);
	//}

	public int getColumns() {
		return (Columns);
	}

	//public int getRows() {
	//	return (Rows);
	//}

	public int getMaxGray() {
		return (MaxGray);
	}

	// 1. test za optimizaciju
	//public int getPixel(int tr, int tc) {
	//	return (tr < 0 || tr > Rows - 1 || tc < 0 || tc > Columns - 1 ? 0
	//			: Pixels[tr][tc]);
	//}

    // 2. test za optimizaciju
	//public int getPixel(int tr, int tc) {
	//	return Pixels[tr][tc];
	//}

    // 3. test za optimizaciju
	//public int getPixel(int pPos) {
	//	return (pPos >= Rows * Columns ? 0 : PixelsP[pPos]);
	//}

    public int getPixel(int pPos) {
        	return (PixelsP[pPos]);
    }

	// set functions
	public void setFilePath(String tFilePath) {
		FilePath = tFilePath;
	}

	public void setType(String tType) {
		Type = tType;
	}

	//public void setComment(String tComment) {
	//	Comment = tComment;
	//}

	public void setDimension(int tColumns, int tRows) {
		Rows = tRows;
		Columns = tColumns;
	//	Pixels = new int[Rows][Columns];
		PixelsP = new int[Rows * Columns];
	}

	public void setMaxGray(int tMaxGray) {
		MaxGray = tMaxGray;
	}

	// 4. test za optimizaciju
	//public void setPixel(int tr, int tc, int tval) {
	//	if (tr < 0 || tr > Rows - 1 || tc < 0 || tc > Columns - 1)
	//		return;
		//else
		//	Pixels[tr][tc] = tval;
	//	PixelsP[tr * Columns + tc] = tval;
	//}

    public void setPixel(int tr, int tc, int tval) {
        //if (tr < 0 || tr > Rows - 1 || tc < 0 || tc > Columns - 1)
        //    return;
        //else
        //	Pixels[tr][tc] = tval;
        PixelsP[tr * Columns + tc] = tval;
    }

	// methods
	public void readImage() {
		try {
			FileInputStream fin = new FileInputStream(FilePath);

			int c;
			String tstr;



			// read first line of ImageHeader
			tstr = "";
			c = fin.read();
			tstr += (char) c;
			c = fin.read();
			tstr += (char) c;
			Type = tstr;

			// read second line of ImageHeader
			c = fin.read(); // read Lf (linefeed)
			c = fin.read(); // read '#'
			tstr = "";
			boolean iscomment = false;
			if ((char) c == '#') // read comment
			{
				iscomment = true;
				tstr += (char) c;
				//while (c != 10 && c != 13) {
					while (c != 48 && c != 49 && c != 50 && c != 51 && c != 52 && c != 53 && c != 54 && c != 55 && c != 56 && c != 57) {
					c = fin.read();
						tstr += (char) c;
						//c = fin.read();

				}
				//c = fin.read();
				// read next '#'
			}
			/*if (tstr.equals("") == false) {
				Comment = tstr.substring(0, tstr.length() - 1);
				//fin.skip(-1);
			}*/

			// read third line of ImageHeader
			// read columns
			tstr = "";
			if (iscomment == true){
				//fin.skip(-1);
				tstr += (char) c;
			c = fin.read();
		}
				//c = fin.read();
			tstr += (char) c;
			while (c != 32 && c != 10 && c != 13) {
				c = fin.read();
				tstr += (char) c;
			}
			tstr = tstr.substring(0, tstr.length() - 1);
			Columns = Integer.parseInt(tstr);

			// read rows
			c = fin.read();
			tstr = "";
			tstr += (char) c;
			while (c != 32 && c != 10 && c != 13) {
				c = fin.read();
				tstr += (char) c;
			}
			tstr = tstr.substring(0, tstr.length() - 1);
			Rows = Integer.parseInt(tstr);

			// read maxgray
			c = fin.read();
			tstr = "";
			tstr += (char) c;
			while (c != 32 && c != 10 && c != 13) {
				c = fin.read();
				tstr += (char) c;
			}
			tstr = tstr.substring(0, tstr.length() - 1);
			MaxGray = Integer.parseInt(tstr);

			// read pixels from ImageData
			//Pixels = new int[Rows][Columns];
			PixelsP = new int[Rows * Columns];
			byte[] buff = new byte[Rows * Columns];
			int n = fin.read(buff);
			if (n != Rows * Columns)
				throw new java.io.IOException("Not enough data");

			for (int tr = 0; tr < Rows; tr++) {
				for (int tc = 0; tc < Columns; tc++){
					setPixel(tr, tc, (int)(buff[tr*Columns + tc] & 0xff));
				}
			}

			fin.close();
		} catch (Exception err) {
			System.out.println("Error: " + err);
			System.exit(-1);
		}
	}

	public void  writeImage() {
		try {
			FileOutputStream fout = new FileOutputStream(FilePath);

			// write image header
			// write PGM magic value 'P5'
			String tstr;
            byte[] PixelB = new byte[PixelsP.length];
			tstr = "P5" + "\n";
			fout.write(tstr.getBytes());

			// write comment
			Comment = Comment + "\n";
			// fout.write(comment.getBytes());

			// write columns
			tstr = Integer.toString(Columns) + "\n";
			fout.write(tstr.getBytes());

			// write rows
			tstr = Integer.toString(Rows) + "\n";
			fout.write(tstr.getBytes());

			// write maxgray
			tstr = Integer.toString(MaxGray);
			tstr = tstr + "\n";
			fout.write(tstr.getBytes());

            //write Pixel Data old
			/*for (int i = 0; i < PixelsP.length; i++){
                Log.e("dekodiranje", String.valueOf(i));
				fout.write(PixelsP[i]);
			}*/

            //write Pixel Data
            for (int i = 0; i < PixelsP.length; i++){
                PixelB[i] = (byte) (PixelsP[i]);
            }
            fout.write(PixelB);
			fout.close();
		} catch (Exception err) {
			System.out.println("Error: " + err);
			System.exit(-1);
		}
	}
}