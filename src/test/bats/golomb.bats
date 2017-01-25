#!/usr/bin/env bats

function setup {
    CMD="timeout 120 mpjrun.sh -cp target/eps4j-choco-0.0.1-SNAPSHOT-with-dependencies.jar:target/eps4j-choco-0.0.1-SNAPSHOT-tests.jar org.eps4j.EPSCmd org.eps4j.chocosolver.ChocoFactory org.eps4j.chocosolver.samples.GolombRuler -np 6"
}

@test "3-ruler, solved by the decomposition" {
    run $CMD  -m 3 -- -d 4
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "o 3")
}

@test "4-ruler, solved by the decomposition" {
    run $CMD  -m 4 -- -d 5
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "o 6")
}

@test "3-ruler, depth 1" {
    run $CMD  -m 3 -- -d 1
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "o 3")
}

@test "4-ruler, depth 2" {
    run $CMD  -m 4 -- -d 2
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "o 6")
}


@test "5-ruler, depth 2 with help requests" {
    run $CMD  -m 5 -- -d 2 -- -- -hf 1
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "o 11")
}

@test "6-ruler, depth 2" {
    run $CMD  -m 6 -- -d 2
    [ "$status" -eq 0 ]
    !(echo  "$output" | grep -q  "Exception")
    (echo  "$output" | grep -q  "o 17")
}


