package home.echyrski.xml.processing.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

import org.apache.xerces.impl.io.UTF8Reader;

import com.google.common.base.Charsets;


/**
 *
 */
public class IOUtils {

    public static byte[] extractCharactersFromStream(InputStream is, long skip, int size) throws IOException {
        UTF8Reader reader = new UTF8Reader(is);
        while (skip > 0) {
            skip -= reader.skip(skip);
        }
        CharBuffer charbuffer = CharBuffer.allocate(size);
        while (charbuffer.remaining() > 0) {
            int read = reader.read(charbuffer);
            if (read == -1) {
                throw new IOException("End of stream!");
            }
        }
        return toBytes(charbuffer.array());
    }


    public static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charsets.UTF_8.encode(charBuffer);
        return Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
    }
}
