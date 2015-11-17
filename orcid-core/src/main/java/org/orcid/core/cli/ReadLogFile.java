package org.orcid.core.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public class ReadLogFile {

    public static void main(String[] args) {

        // args[0]: File path
        // args[1 - N]: Look for these words

        if (args == null || args.length == 0)
            return;

        long lineNumber = 0;
        try {
            FileInputStream input = new FileInputStream(new File(args[0]));
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            decoder.onMalformedInput(CodingErrorAction.IGNORE);
            InputStreamReader reader = new InputStreamReader(input, decoder);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            while (line != null) {
                lineNumber += 1;
                for(int i = 1; i < args.length; i++) {
                    if(line.contains(args[i])) {
                        System.out.println(line);
                        break;
                    }
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            System.out.println("Total number of lines: " + lineNumber);
        } catch (Exception e) {
            System.out.println("Exception " + e);
        }
    }

}
