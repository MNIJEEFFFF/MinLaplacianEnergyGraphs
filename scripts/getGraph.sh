#! /bin/bash
java -cf MinLapGraphs.jar MinLaplacianEnergyGraph $1 $2 > test$1_$2.py && python3 test$1_$2.py
