import os
import shutil
import subprocess
import tempfile
import re

# GitHub repository URL and directory path
GITHUB_REPO = "https://github.com/wizhippo/fldigi-flrig.git"
SRC_DIR = "src/rigs"
OUTPUT_FILE = "set_vfoA_extracted.txt"

# Regular expression to match "set_vfoA" as a function name, accounting for class/namespace qualifiers
FUNCTION_NAME_REGEX = re.compile(r"([a-zA-Z_][a-zA-Z0-9_:]*)::set_vfoA\s*\([^)]*\)\s*\{", re.MULTILINE)


def clone_repo(temp_dir):
    """Clones the GitHub repository to a temporary directory."""
    repo_dir = os.path.join(temp_dir, "fldigi-flrig")
    subprocess.run(["git", "clone", GITHUB_REPO, repo_dir], check=True)
    return repo_dir


def extract_functions(src_dir, output_file):
    """Extracts all set_vfoA functions from source files and saves them to a single file."""
    extracted_count = 0

    def extract_function(content, start_index):
        """Manually extracts the function body by tracking braces."""
        brace_count = 0
        end_index = start_index
        while end_index < len(content):
            char = content[end_index]
            if char == "{":
                brace_count += 1
            elif char == "}":
                brace_count -= 1
                if brace_count == 0:
                    break
            end_index += 1
        return content[start_index:end_index + 1]

    with open(output_file, "w", encoding="utf-8") as out_f:
        for root, _, files in os.walk(src_dir):
            for file in files:
                if file.endswith((".cxx", ".c", ".cpp")):
                    file_path = os.path.join(root, file)
                    print(f"Processing {file_path}")
                    with open(file_path, "r", encoding="utf-8") as f:
                        content = f.read()
                        for match in FUNCTION_NAME_REGEX.finditer(content):
                            print(f"Found 'set_vfoA' in {file}: {match.group(0)}")
                            start_brace = content.find("{", match.start())
                            if start_brace == -1:
                                continue
                            function_body = extract_function(content, start_brace)
                            out_f.write(f"# Extracted from: {file}\n")
                            out_f.write(function_body + "\n\n")
                            extracted_count += 1
    print(f"Total functions extracted: {extracted_count}")


def cleanup(temp_dir):
    """Deletes the temporary directory."""
    shutil.rmtree(temp_dir)
    print("Temporary files cleaned up.")


def main():
    try:
        # Create a temporary directory
        temp_dir = tempfile.mkdtemp()
        print(f"Created temporary directory: {temp_dir}")

        # Clone the repository
        repo_dir = clone_repo(temp_dir)
        print(f"Repository cloned to: {repo_dir}")

        # Source directory within the cloned repo
        src_dir = os.path.join(repo_dir, SRC_DIR)

        # Extract functions
        print(f"Extracting functions to {OUTPUT_FILE}...")
        extract_functions(src_dir, OUTPUT_FILE)

    except Exception as e:
        print(f"An error occurred: {e}")

    finally:
        # Clean up temporary files
        cleanup(temp_dir)


if __name__ == "__main__":
    main()


