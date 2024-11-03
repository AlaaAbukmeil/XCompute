// utils.h
#ifndef UTILS_H
#define UTILS_H

#include <string>
#include <fstream>

using namespace std;

// Logging functions
void initializeLogging();
string getCurrentTimestamp();
void logToFile(const string& message);

// Class wrapper for logging (optional, but provides better control)
class Logger {
public:
    static void initialize();
    static void log(const string& message);
    static void cleanup();

private:
    static ofstream logFile;
};

#endif // UTILS_H