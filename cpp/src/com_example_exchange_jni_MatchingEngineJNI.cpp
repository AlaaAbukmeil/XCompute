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

JNIEXPORT jstring JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_insertOrder(JNIEnv *env, jobject thisObj, jlong handle, jstring id, jstring type, jint price, jint amount)
{
  try
  {
    MatchingEngine *engine = reinterpret_cast<MatchingEngine *>(handle);

    string OrderType = jstring2string(env, type);

    OrderRequest order = OrderRequest(
        OrderType,
        amount,
        amount,
        jstring2string(env, id),
        price,
        engine->getSymbol());

    engine->insertOrder(order);
    OrderBookSummary summary = engine->getMatchingEngineSummary();

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

JNIEXPORT jstring JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_getMatchingEngineSummary(JNIEnv *env, jobject obj, jlong ptr)
{
  if (ptr == 0)
  {
    return env->NewStringUTF("{}");
  }

  try
  {
    MatchingEngine *engine = reinterpret_cast<MatchingEngine *>(ptr);
    OrderBookSummary summary = engine->getMatchingEngineSummary();

    nlohmann::json j;
    j["symbol"] = summary.symbol;

    j["topBuys"] = nlohmann::json::array();
    for (const auto &buy : summary.topBuys)
    {
      nlohmann::json buyOrder = {
          {"id", buy.id},
          {"price", buy.price},
          {"notional", buy.notional},
          {"originalAmount", buy.originalAmount}};
      j["topBuys"].push_back(buyOrder);
    }

    j["lowestSells"] = nlohmann::json::array();
    for (const auto &sell : summary.lowestSells)
    {
      nlohmann::json sellOrder = {
          {"id", sell.id},
          {"price", sell.price},
          {"notional", sell.notional},
          {"originalAmount", sell.originalAmount}};
      j["lowestSells"].push_back(sellOrder);
    }

    j["lastTenFulfilledOrders"] = nlohmann::json::array();
    for (const auto &order : summary.lastTenFulfilledOrders)
    {
      nlohmann::json fulfilledOrder = {
          {"id", order.id},
          {"price", order.price},
          {"notionalAmount", order.originalNotionalAmount},
          {"type", order.type}};
      j["lastTenFulfilledOrders"].push_back(fulfilledOrder);
    }

    string jsonStr = j.dump();
    return cpp2jstring(env, jsonStr);
  }
  catch (const std::exception &e)
  {
    return cpp2jstring(env, "{\"error\": \"Failed to create JSON\"}");
  }
}