cmake_minimum_required(VERSION 3.10.2)  # CMake version check
project(simple_example)               # Create project "simple_example"
set(CMAKE_CXX_STANDARD 14)            # Enable c++14 standard

# Add main.cpp file of project root directory as source file

add_library( tissue STATIC src/main/cpp/tissue.c)
add_library( interface STATIC src/main/cpp/TissueViewModel.cpp)
add_library( native-lib SHARED src/main/cpp/TissueViewModel.cpp)
find_library( log-lib log)
find_library( jnigraphics-lib jnigraphics)
target_link_libraries( native-lib
        interface
        tissue
        ${log-lib}
        ${jnigraphics-lib}
        )