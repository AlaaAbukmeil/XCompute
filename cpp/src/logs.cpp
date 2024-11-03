// utils.cpp
#include "utils.h"
#include <ctime>

// Static member initialization
ofstream Logger::logFile;

void initializeLogging() {
    Logger::initialize();
}

string getCurrentTimestamp() {
    time_t now = time(0);
    char timestamp[26];
    ctime_r(&now, timestamp);
    string ts(timestamp);
    return ts.substr(0, ts.length() - 1);
}

void logToFile(const string& message) {
    Logger::log(message);
}

// Logger class implementation
void Logger::initialize() {
    if (!logFile.is_open()) {
        logFile.open("./cpp/log.txt", ios::app);
    }
}

void Logger::log(const string& message) {
    if (!logFile.is_open()) {
        initialize();
    }
    logFile << "[" << getCurrentTimestamp() << "] " << message << endl;
    logFile.flush();
}

void Logger::cleanup() {
    if (logFile.is_open()) {
        logFile.close();
    }
}