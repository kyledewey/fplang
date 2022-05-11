package fp_example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

import fp_example.lexer.Tokenizer;
import fp_example.lexer.TokenizerException;
import fp_example.parser.Parser;
import fp_example.parser.Program;
import fp_example.parser.ParseException;
import fp_example.typechecker.Typechecker;
import fp_example.typechecker.TypeErrorException;
import fp_example.code_generator.CodeGenerator;
import fp_example.code_generator.CodeGeneratorException;

public class Compiler {
    public static void printUsage() {
        System.out.println("Takes the following params:");
        System.out.println("-Input filename (.fp)");
        System.out.println("-Output filename (.js)");
    }

    public static String fileContentsAsString(final String inputFilename) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            return builder.toString();
        } finally {
            reader.close();
        }
    }

    public static void compile(final String inputFilename,
                               final String outputFilename)
        throws IOException,
               TokenizerException,
               ParseException,
               TypeErrorException,
               CodeGeneratorException {
        final String input = fileContentsAsString(inputFilename);
        final Program program = Parser.parseProgram(Tokenizer.tokenize(input));
        final PrintWriter output =
            new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));
        try {
            Typechecker.assertProgramTypechecks(program);
            CodeGenerator.generateCode(program, output);
        } finally {
            output.close();
        }
    }
               
    public static void main(final String[] args)
        throws IOException,
               TokenizerException,
               ParseException,
               TypeErrorException,
               CodeGeneratorException {
        if (args.length != 2) {
            printUsage();
        } else {
            compile(args[0], args[1]);
        }
    }
}
