# Compiler
CXX = g++
CXXFLAGS = -g -Wall -std=c++20 -fPIC -arch arm64

# Explicit Java home for Homebrew OpenJDK
JAVA_HOME = /opt/homebrew/Cellar/openjdk@21/21.0.4/libexec/openjdk.jdk/Contents/Home

# Include paths
CXXFLAGS += -I$(JAVA_HOME)/include
CXXFLAGS += -I$(JAVA_HOME)/include/darwin
CXXFLAGS += -Iinclude

# Library flags
LDFLAGS = -shared -arch arm64

# Directories
INCLUDE_DIR = include
SRC_DIR = src
BUILD_DIR = build
LIB_DIR = lib

# Files
SRC = $(SRC_DIR)/MatchingEngine.cpp $(SRC_DIR)/com_example_exchange_jni_MatchingEngineJNI.cpp $(SRC_DIR)/logs.cpp
OBJ = $(patsubst $(SRC_DIR)/%.cpp, $(BUILD_DIR)/%.o, $(SRC))

# Target library name (for MacOS)
TARGET = $(LIB_DIR)/libmatching_engine.dylib

# Default target
all: $(TARGET)

# Linking shared library
$(TARGET): $(OBJ)
	@mkdir -p $(LIB_DIR)
	$(CXX) $(LDFLAGS) -o $@ $^

# Compiling
$(BUILD_DIR)/%.o: $(SRC_DIR)/%.cpp
	@mkdir -p $(BUILD_DIR)
	$(CXX) $(CXXFLAGS) -c $< -o $@

# Clean up
clean:
	rm -rf $(BUILD_DIR) $(LIB_DIR)

.PHONY: all clean