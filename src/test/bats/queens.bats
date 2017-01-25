#!/usr/bin/env bats

function setup {
    CMD="timeout 120 mpjrun.sh -cp target/eps4j-choco-0.0.1-SNAPSHOT-with-dependencies.jar:target/eps4j-choco-0.0.1-SNAPSHOT-tests.jar org.eps4j.EPSCmd -np 6 org.eps4j.chocosolver.ChocoFactory org.eps4j.chocosolver.samples.AllNQueenBinary"
}

@test "4-queens, CLI error" {
    run $CMD -q 4 -d 5
    [ "$status" -eq 0 ]
    (echo  "$output" | grep -q  "Exception")
}

@test "5-queens, solved by the decomposition" {
    run $CMD -q 5 -- -d 6
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "d NBSOLS 10")
}

@test "6-queens, depth 2" {
    run $CMD -q 6 -- -d 2
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "d NBSOLS 4")
}

@test "7-queens, depth 4" {
    run $CMD -q 7 -- -d 4
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "d NBSOLS 40")
}

@test "8-queens, depth 2" {
    run $CMD -q 8 -- -d 2
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "d NBSOLS 92")
}

@test "12-queens, solution limit, master interrupted" {
    run $CMD -q 12 -- -d 4 -- -s 1
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "d NBSOLS ")
    !(echo  "$output" | grep -q  "d NBSOLS 14200")
    #(echo  "$output" | grep -q  "d M_QUEUE ")
    (echo  "$output" | grep -q  "d M_QUEUE 0")
    (echo  "$output" | grep -q  "d F_QUEUE ")
    !(echo  "$output" | grep -q  "d F_QUEUE 0")
    
}

@test "13-queens, depth 2" {
    run $CMD -q 13 -- -d 2 
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "d NBSOLS 73712")
}

@test "13-queens, depth 3" {
    run $CMD -q 13 -- -d 2 
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "d NBSOLS 73712")
}

