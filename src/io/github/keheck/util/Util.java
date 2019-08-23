package io.github.keheck.util;

import org.apache.commons.lang3.ArrayUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Util
{
    public static void safeClose(Closeable closeable)
    {
        if(closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
                Log.e("Failed to close: " + closeable.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Returns an array holding all indices of occurences of {@code toSearch}
     * in {@code src}
     *
     * @param src the source string
     * @param toSearch the string to look out for
     * @return an {@code int[]} holding all indices of the occurence,
     *         {@code null} if no occurence could be found
     */
    public static int[] getAllIndicesOf(String src, String toSearch)
    {
        if(!src.contains(toSearch)) return new int[]{-1};

        ArrayList<Integer> indices = new ArrayList<>();

        for(int i = src.indexOf(toSearch); i != -1; i = src.indexOf(toSearch, i+1)) indices.add(i);

        int[] primitiveIndices = ArrayUtils.toPrimitive(indices.toArray(new Integer[0]));
        Arrays.sort(primitiveIndices);
        return primitiveIndices;
    }

    public static int[] getAllIndicesOf(String src, char toSearch)
    {
        return getAllIndicesOf(src, new String(new char[]{toSearch}));
    }
}
