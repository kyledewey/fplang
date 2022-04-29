package fp_example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import fp_example.lexer.Tokenizer;
import fp_example.lexer.TokenizerException;
import fp_example.parser.Parser;
import fp_example.parser.ParseException;

public class Compiler {
    public static String fileContentsAsString(final String inputFilename) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
        String line = null;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }
        return builder.toString();
    }

    public static void main(final String[] args)
        throws IOException,
               TokenizerException,
               ParseException {
        System.out.println(Parser.parseProgram(Tokenizer.tokenize(fileContentsAsString(args[0]))));
    }
}
