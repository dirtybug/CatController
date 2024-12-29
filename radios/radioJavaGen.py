import os
import re

# File containing the extracted set_vfoA functions
EXTRACTED_FILE = "set_vfoA_extracted.txt"
OUTPUT_DIR = "generated_classes"
DRIVERS_DIR = os.path.join(OUTPUT_DIR, "drivers")
ENUM_FILE = "RadioType.java"
FACTORY_FILE = "RadioFactory.java"
CLASS_NAME_REGEX = re.compile(r"# Extracted from: ([a-zA-Z0-9_]+)\.cxx")


def sanitize_radio_name(name):
    """Sanitizes the radio name by removing invalid characters."""
    return name.replace("-", "_")  # Replace '-' with '_' or remove entirely using .replace("-", "")


def create_java_class(class_name, function_body):
    """Creates a Java class file with the given function body."""
    class_code = f"""
package com.Runner.CQMiau.radios.drivers;
import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

public class {class_name} extends RadioBase {{

    public {class_name}() {{
        super();
        super.Max = 54000000L;
        super.Min = 3000L;
    }}

    @Override
    public void setFrequency(String freq) {{
        // Function body extracted from {class_name}.cxx
        {function_body.strip()}
    }}

    @Override
    public void setMode(RadioMode mode) {{
        // Placeholder for setMode implementation
        System.out.println("Mode set: " + mode);
    }}
}}
"""
    return class_code


def create_enum_file(radio_names):
    """Creates an enum file with all radio types."""
    enum_entries = ",\n    ".join(radio_names)
    enum_code = f"""
package com.Runner.CQMiau.radios.drivers;

public enum RadioType {{
    {enum_entries},
    NORADIO;
}}
"""
    return enum_code


def create_factory_file(radio_names):
    """Creates the RadioFactory class with a switch case for all radios."""
    case_entries = "\n            ".join(
        [f"case {name}:\n                return new {name}();" for name in radio_names]
    )
    factory_code = f"""
package com.Runner.CQMiau.radios;

import com.Runner.CQMiau.radios.drivers.RadioBase;
import com.Runner.CQMiau.radios.drivers.RadioType;
import com.Runner.CQMiau.comPort.ComPortConfigActivity;

public class RadioFactory {{

    public static RadioBase getRadio() {{
        RadioType type = ComPortConfigActivity.getInstance().getRadio();
        switch (type) {{
            {case_entries}
            default:
                throw new IllegalArgumentException("Unsupported radio type: " + type);
        }}
    }}
}}
"""
    return factory_code


def generate_classes_and_factory():
    """Reads the extracted functions and generates Java classes, an enum, and a factory."""
    if not os.path.exists(EXTRACTED_FILE):
        print(f"Error: {EXTRACTED_FILE} does not exist.")
        return

    # Ensure the output directories exist
    os.makedirs(DRIVERS_DIR, exist_ok=True)

    radio_names = []

    with open(EXTRACTED_FILE, "r", encoding="utf-8") as f:
        content = f.read()

    # Split content by the extracted block markers
    extracted_functions = content.split("# Extracted from:")
    for block in extracted_functions[1:]:  # Skip the first split as it's before the first match
        lines = block.strip().split("\n")
        if not lines:
            continue

        # Extract the class name from the first line of the block
        class_file_name = lines[0].strip()
        sanitized_class_name = sanitize_radio_name(class_file_name.split(".")[0])
        radio_names.append(sanitized_class_name)

        # Extract the function body
        function_body = "\n".join(lines[1:]).strip()

        # Generate Java class
        java_class_code = create_java_class(sanitized_class_name, function_body)

        # Write to a Java file in the drivers directory
        output_file = os.path.join(DRIVERS_DIR, f"{sanitized_class_name}.java")
        with open(output_file, "w", encoding="utf-8") as class_file:
            class_file.write(java_class_code)

    # Generate the enum file
    enum_code = create_enum_file(radio_names)
    enum_file_path = os.path.join(OUTPUT_DIR, ENUM_FILE)
    with open(enum_file_path, "w", encoding="utf-8") as enum_file:
        enum_file.write(enum_code)

    # Generate the factory file
    factory_code = create_factory_file(radio_names)
    factory_file_path = os.path.join(OUTPUT_DIR, FACTORY_FILE)
    with open(factory_file_path, "w", encoding="utf-8") as factory_file:
        factory_file.write(factory_code)

    print(f"Classes generated in {DRIVERS_DIR}")
    print(f"Enum and Factory generated in {OUTPUT_DIR}")


if __name__ == "__main__":
    generate_classes_and_factory()
