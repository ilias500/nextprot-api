package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

import java.util.Arrays;

/**
 * Code and decode 36-base 8-length string in the interval of longs [0:36^8[
 *
 * Created by fnikitin on 11/03/15.
 */
public class Base36Codec {

    private static final long LOWER_RANGE = 0;
    private static final long UPPER_RANGE = 2_821_109_900_000L; // 36^8

    private static final String BASE36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    private Base36Codec() {
        throw new IllegalAccessError("Utility class not meant to be instantiated");
    }

    /**
     * @return the minimum included long
     */
    public static long getLowerBound() {

        return LOWER_RANGE;
    }

    /**
     * @return the maximum excluded long
     */
    public static long getUpperBound() {

        return UPPER_RANGE;
    }

    /**
     * Encode long in a base-36 string.
     *
     * @param value the long value to encode
     * @return base-36 string
     * @throws IndexOutOfBoundsException if value not in range [0, 2_821_109_900_000[
     */
    public static String encodeBase36(final long value) {

        if (value < 0 || value >= UPPER_RANGE) {
            throw new IndexOutOfBoundsException("cannot encode " + value + ": long out of bound (valid range: [0, 2_821_109_900_000[)");
        }

        char[] buffer = new char[8];

        Arrays.fill(buffer, '0');

        int i=7;

        long tmp = value;

        while(tmp > 0 && i>=0) {

            long remainder = tmp % 36;

            buffer[i] = BASE36.charAt((int)remainder);

            tmp = tmp/36;

            i--;
        }

        return String.copyValueOf(buffer);
    }

    /**
     * Decode a base-36 string in long
     *
     * @param base36String the string to decode in long
     * @return the decoded long
     * @throws java.lang.IllegalArgumentException if base36String length is too long (&gt; 8)
     * @throws NumberFormatException if not a base-36 encoded string
     * @throws IndexOutOfBoundsException if the decoded long is out of range [0, 2_821_109_900_000[
     */
    public static long decodeBase36(String base36String) {

        Preconditions.checkNotNull(base36String);
        Preconditions.checkArgument(base36String.length()<=8);

        long decodedLong = Long.parseLong(base36String, 36);

        if(decodedLong < 0 || decodedLong >= UPPER_RANGE) {
            throw new IndexOutOfBoundsException("cannot decode " + decodedLong + ": long out of bound (valid range: [0, 2_821_109_900_000[)");
        }

        return decodedLong;
    }
}
