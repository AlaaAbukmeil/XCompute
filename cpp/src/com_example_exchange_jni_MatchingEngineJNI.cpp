#include "MatchingEngine.h"
#include "com_example_exchange_jni_MatchingEngineJNI.h"

static MatchingEngine* engine = nullptr;

JNIEXPORT jstring JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_printHello
  (JNIEnv *env, jobject)
{
    if (engine == nullptr) {
        engine = new MatchingEngine();
    }
    std::string message = engine->printHello();
    return env->NewStringUTF(message.c_str());
}
