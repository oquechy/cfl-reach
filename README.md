# cfl-reach
Kotlin implementation of CFL Reachability algorithm [1] for pointer analysis in C++ and Java programs. 

[1] David Melski and Thomas Reps. 1997. Interconvertbility of set constraints and context-free language reachability. SIGPLAN Not. 32, 12 (Dec. 1997), 74–89. https://doi.org/10.1145/258994.259006

## Running

On c++ data:
    
    ./gradlew run --args='cpp path/to/file.csv'

On java data:

    ./gradlew run --args='java path/to/file.csv'

## Benchmarks
    
Run on data from C_points_to:

    ./gradlew test --tests '*.cpp bzip' 
    ./gradlew test --tests '*.cpp gzip' 
    ./gradlew test --tests '*.cpp ls' 

Run on data from Java_points_to:

    ./gradlew test --tests '*.java lusearch' 
    ./gradlew test --tests '*.java sunflow' 
