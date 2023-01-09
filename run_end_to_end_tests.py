#!/usr/bin/env python3

import os
import subprocess

COMPILED_FILE = "output.js"

def file_output(filename):
    with open(filename) as file:
        retval = ""
        for line in file:
            retval += line
        return retval

# returns false if it failed to compile
def compile(filename):
    p = subprocess.run(["mvn",
                        "exec:java",
                        "-Dexec.mainClass=fplang.Compiler",
                        "-Dexec.args={} {}".format(filename, COMPILED_FILE)],
                       stdout=subprocess.DEVNULL,
                       stderr=subprocess.DEVNULL)
    return True if p.returncode == 0 else False

def run_test(fplang_file, expected_output_file):
    if not compile(fplang_file):
        print("{}: FAILED TO COMPILE".format(fplang_file))
    else:
        expected = file_output(expected_output_file)
        received = subprocess.check_output(["node", COMPILED_FILE]).decode('utf-8')
        if expected != received:
            print("{}: FAILED".format(fplang_file))
            print("\tExpected: {}".format(expected))
            print("\tReceived: {}".format(received))
        else:
            print("{}: passed".format(fplang_file))

def basename(name):
    index = name.rfind(".")
    if index == -1:
        return name
    else:
        return name[:index]
    
def run_tests_in_directory(directory):
    for filename in os.listdir(directory):
        if filename.endswith(".fplang"):
            result_filename = "{}.txt".format(basename(filename))
            result_path = "{}/{}".format(directory, result_filename)
            if os.path.exists(result_path):
                run_test("{}/{}".format(directory, filename),
                         result_path)

if __name__ == "__main__":
    run_tests_in_directory("examples")
