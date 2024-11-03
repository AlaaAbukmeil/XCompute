#include "com_example_exchange_jni_MatchingEngineJNI.h"
#include "MatchingEngine.h"
#include "utils.h"

#include <string>
#include <memory>

using namespace std;

string jstring2string(JNIEnv *env, jstring jStr)
{
  if (!jStr)
    return "";
  const char *cstr = env->GetStringUTFChars(jStr, nullptr);
  string str(cstr);
  env->ReleaseStringUTFChars(jStr, cstr);
  return str;
}

jstring cpp2jstring(JNIEnv *env, const string &str)
{
  return env->NewStringUTF(str.c_str());
}

static unique_ptr<MatchingEngine> matchingEngine;

JNIEXPORT jlong JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_createMatchingEngine(JNIEnv *env, jobject obj, jstring symbol)
{
  const char *symbolStr = env->GetStringUTFChars(symbol, 0);
  string bookName(symbolStr);
  env->ReleaseStringUTFChars(symbol, symbolStr);

  MatchingEngine *engine = new MatchingEngine(bookName);
  return reinterpret_cast<jlong>(engine);
}

JNIEXPORT jstring JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_printHello(JNIEnv *env, jobject thisObj)
{
  string result = matchingEngine->printHello();
  return env->NewStringUTF(result.c_str());
}

JNIEXPORT jstring JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_insertOrder(JNIEnv *env, jobject thisObj, jlong handle, jstring id, jstring type, jint price, jint amount, jint originalAmount)
{
  try
  {
    MatchingEngine *engine = reinterpret_cast<MatchingEngine *>(handle);

    string OrderType = jstring2string(env, type);

    // logToFile("=== New Order Request ===");
    // logToFile("Received order type string: '" + OrderType + "'");

    OrderRequest order = OrderRequest(
        OrderType,
        amount,
        originalAmount,
        jstring2string(env, id),
        price,
        engine->getSymbol());
    engine->insertOrder(order);
    OrderBookSummary summary = engine->getMatchingEngineSummary();

    // logToFile("Order Details:");
    // logToFile("  ID: " + jstring2string(env, id));
    // logToFile("  Price: " + to_string(price));
    // logToFile("  Amount: " + to_string(amount));
    // logToFile("  Original Amount: " + to_string(originalAmount));

    string result = summary.toCompactString();
    return cpp2jstring(env, result);
  }
  catch (const exception &e)
  {
    return cpp2jstring(env, e.what());
  }
}

JNIEXPORT void JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_deleteMatchingEngine(JNIEnv *env, jobject obj, jlong ptr)
{
  if (ptr != 0)
  {
    MatchingEngine *engine = reinterpret_cast<MatchingEngine *>(ptr);
    delete engine;
    Logger::cleanup();
  }
}