package de.noobisoft.powerpong.util;

import org.apache.log4j.Logger;

import de.bht.jvr.math.Matrix4;

/**
 * A helper class to transform several objects into other
 * 
 * @author Lars George
 * @autho Chris Krauﬂ
 * 
 */
public class TransformHelper
{

	static Logger	logger	= Logger.getLogger(TransformHelper.class);

	/**
	 * Converting a String to a Matrix (needs to be in the format, that is given
	 * by the method MatrixToNetworkString)
	 * 
	 * @param str
	 *            the string that needs to be converted
	 * @return a matrix representation of the given string
	 */
	public static Matrix4 NetworkStringToMatrix(String str)
	{
		Matrix4 ret = null;
		try
		{
			String[] values = str.split(",");

			ret = new Matrix4(Integer.parseInt(values[0]) / 100.0f,
					Integer.parseInt(values[1]) / 100.0f,
					Integer.parseInt(values[2]) / 100.0f,
					Integer.parseInt(values[3]) / 100.0f,
					Integer.parseInt(values[4]) / 100.0f,
					Integer.parseInt(values[5]) / 100.0f,
					Integer.parseInt(values[6]) / 100.0f,
					Integer.parseInt(values[7]) / 100.0f,
					Integer.parseInt(values[8]) / 100.0f,
					Integer.parseInt(values[9]) / 100.0f,
					Integer.parseInt(values[10]) / 100.0f,
					Integer.parseInt(values[11]) / 100.0f,
					Integer.parseInt(values[12]) / 100.0f,
					Integer.parseInt(values[13]) / 100.0f,
					Integer.parseInt(values[14]) / 100.0f,
					Integer.parseInt(values[15]) / 100.0f);
		}
		catch (Exception e)
		{
			logger.error("Could not transform the given string to a matrix: "
					+ str + "\n" + e.toString());
		}

		return ret;
	}

	/**
	 * transforms the given matrix into a string, that can be send via the
	 * network
	 * 
	 * @param matrix
	 *            the matrix that needs to be converted
	 * @return the matrix elements seperated by a ','
	 */
	public static String MatrixToNetworkString(Matrix4 matrix)
	{
		return (int) (matrix.get(0, 0) * 100) + ","
				+ (int) (matrix.get(0, 1) * 100) + ","
				+ (int) (matrix.get(0, 2) * 100) + ","
				+ (int) (matrix.get(0, 3) * 100) + ","
				+ (int) (matrix.get(1, 0) * 100) + ","
				+ (int) (matrix.get(1, 1) * 100) + ","
				+ (int) (matrix.get(1, 2) * 100) + ","
				+ (int) (matrix.get(1, 3) * 100) + ","
				+ (int) (matrix.get(2, 0) * 100) + ","
				+ (int) (matrix.get(2, 1) * 100) + ","
				+ (int) (matrix.get(2, 2) * 100) + ","
				+ (int) (matrix.get(2, 3) * 100) + ","
				+ (int) (matrix.get(3, 0) * 100) + ","
				+ (int) (matrix.get(3, 1) * 100) + ","
				+ (int) (matrix.get(3, 2) * 100) + ","
				+ (int) (matrix.get(3, 3) * 100);
	}
}
