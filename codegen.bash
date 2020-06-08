set -e
cd "$(dirname "$0")"
export CCHK="java -classpath ./lib/commons-text-1.6.jar:./lib/commons-lang3-3.8.1.jar:./lib/antlr-4.8-complete.jar:./bin Main codegen"
cat > program.txt   # save everything in stdin to program.txt
$CCHK
cp ./test.s output.s
cat output.s
