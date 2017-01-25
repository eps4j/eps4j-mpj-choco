#!/usr/bin/env bats

function setup {
    CMD="timeout 120 mpjrun.sh -cp target/eps4j-choco-0.0.1-SNAPSHOT-with-dependencies.jar:target/eps4j-choco-0.0.1-SNAPSHOT-tests.jar org.eps4j.EPSCmd org.eps4j.chocosolver.ChocoFactory org.eps4j.chocosolver.samples.SetPartition -np 6"
}

@test "Partition, solved by the decomposition" {
    run $CMD  -- -d 4
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "o 13")
}

@test "Partition, depth 1" {
    run $CMD  -- -d 1
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "o 13")
}

@test "Partition, depth 2" {
    run $CMD  -- -d 1
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "o 13")
}






