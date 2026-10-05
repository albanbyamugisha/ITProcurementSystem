#!/usr/bin/env bash
# Run only against a separate local test server. Never reset the assignment database.
# Stop on a failed command, an unset variable or a failed pipeline step instead of continuing with unreliable results.
set -euo pipefail
# Resolve the project directory from this script location so it can be launched from another directory.
project_dir="$(cd -- "$(dirname -- "$0")/.." && pwd)"
# Make relative source, library and SQL paths refer to the project directory.
cd "$project_dir"
# Read an optional test-port override; otherwise use port 3307, separate from the normal database server.
test_port="${PROCUREMENT_TEST_PORT:-3307}"
# Reject a non-numeric port or port 3306 before any disposable database reset can run.
if [[ ! "$test_port" =~ ^[0-9]+$ || "$test_port" == "3306" ]]; then
# Write the port-selection error to standard error so the failure is visible to the caller.
    echo "Use a separate local test server, normally port 3307. Port 3306 is refused." >&2
# Stop with a non-zero exit code, indicating that the test setup was refused.
    exit 1
fi
# Compile into a temporary folder, and remove only that folder when finished.
# Create a unique temporary directory for compiled test classes; keep build output outside the project.
test_classes="$(mktemp -d /tmp/procurement-test-classes.XXXXXX)"
# Remove only the temporary class directory when this script exits, including after a failure.
trap 'rm -rf -- "$test_classes"' EXIT
# The replacement applies to the checked-in schema, not arbitrary user input.
# Reset procurement_test only, then rename the database in the checked-in schema before sending it to the test server.
{ echo 'DROP DATABASE IF EXISTS procurement_test;'; sed 's/it_procurement_db/procurement_test/g' db/it_procurement_schema.sql; } |
# Run that schema against the separate loopback TCP test server using the local root account.
    mysql --protocol=TCP -h 127.0.0.1 -P "$test_port" -u root >/dev/null
# Compile application and test sources for Java 8 using the project JARs and the temporary output directory.
javac --release 8 -cp "lib/*" -d "$test_classes" src/itprocurementsystem/*.java test/itprocurementsystem/*.java
# Include the logo and font resources so the tests can generate real PDFs from the compiled application.
cp -r src/itprocurementsystem/resources "$test_classes/itprocurementsystem/"
# Run the test entry point without requiring an on-screen desktop; subsequent lines belong to this same command.
java -Djava.awt.headless=true \
    "-Dprocurement.test.url=jdbc:mysql://127.0.0.1:$test_port/procurement_test?useSSL=false" \
    -cp "$test_classes:lib/*" itprocurementsystem.WorkflowTest

# Use the fixtures just created by WorkflowTest to verify vendor navigation and protected deletion.
java -Djava.awt.headless=true \
    "-Dprocurement.test.url=jdbc:mysql://127.0.0.1:$test_port/procurement_test?useSSL=false" \
    -cp "$test_classes:lib/*" itprocurementsystem.VendorButtonsTest
