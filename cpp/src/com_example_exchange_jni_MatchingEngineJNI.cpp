#include "com_example_exchange_jni_MatchingEngineJNI.h"
#include "MatchingEngine.h"
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

JNIEXPORT jlong JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_createOrderBook(JNIEnv *env, jobject obj, jstring symbol)
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

JNIEXPORT jstring JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_insertOrder(JNIEnv *env, jobject thisObj, jlong handle, jstring id, jstring type, jlong price, jint amount, jint originalAmount)
{
  try
  {
    MatchingEngine *engine = reinterpret_cast<MatchingEngine *>(handle);

    // Convert jstring type to OrderType
    string typeStr = jstring2string(env, type);
    OrderRequest::OrderType orderType = (typeStr == "BUY") ? OrderRequest::BUY : OrderRequest::SELL;

    OrderRequest order = OrderRequest(
        orderType,
        amount,
        originalAmount,
        jstring2string(env, id),
        price,
        engine->getSymbol());
    engine->insertOrder(order);
    OrderBookSummary summary = engine->getOrderBookSummary();

    string result = summary.toCompactString();
    return cpp2jstring(env, result);
  }
  catch (const exception &e)
  {
    return cpp2jstring(env, e.what());
  }
}

JNIEXPORT void JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_deleteOrderBook(JNIEnv *env, jobject obj, jlong ptr)
{
  if (ptr != 0)
  {
    MatchingEngine *engine = reinterpret_cast<MatchingEngine *>(ptr);
    delete engine;
  }
}