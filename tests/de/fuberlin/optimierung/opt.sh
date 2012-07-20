#!/bin/bash

# Bedienung:
# in java-code muessen DEBUG und STATISTIC flags auf false sein
# aufruf aus gleichem ordner mit z.B.: ./opt.sh /home/dakn/Studium/swp_compiler/swp-uebersetzerbau-ss12/tests/de/fuberlin/optimierung/test.c

# $1 enthaelt zu verarbeitende C-Datei
c_file=$1
llvm_ir_file=$(echo $c_file | sed 's/^\(.*\).$/\1/')
llvm_ir_file_optimized=$llvm_ir_file"opt.s"
llvm_ir_file=$llvm_ir_file"s"
llvm_file=$llvm_ir_file".bc"
llvm_file_optimized=$llvm_ir_file_optimized".bc"

# ohne Optimierung
clang -S -emit-llvm $c_file
cat $llvm_ir_file | sed 's/\(.*\)\(getelement.*)\)\(,.*)\)/  %qq = \2\n\1%qq\3/' | sed 's/\(getelement.*\)(\(.*\))/\1\2/' | sed 's/uwtable//' > $llvm_ir_file".tmp"
cat $llvm_ir_file".tmp" > $llvm_ir_file
rm $llvm_ir_file".tmp"
llvm-as $llvm_ir_file
result=$(lli $llvm_file)
echo $result

# mit Optimierung
cd ../../../../src
java de.fuberlin.optimierung.LLVM_Optimization $llvm_ir_file > $llvm_ir_file_optimized
llvm-as $llvm_ir_file_optimized
result_optimized=$(lli $llvm_file_optimized)
echo $result_optimized

if [ "$result" = "$result_optimized" ]; then
	echo "true"
else
	echo "false"
fi

rm $llvm_ir_file
rm $llvm_ir_file_optimized
#rm $llvm_file
#rm $llvm_file_optimized
