package com.spartansoftwareinc.ws.okapi.filters.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.ais.WSSystemPropertyKey;
import com.idiominc.wssdk.asset.WSMarkupSegment;
import com.idiominc.wssdk.asset.WSSegment;
import com.idiominc.wssdk.component.filter.WSSegmentReader;
import com.idiominc.wssdk.user.WSLocale;
import net.sf.okapi.common.LocaleId;

public class FilterUtil {

    public static LocaleId getOkapiLocaleId(WSNode content) throws WSAisException, IllegalStateException {
        WSLocale wsLocale = (WSLocale) content.getProperty(WSSystemPropertyKey.LOCALE);
        if (wsLocale != null) {
            if (wsLocale.getLanguage() != null) {
                Locale locale = wsLocale.getLanguage().getLocale();
                return new LocaleId(locale.getLanguage(), locale.getCountry());
            }
        }
        throw new IllegalStateException("Could not find locale in AIS content repository" +
                content.toString());
    }

    public static String detectEncoding(WSNode content, String defaultEncoding) throws WSAisException {
        // AIS will return the java.io canonical name rather than the java.nio canonical name.
        // This matters for UTF-8, which is "UTF8" in java.io and "UTF-8" (which we want)
        // in java.nio.  So we fetch the value and then immediately cycle it through
        // java.nio to get the final name.
        String value = (String)content.getProperty(WSSystemPropertyKey.ENCODING);
        String aisEncodingValue = (value != null) ? Charset.forName(value).name() : null;
        return aisEncodingValue == null ? defaultEncoding : aisEncodingValue;
    }

    /**
     * Read the next segment from a segment reader and return it as a WSMarkupSegment.
     *
     * @param segmentReader - WorldServer segments
     * @return original source AIS content
     */
    public static WSMarkupSegment expectMarkupSegment(WSSegmentReader segmentReader) {
        WSSegment srcAisSegment = segmentReader.read();
        if (srcAisSegment == null || !(srcAisSegment instanceof WSMarkupSegment)) {
            throw new IllegalStateException("Expected markup segment, found " + srcAisSegment);
        }
        return (WSMarkupSegment)srcAisSegment;
    }
/*
    public static File convertAisContentIntoFile(WSNode aisContent) throws IOException,
            WSAisException {
        return convertContentIntoFile(aisContent.getInputStream(), getFileExtension(aisContent.getName()));
    }

    public static File convertContentIntoFile(InputStream is, String extension) throws IOException {
        Path tempFile = Files.createTempFile("wsokapi", getFileExtension(extension));
        Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile.toFile();
    }
*/
    private static String getFileExtension(String path) {
        int i = path.lastIndexOf('.');
        if (i == -1) {
            return "";
        }
        return path.substring(i);
    }

    public static String join(String[] array, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
            sb.append(delimiter);
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
}
