import java.awt.*;
import java.io.*;

public class BMPFile extends Component
{

    // --- Private constants
    private final static int BITMAPFILEHEADER_SIZE = 14;
    private final static int BITMAPINFOHEADER_SIZE = 40;

    // --- Private variable declaration

    // --- Bitmap file header
    private byte bitmapFileHeader[] = new byte[14];
    private byte bfType[] = { (byte) 'B', (byte) 'M' };
    private int bfSize = 0;
    private int bfReserved1 = 0;
    private int bfReserved2 = 0;
    private int bfOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE + 1024;

    // --- Bitmap info header
    private byte bitmapInfoHeader[] = new byte[40];
    private int biSize = BITMAPINFOHEADER_SIZE;
    private int biWidth = 0;
    private int biHeight = 0;
    private int biPlanes = 1;
    private int biBitCount = 8;
    private int biCompression = 0;
    private int biSizeImage = 0x030000;
    private int biXPelsPerMeter = 0x0;
    private int biYPelsPerMeter = 0x0;
    private int biClrUsed = 256;
    private int biClrImportant = 256;

    // --- Bitmap raw data
    private byte bitmap[];

    // --- File section
    private FileOutputStream fo;

    // --- Default constructor
    public BMPFile()
    {

    }

    public void saveBitmap(String parFilename, byte[] imagePix, int parWidth, int parHeight)
    {

	try
	{

	    fo = new FileOutputStream(parFilename);
	    save(imagePix, parWidth, parHeight);
	    fo.close();
	} catch (Exception saveEx)
	{
	    saveEx.printStackTrace();
	}

    }

    /*
     * The saveMethod is the main method of the process. This method will call
     * the convertImage method to convert the memory image to a byte array;
     * method writeBitmapFileHeader creates and writes the bitmap file header;
     * writeBitmapInfoHeader creates the information header; 
     */
    public void save(byte[] imagePix, int parWidth, int parHeight)
    {

	try
	{
	    convertImage(imagePix, 640, 480);
	    writeBitmapFileHeader();
	    writeBitmapInfoHeader();
	    
	    //functionality to convert raw data into 8 bit gray scale image
	    byte[] rgbquad = new byte[4];
	    for (int i = 0; i < 256; i++)
	    {

		rgbquad[0] = rgbquad[1] = rgbquad[2] = (byte) i;
		rgbquad[3] = (byte) 0;
		fo.write(rgbquad);
	    }

	    byte[] bmp = new byte[imagePix.length];

	    for (int row = 0; row < 480; row++)
	    {
		for (int col = 0; col < 640; col++)
		{
		    bmp[(479 - row) * 640 + col] = imagePix[row * 640 + col];
		}

	    }

	    fo.write(bmp);

	} catch (Exception saveEx)
	{
	    saveEx.printStackTrace();
	}
    }

    /*
     * convertImage converts the memory image to the bitmap format (BRG). It
     * also computes some information for the bitmap info header.
     */
    private boolean convertImage(byte[] imagePix, int parWidth, int parHeight)
    {

	bitmap = imagePix;
	biSizeImage = ((parWidth * parHeight));
	bfSize = biSizeImage + BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE + 1024;
	biWidth = parWidth;
	biHeight = parHeight;
	return (true);
    }

    /*
     * writeBitmapFileHeader writes the bitmap file header to the file.
     */
    private void writeBitmapFileHeader()
    {

	try
	{

	    fo.write(bfType);
	    fo.write(intToDWord(bfSize));
	    fo.write(intToWord(bfReserved1));
	    fo.write(intToWord(bfReserved2));
	    fo.write(intToDWord(bfOffBits));

	} catch (Exception wbfh)
	{
	    wbfh.printStackTrace();
	}

    }

    /*
     * 
     * writeBitmapInfoHeader writes the bitmap information header to the file.
     */

    private void writeBitmapInfoHeader()
    {

	try
	{
	    fo.write(intToDWord(biSize));
	    fo.write(intToDWord(biWidth));
	    fo.write(intToDWord(biHeight));
	    fo.write(intToWord(biPlanes));
	    fo.write(intToWord(biBitCount));
	    fo.write(intToDWord(biCompression));
	    fo.write(intToDWord(biSizeImage));
	    fo.write(intToDWord(biXPelsPerMeter));
	    fo.write(intToDWord(biYPelsPerMeter));
	    fo.write(intToDWord(biClrUsed));
	    fo.write(intToDWord(biClrImportant));
	} catch (Exception wbih)
	{
	    wbih.printStackTrace();
	}

    }

    /*
     * 
     * intToWord converts an int to a word, where the return value is stored in
     * a 2-byte array.
     */
    private byte[] intToWord(int parValue)
    {

	byte retValue[] = new byte[2];

	retValue[0] = (byte) (parValue & 0x00FF);
	retValue[1] = (byte) ((parValue >> 8) & 0x00FF);

	return (retValue);

    }

    /*
     * 
     * intToDWord converts an int to a double word, where the return value is
     * stored in a 4-byte array.
     */
    private byte[] intToDWord(int parValue)
    {

	byte retValue[] = new byte[4];
	retValue[0] = (byte) (parValue & 0x00FF);
	retValue[1] = (byte) ((parValue >> 8) & 0x000000FF);
	retValue[2] = (byte) ((parValue >> 16) & 0x000000FF);
	retValue[3] = (byte) ((parValue >> 24) & 0x000000FF);

	return (retValue);

    }

}
