# Hack Virtual Machine Translator

This Java package, `cpp.hackvm.Translator`, provides functionality to translate Hack Virtual Machine (VM) code to assembly language.

## Usage

To use the Translator, follow these steps:

1. **Compile the Code**: Compile the Java files in the package using mvn

2. **Running the Translator**:

   Run the `Translator` class with appropriate command-line arguments. The first argument should be the path to the input file or directory containing VM code.

   Example:
   ```bash
   java cpp.hackvm.Translator path/to/input_file_or_directory
   ```

   If a directory is provided, the Translator will process all `.vm` files inside.

3. **Output**:

   The translated assembly code will be generated in a file with the same name as the input file (if it's a single file) or with the name of the directory and a `.asm` extension.

   If an output file with the same name already exists, the Translator will prompt whether to replace it.

## Input Files

The Translator processes Hack VM code files (`.vm`) containing VM instructions.

## Translator Flow

1. Initialize the Translator with input and output files.
2. Parse and process the VM code.
3. Generate corresponding Hack assembly code.
4. Write the assembly code to the output file.

## Classes

### `Translator`

- The main class responsible for coordinating the translation process.
- Accepts command-line arguments for input file or directory.

### `VMCoder`

- A module for writing Hack assembly code.
- Provides methods for writing various VM commands to the output file.

### `VMParser`

- Parses VM code and provides information about each command.
- Used by the Translator to interpret VM instructions.



## Note

- This package assumes valid VM code as input. Ensure that the provided VM code adheres to the Hack VM specification. Error Handling is work in progress.


