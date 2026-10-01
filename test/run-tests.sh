#!/usr/bin/env bash
# Run only against a separate local test server. Never reset the assignment database.
set -euo pipefail
project_dir="$(cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$project_dir"
test_port="${PROCUREMENT_TEST_PORT:-3307}"
if [[ ! "$test_port" =~ ^[0-9]+$ || "$test_port" == "3306" ]]; then
    echo "Use a separate local test server, normally port 3307. Port 3306 is refused." >&2
    exit 1
fi
# Compile into a temporary folder, and remove only that folder when finished.
test_classes="$(mktemp -d /tmp/procurement-test-classes.XXXXXX)"
trap 'rm -rf -- "$test_classes"' EXIT
# The replacement applies to the checked-in schema, not arbitrary user input.
{ echo 'DROP DATABASE IF EXISTS procurement_test;'; sed 's/it_procurement_db/procurement_test/g' db/it_procurement_schema.sql; } |
    mysql --protocol=TCP -h 127.0.0.1 -P "$test_port" -u root >/dev/null
javac --release 8 -d "$test_classes" src/itprocurementsystem/*.java test/itprocurementsystem/*.java
java -Djava.awt.headless=true \
    "-Dprocurement.test.url=jdbc:mysql://127.0.0.1:$test_port/procurement_test?useSSL=false" \
    -cp "$test_classes:lib/mysql-connector-j-26.7.0.jar" itprocurementsystem.WorkflowTest
