#include "MatchingEngine.h"
#include "com_example_exchange_jni_MatchingEngineJNI.h"
#include <memory>

static std::unique_ptr<MatchingEngine> matchingEngine;

JNIEXPORT void JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_initializeEngine
  (JNIEnv *env, jobject, jstring symbol)
{
    const char *symbolChars = env->GetStringUTFChars(symbol, 0);
    std::string symbolStr(symbolChars);
    env->ReleaseStringUTFChars(symbol, symbolChars);

    matchingEngine = std::make_unique<MatchingEngine>(symbolStr);
}

JNIEXPORT void JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_addOrder
  (JNIEnv *env, jobject, jstring orderId, jlong price, jint quantity, jboolean isBuy)
{
    const char *orderIdChars = env->GetStringUTFChars(orderId, 0);
    std::string orderIdStr(orderIdChars);
    env->ReleaseStringUTFChars(orderId, orderIdChars);

    matchingEngine->addOrder(orderIdStr, price, quantity, isBuy);
}

JNIEXPORT jobjectArray JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_matchOrders
  (JNIEnv *env, jobject)
{
    std::vector<Trade> trades = matchingEngine->matchOrders();

    jclass tradeClass = env->FindClass("com/example/exchange/jni/MatchingEngineJNI$Trade");
    jobjectArray result = env->NewObjectArray(trades.size(), tradeClass, nullptr);

    for (size_t i = 0; i < trades.size(); i++) {
        jmethodID constructor = env->GetMethodID(tradeClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;IJI)V");
        
        jstring buyOrderId = env->NewStringUTF(trades[i].buyOrderId.c_str());
        jstring sellOrderId = env->NewStringUTF(trades[i].sellOrderId.c_str());
        
        jobject tradeObject = env->NewObject(tradeClass, constructor, buyOrderId, sellOrderId, 
                                             trades[i].amount, trades[i].price, trades[i].originalBuyAmount);
        
        env->SetObjectArrayElement(result, i, tradeObject);
        
        env->DeleteLocalRef(buyOrderId);
        env->DeleteLocalRef(sellOrderId);
        env->DeleteLocalRef(tradeObject);
    }

    return result;
}

JNIEXPORT jobject JNICALL Java_com_example_exchange_jni_MatchingEngineJNI_getOrderBookSummary
  (JNIEnv *env, jobject)
{
    OrderBookSummary summary = matchingEngine->getOrderBookSummary();

    jclass summaryClass = env->FindClass("com/example/exchange/jni/MatchingEngineJNI$OrderBookSummary");
    jmethodID constructor = env->GetMethodID(summaryClass, "<init>", "()V");
    jobject summaryObject = env->NewObject(summaryClass, constructor);

    // Set top buys
    jclass orderSummaryClass = env->FindClass("com/example/exchange/jni/MatchingEngineJNI$OrderBookSummary$OrderSummary");
    jobjectArray topBuys = env->NewObjectArray(summary.topBuys.size(), orderSummaryClass, nullptr);
    for (size_t i = 0; i < summary.topBuys.size(); i++) {
        jmethodID orderSummaryConstructor = env->GetMethodID(orderSummaryClass, "<init>", "(JII)V");
        jobject orderSummaryObject = env->NewObject(orderSummaryClass, orderSummaryConstructor, 
                                                    summary.topBuys[i].price, summary.topBuys[i].quantity, summary.topBuys[i].originalQuantity);
        env->SetObjectArrayElement(topBuys, i, orderSummaryObject);
        env->DeleteLocalRef(orderSummaryObject);
    }
    jfieldID topBuysField = env->GetFieldID(summaryClass, "topBuys", "[Lcom/example/exchange/jni/MatchingEngineJNI$OrderBookSummary$OrderSummary;");
    env->SetObjectField(summaryObject, topBuysField, topBuys);

    // Set lowest sells (similar to top buys)
    // ...

    // Set symbol
    jfieldID symbolField = env->GetFieldID(summaryClass, "symbol", "Ljava/lang/String;");
    jstring symbolString = env->NewStringUTF(summary.symbol.c_str());
    env->SetObjectField(summaryObject, symbolField, symbolString);

    // Set last ten fulfilled orders
    jobjectArray lastTenOrders = env->NewObjectArray(summary.lastTenFulfilledOrders.size(), env->FindClass("java/lang/String"), nullptr);
    for (size_t i = 0; i < summary.lastTenFulfilledOrders.size(); i++) {
        jstring orderString = env->NewStringUTF(summary.lastTenFulfilledOrders[i].c_str());
        env->SetObjectArrayElement(lastTenOrders, i, orderString);
        env->DeleteLocalRef(orderString);
    }
    jfieldID lastTenOrdersField = env->GetFieldID(summaryClass, "lastTenFulfilledOrders", "[Ljava/lang/String;");
    env->SetObjectField(summaryObject, lastTenOrdersField, lastTenOrders);

    return summaryObject;
}