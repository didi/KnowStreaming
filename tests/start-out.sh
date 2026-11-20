#!/usr/bin/env bash
set -eu

root=$(cd "$(dirname "$0")/.." && pwd)
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
export JAVA_HOME="$work/jdk"
mkdir -p "$work/bin" "$work/logs" "$JAVA_HOME/bin"
cp "$root/bin/startup.sh" "$work/bin/"
cat > "$JAVA_HOME/bin/java" <<'JAVA'
#!/bin/sh
if [ "$1" = "-version" ]; then
    echo 'openjdk version "11.0.0"' >&2
    exit 0
fi
echo "stdout $LAUNCH"
echo "stderr $LAUNCH" >&2
touch "$DONE"
JAVA
chmod +x "$JAVA_HOME/bin/java"

log="$work/logs/start.out"
echo 'prior history' > "$log"
for launch in first second; do
    LAUNCH="$launch" DONE="$work/$launch.done" JAVA_OPT="-Dtest.launch=$launch" \
        bash "$work/bin/startup.sh" > /dev/null
    for ((i=0; i<100; i++)); do
        [ -e "$work/$launch.done" ] && break
        sleep 0.05
    done
    test -f "$work/$launch.done" || { echo "Mock JVM did not finish: $launch" >&2; exit 1; }
done

# Keep just the launch command and the two streams from each JVM.
awk '/-Dtest[.]launch=first/ {print "start first"}
     /-Dtest[.]launch=second/ {print "start second"}
     /^(prior history|stdout |stderr )/ {print}' "$log" > "$work/observed"
diff -u - "$work/observed" <<'EXPECTED'
prior history
start first
stdout first
stderr first
start second
stdout second
stderr second
EXPECTED

mv "$log" "$work/saved.log"
mkdir "$log"  # An unwritable file target, even when the test runs as root.
if LAUNCH=failed DONE="$work/failed.done" bash "$work/bin/startup.sh" > /dev/null 2>&1; then
    echo 'Startup reported success with an unwritable log target' >&2
    exit 1
fi
echo 'start.out regression checks passed'
