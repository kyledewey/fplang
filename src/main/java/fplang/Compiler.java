package fplang;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

import fplang.lexer.Tokenizer;
import fplang.lexer.TokenizerException;
import fplang.parser.Parser;
import fplang.parser.Program;
import fplang.parser.ParseException;
import fplang.typechecker.Typechecker;
import fplang.typechecker.TypeErrorException;
import fplang.codegen.CodeGenerator;
import fplang.codegen.CodeGeneratorException;

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
