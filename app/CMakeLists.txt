cmake_minimum_required(VERSION 3.10.2)  # CMake version check
project(simple_example)               # Create project "simple_example"
set(CMAKE_CXX_STANDARD 14)            # Enable c++14 standard

# Add main.cpp file of project root directory as source file
set(SOURCE_FILES main.cpp)


add_library( # Sets the name of the library.
        native-lib

             # Sets the library as a shared library.
        SHARED

             # Provides a relative path to your source file(s).
        src/main/cpp/TissueViewModel.cpp
        src/main/cpp/tissue.c)
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )
target_link_libraries( # Specifies the target library.
        native-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )