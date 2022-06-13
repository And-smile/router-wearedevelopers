#!/bin/bash
for i in {1..10}
do
    curl http://localhost:8080/echo -H "Content-Type: application/json" -d $i
    echo
done